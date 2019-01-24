// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.grid;

import com.fitechlabs.xtier.services.marshal.Marshallable;

// Referenced classes of package com.fitechlabs.xtier.services.grid:
//            GridTaskUnitContext

public interface GridTaskSplitRef
{

    public abstract double getIoWeight();

    public abstract double getCpuWeight();

    public abstract double getMemoryWeight();

    public abstract GridTaskUnitContext getTaskUnitContext();

    public abstract Object getRouterInfo();

    public abstract long getTimeout();

    public abstract Marshallable getArg();
}
