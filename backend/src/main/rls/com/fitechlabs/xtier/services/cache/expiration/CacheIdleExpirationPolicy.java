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

public class CacheIdleExpirationPolicy
    implements CacheExpirationPolicy, KernelServiceListener, CacheEvents
{
    private class IdleWorker extends SysThread
    {

        protected void body()
        {
            do
            {
                checkInterrupted();
                synchronized(mutex)
                {
                    long l = System.currentTimeMillis();
                    nextTime = !throttle ? 0x7fffffffffffffffL : l + idle;
                    Iterator iterator = orderSet.iterator();
                    do
                    {
                        if(!iterator.hasNext())
                            break;
                        IdleObject idleobject = (IdleObject)iterator.next();
                        long l2 = System.currentTimeMillis();
                        if(idleobject.getTime() > l)
                        {
                            if(!throttle || idleobject.getTime() > nextTime)
                                nextTime = idleobject.getTime();
                            break;
                        }
                        CacheEntry cacheentry = idleobject.getEntry();
                        if(cacheentry.getStats().getLastAccessTime() + idle <= l2 && cache.expire(cacheentry.getKey(), cacheentry.getKeyAttrs()))
                        {
                            iterator.remove();
                            idleMap.remove(cacheentry);
                        }
                    } while(true);
                    long l1 = nextTime != 0x7fffffffffffffffL ? nextTime - System.currentTimeMillis() : nextTime;
                    if(l1 > 0L)
                        Utils.waitOn(mutex, l1);
                }
            } while(true);
        }

        private IdleWorker(String s)
        {
            super(s);
        }

    }

    private class IdleObject
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

        void touch()
        {
            if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            {
                throw new AssertionError();
            } else
            {
                time = entry.getStats().getLastAccessTime() + idle;
                return;
            }
        }

        public int compareTo(Object obj)
        {
            if(!$assertionsDisabled && !Thread.holdsLock(mutex))
                throw new AssertionError();
            if(obj == this)
                return 0;
            IdleObject idleobject = (IdleObject)obj;
            if(!$assertionsDisabled && entry.equals(idleobject.entry))
                throw new AssertionError();
            if(!$assertionsDisabled && id == idleobject.id)
                throw new AssertionError();
            if(time == idleobject.time)
                return id >= idleobject.id ? 1 : -1;
            else
                return time >= idleobject.time ? 1 : -1;
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
        private long time;
        static final boolean $assertionsDisabled; /* synthetic field */


        IdleObject(CacheEntry cacheentry)
        {
            super();
            if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            {
                throw new AssertionError();
            } else
            {
                entry = cacheentry;
                id = idleObjCount++;
                return;
            }
        }
    }


    public CacheIdleExpirationPolicy(int i, long l)
    {
        this(null, i, l, true);
    }

    public CacheIdleExpirationPolicy(String s, int i, long l)
    {
        this(s, i, l, true);
    }

    public CacheIdleExpirationPolicy(String s, int i, long l, boolean flag)
    {
        idleObjCount = 0x8000000000000000L;
        nextTime = 0x7fffffffffffffffL;
        mutex = new Object();
        ArgAssert.illegalArg(i > 0, "initSize");
        ArgAssert.illegalArg(l >= 0L, "maxSize");
        initSize = i;
        idle = l;
        cacheName = s != null ? s : "xtier-dflt-cache";
        throttle = flag;
        idleMap = new HashMap(i);
        orderSet = new TreeSet();
        idleWorker = new IdleWorker(s + "-idle-expiration-policy");
        idleWorker.start();
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
            Utils.stopThread(idleWorker);
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
                IdleObject idleobject = (IdleObject)idleMap.get(cacheentry);
                if(idleobject == null)
                    idleMap.put(cacheentry, idleobject = new IdleObject(cacheentry));
                else
                    orderSet.remove(idleobject);
                idleobject.touch();
                orderSet.add(idleobject);
                if(!throttle && idleobject.getTime() < nextTime)
                {
                    nextTime = idleobject.getTime();
                    mutex.notifyAll();
                }
                break;

            case 3: // '\003'
            case 7: // '\007'
                IdleObject idleobject1 = (IdleObject)idleMap.remove(cacheentry);
                if(idleobject1 != null)
                    orderSet.remove(idleobject1);
                break;
            }
            if(!$assertionsDisabled && idleMap.size() != orderSet.size())
                throw new AssertionError("Sizes don't match [event-id=" + CacheExpirationUtils.getEventIdStr(i) + ", entry=" + cacheentry + ", idle-map-size=" + idleMap.size() + ", order-set-size=" + orderSet.size() + ']');
        }
    }

    public String toString()
    {
        return L10n.format("SRVC.CACHE.TXT10", cacheName, new Integer(initSize), new Long(idle));
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
    private final long idle;
    private final String cacheName;
    private final boolean throttle;
    private Map idleMap;
    private SortedSet orderSet;
    private Cache cache;
    private IdleWorker idleWorker;
    private long idleObjCount;
    private long nextTime;
    private final Object mutex;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(CacheIdleExpirationPolicy.class).desiredAssertionStatus();
    }









}
