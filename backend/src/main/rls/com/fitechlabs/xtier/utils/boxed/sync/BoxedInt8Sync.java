// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.utils.boxed.sync;


// Referenced classes of package com.fitechlabs.xtier.utils.boxed.sync:
//            BoxedVarSync

public class BoxedInt8Sync extends BoxedVarSync
    implements Comparable, Cloneable
{

    public BoxedInt8Sync(byte byte0, Object obj)
    {
        value = byte0;
        mutex = obj;
    }

    public BoxedInt8Sync(byte byte0)
    {
        value = byte0;
        mutex = this;
    }

    public Object clone()
        throws CloneNotSupportedException
    {
        BoxedInt8Sync boxedint8sync = (BoxedInt8Sync)super.clone();
        boxedint8sync.mutex = mutex != this ? new Object() : ((Object) (boxedint8sync));
        return boxedint8sync;
    }

    public byte swap(BoxedInt8Sync boxedint8sync)
    {
        if(boxedint8sync != this)
        {
            BoxedInt8Sync boxedint8sync1 = this;
            BoxedInt8Sync boxedint8sync2 = boxedint8sync;
            if(System.identityHashCode(boxedint8sync1) > System.identityHashCode(boxedint8sync2))
            {
                boxedint8sync1 = boxedint8sync;
                boxedint8sync2 = this;
            }
            synchronized(boxedint8sync1.mutex)
            {
                synchronized(boxedint8sync2.mutex)
                {
                    boxedint8sync1.set(boxedint8sync2.set(boxedint8sync1.get()));
                }
            }
        }
        return value;
    }

    public boolean commit(byte byte0, byte byte1)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        if(value == byte0)
        {
            value = byte1;
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

    public byte preIncr()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return ++value;
        Exception exception;
        exception;
        throw exception;
    }

    public byte postIncr()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value++;
        Exception exception;
        exception;
        throw exception;
    }

    public byte preDecr()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return --value;
        Exception exception;
        exception;
        throw exception;
    }

    public byte postDecr()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value--;
        Exception exception;
        exception;
        throw exception;
    }

    public byte add(byte byte0)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value += byte0;
        Exception exception;
        exception;
        throw exception;
    }

    public void negate()
    {
        synchronized(mutex)
        {
            value = (byte)(-value);
        }
    }

    public byte substruct(byte byte0)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value -= byte0;
        Exception exception;
        exception;
        throw exception;
    }

    public byte devide(byte byte0)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value /= byte0;
        Exception exception;
        exception;
        throw exception;
    }

    public byte multiply(byte byte0)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value *= byte0;
        Exception exception;
        exception;
        throw exception;
    }

    public byte and(byte byte0)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value &= byte0;
        Exception exception;
        exception;
        throw exception;
    }

    public byte or(byte byte0)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value |= byte0;
        Exception exception;
        exception;
        throw exception;
    }

    public byte complement()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value = (byte)(~value);
        Exception exception;
        exception;
        throw exception;
    }

    public byte xor(byte byte0)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value ^= byte0;
        Exception exception;
        exception;
        throw exception;
    }

    public byte get()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value;
        Exception exception;
        exception;
        throw exception;
    }

    public byte set(byte byte0)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        byte byte1 = value;
        value = byte0;
        return byte1;
        Exception exception;
        exception;
        throw exception;
    }

    public int compareTo(Object obj)
    {
        return compareTo((BoxedInt8Sync)obj);
    }

    public int compareTo(byte byte0)
    {
        byte byte1 = get();
        return byte1 != byte0 ? byte1 >= byte0 ? 1 : -1 : 0;
    }

    public int compareTo(BoxedInt8Sync boxedint8sync)
    {
        return compareTo(boxedint8sync.get());
    }

    public int hashCode()
    {
        return get();
    }

    public boolean equals(Object obj)
    {
        if(obj == this)
            return true;
        if(!(obj instanceof BoxedInt8Sync))
            return false;
        else
            return ((BoxedInt8Sync)obj).get() == get();
    }

    public String toString()
    {
        return Byte.toString(get());
    }

    private byte value;
}
