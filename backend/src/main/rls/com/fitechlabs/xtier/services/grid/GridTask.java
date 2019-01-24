// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.grid;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.grid.failover.GridTaskFailFastResolver;
import com.fitechlabs.xtier.services.grid.routers.GridTaskBasicRouter;
import com.fitechlabs.xtier.services.grid.topology.GridTaskBasicTopology;
import com.fitechlabs.xtier.utils.ArgAssert;

// Referenced classes of package com.fitechlabs.xtier.services.grid:
//            GridTaskUnitFactory, GridTaskTopology, GridTaskRouter, GridTaskFailoverResolver

public class GridTask
{

    public GridTask(int i, GridTaskUnitFactory gridtaskunitfactory)
    {
        this(i, gridtaskunitfactory, ((GridTaskTopology) (new GridTaskBasicTopology(null, 0.0F, false, false, true))), ((GridTaskRouter) (new GridTaskBasicRouter())), ((GridTaskFailoverResolver) (new GridTaskFailFastResolver())));
    }

    public GridTask(int i, GridTaskUnitFactory gridtaskunitfactory, GridTaskTopology gridtasktopology, GridTaskRouter gridtaskrouter, GridTaskFailoverResolver gridtaskfailoverresolver)
    {
        factory = null;
        topology = null;
        router = null;
        failover = null;
        ArgAssert.nullArg(gridtaskunitfactory, "factory");
        ArgAssert.nullArg(gridtasktopology, "topology");
        ArgAssert.nullArg(gridtaskrouter, "router");
        ArgAssert.nullArg(gridtaskfailoverresolver, "failover");
        tid = i;
        factory = gridtaskunitfactory;
        topology = gridtasktopology;
        router = gridtaskrouter;
        failover = gridtaskfailoverresolver;
    }

    public int getId()
    {
        return tid;
    }

    public GridTaskUnitFactory getUnitFactory()
    {
        return factory;
    }

    public GridTaskTopology getTopology()
    {
        return topology;
    }

    public GridTaskRouter getRouter()
    {
        return router;
    }

    public GridTaskFailoverResolver getFailoverResolver()
    {
        return failover;
    }

    public boolean equals(Object obj)
    {
        if(obj == this)
            return true;
        if(!(obj instanceof GridTask))
            return false;
        else
            return ((GridTask)obj).getId() == tid;
    }

    public String toString()
    {
        return L10n.format("SRVC.GRID.TXT2", new Integer(tid), factory, topology, router, failover);
    }

    private int tid;
    private GridTaskUnitFactory factory;
    private GridTaskTopology topology;
    private GridTaskRouter router;
    private GridTaskFailoverResolver failover;
}
