// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.cache.impl;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.cache.CacheException;
import com.fitechlabs.xtier.services.cluster.ClusterNode;
import com.fitechlabs.xtier.services.cluster.ClusterService;
import com.fitechlabs.xtier.utils.TimeoutTask;
import com.fitechlabs.xtier.utils.boxed.sync.BoxedInt32Sync;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.cache.impl:
//            CacheTxImpl, CacheRegistry, CacheEventManager

public abstract class CacheTxManager
{

    protected CacheTxManager(CacheRegistry cacheregistry)
    {
        localNodeId = XtierKernel.getInstance().cluster().getLocalNode().getNodeId();
        xidCount = new BoxedInt32Sync(0x80000000);
        localThreadMap = new HashMap();
        localXidMap = new HashMap();
        remoteXidMap = new HashMap();
        if(!$assertionsDisabled && cacheregistry == null)
        {
            throw new AssertionError();
        } else
        {
            registry = cacheregistry;
            return;
        }
    }

    public void start()
    {
        timer = new Timer();
    }

    void stop()
    {
        timer.cancel();
    }

    public Long createXid()
    {
        return new Long(((long)localNodeId & 0xffffffffL) << 32 | (long)xidCount.postIncr() & 0xffffffffL);
    }

    public int extractNodeId(long l)
    {
        return (int)(l >>> 32);
    }

    public void resubscribe(CacheTxImpl cachetximpl)
    {
        cachetximpl.resetTimeout();
        if(!cachetximpl.isRemote())
        {
            cachetximpl.resetDeadlockTimeout();
            timer.schedule(cachetximpl.getDeadlockTimeoutTask(), cachetximpl.getDeadlockTimeout());
        }
        timer.schedule(cachetximpl.getTimeoutTask(), cachetximpl.getTimeout());
    }

    public void unsubscribe(CacheTxImpl cachetximpl)
    {
        cachetximpl.getTimeoutTask().cancel();
    }

    protected abstract CacheTxImpl createLocalTx(int i);

    protected Timer getTimer()
    {
        return timer;
    }

    public CacheTxImpl startLocalTx(int i)
        throws CacheException
    {
        Thread thread = Thread.currentThread();
        CacheTxImpl cachetximpl = createLocalTx(i);
        registry.getEventMgr().onStarted(cachetximpl);
        synchronized(mutex)
        {
            if(localThreadMap.containsKey(thread))
                throw new IllegalStateException(L10n.format("SRVC.CACHE.ERR45"));
            if(!$assertionsDisabled && cachetximpl.isRemote())
                throw new AssertionError();
            localThreadMap.put(thread, cachetximpl);
            localXidMap.put(cachetximpl.getBoxedXid(), cachetximpl);
        }
        cachetximpl.onStart();
        timer.schedule(cachetximpl.getDeadlockTimeoutTask(), cachetximpl.getDeadlockTimeout());
        timer.schedule(cachetximpl.getTimeoutTask(), cachetximpl.getTimeout());
        return cachetximpl;
    }

    public void onRemoteStart(CacheTxImpl cachetximpl)
    {
        if(!$assertionsDisabled && !cachetximpl.isRemote())
            throw new AssertionError();
        Long long1 = cachetximpl.getBoxedXid();
        synchronized(mutex)
        {
            if(!$assertionsDisabled && hasRemoteTx(long1))
                throw new AssertionError("Remote transaction already exists");
            if(!$assertionsDisabled && hasLocalTx(long1))
                throw new AssertionError("Remote and local XIDs matched: " + long1);
            remoteXidMap.put(long1, cachetximpl);
        }
    }

    public void removeRemoteTx(Long long1)
    {
        if(!$assertionsDisabled && long1 == null)
            throw new AssertionError();
        synchronized(mutex)
        {
            CacheTxImpl cachetximpl = (CacheTxImpl)remoteXidMap.remove(long1);
            if(!$assertionsDisabled && cachetximpl == null)
                throw new AssertionError("There is no remote tx with given xid: " + long1);
        }
    }

    public void removeLocalTx(Long long1)
    {
        synchronized(mutex)
        {
            CacheTxImpl cachetximpl = (CacheTxImpl)localXidMap.remove(long1);
            if(!$assertionsDisabled && cachetximpl == null)
                throw new AssertionError("Local trransaction does not exist: " + long1);
            cachetximpl = (CacheTxImpl)localThreadMap.remove(cachetximpl.getStartThread());
            if(!$assertionsDisabled && cachetximpl == null)
                throw new AssertionError("Local thread map does not have transaction: " + long1);
        }
    }

    public CacheTxImpl[] getRemoteTxSnapshot()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return (CacheTxImpl[])remoteXidMap.values().toArray(new CacheTxImpl[remoteXidMap.size()]);
        Exception exception;
        exception;
        throw exception;
    }

    public Set getLocalTxSnapshot()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return new HashSet(localXidMap.values());
        Exception exception;
        exception;
        throw exception;
    }

    public CacheTxImpl getLocalTx(Long long1)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return (CacheTxImpl)localXidMap.get(long1);
        Exception exception;
        exception;
        throw exception;
    }

    public boolean hasLocalTx(Long long1)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return localXidMap.containsKey(long1);
        Exception exception;
        exception;
        throw exception;
    }

    public CacheTxImpl getRemoteTx(Long long1)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return (CacheTxImpl)remoteXidMap.get(long1);
        Exception exception;
        exception;
        throw exception;
    }

    public boolean hasRemoteTx(Long long1)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return remoteXidMap.containsKey(long1);
        Exception exception;
        exception;
        throw exception;
    }

    public CacheTxImpl getLocalTx()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        if(!$assertionsDisabled && localThreadMap.size() != localXidMap.size())
            throw new AssertionError();
        return (CacheTxImpl)localThreadMap.get(Thread.currentThread());
        Exception exception;
        exception;
        throw exception;
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

    protected CacheRegistry registry;
    private Timer timer;
    private int localNodeId;
    private BoxedInt32Sync xidCount;
    private Map localThreadMap;
    private Map localXidMap;
    private Map remoteXidMap;
    protected final Object mutex = new Object();
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(CacheTxManager.class).desiredAssertionStatus();
    }
}
