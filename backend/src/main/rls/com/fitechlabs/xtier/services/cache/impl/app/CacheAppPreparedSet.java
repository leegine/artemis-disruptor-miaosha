// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.cache.impl.app;

import java.util.Set;

class CacheAppPreparedSet
{

    CacheAppPreparedSet(long l, Set set)
    {
        tnc = l;
        activeTxSet = set;
    }

    Set getActiveTxSet()
    {
        return activeTxSet;
    }

    long getTnc()
    {
        return tnc;
    }

    public String toString()
    {
        if(!$assertionsDisabled)
            throw new AssertionError();
        else
            return "Cache prepared set [active-size=" + (activeTxSet != null ? activeTxSet.size() : 0) + ", tnc=" + tnc + ']';
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

    private Set activeTxSet;
    private long tnc;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(CacheAppPreparedSet.class).desiredAssertionStatus();
    }
}
