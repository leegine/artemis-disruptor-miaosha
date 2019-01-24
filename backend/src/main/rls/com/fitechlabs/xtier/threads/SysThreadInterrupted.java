// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.threads;

import com.fitechlabs.xtier.l10n.L10n;
import java.util.Date;

public class SysThreadInterrupted extends RuntimeException
{

    public SysThreadInterrupted()
    {
        name = Thread.currentThread().getName();
        timestamp = System.currentTimeMillis();
    }

    public String toString()
    {
        return L10n.format("SYS.THRDS.ERR2", name, new Date(timestamp));
    }

    private String name;
    private long timestamp;
}
