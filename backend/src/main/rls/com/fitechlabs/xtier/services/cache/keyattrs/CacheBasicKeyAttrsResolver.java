// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.cache.keyattrs;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.cache.CacheKeyAttrs;
import com.fitechlabs.xtier.services.cache.CacheKeyAttrsResolver;

// Referenced classes of package com.fitechlabs.xtier.services.cache.keyattrs:
//            CacheKeyAttrsAdapter

public class CacheBasicKeyAttrsResolver
    implements CacheKeyAttrsResolver
{

    public CacheBasicKeyAttrsResolver()
    {
    }

    public CacheKeyAttrs resolve(Object obj, Object obj1)
    {
        return attrs;
    }

    public String toString()
    {
        return L10n.format("SRVC.CACHE.TXT16");
    }

    private final CacheKeyAttrs attrs = new CacheKeyAttrsAdapter();
}
