// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.objpool.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.objpool.ObjectPool;
import com.fitechlabs.xtier.services.objpool.ObjectPoolStats;
import java.util.Date;

class ObjectPoolStatsImpl
    implements ObjectPoolStats
{

    ObjectPoolStatsImpl(ObjectPool objectpool)
    {
        lastAccessTime = System.currentTimeMillis();
        hits = 0;
        misses = 0;
        accessCount = 0L;
        totalFree = 0L;
        totalAcquired = 0L;
        touch(objectpool);
        initSize = size;
    }

    void onAcquire(ObjectPool objectpool, boolean flag)
    {
        synchronized(mutex)
        {
            if(flag)
                hits++;
            else
                misses++;
            touch(objectpool);
        }
    }

    void onRelease(ObjectPool objectpool)
    {
        touch(objectpool);
    }

    void onInvalidated(ObjectPool objectpool)
    {
        touch(objectpool);
    }

    void touch(ObjectPool objectpool)
    {
        synchronized(mutex)
        {
            size = objectpool.getSize();
            acquired = objectpool.getAcquired();
            free = objectpool.getFree();
            totalFree += free;
            totalAcquired += acquired;
            lastAccessTime = System.currentTimeMillis();
            accessCount++;
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

    public int getFree()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return free;
        Exception exception;
        exception;
        throw exception;
    }

    public int getAcquired()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return acquired;
        Exception exception;
        exception;
        throw exception;
    }

    public int getMisses()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return misses;
        Exception exception;
        exception;
        throw exception;
    }

    public int getHits()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return hits;
        Exception exception;
        exception;
        throw exception;
    }

    public float getAvgMisses()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return misses + hits != 0 ? (float)misses / (float)(misses + hits) : 0.0F;
        Exception exception;
        exception;
        throw exception;
    }

    public float getAvgHits()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return misses + hits != 0 ? (float)hits / (float)(misses + hits) : 0.0F;
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

    public float getAvgFree()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return (float)totalFree / (float)accessCount;
        Exception exception;
        exception;
        throw exception;
    }

    public float getAvgAcquired()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return (float)totalAcquired / (float)accessCount;
        Exception exception;
        exception;
        throw exception;
    }

    public void reset()
    {
        size = 0;
        free = 0;
        acquired = 0;
        hits = 0;
        misses = 0;
        totalFree = 0L;
        totalAcquired = 0L;
        accessCount = 1L;
        lastAccessTime = System.currentTimeMillis();
    }

    public String toString()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return L10n.format("SRVC.OBJPOOL.TXT24", new Object[] {
            new Date(getCreateTime()), new Integer(getSize()), new Integer(getInitialSize()), new Integer(getFree()), new Integer(getAcquired()), new Float(getAvgFree()), new Float(getAvgAcquired()), new Integer(getMisses()), new Integer(getHits()), new Float(getAvgMisses()), 
            new Float(getAvgHits()), new Date(getLastAccessTime())
        });
        Exception exception;
        exception;
        throw exception;
    }

    private final long createTime = System.currentTimeMillis();
    private final int initSize;
    private long lastAccessTime;
    private int hits;
    private int misses;
    private int size;
    private int acquired;
    private int free;
    private long accessCount;
    private long totalFree;
    private long totalAcquired;
    private final Object mutex = new Object();
}
