// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.objpool.policies;

import com.fitechlabs.xtier.services.objpool.threads.ThreadPoolResizePolicy;
import com.fitechlabs.xtier.services.objpool.threads.ThreadPoolStats;

public class ThreadPoolOverflowPolicy
    implements ThreadPoolResizePolicy
{

    public ThreadPoolOverflowPolicy()
    {
    }

    public int addBeforeTaskEnqueued(ThreadPoolStats threadpoolstats)
    {
        return threadpoolstats.getFreeThreads() != 0 ? 0 : 1;
    }

    public int removeAfterTaskFinished(ThreadPoolStats threadpoolstats)
    {
        return threadpoolstats.getBacklog() != 0 || threadpoolstats.getSize() <= threadpoolstats.getInitialSize() ? 0 : threadpoolstats.getSize() - threadpoolstats.getInitialSize();
    }
}
