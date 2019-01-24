// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.grid.adapters;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.grid.GridTaskSplitRef;
import com.fitechlabs.xtier.services.grid.GridTaskUnitContext;
import com.fitechlabs.xtier.services.marshal.Marshallable;
import com.fitechlabs.xtier.utils.ArgAssert;

public class GridTaskSplitRefAdapter
    implements GridTaskSplitRef
{

    public GridTaskSplitRefAdapter(GridTaskUnitContext gridtaskunitcontext, Marshallable marshallable)
    {
        ctx = null;
        ioWeight = -1D;
        cpuWeight = -1D;
        memWeight = -1D;
        arg = null;
        timeout = 0L;
        routerInfo = null;
        ctx = gridtaskunitcontext;
        arg = marshallable;
    }

    public GridTaskSplitRefAdapter(GridTaskUnitContext gridtaskunitcontext, Marshallable marshallable, long l, double d, double d1, double d2)
    {
        ctx = null;
        ioWeight = -1D;
        cpuWeight = -1D;
        memWeight = -1D;
        arg = null;
        timeout = 0L;
        routerInfo = null;
        ArgAssert.illegalArg(d1 == -1D || d1 > 0.0D, "cpuWeight");
        ArgAssert.illegalArg(d == -1D || d > 0.0D, "ioWeight");
        ArgAssert.illegalArg(d2 == -1D || d2 > 0.0D, "memWeight");
        ArgAssert.illegalArg(l >= 0L, "timeout");
        ctx = gridtaskunitcontext;
        timeout = l;
        arg = marshallable;
        ioWeight = d;
        cpuWeight = d1;
        memWeight = d2;
    }

    public void setRouterInfo(Object obj)
    {
        routerInfo = obj;
    }

    public Object getRouterInfo()
    {
        return routerInfo;
    }

    public GridTaskUnitContext getTaskUnitContext()
    {
        return ctx;
    }

    public Marshallable getArg()
    {
        return arg;
    }

    public double getCpuWeight()
    {
        return cpuWeight;
    }

    public double getIoWeight()
    {
        return ioWeight;
    }

    public double getMemoryWeight()
    {
        return memWeight;
    }

    public long getTimeout()
    {
        return timeout;
    }

    public void setCpuWeight(int i)
    {
        ArgAssert.illegalArg(i >= -1, "cpuWeight");
        cpuWeight = i;
    }

    public void setIoWeight(int i)
    {
        ArgAssert.illegalArg(i >= -1, "ioWeight");
        ioWeight = i;
    }

    public void setMemWeight(int i)
    {
        ArgAssert.illegalArg(i >= -1, "memWeight");
        memWeight = i;
    }

    public void setTimeout(long l)
    {
        ArgAssert.illegalArg(l >= 0L, "timeout");
        timeout = l;
    }

    public String toString()
    {
        return L10n.format("SRVC.GRID.TXT6", new Object[] {
            ctx, new Long(timeout), arg, (new Double(ioWeight)).toString(), (new Double(cpuWeight)).toString(), (new Double(memWeight)).toString(), routerInfo
        });
    }

    private GridTaskUnitContext ctx;
    private double ioWeight;
    private double cpuWeight;
    private double memWeight;
    private Marshallable arg;
    private long timeout;
    private Object routerInfo;
}
