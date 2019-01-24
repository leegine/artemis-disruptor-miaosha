// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.objpool.adapters;

import com.fitechlabs.xtier.services.objpool.ObjectPoolException;
import com.fitechlabs.xtier.services.objpool.ObjectPoolFactory;

public abstract class ObjectPoolFactoryAbstractAdapter
    implements ObjectPoolFactory
{

    public ObjectPoolFactoryAbstractAdapter()
    {
    }

    public void disposeObj(Object obj)
        throws ObjectPoolException
    {
    }
}
