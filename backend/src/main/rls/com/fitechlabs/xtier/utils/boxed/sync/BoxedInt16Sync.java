// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.utils.boxed.sync;


// Referenced classes of package com.fitechlabs.xtier.utils.boxed.sync:
//            BoxedVarSync

public class BoxedInt16Sync extends BoxedVarSync
    implements Comparable, Cloneable
{

    public BoxedInt16Sync(short word0, Object obj)
    {
        value = word0;
        mutex = obj;
    }

    public BoxedInt16Sync(short word0)
    {
        value = word0;
        mutex = this;
    }

    public Object clone()
        throws CloneNotSupportedException
    {
        BoxedInt16Sync boxedint16sync = (BoxedInt16Sync)super.clone();
        boxedint16sync.mutex = mutex != this ? new Object() : ((Object) (boxedint16sync));
        return boxedint16sync;
    }

    public short swap(BoxedInt16Sync boxedint16sync)
    {
        if(boxedint16sync != this)
        {
            BoxedInt16Sync boxedint16sync1 = this;
            BoxedInt16Sync boxedint16sync2 = boxedint16sync;
            if(System.identityHashCode(boxedint16sync1) > System.identityHashCode(boxedint16sync2))
            {
                boxedint16sync1 = boxedint16sync;
                boxedint16sync2 = this;
            }
            synchronized(boxedint16sync1.mutex)
            {
                synchronized(boxedint16sync2.mutex)
                {
                    boxedint16sync1.set(boxedint16sync2.set(boxedint16sync1.get()));
                }
            }
        }
        return value;
    }

    public boolean commit(short word0, short word1)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        if(value == word0)
        {
            value = word1;
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

    public short preIncr()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return ++value;
        Exception exception;
        exception;
        throw exception;
    }

    public short postIncr()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value++;
        Exception exception;
        exception;
        throw exception;
    }

    public short preDecr()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return --value;
        Exception exception;
        exception;
        throw exception;
    }

    public short postDecr()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value--;
        Exception exception;
        exception;
        throw exception;
    }

    public short add(short word0)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value += word0;
        Exception exception;
        exception;
        throw exception;
    }

    public void negate()
    {
        synchronized(mutex)
        {
            value = (short)(-value);
        }
    }

    public short substruct(short word0)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value -= word0;
        Exception exception;
        exception;
        throw exception;
    }

    public short devide(short word0)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value /= word0;
        Exception exception;
        exception;
        throw exception;
    }

    public short multiply(short word0)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value *= word0;
        Exception exception;
        exception;
        throw exception;
    }

    public short and(short word0)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value &= word0;
        Exception exception;
        exception;
        throw exception;
    }

    public short or(short word0)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value |= word0;
        Exception exception;
        exception;
        throw exception;
    }

    public short complement()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value = (short)(~value);
        Exception exception;
        exception;
        throw exception;
    }

    public short xor(short word0)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value ^= word0;
        Exception exception;
        exception;
        throw exception;
    }

    public short get()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value;
        Exception exception;
        exception;
        throw exception;
    }

    public short set(short word0)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        short word1 = value;
        value = word0;
        return word1;
        Exception exception;
        exception;
        throw exception;
    }

    public int compareTo(Object obj)
    {
        return compareTo((BoxedInt16Sync)obj);
    }

    public int compareTo(short word0)
    {
        short word1 = get();
        return word1 != word0 ? word1 >= word0 ? 1 : -1 : 0;
    }

    public int compareTo(BoxedInt16Sync boxedint16sync)
    {
        return compareTo(boxedint16sync.get());
    }

    public int hashCode()
    {
        return get();
    }

    public boolean equals(Object obj)
    {
        if(obj == this)
            return true;
        if(!(obj instanceof BoxedInt16Sync))
            return false;
        else
            return ((BoxedInt16Sync)obj).get() == get();
    }

    public String toString()
    {
        return Short.toString(get());
    }

    private short value;
}
