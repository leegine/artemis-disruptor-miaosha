// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.cache;

import java.util.Map;

// Referenced classes of package com.fitechlabs.xtier.services.cache:
//            CacheKeyAttrs, CacheStats

public interface CacheEntry
    extends Map.Entry
{

    public abstract Object getKey();

    public abstract Object getValue();

    public abstract boolean isRemoved();

    public abstract Object setValue(Object obj);

    public abstract CacheKeyAttrs getKeyAttrs();

    public abstract CacheStats getStats();
}
