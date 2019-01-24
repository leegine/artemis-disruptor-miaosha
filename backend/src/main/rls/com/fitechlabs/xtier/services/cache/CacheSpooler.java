// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.cache;

import java.util.Map;

// Referenced classes of package com.fitechlabs.xtier.services.cache:
//            CacheException, CacheKeyAttrs

public interface CacheSpooler
{

    public abstract void spool(Object obj, Object obj1, CacheKeyAttrs cachekeyattrs)
        throws CacheException;

    public abstract void remove(Object obj, CacheKeyAttrs cachekeyattrs)
        throws CacheException;

    public abstract Object load(Object obj, CacheKeyAttrs cachekeyattrs)
        throws CacheException;

    public abstract Map loadAll(Map map)
        throws CacheException;
}
