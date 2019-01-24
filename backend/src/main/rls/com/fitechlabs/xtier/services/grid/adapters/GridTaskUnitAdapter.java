// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.grid.adapters;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.grid.GridTaskUnit;
import com.fitechlabs.xtier.services.grid.GridTaskUnitContext;

public abstract class GridTaskUnitAdapter
    implements GridTaskUnit
{

    protected GridTaskUnitAdapter(GridTaskUnitContext gridtaskunitcontext)
    {
        ctx = null;
        ctx = gridtaskunitcontext;
    }

    protected GridTaskUnitContext getTaskUnitContext()
    {
        return ctx;
    }

    protected int getTaskId()
    {
        return ctx.getTaskId();
    }

    protected int getUnitId()
    {
        return ctx.getUnitId();
    }

    protected long getExecId()
    {
        return ctx.getExecId();
    }

    public String toString()
    {
        return L10n.format("SRVC.GRID.TXT5", ctx);
    }

    private GridTaskUnitContext ctx;
}
