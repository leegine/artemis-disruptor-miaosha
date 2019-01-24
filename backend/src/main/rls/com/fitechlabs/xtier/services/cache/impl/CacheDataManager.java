// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.cache.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.cache.*;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.utils.Utils;
import com.fitechlabs.xtier.utils.boxed.sync.BoxedInt32Sync;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.cache.impl:
//            CacheStatsImpl, CacheMap, CacheBucket, CacheEntryImpl,
//            CacheGroup, CacheRegistry, CacheConfig, CacheTxManager,
//            CacheTxImpl, CacheTxEntry, CacheEventManager, CacheUtils,
//            CacheObject

public class CacheDataManager
{

    protected CacheDataManager(CacheRegistry cacheregistry)
    {
        grps = new HashMap();
        stats = new CacheStatsImpl();
        size = new BoxedInt32Sync(0);
        if(!$assertionsDisabled && cacheregistry == null)
        {
            throw new AssertionError();
        } else
        {
            registry = cacheregistry;
            cacheMap = new CacheMap(Utils.getNonRehashCapacity(cacheregistry.getConfig().getInitSize()));
            return;
        }
    }

    public void start()
    {
    }

    public void stop()
    {
    }

    protected CacheStats getStats()
    {
        return stats;
    }

    protected Object get(Object obj, Object obj1, CacheLoader cacheloader)
        throws CacheException
    {
        CacheKeyAttrs cachekeyattrs;
        CacheTxImpl cachetximpl;
        CacheTxEntry cachetxentry;
        boolean flag;
        if(cacheloader == null)
            cacheloader = registry.getConfig().getLoader();
        cachekeyattrs = resolveAttrs(obj, obj1);
        cachetximpl = registry.getTxMgr().getLocalTx();
        cachetxentry = null;
        if(cachetximpl != null)
        {
            if(cachetximpl.getIsolationLevel() == 1)
                cachetximpl.checkReadState();
            cachetxentry = cachetximpl.getTxEntry(obj, cachekeyattrs);
            if(cachetxentry != null && cachetxentry.getTxValue() != null)
            {
                onHit(cachetxentry.getEntry());
                return cachetxentry.getTxValue();
            }
        }
        flag = false;
_L1:
        CacheEntryImpl cacheentryimpl = getOrCreateEntry(obj, cachekeyattrs);
        Object obj2 = cacheentryimpl.getMutex();
        JVM INSTR monitorenter ;
        if(!cacheentryimpl.isRemoved()) goto _L2; else goto _L1
_L2:
        Object obj4;
        Object obj3 = cacheentryimpl.getValue();
        if(cachetxentry != null)
        {
            onMiss(cacheentryimpl);
            obj3 = cacheloader != null ? cacheloader.load(obj, obj1) : null;
            cachetximpl.add(cacheentryimpl, obj3, (byte)1, obj1);
        } else
        if(obj3 != null)
        {
            onHit(cacheentryimpl);
            if(cachetximpl != null && cachetximpl.getIsolationLevel() == 2)
            {
                cachetximpl.add(cacheentryimpl, obj3, (byte)1, obj1);
            } else
            {
                registry.getEventMgr().addEvent(1, cacheentryimpl);
                flag = true;
            }
        } else
        {
            onMiss(cacheentryimpl);
            obj3 = spoolLoad(cacheentryimpl);
            if(obj3 == null && cacheloader != null)
                obj3 = cacheloader.load(obj, obj1);
            if(cachetximpl != null && cachetximpl.getIsolationLevel() == 2)
            {
                cachetximpl.add(cacheentryimpl, obj3, (byte)1, obj1);
            } else
            {
                if(obj3 != null)
                    cacheentryimpl.setVal(obj3);
                registry.getEventMgr().addEvent(1, cacheentryimpl);
                flag = true;
            }
        }
        obj4 = obj3;
        obj2;
        JVM INSTR monitorexit ;
        return obj4;
        Exception exception;
        exception;
        throw exception;
        if(flag)
            registry.getEventMgr().dispatch();
        JVM INSTR ret 14;
    }

    protected Map getAll(Collection collection, Object obj, CacheLoader cacheloader)
        throws CacheException
    {
        CacheTxImpl cachetximpl;
        HashMap hashmap;
        HashMap hashmap1;
        HashMap hashmap2;
        boolean flag;
        Iterator iterator;
        if(!$assertionsDisabled && collection == null)
            throw new AssertionError();
        cachetximpl = registry.getTxMgr().getLocalTx();
        if(cachetximpl != null && cachetximpl.getIsolationLevel() == 1)
            cachetximpl.checkReadState();
        hashmap = new HashMap(Utils.getNonRehashCapacity(collection.size()));
        hashmap1 = null;
        hashmap2 = null;
        flag = false;
        iterator = collection.iterator();
_L4:
        Object obj1;
        CacheKeyAttrs cachekeyattrs;
        CacheTxEntry cachetxentry;
        do
        {
label0:
            {
                if(!iterator.hasNext())
                    break MISSING_BLOCK_LABEL_496;
                obj1 = iterator.next();
                cachekeyattrs = resolveAttrs(obj1, obj);
                cachetxentry = null;
                if(cachetximpl == null)
                    break label0;
                cachetxentry = cachetximpl.getTxEntry(obj1, cachekeyattrs);
                if(cachetxentry == null || cachetxentry.getTxValue() == null)
                    break label0;
                hashmap.put(obj1, cachetxentry.getTxValue());
                onHit(cachetxentry.getEntry());
            }
        } while(true);
_L1:
        CacheEntryImpl cacheentryimpl = getOrCreateEntry(obj1, cachekeyattrs);
        Object obj2 = cacheentryimpl.getMutex();
        JVM INSTR monitorenter ;
        if(!cacheentryimpl.isRemoved()) goto _L2; else goto _L1
_L2:
        Object obj3 = cacheentryimpl.getValue();
        if(cachetxentry != null)
        {
            onMiss(cacheentryimpl);
            if(hashmap1 == null)
            {
                int i = Utils.getNonRehashCapacity(collection.size() - hashmap.size());
                hashmap1 = new HashMap(i);
                hashmap2 = new HashMap(i);
            }
            hashmap1.put(cacheentryimpl.getKey(), cacheentryimpl.getKeyAttrs());
            hashmap2.put(cacheentryimpl.getKey(), new Long(cacheentryimpl.getModCount()));
        } else
        if(obj3 != null)
        {
            hashmap.put(obj1, obj3);
            onHit(cacheentryimpl);
            if(cachetximpl != null && cachetximpl.getIsolationLevel() == 2)
            {
                cachetximpl.add(cacheentryimpl, obj3, (byte)1, obj);
            } else
            {
                registry.getEventMgr().addEvent(1, cacheentryimpl);
                flag = true;
            }
        } else
        {
            onMiss(cacheentryimpl);
            if(hashmap1 == null)
            {
                int j = Utils.getNonRehashCapacity(collection.size() - hashmap.size());
                hashmap1 = new HashMap(j);
                hashmap2 = new HashMap(j);
            }
            hashmap1.put(cacheentryimpl.getKey(), cacheentryimpl.getKeyAttrs());
            hashmap2.put(cacheentryimpl.getKey(), new Long(cacheentryimpl.getModCount()));
        }
        obj2;
        JVM INSTR monitorexit ;
        if(true) goto _L4; else goto _L3
_L3:
        Exception exception;
        exception;
        throw exception;
        if(flag)
            registry.getEventMgr().dispatch();
        if(registry.getConfig().getSpooler() != null && hashmap1 != null && !hashmap1.isEmpty())
            addLoaded(hashmap1, hashmap2, spoolLoad(hashmap1), hashmap, obj, cachetximpl, false);
        cacheloader = cacheloader == null ? registry.getConfig().getLoader() : cacheloader;
        if(cacheloader != null && hashmap1 != null && !hashmap1.isEmpty())
            addLoaded(hashmap1, hashmap2, cacheloader.loadAll(hashmap1.keySet(), obj), hashmap, obj, cachetximpl, true);
        if(!$assertionsDisabled && hashmap1 != null && !hashmap1.isEmpty())
            throw new AssertionError();
        else
            return hashmap;
    }

    private void addLoaded(Map map, Map map1, Map map2, Map map3, Object obj, CacheTxImpl cachetximpl, boolean flag)
        throws CacheException
    {
        boolean flag1;
        Iterator iterator;
        flag1 = false;
        iterator = map.entrySet().iterator();
_L4:
        Object obj1;
        CacheKeyAttrs cachekeyattrs;
        Object obj2;
        if(!iterator.hasNext())
            break MISSING_BLOCK_LABEL_286;
        Map.Entry entry = (Map.Entry)iterator.next();
        obj1 = entry.getKey();
        cachekeyattrs = (CacheKeyAttrs)entry.getValue();
        obj2 = map2 != null ? map2.get(obj1) : null;
        if(obj2 == null && !flag)
            continue; /* Loop/switch isn't completed */
        iterator.remove();
        map3.put(obj1, obj2);
_L2:
label0:
        {
            CacheEntryImpl cacheentryimpl = getOrCreateEntry(obj1, cachekeyattrs);
            synchronized(cacheentryimpl.getMutex())
            {
                if(cacheentryimpl.isRemoved())
                    break label0;
                if(cachetximpl != null && cachetximpl.getIsolationLevel() == 2)
                {
                    cachetximpl.add(cacheentryimpl, obj2, (byte)1, obj);
                } else
                {
                    CacheTxEntry cachetxentry = cachetximpl != null ? cachetximpl.getTxEntry(obj1, cachekeyattrs) : null;
                    Long long1 = (Long)map1.get(obj1);
                    if(obj2 != null && long1.longValue() == cacheentryimpl.getModCount())
                        if(cachetxentry != null)
                            cachetximpl.add(cacheentryimpl, obj2, (byte)1, obj);
                        else
                            cacheentryimpl.setVal(obj2);
                    if(cachetxentry == null)
                    {
                        registry.getEventMgr().addEvent(1, cacheentryimpl);
                        flag1 = true;
                    }
                }
            }
            continue; /* Loop/switch isn't completed */
        }
        obj3;
        JVM INSTR monitorexit ;
        if(true) goto _L2; else goto _L1
_L1:
        exception;
        throw exception;
        if(flag1)
            registry.getEventMgr().dispatch();
        return;
        if(true) goto _L4; else goto _L3
_L3:
    }

    protected Object put(Object obj, Object obj1, Object obj2)
        throws CacheException
    {
        CacheKeyAttrs cachekeyattrs;
        CacheTxImpl cachetximpl;
        cachekeyattrs = resolveAttrs(obj, obj2);
        cachetximpl = registry.getTxMgr().getLocalTx();
        if(cachetximpl == null)
            throw new CacheException(L10n.format("SRVC.CACHE.ERR21"));
        Object obj3 = null;
_L2:
        Object obj4;
label0:
        {
            CacheEntryImpl cacheentryimpl = getOrCreateEntry(obj, cachekeyattrs);
            synchronized(cacheentryimpl.getMutex())
            {
                if(cacheentryimpl.isRemoved())
                    break label0;
                obj4 = cachetximpl.add(cacheentryimpl, obj1, (byte)2, obj2);
                if(obj4 == null)
                    obj4 = cacheentryimpl.getValue();
            }
            break; /* Loop/switch isn't completed */
        }
        obj5;
        JVM INSTR monitorexit ;
        if(true) goto _L2; else goto _L1
        exception;
        throw exception;
_L1:
        CacheStore cachestore = registry.getConfig().getStore();
        if(cachestore != null)
            cachestore.store(obj, obj1, obj2);
        return obj4;
    }

    protected void putAll(Map map, Object obj)
        throws CacheException
    {
        CacheTxImpl cachetximpl;
        Iterator iterator;
        if(!$assertionsDisabled && map == null)
            throw new AssertionError();
        cachetximpl = registry.getTxMgr().getLocalTx();
        if(cachetximpl == null)
            throw new CacheException(L10n.format("SRVC.CACHE.ERR22"));
        iterator = map.entrySet().iterator();
_L4:
        Object obj1;
        Object obj2;
        CacheKeyAttrs cachekeyattrs;
        if(!iterator.hasNext())
            break MISSING_BLOCK_LABEL_167;
        Map.Entry entry = (Map.Entry)iterator.next();
        obj1 = entry.getKey();
        obj2 = entry.getValue();
        cachekeyattrs = resolveAttrs(obj1, obj);
_L2:
label0:
        {
            CacheEntryImpl cacheentryimpl = getOrCreateEntry(obj1, cachekeyattrs);
            synchronized(cacheentryimpl.getMutex())
            {
                if(cacheentryimpl.isRemoved())
                    break label0;
                cachetximpl.add(cacheentryimpl, obj2, (byte)2, obj);
            }
            continue; /* Loop/switch isn't completed */
        }
        obj3;
        JVM INSTR monitorexit ;
        if(true) goto _L2; else goto _L1
_L1:
        exception;
        throw exception;
        CacheStore cachestore = registry.getConfig().getStore();
        if(cachestore != null)
            cachestore.storeAll(map, obj);
        return;
        if(true) goto _L4; else goto _L3
_L3:
    }

    protected Object load(Object obj, Object obj1, Object obj2)
        throws CacheException
    {
        CacheKeyAttrs cachekeyattrs;
        CacheTxImpl cachetximpl;
        boolean flag;
        cachekeyattrs = resolveAttrs(obj, obj2);
        cachetximpl = registry.getTxMgr().getLocalTx();
        flag = false;
_L1:
label0:
        {
            CacheEntryImpl cacheentryimpl;
            Object obj5;
            Object obj6;
            Object obj7;
            try
            {
                cacheentryimpl = getOrCreateEntry(obj, cachekeyattrs);
                synchronized(cacheentryimpl.getMutex())
                {
                    if(cacheentryimpl.isRemoved())
                        break MISSING_BLOCK_LABEL_219;
                    if(cachetximpl == null || cachetximpl.getIsolationLevel() != 2)
                        break label0;
                    Object obj4 = cachetximpl.add(cacheentryimpl, obj1, (byte)1, obj2);
                    obj6 = obj4 != null ? obj4 : cacheentryimpl.getValue();
                }
            }
            finally
            {
                if(flag)
                    registry.getEventMgr().dispatch();
            }
            return obj6;
        }
        if(!$assertionsDisabled && cachetximpl != null && cachetximpl.getIsolationLevel() != 1)
            throw new AssertionError();
        obj5 = cacheentryimpl.getValue();
        obj6 = cachetximpl != null ? ((Object) (cachetximpl.getTxEntry(obj, cachekeyattrs))) : null;
        if(obj6 != null)
        {
            obj7 = cachetximpl.add(cacheentryimpl, obj1, (byte)1, obj2);
            if(obj7 != null)
                obj5 = obj7;
        } else
        {
            cacheentryimpl.setVal(obj1);
            registry.getEventMgr().addEvent(1, cacheentryimpl);
            flag = true;
        }
        obj7 = obj5;
        obj3;
        JVM INSTR monitorexit ;
        return obj7;
        obj3;
        JVM INSTR monitorexit ;
          goto _L1
        exception;
        throw exception;
    }

    protected void loadAll(Map map, Object obj)
        throws CacheException
    {
        if(!$assertionsDisabled && map == null)
            throw new AssertionError();
        Map.Entry entry;
        for(Iterator iterator = map.entrySet().iterator(); iterator.hasNext(); load(entry.getKey(), entry.getValue(), obj))
            entry = (Map.Entry)iterator.next();

    }

    protected Object remove(Object obj, Object obj1)
        throws CacheException
    {
        CacheKeyAttrs cachekeyattrs;
        CacheTxImpl cachetximpl;
        cachekeyattrs = resolveAttrs(obj, obj1);
        cachetximpl = registry.getTxMgr().getLocalTx();
        if(cachetximpl == null)
            throw new CacheException(L10n.format("SRVC.CACHE.ERR23"));
        Object obj2 = null;
        Object obj4 = null;
_L2:
        Object obj3;
label0:
        {
            CacheEntryImpl cacheentryimpl = getOrCreateEntry(obj, cachekeyattrs);
            synchronized(cacheentryimpl.getMutex())
            {
                if(cacheentryimpl.isRemoved())
                    break label0;
                obj3 = cachetximpl.add(cacheentryimpl, null, (byte)3, obj1);
                if(obj3 == null)
                    obj3 = cacheentryimpl.getValue();
            }
            break; /* Loop/switch isn't completed */
        }
        obj5;
        JVM INSTR monitorexit ;
        if(true) goto _L2; else goto _L1
        exception;
        throw exception;
_L1:
        CacheStore cachestore = registry.getConfig().getStore();
        if(cachestore != null)
            cachestore.remove(obj, obj1);
        return obj3;
    }

    protected void removeAll(Collection collection, Object obj)
        throws CacheException
    {
        CacheTxImpl cachetximpl;
        Iterator iterator;
        cachetximpl = registry.getTxMgr().getLocalTx();
        if(cachetximpl == null)
            throw new CacheException(L10n.format("SRVC.CACHE.ERR24"));
        iterator = collection.iterator();
_L4:
        Object obj1;
        CacheKeyAttrs cachekeyattrs;
        if(!iterator.hasNext())
            break MISSING_BLOCK_LABEL_125;
        obj1 = iterator.next();
        Object obj2 = null;
        cachekeyattrs = resolveAttrs(obj1, obj);
_L2:
label0:
        {
            CacheEntryImpl cacheentryimpl = getOrCreateEntry(obj1, cachekeyattrs);
            synchronized(cacheentryimpl.getMutex())
            {
                if(cacheentryimpl.isRemoved())
                    break label0;
                cachetximpl.add(cacheentryimpl, null, (byte)3, obj);
            }
            continue; /* Loop/switch isn't completed */
        }
        obj3;
        JVM INSTR monitorexit ;
        if(true) goto _L2; else goto _L1
_L1:
        exception;
        throw exception;
        CacheStore cachestore = registry.getConfig().getStore();
        if(cachestore != null)
            cachestore.removeAll(collection, obj);
        return;
        if(true) goto _L4; else goto _L3
_L3:
    }

    protected boolean expire(Object obj, CacheKeyAttrs cachekeyattrs)
    {
        CacheBucket cachebucket;
        if(!$assertionsDisabled && obj == null)
            throw new AssertionError();
        if(!$assertionsDisabled && cachekeyattrs == null)
            throw new AssertionError();
        cachebucket = cacheMap.get(obj.hashCode(), cachekeyattrs);
        Object obj1 = null;
        if(cachebucket == null)
            break MISSING_BLOCK_LABEL_208;
        Object obj2 = cachebucket.getMutex();
        JVM INSTR monitorenter ;
        CacheEntryImpl cacheentryimpl;
        if(cachebucket.isRemoved())
            break MISSING_BLOCK_LABEL_189;
        cacheentryimpl = cachebucket.get(obj);
        if(cacheentryimpl == null)
            break MISSING_BLOCK_LABEL_172;
        Object obj3 = cacheentryimpl.getMutex();
        JVM INSTR monitorenter ;
        if(cacheentryimpl.hasTx()) goto _L2; else goto _L1
_L1:
        cacheentryimpl.setRemoved(true);
        cachebucket.remove(cacheentryimpl);
        size.postDecr();
        spoolStore(cacheentryimpl);
        if(!CacheUtils.isRemovable(cachebucket))
            return true;
        cachebucket.setRemoved(true);
          goto _L3
_L2:
        false;
        obj3;
        JVM INSTR monitorexit ;
        obj2;
        JVM INSTR monitorexit ;
        return;
_L3:
        obj3;
        JVM INSTR monitorexit ;
        break MISSING_BLOCK_LABEL_189;
        Exception exception;
        exception;
        obj3;
        JVM INSTR monitorexit ;
        throw exception;
        if(CacheUtils.isRemovable(cachebucket)) goto _L5; else goto _L4
_L4:
        true;
        obj2;
        JVM INSTR monitorexit ;
        return;
_L5:
        cachebucket.setRemoved(true);
        obj2;
        JVM INSTR monitorexit ;
          goto _L6
        Exception exception1;
        exception1;
        throw exception1;
_L6:
        postDelete(cachebucket);
        return true;
    }

    protected void invalidate(Long long1, Object obj)
        throws CacheException
    {
        CacheTxImpl cachetximpl;
        cachetximpl = registry.getTxMgr().getLocalTx();
        if(cachetximpl == null)
            throw new CacheException(L10n.format("SRVC.CACHE.ERR25"));
_L2:
label0:
        {
            CacheGroup cachegroup = getOrCreateGroup(long1);
            synchronized(cachegroup.getMutex())
            {
                if(cachegroup.isRemoved())
                    break label0;
                cachetximpl.addGroup(cachegroup, obj);
            }
            break; /* Loop/switch isn't completed */
        }
        obj1;
        JVM INSTR monitorexit ;
        if(true) goto _L2; else goto _L1
        exception;
        throw exception;
_L1:
    }

    protected CacheEntry peek(Object obj, Object obj1)
        throws CacheException
    {
        if(!$assertionsDisabled && obj == null)
            throw new AssertionError();
        else
            return cacheMap.peek(obj, resolveAttrs(obj, obj1));
    }

    protected boolean containsKey(Object obj, Object obj1)
        throws CacheException
    {
        if(!$assertionsDisabled && obj == null)
            throw new AssertionError();
        else
            return cacheMap.containsKey(obj, resolveAttrs(obj, obj1));
    }

    protected boolean containsValue(Object obj)
    {
        if(!$assertionsDisabled && obj == null)
            throw new AssertionError();
        else
            return cacheMap.containsValue(obj);
    }

    protected int size()
    {
        return size.get();
    }

    protected boolean isEmpty()
    {
        return cacheMap.isEmpty();
    }

    protected void clear()
    {
        Iterator iterator = cacheMap.buckets();
_L3:
        CacheBucket cachebucket;
label0:
        {
            if(!iterator.hasNext())
                break; /* Loop/switch isn't completed */
            cachebucket = (CacheBucket)iterator.next();
            synchronized(cachebucket.getMutex())
            {
                List list = cachebucket.getEntries();
                for(int i = list.size(); i-- > 0;)
                {
                    CacheEntryImpl cacheentryimpl = (CacheEntryImpl)list.get(i);
                    synchronized(cacheentryimpl.getMutex())
                    {
                        if(!cacheentryimpl.isRemoved() && !cacheentryimpl.hasTx())
                        {
                            cacheentryimpl.setRemoved(true);
                            list.remove(i);
                            size.postDecr();
                            registry.getEventMgr().addEvent(3, cacheentryimpl);
                        }
                    }
                }

                if(CacheUtils.isRemovable(cachebucket))
                    break label0;
            }
            continue; /* Loop/switch isn't completed */
        }
        cachebucket.setRemoved(true);
        obj;
        JVM INSTR monitorexit ;
          goto _L1
        exception1;
        throw exception1;
_L1:
        postDelete(cachebucket);
        if(true) goto _L3; else goto _L2
_L2:
        registry.getEventMgr().dispatch();
        return;
    }

    protected Set keySet()
    {
        return cacheMap.keySet(size);
    }

    protected Collection values()
    {
        return cacheMap.values(size);
    }

    protected Set entrySet()
    {
        return cacheMap.entrySet(size);
    }

    public CacheEntryImpl getOrCreateEntry(Object obj, CacheKeyAttrs cachekeyattrs)
    {
_L2:
        CacheGroup cachegroup;
        CacheBucket cachebucket;
        cachegroup = getOrCreateGroup(new Long(cachekeyattrs.getGroupId()));
        if(!$assertionsDisabled && Thread.holdsLock(cachegroup.getMutex()))
            throw new AssertionError();
        cachebucket = getOrCreateBucket(obj.hashCode(), cachekeyattrs);
        if(!$assertionsDisabled && Thread.holdsLock(cachebucket.getMutex()))
            throw new AssertionError();
        Object obj1 = cachebucket.getMutex();
        JVM INSTR monitorenter ;
        if(!cachebucket.isRemoved())
        {
            CacheEntryImpl cacheentryimpl = cachebucket.get(obj);
            if(cacheentryimpl == null || cacheentryimpl.isRemoved())
            {
                cachebucket.put(cacheentryimpl = new CacheEntryImpl(cachebucket, obj, null, cachekeyattrs, cachegroup));
                size.postIncr();
            }
            if(!$assertionsDisabled && Thread.holdsLock(cacheentryimpl.getMutex()))
                throw new AssertionError();
            return cacheentryimpl;
        }
        obj1;
        JVM INSTR monitorexit ;
        if(true) goto _L2; else goto _L1
_L1:
        Exception exception;
        exception;
        throw exception;
    }

    public CacheBucket getOrCreateBucket(int i, CacheKeyAttrs cachekeyattrs)
    {
_L3:
        CacheGroup cachegroup;
        cachegroup = getOrCreateGroup(new Long(cachekeyattrs.getGroupId()));
        if(!$assertionsDisabled && Thread.holdsLock(cachegroup.getMutex()))
            throw new AssertionError();
        CacheMap cachemap = cacheMap;
        JVM INSTR monitorenter ;
        Object obj = cachegroup.getMutex();
        JVM INSTR monitorenter ;
        CacheBucket cachebucket;
        if(cachegroup.isRemoved())
            break MISSING_BLOCK_LABEL_155;
        cachebucket = cacheMap.get(i, cachekeyattrs);
        if(!$assertionsDisabled && cachebucket != null && Thread.holdsLock(cachebucket.getMutex()))
            throw new AssertionError();
        if(cachebucket == null || cachebucket.isRemoved())
        {
            cacheMap.put(cachebucket = new CacheBucket(i, cachekeyattrs, cachegroup));
            cachegroup.add(cachebucket);
        }
        return cachebucket;
        obj;
        JVM INSTR monitorexit ;
          goto _L1
        Exception exception;
        exception;
        obj;
        JVM INSTR monitorexit ;
        throw exception;
_L1:
        cachemap;
        JVM INSTR monitorexit ;
        if(true) goto _L3; else goto _L2
_L2:
        Exception exception1;
        exception1;
        throw exception1;
    }

    public CacheGroup getOrCreateGroup(Long long1)
    {
        Map map = grps;
        JVM INSTR monitorenter ;
        CacheGroup cachegroup = (CacheGroup)grps.get(long1);
        if(!$assertionsDisabled && cachegroup != null && Thread.holdsLock(cachegroup.getMutex()))
            throw new AssertionError();
        if(cachegroup == null || cachegroup.isRemoved())
            grps.put(long1, cachegroup = new CacheGroup(long1));
        return cachegroup;
        Exception exception;
        exception;
        throw exception;
    }

    public void delete(CacheObject cacheobject)
    {
        if(cacheobject.getType() == 2)
            delete((CacheEntryImpl)cacheobject);
        else
        if(cacheobject.getType() == 3)
            delete((CacheBucket)cacheobject);
        else
            delete((CacheGroup)cacheobject);
    }

    public void delete(CacheGroup cachegroup)
    {
        synchronized(grps)
        {
            synchronized(cachegroup.getMutex())
            {
                if(CacheUtils.isRemovable(cachegroup))
                {
                    grps.remove(cachegroup.getGroupId());
                    cachegroup.setRemoved(true);
                }
            }
        }
    }

    public void delete(CacheBucket cachebucket)
    {
label0:
        {
            synchronized(cachebucket.getMutex())
            {
                if(cachebucket.isRemoved() || cachebucket.hasTx())
                    break MISSING_BLOCK_LABEL_137;
                List list = cachebucket.getEntries();
                for(int i = list.size(); i-- > 0;)
                {
                    CacheEntryImpl cacheentryimpl = (CacheEntryImpl)list.get(i);
                    synchronized(cacheentryimpl.getMutex())
                    {
                        if(!cacheentryimpl.isRemoved() && !cacheentryimpl.hasTx())
                        {
                            cacheentryimpl.setRemoved(true);
                            size.postDecr();
                            list.remove(i);
                        }
                    }
                }

                if(CacheUtils.isRemovable(cachebucket))
                    break label0;
            }
            return;
        }
        cachebucket.setRemoved(true);
        break MISSING_BLOCK_LABEL_140;
        obj;
        JVM INSTR monitorexit ;
        return;
        obj;
        JVM INSTR monitorexit ;
          goto _L1
        exception1;
        throw exception1;
_L1:
        postDelete(cachebucket);
        return;
    }

    public void delete(CacheEntryImpl cacheentryimpl)
    {
        CacheBucket cachebucket;
label0:
        {
            cachebucket = cacheentryimpl.getBucket();
            synchronized(cacheentryimpl.getMutex())
            {
                if(CacheUtils.isRemovable(cacheentryimpl))
                {
                    cacheentryimpl.setRemoved(true);
                    size.postDecr();
                    break label0;
                }
            }
            return;
        }
        obj;
        JVM INSTR monitorexit ;
          goto _L1
        exception;
        throw exception;
_L1:
label1:
        {
            if(!$assertionsDisabled && !cacheentryimpl.isRemoved())
                throw new AssertionError();
            synchronized(cachebucket.getMutex())
            {
                if(cachebucket.isRemoved())
                    break MISSING_BLOCK_LABEL_111;
                cachebucket.remove(cacheentryimpl);
                if(CacheUtils.isRemovable(cachebucket))
                    break label1;
            }
            return;
        }
        cachebucket.setRemoved(true);
        break MISSING_BLOCK_LABEL_114;
        obj1;
        JVM INSTR monitorexit ;
        return;
        obj1;
        JVM INSTR monitorexit ;
          goto _L2
        exception1;
        throw exception1;
_L2:
        postDelete(cachebucket);
        return;
    }

    private void postDelete(CacheBucket cachebucket)
    {
        if(!$assertionsDisabled && !cachebucket.isRemoved())
            throw new AssertionError();
        if(!$assertionsDisabled && Thread.holdsLock(cachebucket.getMutex()))
            throw new AssertionError();
        cacheMap.remove(cachebucket);
        CacheGroup cachegroup = cachebucket.getGroup();
        synchronized(grps)
        {
            synchronized(cachegroup.getMutex())
            {
                cachegroup.remove(cachebucket);
                if(CacheUtils.isRemovable(cachegroup))
                {
                    cachegroup.setRemoved(true);
                    grps.remove(cachegroup.getGroupId());
                }
            }
        }
    }

    public Map spoolLoad(Map map)
    {
        CacheSpooler cachespooler = registry.getConfig().getSpooler();
        if(cachespooler != null)
            try
            {
                return cachespooler.loadAll(map);
            }
            catch(CacheException cacheexception)
            {
                registry.getConfig().setSpooler(null);
                registry.getLogger().error(L10n.format("SRVC.CACHE.ERR26"), cacheexception);
            }
        return null;
    }

    public Object spoolLoad(CacheEntryImpl cacheentryimpl)
    {
        if(!$assertionsDisabled && !Thread.holdsLock(cacheentryimpl.getMutex()))
            throw new AssertionError();
        CacheSpooler cachespooler = registry.getConfig().getSpooler();
        if(cachespooler != null)
            try
            {
                return cachespooler.load(cacheentryimpl.getKey(), cacheentryimpl.getKeyAttrs());
            }
            catch(CacheException cacheexception)
            {
                registry.getConfig().setSpooler(null);
                registry.getLogger().error(L10n.format("SRVC.CACHE.ERR26"), cacheexception);
            }
        return null;
    }

    public void spoolRemove(CacheEntryImpl cacheentryimpl)
    {
        if(!$assertionsDisabled && !Thread.holdsLock(cacheentryimpl.getMutex()))
            throw new AssertionError();
        CacheSpooler cachespooler = registry.getConfig().getSpooler();
        if(cachespooler != null)
            try
            {
                cachespooler.remove(cacheentryimpl.getKey(), cacheentryimpl.getKeyAttrs());
            }
            catch(CacheException cacheexception)
            {
                registry.getConfig().setSpooler(null);
                registry.getLogger().error(L10n.format("SRVC.CACHE.ERR27"), cacheexception);
            }
    }

    public void spoolStore(CacheEntryImpl cacheentryimpl)
    {
        if(!$assertionsDisabled && !Thread.holdsLock(cacheentryimpl.getMutex()))
            throw new AssertionError();
        CacheSpooler cachespooler = registry.getConfig().getSpooler();
        if(cachespooler != null && cacheentryimpl.getValue() != null)
            try
            {
                cachespooler.spool(cacheentryimpl.getKey(), cacheentryimpl.getValue(), cacheentryimpl.getKeyAttrs());
            }
            catch(CacheException cacheexception)
            {
                registry.getConfig().setSpooler(null);
                registry.getLogger().error(L10n.format("SRVC.CACHE.ERR28"), cacheexception);
            }
    }

    private CacheKeyAttrs resolveAttrs(Object obj, Object obj1)
        throws CacheException
    {
        CacheKeyAttrs cachekeyattrs = registry.getConfig().getAttrsResolver().resolve(obj, obj1);
        if(cachekeyattrs == null)
            throw new IllegalStateException(L10n.format("SRVC.CACHE.ERR29"));
        else
            return cachekeyattrs;
    }

    public void onHit(CacheEntryImpl cacheentryimpl)
    {
        stats.onHit();
        cacheentryimpl.getStatsImpl().onHit();
    }

    public void onMiss(CacheEntryImpl cacheentryimpl)
    {
        stats.onMiss();
        cacheentryimpl.getStatsImpl().onMiss();
    }

    public void onWrite(CacheEntryImpl cacheentryimpl)
    {
        stats.onWrite();
        cacheentryimpl.getStatsImpl().onWrite();
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

    protected CacheRegistry registry;
    protected CacheMap cacheMap;
    protected Map grps;
    private CacheStatsImpl stats;
    private BoxedInt32Sync size;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(CacheDataManager.class).desiredAssertionStatus();
    }
}
