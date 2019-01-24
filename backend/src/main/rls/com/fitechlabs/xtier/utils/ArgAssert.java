// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.utils;

import com.fitechlabs.xtier.l10n.L10n;

public final class ArgAssert
{

    public ArgAssert()
    {
    }

    public static void illegalRange(boolean flag, String s, String s1)
    {
        if(!flag)
            throw new IllegalArgumentException(L10n.format("ILLEGAL.RANGE.ERR", s, s1));
        else
            return;
    }

    public static void illegalArg(boolean flag, String s)
    {
        if(!flag)
            throw new IllegalArgumentException(L10n.format("ILLEGAL.ARG.ERR", s));
        else
            return;
    }

    public static void nullArg(Object obj, String s)
    {
        if(obj == null)
            throw new NullPointerException(L10n.format("NULL.ARG.ERR", s));
        else
            return;
    }
}
