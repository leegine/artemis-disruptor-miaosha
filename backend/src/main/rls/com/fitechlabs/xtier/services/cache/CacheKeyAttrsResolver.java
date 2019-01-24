// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.cache;


// Referenced classes of package com.fitechlabs.xtier.services.cache:
//            CacheException, CacheKeyAttrs

public interface CacheKeyAttrsResolver
{

    public abstract CacheKeyAttrs resolve(Object obj, Object obj1)
        throws CacheException;
}
