// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.utils.boxed.sync;


// Referenced classes of package com.fitechlabs.xtier.utils.boxed.sync:
//            BoxedVarSync

public class BoxedRefSync extends BoxedVarSync
{

    public BoxedRefSync(Object obj, Object obj1)
    {
        value = obj;
        mutex = obj1;
    }

    public BoxedRefSync(Object obj)
    {
        value = obj;
        mutex = this;
    }

    public Object swap(BoxedRefSync boxedrefsync)
    {
        if(boxedrefsync != this)
        {
            BoxedRefSync boxedrefsync1 = this;
            BoxedRefSync boxedrefsync2 = boxedrefsync;
            if(System.identityHashCode(boxedrefsync1) > System.identityHashCode(boxedrefsync2))
            {
                boxedrefsync1 = boxedrefsync;
                boxedrefsync2 = this;
            }
            synchronized(boxedrefsync1.mutex)
            {
                synchronized(boxedrefsync2.mutex)
                {
                    boxedrefsync1.set(boxedrefsync2.set(boxedrefsync1.get()));
                }
            }
        }
        return value;
    }

    public boolean commit(Object obj, Object obj1)
    {
        Object obj2 = mutex;
        JVM INSTR monitorenter ;
        if(value == obj)
        {
            value = obj1;
            return true;
        }
        false;
        obj2;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public Object get()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return value;
        Exception exception;
        exception;
        throw exception;
    }

    public Object set(Object obj)
    {
        Object obj1 = mutex;
        JVM INSTR monitorenter ;
        Object obj2 = value;
        value = obj;
        return obj2;
        Exception exception;
        exception;
        throw exception;
    }

    public int hashCode()
    {
        return get().hashCode();
    }

    public boolean equals(Object obj)
    {
        if(obj == this)
            return true;
        if(!(obj instanceof BoxedRefSync))
            return false;
        else
            return ((BoxedRefSync)obj).get() == get();
    }

    public String toString()
    {
        return get().toString();
    }

    private Object value;
}
