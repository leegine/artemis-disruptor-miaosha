// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.marshal.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.marshal.ByteUtf8DecodeResult;

public class ByteUtf8DecodeResultImpl
    implements ByteUtf8DecodeResult
{

    ByteUtf8DecodeResultImpl(int i, String s)
    {
        off = i;
        obj = s;
    }

    public int getOffset()
    {
        return off;
    }

    public String getObject()
    {
        return obj;
    }

    public String toString()
    {
        return L10n.format("SRVC.MARSHAL.TXT4", new Integer(off), obj);
    }

    private int off;
    private String obj;
}
