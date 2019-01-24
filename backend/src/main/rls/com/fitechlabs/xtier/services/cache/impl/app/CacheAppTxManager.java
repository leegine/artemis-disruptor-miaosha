// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.cache.impl.app;

import com.fitechlabs.xtier.services.cache.impl.*;
import com.fitechlabs.xtier.utils.FifoQueue;
import com.fitechlabs.xtier.utils.boxed.sync.BoxedInt32Sync;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.cache.impl.app:
//            CacheAppLocalTxImpl, CacheAppRegistry, CacheAppRemoteTxImpl, CacheAppRemoteTxException,
//            CacheAppPreparedSet, CacheAppTxImpl

public class CacheAppTxManager extends CacheTxManager
{

    public CacheAppTxManager(CacheAppRegistry cacheappregistry)
    {
        super(cacheappregistry);
        active = new TreeMap();
        prepared = new HashSet();
        committed = new FifoQueue();
        finished = new HashMap();
        tnc = 0L;
    }

    protected CacheTxImpl createLocalTx(int i)
    {
        return new CacheAppLocalTxImpl((CacheAppRegistry)registry, i);
    }

    CacheAppRemoteTxImpl startRemoteTx(long l, int i, long l1)
        throws CacheAppRemoteTxException
    {
        Long long1 = new Long(l);
        CacheAppRemoteTxImpl cacheappremotetximpl = new CacheAppRemoteTxImpl((CacheAppRegistry)registry, long1, i, l1);
        synchronized(mutex)
        {
            if(finished.containsKey(long1))
                throw new CacheAppRemoteTxException();
            if(hasRemoteTx(long1))
                throw new CacheAppRemoteTxException();
            super.onRemoteStart(cacheappremotetximpl);
        }
        cacheappremotetximpl.onStart();
        getTimer().schedule(cacheappremotetximpl.getTimeoutTask(), cacheappremotetximpl.getTimeout());
        return cacheappremotetximpl;
    }

    CacheAppRemoteTxImpl getRemoteTx(Long long1, long l)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        CacheAppRemoteTxImpl cacheappremotetximpl = (CacheAppRemoteTxImpl)getRemoteTx(long1);
        if(cacheappremotetximpl != null)
            return cacheappremotetximpl;
        addFinishedXid(long1, l);
        null;
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    Long getStartTnc()
    {
        Object obj = valLock;
        JVM INSTR monitorenter ;
        Long long1 = new Long(tnc);
        registerStartTnc(long1);
        return long1;
        Exception exception;
        exception;
        throw exception;
    }

    void onCommit(CacheAppTxImpl cacheapptximpl)
    {
        synchronized(valLock)
        {
            deregisterStartTnc(cacheapptximpl.getStartTnc());
            if(!cacheapptximpl.isReadOnly())
            {
                prepared.remove(cacheapptximpl);
                cacheapptximpl.setEndTnc(++tnc);
                committed.add(cacheapptximpl);
            }
        }
        unsubscribe(cacheapptximpl);
    }

    void onRollback(CacheAppTxImpl cacheapptximpl)
    {
        synchronized(valLock)
        {
            deregisterStartTnc(cacheapptximpl.getStartTnc());
            prepared.remove(cacheapptximpl);
        }
        unsubscribe(cacheapptximpl);
    }

    void removeTx(CacheAppTxImpl cacheapptximpl)
    {
        registry.getEventMgr().dispatch();
        synchronized(mutex)
        {
            if(cacheapptximpl.isRemote())
            {
                removeRemoteTx(cacheapptximpl.getBoxedXid());
                addFinishedXid(cacheapptximpl.getBoxedXid(), ((CacheAppRemoteTxImpl)cacheapptximpl).getTimespan());
            } else
            {
                removeLocalTx(cacheapptximpl.getBoxedXid());
            }
        }
    }

    CacheAppPreparedSet getPreparedSet(CacheAppTxImpl cacheapptximpl)
    {
        Object obj = valLock;
        JVM INSTR monitorenter ;
        if(!$assertionsDisabled && cacheapptximpl == null)
            throw new AssertionError();
        if(cacheapptximpl.isReadOnly())
            return new CacheAppPreparedSet(tnc, !prepared.isEmpty() ? ((Set) (new HashSet(prepared))) : null);
        CacheAppPreparedSet cacheapppreparedset;
        cacheapppreparedset = new CacheAppPreparedSet(tnc, !prepared.isEmpty() ? ((Set) (new HashSet(prepared))) : null);
        prepared.add(cacheapptximpl);
        cacheapppreparedset;
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    CacheAppTxImpl[] getCommittedArr(long l, long l1)
    {
        if(!$assertionsDisabled && l > l1)
            throw new AssertionError("Invalid low and/or high tnc [low=" + l + ", high=" + l1 + ']');
        Object obj = valLock;
        JVM INSTR monitorenter ;
        if(l == l1)
            return null;
        CacheAppTxImpl acacheapptximpl[];
        if(!$assertionsDisabled && committed.isEmpty())
            throw new AssertionError("Invalid queue state [low=" + l + ", high=" + l1 + ']');
        long l2 = ((CacheAppTxImpl)committed.peek()).getEndTnc();
        if(!$assertionsDisabled && l < l2 - 1L)
            throw new AssertionError("Invalid low tnc [low=" + l + ", min-tnc=" + l2 + ']');
        if(!$assertionsDisabled && l1 > l2 + (long)committed.getCount())
            throw new AssertionError("Invalid high tnc [high=" + l1 + ", min-tnc=" + l2 + ", count=" + committed.getCount() + ']');
        acacheapptximpl = new CacheAppTxImpl[(int)(l1 - l)];
        int i = 0;
        int j = (int)((l - l2) + 1L);
        for(; i < acacheapptximpl.length; i++)
            acacheapptximpl[i] = (CacheAppTxImpl)committed.peek(j + i);

        acacheapptximpl;
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    private void addFinishedXid(Long long1, long l)
    {
        if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            throw new AssertionError();
        long l1 = System.currentTimeMillis();
        long l2 = l1 + l;
        if(nextCleanup == 0L)
            nextCleanup = l2;
        if(!finished.containsKey(long1))
        {
            if(nextCleanup > l2)
                nextCleanup = l2;
            finished.put(long1, new Long(l2));
        }
        if(nextCleanup <= l1)
        {
            Iterator iterator = finished.entrySet().iterator();
            do
            {
                if(!iterator.hasNext())
                    break;
                if(((Long)((Map.Entry)iterator.next()).getValue()).longValue() <= l1)
                    iterator.remove();
            } while(true);
        }
    }

    private void registerStartTnc(Long long1)
    {
        if(!$assertionsDisabled && !Thread.holdsLock(valLock))
            throw new AssertionError();
        BoxedInt32Sync boxedint32sync = (BoxedInt32Sync)active.get(long1);
        if(boxedint32sync == null)
            active.put(long1, new BoxedInt32Sync(1));
        else
            boxedint32sync.preIncr();
    }

    private void deregisterStartTnc(Long long1)
    {
        if(!$assertionsDisabled && !Thread.holdsLock(valLock))
            throw new AssertionError();
        BoxedInt32Sync boxedint32sync = (BoxedInt32Sync)active.get(long1);
        if(!$assertionsDisabled && boxedint32sync == null)
            throw new AssertionError("Attempted to deregister non-existent start tnc: " + long1);
        if(!$assertionsDisabled && boxedint32sync.get() <= 0)
            throw new AssertionError();
        if(boxedint32sync.preDecr() == 0)
        {
            active.remove(long1);
            adjustCommitted();
        }
    }

    private void adjustCommitted()
    {
        if(!$assertionsDisabled && !Thread.holdsLock(valLock))
            throw new AssertionError();
        if(active.isEmpty())
        {
            committed.clear();
        } else
        {
            for(long l = ((Long)active.firstKey()).longValue(); !committed.isEmpty() && ((CacheAppTxImpl)committed.peek()).getEndTnc() <= l; committed.get());
        }
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

    private static final int DEBUG_MAX_SIZE = 1000;
    private SortedMap active;
    private Set prepared;
    private FifoQueue committed;
    private Map finished;
    private long nextCleanup;
    private long tnc;
    private final Object valLock = new Object();
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(CacheAppTxManager.class).desiredAssertionStatus();
    }
}
