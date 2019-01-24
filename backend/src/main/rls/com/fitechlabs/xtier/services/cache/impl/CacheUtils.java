// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.cache.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.cache.CacheException;
import com.fitechlabs.xtier.services.cache.CacheKeyAttrs;
import com.fitechlabs.xtier.services.objpool.ObjectPool;
import com.fitechlabs.xtier.services.objpool.ObjectPoolException;
import com.fitechlabs.xtier.services.objpool.adapters.PoolObjectAbstractAdapter;

// Referenced classes of package com.fitechlabs.xtier.services.cache.impl:
//            CacheEntryImpl, CacheBucket, CacheGroup, CacheObject

public class CacheUtils
{

    public CacheUtils()
    {
    }

    public static String getIsolationStr(int i)
    {
        switch(i)
        {
        case 1: // '\001'
            return "READ_COMMITTED";

        case 2: // '\002'
            return "SERIALIZABLE";
        }
        if(!$assertionsDisabled)
            throw new AssertionError();
        else
            return null;
    }

    public static String getEnlistStr(byte byte0)
    {
        switch(byte0)
        {
        case 1: // '\001'
            return "GET";

        case 2: // '\002'
            return "PUT";

        case 3: // '\003'
            return "REMOVE";
        }
        if(!$assertionsDisabled)
            throw new AssertionError();
        else
            return null;
    }

    public static String getOpStr(byte byte0)
    {
        switch(byte0)
        {
        case -1:
            return "NONE";

        case 1: // '\001'
            return "GET";

        case 2: // '\002'
            return "PUT";

        case 3: // '\003'
            return "REMOVE";

        case 4: // '\004'
            return "GRP_INV";
        }
        if(!$assertionsDisabled)
            throw new AssertionError();
        else
            return null;
    }

    public static String getTxStateStr(int i)
    {
        switch(i)
        {
        case 10: // '\n'
            return "TX_ACTIVE";

        case 12: // '\f'
            return "TX_COMMMITTED";

        case 11: // '\013'
            return "TX_COMMITING";

        case 16: // '\020'
            return "TX_PREPARED";

        case 15: // '\017'
            return "TX_PREPARING";

        case 14: // '\016'
            return "TX_ROLLED_BACK";

        case 13: // '\r'
            return "TX_ROLLING_BACK";

        case 17: // '\021'
            return "TX_MARKED_ROLLBACK";

        case 18: // '\022'
            return "TX_HEURISTIC";
        }
        if(!$assertionsDisabled)
            throw new AssertionError("Unknown cache tx state: " + i);
        else
            return null;
    }

    public static int getEventId(byte byte0, boolean flag)
    {
        switch(byte0)
        {
        case 1: // '\001'
            return !flag ? 1 : 5;

        case 2: // '\002'
            return !flag ? 2 : 6;

        case 3: // '\003'
            return !flag ? 3 : 7;
        }
        if(!$assertionsDisabled)
            throw new AssertionError();
        else
            return -1;
    }

    public static boolean equals(CacheKeyAttrs cachekeyattrs, CacheKeyAttrs cachekeyattrs1)
    {
        if(!$assertionsDisabled && cachekeyattrs == null)
            throw new AssertionError();
        if(!$assertionsDisabled && cachekeyattrs1 == null)
            throw new AssertionError();
        else
            return cachekeyattrs.getTypeId() == cachekeyattrs1.getTypeId() && cachekeyattrs.getGroupId() == cachekeyattrs1.getGroupId() && cachekeyattrs.isDepended() == cachekeyattrs1.isDepended();
    }

    public static boolean isPreparedState(int i)
    {
        return i == 16 || i == 11;
    }

    public static boolean isRemovable(CacheObject cacheobject)
    {
        return cacheobject.getType() != 2 ? cacheobject.getType() != 3 ? isRemovable((CacheGroup)cacheobject) : isRemovable((CacheBucket)cacheobject) : isRemovable((CacheEntryImpl)cacheobject);
    }

    public static boolean isRemovable(CacheEntryImpl cacheentryimpl)
    {
        return !cacheentryimpl.isRemoved() && cacheentryimpl.isPendingRemove() && !cacheentryimpl.hasTx();
    }

    public static boolean isRemovable(CacheBucket cachebucket)
    {
        return !cachebucket.isRemoved() && !cachebucket.hasTx() && cachebucket.isEmpty();
    }

    public static boolean isRemovable(CacheGroup cachegroup)
    {
        return !cachegroup.isRemoved() && !cachegroup.hasTx() && cachegroup.isEmpty();
    }

    public static PoolObjectAbstractAdapter aquire(ObjectPool objectpool)
        throws CacheException
    {
        try
        {
            return (PoolObjectAbstractAdapter)objectpool.acquire();
        }
        catch(ObjectPoolException objectpoolexception)
        {
            throw new CacheException(L10n.format("SRVC.CACHE.ERR43"), objectpoolexception);
        }
    }

    public static void release(PoolObjectAbstractAdapter poolobjectabstractadapter)
        throws CacheException
    {
        try
        {
            poolobjectabstractadapter.release();
        }
        catch(ObjectPoolException objectpoolexception)
        {
            throw new CacheException(L10n.format("SRVC.CACHE.ERR42"), objectpoolexception);
        }
    }

    public static void invalidate(PoolObjectAbstractAdapter poolobjectabstractadapter)
    {
        try
        {
            poolobjectabstractadapter.invalidate();
        }
        catch(ObjectPoolException objectpoolexception) { }
    }

    static Class _mthclass$(String s)
    {
        try
        {
            return Class.forName(s);
        }
        catch(ClassNotFoundException classnotfoundexception)
        {
            throw (new NoClassDefFoundError()).initCause(classnotfoundexception);
        }
    }

    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(CacheUtils.class).desiredAssertionStatus();
    }
}
