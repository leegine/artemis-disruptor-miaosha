// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.objpool;

import java.util.List;

// Referenced classes of package com.fitechlabs.xtier.services.objpool:
//            ObjectPoolException, ObjectPoolResizePolicy, ObjectPoolFactory, ObjectPoolStats, 
//            ObjectPoolListener

public interface ObjectPool
{

    public abstract String getName();

    public abstract int getSize();

    public abstract int getFree();

    public abstract int getAcquired();

    public abstract void resize(int i)
        throws ObjectPoolException;

    public abstract void resize(float f)
        throws ObjectPoolException;

    public abstract Object acquire()
        throws ObjectPoolException;

    public abstract Object acquireWait()
        throws ObjectPoolException;

    public abstract Object acquireWait(long l)
        throws ObjectPoolException;

    public abstract void release(Object obj)
        throws ObjectPoolException;

    public abstract void invalidate(Object obj)
        throws ObjectPoolException;

    public abstract ObjectPoolResizePolicy getResizePolicy();

    public abstract ObjectPoolFactory getFactory();

    public abstract boolean isLazy();

    public abstract ObjectPoolStats getStats();

    public abstract boolean addListener(ObjectPoolListener objectpoollistener);

    public abstract boolean removeListener(ObjectPoolListener objectpoollistener);

    public abstract List getAllListeners();
}
