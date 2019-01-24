// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.utils.boxed;


public class BoxedFloat32
    implements Comparable, Cloneable
{

    public BoxedFloat32(float f)
    {
        value = f;
    }

    public Object clone()
        throws CloneNotSupportedException
    {
        return super.clone();
    }

    public float swap(BoxedFloat32 boxedfloat32)
    {
        if(boxedfloat32 != this)
            set(boxedfloat32.set(get()));
        return value;
    }

    public boolean commit(float f, float f1)
    {
        if(value == f)
        {
            value = f1;
            return true;
        } else
        {
            return false;
        }
    }

    public float preIncr()
    {
        return ++value;
    }

    public float postIncr()
    {
        return value++;
    }

    public float preDecr()
    {
        return --value;
    }

    public float postDecr()
    {
        return value--;
    }

    public float add(float f)
    {
        return value += f;
    }

    public void negate()
    {
        value = -value;
    }

    public float substruct(float f)
    {
        return value -= f;
    }

    public float devide(float f)
    {
        return value /= f;
    }

    public float multiply(float f)
    {
        return value *= f;
    }

    public float get()
    {
        return value;
    }

    public float set(float f)
    {
        float f1 = value;
        value = f;
        return f1;
    }

    public int compareTo(Object obj)
    {
        return compareTo((BoxedFloat32)obj);
    }

    public int compareTo(float f)
    {
        float f1 = get();
        return f1 != f ? f1 >= f ? 1 : -1 : 0;
    }

    public int compareTo(BoxedFloat32 boxedfloat32)
    {
        return compareTo(boxedfloat32.get());
    }

    public int hashCode()
    {
        return Float.floatToIntBits(get());
    }

    public boolean equals(Object obj)
    {
        if(obj == this)
            return true;
        if(!(obj instanceof BoxedFloat32))
            return false;
        else
            return ((BoxedFloat32)obj).get() == get();
    }

    public String toString()
    {
        return Float.toString(get());
    }

    private float value;
}
