// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.kernel.ioc;

import com.fitechlabs.xtier.l10n.L10n;

public class IocArg
{

    public IocArg()
    {
        type = null;
        value = null;
        refUid = null;
    }

    public String getRefUid()
    {
        return refUid;
    }

    public Class getType()
    {
        return type;
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object obj)
    {
        value = obj;
    }

    public void setType(Class class1)
    {
        type = class1;
    }

    public void setRefUid(String s)
    {
        refUid = s;
    }

    public String toString()
    {
        return L10n.format("KRNL.IOC.TXT3", type, value, refUid);
    }

    private Class type;
    private Object value;
    private String refUid;
}
