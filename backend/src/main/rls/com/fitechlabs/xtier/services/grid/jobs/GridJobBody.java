// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.grid.jobs;

import com.fitechlabs.xtier.services.grid.GridTaskUnitContext;
import com.fitechlabs.xtier.services.grid.GridTaskUnitResult;
import com.fitechlabs.xtier.services.marshal.Marshallable;

public interface GridJobBody
{

    public abstract GridTaskUnitResult exec(GridTaskUnitContext gridtaskunitcontext, Marshallable marshallable);
}
