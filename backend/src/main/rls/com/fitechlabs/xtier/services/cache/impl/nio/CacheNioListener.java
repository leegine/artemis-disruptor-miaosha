// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.cache.impl.nio;

import java.nio.ByteBuffer;

public interface CacheNioListener
{

    public abstract void onReceive(ByteBuffer bytebuffer);
}
