// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.cache.impl;

import com.fitechlabs.xtier.services.cache.CacheKeyAttrs;

// Referenced classes of package com.fitechlabs.xtier.services.cache.impl:
//            CacheGroup, CacheObject

public interface CacheTxObject
{

    public abstract CacheGroup getGroup();

    public abstract Object getTxKey();

    public abstract CacheKeyAttrs getKeyAttrs();

    public abstract CacheObject getObj();

    public abstract byte getOp();

    public abstract Object getUserArgs();

    public abstract void invalidate();

    public static final byte NONE = -1;
    public static final byte GET = 1;
    public static final byte PUT = 2;
    public static final byte REMOVE = 3;
    public static final byte GRP_INV = 4;
}
