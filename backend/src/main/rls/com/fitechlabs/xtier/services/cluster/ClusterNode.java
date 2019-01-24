// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.cluster;

import java.net.InetAddress;
import java.util.Map;

// Referenced classes of package com.fitechlabs.xtier.services.cluster:
//            ClusterGroupMembership

public interface ClusterNode
{

    public abstract boolean isActive();

    public abstract boolean hasService(String s);

    public abstract int getNodeId();

    public abstract InetAddress getAddress();

    public abstract int getPort();

    public abstract String[] getServices();

    public abstract boolean isLocalNode();

    public abstract boolean isLocalHostNode();

    public abstract boolean isRemoteNode();

    public abstract int getNumberOfCpus();

    public abstract long getStartTime();

    public abstract String getOsName();

    public abstract String getOsArch();

    public abstract long getTotalMemory();

    public abstract long getFreeMemory();

    public abstract float getCpuLoad();

    public abstract boolean isGroupMember(String s);

    public abstract ClusterGroupMembership getGroupMembership(String s);

    public abstract Map getGroupMemberships();

    public abstract boolean checkFailureStatus();
}
