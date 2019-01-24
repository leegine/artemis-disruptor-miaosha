// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.utils.boxed.sync;


abstract class BoxedVarSync
{

    BoxedVarSync()
    {
        mutex = new Object();
    }

    public Object getMutex()
    {
        return mutex;
    }

    protected Object mutex;
}
