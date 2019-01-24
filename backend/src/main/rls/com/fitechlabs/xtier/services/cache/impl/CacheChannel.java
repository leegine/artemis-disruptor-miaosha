// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.cache.impl;

import com.fitechlabs.xtier.services.objpool.ObjectPool;

public class CacheChannel
    implements Comparable
{

    CacheChannel(int i, int j, int k, int l, long l1)
    {
        port = i;
        max = j;
        min = k;
        attempts = l;
        timeout = l1;
    }

    public int getMax()
    {
        return max;
    }

    public int getMin()
    {
        return min;
    }

    public int getPort()
    {
        return port;
    }

    public long getTimeout()
    {
        return timeout;
    }

    public ObjectPool getPool()
    {
        return pool;
    }

    public void setPool(ObjectPool objectpool)
    {
        pool = objectpool;
    }

    public int getAttempts()
    {
        return attempts;
    }

    public int compareTo(Object obj)
    {
        CacheChannel cachechannel = (CacheChannel)obj;
        return min >= cachechannel.min ? ((int) (min != cachechannel.min ? 1 : 0)) : -1;
    }

    private int port;
    private int max;
    private int min;
    private int attempts;
    private long timeout;
    private ObjectPool pool;
}
