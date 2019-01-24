// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.cluster.filters;

import com.fitechlabs.xtier.services.cluster.ClusterFilter;
import com.fitechlabs.xtier.services.cluster.ClusterNode;

// Referenced classes of package com.fitechlabs.xtier.services.cluster.filters:
//            ClusterFilterAdapter

public class ClusterNodeTypeFilter extends ClusterFilterAdapter
{

    public ClusterNodeTypeFilter()
    {
        local = false;
        localHost = false;
        remote = false;
    }

    public ClusterNodeTypeFilter(boolean flag, boolean flag1, boolean flag2)
    {
        local = false;
        localHost = false;
        remote = false;
        local = flag;
        localHost = flag1;
        remote = flag2;
    }

    public ClusterNodeTypeFilter(boolean flag, boolean flag1, boolean flag2, ClusterFilter clusterfilter)
    {
        super(clusterfilter);
        local = false;
        localHost = false;
        remote = false;
        local = flag;
        localHost = flag1;
        remote = flag2;
    }

    public boolean isAcceptLocalHost()
    {
        return localHost;
    }

    public void setAcceptLocalHost(boolean flag)
    {
        localHost = flag;
    }

    public boolean isAcceptLocal()
    {
        return local;
    }

    public void setAcceptLocal(boolean flag)
    {
        local = flag;
    }

    public boolean isAcceptRemote()
    {
        return remote;
    }

    public void setAcceptRemote(boolean flag)
    {
        remote = flag;
    }

    public boolean accept(ClusterNode clusternode)
    {
        ClusterFilter clusterfilter = getNested();
        if(clusterfilter != null && !clusterfilter.accept(clusternode))
            return false;
        if(local && clusternode.isLocalNode())
            return true;
        if(localHost && clusternode.isLocalHostNode())
            return true;
        return remote && clusternode.isRemoteNode();
    }

    private boolean local;
    private boolean localHost;
    private boolean remote;
}
