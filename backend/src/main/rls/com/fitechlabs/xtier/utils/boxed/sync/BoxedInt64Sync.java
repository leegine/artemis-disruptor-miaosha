// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.utils.boxed.sync;


// Referenced classes of package com.fitechlabs.xtier.utils.boxed.sync:
//            BoxedVarSync

public class BoxedInt64Sync extends BoxedVarSync
    implements Comparable, Cloneable
{

    public BoxedInt64Sync(long l, Object obj)
    {
        value = l;
        mutex = obj;
    }

    public BoxedInt64Sync(long l)
    {
        value = l;
        mutex = this;
    }

    public Object clone()
        throws CloneNotSupportedException
    {
        BoxedInt64Sync boxedint64sync = (BoxedInt64Sync)super.clone();
        boxedint64sync.mutex = mutex != this ? new Object() : ((Object) (boxedint64sync));
        return boxedint64sync;
    }

    public long swap(BoxedInt64Sync boxedint64sync)
    {
        if(boxedint64sync != this)
        {
            BoxedInt64Sync boxedint64sync1 = this;
            BoxedInt64Sync boxedint64sync2 = boxedint64sync;
            if(System.identityHashCode(boxedint64sync1) > System.identityHashCode(boxedint64sync2))
            {
                boxedint64sync1 = boxedint64sync;
                boxedint64sync2 = this;
            }
            synchronized(boxedint64sync1.mutex)
            {
                synchronized(boxedint64sync2.mutex)
                {
                    boxedint64sync1.set(boxedint64sync2.set(boxedint64sync1.get()));
                }
            }
        }
        return value;
    }

    public boolean commit(long l, long l1)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        if(value == l)
        {
            value = l1;
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

    public long preIncr()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return ++value;
        Exception exception;
        exception;
        throw exception;
    }

    public long postIncr()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value++;
        Exception exception;
        exception;
        throw exception;
    }

    public long preDecr()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return --value;
        Exception exception;
        exception;
        throw exception;
    }

    public long postDecr()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value--;
        Exception exception;
        exception;
        throw exception;
    }

    public long add(long l)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value += l;
        Exception exception;
        exception;
        throw exception;
    }

    public void negate()
    {
        synchronized(mutex)
        {
            value = -value;
        }
    }

    public long substruct(long l)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value -= l;
        Exception exception;
        exception;
        throw exception;
    }

    public long devide(long l)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value /= l;
        Exception exception;
        exception;
        throw exception;
    }

    public long multiply(long l)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value *= l;
        Exception exception;
        exception;
        throw exception;
    }

    public long and(long l)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value &= l;
        Exception exception;
        exception;
        throw exception;
    }

    public long or(long l)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value |= l;
        Exception exception;
        exception;
        throw exception;
    }

    public long complement()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value = ~value;
        Exception exception;
        exception;
        throw exception;
    }

    public long xor(long l)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value ^= l;
        Exception exception;
        exception;
        throw exception;
    }

    public long get()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value;
        Exception exception;
        exception;
        throw exception;
    }

    public long set(long l)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        long l1 = value;
        value = l;
        return l1;
        Exception exception;
        exception;
        throw exception;
    }

    public int compareTo(Object obj)
    {
        return compareTo((BoxedInt64Sync)obj);
    }

    public int compareTo(long l)
    {
        long l1 = get();
        return l1 != l ? l1 >= l ? 1 : -1 : 0;
    }

    public int compareTo(BoxedInt64Sync boxedint64sync)
    {
        return compareTo(boxedint64sync.get());
    }

    public int hashCode()
    {
        long l = get();
        return (int)(l ^ l >>> 32);
    }

    public boolean equals(Object obj)
    {
        if(obj == this)
            return true;
        if(!(obj instanceof BoxedInt64Sync))
            return false;
        else
            return ((BoxedInt64Sync)obj).get() == get();
    }

    public String toString()
    {
        return Long.toString(get());
    }

    private long value;
}
