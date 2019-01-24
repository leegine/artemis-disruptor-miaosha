// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.cache.jcache;

import com.fitechlabs.xtier.services.cache.*;
import java.util.*;

public interface JCache
    extends Map
{

    public abstract String getName();

    public abstract int getCacheType();

    public abstract Object get(Object obj);

    public abstract Object get(Object obj, Object obj1);

    public abstract Object get(Object obj, Object obj1, CacheLoader cacheloader);

    public abstract Map getAll(Collection collection);

    public abstract Map getAll(Collection collection, Object obj);

    public abstract Map getAll(Collection collection, Object obj, CacheLoader cacheloader);

    public abstract Object load(Object obj, Object obj1);

    public abstract Object load(Object obj, Object obj1, Object obj2);

    public abstract void loadAll(Map map);

    public abstract void loadAll(Map map, Object obj);

    public abstract Object put(Object obj, Object obj1);

    public abstract Object put(Object obj, Object obj1, Object obj2);

    public abstract void putAll(Map map);

    public abstract void putAll(Map map, Object obj);

    public abstract Object remove(Object obj);

    public abstract Object remove(Object obj, Object obj1);

    public abstract void removeAll(Collection collection);

    public abstract void removeAll(Collection collection, Object obj);

    public abstract void invalidate(long l);

    public abstract void invalidate(long l, Object obj);

    public abstract CacheEntry peek(Object obj);

    public abstract CacheEntry peek(Object obj, Object obj1);

    public abstract boolean expire(Object obj, CacheKeyAttrs cachekeyattrs);

    public abstract int size();

    public abstract boolean isEmpty();

    public abstract boolean containsKey(Object obj);

    public abstract boolean containsKey(Object obj, Object obj1);

    public abstract boolean containsValue(Object obj);

    public abstract void clear();

    public abstract Set keySet();

    public abstract Collection values();

    public abstract Set entrySet();

    public abstract CacheTx startTx();

    public abstract CacheTx startTx(int i);

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
