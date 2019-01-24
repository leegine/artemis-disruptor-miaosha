// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.cache.impl;

import com.fitechlabs.xtier.services.cache.CacheKeyAttrs;
import java.util.HashSet;
import java.util.Set;

// Referenced classes of package com.fitechlabs.xtier.services.cache.impl:
//            CacheObject, CacheMap, CacheBucket, CacheTxImpl

public class CacheGroup extends CacheObject
{

    CacheGroup(Long long1)
    {
        super((byte)1);
        depended = new CacheMap();
        notDepended = new CacheMap();
        memberPreparedTxSet = new HashSet();
        grpId = long1;
    }

    public Long getGroupId()
    {
        return grpId;
    }

    public boolean isEmpty()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return depended.isEmpty() && notDepended.isEmpty();
        Exception exception;
        exception;
        throw exception;
    }

    public void add(CacheBucket cachebucket)
    {
        synchronized(mutex)
        {
            getMap(cachebucket.getKeyAttrs().isDepended()).put(cachebucket);
        }
    }

    public void remove(CacheBucket cachebucket)
    {
        synchronized(mutex)
        {
            getMap(cachebucket.getKeyAttrs().isDepended()).remove(cachebucket);
        }
    }

    private CacheMap getMap(boolean flag)
    {
        return !flag ? notDepended : depended;
    }

    public CacheMap getDepended()
    {
        return depended;
    }

    public CacheMap getNotDepended()
    {
        return notDepended;
    }

    public void addMemberPreparedTx(CacheTxImpl cachetximpl)
    {
        synchronized(mutex)
        {
            memberPreparedTxSet.add(cachetximpl);
        }
    }

    public void removeMemeberPreparedTx(CacheTxImpl cachetximpl)
    {
        synchronized(mutex)
        {
            memberPreparedTxSet.remove(cachetximpl);
        }
    }

    public boolean hasMemberPreparedTx()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return !memberPreparedTxSet.isEmpty();
        Exception exception;
        exception;
        throw exception;
    }

    public boolean hasMemberPreparedTx(CacheTxImpl cachetximpl)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return memberPreparedTxSet.contains(cachetximpl);
        Exception exception;
        exception;
        throw exception;
    }

    public boolean hasOtherMemberPreparedTx(CacheTxImpl cachetximpl)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return !memberPreparedTxSet.contains(cachetximpl) ? memberPreparedTxSet.size() > 0 : memberPreparedTxSet.size() > 1;
        Exception exception;
        exception;
        throw exception;
    }

    public int hashCode()
    {
        return grpId.hashCode();
    }

    public boolean equals(Object obj)
    {
        if(obj == this)
            return true;
        if(!$assertionsDisabled && grpId.equals(((CacheGroup)obj).grpId))
            throw new AssertionError();
        else
            return false;
    }

    public String toString()
    {
        if(!$assertionsDisabled)
            throw new AssertionError();
        else
            return "Cache group [group-id=" + grpId + ", depended-count=" + depended.size() + ", not-depeded-count=" + notDepended.size() + ']';
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

    private Long grpId;
    private CacheMap depended;
    private CacheMap notDepended;
    private Set memberPreparedTxSet;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(CacheGroup.class).desiredAssertionStatus();
    }
}
