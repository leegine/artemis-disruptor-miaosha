// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.utils.boxed.sync;


// Referenced classes of package com.fitechlabs.xtier.utils.boxed.sync:
//            BoxedVarSync

public class BoxedInt32Sync extends BoxedVarSync
    implements Comparable, Cloneable
{

    public BoxedInt32Sync(int i, Object obj)
    {
        value = i;
        mutex = obj;
    }

    public BoxedInt32Sync(int i)
    {
        value = i;
        mutex = this;
    }

    public Object clone()
        throws CloneNotSupportedException
    {
        BoxedInt32Sync boxedint32sync = (BoxedInt32Sync)super.clone();
        boxedint32sync.mutex = mutex != this ? new Object() : ((Object) (boxedint32sync));
        return boxedint32sync;
    }

    public int swap(BoxedInt32Sync boxedint32sync)
    {
        if(boxedint32sync != this)
        {
            BoxedInt32Sync boxedint32sync1 = this;
            BoxedInt32Sync boxedint32sync2 = boxedint32sync;
            if(System.identityHashCode(boxedint32sync1) > System.identityHashCode(boxedint32sync2))
            {
                boxedint32sync1 = boxedint32sync;
                boxedint32sync2 = this;
            }
            synchronized(boxedint32sync1.mutex)
            {
                synchronized(boxedint32sync2.mutex)
                {
                    boxedint32sync1.set(boxedint32sync2.set(boxedint32sync1.get()));
                }
            }
        }
        return value;
    }

    public boolean commit(int i, int j)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        if(value == i)
        {
            value = j;
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

    public int preIncr()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return ++value;
        Exception exception;
        exception;
        throw exception;
    }

    public int postIncr()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value++;
        Exception exception;
        exception;
        throw exception;
    }

    public int preDecr()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return --value;
        Exception exception;
        exception;
        throw exception;
    }

    public int postDecr()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value--;
        Exception exception;
        exception;
        throw exception;
    }

    public int add(int i)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value += i;
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

    public int substruct(int i)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value -= i;
        Exception exception;
        exception;
        throw exception;
    }

    public int devide(int i)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value /= i;
        Exception exception;
        exception;
        throw exception;
    }

    public int multiply(int i)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value *= i;
        Exception exception;
        exception;
        throw exception;
    }

    public int and(int i)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value &= i;
        Exception exception;
        exception;
        throw exception;
    }

    public int or(int i)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value |= i;
        Exception exception;
        exception;
        throw exception;
    }

    public int complement()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value = ~value;
        Exception exception;
        exception;
        throw exception;
    }

    public int xor(int i)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value ^= i;
        Exception exception;
        exception;
        throw exception;
    }

    public int get()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value;
        Exception exception;
        exception;
        throw exception;
    }

    public int set(int i)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        int j = value;
        value = i;
        return j;
        Exception exception;
        exception;
        throw exception;
    }

    public int compareTo(Object obj)
    {
        return compareTo((BoxedInt32Sync)obj);
    }

    public int compareTo(int i)
    {
        int j = get();
        return j != i ? j >= i ? 1 : -1 : 0;
    }

    public int compareTo(BoxedInt32Sync boxedint32sync)
    {
        return compareTo(boxedint32sync.get());
    }

    public int hashCode()
    {
        return get();
    }

    public boolean equals(Object obj)
    {
        if(obj == this)
            return true;
        if(!(obj instanceof BoxedInt32Sync))
            return false;
        else
            return ((BoxedInt32Sync)obj).get() == get();
    }

    public String toString()
    {
        return Integer.toString(get());
    }

    private int value;
}
