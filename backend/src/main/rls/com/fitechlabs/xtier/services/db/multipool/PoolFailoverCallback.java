// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.db.multipool;


public interface PoolFailoverCallback
{

    public abstract int allowFailover(int i, String s, String s1);

    public static final int RSP_OK = 0;
    public static final int RSP_RETRY = 1;
    public static final int RSP_FAIL = 2;
    public static final int EVT_VALIDATION_FAILED = 103;
    public static final int EVT_POOL_BUSY = 104;
}
