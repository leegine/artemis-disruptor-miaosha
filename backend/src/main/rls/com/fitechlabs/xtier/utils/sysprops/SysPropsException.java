// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.utils.sysprops;

import com.fitechlabs.xtier.l10n.L10n;

public class SysPropsException extends Exception
{

    public SysPropsException(String s)
    {
        super(s);
    }

    public SysPropsException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public String toString()
    {
        return L10n.format("KRNL.UTILS.SYSPROPS.TXT1", getMessage());
    }
}
