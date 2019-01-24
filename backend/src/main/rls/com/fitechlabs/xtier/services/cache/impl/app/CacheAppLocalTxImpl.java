// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.cache.impl.app;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.cache.CacheException;
import com.fitechlabs.xtier.services.cache.impl.*;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.utils.boxed.sync.BoxedBooleanSync;
import java.util.Iterator;
import java.util.Set;

// Referenced classes of package com.fitechlabs.xtier.services.cache.impl.app:
//            CacheAppTxImpl, CacheAppRegistry, CacheAppConfig, CacheAppTxManager, 
//            CacheAppCommManager

class CacheAppLocalTxImpl extends CacheAppTxImpl
{

    CacheAppLocalTxImpl(CacheAppRegistry cacheappregistry, int i)
    {
        super(cacheappregistry, cacheappregistry.getTxMgr().createXid(), i, cacheappregistry.getConfig().getDfltTxTimeout(), cacheappregistry.getAppConfig().getTxDeadlockTimeout(), false);
        distributed = false;
    }

    public void onTimeout()
    {
label0:
        {
            synchronized(mutex)
            {
                if(state == 10 || state == 17)
                {
                    isTimedOut.set(true);
                    state = 17;
                    break label0;
                }
            }
            return;
        }
        obj;
        JVM INSTR monitorexit ;
          goto _L1
        exception;
        throw exception;
_L1:
        localRollback();
        return;
    }

    public void onDeadlockTimeout()
    {
label0:
        {
            synchronized(mutex)
            {
                if(writeSet != null && writeSet.getTxObjsCount() >= 2)
                    break label0;
            }
            return;
        }
        Set set = getAppRegistry().getAppTxMgr().getLocalTxSnapshot();
        Iterator iterator = set.iterator();
        CacheTxImpl cachetximpl;
        do
        {
            if(!iterator.hasNext())
                break MISSING_BLOCK_LABEL_148;
            cachetximpl = (CacheTxImpl)iterator.next();
        } while(cachetximpl.equals(this) || cachetximpl.getWriteSet() == null || !writeSet.hasReverseOrder(cachetximpl.getWriteSet()));
        resetTimeout();
        if(state == 10 || state == 17)
        {
            state = 17;
            isDeadlocked.set(true);
            localRollback();
        }
        obj;
        JVM INSTR monitorexit ;
        return;
        boolean flag = false;
        try
        {
            flag = getAppRegistry().getAppCommMgr().sendDeadlockDetect(this);
        }
        catch(CacheException cacheexception)
        {
            getRegistry().getLogger().error(L10n.format("SRVC.CACHE.ERR60"), cacheexception);
        }
        if(flag)
        {
            resetTimeout();
            if(state == 10 || state == 17)
            {
                state = 17;
                isDeadlocked.set(true);
                localRollback();
            }
        }
        obj;
        JVM INSTR monitorexit ;
          goto _L1
        exception;
        throw exception;
_L1:
    }

    public Object add(CacheEntryImpl cacheentryimpl, Object obj, byte byte0, Object obj1)
        throws CacheException
    {
        checkStartThread();
        checkAddState();
        return super.add(cacheentryimpl, obj, byte0, obj1);
    }

    public void prepare()
        throws CacheException
    {
        checkStartThread();
        checkPrepareState();
        try
        {
            localPrepare();
            if(!isReadOnly())
            {
                distributed = true;
                getAppRegistry().getAppCommMgr().sendPhaseOne(this);
            }
        }
        catch(CacheException cacheexception)
        {
            setState(17);
            throw cacheexception;
        }
        setState(16);
        try
        {
            getRegistry().getEventMgr().onPrepared(this);
        }
        catch(CacheException cacheexception1)
        {
            setState(17);
            throw cacheexception1;
        }
    }

    public void commit()
        throws CacheException
    {
        checkStartThread();
        checkCommitState();
        localCommit();
        if(!isReadOnly())
            getAppRegistry().getAppCommMgr().sendPhaseTwo(this, true);
        CacheAppRegistry cacheappregistry = getAppRegistry();
        cacheappregistry.getAppTxMgr().removeTx(this);
        setState(12);
        try
        {
            cacheappregistry.getEventMgr().onFinished(this);
        }
        catch(CacheException cacheexception)
        {
            setState(18);
            throw cacheexception;
        }
    }

    public void rollback()
        throws CacheException
    {
        checkStartThread();
        checkRollbackState();
        if(!isTimedOut.get())
        {
            localRollback();
            if(distributed && !isReadOnly())
                getAppRegistry().getAppCommMgr().sendPhaseTwo(this, false);
        }
        CacheAppRegistry cacheappregistry = getAppRegistry();
        cacheappregistry.getAppTxMgr().removeTx(this);
        setState(14);
        try
        {
            cacheappregistry.getEventMgr().onFinished(this);
        }
        catch(CacheException cacheexception)
        {
            setState(18);
            throw cacheexception;
        }
    }

    private boolean distributed;
}
