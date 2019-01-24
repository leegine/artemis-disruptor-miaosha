// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.cache;

import com.fitechlabs.xtier.kernel.KernelService;
import com.fitechlabs.xtier.services.cache.jcache.JCache;

// Referenced classes of package com.fitechlabs.xtier.services.cache:
//            Cache

public interface CacheService
    extends KernelService
{

    public abstract Cache getCache();

    public abstract Cache getCache(String s);

    public abstract JCache getJCache();

    public abstract JCache getJCache(String s);
}
