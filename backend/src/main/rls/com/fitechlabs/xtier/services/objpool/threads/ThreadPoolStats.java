// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.objpool.threads;


public interface ThreadPoolStats
{

    public abstract int getSize();

    public abstract int getInitialSize();

    public abstract long getCreateTime();

    public abstract int getFreeThreads();

    public abstract float getAvgFreeThreads();

    public abstract float getAvgBusyThreads();

    public abstract int getBusyThreads();

    public abstract int getBacklog();

    public abstract float getAvgBacklog();

    public abstract long getLastAccessTime();

    public abstract long getLastBacklogTime();

    public abstract long getAvgBacklogTime();

    public abstract void reset();
}
