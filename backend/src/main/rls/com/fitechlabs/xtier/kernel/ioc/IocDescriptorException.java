// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.kernel.ioc;

import com.fitechlabs.xtier.l10n.L10n;

public class IocDescriptorException extends Exception
{

    public IocDescriptorException(String s)
    {
        super(s);
    }

    public IocDescriptorException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public String toString()
    {
        return L10n.format("KRNL.IOC.TXT4", getMessage());
    }
}
