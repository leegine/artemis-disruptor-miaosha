// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.cache;

import com.fitechlabs.xtier.l10n.L10n;

// Referenced classes of package com.fitechlabs.xtier.services.cache:
//            CacheException

public class CacheTxDeadlockException extends CacheException
{

    public CacheTxDeadlockException(String s)
    {
        super(s);
    }

    public CacheTxDeadlockException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public String toString()
    {
        return L10n.format("SRVC.CACHE.TXT19", getMessage());
    }
}
