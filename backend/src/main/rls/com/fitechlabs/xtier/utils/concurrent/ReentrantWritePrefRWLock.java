// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.utils.concurrent;

import com.fitechlabs.xtier.utils.Utils;
import java.util.HashMap;
import java.util.Map;

// Referenced classes of package com.fitechlabs.xtier.utils.concurrent:
//            ReentrantRWLock

public class ReentrantWritePrefRWLock
    implements ReentrantRWLock
{

    public ReentrantWritePrefRWLock(int i)
    {
        reads = 0;
        writes = 0;
        writeOwner = null;
        pendingWrites = 0;
        readers = new HashMap(i, 1.0F);
    }

    public ReentrantWritePrefRWLock()
    {
        reads = 0;
        writes = 0;
        writeOwner = null;
        pendingWrites = 0;
        readers = new HashMap();
    }

    private void incr(Thread thread, Integer integer)
    {
        if(!$assertionsDisabled && !Thread.holdsLock(this))
            throw new AssertionError();
        if(integer == ONE)
            readers.put(thread, TWO);
        else
        if(integer == TWO)
            readers.put(thread, THREE);
        else
            readers.put(thread, new Integer(integer.intValue() + 1));
    }

    private void decr(Thread thread, Integer integer)
    {
        if(!$assertionsDisabled && !Thread.holdsLock(this))
            throw new AssertionError();
        if(integer == TWO)
            readers.put(thread, ONE);
        else
        if(integer == THREE)
            readers.put(thread, TWO);
        else
            readers.put(thread, new Integer(integer.intValue() - 1));
    }

    public synchronized void acquireRead()
    {
        Thread thread = Thread.currentThread();
        Integer integer = (Integer)readers.get(thread);
        if(integer == null)
        {
            if(thread != writeOwner)
                do
                {
                    if(writes <= 0 && pendingWrites <= 0)
                        break;
                    try
                    {
                        Utils.waitOn(this);
                    }
                    catch(RuntimeException runtimeexception)
                    {
                        notifyAll();
                        throw runtimeexception;
                    }
                } while(true);
            readers.put(thread, ONE);
        } else
        {
            incr(thread, integer);
        }
        reads++;
    }

    public boolean acquireRead(long l)
    {
        Thread thread = Thread.currentThread();
        Integer integer = (Integer)readers.get(thread);
        if(integer == null)
        {
            if(thread != writeOwner)
            {
                long l1 = System.currentTimeMillis();
                while(writes > 0 || pendingWrites > 0)
                {
                    long l2 = l - (System.currentTimeMillis() - l1);
                    if(l2 > 0L)
                        try
                        {
                            Utils.waitOn(this, l2);
                        }
                        catch(RuntimeException runtimeexception)
                        {
                            notifyAll();
                            throw runtimeexception;
                        }
                    else
                        return false;
                }
            }
            readers.put(thread, ONE);
        } else
        {
            incr(thread, integer);
        }
        reads++;
        return true;
    }

    public synchronized void releaseRead()
    {
        Thread thread = Thread.currentThread();
        Integer integer = (Integer)readers.get(thread);
        if(!$assertionsDisabled && reads <= 0)
            throw new AssertionError("Invalid read-lock release.");
        if(!$assertionsDisabled && integer == null)
            throw new AssertionError("Invalid read-lock release.");
        if(!$assertionsDisabled && integer.intValue() < 1)
            throw new AssertionError("Invalid read-lock release.");
        if(integer.intValue() == 1)
            readers.remove(thread);
        else
            decr(thread, integer);
        reads--;
        if(reads == 0 && writeOwner == null)
            notifyAll();
    }

    public synchronized boolean acquireWrite(long l)
    {
        if(!$assertionsDisabled && l <= 0L)
            throw new AssertionError();
        Thread thread = Thread.currentThread();
        if(!$assertionsDisabled && writeOwner != thread && readers.containsKey(thread))
            throw new AssertionError("Cannot acquire write-lock after acquiring read-lock.");
        if(thread != writeOwner)
        {
            pendingWrites++;
            long l1 = System.currentTimeMillis();
            while(reads > 0 || writes > 0)
            {
                long l2 = l - (System.currentTimeMillis() - l1);
                if(l2 > 0L)
                {
                    try
                    {
                        Utils.waitOn(this, l2);
                    }
                    catch(RuntimeException runtimeexception)
                    {
                        pendingWrites--;
                        notifyAll();
                        throw runtimeexception;
                    }
                } else
                {
                    pendingWrites--;
                    return false;
                }
            }
            pendingWrites--;
            writeOwner = thread;
        }
        writes++;
        return true;
    }

    public synchronized void acquireWrite()
    {
        Thread thread = Thread.currentThread();
        if(!$assertionsDisabled && writeOwner != thread && readers.containsKey(thread))
            throw new AssertionError("Cannot acquire write-lock after acquiring read-lock.");
        if(thread != writeOwner)
        {
            pendingWrites++;
            do
            {
                if(reads <= 0 && writes <= 0)
                    break;
                try
                {
                    Utils.waitOn(this);
                }
                catch(RuntimeException runtimeexception)
                {
                    pendingWrites--;
                    notifyAll();
                    throw runtimeexception;
                }
            } while(true);
            pendingWrites--;
            writeOwner = thread;
        }
        writes++;
    }

    public synchronized void releaseWrite()
    {
        if(!$assertionsDisabled && writes <= 0)
            throw new AssertionError("Invalid write-lock release.");
        if(!$assertionsDisabled && !holdsWriteLock())
            throw new AssertionError("Invalid write-lock release.");
        if(--writes == 0)
        {
            writeOwner = null;
            notifyAll();
        }
    }

    public synchronized boolean holdsWriteLock()
    {
        return Thread.currentThread() == writeOwner;
    }

    public synchronized boolean holdsReadLock()
    {
        return readers.containsKey(Thread.currentThread());
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

    private static final Integer ONE = new Integer(1);
    private static final Integer TWO = new Integer(2);
    private static final Integer THREE = new Integer(3);
    private short reads;
    private short writes;
    private Thread writeOwner;
    private short pendingWrites;
    private Map readers;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(ReentrantWritePrefRWLock.class).desiredAssertionStatus();
    }
}
