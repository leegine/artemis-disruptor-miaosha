// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.cache.impl;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.services.cache.CacheException;
import com.fitechlabs.xtier.services.log.LogService;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.services.objpool.ObjectPoolService;
import com.fitechlabs.xtier.services.objpool.threads.ThreadPool;

// Referenced classes of package com.fitechlabs.xtier.services.cache.impl:
//            CacheImpl, CacheConfig, CacheEventManager, CacheDataManager,
//            CacheTxManager, CacheCommManager, CacheStartupManager

public class CacheRegistry
{

    public CacheRegistry()
    {
    }

    protected void start()
        throws CacheException
    {
        XtierKernel xtierkernel = XtierKernel.getInstance();
        logger = xtierkernel.log().getLogger(cache.getName());
        pool = xtierkernel.objpool().getThreadPool(config.getThreadPoolName());
        if(!$assertionsDisabled && pool == null)
        {
            throw new AssertionError();
        } else
        {
            eventMgr.start();
            dataMgr.start();
            txMgr.start();
            commMgr.start();
            return;
        }
    }

    protected void stop()
        throws CacheException
    {
        commMgr.stop();
        txMgr.stop();
        dataMgr.stop();
        eventMgr.stop();
        XtierKernel.getInstance().objpool().deleteWaitThreadPool(pool.getName());
    }

    public ThreadPool getPool()
    {
        return pool;
    }

    public Logger getLogger()
    {
        return logger;
    }

    public CacheImpl getCache()
    {
        return cache;
    }

    public void setCache(CacheImpl cacheimpl)
    {
        cache = cacheimpl;
    }

    public CacheCommManager getCommMgr()
    {
        return commMgr;
    }

    public void setCommMgr(CacheCommManager cachecommmanager)
    {
        commMgr = cachecommmanager;
    }

    public CacheConfig getConfig()
    {
        return config;
    }

    public void setConfig(CacheConfig cacheconfig)
    {
        config = cacheconfig;
    }

    public CacheTxManager getTxMgr()
    {
        return txMgr;
    }

    public void setTxMgr(CacheTxManager cachetxmanager)
    {
        txMgr = cachetxmanager;
    }

    public CacheDataManager getDataMgr()
    {
        return dataMgr;
    }

    public void setDataMgr(CacheDataManager cachedatamanager)
    {
        dataMgr = cachedatamanager;
    }

    public CacheEventManager getEventMgr()
    {
        return eventMgr;
    }

    public void setEventMgr(CacheEventManager cacheeventmanager)
    {
        eventMgr = cacheeventmanager;
    }

    public CacheStartupManager getStartupMgr()
    {
        return startupMgr;
    }

    public void setStartupMgr(CacheStartupManager cachestartupmanager)
    {
        startupMgr = cachestartupmanager;
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

    private ThreadPool pool;
    private CacheImpl cache;
    private CacheTxManager txMgr;
    private CacheCommManager commMgr;
    private CacheDataManager dataMgr;
    private CacheEventManager eventMgr;
    private CacheStartupManager startupMgr;
    private CacheConfig config;
    private Logger logger;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(CacheRegistry.class).desiredAssertionStatus();
    }
}
