// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.cache.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.cache.CacheStats;
import java.util.Date;

class CacheStatsImpl
    implements CacheStats
{

    CacheStatsImpl()
    {
        createTime = System.currentTimeMillis();
        lastAccessTime = System.currentTimeMillis();
        reads = 0L;
        writes = 0L;
        hits = 0L;
        misses = 0L;
        mutex = new Object();
    }

    public long getCreateTime()
    {
        return createTime;
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

    public long getReads()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return reads;
        Exception exception;
        exception;
        throw exception;
    }

    public long getWrites()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return writes;
        Exception exception;
        exception;
        throw exception;
    }

    public long getHits()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return hits;
        Exception exception;
        exception;
        throw exception;
    }

    public long getMisses()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return misses;
        Exception exception;
        exception;
        throw exception;
    }

    public float getReadRate()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return (float)reads / (float)(reads + writes);
        Exception exception;
        exception;
        throw exception;
    }

    public float getWriteRate()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return (float)writes / (float)(reads + writes);
        Exception exception;
        exception;
        throw exception;
    }

    public float getHitRate()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return (float)hits / (float)(hits + misses);
        Exception exception;
        exception;
        throw exception;
    }

    public float getMissRate()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return (float)misses / (float)(hits + misses);
        Exception exception;
        exception;
        throw exception;
    }

    public void onWrite()
    {
        synchronized(mutex)
        {
            writes++;
            lastAccessTime = System.currentTimeMillis();
        }
    }

    public void onHit()
    {
        synchronized(mutex)
        {
            hits++;
            reads++;
            lastAccessTime = System.currentTimeMillis();
        }
    }

    public void onMiss()
    {
        synchronized(mutex)
        {
            misses++;
            reads++;
            lastAccessTime = System.currentTimeMillis();
        }
    }

    public String toString()
    {
        return L10n.format("SRVC.CACHE.TXT18", new Object[] {
            new Long(getHits()), new Long(getMisses()), new Long(getReads()), new Long(getWrites()), new Float(getHitRate()), new Float(getMissRate()), new Float(getReadRate()), new Float(getWriteRate()), new Date(createTime), new Date(lastAccessTime)
        });
    }

    private long createTime;
    private long lastAccessTime;
    private long reads;
    private long writes;
    private long hits;
    private long misses;
    private Object mutex;
}
