// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.cluster;

import com.fitechlabs.xtier.kernel.KernelService;
import java.net.InetAddress;
import java.util.List;
import java.util.Set;

// Referenced classes of package com.fitechlabs.xtier.services.cluster:
//            ClusterFilter, ClusterNode, ClusterListener, ClusterErrorListener, 
//            ClusterNodeFailureResolver

public interface ClusterService
    extends KernelService
{

    public abstract int getTopologyVersion();

    public abstract Set getAllNodes();

    public abstract Set getNodes(ClusterFilter clusterfilter);

    public abstract ClusterNode getNode(InetAddress inetaddress, int i);

    public abstract ClusterNode getNode(int i);

    public abstract ClusterNode getLocalNode();

    public abstract InetAddress getMulticastGroup();

    public abstract int getMulticastPort();

    public abstract boolean addListener(ClusterListener clusterlistener);

    public abstract List getAllListeners();

    public abstract boolean removeListener(ClusterListener clusterlistener);

    public abstract boolean addErrorListener(ClusterErrorListener clustererrorlistener);

    public abstract List getAllErrorListeners();

    public abstract boolean removeErrorListener(ClusterErrorListener clustererrorlistener);

    public abstract ClusterNodeFailureResolver getNodeFailureResolver();
}
