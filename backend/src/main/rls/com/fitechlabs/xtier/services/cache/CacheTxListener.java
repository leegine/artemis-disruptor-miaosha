// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.cache;


// Referenced classes of package com.fitechlabs.xtier.services.cache:
//            CacheException, Cache, CacheTx

public interface CacheTxListener
{

    public abstract void onTxEvent(int i, Cache cache, CacheTx cachetx)
        throws CacheException;

    public static final int TX_STARTED = 1;
    public static final int TX_PREPARED = 2;
    public static final int TX_FINISHED = 3;
}
