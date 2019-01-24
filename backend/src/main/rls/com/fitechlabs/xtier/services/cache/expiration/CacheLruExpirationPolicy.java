// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.cache.expiration;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.cache.*;
import com.fitechlabs.xtier.utils.ArgAssert;
import java.util.*;

public class CacheLruExpirationPolicy
    implements CacheExpirationPolicy, CacheEvents
{

    public CacheLruExpirationPolicy(int i, int j, boolean flag)
    {
        this(null, i, j, flag);
    }

    public CacheLruExpirationPolicy(String s, final int final_i, int i, boolean flag)
    {
        ArgAssert.illegalArg(final_i > 0, "initSize");
        ArgAssert.illegalArg(i >= final_i, "maxSize");
        initSize = final_i;
        maxSize = i;
        cacheName = s;
        lruMap = new LinkedHashMap(0.75F, flag, i) {

            protected boolean removeEldestEntry(Map.Entry entry)
            {
                if(size() > maxSize)
                {
                    Iterator iterator = entrySet().iterator();
                    do
                    {
                        if(size() <= maxSize || !iterator.hasNext())
                            break;
                        CacheEntry cacheentry = (CacheEntry)((Map.Entry)iterator.next()).getKey();
                        if(cache.expire(cacheentry.getKey(), cacheentry.getKeyAttrs()))
                            iterator.remove();
                    } while(true);
                }
                return false;
            }


            {
                super(final_i, f, flag);
            }
        }
;
    }

    public synchronized void onEntryEvent(Cache cache1, int i, CacheEntry cacheentry)
    {
        if(cache == null)
            cache = cache1;
        switch(i)
        {
        case 1: // '\001'
        case 2: // '\002'
        case 5: // '\005'
        case 6: // '\006'
            lruMap.put(cacheentry, DUMMY);
            break;

        case 3: // '\003'
        case 7: // '\007'
            lruMap.remove(cacheentry);
            break;
        }
    }

    public String toString()
    {
        return L10n.format("SRVC.CACHE.TXT11", cacheName, new Integer(initSize), new Integer(maxSize));
    }

    private static final Object DUMMY = new Object();
    private LinkedHashMap lruMap;
    private final int initSize;
    private final int maxSize;
    private final String cacheName;
    private Cache cache;


}
