// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.grid.impl;

import com.fitechlabs.xtier.services.cluster.ClusterNode;
import com.fitechlabs.xtier.services.grid.GridTaxonomy;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Map;

class GridConfig
{

    GridConfig()
    {
        ipAddr = null;
        nodePorts = null;
        taxonomy = null;
        jmsFactoryName = null;
        execLocalNoSplit = true;
        tcpClientPort = 0;
    }

    InetAddress getIpAddr()
    {
        return ipAddr;
    }

    void setIpAddr(InetAddress inetaddress)
    {
        ipAddr = inetaddress;
    }

    void setNodePorts(Map map)
    {
        nodePorts = map;
    }

    int getMaxExecTraces()
    {
        return maxExecTraces;
    }

    void setMaxExecTraces(int i)
    {
        maxExecTraces = i;
    }

    int getPort(ClusterNode clusternode)
    {
        Integer integer = (Integer)nodePorts.get(new InetSocketAddress(clusternode.getAddress(), clusternode.getPort()));
        int i = integer == null ? dfltPort : integer.intValue();
        return i;
    }

    void setDfltPort(int i)
    {
        dfltPort = i;
    }

    GridTaxonomy getTaxonomy()
    {
        return taxonomy;
    }

    void setTaxonomy(GridTaxonomy gridtaxonomy)
    {
        taxonomy = gridtaxonomy;
    }

    String getPoolName()
    {
        return poolName;
    }

    void setPoolName(String s)
    {
        poolName = s;
    }

    String getJmsFactoryName()
    {
        return jmsFactoryName;
    }

    void setJmsFactoryName(String s)
    {
        jmsFactoryName = s;
    }

    int getTcpClientPort()
    {
        return tcpClientPort;
    }

    void setTcpClientPort(int i)
    {
        tcpClientPort = i;
    }

    boolean isExecLocalNoSplit()
    {
        return execLocalNoSplit;
    }

    void setExecLocalNoSplit(boolean flag)
    {
        execLocalNoSplit = flag;
    }

    private InetAddress ipAddr;
    private int dfltPort;
    private int maxExecTraces;
    private String poolName;
    private Map nodePorts;
    private GridTaxonomy taxonomy;
    private String jmsFactoryName;
    private boolean execLocalNoSplit;
    private int tcpClientPort;
}
