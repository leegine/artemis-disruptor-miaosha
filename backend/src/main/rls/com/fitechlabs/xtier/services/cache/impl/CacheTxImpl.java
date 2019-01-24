// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.cache.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.cache.*;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.TimeoutTask;
import com.fitechlabs.xtier.utils.boxed.sync.BoxedBooleanSync;
import java.util.HashSet;
import java.util.Set;

// Referenced classes of package com.fitechlabs.xtier.services.cache.impl:
//            CacheTxEntry, CacheTxBucket, CacheTxSet, CacheRegistry,
//            CacheTxManager, CacheEntryImpl, CacheDataManager, CacheBucket,
//            CacheTxObject, CacheGroup, CacheTxGroup

public abstract class CacheTxImpl
    implements CacheTx
{

    protected CacheTxImpl(CacheRegistry cacheregistry, Long long1, int i, long l, long l1,
            boolean flag)
    {
        readSet = null;
        writeSet = null;
        isTimedOut = new BoxedBooleanSync(false);
        isDeadlocked = new BoxedBooleanSync(false);
        txGrpSet = null;
        nodes = null;
        registry = cacheregistry;
        xid = long1;
        isolationLevel = i;
        timeout = l;
        deadlockTimeout = l1;
        isRemote = flag;
        state = 10;
    }

    protected void onStart()
    {
    }

    protected abstract void onTimeout();

    protected abstract void onDeadlockTimeout();

    public TimeoutTask getTimeoutTask()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        if(timeoutTask == null)
            timeoutTask = new TimeoutTask() {

                protected void onTimeout()
                {
                    CacheTxImpl.this.onTimeout();
                }


            {
                super();
            }
            }
;
        return timeoutTask;
        Exception exception;
        exception;
        throw exception;
    }

    protected TimeoutTask getDeadlockTimeoutTask()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        if(isRemote)
            return null;
        if(deadlockTimeoutTask == null)
            deadlockTimeoutTask = new TimeoutTask() {

                protected void onTimeout()
                {
                    onDeadlockTimeout();
                }


            {
                super();
            }
            }
;
        deadlockTimeoutTask;
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    protected void resetTimeout()
    {
        synchronized(mutex)
        {
            if(timeoutTask != null)
            {
                timeoutTask.cancel();
                timeoutTask = null;
            }
        }
    }

    protected void resetDeadlockTimeout()
    {
        synchronized(mutex)
        {
            if(deadlockTimeoutTask != null)
            {
                deadlockTimeoutTask.cancel();
                deadlockTimeoutTask = null;
            }
        }
    }

    protected long getDeadlockTimeout()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return deadlockTimeout;
        Exception exception;
        exception;
        throw exception;
    }

    public long getTimeout()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return timeout;
        Exception exception;
        exception;
        throw exception;
    }

    public void setTimeout(long l)
    {
        ArgAssert.illegalArg(l > 0L, "timeout");
        synchronized(mutex)
        {
            if(!isEmpty())
                throw new IllegalStateException(L10n.format("SRVC.CACHE.ERR33"));
            timeout = l;
        }
        registry.getTxMgr().resubscribe(this);
    }

    public boolean isRemote()
    {
        return isRemote;
    }

    public long getXid()
    {
        return xid.longValue();
    }

    public Long getBoxedXid()
    {
        return xid;
    }

    public int getIsolationLevel()
    {
        return isolationLevel;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public boolean isRollbackOnly()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return state == 17;
        Exception exception;
        exception;
        throw exception;
    }

    protected CacheRegistry getRegistry()
    {
        return registry;
    }

    protected boolean isFailureState()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return state == 13 || state == 14 || state == 17;
        Exception exception;
        exception;
        throw exception;
    }

    public int getState()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return state;
        Exception exception;
        exception;
        throw exception;
    }

    protected void setState(int i)
    {
        synchronized(mutex)
        {
            if(state != 17 || i == 13)
                state = i;
        }
    }

    public void setRollbackOnly()
    {
        synchronized(mutex)
        {
            state = 17;
        }
    }

    public Object add(CacheEntryImpl cacheentryimpl, Object obj, byte byte0, Object obj1)
        throws CacheException
    {
        if(!$assertionsDisabled && !Thread.holdsLock(cacheentryimpl.getMutex()))
            throw new AssertionError();
        if(!$assertionsDisabled && cacheentryimpl.isRemoved())
        {
            throw new AssertionError("Adding removed entry to transaction: " + cacheentryimpl);
        } else
        {
            cacheentryimpl.addTx(this);
            return add(((CacheTxObject) (new CacheTxEntry(cacheentryimpl, obj, byte0, obj1))));
        }
    }

    public void add(int i, CacheKeyAttrs cachekeyattrs, byte byte0)
    {
        CacheBucket cachebucket = null;
_L2:
label0:
        {
            cachebucket = registry.getDataMgr().getOrCreateBucket(i, cachekeyattrs);
            synchronized(cachebucket.getMutex())
            {
                if(cachebucket.isRemoved())
                    break label0;
                cachebucket.addTx(this);
            }
            break; /* Loop/switch isn't completed */
        }
        obj;
        JVM INSTR monitorexit ;
        if(true) goto _L2; else goto _L1
        exception;
        throw exception;
_L1:
        add(((CacheTxObject) (new CacheTxBucket(cachebucket, byte0))));
        return;
    }

    private Object add(CacheTxObject cachetxobject)
    {
        if(cachetxobject.getOp() == 1)
        {
            CacheTxObject cachetxobject1 = null;
            if(writeSet != null)
                cachetxobject1 = writeSet.getTxObj(cachetxobject.getTxKey());
            if(cachetxobject1 == null)
            {
                if(!$assertionsDisabled && isolationLevel != 2)
                    throw new AssertionError();
                if(readSet == null)
                    readSet = new CacheTxSet(isRemote);
                cachetxobject1 = readSet.add(cachetxobject);
                return cachetxobject1 != null ? !isRemote ? ((CacheTxEntry)cachetxobject1).getTxValue() : null : null;
            }
            if(!isRemote)
            {
                CacheTxEntry cachetxentry = (CacheTxEntry)cachetxobject1;
                Object obj1 = cachetxentry.getTxValue();
                if(!$assertionsDisabled && cachetxentry.getOp() == 3 && obj1 != null)
                {
                    throw new AssertionError();
                } else
                {
                    cachetxentry.setTxValue(((CacheTxEntry)cachetxobject).getTxValue());
                    return obj1;
                }
            } else
            {
                return null;
            }
        }
        Object obj = getOldValue(cachetxobject.getTxKey());
        Long long1 = cachetxobject.getGroup().getGroupId();
        if(readSet != null && !isRemote)
            readSet.remove(cachetxobject.getTxKey());
        if(!isRemote)
        {
            invalidateDepended(readSet, long1);
            invalidateDepended(writeSet, long1);
        }
        if(writeSet == null)
            writeSet = new CacheTxSet(isRemote);
        writeSet.add(cachetxobject);
        return obj;
    }

    private Object getOldValue(Object obj)
    {
        if(isRemote)
            return null;
        CacheTxEntry cachetxentry = null;
        if(writeSet != null)
            cachetxentry = (CacheTxEntry)writeSet.getTxObj(obj);
        if(cachetxentry == null && readSet != null)
            cachetxentry = (CacheTxEntry)readSet.getTxObj(obj);
        return cachetxentry != null ? cachetxentry.getTxValue() : null;
    }

    public void addGroup(CacheGroup cachegroup, Object obj)
    {
        if(!$assertionsDisabled && cachegroup == null)
            throw new AssertionError();
        cachegroup.addTx(this);
        Long long1 = cachegroup.getGroupId();
        if(!isRemote)
        {
            invalidate(readSet, long1);
            invalidate(writeSet, long1);
        }
        if(writeSet == null)
            writeSet = new CacheTxSet(isRemote);
        CacheTxGroup cachetxgroup = writeSet.enlist(cachegroup, obj);
        if(txGrpSet == null)
            txGrpSet = new HashSet();
        txGrpSet.add(cachetxgroup);
    }

    public CacheTxEntry getTxEntry(Object obj, CacheKeyAttrs cachekeyattrs)
    {
        CacheTxEntry cachetxentry = null;
        if(writeSet != null)
            cachetxentry = writeSet.getTxEntry(obj, cachekeyattrs);
        if(cachetxentry == null && readSet != null)
            cachetxentry = readSet.getTxEntry(obj, cachekeyattrs);
        return cachetxentry;
    }

    public CacheTxBucket getTxBucket(int i, CacheKeyAttrs cachekeyattrs)
    {
        return writeSet != null ? writeSet.getTxBucket(i, cachekeyattrs) : null;
    }

    public boolean isReadOnly()
    {
        return !hasWrites() && !hasTxGrps();
    }

    public boolean hasReads()
    {
        return readSet != null && readSet.getTxObjsCount() > 0;
    }

    public int readCount()
    {
        return readSet != null ? readSet.getTxObjsCount() : 0;
    }

    public boolean hasWrites()
    {
        return writeSet != null && writeSet.getTxObjsCount() > 0;
    }

    public int writeCount()
    {
        return writeSet != null ? writeSet.getTxObjsCount() : 0;
    }

    public boolean hasTxGrps()
    {
        return txGrpSet != null && !txGrpSet.isEmpty();
    }

    public boolean hasTxGrp(Long long1)
    {
        if(!$assertionsDisabled && long1 == null)
            throw new AssertionError();
        if(writeSet == null)
            return false;
        CacheTxGroup cachetxgroup = writeSet.getTxGroup(long1);
        if(cachetxgroup != null)
            return cachetxgroup.isEnlisted();
        else
            return false;
    }

    public int groupCount()
    {
        return txGrpSet != null ? txGrpSet.size() : 0;
    }

    public CacheTxSet getReadSet()
    {
        return readSet;
    }

    public CacheTxSet getWriteSet()
    {
        return writeSet;
    }

    public Set getTxGrpSet()
    {
        return txGrpSet;
    }

    public boolean isEmpty()
    {
        return !hasReads() && !hasWrites() && !hasTxGrps();
    }

    protected CacheTxEntry.EntryKey getTxKey(CacheEntry cacheentry)
    {
        return new CacheTxEntry.EntryKey(cacheentry.getKey(), cacheentry.getKeyAttrs());
    }

    protected CacheTxBucket.BucketKey getTxKey(CacheBucket cachebucket)
    {
        return new CacheTxBucket.BucketKey(cachebucket.getHashCode(), cachebucket.getKeyAttrs());
    }

    private void invalidateDepended(CacheTxSet cachetxset, Long long1)
    {
        if(!$assertionsDisabled && long1 == null)
            throw new AssertionError();
        if(cachetxset != null)
        {
            CacheTxGroup cachetxgroup = cachetxset.getTxGroup(long1);
            if(cachetxgroup != null)
                cachetxgroup.invalidateDepended();
        }
    }

    private void invalidate(CacheTxSet cachetxset, Long long1)
    {
        if(!$assertionsDisabled && long1 == null)
            throw new AssertionError();
        if(cachetxset != null)
        {
            CacheTxGroup cachetxgroup = cachetxset.getTxGroup(long1);
            if(cachetxgroup != null)
                cachetxgroup.invalidate();
        }
    }

    protected Thread getStartThread()
    {
        return startThread;
    }

    protected void checkStartThread()
    {
        if(Thread.currentThread() != startThread)
        {
            state = 17;
            throw new IllegalStateException(L10n.format("SRVC.CACHE.ERR34", this));
        } else
        {
            return;
        }
    }

    public Set getNodes()
    {
        return nodes;
    }

    public void setNodes(Set set)
    {
        nodes = set;
    }

    public void checkReadState()
        throws CacheException
    {
        checkAddState();
    }

    protected boolean checkAddState()
        throws CacheException
    {
        synchronized(mutex)
        {
            switch(getState())
            {
            case 11: // '\013'
            case 13: // '\r'
            case 15: // '\017'
                if(!$assertionsDisabled)
                    throw new AssertionError("Invalid cache tx state: " + this);
                // fall through

            case 12: // '\f'
            case 14: // '\016'
                throw new IllegalStateException(L10n.format("SRVC.CACHE.ERR35", this));

            case 16: // '\020'
                setState(17);
                throw new IllegalStateException(L10n.format("SRVC.CACHE.ERR36", this));

            case 17: // '\021'
                if(isDeadlocked.get())
                    throw new CacheTxDeadlockException(L10n.format("SRVC.CACHE.ERR61", this));
                if(isTimedOut.get())
                    throw new CacheTxTimeoutException(L10n.format("SRVC.CACHE.ERR37", this));
                else
                    throw new CacheTxRollbackException(L10n.format("SRVC.CACHE.ERR38", this));

            case 18: // '\022'
                throw new CacheException(L10n.format("SRVC.CACHE.ERR58", this));

            default:
                if(!$assertionsDisabled)
                    throw new AssertionError("Unknown cache tx state: " + getState());
                break;

            case 10: // '\n'
                break;
            }
        }
        return true;
    }

    protected boolean checkPrepareState()
        throws CacheException
    {
        synchronized(mutex)
        {
            switch(getState())
            {
            case 11: // '\013'
            case 13: // '\r'
            case 15: // '\017'
                if(!$assertionsDisabled)
                    throw new AssertionError("Invalid cache tx state: " + this);
                // fall through

            case 12: // '\f'
            case 14: // '\016'
                throw new IllegalStateException(L10n.format("SRVC.CACHE.ERR35", this));

            case 16: // '\020'
                setState(17);
                throw new IllegalStateException(L10n.format("SRVC.CACHE.ERR36", this));

            case 17: // '\021'
                if(isDeadlocked.get())
                    throw new CacheTxDeadlockException(L10n.format("SRVC.CACHE.ERR61", this));
                if(isTimedOut.get())
                    throw new CacheTxTimeoutException(L10n.format("SRVC.CACHE.ERR37", this));
                else
                    throw new CacheTxRollbackException(L10n.format("SRVC.CACHE.ERR38", this));

            case 18: // '\022'
                throw new CacheException(L10n.format("SRVC.CACHE.ERR58", this));

            default:
                if(!$assertionsDisabled)
                    throw new AssertionError("Unknown cache tx state: " + getState());
                // fall through

            case 10: // '\n'
                setState(15);
                break;
            }
        }
        return true;
    }

    protected boolean checkCommitState()
        throws CacheException
    {
        synchronized(mutex)
        {
            switch(getState())
            {
            case 11: // '\013'
            case 13: // '\r'
            case 15: // '\017'
                if(!$assertionsDisabled)
                    throw new AssertionError("Invalid cache tx state: " + this);
                // fall through

            case 12: // '\f'
            case 14: // '\016'
                throw new IllegalStateException(L10n.format("SRVC.CACHE.ERR35", this));

            case 10: // '\n'
                setState(17);
                throw new IllegalStateException(L10n.format("SRVC.CACHE.ERR39", this));

            case 17: // '\021'
                if(!$assertionsDisabled)
                    throw new AssertionError("Transaction marked for rollback should not reach its commit phase: " + this);
                // fall through

            case 18: // '\022'
                throw new CacheException(L10n.format("SRVC.CACHE.ERR58", this));

            default:
                if(!$assertionsDisabled)
                    throw new AssertionError("Unknown cache tx state: " + getState());
                // fall through

            case 16: // '\020'
                setState(11);
                break;
            }
        }
        return true;
    }

    protected boolean checkRollbackState()
        throws CacheException
    {
        synchronized(mutex)
        {
            switch(getState())
            {
            case 11: // '\013'
            case 13: // '\r'
            case 15: // '\017'
                if(!$assertionsDisabled)
                    throw new AssertionError("Invalid cache remote tx state in: " + this);
                // fall through

            case 12: // '\f'
            case 14: // '\016'
                throw new IllegalStateException(L10n.format("SRVC.CACHE.ERR35", this));

            case 18: // '\022'
                throw new CacheException(L10n.format("SRVC.CACHE.ERR58", this));

            default:
                if(!$assertionsDisabled)
                    throw new AssertionError("Unknown cache remote tx state: " + getState());
                // fall through

            case 10: // '\n'
            case 16: // '\020'
            case 17: // '\021'
                setState(13);
                break;
            }
        }
        return true;
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

    private final CacheRegistry registry;
    protected final Long xid;
    protected final int isolationLevel;
    protected final long timestamp = System.currentTimeMillis();
    protected final Thread startThread = Thread.currentThread();
    protected final boolean isRemote;
    protected int state;
    protected CacheTxSet readSet;
    protected CacheTxSet writeSet;
    protected long timeout;
    protected long deadlockTimeout;
    protected BoxedBooleanSync isTimedOut;
    protected BoxedBooleanSync isDeadlocked;
    protected final Object mutex = new Object();
    protected Set txGrpSet;
    private Set nodes;
    private TimeoutTask timeoutTask;
    private TimeoutTask deadlockTimeoutTask;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(CacheTxImpl.class).desiredAssertionStatus();
    }
}
