// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.utils.boxed;


public class BoxedFloat64
    implements Comparable, Cloneable
{

    public BoxedFloat64(double d)
    {
        value = d;
    }

    public Object clone()
        throws CloneNotSupportedException
    {
        return super.clone();
    }

    public double swap(BoxedFloat64 boxedfloat64)
    {
        if(boxedfloat64 != this)
            set(boxedfloat64.set(get()));
        return value;
    }

    public boolean commit(double d, double d1)
    {
        if(value == d)
        {
            value = d1;
            return true;
        } else
        {
            return false;
        }
    }

    public double preIncr()
    {
        return ++value;
    }

    public double postIncr()
    {
        return value++;
    }

    public double preDecr()
    {
        return --value;
    }

    public double postDecr()
    {
        return value--;
    }

    public double add(double d)
    {
        return value += d;
    }

    public void negate()
    {
        value = -value;
    }

    public double substruct(double d)
    {
        return value -= d;
    }

    public double devide(double d)
    {
        return value /= d;
    }

    public double multiply(double d)
    {
        return value *= d;
    }

    public double get()
    {
        return value;
    }

    public double set(double d)
    {
        double d1 = value;
        value = d;
        return d1;
    }

    public int compareTo(Object obj)
    {
        return compareTo((BoxedFloat64)obj);
    }

    public int compareTo(double d)
    {
        double d1 = get();
        return d1 != d ? d1 >= d ? 1 : -1 : 0;
    }

    public int compareTo(BoxedFloat64 boxedfloat64)
    {
        return compareTo(boxedfloat64.get());
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
        if(!(obj instanceof BoxedFloat64))
            return false;
        else
            return ((BoxedFloat64)obj).get() == get();
    }

    public String toString()
    {
        return Double.toString(get());
    }

    private double value;
}
