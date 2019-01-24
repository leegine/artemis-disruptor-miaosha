// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.kernel;

import com.fitechlabs.xtier.l10n.L10n;

public abstract class KernelServiceException extends Exception
{

    public KernelServiceException(String s)
    {
        super(s);
    }

    public KernelServiceException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public String toString()
    {
        return L10n.format("KRNL.KRNL.TXT17", getMessage());
    }
}
