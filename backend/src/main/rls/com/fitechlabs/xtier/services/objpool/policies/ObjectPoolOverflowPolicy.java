// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.objpool.policies;

import com.fitechlabs.xtier.services.objpool.*;

public class ObjectPoolOverflowPolicy
    implements ObjectPoolResizePolicy
{

    public ObjectPoolOverflowPolicy()
    {
    }

    public int addBeforeAcquire(ObjectPoolStats objectpoolstats)
        throws ObjectPoolException
    {
        return objectpoolstats.getFree() != 0 ? 0 : 1;
    }

    public int removeBeforeRelease(ObjectPoolStats objectpoolstats)
        throws ObjectPoolException
    {
        return objectpoolstats.getFree() != objectpoolstats.getInitialSize() ? 0 : 1;
    }
}
