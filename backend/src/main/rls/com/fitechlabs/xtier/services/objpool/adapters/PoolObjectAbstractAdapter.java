// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.objpool.adapters;

import com.fitechlabs.xtier.services.objpool.*;
import com.fitechlabs.xtier.utils.boxed.sync.BoxedBooleanSync;

public abstract class PoolObjectAbstractAdapter
    implements ObjectPoolNotifyable
{

    public PoolObjectAbstractAdapter()
    {
        acquired = new BoxedBooleanSync(false);
        pool = null;
    }

    public void onAcquire(ObjectPool objectpool)
    {
        if(!$assertionsDisabled && objectpool == null)
        {
            throw new AssertionError();
        } else
        {
            pool = objectpool;
            acquired.set(true);
            return;
        }
    }

    public void onRelease(ObjectPool objectpool)
    {
        if(!$assertionsDisabled && objectpool == null)
        {
            throw new AssertionError();
        } else
        {
            acquired.set(false);
            return;
        }
    }

    public final void release()
        throws ObjectPoolException
    {
        if(!$assertionsDisabled && pool == null)
        {
            throw new AssertionError();
        } else
        {
            pool.release(this);
            return;
        }
    }

    public final void invalidate()
        throws ObjectPoolException
    {
        if(!$assertionsDisabled && pool == null)
        {
            throw new AssertionError();
        } else
        {
            pool.invalidate(this);
            return;
        }
    }

    public final boolean isAcquired()
    {
        return acquired.get();
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

    private BoxedBooleanSync acquired;
    private ObjectPool pool;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(PoolObjectAbstractAdapter.class).desiredAssertionStatus();
    }
}
