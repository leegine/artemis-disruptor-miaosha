// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.objpool;


// Referenced classes of package com.fitechlabs.xtier.services.objpool:
//            ObjectPoolException, ObjectPoolStats

public interface ObjectPoolResizePolicy
{

    public abstract int addBeforeAcquire(ObjectPoolStats objectpoolstats)
        throws ObjectPoolException;

    public abstract int removeBeforeRelease(ObjectPoolStats objectpoolstats)
        throws ObjectPoolException;
}
