// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.cache;


public interface CacheStats
{

    public abstract long getCreateTime();

    public abstract long getLastAccessTime();

    public abstract long getReads();

    public abstract long getWrites();

    public abstract long getHits();

    public abstract long getMisses();

    public abstract float getReadRate();

    public abstract float getWriteRate();

    public abstract float getHitRate();

    public abstract float getMissRate();
}
