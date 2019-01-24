// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.utils.boxed;


public class BoxedInt16
    implements Comparable, Cloneable
{

    public BoxedInt16(short word0)
    {
        value = word0;
    }

    public Object clone()
        throws CloneNotSupportedException
    {
        return super.clone();
    }

    public short swap(BoxedInt16 boxedint16)
    {
        if(boxedint16 != this)
            set(boxedint16.set(get()));
        return value;
    }

    public boolean commit(short word0, short word1)
    {
        if(value == word0)
        {
            value = word1;
            return true;
        } else
        {
            return false;
        }
    }

    public short preIncr()
    {
        return ++value;
    }

    public short postIncr()
    {
        return value++;
    }

    public short preDecr()
    {
        return --value;
    }

    public short postDecr()
    {
        return value--;
    }

    public short add(short word0)
    {
        return value += word0;
    }

    public void negate()
    {
        value = (short)(-value);
    }

    public short substruct(short word0)
    {
        return value -= word0;
    }

    public short devide(short word0)
    {
        return value /= word0;
    }

    public short multiply(short word0)
    {
        return value *= word0;
    }

    public short and(short word0)
    {
        return value &= word0;
    }

    public short or(short word0)
    {
        return value |= word0;
    }

    public short complement()
    {
        return value = (short)(~value);
    }

    public short xor(short word0)
    {
        return value ^= word0;
    }

    public short get()
    {
        return value;
    }

    public short set(short word0)
    {
        short word1 = value;
        value = word0;
        return word1;
    }

    public int compareTo(Object obj)
    {
        return compareTo((BoxedInt16)obj);
    }

    public int compareTo(short word0)
    {
        short word1 = get();
        return word1 != word0 ? word1 >= word0 ? 1 : -1 : 0;
    }

    public int compareTo(BoxedInt16 boxedint16)
    {
        return compareTo(boxedint16.get());
    }

    public int hashCode()
    {
        return get();
    }

    public boolean equals(Object obj)
    {
        if(obj == this)
            return true;
        if(!(obj instanceof BoxedInt16))
            return false;
        else
            return ((BoxedInt16)obj).get() == get();
    }

    public String toString()
    {
        return Short.toString(get());
    }

    private short value;
}
