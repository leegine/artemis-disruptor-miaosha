// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.utils.boxed;


public class BoxedRef
{

    public BoxedRef(Object obj)
    {
        value = obj;
    }

    public Object swap(BoxedRef boxedref)
    {
        if(boxedref != this)
            set(boxedref.set(get()));
        return value;
    }

    public boolean commit(Object obj, Object obj1)
    {
        if(value == obj)
        {
            value = obj1;
            return true;
        } else
        {
            return false;
        }
    }

    public Object get()
    {
        return value;
    }

    public Object set(Object obj)
    {
        Object obj1 = value;
        value = obj;
        return obj1;
    }

    public int hashCode()
    {
        return get().hashCode();
    }

    public boolean equals(Object obj)
    {
        if(obj == this)
            return true;
        if(!(obj instanceof BoxedRef))
            return false;
        else
            return ((BoxedRef)obj).get() == get();
    }

    public String toString()
    {
        return get().toString();
    }

    private Object value;
}
