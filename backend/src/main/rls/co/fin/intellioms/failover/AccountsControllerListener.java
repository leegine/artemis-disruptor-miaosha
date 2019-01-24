// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   AccountsControllerListener.java

package co.fin.intellioms.failover;

import co.fin.intellioms.account.AccountsInfo;
import co.fin.intellioms.account.AccountsManager;
import co.fin.intellioms.rulesys.OmsRuleEngine;
import co.fin.intellioms.util.Log;
import com.fitechlabs.fin.intellioms.account.*;
import co.fin.intellioms.util.InitializationException;
import com.fitechlabs.xtier.threads.SysThread;
import java.util.*;

// Referenced classes of package com.com.fin.intellioms.failover:
//            AccountsController, FailoverListener, RuleEngineNode

public class AccountsControllerListener
    implements FailoverListener
{

    public AccountsControllerListener(AccountsManager manager, OmsRuleEngine engine)
    {
        accountsUpdaterMux = new Object();
        accountsUpdater = new SysThread("AccountsUpdater") {

            protected void body()
            {
                {
                    checkInterrupted();
                    List nodes = null;
                    synchronized(accountsUpdaterMux)
                    {
                        if(accountsUpdaterNodes != null)
                        {
                            nodes = new ArrayList(accountsUpdaterNodes);
                            accountsUpdaterNodes = null;
                        }
                    }
                    if(nodes != null)
                    {
                        int i = 0;
                        do
                        {
                            if(i >= nodes.size())
                                break;
                            RuleEngineNode node = (RuleEngineNode)nodes.get(i);
                            if(node.isLocalNode())
                            {
                                AccountsInfo accInfo = new AccountsInfo(ctrl.getAccountsRange());
                                try
                                {
                                    ruleEngine.registerAccounts(accInfo);
                                }
                                catch(Throwable e)
                                {
                                    if(AccountsControllerListener.log.isError())
                                        AccountsControllerListener.log.error((new StringBuilder()).append("Unable to to update rule engine accounts info: ").append(e.getMessage()).toString(), e);
                                }
                                break;
                            }
                            i++;
                        } while(true);
                    }
                    synchronized(accountsUpdaterMux)
                    {
                        if(accountsUpdaterNodes == null)
                            break label0;
                    }
                    continue; /* Loop/switch isn't completed */
                }
                try
                {
                    accountsUpdaterMux.wait();
                }
                catch(InterruptedException e) { }

            }

            final AccountsControllerListener this$0;


            {
                this$0 = AccountsControllerListener.this;
                super(x0);
            }
        }
;
        accountsManager = manager;
        ruleEngine = engine;
    }

    public synchronized void onNodeJoin(RuleEngineNode node, Set newTopology)
    {
        ctrl.addNode(node);
        updateAccountsForLocalNode();
    }

    public synchronized void onNodeFailure(RuleEngineNode node, Set newTopology)
    {
        ctrl.removeNode(node);
        updateAccountsForLocalNode();
    }

    public synchronized void onNodeLeft(RuleEngineNode node, Set newTopology)
    {
        ctrl.removeNode(node);
        updateAccountsForLocalNode();
    }

    public void start(Set topology)
        throws InitializationException
    {
        if(log.isInfo())
            log.info((new StringBuilder()).append("Starting with initial topology: ").append(topology).toString());
        try
        {
            ctrl = new AccountsController(accountsManager.getAccountsCount(), new ArrayList(topology));
        }
        catch(AccountException e)
        {
            throw new InitializationException((new StringBuilder()).append("Unable to instantiate accounts controller: ").append(e.getMessage()).toString(), e);
        }
        accountsUpdater.start();
        updateAccountsForLocalNode();
        if(log.isInfo())
            log.info("Started.");
    }

    public void stop()
    {
        accountsUpdater.safeStop();
    }

    public AccountsController getAccountsController()
    {
        return ctrl;
    }

    private void updateAccountsForLocalNode()
    {
        synchronized(accountsUpdaterMux)
        {
            accountsUpdaterNodes = ctrl.getRuleEngines();
            accountsUpdaterMux.notifyAll();
        }
    }

    private static final Log log = Log.getLogger(com/ com /fin/intellioms/failover/AccountsControllerListener);
    private AccountsManager accountsManager;
    private AccountsController ctrl;
    private OmsRuleEngine ruleEngine;
    private Object accountsUpdaterMux;
    private List accountsUpdaterNodes;
    private SysThread accountsUpdater;







}
