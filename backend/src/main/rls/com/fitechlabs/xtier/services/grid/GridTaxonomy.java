// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.grid;

import com.fitechlabs.xtier.services.cluster.ClusterNode;

public interface GridTaxonomy
{

    public abstract double getIoWeight(ClusterNode clusternode);

    public abstract double getCpuWeight(ClusterNode clusternode);

    public abstract double getMemoryWeight(ClusterNode clusternode);

    public abstract Object getUserWeight(ClusterNode clusternode);

    public static final double NA_WEIGHT = -1D;
}
