// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.marshal.impl;


class ArrayInfo
{

    private ArrayInfo(Class class1, int i)
    {
        comp = class1;
        level = i;
    }

    Class getComponent()
    {
        return comp;
    }

    int getLevel()
    {
        return level;
    }

    static ArrayInfo getInstance(Object obj)
    {
        if(!$assertionsDisabled && obj == null)
            throw new AssertionError();
        else
            return getInstance(obj.getClass(), 1);
    }

    private static ArrayInfo getInstance(Class class1, int i)
    {
        return !class1.isArray() ? new ArrayInfo(class1, i) : getInstance(class1.getComponentType(), i + 1);
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

    private Class comp;
    private int level;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(ArrayInfo.class).desiredAssertionStatus();
    }
}
