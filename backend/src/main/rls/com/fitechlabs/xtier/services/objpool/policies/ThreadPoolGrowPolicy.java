// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.objpool.policies;

import com.fitechlabs.xtier.services.objpool.threads.ThreadPoolResizePolicy;
import com.fitechlabs.xtier.services.objpool.threads.ThreadPoolStats;
import com.fitechlabs.xtier.utils.ArgAssert;

public class ThreadPoolGrowPolicy
    implements ThreadPoolResizePolicy
{

    public ThreadPoolGrowPolicy()
    {
        this(10);
    }

    public ThreadPoolGrowPolicy(float f)
    {
        factor = 0.0F;
        increment = 0;
        ArgAssert.illegalRange(f > 1.0F, "factor > 1", "factor");
        factor = f;
    }

    public ThreadPoolGrowPolicy(int i)
    {
        factor = 0.0F;
        increment = 0;
        ArgAssert.illegalRange(i > 1, "increment > 1", "increment");
        increment = i;
    }

    public int addBeforeTaskEnqueued(ThreadPoolStats threadpoolstats)
    {
        if(threadpoolstats.getFreeThreads() == 0)
            return factor <= 0.0F ? increment : (int)((float)threadpoolstats.getSize() * factor);
        else
            return 0;
    }

    public int removeAfterTaskFinished(ThreadPoolStats threadpoolstats)
    {
        return 0;
    }

    private float factor;
    private int increment;
}
