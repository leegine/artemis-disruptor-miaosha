// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.cache.impl.app;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.cache.*;
import com.fitechlabs.xtier.services.cache.impl.*;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.cache.impl.app:
//            CacheAppRegistry, CacheAppTxManager, CacheAppPreparedSet

abstract class CacheAppTxImpl extends CacheTxImpl
{

    protected CacheAppTxImpl(CacheRegistry cacheregistry, Long long1, int i, long l, long l1,
            boolean flag)
    {
        super(cacheregistry, long1, i, l, l1, flag);
    }

    protected void onStart()
    {
        startTnc = getAppRegistry().getAppTxMgr().getStartTnc();
    }

    protected CacheAppRegistry getAppRegistry()
    {
        return (CacheAppRegistry)getRegistry();
    }

    Long getStartTnc()
    {
        return startTnc;
    }

    long getEndTnc()
    {
        return endTnc;
    }

    void setEndTnc(long l)
    {
        endTnc = l;
    }

    long getTimespan()
    {
        return timespan;
    }

    void setTimespan(long l)
    {
        timespan = l;
    }

    protected void localPrepare()
        throws CacheException
    {
        if(!isEmpty())
        {
            CacheAppRegistry cacheappregistry = getAppRegistry();
            CacheAppPreparedSet cacheapppreparedset = cacheappregistry.getAppTxMgr().getPreparedSet(this);
            if(isolationLevel == 2 && writeSet != null)
            {
                CacheAppTxImpl acacheapptximpl[] = null;
                acacheapptximpl = cacheappregistry.getAppTxMgr().getCommittedArr(startTnc.longValue(), cacheapppreparedset.getTnc());
                if(acacheapptximpl != null)
                {
                    for(int i = 0; i < acacheapptximpl.length; i++)
                    {
                        CacheAppTxImpl cacheapptximpl1 = acacheapptximpl[i];
                        if(!$assertionsDisabled && cacheapptximpl1.writeSet == null)
                            throw new AssertionError("Only mutating transactions can be added to array of committed transactions.");
                        if(intersects(readSet, cacheapptximpl1.writeSet))
                            throwPrepareException();
                    }

                }
            }
            if(cacheapppreparedset.getActiveTxSet() != null)
            {
                Iterator iterator = cacheapppreparedset.getActiveTxSet().iterator();
                do
                {
                    if(!iterator.hasNext())
                        break;
                    CacheAppTxImpl cacheapptximpl = (CacheAppTxImpl)iterator.next();
                    if(!$assertionsDisabled && cacheapptximpl == null)
                        throw new AssertionError();
                    if(!$assertionsDisabled && cacheapptximpl == this)
                        throw new AssertionError();
                    if((isolationLevel == 2 || cacheapptximpl.isolationLevel == 2) && !cacheapptximpl.isFailureState())
                    {
                        if(intersects(readSet, cacheapptximpl.writeSet))
                            throwPrepareException();
                        if(intersects(writeSet, cacheapptximpl.writeSet))
                            throwPrepareException();
                    }
                } while(true);
            }
            if(hasWrites())
            {
                for(Iterator iterator1 = writeSet.getTxObjs().values().iterator(); iterator1.hasNext(); ((CacheTxObject)iterator1.next()).getGroup().addMemberPreparedTx(this));
            }
        }
    }

    protected void localCommit()
    {
        CacheEventManager cacheeventmanager = getAppRegistry().getEventMgr();
        if(hasReads())
        {
            if(!$assertionsDisabled && getIsolationLevel() != 2)
                throw new AssertionError();
            for(Iterator iterator = readSet.getTxObjs().values().iterator(); iterator.hasNext();)
            {
                CacheTxObject cachetxobject = (CacheTxObject)iterator.next();
                synchronized(cachetxobject.getObj().getMutex())
                {
                    cachetxobject.getObj().removeTx(this);
                    if(!isRemote())
                    {
                        CacheTxEntry cachetxentry = (CacheTxEntry)cachetxobject;
                        CacheEntryImpl cacheentryimpl = cachetxentry.getEntry();
                        if(cachetxentry.getModCount() == cacheentryimpl.getModCount() && cachetxentry.getTxValue() != null)
                            cacheentryimpl.setVal(cachetxentry.getTxValue());
                        cacheentryimpl.setPendingRemove(false);
                        cacheeventmanager.addEvent(CacheUtils.getEventId(cachetxentry.getOp(), isRemote()), cacheentryimpl);
                    }
                }
            }

        }
        int i = writeCount();
        HashSet hashset = !isReadOnly() ? new HashSet(i >= 4 ? i / 2 : i) : null;
        if(hasTxGrps())
        {
            CacheGroup cachegroup;
            for(Iterator iterator1 = txGrpSet.iterator(); iterator1.hasNext(); hashset.add(cachegroup))
            {
                CacheTxGroup cachetxgroup = (CacheTxGroup)iterator1.next();
                cachegroup = cachetxgroup.getGroup();
                commitGroup(cachegroup, (byte)4);
            }

        }
        if(hasWrites())
        {
            for(Iterator iterator2 = writeSet.getTxObjs().values().iterator(); iterator2.hasNext();)
            {
                CacheTxObject cachetxobject1 = (CacheTxObject)iterator2.next();
                CacheGroup cachegroup1 = cachetxobject1.getGroup();
                if(!hashset.contains(cachegroup1))
                {
                    synchronized(cachegroup1.getMutex())
                    {
                        commitAll(cachegroup1.getDepended(), (byte)-1);
                        cachegroup1.removeMemeberPreparedTx(this);
                    }
                    hashset.add(cachegroup1);
                }
                if(cachetxobject1.getObj().getType() == 2)
                    commitEntry((CacheEntryImpl)cachetxobject1.getObj(), cachetxobject1.getOp());
                else
                    commitBucket((CacheBucket)cachetxobject1.getObj(), cachetxobject1.getOp());
            }

        }
        getAppRegistry().getAppTxMgr().onCommit(this);
    }

    protected void localRollback()
    {
        if(hasTxGrps())
            rollback(txGrpSet.iterator());
        if(hasWrites())
            rollback(writeSet.iterator());
        if(hasReads())
            rollback(readSet.iterator());
        getAppRegistry().getAppTxMgr().onRollback(this);
    }

    private void rollback(Iterator iterator)
    {
        do
        {
            if(!iterator.hasNext())
                break;
            CacheObject cacheobject = ((CacheTxObject)iterator.next()).getObj();
            cacheobject.removeTx(this);
            if(cacheobject.getType() == 2)
                ((CacheEntryImpl)cacheobject).getGroup().removeMemeberPreparedTx(this);
            else
            if(cacheobject.getType() == 3)
                ((CacheBucket)cacheobject).getGroup().removeMemeberPreparedTx(this);
            if(CacheUtils.isRemovable(cacheobject))
                getRegistry().getDataMgr().delete(cacheobject);
        } while(true);
    }

    private void commitAll(CacheMap cachemap, byte byte0)
    {
        if(cachemap != null && !cachemap.isEmpty())
        {
            for(Iterator iterator = cachemap.buckets(); iterator.hasNext(); commitBucket((CacheBucket)iterator.next(), byte0));
        }
    }

    private void commitGroup(CacheGroup cachegroup, byte byte0)
    {
        synchronized(cachegroup.getMutex())
        {
            cachegroup.removeTx(this);
            commitAll(cachegroup.getDepended(), byte0);
            commitAll(cachegroup.getNotDepended(), byte0);
            cachegroup.removeMemeberPreparedTx(this);
        }
    }

    private void commitBucket(CacheBucket cachebucket, byte byte0)
    {
        if(getIsolationLevel() == 1)
        {
            CacheGroup cachegroup = cachebucket.getGroup();
            boolean flag = !cachebucket.getKeyAttrs().isDepended() ? cachegroup.hasOtherPreparedTx(this) : cachegroup.hasOtherMemberPreparedTx(this);
            synchronized(cachebucket.getMutex())
            {
                byte0 = resolveOp(cachebucket, byte0);
                if(resolveConflict(cachebucket, getTxKey(cachebucket)))
                    flag = true;
                if(!cachebucket.isEmpty())
                {
                    List list1 = cachebucket.getEntries();
                    for(int j = list1.size(); j-- > 0;)
                        commit((CacheEntryImpl)list1.get(j), byte0, flag);

                }
                getRegistry().getEventMgr().addEvent(cachebucket);
            }
        } else
        {
            synchronized(cachebucket.getMutex())
            {
                byte0 = resolveOp(cachebucket, byte0);
                if(!cachebucket.isEmpty())
                {
                    List list = cachebucket.getEntries();
                    for(int i = list.size(); i-- > 0;)
                        commit((CacheEntryImpl)list.get(i), byte0, false);

                }
                getRegistry().getEventMgr().addEvent(cachebucket);
            }
        }
    }

    private void commitEntry(CacheEntryImpl cacheentryimpl, byte byte0)
    {
        if(isolationLevel == 1)
        {
            CacheGroup cachegroup = cacheentryimpl.getGroup();
            boolean flag = !cacheentryimpl.getKeyAttrs().isDepended() ? cachegroup.hasOtherPreparedTx(this) : cachegroup.hasOtherMemberPreparedTx(this);
            CacheBucket cachebucket = cacheentryimpl.getBucket();
            synchronized(cachebucket.getMutex())
            {
                if(resolveConflict(cachebucket, getTxKey(cachebucket)))
                    flag = true;
                commit(cacheentryimpl, byte0, flag);
            }
        } else
        {
            commit(cacheentryimpl, byte0, false);
        }
    }

    private void commit(CacheEntryImpl cacheentryimpl, byte byte0, boolean flag)
    {
        com.fitechlabs.xtier.services.cache.impl.CacheTxEntry.EntryKey entrykey = getTxKey(cacheentryimpl);
        if(getIsolationLevel() == 1)
            synchronized(cacheentryimpl.getMutex())
            {
                byte0 = resolveOp(cacheentryimpl, byte0);
                if(resolveConflict(cacheentryimpl, entrykey))
                    flag = true;
                Object obj2 = null;
                if(!isRemote())
                {
                    CacheTxEntry cachetxentry = (CacheTxEntry)writeSet.getTxObj(entrykey);
                    obj2 = cachetxentry != null && !flag ? cachetxentry.getTxValue() : null;
                }
                cacheentryimpl.setVal(obj2);
                getRegistry().getDataMgr().spoolRemove(cacheentryimpl);
                getRegistry().getDataMgr().onWrite(cacheentryimpl);
                cacheentryimpl.setPendingRemove(byte0 == 3);
                if(byte0 != -1)
                    getRegistry().getEventMgr().addEvent(CacheUtils.getEventId(byte0, isRemote()), cacheentryimpl);
            }
        else
            synchronized(cacheentryimpl.getMutex())
            {
                byte0 = resolveOp(cacheentryimpl, byte0);
                Object obj3 = null;
                if(!isRemote())
                {
                    CacheTxEntry cachetxentry1 = (CacheTxEntry)writeSet.getTxObj(entrykey);
                    obj3 = cachetxentry1 != null ? cachetxentry1.getTxValue() : null;
                }
                cacheentryimpl.setVal(obj3);
                cacheentryimpl.setPendingRemove(byte0 == 3);
                if(byte0 != -1)
                    getRegistry().getEventMgr().addEvent(CacheUtils.getEventId(byte0, false), cacheentryimpl);
            }
    }

    private byte resolveOp(CacheObject cacheobject, byte byte0)
    {
        if(!$assertionsDisabled && !Thread.holdsLock(cacheobject.getMutex()))
            throw new AssertionError();
        if(cacheobject.removeTx(this) && (byte0 == 4 || byte0 == -1))
        {
            if(cacheobject.getType() == 3)
            {
                CacheBucket cachebucket = (CacheBucket)cacheobject;
                byte0 = writeSet.getTxBucket(cachebucket.getHashCode(), cachebucket.getKeyAttrs()).getOp();
            } else
            if(cacheobject.getType() == 2)
            {
                CacheEntryImpl cacheentryimpl = (CacheEntryImpl)cacheobject;
                byte0 = writeSet.getTxEntry(cacheentryimpl.getKey(), cacheentryimpl.getKeyAttrs()).getOp();
            }
        } else
        if(byte0 == 4)
            byte0 = -1;
        return byte0;
    }

    private boolean resolveConflict(CacheObject cacheobject, Object obj)
    {
        if(!$assertionsDisabled && !Thread.holdsLock(cacheobject.getMutex()))
            throw new AssertionError();
        if(cacheobject.hasTx())
        {
            List list = cacheobject.getTxList();
            int i = 0;
            for(int j = list.size(); i < j; i++)
            {
                CacheAppTxImpl cacheapptximpl = (CacheAppTxImpl)list.get(i);
                if(!$assertionsDisabled && cacheapptximpl == this)
                    throw new AssertionError();
                if(!$assertionsDisabled && CacheUtils.isPreparedState(cacheapptximpl.getState()) && cacheapptximpl.getIsolationLevel() != 1 && cacheapptximpl.readSet.getTxObj(obj) == null)
                    throw new AssertionError("Invalid tx: " + cacheapptximpl);
                if(cacheapptximpl.getIsolationLevel() == 1 && CacheUtils.isPreparedState(cacheapptximpl.getState()))
                {
                    cacheapptximpl.writeSet.getTxObj(obj).invalidate();
                    return true;
                }
            }

        }
        return false;
    }

    private boolean intersects(CacheTxSet cachetxset, CacheTxSet cachetxset1)
    {
        return cachetxset != null && cachetxset1 != null ? cachetxset.intersects(cachetxset1) : false;
    }

    private void throwPrepareException()
        throws CacheTxRollbackException, CacheTxOptimisticLockException
    {
        synchronized(mutex)
        {
            if(state == 17)
                throw new CacheTxRollbackException(L10n.format("SRVC.CACHE.ERR3", xid));
            state = 17;
        }
        throw new CacheTxOptimisticLockException(L10n.format("SRVC.CACHE.ERR2", xid));
    }

    public String toString()
    {
        return L10n.format("SRVC.CACHE.TXT13", new Object[] {
            xid, CacheUtils.getTxStateStr(getState()), CacheUtils.getIsolationStr(getIsolationLevel()), Boolean.valueOf(isRemote()), new Integer(readCount()), new Integer(writeCount()), new Integer(groupCount())
        });
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

    private Long startTnc;
    private long endTnc;
    private long timespan;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(CacheAppTxImpl.class).desiredAssertionStatus();
    }
}
