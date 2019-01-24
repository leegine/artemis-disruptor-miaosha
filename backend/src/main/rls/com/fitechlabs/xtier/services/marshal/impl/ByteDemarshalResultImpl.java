// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.marshal.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.marshal.ByteDemarshalResult;
import com.fitechlabs.xtier.services.marshal.Marshallable;

public class ByteDemarshalResultImpl
    implements ByteDemarshalResult
{

    ByteDemarshalResultImpl(int i, Marshallable marshallable)
    {
        off = i;
        obj = marshallable;
    }

    public int getOffset()
    {
        return off;
    }

    public Marshallable getObject()
    {
        return obj;
    }

    public String toString()
    {
        return L10n.format("SRVC.MARSHAL.TXT5", new Integer(off), obj);
    }

    private int off;
    private Marshallable obj;
}
