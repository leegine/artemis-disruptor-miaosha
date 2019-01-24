// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.utils.boxed;


public class BoxedInt8
    implements Comparable, Cloneable
{

    public BoxedInt8(byte byte0)
    {
        value = byte0;
    }

    public Object clone()
        throws CloneNotSupportedException
    {
        return super.clone();
    }

    public byte swap(BoxedInt8 boxedint8)
    {
        if(boxedint8 != this)
            set(boxedint8.set(get()));
        return value;
    }

    public boolean commit(byte byte0, byte byte1)
    {
        if(value == byte0)
        {
            value = byte1;
            return true;
        } else
        {
            return false;
        }
    }

    public byte preIncr()
    {
        return ++value;
    }

    public byte postIncr()
    {
        return value++;
    }

    public byte preDecr()
    {
        return --value;
    }

    public byte postDecr()
    {
        return value--;
    }

    public byte add(byte byte0)
    {
        return value += byte0;
    }

    public void negate()
    {
        value = (byte)(-value);
    }

    public byte substruct(byte byte0)
    {
        return value -= byte0;
    }

    public byte devide(byte byte0)
    {
        return value /= byte0;
    }

    public byte multiply(byte byte0)
    {
        return value *= byte0;
    }

    public byte and(byte byte0)
    {
        return value &= byte0;
    }

    public byte or(byte byte0)
    {
        return value |= byte0;
    }

    public byte complement()
    {
        return value = (byte)(~value);
    }

    public byte xor(byte byte0)
    {
        return value ^= byte0;
    }

    public byte get()
    {
        return value;
    }

    public byte set(byte byte0)
    {
        byte byte1 = value;
        value = byte0;
        return byte1;
    }

    public int compareTo(Object obj)
    {
        return compareTo((BoxedInt8)obj);
    }

    public int compareTo(byte byte0)
    {
        byte byte1 = get();
        return byte1 != byte0 ? byte1 >= byte0 ? 1 : -1 : 0;
    }

    public int compareTo(BoxedInt8 boxedint8)
    {
        return compareTo(boxedint8.get());
    }

    public int hashCode()
    {
        return get();
    }

    public boolean equals(Object obj)
    {
        if(obj == this)
            return true;
        if(!(obj instanceof BoxedInt8))
            return false;
        else
            return ((BoxedInt8)obj).get() == get();
    }

    public String toString()
    {
        return Byte.toString(get());
    }

    private byte value;
}
