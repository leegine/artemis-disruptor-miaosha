// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.cache.impl;

import com.fitechlabs.xtier.services.cache.CacheKeyAttrs;

// Referenced classes of package com.fitechlabs.xtier.services.cache.impl:
//            CacheTxObject, CacheBucket, CacheObject, CacheGroup,
//            CacheUtils

public class CacheTxBucket
    implements CacheTxObject
{
    public static class BucketKey
    {

        public void set(int i, CacheKeyAttrs cachekeyattrs)
        {
            if(!$assertionsDisabled && cachekeyattrs == null)
            {
                throw new AssertionError();
            } else
            {
                hashCode = i;
                keyAttrs = cachekeyattrs;
                return;
            }
        }

        public int hashCode()
        {
            return hashCode;
        }

        public boolean equals(Object obj)
        {
            BucketKey bucketkey = (BucketKey)obj;
            return bucketkey.hashCode == hashCode && CacheUtils.equals(bucketkey.keyAttrs, keyAttrs);
        }

        private int hashCode;
        private CacheKeyAttrs keyAttrs;
        static final boolean $assertionsDisabled; /* synthetic field */


        public BucketKey()
        {
        }

        public BucketKey(int i, CacheKeyAttrs cachekeyattrs)
        {
            set(i, cachekeyattrs);
        }
    }


    CacheTxBucket(CacheBucket cachebucket, byte byte0)
    {
        if(!$assertionsDisabled && cachebucket == null)
        {
            throw new AssertionError();
        } else
        {
            bucket = cachebucket;
            enlistType = byte0;
            txKey = new BucketKey(cachebucket.getHashCode(), cachebucket.getKeyAttrs());
            return;
        }
    }

    public CacheObject getObj()
    {
        return bucket;
    }

    public CacheBucket getBucket()
    {
        return bucket;
    }

    public int getHashCode()
    {
        return bucket.getHashCode();
    }

    public CacheKeyAttrs getKeyAttrs()
    {
        return bucket.getKeyAttrs();
    }

    public Object getTxKey()
    {
        return txKey;
    }

    public CacheGroup getGroup()
    {
        return bucket.getGroup();
    }

    public byte getOp()
    {
        return enlistType;
    }

    public Object getUserArgs()
    {
        return null;
    }

    public void invalidate()
    {
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

    private final CacheBucket bucket;
    private final BucketKey txKey;
    private final byte enlistType;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(CacheTxBucket.class).desiredAssertionStatus();
    }
}
