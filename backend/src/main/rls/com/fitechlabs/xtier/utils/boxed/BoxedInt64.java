// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.utils.boxed;


public class BoxedInt64
    implements Comparable, Cloneable
{

    public BoxedInt64(long l)
    {
        value = l;
    }

    public Object clone()
        throws CloneNotSupportedException
    {
        return super.clone();
    }

    public long swap(BoxedInt64 boxedint64)
    {
        if(boxedint64 != this)
            set(boxedint64.set(get()));
        return value;
    }

    public boolean commit(long l, long l1)
    {
        if(value == l)
        {
            value = l1;
            return true;
        } else
        {
            return false;
        }
    }

    public long preIncr()
    {
        return ++value;
    }

    public long postIncr()
    {
        return value++;
    }

    public long preDecr()
    {
        return --value;
    }

    public long postDecr()
    {
        return value--;
    }

    public long add(long l)
    {
        return value += l;
    }

    public void negate()
    {
        value = -value;
    }

    public long substruct(long l)
    {
        return value -= l;
    }

    public long devide(long l)
    {
        return value /= l;
    }

    public long multiply(long l)
    {
        return value *= l;
    }

    public long and(long l)
    {
        return value &= l;
    }

    public long or(long l)
    {
        return value |= l;
    }

    public long complement()
    {
        return value = ~value;
    }

    public long xor(long l)
    {
        return value ^= l;
    }

    public long get()
    {
        return value;
    }

    public long set(long l)
    {
        long l1 = value;
        value = l;
        return l1;
    }

    public int compareTo(Object obj)
    {
        return compareTo((BoxedInt64)obj);
    }

    public int compareTo(long l)
    {
        long l1 = get();
        return l1 != l ? l1 >= l ? 1 : -1 : 0;
    }

    public int compareTo(BoxedInt64 boxedint64)
    {
        return compareTo(boxedint64.get());
    }

    public int hashCode()
    {
        long l = get();
        return (int)(l ^ l >>> 32);
    }

    public boolean equals(Object obj)
    {
        if(obj == this)
            return true;
        if(!(obj instanceof BoxedInt64))
            return false;
        else
            return ((BoxedInt64)obj).get() == get();
    }

    public String toString()
    {
        return Long.toString(get());
    }

    private long value;
}
