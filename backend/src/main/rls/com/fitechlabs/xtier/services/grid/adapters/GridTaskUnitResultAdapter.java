// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.grid.adapters;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.grid.GridTaskUnitResult;
import com.fitechlabs.xtier.services.grid.GridUtils;
import com.fitechlabs.xtier.services.marshal.Marshallable;

public class GridTaskUnitResultAdapter
    implements GridTaskUnitResult
{

    public GridTaskUnitResultAdapter(int i, Marshallable marshallable, int j)
    {
        userErrCode = 0x80000000;
        retVal = null;
        retCode = i;
        retVal = marshallable;
        userErrCode = j;
    }

    public GridTaskUnitResultAdapter(int i, Marshallable marshallable)
    {
        userErrCode = 0x80000000;
        retVal = null;
        retCode = i;
        retVal = marshallable;
    }

    public GridTaskUnitResultAdapter(int i)
    {
        userErrCode = 0x80000000;
        retVal = null;
        retCode = i;
    }

    public int getReturnCode()
    {
        return retCode;
    }

    public int getUserErrorCode()
    {
        return userErrCode;
    }

    public Marshallable getReturnValue()
    {
        return retVal;
    }

    public String toString()
    {
        return L10n.format("SRVC.GRID.TXT7", new Object[] {
            GridUtils.getStrRetCode(retCode), retVal, new Integer(userErrCode)
        });
    }

    private int retCode;
    private int userErrCode;
    private Marshallable retVal;
}
