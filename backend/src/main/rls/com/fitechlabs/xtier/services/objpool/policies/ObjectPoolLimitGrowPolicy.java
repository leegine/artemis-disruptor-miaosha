// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.objpool.policies;

import com.fitechlabs.xtier.services.objpool.*;
import com.fitechlabs.xtier.utils.ArgAssert;

public class ObjectPoolLimitGrowPolicy
    implements ObjectPoolResizePolicy
{

    public ObjectPoolLimitGrowPolicy(int i)
    {
        this(i, true);
    }

    public ObjectPoolLimitGrowPolicy(int i, boolean flag)
    {
        ArgAssert.illegalRange(i > 0, "limit", "limit > 0");
        limit = i;
        overflow = flag;
    }

    public int addBeforeAcquire(ObjectPoolStats objectpoolstats)
        throws ObjectPoolException
    {
        return objectpoolstats.getFree() != 0 || objectpoolstats.getSize() >= limit && !overflow ? 0 : 1;
    }

    public int removeBeforeRelease(ObjectPoolStats objectpoolstats)
        throws ObjectPoolException
    {
        return objectpoolstats.getSize() <= limit ? 0 : 1;
    }

    private final int limit;
    private final boolean overflow;
}
