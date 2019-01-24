// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   AbstractConnector.java

package co.fin.intellioms.rlsclt.impl;

import co.fin.intellioms.account.AccountIDRange;
import co.fin.intellioms.account.AccountsManager;
import co.fin.intellioms.connector.ConnectorClient;
import co.fin.intellioms.connector.MessageListener;
import co.fin.intellioms.connector.impl.ConnectorClientImpl;
import co.fin.intellioms.failover.AccountsController;
import co.fin.intellioms.failover.RuleEngineNode;
import co.fin.intellioms.util.Log;
import co.fin.intellioms.util.ServiceState;
import co.fin.intellioms.util.Startable;
import com.fitechlabs.fin.intellioms.account.*;
import co.fin.intellioms.rlsclt.RuleEngineConnectorException;
import com.fitechlabs.fin.intellioms.util.*;
import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.services.cluster.*;
import com.fitechlabs.xtier.services.marshal.MarshalException;
import com.fitechlabs.xtier.services.marshal.MarshalObject;

import javax.security.auth.login.AccountException;
import java.io.IOException;
import java.util.*;

public class AbstractConnector
    implements Startable
{

    /**
     * @deprecated Method AbstractConnector is deprecated
     */

    public AbstractConnector(AccountsManager accountsManager, long discoverInterval)
    {
        this(accountsManager, 100, 500L);
    }

    public AbstractConnector(AccountsManager accountsManager, int retries, long retryInterval)
    {
        cluster = XtierKernel.getInstance().cluster();
        connections = new HashMap();
        mux = new Object();
        if(!$assertionsDisabled && retryInterval <= 0L)
        {
            throw new AssertionError("Failover interval must be above zero.");
        } else
        {
            log = Log.getLogger(getClass());
            state = new ServiceState(getClass().getName());
            this.accountsManager = accountsManager;
            this.retryInterval = retryInterval;
            this.retries = retries;
            return;
        }
    }

    public AbstractConnector(AccountsManager accountsManager)
    {
        this(accountsManager, 100, 500L);
    }

    public boolean isStarted()
    {
        return state.isStarted();
    }

    public RuleEngineNode getRuleEngineByAccountId(long account)
    {
        return accountController.getNodeByAccountId(account);
    }

    public List getRuleEngines()
    {
        return accountController.getRuleEngines();
    }

    public MarshalObject send(RuleEngineNode ruleEngine, MarshalObject msg)
        throws RuleEngineConnectorException
    {
        if(!$assertionsDisabled && ruleEngine == null)
            throw new AssertionError("Rule engine ID is null.");
        if(!$assertionsDisabled && msg == null)
            throw new AssertionError("Message is null.");
        int i = retries;
        do
        {
            try
            {
                return tryToSend(i, ruleEngine, msg);
            }
            catch(RuleEngineConnectorException e)
            {
                if(e.getErrType() == 1)
                {
                    if(i == 0)
                        throw e;
                    if(log.isWarn())
                        log.warn((new StringBuilder()).append("Unable to send message ").append(msg).append(" :").append(e.getMessage()).append(" ...failing over (attempts left ").append(retries >= 0 ? String.valueOf(i) : "unlimited").append(")").toString());
                } else
                {
                    throw e;
                }
            }
            try
            {
                Thread.sleep(retryInterval);
            }
            catch(InterruptedException e)
            {
                throw new RuleEngineConnectorException((new StringBuilder()).append("Unable to send message ").append(msg).append(" to engine '").append(ruleEngine).append("': Sender thread interrupted.").toString(), e, 0);
            }
            i--;
        } while(true);
    }

    public MarshalObject send(long accountId, MarshalObject msg)
        throws RuleEngineConnectorException
    {
        int i = retries;
        do
        {
            RuleEngineNode ruleEngine;
            synchronized(mux)
            {
                ruleEngine = getRuleEngineByAccountId(accountId);
            }
            if(ruleEngine != null)
            {
                try
                {
                    return tryToSend(i, ruleEngine, msg);
                }
                catch(RuleEngineConnectorException e)
                {
                    if(e.getErrType() == 1)
                    {
                        if(i == 0)
                            throw e;
//                        if(log.isWarn())
//                            log.warn((new StringBuilder()).append("Unable to send message ").append(msg).append(" :").append(e.getMessage()).append(" ...trying to failover (attempts left : ").append(retries >= 0 ? String.valueOf(i) : "unlimited").append(")").toString());
                    } else
                    {
                        throw e;
                    }
                }
            } else
            {
                if(i == 0)
                    throw new RuleEngineConnectorException((new StringBuilder()).append("Unable to find rule engine for account ID = ").append(accountId).toString(), 0);
//                if(log.isWarn())
//                    log.warn((new StringBuilder()).append("Unable to find rule engine for account ID = ").append(accountId).append(" ...trying to failover (attempts left : ").append(retries >= 0 ? String.valueOf(i) : "unlimited").append(")").toString());
            }
            try
            {
                Thread.sleep(retryInterval);
            }
            catch(InterruptedException e)
            {
                throw new RuleEngineConnectorException((new StringBuilder()).append("Unable to send message ").append(msg).append(" to engine '").append(ruleEngine).append("': Sender thread interrupted.").toString(), e, 0);
            }
            i--;
        } while(true);
    }

    private MarshalObject tryToSend(int attempt, RuleEngineNode ruleEngine, MarshalObject msg)
        throws RuleEngineConnectorException
    {
        ConnectorClient conn;
        synchronized(mux)
        {
            conn = (ConnectorClient)connections.get(ruleEngine);
            if(conn == null)
                throw new RuleEngineConnectorException((new StringBuilder()).append("Not connected to rule engine '").append(ruleEngine).append("'").toString(), 1);
        }
        try
        {
            if(!conn.isConnected())
                conn.connect();
            return conn.sendSync(msg);
        }
        catch(MarshalException e)
        {
            throw new RuleEngineConnectorException((new StringBuilder()).append("Unable to send message ").append(msg).append(" to engine '").append(ruleEngine).append("': ").append(e.getMessage()).toString(), e, 1);
        }
        catch(IOException e)
        {
            throw new RuleEngineConnectorException((new StringBuilder()).append("Unable to send message ").append(msg).append(" to engine '").append(ruleEngine).append("': ").append(e.getMessage()).toString(), e, 1);
        }
        catch(InterruptedException e)
        {
            throw new RuleEngineConnectorException((new StringBuilder()).append("Unable to send message ").append(msg).append(" to engine '").append(ruleEngine).append("': Sender thread interrupted.").toString(), e, 0);
        }
    }

    public void start()
        throws InitializationException
    {
        state.checkNotStarted();
//        if(log.isInfo())
//            log.info("Starting...");
        AccountIDRange accounts;
        try
        {
            accounts = accountsManager.getAccountsCount();
        }
        catch(AccountException e)
        {
            throw new InitializationException((new StringBuilder()).append("Unable to retreive accounts information: ").append(e.getMessage()).toString(), e);
        }
        List nodes = topologyToRuleEngineNodes(cluster.getAllNodes());
        cluster.addListener(lstnr);
        synchronized(mux)
        {
            accountController = new AccountsController(accounts, nodes);
            for(int i = 0; i < nodes.size(); i++)
            {
                RuleEngineNode node = (RuleEngineNode)nodes.get(i);
                registerConnection(node);
            }

        }
        if(log.isInfo())
            log.info("Started.");
        state.start();
    }

    public void stop()
    {
        state.checkStarted();
        if(XtierKernel.getInstance().getState() == 1)
            cluster.removeListener(lstnr);
        List tmp;
        synchronized(mux)
        {
            tmp = new ArrayList(connections.entrySet());
            connections.clear();
        }
        RuleEngineNode node;
        for(Iterator iter = tmp.iterator(); iter.hasNext(); accountController.removeNode(node))
        {
            Map.Entry e = (Map.Entry)iter.next();
            node = (RuleEngineNode)e.getKey();
            ConnectorClient conn = (ConnectorClient)e.getValue();
            if(conn == null || !conn.isConnected())
                continue;
            try
            {
                conn.disconnect();
                if(log.isInfo())
                    log.info((new StringBuilder()).append("Disconnected from rule engine: ").append(node).toString());
                continue;
            }
            catch(IOException e1)
            {
                if(log.isWarn())
                    log.warn(e1.getMessage(), e1);
            }
        }

        state.stop();
    }

    private void registerConnection(final RuleEngineNode eng)
    {
label0:
        {
            if(log.isInfo())
                log.info((new StringBuilder()).append("Connecting to rule engine ").append(eng).toString());
            synchronized(mux)
            {
                if(!connections.containsKey(eng))
                    break label0;
                if(log.isWarn())
                    log.warn((new StringBuilder()).append("Already connected to rule enine: ").append(eng).toString());
            }
            return;
        }
        ConnectorClient conn = new ConnectorClientImpl(eng.getHost(), eng.getPort());
        connections.put(eng, conn);
        conn.setListener(new MessageListener() {

            public void onMessage(MarshalObject marshalobject)
            {
            }

            public void onClose()
            {
                if(log.isInfo())
                    log.info((new StringBuilder()).append("Disconnected from rule engine ").append(eng).toString());
            }

            final RuleEngineNode val$eng;
            final AbstractConnector this$0;


            {
                this$0 = AbstractConnector.this;
                eng = ruleenginenode;
                super();
            }
        }
);
        accountController.addNode(eng);
        obj;
        JVM INSTR monitorexit ;
          goto _L1
        exception;
        throw exception;
_L1:
    }

    private List topologyToRuleEngineNodes(Set topology)
    {
        List rules = new ArrayList();
        if(topology != null)
        {
            Iterator iter = topology.iterator();
            do
            {
                if(!iter.hasNext())
                    break;
                ClusterNode n = (ClusterNode)iter.next();
                RuleEngineNode rn = ClusterUtil.getRuleEngineNode(n);
                if(rn != null)
                    rules.add(rn);
            } while(true);
        }
        return rules;
    }

    private ClusterListener lstnr = new ClusterListener() {

        public void onNodeEvent(int eventId, ClusterNode node, int topologyVersion)
        {
            RuleEngineNode eng = ClusterUtil.getRuleEngineNode(node);
            if(eng != null)
                switch(eventId)
                {
                default:
                    break;

                case 2: // '\002'
                case 3: // '\003'
                    ConnectorClient conn;
                    synchronized(mux)
                    {
                        accountController.removeNode(eng);
                        conn = (ConnectorClient)connections.remove(eng);
                    }
                    if(conn == null || !conn.isConnected())
                        break;
                    try
                    {
                        conn.disconnect();
                        break;
                    }
                    catch(IOException e) { }
                    if(log.isWarn())
                        log.warn((new StringBuilder()).append("Error while disconnecting from rule engine ").append(eng).append(".").toString());
                    break;

                case 1: // '\001'
                    registerConnection(eng);
                    break;
                }
        }

        final AbstractConnector this$0;


            {
                this$0 = AbstractConnector.this;
                super();
            }
    }
;
    public static final long DEFAULT_FAILOVER_RETRY_INTERVAL = 500L;
    public static final int DEFAULT_FAILOVER_ATTEMPTS = 100;
    protected final Log log;
    private ClusterService cluster;
    private Map connections;
    private final AccountsManager accountsManager;
    private AccountsController accountController;
    private final Object mux;
    private final ServiceState state;
    private long retryInterval;
    private int retries;
//    static final boolean $assertionsDisabled = !com/ com /fin/intellioms/rlsclt/impl/AbstractConnector.desiredAssertionStatus();





}
