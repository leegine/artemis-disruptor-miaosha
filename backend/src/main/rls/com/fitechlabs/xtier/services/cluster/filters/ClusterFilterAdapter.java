// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.cluster.filters;

import com.fitechlabs.xtier.services.cluster.ClusterFilter;

public abstract class ClusterFilterAdapter
    implements ClusterFilter
{

    protected ClusterFilterAdapter()
    {
        nested = null;
    }

    protected ClusterFilterAdapter(ClusterFilter clusterfilter)
    {
        nested = null;
        nested = clusterfilter;
    }

    public final ClusterFilter getNested()
    {
        return nested;
    }

    public final void setNested(ClusterFilter clusterfilter)
    {
        nested = clusterfilter;
    }

    private ClusterFilter nested;
}
