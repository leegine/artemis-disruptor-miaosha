// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.objpool;


public interface ObjectPoolStats
{

    public abstract long getCreateTime();

    public abstract int getSize();

    public abstract int getInitialSize();

    public abstract int getFree();

    public abstract int getAcquired();

    public abstract int getMisses();

    public abstract int getHits();

    public abstract float getAvgMisses();

    public abstract float getAvgFree();

    public abstract float getAvgAcquired();

    public abstract float getAvgHits();

    public abstract long getLastAccessTime();

    public abstract void reset();
}
