// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.cache;


public interface CacheEvents
{

    public static final int LOCAL_OP_GET = 1;
    public static final int LOCAL_OP_PUT = 2;
    public static final int LOCAL_OP_REMOVE = 3;
    public static final int REMOTE_OP_GET = 5;
    public static final int REMOTE_OP_PUT = 6;
    public static final int REMOTE_OP_REMOVE = 7;
}
