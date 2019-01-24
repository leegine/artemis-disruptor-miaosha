// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.cache.impl;

import com.fitechlabs.xtier.services.cache.CacheKeyAttrs;

// Referenced classes of package com.fitechlabs.xtier.services.cache.impl:
//            CacheTxObject, CacheEntryImpl, CacheUtils, CacheObject,
//            CacheGroup

public class CacheTxEntry
    implements CacheTxObject
{
    public static class EntryKey
    {

        public int hashCode()
        {
            return entryKey.hashCode();
        }

        public boolean equals(Object obj)
        {
            EntryKey entrykey = (EntryKey)obj;
            return entrykey.entryKey.equals(entryKey) && CacheUtils.equals(entrykey.keyAttrs, keyAttrs);
        }

        private Object entryKey;
        private CacheKeyAttrs keyAttrs;
        static final boolean $assertionsDisabled; /* synthetic field */


        public EntryKey(Object obj, CacheKeyAttrs cachekeyattrs)
        {
            if(!$assertionsDisabled && obj == null)
                throw new AssertionError();
            if(!$assertionsDisabled && cachekeyattrs == null)
            {
                throw new AssertionError();
            } else
            {
                entryKey = obj;
                keyAttrs = cachekeyattrs;
                return;
            }
        }
    }


    CacheTxEntry(CacheEntryImpl cacheentryimpl, Object obj, byte byte0, Object obj1)
    {
        if(!$assertionsDisabled && cacheentryimpl == null)
            throw new AssertionError();
        if(!$assertionsDisabled && !Thread.holdsLock(cacheentryimpl.getMutex()))
        {
            throw new AssertionError();
        } else
        {
            entry = cacheentryimpl;
            enlistType = byte0;
            txValue = obj;
            userArgs = obj1;
            modCount = cacheentryimpl.getModCount();
            txKey = new EntryKey(cacheentryimpl.getKey(), cacheentryimpl.getKeyAttrs());
            return;
        }
    }

    public CacheObject getObj()
    {
        return entry;
    }

    public Object getTxKey()
    {
        return txKey;
    }

    public long getModCount()
    {
        return modCount;
    }

    public CacheKeyAttrs getKeyAttrs()
    {
        return entry.getKeyAttrs();
    }

    public CacheGroup getGroup()
    {
        return entry.getGroup();
    }

    public byte getOp()
    {
        return enlistType;
    }

    public Object getUserArgs()
    {
        return userArgs;
    }

    public CacheEntryImpl getEntry()
    {
        return entry;
    }

    public Object getKey()
    {
        return entry.getKey();
    }

    public Object getTxValue()
    {
        return txValue;
    }

    public void setTxValue(Object obj)
    {
        txValue = obj;
    }

    public void invalidate()
    {
        txValue = null;
    }

    public String toString()
    {
        if(!$assertionsDisabled)
            throw new AssertionError();
        else
            return "Enlistment [entry=" + entry + ", type=" + CacheUtils.getEnlistStr(enlistType) + ']';
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

    private final CacheEntryImpl entry;
    private final byte enlistType;
    private final EntryKey txKey;
    private Object txValue;
    private Object userArgs;
    private final long modCount;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(CacheTxEntry.class).desiredAssertionStatus();
    }
}
