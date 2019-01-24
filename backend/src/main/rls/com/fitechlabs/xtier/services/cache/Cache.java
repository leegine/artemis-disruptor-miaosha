// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.cache;

import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.cache:
//            CacheException, CacheLoader, CacheEntry, CacheKeyAttrs, 
//            CacheTx, CacheStats, CacheKeyAttrsResolver, CacheSpooler, 
//            CacheStore, CacheExpirationPolicy, CacheTopology, CacheListener, 
//            CacheTxListener

public interface Cache
{

    public abstract String getName();

    public abstract int getCacheType();

    public abstract Object get(Object obj)
        throws CacheException;

    public abstract Object get(Object obj, Object obj1)
        throws CacheException;

    public abstract Object get(Object obj, Object obj1, CacheLoader cacheloader)
        throws CacheException;

    public abstract Map getAll(Collection collection)
        throws CacheException;

    public abstract Map getAll(Collection collection, Object obj)
        throws CacheException;

    public abstract Map getAll(Collection collection, Object obj, CacheLoader cacheloader)
        throws CacheException;

    public abstract Object load(Object obj, Object obj1)
        throws CacheException;

    public abstract Object load(Object obj, Object obj1, Object obj2)
        throws CacheException;

    public abstract void loadAll(Map map)
        throws CacheException;

    public abstract void loadAll(Map map, Object obj)
        throws CacheException;

    public abstract Object put(Object obj, Object obj1)
        throws CacheException;

    public abstract Object put(Object obj, Object obj1, Object obj2)
        throws CacheException;

    public abstract void putAll(Map map)
        throws CacheException;

    public abstract void putAll(Map map, Object obj)
        throws CacheException;

    public abstract Object remove(Object obj)
        throws CacheException;

    public abstract Object remove(Object obj, Object obj1)
        throws CacheException;

    public abstract void removeAll(Collection collection)
        throws CacheException;

    public abstract void removeAll(Collection collection, Object obj)
        throws CacheException;

    public abstract void invalidate(long l)
        throws CacheException;

    public abstract void invalidate(long l, Object obj)
        throws CacheException;

    public abstract CacheEntry peek(Object obj)
        throws CacheException;

    public abstract CacheEntry peek(Object obj, Object obj1)
        throws CacheException;

    public abstract boolean expire(Object obj, CacheKeyAttrs cachekeyattrs);

    public abstract int size();

    public abstract boolean isEmpty();

    public abstract boolean containsKey(Object obj)
        throws CacheException;

    public abstract boolean containsKey(Object obj, Object obj1)
        throws CacheException;

    public abstract boolean containsValue(Object obj);

    public abstract void clear();

    public abstract Set keySet();

    public abstract Collection values();

    public abstract Set entrySet();

    public abstract CacheTx startTx()
        throws CacheException;

    public abstract CacheTx startTx(int i)
        throws CacheException;

    public abstract CacheTx getTx();

    public abstract CacheStats getStats();

    public abstract CacheKeyAttrsResolver getKeyAttrsResolver();

    public abstract CacheSpooler getSpooler();

    public abstract CacheLoader getLoader();

    public abstract CacheStore getStore();

    public abstract CacheExpirationPolicy getExpirationPolicy();

    public abstract CacheTopology getTopology();

    public abstract boolean addEventListener(CacheListener cachelistener);

    public abstract List getAllEventListeners();

    public abstract boolean removeEventListener(CacheListener cachelistener);

    public abstract boolean addTxListener(CacheTxListener cachetxlistener);

    public abstract List getAllTxListeners();

    public abstract boolean removeTxListener(CacheTxListener cachetxlistener);

    public static final String DFLT_CACHE_NAME = "xtier-dflt-cache";
    public static final int APP = 1;
    public static final int DB = 2;
}
