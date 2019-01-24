// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.cache.impl.pools;

import com.fitechlabs.xtier.services.objpool.ObjectPoolException;
import com.fitechlabs.xtier.services.objpool.ObjectPoolFactory;
import java.nio.ByteBuffer;

// Referenced classes of package com.fitechlabs.xtier.services.cache.impl.pools:
//            CacheByteBuffer

public class CacheByteBufferFactory
    implements ObjectPoolFactory
{

    public CacheByteBufferFactory(int i)
    {
        size = i;
    }

    public Object createObj()
        throws ObjectPoolException
    {
        return new CacheByteBuffer(ByteBuffer.allocateDirect(size));
    }

    public void disposeObj(Object obj)
        throws ObjectPoolException
    {
    }

    private final int size;
}
