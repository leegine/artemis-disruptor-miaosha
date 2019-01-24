// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.utils.boxed;


public class BoxedInt32
    implements Comparable, Cloneable
{

    public BoxedInt32(int i)
    {
        value = i;
    }

    public Object clone()
        throws CloneNotSupportedException
    {
        return super.clone();
    }

    public int swap(BoxedInt32 boxedint32)
    {
        if(boxedint32 != this)
            set(boxedint32.set(get()));
        return value;
    }

    public boolean commit(int i, int j)
    {
        if(value == i)
        {
            value = j;
            return true;
        } else
        {
            return false;
        }
    }

    public int preIncr()
    {
        return ++value;
    }

    public int postIncr()
    {
        return value++;
    }

    public int preDecr()
    {
        return --value;
    }

    public int postDecr()
    {
        return value--;
    }

    public int add(int i)
    {
        return value += i;
    }

    public void negate()
    {
        value = -value;
    }

    public int substruct(int i)
    {
        return value -= i;
    }

    public int devide(int i)
    {
        return value /= i;
    }

    public int multiply(int i)
    {
        return value *= i;
    }

    public int get()
    {
        return value;
    }

    public int set(int i)
    {
        int j = value;
        value = i;
        return j;
    }

    public int compareTo(Object obj)
    {
        return compareTo((BoxedInt32)obj);
    }

    public int compareTo(int i)
    {
        int j = get();
        return j != i ? j >= i ? 1 : -1 : 0;
    }

    public int compareTo(BoxedInt32 boxedint32)
    {
        return compareTo(boxedint32.get());
    }

    public int hashCode()
    {
        return get();
    }

    public boolean equals(Object obj)
    {
        if(obj == this)
            return true;
        if(!(obj instanceof BoxedInt32))
            return false;
        else
            return ((BoxedInt32)obj).get() == get();
    }

    public String toString()
    {
        return Integer.toString(get());
    }

    private int value;
}
