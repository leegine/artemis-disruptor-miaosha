// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.objpool;


// Referenced classes of package com.fitechlabs.xtier.services.objpool:
//            ObjectPoolException, ObjectPool

public interface ObjectPoolListener
{

    public abstract void onAcquired(ObjectPool objectpool, Object obj)
        throws ObjectPoolException;

    public abstract void onReleased(ObjectPool objectpool, Object obj)
        throws ObjectPoolException;

    public abstract void onInvalidated(ObjectPool objectpool, Object obj)
        throws ObjectPoolException;
}
