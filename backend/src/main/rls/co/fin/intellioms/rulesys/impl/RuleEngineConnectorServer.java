// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   RuleEngineConnectorServer.java

package co.fin.intellioms.rulesys.impl;

import co.fin.intellioms.connector.ConnectorServer;
import co.fin.intellioms.connector.ServerMessagesListener;
import co.fin.intellioms.connector.impl.ConnectorServerImpl;
import co.fin.intellioms.failover.RuleEngineNode;
import com.fitechlabs.fin.intellioms.util.*;
import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.services.cluster.ClusterNode;
import com.fitechlabs.xtier.services.cluster.ClusterService;

public class RuleEngineConnectorServer
    implements Startable
{

    public RuleEngineConnectorServer(ServerMessagesListener lstnr)
    {
        this.lstnr = lstnr;
    }

    public int getPort()
    {
        ClusterNode clNode = XtierKernel.getInstance().cluster().getLocalNode();
        RuleEngineNode localNode = ClusterUtil.getRuleEngineNode(clNode);
        return localNode == null ? -1 : localNode.getPort();
    }

    public boolean isStarted()
    {
        return state.isStarted();
    }

    public void start()
        throws InitializationException
    {
        state.start();
        ClusterNode clNode = XtierKernel.getInstance().cluster().getLocalNode();
        RuleEngineNode localNode = ClusterUtil.getRuleEngineNode(clNode);
        if(localNode == null)
            throw new InitializationException("Local node is not a rule engine.");
        serv = new ConnectorServerImpl(localNode.getPort());
        serv.setListener(lstnr);
        serv.start();
        if(log.isInfo())
            log.info("Started.");
    }

    public void stop()
    {
        if(serv != null)
            serv.stop();
        state.stop();
        if(log.isInfo())
            log.info("Stopped.");
    }

    private static final Log log = Log.getLogger(com/ com /fin/intellioms/rulesys/impl/RuleEngineConnectorServer);
    private ConnectorServer serv;
    private final ServiceState state = new ServiceState("RuleEngineConnectorServer");
    private ServerMessagesListener lstnr;

}
