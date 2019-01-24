// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.utils.boxed.sync;


// Referenced classes of package com.fitechlabs.xtier.utils.boxed.sync:
//            BoxedVarSync

public class BoxedFloat64Sync extends BoxedVarSync
    implements Comparable, Cloneable
{

    public BoxedFloat64Sync(double d, Object obj)
    {
        value = d;
        mutex = obj;
    }

    public BoxedFloat64Sync(double d)
    {
        value = d;
        mutex = this;
    }

    public Object clone()
        throws CloneNotSupportedException
    {
        BoxedFloat64Sync boxedfloat64sync = (BoxedFloat64Sync)super.clone();
        boxedfloat64sync.mutex = mutex != this ? new Object() : ((Object) (boxedfloat64sync));
        return boxedfloat64sync;
    }

    public double swap(BoxedFloat64Sync boxedfloat64sync)
    {
        if(boxedfloat64sync != this)
        {
            BoxedFloat64Sync boxedfloat64sync1 = this;
            BoxedFloat64Sync boxedfloat64sync2 = boxedfloat64sync;
            if(System.identityHashCode(boxedfloat64sync1) > System.identityHashCode(boxedfloat64sync2))
            {
                boxedfloat64sync1 = boxedfloat64sync;
                boxedfloat64sync2 = this;
            }
            synchronized(boxedfloat64sync1.mutex)
            {
                synchronized(boxedfloat64sync2.mutex)
                {
                    boxedfloat64sync1.set(boxedfloat64sync2.set(boxedfloat64sync1.get()));
                }
            }
        }
        return value;
    }

    public boolean commit(double d, double d1)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        if(value == d)
        {
            value = d1;
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

    public double preIncr()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return ++value;
        Exception exception;
        exception;
        throw exception;
    }

    public double postIncr()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value++;
        Exception exception;
        exception;
        throw exception;
    }

    public double preDecr()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return --value;
        Exception exception;
        exception;
        throw exception;
    }

    public double postDecr()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value--;
        Exception exception;
        exception;
        throw exception;
    }

    public double add(double d)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value += d;
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

    public double substruct(double d)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value -= d;
        Exception exception;
        exception;
        throw exception;
    }

    public double devide(double d)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value /= d;
        Exception exception;
        exception;
        throw exception;
    }

    public double multiply(double d)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value *= d;
        Exception exception;
        exception;
        throw exception;
    }

    public double get()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value;
        Exception exception;
        exception;
        throw exception;
    }

    public double set(double d)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        double d1 = value;
        value = d;
        return d1;
        Exception exception;
        exception;
        throw exception;
    }

    public int compareTo(Object obj)
    {
        return compareTo((BoxedFloat64Sync)obj);
    }

    public int compareTo(double d)
    {
        double d1 = get();
        return d1 != d ? d1 >= d ? 1 : -1 : 0;
    }

    public int compareTo(BoxedFloat64Sync boxedfloat64sync)
    {
        return compareTo(boxedfloat64sync.get());
    }

    public int hashCode()
    {
        long l = Double.doubleToLongBits(get());
        return (int)(l ^ l >>> 32);
    }

    public boolean equals(Object obj)
    {
        if(obj == this)
            return true;
        if(!(obj instanceof BoxedFloat64Sync))
            return false;
        else
            return ((BoxedFloat64Sync)obj).get() == get();
    }

    public String toString()
    {
        return Double.toString(get());
    }

    private double value;
}
