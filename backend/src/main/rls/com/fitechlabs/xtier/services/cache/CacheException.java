// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.cache;

import com.fitechlabs.xtier.l10n.L10n;

public class CacheException extends Exception
{

    public CacheException(String s)
    {
        super(s);
    }

    public CacheException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public String toString()
    {
        return L10n.format("SRVC.CACHE.TXT1", getMessage());
    }
}
