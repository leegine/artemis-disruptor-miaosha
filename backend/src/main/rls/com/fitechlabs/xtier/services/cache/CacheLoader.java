// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.cache;

import java.util.Collection;
import java.util.Map;

// Referenced classes of package com.fitechlabs.xtier.services.cache:
//            CacheException

public interface CacheLoader
{

    public abstract Object load(Object obj, Object obj1)
        throws CacheException;

    public abstract Map loadAll(Collection collection, Object obj)
        throws CacheException;
}
