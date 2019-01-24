// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.objpool.policies;

import com.fitechlabs.xtier.services.objpool.*;
import com.fitechlabs.xtier.utils.ArgAssert;

public class ObjectPoolGrowPolicy
    implements ObjectPoolResizePolicy
{

    public ObjectPoolGrowPolicy()
    {
        this(10);
    }

    public ObjectPoolGrowPolicy(float f)
    {
        factor = 0.0F;
        increment = 0;
        ArgAssert.illegalRange(f > 0.0F, "factor > 1", "factor");
        factor = f;
    }

    public ObjectPoolGrowPolicy(int i)
    {
        factor = 0.0F;
        increment = 0;
        ArgAssert.illegalRange(i > 0, "increment > 1", "increment");
        increment = i;
    }

    public int addBeforeAcquire(ObjectPoolStats objectpoolstats)
        throws ObjectPoolException
    {
        if(objectpoolstats.getFree() == 0)
            return factor <= 0.0F ? increment : (int)((float)objectpoolstats.getSize() * factor);
        else
            return 0;
    }

    public int removeBeforeRelease(ObjectPoolStats objectpoolstats)
        throws ObjectPoolException
    {
        return 0;
    }

    private float factor;
    private int increment;
}
