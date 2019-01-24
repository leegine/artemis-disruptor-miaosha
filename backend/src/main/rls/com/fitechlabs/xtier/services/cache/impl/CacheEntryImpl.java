// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.cache.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.cache.*;
import com.fitechlabs.xtier.utils.boxed.sync.BoxedInt64Sync;

// Referenced classes of package com.fitechlabs.xtier.services.cache.impl:
//            CacheObject, CacheStatsImpl, CacheUtils, CacheGroup,
//            CacheBucket

public class CacheEntryImpl extends CacheObject
    implements CacheEntry
{

    CacheEntryImpl(CacheBucket cachebucket, Object obj, Object obj1, CacheKeyAttrs cachekeyattrs, CacheGroup cachegroup)
    {
        super((byte)2);
        stats = new CacheStatsImpl();
        modCount = count.postIncr();
        isPendingRemove = false;
        if(!$assertionsDisabled && cachebucket == null)
            throw new AssertionError();
        if(!$assertionsDisabled && obj == null)
            throw new AssertionError();
        if(!$assertionsDisabled && cachekeyattrs == null)
            throw new AssertionError();
        if(!$assertionsDisabled && cachegroup == null)
        {
            throw new AssertionError();
        } else
        {
            bucket = cachebucket;
            key = obj;
            value = obj1;
            keyAttrs = cachekeyattrs;
            grp = cachegroup;
            hash = (int)((long)obj.hashCode() ^ cachekeyattrs.getGroupId() ^ cachekeyattrs.getTypeId() ^ (long)(!cachekeyattrs.isDepended() ? 0 : 1));
            return;
        }
    }

    public Object getKey()
    {
        return key;
    }

    public CacheKeyAttrs getKeyAttrs()
    {
        return keyAttrs;
    }

    public Object getValue()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value;
        Exception exception;
        exception;
        throw exception;
    }

    public CacheStats getStats()
    {
        return stats;
    }

    public CacheStatsImpl getStatsImpl()
    {
        return stats;
    }

    public Object setValue(Object obj)
    {
        throw new UnsupportedOperationException(L10n.format("SRVC.CACHE.ERR30", "CacheEntry.setValue(Object)"));
    }

    public CacheBucket getBucket()
    {
        return bucket;
    }

    public boolean isPendingRemove()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return isPendingRemove;
        Exception exception;
        exception;
        throw exception;
    }

    public void setPendingRemove(boolean flag)
    {
        synchronized(mutex)
        {
            isPendingRemove = flag;
        }
    }

    public void setVal(Object obj)
    {
        synchronized(mutex)
        {
            value = obj;
            modCount++;
        }
    }

    public void invalidate()
    {
        synchronized(mutex)
        {
            value = null;
            modCount++;
        }
    }

    public CacheGroup getGroup()
    {
        return grp;
    }

    public long getModCount()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return modCount;
        Exception exception;
        exception;
        throw exception;
    }

    public int hashCode()
    {
        return hash;
    }

    public boolean equals(Object obj)
    {
        if(obj == this)
        {
            return true;
        } else
        {
            CacheEntryImpl cacheentryimpl = (CacheEntryImpl)obj;
            return hash == cacheentryimpl.hash && key.equals(cacheentryimpl.key) && CacheUtils.equals(keyAttrs, cacheentryimpl.keyAttrs);
        }
    }

    public String toString()
    {
        return L10n.format("SRVC.CACHE.TXT17", key, keyAttrs, value, stats);
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

    private static final BoxedInt64Sync count = new BoxedInt64Sync(0L);
    private final Object key;
    private final CacheKeyAttrs keyAttrs;
    private final CacheGroup grp;
    private final CacheBucket bucket;
    private final int hash;
    private Object value;
    private CacheStatsImpl stats;
    private long modCount;
    private boolean isPendingRemove;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(CacheEntryImpl.class).desiredAssertionStatus();
    }
}
