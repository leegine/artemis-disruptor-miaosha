// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.grid;

import com.fitechlabs.xtier.l10n.L10n;

public class GridTaskUnitContext
{

    public GridTaskUnitContext(int i, int j, long l)
    {
        tid = i;
        uid = j;
        eid = l;
    }

    public int getTaskId()
    {
        return tid;
    }

    public int getUnitId()
    {
        return uid;
    }

    public long getExecId()
    {
        return eid;
    }

    public String toString()
    {
        return L10n.format("SRVC.GRID.TXT9", new Integer(tid), new Integer(uid), new Long(eid));
    }

    private int tid;
    private int uid;
    private long eid;
}
