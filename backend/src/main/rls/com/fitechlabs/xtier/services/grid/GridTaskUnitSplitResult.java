// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.grid;

import com.fitechlabs.xtier.services.cluster.ClusterNode;

// Referenced classes of package com.fitechlabs.xtier.services.grid:
//            GridTaskUnitResult

public interface GridTaskUnitSplitResult
    extends GridTaskUnitResult
{

    public abstract ClusterNode getClusterNode();

    public abstract long getSendReqTime();

    public abstract long getRecvReqTime();

    public abstract long getSendResTime();

    public abstract long getRecvResTime();

    public abstract long getBeginExecTime();

    public abstract long getEndExecTime();
}
