// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.objpool.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.objpool.*;
import com.fitechlabs.xtier.utils.*;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.objpool.impl:
//            ObjectPoolStatsImpl

class ObjectPoolImpl
    implements ObjectPool
{

    ObjectPoolImpl(String s, int i, ObjectPoolFactory objectpoolfactory, boolean flag, ObjectPoolResizePolicy objectpoolresizepolicy)
        throws ObjectPoolException
    {
        factory = null;
        queue = null;
        ready = false;
        acqNum = 0;
        size = 0;
        listeners = new ArrayList();
        if(!$assertionsDisabled && (s == null || objectpoolfactory == null || objectpoolresizepolicy == null || i < 0))
            throw new AssertionError();
        name = s;
        factory = objectpoolfactory;
        resizePolicy = objectpoolresizepolicy;
        lazy = flag;
        queue = new FifoQueue(i);
        size = i;
        if(!flag)
        {
            for(int j = 0; j < i; j++)
                queue.add(objectpoolfactory.createObj());

        }
        ready = true;
    }

    public boolean isLazy()
    {
        return lazy;
    }

    public String getName()
    {
        return name;
    }

    public ObjectPoolStats getStats()
    {
        return stats;
    }

    public int getSize()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        checkReady();
        return size;
        Exception exception;
        exception;
        throw exception;
    }

    public int getFree()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        checkReady();
        return size - acqNum;
        Exception exception;
        exception;
        throw exception;
    }

    public int getAcquired()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        checkReady();
        return acqNum;
        Exception exception;
        exception;
        throw exception;
    }

    public boolean addListener(ObjectPoolListener objectpoollistener)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        checkReady();
        if(listeners.contains(objectpoollistener))
            return false;
        listeners.add(objectpoollistener);
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public boolean removeListener(ObjectPoolListener objectpoollistener)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        checkReady();
        return listeners.remove(objectpoollistener);
        Exception exception;
        exception;
        throw exception;
    }

    public List getAllListeners()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        checkReady();
        return Collections.unmodifiableList(listeners);
        Exception exception;
        exception;
        throw exception;
    }

    void discard()
        throws ObjectPoolException
    {
        synchronized(mutex)
        {
            checkReady();
            int i = queue.getCount();
            ObjectPoolException objectpoolexception = null;
            for(int j = 0; j < i; j++)
                try
                {
                    factory.disposeObj(queue.get());
                    continue;
                }
                catch(ObjectPoolException objectpoolexception1)
                {
                    if(objectpoolexception == null)
                        objectpoolexception = objectpoolexception1;
                }

            queue.clear();
            queue = null;
            acqNum = 0;
            ready = false;
            if(objectpoolexception != null)
                throw objectpoolexception;
        }
    }

    public void resize(float f)
        throws ObjectPoolException
    {
        ArgAssert.illegalArg(f > 0.0F, "factor");
        synchronized(mutex)
        {
            checkReady();
            int i = (int)((float)queue.getCapacity() * f);
            ArgAssert.illegalArg(i > 0, "factor");
            resize(i);
        }
    }

    public void resize(int i)
        throws ObjectPoolException
    {
label0:
        {
            ArgAssert.illegalArg(i > 0, "newSize");
            synchronized(mutex)
            {
                checkReady();
                if(size != i)
                    break label0;
            }
            return;
        }
        int j = Math.abs(size - i);
        if(i < size)
        {
            for(int k = 0; k < j; k++)
                if(!queue.isEmpty())
                {
                    factory.disposeObj(queue.get());
                } else
                {
                    acqNum--;
                    if(!$assertionsDisabled && acqNum < 0)
                        throw new AssertionError();
                }

        } else
        if(!lazy)
        {
            for(int l = 0; l < j; l++)
                queue.add(createObj());

        }
        size = i;
        stats.touch(this);
        obj;
        JVM INSTR monitorexit ;
          goto _L1
        exception;
        throw exception;
_L1:
    }

    public Object acquire()
        throws ObjectPoolException
    {
        return acquireImpl(-1L);
    }

    public Object acquireWait()
        throws ObjectPoolException
    {
        return acquireImpl(0L);
    }

    public Object acquireWait(long l)
        throws ObjectPoolException
    {
        ArgAssert.illegalArg(l >= 0L, "msec");
        return acquireImpl(l);
    }

    private Object createObj()
        throws ObjectPoolException
    {
        Object obj = factory.createObj();
        if(obj == null)
            throw new NullPointerException(L10n.format("SRVC.OBJPOOL.ERR16"));
        else
            return obj;
    }

    private Object acquireImpl(long l)
        throws ObjectPoolException
    {
        ArgAssert.illegalArg(l >= -1L, "msec");
        Object obj = mutex;
        JVM INSTR monitorenter ;
        checkReady();
        int i = resizePolicy.addBeforeAcquire(stats);
        if(i < 0)
            throw new IllegalStateException(L10n.format("SRVC.OBJPOOL.ERR17", new Integer(i)));
        size += i;
        Object obj1 = null;
        if(i > 0)
        {
            if(!lazy)
            {
                for(int j = 1; j < i; j++)
                    queue.add(createObj());

                if(i > 1)
                    mutex.notifyAll();
                obj1 = createObj();
            } else
            {
                obj1 = !queue.isEmpty() ? queue.get() : createObj();
            }
            if(!$assertionsDisabled && obj1 == null)
                throw new AssertionError();
        } else
        if(queue.isEmpty())
        {
            if(acqNum < size)
                obj1 = createObj();
            else
            if(l > 0L)
            {
                if(!$assertionsDisabled && acqNum != size)
                    throw new AssertionError();
                Utils.waitOn(mutex, l);
                if(!queue.isEmpty())
                    obj1 = queue.get();
            } else
            if(l == 0L)
            {
                if(!$assertionsDisabled && acqNum != size)
                    throw new AssertionError();
                do
                {
                    if(obj1 != null)
                        break;
                    Utils.waitOn(mutex);
                    if(!queue.isEmpty())
                        obj1 = queue.get();
                } while(true);
            }
        } else
        {
            obj1 = queue.get();
        }
        if(obj1 != null)
        {
            onAcquire(obj1);
            acqNum++;
        }
        stats.onAcquire(this, obj1 != null);
        return obj1;
        Exception exception;
        exception;
        throw exception;
    }

    private void onAcquire(Object obj)
        throws ObjectPoolException
    {
        if((obj instanceof ObjectPoolNotifyable))
            ((ObjectPoolNotifyable)obj).onAcquire(this);
        int i = 0;
        for(int j = listeners.size(); i < j; i++)
            ((ObjectPoolListener)listeners.get(i)).onAcquired(this, obj);

    }

    private void onRelease(Object obj)
        throws ObjectPoolException
    {
        if((obj instanceof ObjectPoolNotifyable))
            ((ObjectPoolNotifyable)obj).onRelease(this);
        int i = 0;
        for(int j = listeners.size(); i < j; i++)
            ((ObjectPoolListener)listeners.get(i)).onReleased(this, obj);

    }

    private void onInvalidated(Object obj)
        throws ObjectPoolException
    {
        int i = 0;
        for(int j = listeners.size(); i < j; i++)
            ((ObjectPoolListener)listeners.get(i)).onInvalidated(this, obj);

    }

    public void invalidate(Object obj)
        throws ObjectPoolException
    {
        ArgAssert.nullArg(obj, "obj");
        synchronized(mutex)
        {
            checkReady();
            factory.disposeObj(obj);
            if(acqNum > 0)
                acqNum--;
            onInvalidated(obj);
            stats.onInvalidated(this);
        }
    }

    public void release(Object obj)
        throws ObjectPoolException
    {
        ArgAssert.nullArg(obj, "obj");
        synchronized(mutex)
        {
            checkReady();
            int i = resizePolicy.removeBeforeRelease(stats);
            if(i < 0)
                throw new IllegalStateException(L10n.format("SRVC.OBJPOOL.ERR17", new Integer(i)));
            if(i > size)
                throw new IllegalStateException(L10n.format("SRVC.OBJPOOL.ERR18", new Integer(size), new Integer(i)));
            size -= i;
            if(i > 0)
            {
                factory.disposeObj(obj);
                acqNum--;
                for(int j = 1; j < i; j++)
                    if(!queue.isEmpty())
                        factory.disposeObj(queue.get());
                    else
                    if(acqNum > 0)
                        acqNum--;

            } else
            if(acqNum == 0)
            {
                factory.disposeObj(obj);
            } else
            {
                acqNum--;
                if((obj instanceof ObjectPoolValidatable) && !((ObjectPoolValidatable)obj).isValid())
                {
                    factory.disposeObj(obj);
                    onInvalidated(obj);
                    if(!lazy)
                    {
                        queue.add(createObj());
                        mutex.notifyAll();
                    }
                } else
                {
                    queue.add(obj);
                    mutex.notifyAll();
                }
            }
            onRelease(obj);
            stats.onRelease(this);
        }
    }

    public ObjectPoolFactory getFactory()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        checkReady();
        return factory;
        Exception exception;
        exception;
        throw exception;
    }

    private void checkReady()
    {
        synchronized(mutex)
        {
            if(!ready)
                throw new IllegalStateException(L10n.format("SRVC.OBJPOOL.ERR1", name));
        }
    }

    public ObjectPoolResizePolicy getResizePolicy()
    {
        return resizePolicy;
    }

    public String toString()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        if(!ready)
            return L10n.format("SRVC.OBJPOOL.TXT18", name);
        L10n.format("SRVC.OBJPOOL.TXT1", new Object[] {
            name, new Integer(size), new Integer(queue.getCount()), Boolean.valueOf(lazy), factory, stats
        });
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
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

    private final Object mutex = new Object();
    private final String name;
    private final boolean lazy;
    private final ObjectPoolStatsImpl stats = new ObjectPoolStatsImpl(this);
    private ObjectPoolFactory factory;
    private FifoQueue queue;
    private boolean ready;
    private int acqNum;
    private int size;
    private ObjectPoolResizePolicy resizePolicy;
    private List listeners;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(ObjectPoolImpl.class).desiredAssertionStatus();
    }
}
