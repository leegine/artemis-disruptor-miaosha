// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.cache;


// Referenced classes of package com.fitechlabs.xtier.services.cache:
//            CacheException

public interface CacheTx
{

    public abstract long getXid();

    public abstract int getIsolationLevel();

    public abstract long getTimestamp();

    public abstract long getTimeout();

    public abstract void setTimeout(long l);

    public abstract void prepare()
        throws CacheException;

    public abstract void commit()
        throws CacheException;

    public abstract void rollback()
        throws CacheException;

    public abstract void setRollbackOnly();

    public abstract boolean isRollbackOnly();

    public abstract int getState();

    public static final int READ_COMMITTED = 1;
    public static final int SERIALIZABLE = 2;
    public static final int TX_ACTIVE = 10;
    public static final int TX_COMMITTING = 11;
    public static final int TX_COMMITTED = 12;
    public static final int TX_ROLLING_BACK = 13;
    public static final int TX_ROLLED_BACK = 14;
    public static final int TX_PREPARING = 15;
    public static final int TX_PREPARED = 16;
    public static final int TX_MARKED_ROLLBACK = 17;
    public static final int TX_HEURISTIC = 18;
}
