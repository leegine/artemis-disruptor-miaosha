// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.objpool.threads;

import com.fitechlabs.xtier.services.objpool.ObjectPoolException;
import java.util.List;

// Referenced classes of package com.fitechlabs.xtier.services.objpool.threads:
//            ThreadPoolResizePolicy, ThreadPoolListener

public interface ThreadPool
{

    public abstract String getName();

    public abstract void addTask(Runnable runnable);

    public abstract int getSize();

    public abstract int getBacklog();

    public abstract int getFreeThreads();

    public abstract int getBusyThreads();

    public abstract void resize(int i)
        throws ObjectPoolException;

    public abstract void resize(float f)
        throws ObjectPoolException;

    public abstract ThreadPoolResizePolicy getResizePolicy();

    public abstract int getPriority();

    public abstract boolean isLazy();

    public abstract void waitForTasks();

    public abstract boolean addListener(ThreadPoolListener threadpoollistener);

    public abstract boolean removeListener(ThreadPoolListener threadpoollistener);

    public abstract List getAllListeners();
}
