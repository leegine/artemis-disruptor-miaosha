// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   RuleEngineNode.java

package co.fin.intellioms.failover;


public class RuleEngineNode
    implements Comparable
{

    public RuleEngineNode(String host, int port, boolean localNode)
    {
        this.host = host;
        this.port = port;
        this.localNode = localNode;
        id = (new StringBuilder()).append(host).append(":").append(port).toString();
    }

    public String getId()
    {
        return id;
    }

    public String getHost()
    {
        return host;
    }

    public int getPort()
    {
        return port;
    }

    public boolean isLocalNode()
    {
        return localNode;
    }

    public int hashCode()
    {
        return id.hashCode();
    }

    public boolean equals(Object o)
    {
        if(o instanceof RuleEngineNode)
        {
            RuleEngineNode n = (RuleEngineNode)o;
            return getId().equals(n.getId());
        } else
        {
            return false;
        }
    }

    public String toString()
    {
        return (new StringBuilder()).append("RuleEngineNode[id=").append(id).append(", address=").append(host).append(", port=").append(port).append(", local=").append(localNode).append("]").toString();
    }

    public int compareTo(Object o)
    {
        RuleEngineNode n = (RuleEngineNode)o;
        String addr1 = (new StringBuilder()).append(host).append(":").append(port).toString();
        String addr2 = (new StringBuilder()).append(n.getHost()).append(":").append(n.getPort()).toString();
        return addr1.compareTo(addr2);
    }

    private final String id;
    private final String host;
    private final int port;
    private final boolean localNode;
}
