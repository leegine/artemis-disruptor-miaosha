// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.cache.impl;

import com.fitechlabs.xtier.services.cache.CacheEntry;
import com.fitechlabs.xtier.services.cache.CacheKeyAttrs;
import java.util.ArrayList;
import java.util.List;

// Referenced classes of package com.fitechlabs.xtier.services.cache.impl:
//            CacheObject, CacheEntryImpl, CacheUtils, CacheGroup

public class CacheBucket extends CacheObject
{

    CacheBucket(int i, CacheKeyAttrs cachekeyattrs, CacheGroup cachegroup)
    {
        super((byte)3);
        entries = new ArrayList(1);
        if(!$assertionsDisabled && cachekeyattrs == null)
            throw new AssertionError();
        if(!$assertionsDisabled && cachegroup == null)
        {
            throw new AssertionError();
        } else
        {
            hashCode = i;
            keyAttrs = cachekeyattrs;
            grp = cachegroup;
            return;
        }
    }

    public CacheKeyAttrs getKeyAttrs()
    {
        return keyAttrs;
    }

    public int getHashCode()
    {
        return hashCode;
    }

    public List getEntries()
    {
        return entries;
    }

    public boolean isEmpty()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return entries.isEmpty();
        Exception exception;
        exception;
        throw exception;
    }

    public void put(CacheEntryImpl cacheentryimpl)
    {
        if(!$assertionsDisabled && cacheentryimpl == null)
            throw new AssertionError();
        synchronized(mutex)
        {
            entries.add(cacheentryimpl);
        }
    }

    public CacheEntryImpl remove(CacheEntry cacheentry)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
label0:
        {
            int i = entries.size();
            CacheEntryImpl cacheentryimpl;
            do
            {
                if(i-- <= 0)
                    break label0;
                cacheentryimpl = (CacheEntryImpl)entries.get(i);
                if(cacheentryimpl.isRemoved())
                    entries.remove(i);
            } while(cacheentryimpl != cacheentry);
            if(!$assertionsDisabled && !cacheentryimpl.isRemoved())
                throw new AssertionError();
            return cacheentryimpl;
        }
        null;
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public CacheEntryImpl get(Object obj)
    {
        Object obj1 = mutex;
        JVM INSTR monitorenter ;
label0:
        {
            CacheEntryImpl cacheentryimpl;
label1:
            do
            {
                for(int i = entries.size(); i-- > 0;)
                {
                    cacheentryimpl = (CacheEntryImpl)entries.get(i);
                    if(!cacheentryimpl.isRemoved())
                        continue label1;
                    entries.remove(i);
                }

                break label0;
            } while(!cacheentryimpl.getKey().equals(obj));
            return cacheentryimpl;
        }
        null;
        obj1;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public boolean containsKey(Object obj)
    {
        if(!$assertionsDisabled && obj == null)
            throw new AssertionError();
        Object obj1 = mutex;
        JVM INSTR monitorenter ;
        return get(obj) != null;
        Exception exception;
        exception;
        throw exception;
    }

    public boolean containsValue(Object obj)
    {
        if(!$assertionsDisabled && obj == null)
            throw new AssertionError();
        Object obj1 = mutex;
        JVM INSTR monitorenter ;
        int i = entries.size();
_L3:
        CacheEntryImpl cacheentryimpl;
        if(i-- <= 0)
            break MISSING_BLOCK_LABEL_126;
        cacheentryimpl = (CacheEntryImpl)entries.get(i);
        if(!cacheentryimpl.isRemoved()) goto _L2; else goto _L1
_L1:
        entries.remove(i);
          goto _L3
_L2:
        Object obj2 = cacheentryimpl.getMutex();
        JVM INSTR monitorenter ;
        if(cacheentryimpl.getValue() != null && cacheentryimpl.getValue().equals(obj))
            return true;
        obj2;
        JVM INSTR monitorexit ;
          goto _L3
        Exception exception;
        exception;
        obj2;
        JVM INSTR monitorexit ;
        throw exception;
        false;
        obj1;
        JVM INSTR monitorexit ;
        return;
        Exception exception1;
        exception1;
        throw exception1;
    }

    public CacheGroup getGroup()
    {
        return grp;
    }

    public int hashCode()
    {
        return hashCode;
    }

    public boolean equals(Object obj)
    {
        if(obj == this)
        {
            return true;
        } else
        {
            CacheBucket cachebucket = (CacheBucket)obj;
            return cachebucket.hashCode == hashCode && CacheUtils.equals(cachebucket.keyAttrs, keyAttrs) && !cachebucket.isRemoved() && !isRemoved();
        }
    }

    public String toString()
    {
        if(!$assertionsDisabled)
            throw new AssertionError();
        else
            return "Cache bucket [hash-code=" + hashCode + ", key-attributes=" + keyAttrs + ']';
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

    private final int hashCode;
    private final CacheKeyAttrs keyAttrs;
    private final CacheGroup grp;
    private ArrayList entries;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(CacheBucket.class).desiredAssertionStatus();
    }
}
