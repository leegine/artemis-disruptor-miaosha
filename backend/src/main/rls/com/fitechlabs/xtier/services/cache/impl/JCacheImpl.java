// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.cache.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.cache.*;
import com.fitechlabs.xtier.services.cache.jcache.JCache;
import com.fitechlabs.xtier.services.cache.jcache.JCacheException;
import java.util.*;

class JCacheImpl
    implements JCache
{

    JCacheImpl(Cache cache1)
    {
        if(!$assertionsDisabled && cache1 == null)
        {
            throw new AssertionError();
        } else
        {
            cache = cache1;
            return;
        }
    }

    public String getName()
    {
        return cache.getName();
    }

    public int getCacheType()
    {
        return cache.getCacheType();
    }

    public void clear()
    {
        cache.clear();
    }

    public boolean containsKey(Object obj)
    {
        try
        {
            return cache.containsKey(obj);
        }
        catch(CacheException cacheexception)
        {
            throw new JCacheException(L10n.format("SRVC.CACHE.ERR1"), cacheexception);
        }
    }

    public boolean containsKey(Object obj, Object obj1)
    {
        try
        {
            return cache.containsKey(obj, obj1);
        }
        catch(CacheException cacheexception)
        {
            throw new JCacheException(L10n.format("SRVC.CACHE.ERR1"), cacheexception);
        }
    }

    public boolean containsValue(Object obj)
    {
        return cache.containsValue(obj);
    }

    public Set entrySet()
    {
        return cache.entrySet();
    }

    public boolean expire(Object obj, CacheKeyAttrs cachekeyattrs)
    {
        return cache.expire(obj, cachekeyattrs);
    }

    public Object get(Object obj)
    {
        try
        {
            return cache.get(obj);
        }
        catch(CacheException cacheexception)
        {
            throw new JCacheException(L10n.format("SRVC.CACHE.ERR1"), cacheexception);
        }
    }

    public Object get(Object obj, Object obj1)
    {
        try
        {
            return cache.get(obj, obj1);
        }
        catch(CacheException cacheexception)
        {
            throw new JCacheException(L10n.format("SRVC.CACHE.ERR1"), cacheexception);
        }
    }

    public Object get(Object obj, Object obj1, CacheLoader cacheloader)
    {
        try
        {
            return cache.get(obj, obj1, cacheloader);
        }
        catch(CacheException cacheexception)
        {
            throw new JCacheException(L10n.format("SRVC.CACHE.ERR1"), cacheexception);
        }
    }

    public Map getAll(Collection collection)
    {
        try
        {
            return cache.getAll(collection);
        }
        catch(CacheException cacheexception)
        {
            throw new JCacheException(L10n.format("SRVC.CACHE.ERR1"), cacheexception);
        }
    }

    public Map getAll(Collection collection, Object obj)
    {
        try
        {
            return cache.getAll(collection, obj);
        }
        catch(CacheException cacheexception)
        {
            throw new JCacheException(L10n.format("SRVC.CACHE.ERR1"), cacheexception);
        }
    }

    public Map getAll(Collection collection, Object obj, CacheLoader cacheloader)
    {
        try
        {
            return cache.getAll(collection, obj, cacheloader);
        }
        catch(CacheException cacheexception)
        {
            throw new JCacheException(L10n.format("SRVC.CACHE.ERR1"), cacheexception);
        }
    }

    public void invalidate(long l)
    {
        try
        {
            cache.invalidate(l);
        }
        catch(CacheException cacheexception)
        {
            throw new JCacheException(L10n.format("SRVC.CACHE.ERR1"), cacheexception);
        }
    }

    public void invalidate(long l, Object obj)
    {
        try
        {
            cache.invalidate(l, obj);
        }
        catch(CacheException cacheexception)
        {
            throw new JCacheException(L10n.format("SRVC.CACHE.ERR1"), cacheexception);
        }
    }

    public boolean isEmpty()
    {
        return cache.isEmpty();
    }

    public Set keySet()
    {
        return cache.keySet();
    }

    public Object load(Object obj, Object obj1)
    {
        try
        {
            return cache.load(obj, obj1);
        }
        catch(CacheException cacheexception)
        {
            throw new JCacheException(L10n.format("SRVC.CACHE.ERR1"), cacheexception);
        }
    }

    public Object load(Object obj, Object obj1, Object obj2)
    {
        try
        {
            return cache.load(obj, obj1, obj2);
        }
        catch(CacheException cacheexception)
        {
            throw new JCacheException(L10n.format("SRVC.CACHE.ERR1"), cacheexception);
        }
    }

    public void loadAll(Map map)
    {
        try
        {
            cache.loadAll(map);
        }
        catch(CacheException cacheexception)
        {
            throw new JCacheException(L10n.format("SRVC.CACHE.ERR1"), cacheexception);
        }
    }

    public void loadAll(Map map, Object obj)
    {
        try
        {
            cache.loadAll(map, obj);
        }
        catch(CacheException cacheexception)
        {
            throw new JCacheException(L10n.format("SRVC.CACHE.ERR1"), cacheexception);
        }
    }

    public CacheEntry peek(Object obj)
    {
        try
        {
            return cache.peek(obj);
        }
        catch(CacheException cacheexception)
        {
            throw new JCacheException(L10n.format("SRVC.CACHE.ERR1"), cacheexception);
        }
    }

    public CacheEntry peek(Object obj, Object obj1)
    {
        try
        {
            return cache.peek(obj, obj1);
        }
        catch(CacheException cacheexception)
        {
            throw new JCacheException(L10n.format("SRVC.CACHE.ERR1"), cacheexception);
        }
    }

    public Object put(Object obj, Object obj1)
    {
        try
        {
            return cache.put(obj, obj1);
        }
        catch(CacheException cacheexception)
        {
            throw new JCacheException(L10n.format("SRVC.CACHE.ERR1"), cacheexception);
        }
    }

    public Object put(Object obj, Object obj1, Object obj2)
    {
        try
        {
            return cache.put(obj, obj1, obj2);
        }
        catch(CacheException cacheexception)
        {
            throw new JCacheException(L10n.format("SRVC.CACHE.ERR1"), cacheexception);
        }
    }

    public void putAll(Map map)
    {
        try
        {
            cache.putAll(map);
        }
        catch(CacheException cacheexception)
        {
            throw new JCacheException(L10n.format("SRVC.CACHE.ERR1"), cacheexception);
        }
    }

    public void putAll(Map map, Object obj)
    {
        try
        {
            cache.putAll(map, obj);
        }
        catch(CacheException cacheexception)
        {
            throw new JCacheException(L10n.format("SRVC.CACHE.ERR1"), cacheexception);
        }
    }

    public Object remove(Object obj)
    {
        try
        {
            return cache.remove(obj);
        }
        catch(CacheException cacheexception)
        {
            throw new JCacheException(L10n.format("SRVC.CACHE.ERR1"), cacheexception);
        }
    }

    public Object remove(Object obj, Object obj1)
    {
        try
        {
            return cache.remove(obj, obj1);
        }
        catch(CacheException cacheexception)
        {
            throw new JCacheException(L10n.format("SRVC.CACHE.ERR1"), cacheexception);
        }
    }

    public void removeAll(Collection collection)
    {
        try
        {
            cache.removeAll(collection);
        }
        catch(CacheException cacheexception)
        {
            throw new JCacheException(L10n.format("SRVC.CACHE.ERR1"), cacheexception);
        }
    }

    public void removeAll(Collection collection, Object obj)
    {
        try
        {
            cache.removeAll(collection, obj);
        }
        catch(CacheException cacheexception)
        {
            throw new JCacheException(L10n.format("SRVC.CACHE.ERR1"), cacheexception);
        }
    }

    public boolean removeEventListener(CacheListener cachelistener)
    {
        return cache.removeEventListener(cachelistener);
    }

    public boolean removeTxListener(CacheTxListener cachetxlistener)
    {
        return cache.removeTxListener(cachetxlistener);
    }

    public int size()
    {
        return cache.size();
    }

    public CacheTx getTx()
    {
        return cache.getTx();
    }

    public CacheTx startTx()
    {
        try
        {
            return cache.startTx();
        }
        catch(CacheException cacheexception)
        {
            throw new JCacheException(L10n.format("SRVC.CACHE.ERR1"), cacheexception);
        }
    }

    public CacheTx startTx(int i)
    {
        try
        {
            return cache.startTx(i);
        }
        catch(CacheException cacheexception)
        {
            throw new JCacheException(L10n.format("SRVC.CACHE.ERR1"), cacheexception);
        }
    }

    public Collection values()
    {
        return cache.values();
    }

    public boolean addEventListener(CacheListener cachelistener)
    {
        return cache.addEventListener(cachelistener);
    }

    public boolean addTxListener(CacheTxListener cachetxlistener)
    {
        return cache.addTxListener(cachetxlistener);
    }

    public List getAllEventListeners()
    {
        return cache.getAllEventListeners();
    }

    public List getAllTxListeners()
    {
        return cache.getAllTxListeners();
    }

    public CacheExpirationPolicy getExpirationPolicy()
    {
        return cache.getExpirationPolicy();
    }

    public CacheKeyAttrsResolver getKeyAttrsResolver()
    {
        return cache.getKeyAttrsResolver();
    }

    public CacheLoader getLoader()
    {
        return cache.getLoader();
    }

    public CacheSpooler getSpooler()
    {
        return cache.getSpooler();
    }

    public CacheStats getStats()
    {
        return cache.getStats();
    }

    public CacheStore getStore()
    {
        return cache.getStore();
    }

    public CacheTopology getTopology()
    {
        return cache.getTopology();
    }

    public String toString()
    {
        return L10n.format("SRVC.CACHE.TXT8", getName(), getStats());
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

    private Cache cache;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(JCacheImpl.class).desiredAssertionStatus();
    }
}
