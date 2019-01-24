// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.cache;

import java.util.Set;

// Referenced classes of package com.fitechlabs.xtier.services.cache:
//            CacheException, CacheEntry

public interface CacheTopology
{

    public abstract Set getNodes(CacheEntry cacheentry, Object obj)
        throws CacheException;

    public abstract Set getNodes(long l, Object obj)
        throws CacheException;
}
