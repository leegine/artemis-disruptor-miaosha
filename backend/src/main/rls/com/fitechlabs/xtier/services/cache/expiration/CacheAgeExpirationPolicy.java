// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.cache.expiration;

import com.fitechlabs.xtier.kernel.KernelServiceListener;
import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.cache.*;
import com.fitechlabs.xtier.threads.SysThread;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.Utils;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.cache.expiration:
//            CacheExpirationUtils

public class CacheAgeExpirationPolicy
    implements CacheExpirationPolicy, KernelServiceListener, CacheEvents
{
    private class AgeWorker extends SysThread
    {

        protected void body()
        {
            do
            {
                checkInterrupted();
                synchronized(mutex)
                {
                    long l = System.currentTimeMillis();
                    nextTime = !throttle ? 0x7fffffffffffffffL : l + age;
                    Iterator iterator = orderSet.iterator();
                    do
                    {
                        if(!iterator.hasNext())
                            break;
                        AgeObject ageobject = (AgeObject)iterator.next();
                        if(ageobject.getTime() > l)
                        {
                            if(!throttle || ageobject.getTime() > nextTime)
                                nextTime = ageobject.getTime();
                            break;
                        }
                        CacheEntry cacheentry = ageobject.getEntry();
                        if(cache.expire(cacheentry.getKey(), cacheentry.getKeyAttrs()))
                        {
                            iterator.remove();
                            ageMap.remove(cacheentry);
                        }
                    } while(true);
                    long l1 = nextTime != 0x7fffffffffffffffL ? nextTime - System.currentTimeMillis() : nextTime;
                    if(l1 > 0L)
                        Utils.waitOn(mutex, l1);
                }
            } while(true);
        }

        private AgeWorker(String s)
        {
            super(s);
        }

    }

    private class AgeObject
        implements Comparable
    {

        CacheEntry getEntry()
        {
            return entry;
        }

        long getTime()
        {
            return time;
        }

        public int compareTo(Object obj)
        {
            if(!$assertionsDisabled && !Thread.holdsLock(mutex))
                throw new AssertionError();
            if(obj == this)
                return 0;
            AgeObject ageobject = (AgeObject)obj;
            if(!$assertionsDisabled && entry.equals(ageobject.entry))
                throw new AssertionError();
            if(!$assertionsDisabled && id == ageobject.id)
                throw new AssertionError();
            if(time == ageobject.time)
                return id >= ageobject.id ? 1 : -1;
            else
                return time >= ageobject.time ? 1 : -1;
        }

        public boolean equals(Object obj)
        {
            if(!$assertionsDisabled)
                throw new AssertionError("Equals method on the TreeSet objects should never be called.");
            else
                return false;
        }

        private final long id;
        private final CacheEntry entry;
        private final long time;
        static final boolean $assertionsDisabled; /* synthetic field */


        AgeObject(CacheEntry cacheentry)
        {
            super();
            if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            {
                throw new AssertionError();
            } else
            {
                entry = cacheentry;
                id = ageObjCount++;
                time = cacheentry.getStats().getCreateTime() + age;
                return;
            }
        }
    }


    public CacheAgeExpirationPolicy(int i, long l)
    {
        this(null, i, l, true);
    }

    public CacheAgeExpirationPolicy(String s, int i, long l)
    {
        this(s, i, l, true);
    }

    public CacheAgeExpirationPolicy(String s, int i, long l, boolean flag)
    {
        ageObjCount = 0x8000000000000000L;
        nextTime = 0x7fffffffffffffffL;
        mutex = new Object();
        ArgAssert.illegalArg(i > 0, "initSize");
        ArgAssert.illegalArg(l >= 0L, "maxSize");
        initSize = i;
        age = l;
        cacheName = s != null ? s : "xtier-dflt-cache";
        throttle = flag;
        ageMap = new HashMap(i);
        orderSet = new TreeSet();
        ageWorker = new AgeWorker(s + "-age-expiration-policy");
        ageWorker.start();
        XtierKernel.getInstance().addServiceListener("cache", this);
    }

    public void afterStart(String s)
    {
        if(!$assertionsDisabled && !s.equals("cache"))
            throw new AssertionError();
        else
            return;
    }

    public void beforeStop(String s)
    {
        if(!$assertionsDisabled && !s.equals("cache"))
        {
            throw new AssertionError();
        } else
        {
            Utils.stopThread(ageWorker);
            return;
        }
    }

    public void onEntryEvent(Cache cache1, int i, CacheEntry cacheentry)
    {
        synchronized(mutex)
        {
            if(cache == null)
                cache = cache1;
            switch(i)
            {
            case 1: // '\001'
            case 2: // '\002'
            case 5: // '\005'
            case 6: // '\006'
                AgeObject ageobject = (AgeObject)ageMap.get(cacheentry);
                if(ageobject == null)
                {
                    AgeObject ageobject1;
                    ageMap.put(cacheentry, ageobject1 = new AgeObject(cacheentry));
                    orderSet.add(ageobject1);
                    if(!throttle && ageobject1.getTime() < nextTime)
                    {
                        nextTime = ageobject1.getTime();
                        mutex.notifyAll();
                    }
                }
                break;

            case 3: // '\003'
            case 7: // '\007'
                AgeObject ageobject2 = (AgeObject)ageMap.remove(cacheentry);
                if(ageobject2 != null)
                    orderSet.remove(ageobject2);
                break;
            }
            if(!$assertionsDisabled && ageMap.size() != orderSet.size())
                throw new AssertionError("Sizes don't match [event-id=" + CacheExpirationUtils.getEventIdStr(i) + ", entry=" + cacheentry + ", age-map-size=" + ageMap.size() + ", order-set-size=" + orderSet.size() + ']');
        }
    }

    public String toString()
    {
        return L10n.format("SRVC.CACHE.TXT9", cacheName, new Integer(initSize), new Long(age));
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
    private final long age;
    private final String cacheName;
    private final boolean throttle;
    private Map ageMap;
    private SortedSet orderSet;
    private Cache cache;
    private AgeWorker ageWorker;
    private long ageObjCount;
    private long nextTime;
    private final Object mutex;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(CacheAgeExpirationPolicy.class).desiredAssertionStatus();
    }









}
