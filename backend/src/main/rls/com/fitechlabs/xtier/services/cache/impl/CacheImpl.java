// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.cache.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.cache.*;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.Utils;
import com.fitechlabs.xtier.utils.concurrent.RWLock;
import com.fitechlabs.xtier.utils.concurrent.ReentrantWritePrefRWLock;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.cache.impl:
//            CacheTxImpl, CacheRegistry, CacheTxManager, CacheEventManager, 
//            CacheConfig, CacheDataManager

public class CacheImpl
    implements Cache
{

    CacheImpl(CacheRegistry cacheregistry)
    {
        isStopped = false;
        rwLock = new ReentrantWritePrefRWLock();
        registry = cacheregistry;
    }

    CacheRegistry getRegistry()
    {
        return registry;
    }

    void start()
        throws CacheException
    {
        dataMgr = registry.getDataMgr();
        rwLock.acquireWrite();
        registry.start();
        isStopped = false;
        rwLock.releaseWrite();
        break MISSING_BLOCK_LABEL_56;
        Exception exception;
        exception;
        rwLock.releaseWrite();
        throw exception;
    }

    void stop()
        throws CacheException
    {
        rwLock.acquireWrite();
        isStopped = true;
        registry.stop();
        rwLock.releaseWrite();
        break MISSING_BLOCK_LABEL_45;
        Exception exception;
        exception;
        rwLock.releaseWrite();
        throw exception;
    }

    void onTopologyEvent()
    {
        final Set txSet = registry.getTxMgr().getLocalTxSnapshot();
        CacheTxListener cachetxlistener = new CacheTxListener() {

            public void onTxEvent(int i, Cache cache, CacheTx cachetx)
            {
                if(i == 3)
                    synchronized(txSet)
                    {
                        txSet.remove(cachetx);
                        if(txSet.isEmpty())
                            txSet.notifyAll();
                    }
            }

            
            {
                super();
            }
        }
;
        registry.getEventMgr().subscribe(cachetxlistener);
        synchronized(txSet)
        {
            Iterator iterator = txSet.iterator();
            do
            {
                if(!iterator.hasNext())
                    break;
                CacheTxImpl cachetximpl = (CacheTxImpl)iterator.next();
                if(cachetximpl.getState() == 12 || cachetximpl.getState() == 14)
                    iterator.remove();
            } while(true);
            for(; !txSet.isEmpty(); Utils.waitOn(txSet));
        }
        registry.getEventMgr().unsubscribe(cachetxlistener);
    }

    public String getName()
    {
        rwLock.acquireRead();
        String s = registry.getConfig().getCacheName();
        rwLock.releaseRead();
        return s;
        Exception exception;
        exception;
        rwLock.releaseRead();
        throw exception;
    }

    public int getCacheType()
    {
        rwLock.acquireRead();
        int i = registry.getConfig().getCacheType();
        rwLock.releaseRead();
        return i;
        Exception exception;
        exception;
        rwLock.releaseRead();
        throw exception;
    }

    private void checkState()
    {
        if(isStopped)
            throw new IllegalStateException(L10n.format("SRVC.CACHE.ERR31", registry.getConfig().getCacheName()));
        else
            return;
    }

    public Object get(Object obj)
        throws CacheException
    {
        return get(obj, null, null);
    }

    public Object get(Object obj, Object obj1)
        throws CacheException
    {
        return get(obj, obj1, null);
    }

    public Object get(Object obj, Object obj1, CacheLoader cacheloader)
        throws CacheException
    {
        ArgAssert.nullArg(obj, "key");
        rwLock.acquireRead();
        Object obj2;
        checkState();
        obj2 = dataMgr.get(obj, obj1, cacheloader);
        rwLock.releaseRead();
        return obj2;
        Exception exception;
        exception;
        rwLock.releaseRead();
        throw exception;
    }

    public Map getAll(Collection collection)
        throws CacheException
    {
        return getAll(collection, null, null);
    }

    public Map getAll(Collection collection, Object obj)
        throws CacheException
    {
        return getAll(collection, obj, null);
    }

    public Map getAll(Collection collection, Object obj, CacheLoader cacheloader)
        throws CacheException
    {
        ArgAssert.nullArg(collection, "keys");
        rwLock.acquireRead();
        Map map;
        checkState();
        map = dataMgr.getAll(collection, obj, cacheloader);
        rwLock.releaseRead();
        return map;
        Exception exception;
        exception;
        rwLock.releaseRead();
        throw exception;
    }

    public Object put(Object obj, Object obj1)
        throws CacheException
    {
        return put(obj, obj1, null);
    }

    public Object put(Object obj, Object obj1, Object obj2)
        throws CacheException
    {
        ArgAssert.nullArg(obj, "key");
        rwLock.acquireRead();
        Object obj3;
        checkState();
        obj3 = dataMgr.put(obj, obj1, obj2);
        rwLock.releaseRead();
        return obj3;
        Exception exception;
        exception;
        rwLock.releaseRead();
        throw exception;
    }

    public void putAll(Map map)
        throws CacheException
    {
        putAll(map, null);
    }

    public void putAll(Map map, Object obj)
        throws CacheException
    {
        ArgAssert.nullArg(map, "map");
        rwLock.acquireRead();
        checkState();
        dataMgr.putAll(map, obj);
        rwLock.releaseRead();
        break MISSING_BLOCK_LABEL_52;
        Exception exception;
        exception;
        rwLock.releaseRead();
        throw exception;
    }

    public Object load(Object obj, Object obj1)
        throws CacheException
    {
        return load(obj, obj1, null);
    }

    public Object load(Object obj, Object obj1, Object obj2)
        throws CacheException
    {
        ArgAssert.nullArg(obj, "key");
        rwLock.acquireRead();
        Object obj3;
        checkState();
        obj3 = dataMgr.load(obj, obj1, obj2);
        rwLock.releaseRead();
        return obj3;
        Exception exception;
        exception;
        rwLock.releaseRead();
        throw exception;
    }

    public void loadAll(Map map)
        throws CacheException
    {
        loadAll(map, null);
    }

    public void loadAll(Map map, Object obj)
        throws CacheException
    {
        ArgAssert.nullArg(map, "map");
        rwLock.acquireRead();
        checkState();
        dataMgr.loadAll(map, obj);
        rwLock.releaseRead();
        break MISSING_BLOCK_LABEL_52;
        Exception exception;
        exception;
        rwLock.releaseRead();
        throw exception;
    }

    public Object remove(Object obj)
        throws CacheException
    {
        return remove(obj, null);
    }

    public Object remove(Object obj, Object obj1)
        throws CacheException
    {
        ArgAssert.nullArg(obj, "key");
        rwLock.acquireRead();
        Object obj2;
        checkState();
        obj2 = dataMgr.remove(obj, obj1);
        rwLock.releaseRead();
        return obj2;
        Exception exception;
        exception;
        rwLock.releaseRead();
        throw exception;
    }

    public void removeAll(Collection collection)
        throws CacheException
    {
        removeAll(collection, null);
    }

    public void removeAll(Collection collection, Object obj)
        throws CacheException
    {
        ArgAssert.nullArg(collection, "keys");
        rwLock.acquireRead();
        checkState();
        dataMgr.removeAll(collection, obj);
        rwLock.releaseRead();
        break MISSING_BLOCK_LABEL_52;
        Exception exception;
        exception;
        rwLock.releaseRead();
        throw exception;
    }

    public void invalidate(long l)
        throws CacheException
    {
        invalidate(l, null);
    }

    public void invalidate(long l, Object obj)
        throws CacheException
    {
        rwLock.acquireRead();
        checkState();
        dataMgr.invalidate(new Long(l), obj);
        rwLock.releaseRead();
        break MISSING_BLOCK_LABEL_55;
        Exception exception;
        exception;
        rwLock.releaseRead();
        throw exception;
    }

    public CacheEntry peek(Object obj)
        throws CacheException
    {
        return peek(obj, null);
    }

    public CacheEntry peek(Object obj, Object obj1)
        throws CacheException
    {
        ArgAssert.nullArg(obj, "key");
        rwLock.acquireRead();
        CacheEntry cacheentry;
        checkState();
        cacheentry = dataMgr.peek(obj, obj1);
        rwLock.releaseRead();
        return cacheentry;
        Exception exception;
        exception;
        rwLock.releaseRead();
        throw exception;
    }

    public boolean expire(Object obj, CacheKeyAttrs cachekeyattrs)
    {
        ArgAssert.nullArg(obj, "key");
        ArgAssert.nullArg(cachekeyattrs, "keyAttrs");
        rwLock.acquireRead();
        boolean flag;
        checkState();
        flag = dataMgr.expire(obj, cachekeyattrs);
        rwLock.releaseRead();
        return flag;
        Exception exception;
        exception;
        rwLock.releaseRead();
        throw exception;
    }

    public boolean containsKey(Object obj)
        throws CacheException
    {
        return containsKey(obj, null);
    }

    public boolean containsKey(Object obj, Object obj1)
        throws CacheException
    {
        ArgAssert.nullArg(obj, "key");
        rwLock.acquireRead();
        boolean flag;
        checkState();
        flag = dataMgr.containsKey(obj, obj1);
        rwLock.releaseRead();
        return flag;
        Exception exception;
        exception;
        rwLock.releaseRead();
        throw exception;
    }

    public boolean containsValue(Object obj)
    {
        ArgAssert.nullArg(obj, "value");
        rwLock.acquireRead();
        boolean flag;
        checkState();
        flag = dataMgr.containsValue(obj);
        rwLock.releaseRead();
        return flag;
        Exception exception;
        exception;
        rwLock.releaseRead();
        throw exception;
    }

    public int size()
    {
        rwLock.acquireRead();
        int i;
        checkState();
        i = dataMgr.size();
        rwLock.releaseRead();
        return i;
        Exception exception;
        exception;
        rwLock.releaseRead();
        throw exception;
    }

    public boolean isEmpty()
    {
        rwLock.acquireRead();
        boolean flag;
        checkState();
        flag = dataMgr.isEmpty();
        rwLock.releaseRead();
        return flag;
        Exception exception;
        exception;
        rwLock.releaseRead();
        throw exception;
    }

    public void clear()
    {
        rwLock.acquireRead();
        checkState();
        dataMgr.clear();
        rwLock.releaseRead();
        break MISSING_BLOCK_LABEL_44;
        Exception exception;
        exception;
        rwLock.releaseRead();
        throw exception;
    }

    public Set keySet()
    {
        rwLock.acquireRead();
        Set set;
        checkState();
        set = dataMgr.keySet();
        rwLock.releaseRead();
        return set;
        Exception exception;
        exception;
        rwLock.releaseRead();
        throw exception;
    }

    public Collection values()
    {
        rwLock.acquireRead();
        Collection collection;
        checkState();
        collection = dataMgr.values();
        rwLock.releaseRead();
        return collection;
        Exception exception;
        exception;
        rwLock.releaseRead();
        throw exception;
    }

    public Set entrySet()
    {
        rwLock.acquireRead();
        Set set;
        checkState();
        set = dataMgr.entrySet();
        rwLock.releaseRead();
        return set;
        Exception exception;
        exception;
        rwLock.releaseRead();
        throw exception;
    }

    public CacheTx startTx()
        throws CacheException
    {
        return startTx(1);
    }

    public CacheTx startTx(int i)
        throws CacheException
    {
        rwLock.acquireRead();
        CacheTxImpl cachetximpl;
        checkState();
        cachetximpl = registry.getTxMgr().startLocalTx(i);
        rwLock.releaseRead();
        return cachetximpl;
        Exception exception;
        exception;
        rwLock.releaseRead();
        throw exception;
    }

    public CacheTx getTx()
    {
        rwLock.acquireRead();
        CacheTxImpl cachetximpl;
        checkState();
        cachetximpl = registry.getTxMgr().getLocalTx();
        rwLock.releaseRead();
        return cachetximpl;
        Exception exception;
        exception;
        rwLock.releaseRead();
        throw exception;
    }

    public CacheStats getStats()
    {
        rwLock.acquireRead();
        CacheStats cachestats;
        checkState();
        cachestats = dataMgr.getStats();
        rwLock.releaseRead();
        return cachestats;
        Exception exception;
        exception;
        rwLock.releaseRead();
        throw exception;
    }

    public CacheKeyAttrsResolver getKeyAttrsResolver()
    {
        rwLock.acquireRead();
        CacheKeyAttrsResolver cachekeyattrsresolver;
        checkState();
        cachekeyattrsresolver = registry.getConfig().getAttrsResolver();
        rwLock.releaseRead();
        return cachekeyattrsresolver;
        Exception exception;
        exception;
        rwLock.releaseRead();
        throw exception;
    }

    public CacheSpooler getSpooler()
    {
        rwLock.acquireRead();
        CacheSpooler cachespooler;
        checkState();
        cachespooler = registry.getConfig().getSpooler();
        rwLock.releaseRead();
        return cachespooler;
        Exception exception;
        exception;
        rwLock.releaseRead();
        throw exception;
    }

    public CacheLoader getLoader()
    {
        rwLock.acquireRead();
        CacheLoader cacheloader;
        checkState();
        cacheloader = registry.getConfig().getLoader();
        rwLock.releaseRead();
        return cacheloader;
        Exception exception;
        exception;
        rwLock.releaseRead();
        throw exception;
    }

    public CacheStore getStore()
    {
        rwLock.acquireRead();
        CacheStore cachestore;
        checkState();
        cachestore = registry.getConfig().getStore();
        rwLock.releaseRead();
        return cachestore;
        Exception exception;
        exception;
        rwLock.releaseRead();
        throw exception;
    }

    public CacheExpirationPolicy getExpirationPolicy()
    {
        rwLock.acquireRead();
        CacheExpirationPolicy cacheexpirationpolicy;
        checkState();
        cacheexpirationpolicy = registry.getConfig().getExpPolicy();
        rwLock.releaseRead();
        return cacheexpirationpolicy;
        Exception exception;
        exception;
        rwLock.releaseRead();
        throw exception;
    }

    public CacheTopology getTopology()
    {
        rwLock.acquireRead();
        CacheTopology cachetopology;
        checkState();
        cachetopology = registry.getConfig().getTopology();
        rwLock.releaseRead();
        return cachetopology;
        Exception exception;
        exception;
        rwLock.releaseRead();
        throw exception;
    }

    public boolean addEventListener(CacheListener cachelistener)
    {
        ArgAssert.nullArg(cachelistener, "listener");
        rwLock.acquireRead();
        boolean flag;
        checkState();
        flag = registry.getEventMgr().subscribe(cachelistener);
        rwLock.releaseRead();
        return flag;
        Exception exception;
        exception;
        rwLock.releaseRead();
        throw exception;
    }

    public List getAllEventListeners()
    {
        rwLock.acquireRead();
        List list;
        checkState();
        list = registry.getEventMgr().getAllListeners();
        rwLock.releaseRead();
        return list;
        Exception exception;
        exception;
        rwLock.releaseRead();
        throw exception;
    }

    public boolean removeEventListener(CacheListener cachelistener)
    {
        ArgAssert.nullArg(cachelistener, "listener");
        rwLock.acquireRead();
        boolean flag;
        checkState();
        flag = registry.getEventMgr().unsubscribe(cachelistener);
        rwLock.releaseRead();
        return flag;
        Exception exception;
        exception;
        rwLock.releaseRead();
        throw exception;
    }

    public boolean addTxListener(CacheTxListener cachetxlistener)
    {
        ArgAssert.nullArg(cachetxlistener, "listener");
        rwLock.acquireRead();
        boolean flag;
        checkState();
        flag = registry.getEventMgr().subscribe(cachetxlistener);
        rwLock.releaseRead();
        return flag;
        Exception exception;
        exception;
        rwLock.releaseRead();
        throw exception;
    }

    public List getAllTxListeners()
    {
        rwLock.acquireRead();
        List list;
        checkState();
        list = registry.getEventMgr().getAllTxListeners();
        rwLock.releaseRead();
        return list;
        Exception exception;
        exception;
        rwLock.releaseRead();
        throw exception;
    }

    public boolean removeTxListener(CacheTxListener cachetxlistener)
    {
        ArgAssert.nullArg(cachetxlistener, "listener");
        rwLock.acquireRead();
        boolean flag;
        checkState();
        flag = registry.getEventMgr().unsubscribe(cachetxlistener);
        rwLock.releaseRead();
        return flag;
        Exception exception;
        exception;
        rwLock.releaseRead();
        throw exception;
    }

    public String toString()
    {
        return L10n.format("SRVC.CACHE.TXT7", getName(), getStats());
    }

    private CacheRegistry registry;
    private CacheDataManager dataMgr;
    private boolean isStopped;
    private RWLock rwLock;
}
