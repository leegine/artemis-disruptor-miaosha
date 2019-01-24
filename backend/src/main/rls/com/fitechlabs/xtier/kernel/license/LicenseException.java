// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.kernel.license;

import com.fitechlabs.xtier.l10n.L10n;

public class LicenseException extends Exception
{

    public LicenseException(String s)
    {
        super(s);
    }

    public LicenseException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public String toString()
    {
        return L10n.format("KRNL.LCNS.TXT1", getMessage());
    }
}
