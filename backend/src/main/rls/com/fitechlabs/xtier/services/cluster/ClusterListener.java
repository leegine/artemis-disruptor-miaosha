// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.cluster;

import java.util.EventListener;

// Referenced classes of package com.fitechlabs.xtier.services.cluster:
//            ClusterNode

public interface ClusterListener
    extends EventListener
{

    public abstract void onNodeEvent(int i, ClusterNode clusternode, int j);

    public static final int NODE_JOINED = 1;
    public static final int NODE_LEFT = 2;
    public static final int NODE_FAILED = 3;
}
