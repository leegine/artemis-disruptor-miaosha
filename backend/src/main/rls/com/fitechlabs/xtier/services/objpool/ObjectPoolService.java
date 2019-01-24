// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.objpool;

import com.fitechlabs.xtier.kernel.KernelService;
import com.fitechlabs.xtier.services.objpool.threads.ThreadPool;
import com.fitechlabs.xtier.services.objpool.threads.ThreadPoolResizePolicy;

// Referenced classes of package com.fitechlabs.xtier.services.objpool:
//            ObjectPoolException, ObjectPoolFactory, ObjectPoolResizePolicy, ObjectPool

public interface ObjectPoolService
    extends KernelService
{

    public abstract ObjectPool createPool(String s, int i, ObjectPoolFactory objectpoolfactory, boolean flag, ObjectPoolResizePolicy objectpoolresizepolicy)
        throws ObjectPoolException;

    public abstract ObjectPool createPool(String s, ObjectPoolFactory objectpoolfactory)
        throws ObjectPoolException;

    public abstract ThreadPool createThreadPool(String s, int i, boolean flag, int j, ThreadPoolResizePolicy threadpoolresizepolicy)
        throws ObjectPoolException;

    public abstract ThreadPool createThreadPool(String s)
        throws ObjectPoolException;

    public abstract ObjectPool getPool(String s);

    public abstract ThreadPool getThreadPool(String s);

    public abstract boolean deletePool(String s)
        throws ObjectPoolException;

    public abstract boolean deleteThreadPool(String s);

    public abstract boolean deleteWaitThreadPool(String s);
}
