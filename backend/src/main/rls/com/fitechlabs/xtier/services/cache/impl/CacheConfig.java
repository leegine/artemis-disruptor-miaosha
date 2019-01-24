// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.cache.impl;

import com.fitechlabs.xtier.services.cache.*;

public class CacheConfig
{

    protected CacheConfig(int i)
    {
        cacheType = i;
    }

    public int getCacheType()
    {
        return cacheType;
    }

    public int getDgcPort()
    {
        return dgcPort;
    }

    public void setDgcPort(int i)
    {
        dgcPort = i;
    }

    public int getDgcReplyPort()
    {
        return dgcReplyPort;
    }

    public void setDgcReplyPort(int i)
    {
        dgcReplyPort = i;
    }

    public long getDgcTimeout()
    {
        return dgcTimeout;
    }

    public void setDgcTimeout(long l)
    {
        dgcTimeout = l;
    }

    public long getDgcTxAge()
    {
        return dgcTxAge;
    }

    public void setDgcTxAge(long l)
    {
        dgcTxAge = l;
    }

    public CacheKeyAttrsResolver getAttrsResolver()
    {
        return attrsResolver;
    }

    public void setAttrsResolver(CacheKeyAttrsResolver cachekeyattrsresolver)
    {
        attrsResolver = cachekeyattrsresolver;
    }

    public String getCacheName()
    {
        return cacheName;
    }

    public void setCacheName(String s)
    {
        cacheName = s;
    }

    public CacheExpirationPolicy getExpPolicy()
    {
        return expPolicy;
    }

    public void setExpPolicy(CacheExpirationPolicy cacheexpirationpolicy)
    {
        expPolicy = cacheexpirationpolicy;
    }

    public CacheSpooler getSpooler()
    {
        return spooler;
    }

    public void setSpooler(CacheSpooler cachespooler)
    {
        spooler = cachespooler;
    }

    public CacheStore getStore()
    {
        return store;
    }

    public void setStore(CacheStore cachestore)
    {
        store = cachestore;
    }

    public String getThreadPoolName()
    {
        return threadPoolName;
    }

    public void setThreadPoolName(String s)
    {
        threadPoolName = s;
    }

    public CacheTopology getTopology()
    {
        return topology;
    }

    public void setTopology(CacheTopology cachetopology)
    {
        topology = cachetopology;
    }

    public CacheLoader getLoader()
    {
        return loader;
    }

    public void setLoader(CacheLoader cacheloader)
    {
        loader = cacheloader;
    }

    public int getInitSize()
    {
        return initSize;
    }

    public void setInitSize(int i)
    {
        initSize = i;
    }

    public long getDfltTxTimeout()
    {
        return dfltTxTimeout;
    }

    public void setDfltTxTimeout(long l)
    {
        dfltTxTimeout = l;
    }

    private String cacheName;
    private int initSize;
    private String threadPoolName;
    private long dfltTxTimeout;
    private int dgcPort;
    private int dgcReplyPort;
    private long dgcTimeout;
    private long dgcTxAge;
    private CacheLoader loader;
    private CacheStore store;
    private CacheExpirationPolicy expPolicy;
    private CacheTopology topology;
    private CacheKeyAttrsResolver attrsResolver;
    private CacheSpooler spooler;
    private final int cacheType;
}
