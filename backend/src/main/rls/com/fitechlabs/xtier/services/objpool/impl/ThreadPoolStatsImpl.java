// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.objpool.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.objpool.threads.ThreadPoolStats;
import java.util.Date;

// Referenced classes of package com.fitechlabs.xtier.services.objpool.impl:
//            ThreadPoolImpl

class ThreadPoolStatsImpl
    implements ThreadPoolStats
{

    public ThreadPoolStatsImpl(ThreadPoolImpl threadpoolimpl)
    {
        totalBacklog = 0;
        totalFree = 0;
        totalBusy = 0;
        totalBacklogTime = 0L;
        accessCount = 0L;
        synchronized(mutex)
        {
            touch(threadpoolimpl);
            initSize = size;
            lastAccessTime = System.currentTimeMillis();
        }
    }

    void onTaskAdded(ThreadPoolImpl threadpoolimpl)
    {
        synchronized(mutex)
        {
            lastAccessTime = System.currentTimeMillis();
            touch(threadpoolimpl);
        }
    }

    void onTaskFinished(ThreadPoolImpl threadpoolimpl)
    {
        touch(threadpoolimpl);
    }

    void onTaskStarted(long l)
    {
        synchronized(mutex)
        {
            lastBacklogTime = System.currentTimeMillis() - l;
            totalBacklogTime += lastBacklogTime;
            taskCount++;
        }
    }

    void touch(ThreadPoolImpl threadpoolimpl)
    {
        synchronized(mutex)
        {
            if(!threadpoolimpl.isStopped())
            {
                size = threadpoolimpl.getSize();
                backlog = threadpoolimpl.getBacklog();
                free = threadpoolimpl.getFreeThreads();
                busy = threadpoolimpl.getBusyThreads();
                totalFree += free;
                totalBusy += busy;
                totalBacklog += backlog;
                accessCount++;
            }
        }
    }

    public long getCreateTime()
    {
        return createTime;
    }

    public int getInitialSize()
    {
        return initSize;
    }

    public int getSize()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return size;
        Exception exception;
        exception;
        throw exception;
    }

    public int getFreeThreads()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return free;
        Exception exception;
        exception;
        throw exception;
    }

    public int getBusyThreads()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return busy;
        Exception exception;
        exception;
        throw exception;
    }

    public float getAvgFreeThreads()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        if(!$assertionsDisabled && accessCount <= 0L)
            throw new AssertionError();
        return (float)totalFree / (float)accessCount;
        Exception exception;
        exception;
        throw exception;
    }

    public float getAvgBusyThreads()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        if(!$assertionsDisabled && accessCount <= 0L)
            throw new AssertionError();
        return (float)totalBusy / (float)accessCount;
        Exception exception;
        exception;
        throw exception;
    }

    public int getBacklog()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return backlog;
        Exception exception;
        exception;
        throw exception;
    }

    public float getAvgBacklog()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        if(!$assertionsDisabled && accessCount <= 0L)
            throw new AssertionError();
        return (float)totalBacklog / (float)accessCount;
        Exception exception;
        exception;
        throw exception;
    }

    public long getLastAccessTime()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return lastAccessTime;
        Exception exception;
        exception;
        throw exception;
    }

    public long getLastBacklogTime()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return lastBacklogTime;
        Exception exception;
        exception;
        throw exception;
    }

    public long getAvgBacklogTime()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return taskCount != 0L ? totalBacklogTime / taskCount : 0L;
        Exception exception;
        exception;
        throw exception;
    }

    public void reset()
    {
        size = 0;
        backlog = 0;
        free = 0;
        busy = 0;
        totalFree = 0;
        totalBusy = 0;
        totalBacklog = 0;
        totalBacklogTime = 0L;
        lastAccessTime = System.currentTimeMillis();
        accessCount = 1L;
        taskCount = 0L;
    }

    public String toString()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return L10n.format("SRVC.OBJPOOL.TXT25", new Object[] {
            new Date(getCreateTime()), new Integer(getSize()), new Integer(getInitialSize()), new Integer(getFreeThreads()), new Integer(getBusyThreads()), new Float(getAvgFreeThreads()), new Float(getAvgBusyThreads()), new Integer(getBacklog()), new Float(getAvgBacklog()), new Long(getLastBacklogTime()),
            new Long(getAvgBacklogTime()), new Date(getLastAccessTime())
        });
        Exception exception;
        exception;
        throw exception;
    }

    static Class _mthclass$(String s)
    {
        try
        {
            return Class.forName(s);
        }
        catch(ClassNotFoundException classnotfoundexception)
        {
            throw (new NoClassDefFoundError()).initCause(classnotfoundexception);
        }
    }

    private final long createTime = System.currentTimeMillis();
    private final int initSize;
    private int size;
    private int free;
    private int busy;
    private int backlog;
    private long lastAccessTime;
    private long lastBacklogTime;
    private int totalBacklog;
    private int totalFree;
    private int totalBusy;
    private long totalBacklogTime;
    private long accessCount;
    private long taskCount;
    private final Object mutex = new Object();
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(ThreadPoolStatsImpl.class).desiredAssertionStatus();
    }
}
