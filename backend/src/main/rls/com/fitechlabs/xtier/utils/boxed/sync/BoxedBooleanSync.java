// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.utils.boxed.sync;


// Referenced classes of package com.fitechlabs.xtier.utils.boxed.sync:
//            BoxedVarSync

public class BoxedBooleanSync extends BoxedVarSync
    implements Cloneable
{

    public BoxedBooleanSync(boolean flag, Object obj)
    {
        value = flag;
        mutex = obj;
    }

    public BoxedBooleanSync(boolean flag)
    {
        value = flag;
        mutex = this;
    }

    public Object clone()
        throws CloneNotSupportedException
    {
        BoxedBooleanSync boxedbooleansync = (BoxedBooleanSync)super.clone();
        boxedbooleansync.mutex = mutex != this ? new Object() : ((Object) (boxedbooleansync));
        return boxedbooleansync;
    }

    public boolean swap(BoxedBooleanSync boxedbooleansync)
    {
        if(boxedbooleansync != this)
        {
            BoxedBooleanSync boxedbooleansync1 = this;
            BoxedBooleanSync boxedbooleansync2 = boxedbooleansync;
            if(System.identityHashCode(boxedbooleansync1) > System.identityHashCode(boxedbooleansync2))
            {
                boxedbooleansync1 = boxedbooleansync;
                boxedbooleansync2 = this;
            }
            synchronized(boxedbooleansync1.mutex)
            {
                synchronized(boxedbooleansync2.mutex)
                {
                    boxedbooleansync1.set(boxedbooleansync2.set(boxedbooleansync1.get()));
                }
            }
        }
        return value;
    }

    public boolean commit(boolean flag, boolean flag1)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        if(value == flag)
        {
            value = flag1;
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

    public boolean preFlip()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        value = !value;
        return value;
        Exception exception;
        exception;
        throw exception;
    }

    public boolean postFlip()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        boolean flag = value;
        value = !value;
        return flag;
        Exception exception;
        exception;
        throw exception;
    }

    public boolean get()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value;
        Exception exception;
        exception;
        throw exception;
    }

    public boolean set(boolean flag)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        boolean flag1 = value;
        value = flag;
        return flag1;
        Exception exception;
        exception;
        throw exception;
    }

    public int hashCode()
    {
        return !value ? '\u04D5' : 1231;
    }

    public boolean equals(Object obj)
    {
        if(obj == this)
            return true;
        if(!(obj instanceof BoxedBooleanSync))
            return false;
        else
            return ((BoxedBooleanSync)obj).get() == get();
    }

    public String toString()
    {
        return Boolean.toString(value);
    }

    private boolean value;
}
