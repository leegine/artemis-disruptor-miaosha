// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.objpool.arrpools;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.objpool.adapters.PoolObjectAbstractAdapter;
import com.fitechlabs.xtier.utils.ArgAssert;

public class FloatArray extends PoolObjectAbstractAdapter
{

    public FloatArray(int i)
    {
        ArgAssert.illegalArg(i >= 0, "size");
        arr = new float[i];
    }

    public float[] getArr()
    {
        return arr;
    }

    public String toString()
    {
        return L10n.format("SRVC.OBJPOOL.TXT10", new Integer(arr.length));
    }

    private final float arr[];
}
