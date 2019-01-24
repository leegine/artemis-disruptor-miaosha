// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.grid.topology;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.services.cluster.*;
import com.fitechlabs.xtier.services.cluster.filters.*;
import com.fitechlabs.xtier.services.grid.GridTaskTopology;
import com.fitechlabs.xtier.services.grid.GridTaskUnitContext;
import com.fitechlabs.xtier.utils.ArgAssert;
import java.util.Set;

public class GridTaskBasicTopology
    implements GridTaskTopology
{

    public GridTaskBasicTopology(String s, final float threshold, boolean flag, boolean flag1, boolean flag2)
    {
        filter = null;
        cluster = null;
        ArgAssert.illegalArg(threshold >= 0.0F && threshold <= 1.0F, "threshold");
        ClusterServiceFilter clusterservicefilter = new ClusterServiceFilter("grid", new ClusterNodeTypeFilter(flag, flag1, flag2, new ClusterFilter() {

            public boolean accept(ClusterNode clusternode)
            {
                return clusternode.getCpuLoad() >= threshold;
            }

            
            {
                super();
            }
        }
));
        filter = ((ClusterFilter) (s == null ? ((ClusterFilter) (clusterservicefilter)) : ((ClusterFilter) (new ClusterGroupFilter(s, clusterservicefilter)))));
        cluster = XtierKernel.getInstance().cluster();
    }

    public GridTaskBasicTopology()
    {
        this(null, 0.0F, true, true, true);
    }

    public Set getNodes(GridTaskUnitContext gridtaskunitcontext)
    {
        return cluster.getNodes(filter);
    }

    private ClusterFilter filter;
    private ClusterService cluster;
}
