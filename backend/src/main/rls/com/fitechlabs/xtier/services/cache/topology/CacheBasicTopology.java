// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.cache.topology;

import com.fitechlabs.xtier.kernel.*;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.cache.*;
import com.fitechlabs.xtier.services.cluster.*;
import com.fitechlabs.xtier.services.cluster.filters.*;
import java.util.Map;
import java.util.Set;

public class CacheBasicTopology
    implements CacheTopology, KernelServiceListener
{

    public CacheBasicTopology()
    {
        this(null, null, false, true, true);
    }

    public CacheBasicTopology(boolean flag, boolean flag1, boolean flag2)
    {
        this(null, null, flag, flag1, flag2);
    }

    public CacheBasicTopology(String s, Map map, boolean flag, boolean flag1, boolean flag2)
    {
        mutex = new Object();
        ClusterServiceFilter clusterservicefilter = new ClusterServiceFilter("cache", new ClusterNodeTypeFilter(flag, flag1, flag2));
        filter = ((ClusterFilter) (s != null ? ((ClusterFilter) (new ClusterGroupFilter(s, map, clusterservicefilter))) : ((ClusterFilter) (clusterservicefilter))));
        XtierKernel xtierkernel = XtierKernel.getInstance();
        cluster = xtierkernel.cluster();
        xtierkernel.addServiceListener("cache", this);
        cluster.addListener(clusterListener);
        synchronized(mutex)
        {
            topology = getCacheTopology();
        }
    }

    public void afterStart(String s)
        throws KernelServiceException
    {
    }

    public void beforeStop(String s)
        throws KernelServiceException
    {
        synchronized(mutex)
        {
            cluster.removeListener(clusterListener);
        }
    }

    private Set getCacheTopology()
    {
        return cluster.getNodes(filter);
    }

    public Set getNodes(CacheEntry cacheentry, Object obj)
    {
        Object obj1 = mutex;
        JVM INSTR monitorenter ;
        return topology;
        Exception exception;
        exception;
        throw exception;
    }

    public Set getNodes(long l, Object obj)
        throws CacheException
    {
        Object obj1 = mutex;
        JVM INSTR monitorenter ;
        return topology;
        Exception exception;
        exception;
        throw exception;
    }

    public String toString()
    {
        return L10n.format("SRVC.CACHE.TXT14");
    }

    private Set topology;
    private ClusterListener clusterListener = new ClusterListener() {

        public void onNodeEvent(int i, ClusterNode clusternode, int j)
        {
            synchronized(mutex)
            {
                topology = getCacheTopology();
            }
        }

            
            {
                super();
            }
    }
;
    private final Object mutex;
    private ClusterService cluster;
    private ClusterFilter filter;



}
