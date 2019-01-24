// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.objpool;


// Referenced classes of package com.fitechlabs.xtier.services.objpool:
//            ObjectPoolException

public interface ObjectPoolFactory
{

    public abstract Object createObj()
        throws ObjectPoolException;

    public abstract void disposeObj(Object obj)
        throws ObjectPoolException;
}
