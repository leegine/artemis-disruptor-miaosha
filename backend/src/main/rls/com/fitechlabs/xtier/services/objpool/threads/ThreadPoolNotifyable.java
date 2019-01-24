// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.objpool.threads;


// Referenced classes of package com.fitechlabs.xtier.services.objpool.threads:
//            ThreadPool

public interface ThreadPoolNotifyable
{

    public abstract void onTaskEnqueued(ThreadPool threadpool);

    public abstract void onTaskStarted(ThreadPool threadpool);

    public abstract void onTaskFinished(ThreadPool threadpool);
}
