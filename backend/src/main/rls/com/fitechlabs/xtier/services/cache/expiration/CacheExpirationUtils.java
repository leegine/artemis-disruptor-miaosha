// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.cache.expiration;


class CacheExpirationUtils
{

    CacheExpirationUtils()
    {
    }

    static String getEventIdStr(int i)
    {
        switch(i)
        {
        case 1: // '\001'
            return "LOCAL_OP_GET";

        case 2: // '\002'
            return "LOCAL_OP_PUT";

        case 3: // '\003'
            return "LOCAL_OP_REMOVE";

        case 5: // '\005'
            return "REMOTE_OP_GET";

        case 6: // '\006'
            return "REMOTE_OP_PUT";

        case 7: // '\007'
            return "REMOTE_OP_REMOVE";
        }
        if(!$assertionsDisabled)
            throw new AssertionError();
        else
            return null;
    }

    static int compareIds(Object obj, Object obj1)
    {
        return System.identityHashCode(obj) >= System.identityHashCode(obj1) ? 1 : -1;
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
        $assertionsDisabled = !(CacheExpirationUtils.class).desiredAssertionStatus();
    }
}
