// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.utils.boxed.sync;


// Referenced classes of package com.fitechlabs.xtier.utils.boxed.sync:
//            BoxedVarSync

public class BoxedFloat32Sync extends BoxedVarSync
    implements Comparable, Cloneable
{

    public BoxedFloat32Sync(float f, Object obj)
    {
        value = f;
        mutex = obj;
    }

    public BoxedFloat32Sync(float f)
    {
        value = f;
        mutex = this;
    }

    public Object clone()
        throws CloneNotSupportedException
    {
        BoxedFloat32Sync boxedfloat32sync = (BoxedFloat32Sync)super.clone();
        boxedfloat32sync.mutex = mutex != this ? new Object() : ((Object) (boxedfloat32sync));
        return boxedfloat32sync;
    }

    public float swap(BoxedFloat32Sync boxedfloat32sync)
    {
        if(boxedfloat32sync != this)
        {
            BoxedFloat32Sync boxedfloat32sync1 = this;
            BoxedFloat32Sync boxedfloat32sync2 = boxedfloat32sync;
            if(System.identityHashCode(boxedfloat32sync1) > System.identityHashCode(boxedfloat32sync2))
            {
                boxedfloat32sync1 = boxedfloat32sync;
                boxedfloat32sync2 = this;
            }
            synchronized(boxedfloat32sync1.mutex)
            {
                synchronized(boxedfloat32sync2.mutex)
                {
                    boxedfloat32sync1.set(boxedfloat32sync2.set(boxedfloat32sync1.get()));
                }
            }
        }
        return value;
    }

    public boolean commit(float f, float f1)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        if(value == f)
        {
            value = f1;
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

    public float preIncr()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return ++value;
        Exception exception;
        exception;
        throw exception;
    }

    public float postIncr()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value++;
        Exception exception;
        exception;
        throw exception;
    }

    public float preDecr()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return --value;
        Exception exception;
        exception;
        throw exception;
    }

    public float postDecr()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value--;
        Exception exception;
        exception;
        throw exception;
    }

    public float add(float f)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value += f;
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

    public float substruct(float f)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value -= f;
        Exception exception;
        exception;
        throw exception;
    }

    public float devide(float f)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value /= f;
        Exception exception;
        exception;
        throw exception;
    }

    public float multiply(float f)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value *= f;
        Exception exception;
        exception;
        throw exception;
    }

    public float get()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value;
        Exception exception;
        exception;
        throw exception;
    }

    public float set(float f)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        float f1 = value;
        value = f;
        return f1;
        Exception exception;
        exception;
        throw exception;
    }

    public int compareTo(Object obj)
    {
        return compareTo((BoxedFloat32Sync)obj);
    }

    public int compareTo(float f)
    {
        float f1 = get();
        return f1 != f ? f1 >= f ? 1 : -1 : 0;
    }

    public int compareTo(BoxedFloat32Sync boxedfloat32sync)
    {
        return compareTo(boxedfloat32sync.get());
    }

    public int hashCode()
    {
        return Float.floatToIntBits(get());
    }

    public boolean equals(Object obj)
    {
        if(obj == this)
            return true;
        if(!(obj instanceof BoxedFloat32Sync))
            return false;
        else
            return ((BoxedFloat32Sync)obj).get() == get();
    }

    public String toString()
    {
        return Float.toString(get());
    }

    private float value;
}
