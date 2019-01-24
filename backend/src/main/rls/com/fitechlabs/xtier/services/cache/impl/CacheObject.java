// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.cache.impl;

import java.util.ArrayList;
import java.util.List;

// Referenced classes of package com.fitechlabs.xtier.services.cache.impl:
//            CacheTxImpl, CacheUtils

public abstract class CacheObject
{

    CacheObject(byte byte0)
    {
        isRemoved = false;
        txList = new ArrayList(3);
        mutex = new Object();
        if(!$assertionsDisabled && byte0 != 1 && byte0 != 2 && byte0 != 3)
        {
            throw new AssertionError("Invalid type: " + byte0);
        } else
        {
            type = byte0;
            return;
        }
    }

    public final byte getType()
    {
        return type;
    }

    public boolean isRemoved()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return isRemoved;
        Exception exception;
        exception;
        throw exception;
    }

    public void setRemoved(boolean flag)
    {
        synchronized(mutex)
        {
            isRemoved = flag;
        }
    }

    public final List getTxList()
    {
        return txList;
    }

    public final void addTx(CacheTxImpl cachetximpl)
    {
        if(!$assertionsDisabled && cachetximpl == null)
            throw new AssertionError();
        synchronized(mutex)
        {
            if(!txList.contains(cachetximpl))
                txList.add(cachetximpl);
        }
    }

    public final boolean removeTx(CacheTxImpl cachetximpl)
    {
        if(!$assertionsDisabled && cachetximpl == null)
            throw new AssertionError();
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return txList.remove(cachetximpl);
        Exception exception;
        exception;
        throw exception;
    }

    public final boolean hasTx()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return !txList.isEmpty();
        Exception exception;
        exception;
        throw exception;
    }

    public final boolean hasTx(CacheTxImpl cachetximpl)
    {
        if(!$assertionsDisabled && cachetximpl == null)
            throw new AssertionError();
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return txList.contains(cachetximpl);
        Exception exception;
        exception;
        throw exception;
    }

    public final boolean hasOtherTx(CacheTxImpl cachetximpl)
    {
        if(!$assertionsDisabled && cachetximpl == null)
            throw new AssertionError();
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return !txList.contains(cachetximpl) ? txList.size() > 0 : txList.size() > 1;
        Exception exception;
        exception;
        throw exception;
    }

    public final boolean hasPreparedTx()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
label0:
        {
            int i = txList.size();
            do
                if(i-- <= 0)
                    break label0;
            while(!CacheUtils.isPreparedState(((CacheTxImpl)txList.get(i)).getState()));
            return true;
        }
        false;
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public final boolean hasPreparedTx(CacheTxImpl cachetximpl)
    {
        if(!$assertionsDisabled && cachetximpl == null)
            throw new AssertionError();
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return CacheUtils.isPreparedState(cachetximpl.getState()) && txList.contains(cachetximpl);
        Exception exception;
        exception;
        throw exception;
    }

    public final boolean hasOtherPreparedTx(CacheTxImpl cachetximpl)
    {
        if(!$assertionsDisabled && cachetximpl == null)
            throw new AssertionError();
        Object obj = mutex;
        JVM INSTR monitorenter ;
label0:
        {
            int i = txList.size();
            CacheTxImpl cachetximpl1;
            do
            {
                if(i-- <= 0)
                    break label0;
                cachetximpl1 = (CacheTxImpl)txList.get(i);
            } while(cachetximpl1.equals(cachetximpl) || !CacheUtils.isPreparedState(cachetximpl1.getState()));
            return true;
        }
        false;
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public final Object getMutex()
    {
        return mutex;
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

    public static final byte GROUP = 1;
    public static final byte ENTRY = 2;
    public static final byte BUCKET = 3;
    private final byte type;
    private boolean isRemoved;
    private List txList;
    protected Object mutex;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(CacheObject.class).desiredAssertionStatus();
    }
}
