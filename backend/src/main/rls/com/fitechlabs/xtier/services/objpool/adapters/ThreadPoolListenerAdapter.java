// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.objpool.adapters;

import com.fitechlabs.xtier.services.objpool.threads.ThreadPool;
import com.fitechlabs.xtier.services.objpool.threads.ThreadPoolListener;

public class ThreadPoolListenerAdapter
    implements ThreadPoolListener
{

    public ThreadPoolListenerAdapter()
    {
    }

    public void onTaskEnqueued(ThreadPool threadpool, Runnable runnable)
    {
    }

    public void onTaskStarted(ThreadPool threadpool, Runnable runnable)
    {
    }

    public void onTaskFinished(ThreadPool threadpool, Runnable runnable)
    {
    }
}
