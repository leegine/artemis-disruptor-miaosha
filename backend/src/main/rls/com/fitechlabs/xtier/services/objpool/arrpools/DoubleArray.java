// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.objpool.arrpools;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.objpool.adapters.PoolObjectAbstractAdapter;
import com.fitechlabs.xtier.utils.ArgAssert;

public class DoubleArray extends PoolObjectAbstractAdapter
{

    public DoubleArray(int i)
    {
        ArgAssert.illegalArg(i >= 0, "size");
        arr = new double[i];
    }

    public double[] getArr()
    {
        return arr;
    }

    public String toString()
    {
        return L10n.format("SRVC.OBJPOOL.TXT8", new Integer(arr.length));
    }

    private final double arr[];
}
