// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.cache.expiration;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.cache.*;
import com.fitechlabs.xtier.utils.ArgAssert;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.cache.expiration:
//            CacheExpirationUtils

public class CacheLfuExpirationPolicy
    implements CacheExpirationPolicy, CacheEvents
{
    private class LfuObject
        implements Comparable
    {

        CacheEntry getEntry()
        {
            return entry;
        }

        long getCount()
        {
            return count;
        }

        void touch()
        {
            if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            {
                throw new AssertionError();
            } else
            {
                count++;
                return;
            }
        }

        public int compareTo(Object obj)
        {
            if(!$assertionsDisabled && !Thread.holdsLock(mutex))
                throw new AssertionError();
            if(!$assertionsDisabled && obj == null)
                throw new AssertionError();
            if(obj == this)
                return 0;
            LfuObject lfuobject = (LfuObject)obj;
            if(!$assertionsDisabled && entry.equals(lfuobject.entry))
                throw new AssertionError();
            if(!$assertionsDisabled && id == lfuobject.id)
                throw new AssertionError();
            else
                return count != lfuobject.count ? ((byte)(count >= lfuobject.count ? 1 : -1)) : id >= lfuobject.id ? 1 : -1;
        }

        public boolean equals(Object obj)
        {
            if(!$assertionsDisabled)
                throw new AssertionError("Equals method on the TreeSet objects should never be called.");
            else
                return false;
        }

        public String toString()
        {
            if(!$assertionsDisabled)
                throw new AssertionError();
            else
                return "Lfu object [count=" + count + ", key=" + entry.getKey() + ", system-id=" + System.identityHashCode(this) + ']';
        }

        private final long id;
        private final CacheEntry entry;
        private long count;
        static final boolean $assertionsDisabled; /* synthetic field */


        LfuObject(CacheEntry cacheentry)
        {
            super();
            if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            {
                throw new AssertionError();
            } else
            {
                entry = cacheentry;
                id = lfuCount++;
                count = 0L;
                return;
            }
        }
    }


    public CacheLfuExpirationPolicy(int i, int j)
    {
        this(null, i, j);
    }

    public CacheLfuExpirationPolicy(String s, int i, int j)
    {
        lfuCount = 0x8000000000000000L;
        mutex = new Object();
        ArgAssert.illegalArg(i > 0, "initSize");
        ArgAssert.illegalArg(j >= i, "maxSize");
        initSize = i;
        maxSize = j;
        cacheName = s;
        lfuMap = new HashMap(i);
        orderSet = new TreeSet();
    }

    public void onEntryEvent(Cache cache, int i, CacheEntry cacheentry)
    {
        synchronized(mutex)
        {
            switch(i)
            {
            case 1: // '\001'
            case 2: // '\002'
            case 5: // '\005'
            case 6: // '\006'
                LfuObject lfuobject = (LfuObject)lfuMap.get(cacheentry);
                if(lfuobject == null)
                    lfuMap.put(cacheentry, lfuobject = new LfuObject(cacheentry));
                else
                    orderSet.remove(lfuobject);
                lfuobject.touch();
                orderSet.add(lfuobject);
                if(lfuMap.size() > maxSize)
                {
                    Iterator iterator = orderSet.iterator();
                    do
                    {
                        if(lfuMap.size() <= maxSize || !iterator.hasNext())
                            break;
                        LfuObject lfuobject1 = (LfuObject)iterator.next();
                        CacheEntry cacheentry1 = lfuobject1.getEntry();
                        if(cache.expire(cacheentry1.getKey(), cacheentry1.getKeyAttrs()))
                        {
                            iterator.remove();
                            lfuMap.remove(cacheentry1);
                        }
                    } while(true);
                }
                break;

            case 3: // '\003'
            case 7: // '\007'
                LfuObject lfuobject2 = (LfuObject)lfuMap.remove(cacheentry);
                if(lfuobject2 != null)
                    orderSet.remove(lfuobject2);
                break;
            }
            if(!$assertionsDisabled && lfuMap.size() != orderSet.size())
                throw new AssertionError("Sizes don't match [event-id=" + CacheExpirationUtils.getEventIdStr(i) + ", entry=" + cacheentry + ", lfu-map-size=" + lfuMap.size() + ", order-set-size=" + orderSet.size() + ']');
        }
    }

    public String toString()
    {
        return L10n.format("SRVC.CACHE.TXT12", cacheName, new Integer(initSize), new Integer(maxSize));
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

    private final int initSize;
    private final int maxSize;
    private final String cacheName;
    private Map lfuMap;
    private SortedSet orderSet;
    private long lfuCount;
    private final Object mutex;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(CacheLfuExpirationPolicy.class).desiredAssertionStatus();
    }


}
