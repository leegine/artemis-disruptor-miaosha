// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.cache.impl.pools;

import com.fitechlabs.xtier.services.objpool.ObjectPool;
import com.fitechlabs.xtier.services.objpool.adapters.PoolObjectAbstractAdapter;
import java.nio.ByteBuffer;

public class CacheByteBuffer extends PoolObjectAbstractAdapter
{

    CacheByteBuffer(ByteBuffer bytebuffer)
    {
        if(!$assertionsDisabled && bytebuffer == null)
            throw new AssertionError();
        if(!$assertionsDisabled && bytebuffer.capacity() <= 0)
        {
            throw new AssertionError();
        } else
        {
            buf = bytebuffer;
            return;
        }
    }

    public ByteBuffer getBuf()
    {
        return buf;
    }

    public byte[] getBytes()
    {
        return buf.array();
    }

    public void onAcquire(ObjectPool objectpool)
    {
        super.onAcquire(objectpool);
        buf.clear();
    }

    static Class _mthclass$(String s)
    {
        try
        {
            return Class.forName(s);
        }
        catch(ClassNotFoundException classnotfoundexception)
        {
            throw (new NoClassDefFoundError()).initCause(classnotfoundexception);
        }
    }

    private ByteBuffer buf;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(CacheByteBuffer.class).desiredAssertionStatus();
    }
}
