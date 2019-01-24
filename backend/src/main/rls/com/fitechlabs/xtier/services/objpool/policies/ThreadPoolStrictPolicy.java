// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.objpool.policies;

import com.fitechlabs.xtier.services.objpool.threads.ThreadPoolResizePolicy;
import com.fitechlabs.xtier.services.objpool.threads.ThreadPoolStats;

public class ThreadPoolStrictPolicy
    implements ThreadPoolResizePolicy
{

    public ThreadPoolStrictPolicy()
    {
    }

    public int addBeforeTaskEnqueued(ThreadPoolStats threadpoolstats)
    {
        return 0;
    }

    public int removeAfterTaskFinished(ThreadPoolStats threadpoolstats)
    {
        return 0;
    }
}
