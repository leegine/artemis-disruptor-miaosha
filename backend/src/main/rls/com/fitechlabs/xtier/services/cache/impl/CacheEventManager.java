// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.cache.impl;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.services.cache.*;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.services.objpool.*;
import com.fitechlabs.xtier.services.objpool.adapters.ObjectPoolFactoryAbstractAdapter;
import com.fitechlabs.xtier.services.objpool.policies.ObjectPoolGrowPolicy;
import com.fitechlabs.xtier.utils.FifoQueue;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.cache.impl:
//            CacheRegistry, CacheImpl, CacheUtils, CacheDataManager,
//            CacheEntryImpl, CacheBucket, CacheGroup

public class CacheEventManager
{
    private class Event
    {

        void reset()
        {
            entry = null;
            grp = null;
            bucket = null;
        }

        byte getType()
        {
            return type;
        }

        void set(int i, CacheEntryImpl cacheentryimpl)
        {
            eventId = i;
            entry = cacheentryimpl;
            type = 1;
        }

        void set(CacheBucket cachebucket)
        {
            bucket = cachebucket;
            type = 2;
        }

        void set(int i, CacheGroup cachegroup)
        {
            eventId = i;
            grp = cachegroup;
            type = 3;
        }

        public CacheEntryImpl getEntry()
        {
            return entry;
        }

        public CacheBucket getBucket()
        {
            return bucket;
        }

        public int getEventId()
        {
            return eventId;
        }

        public CacheGroup getGroup()
        {
            return grp;
        }

        static final byte ENTRY = 1;
        static final byte BUCKET = 2;
        static final byte GROUP = 3;
        private byte type;
        private int eventId;
        private CacheEntryImpl entry;
        private CacheGroup grp;
        private CacheBucket bucket;

        private Event()
        {
            super();
        }

    }


    CacheEventManager(CacheRegistry cacheregistry)
        throws CacheException
    {
        listeners = new ArrayList();
        txListeners = new ArrayList();
        events = new FifoQueue();
        registry = cacheregistry;
        cache = cacheregistry.getCache();
        try
        {
            eventPool = objpool.createPool(cache.getName() + "-event", 10, new ObjectPoolFactoryAbstractAdapter() {

                public Object createObj()
                    throws ObjectPoolException
                {
                    return new Event();
                }


            {
                super();
            }
            }
, true, new ObjectPoolGrowPolicy());
        }
        catch(ObjectPoolException objectpoolexception)
        {
            throw new CacheException(objectpoolexception.getMessage(), objectpoolexception);
        }
    }

    void start()
    {
    }

    void stop()
    {
        try
        {
            objpool.deletePool(eventPool.getName());
        }
        catch(ObjectPoolException objectpoolexception)
        {
            registry.getLogger().error(objectpoolexception.getMessage(), objectpoolexception);
        }
    }

    public boolean subscribe(CacheListener cachelistener)
    {
        if(!$assertionsDisabled && cachelistener == null)
            throw new AssertionError();
        Object obj = mutex;
        JVM INSTR monitorenter ;
        if(!listeners.contains(cachelistener))
        {
            listeners.add(cachelistener);
            return true;
        }
        false;
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public boolean unsubscribe(CacheListener cachelistener)
    {
        if(!$assertionsDisabled && cachelistener == null)
            throw new AssertionError();
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return listeners.remove(cachelistener);
        Exception exception;
        exception;
        throw exception;
    }

    public List getAllListeners()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return Collections.unmodifiableList(new ArrayList(listeners));
        Exception exception;
        exception;
        throw exception;
    }

    public boolean subscribe(CacheTxListener cachetxlistener)
    {
        if(!$assertionsDisabled && cachetxlistener == null)
            throw new AssertionError();
        Object obj = mutex;
        JVM INSTR monitorenter ;
        if(txListeners.contains(cachetxlistener))
            return false;
        txListeners.add(cachetxlistener);
        true;
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public boolean unsubscribe(CacheTxListener cachetxlistener)
    {
        if(!$assertionsDisabled && cachetxlistener == null)
            throw new AssertionError();
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return txListeners.remove(cachetxlistener);
        Exception exception;
        exception;
        throw exception;
    }

    public List getAllTxListeners()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return Collections.unmodifiableList(new ArrayList(txListeners));
        Exception exception;
        exception;
        throw exception;
    }

    public void addEvent(int i, CacheEntryImpl cacheentryimpl)
    {
        synchronized(events)
        {
            Event event = aquire();
            event.set(i, cacheentryimpl);
            events.add(event);
        }
    }

    public void addEvent(CacheBucket cachebucket)
    {
        synchronized(events)
        {
            Event event = aquire();
            event.set(cachebucket);
            events.add(event);
        }
    }

    public void onStarted(CacheTx cachetx)
        throws CacheException
    {
        synchronized(mutex)
        {
            int i = 0;
            for(int j = txListeners.size(); i < j; i++)
                ((CacheTxListener)txListeners.get(i)).onTxEvent(1, cache, cachetx);

        }
    }

    public void onPrepared(CacheTx cachetx)
        throws CacheException
    {
        synchronized(mutex)
        {
            int i = 0;
            for(int j = txListeners.size(); i < j; i++)
                ((CacheTxListener)txListeners.get(i)).onTxEvent(2, cache, cachetx);

        }
    }

    public void onFinished(CacheTx cachetx)
        throws CacheException
    {
        synchronized(mutex)
        {
            int i = 0;
            for(int j = txListeners.size(); i < j; i++)
                ((CacheTxListener)txListeners.get(i)).onTxEvent(3, cache, cachetx);

        }
    }

    public void dispatch()
    {
        int i;
        int j;
        synchronized(events)
        {
            i = events.getCount();
        }
        j = 0;
_L3:
label0:
        {
            if(j >= i)
                break; /* Loop/switch isn't completed */
            synchronized(events)
            {
                if(!events.isEmpty())
                    break label0;
            }
            break; /* Loop/switch isn't completed */
        }
        Event event = (Event)events.get();
        fifoqueue1;
        JVM INSTR monitorexit ;
          goto _L1
        exception1;
        throw exception1;
_L1:
        dispatch(event);
        j++;
        if(true) goto _L3; else goto _L2
_L2:
    }

    private void dispatch(Event event)
    {
        switch(event.getType())
        {
        case 1: // '\001'
            if(CacheUtils.isRemovable(event.getEntry()))
                registry.getDataMgr().delete(event.getEntry());
            synchronized(mutex)
            {
                int i = 0;
                for(int j = listeners.size(); i < j; i++)
                    ((CacheListener)listeners.get(i)).onEntryEvent(cache, event.getEventId(), event.getEntry());

            }
            break;

        case 2: // '\002'
            if(CacheUtils.isRemovable(event.getBucket()))
                registry.getDataMgr().delete(event.getBucket());
            break;

        default:
            if(!$assertionsDisabled)
                throw new AssertionError();
            break;
        }
        release(event);
    }

    private Event aquire()
    {
        try
        {
            return (Event)eventPool.acquire();
        }
        catch(ObjectPoolException objectpoolexception) { }
        if(!$assertionsDisabled)
            throw new AssertionError();
        else
            return null;
    }

    private void release(Event event)
    {
        event.reset();
        try
        {
            eventPool.release(event);
        }
        catch(ObjectPoolException objectpoolexception)
        {
            if(!$assertionsDisabled)
                throw new AssertionError();
        }
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

    private CacheImpl cache;
    private CacheRegistry registry;
    private final ObjectPoolService objpool = XtierKernel.getInstance().objpool();
    private List listeners;
    private List txListeners;
    private FifoQueue events;
    private ObjectPool eventPool;
    private final Object mutex = new Object();
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(CacheEventManager.class).desiredAssertionStatus();
    }
}
