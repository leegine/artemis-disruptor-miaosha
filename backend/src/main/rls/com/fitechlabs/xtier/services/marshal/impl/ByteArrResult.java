// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.marshal.impl;


class ByteArrResult
{

    ByteArrResult(int i, Object obj1)
    {
        off = i;
        obj = obj1;
    }

    int getOffset()
    {
        return off;
    }

    Object getObject()
    {
        return obj;
    }

    public String toString()
    {
        if(!$assertionsDisabled)
            throw new AssertionError();
        else
            return "Byte array result [off=" + off + ", obj=" + obj + ']';
    }

    static Class _mthclass$(String s)
    {
        try
        {
            return Class.forName(s);
        }
        catch(ClassNotFoundException classnotfoundexception)
        {
            throw (new NoClassDefFoundError()).initCause(classnotfoundexception);
        }
    }

    private int off;
    private Object obj;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(ByteArrResult.class).desiredAssertionStatus();
    }
}
