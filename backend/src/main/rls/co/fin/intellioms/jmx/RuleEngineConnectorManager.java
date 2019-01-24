// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   RuleEngineConnectorManager.java

package co.fin.intellioms.jmx;

import co.fin.intellioms.connector.ClientConnection;
import co.fin.intellioms.rulesys.impl.ConnectorServerListener;
import co.fin.intellioms.rulesys.impl.RuleEngineConnectorServer;
import co.fin.intellioms.util.InitializationException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.Set;

// Referenced classes of package com.com.fin.intellioms.jmx:
//            RuleEngineConnectorManagerMBean

public class RuleEngineConnectorManager
    implements RuleEngineConnectorManagerMBean
{

    public RuleEngineConnectorManager(RuleEngineConnectorServer connector, ConnectorServerListener lstnr)
    {
        this.connector = connector;
        this.lstnr = lstnr;
    }

    public int getPort()
    {
        return connector.getPort();
    }

    public SocketAddress[] getConnectedClientAddresses()
    {
        Set clients = lstnr.getClients();
        if(clients != null)
        {
            SocketAddress addr[] = new SocketAddress[clients.size()];
            int i = 0;
            for(Iterator iter = clients.iterator(); iter.hasNext();)
                addr[i++] = ((ClientConnection)iter.next()).getRemoteSocketAddress();

            return addr;
        } else
        {
            return EMPTY_ADDR;
        }
    }

    public boolean isStarted()
    {
        return connector.isStarted();
    }

    public void start()
        throws InitializationException
    {
        connector.start();
    }

    public void stop()
    {
        connector.stop();
    }

    private static final InetSocketAddress EMPTY_ADDR[] = new InetSocketAddress[0];
    private final RuleEngineConnectorServer connector;
    private final ConnectorServerListener lstnr;

}
