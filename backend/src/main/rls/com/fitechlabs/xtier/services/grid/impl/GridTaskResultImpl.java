// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.grid.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.grid.GridTaskResult;
import com.fitechlabs.xtier.services.marshal.Marshallable;
import java.util.Date;

class GridTaskResultImpl
    implements GridTaskResult
{

    GridTaskResultImpl(int i, long l, Marshallable marshallable, boolean flag, long l1, 
            long l2)
    {
        tid = i;
        eid = l;
        retval = marshallable;
        isSuccessful = flag;
        startTime = l1;
        endTime = l2;
    }

    public int getTaskId()
    {
        return tid;
    }

    public long getExecId()
    {
        return eid;
    }

    public long getStartTime()
    {
        return startTime;
    }

    public long getEndTime()
    {
        return endTime;
    }

    public boolean isSuccessful()
    {
        return isSuccessful;
    }

    public Marshallable getReturnValue()
    {
        return retval;
    }

    public String toString()
    {
        return L10n.format("SRVC.GRID.TXT4", new Object[] {
            new Integer(tid), new Long(eid), new Date(startTime), new Date(endTime), new Boolean(isSuccessful), retval
        });
    }

    private int tid;
    private long eid;
    private long startTime;
    private long endTime;
    private boolean isSuccessful;
    private Marshallable retval;
}
