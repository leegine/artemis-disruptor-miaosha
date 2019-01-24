// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.grid.adapters;

import com.fitechlabs.xtier.services.cluster.ClusterNode;
import com.fitechlabs.xtier.services.grid.GridTaxonomy;

public class GridTaxonomyAdapter
    implements GridTaxonomy
{

    public GridTaxonomyAdapter()
    {
    }

    public double getIoWeight(ClusterNode clusternode)
    {
        return -1D;
    }

    public double getCpuWeight(ClusterNode clusternode)
    {
        return -1D;
    }

    public double getMemoryWeight(ClusterNode clusternode)
    {
        return -1D;
    }

    public Object getUserWeight(ClusterNode clusternode)
    {
        return null;
    }
}
