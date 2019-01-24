// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.utils.boxed;


public class BoxedBoolean
    implements Cloneable
{

    public BoxedBoolean(boolean flag)
    {
        value = flag;
    }

    public Object clone()
        throws CloneNotSupportedException
    {
        return super.clone();
    }

    public boolean swap(BoxedBoolean boxedboolean)
    {
        if(boxedboolean != this)
            set(boxedboolean.set(get()));
        return value;
    }

    public boolean commit(boolean flag, boolean flag1)
    {
        if(value == flag)
        {
            value = flag1;
            return true;
        } else
        {
            return false;
        }
    }

    public boolean preFlip()
    {
        value = !value;
        return value;
    }

    public boolean postFlip()
    {
        boolean flag = value;
        value = !value;
        return flag;
    }

    public boolean get()
    {
        return value;
    }

    public boolean set(boolean flag)
    {
        boolean flag1 = value;
        value = flag;
        return flag1;
    }

    public int hashCode()
    {
        return !value ? '\u04D5' : 1231;
    }

    public boolean equals(Object obj)
    {
        if(obj == this)
            return true;
        if(!(obj instanceof BoxedBoolean))
            return false;
        else
            return ((BoxedBoolean)obj).get() == get();
    }

    public String toString()
    {
        return Boolean.toString(value);
    }

    private boolean value;
}
