// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.cache;

import java.util.Collection;
import java.util.Map;

// Referenced classes of package com.fitechlabs.xtier.services.cache:
//            CacheLoader, CacheException

public interface CacheStore
    extends CacheLoader
{

    public abstract void store(Object obj, Object obj1, Object obj2)
        throws CacheException;

    public abstract void storeAll(Map map, Object obj)
        throws CacheException;

    public abstract void remove(Object obj, Object obj1)
        throws CacheException;

    public abstract void removeAll(Collection collection, Object obj)
        throws CacheException;
}
