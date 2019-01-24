// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.objpool.adapters;


// Referenced classes of package com.fitechlabs.xtier.services.objpool.adapters:
//            PoolObjectAbstractAdapter

public class PoolObjectAggrAdapter extends PoolObjectAbstractAdapter
{

    public PoolObjectAggrAdapter()
    {
        obj = null;
    }

    public final void setObject(Object obj1)
    {
        obj = obj1;
    }

    public final Object getObject()
    {
        return obj;
    }

    private Object obj;
}
