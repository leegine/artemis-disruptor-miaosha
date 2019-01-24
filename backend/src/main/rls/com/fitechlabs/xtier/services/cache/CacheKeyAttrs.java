// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.cache;


public interface CacheKeyAttrs
{

    public abstract long getTypeId();

    public abstract long getGroupId();

    public abstract boolean isDepended();

    public static final long DEFAULT_GROUP_ID = 0x8000000000000000L;
    public static final long DEFAULT_TYPE_ID = 0x8000000000000000L;
}
