// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.cache.impl.app;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.cache.CacheException;
import com.fitechlabs.xtier.services.cache.CacheTxRollbackException;
import com.fitechlabs.xtier.services.cache.impl.CacheConfig;
import com.fitechlabs.xtier.services.cache.impl.CacheTxManager;

// Referenced classes of package com.fitechlabs.xtier.services.cache.impl.app:
//            CacheAppTxImpl, CacheAppRegistry, CacheAppConfig, CacheAppCommManager,
//            CacheAppTxManager

class CacheAppRemoteTxImpl extends CacheAppTxImpl
{

    CacheAppRemoteTxImpl(CacheAppRegistry cacheappregistry, Long long1, int i, long l)
    {
        super(cacheappregistry, long1, i, cacheappregistry.getConfig().getDgcTxAge(), cacheappregistry.getAppConfig().getTxDeadlockTimeout(), true);
        setTimespan(l);
    }

    public void onTimeout()
    {
        if(getAppRegistry().getAppCommMgr().checkDgc(this))
            commit();
        else
            getAppRegistry().getTxMgr().resubscribe(this);
    }

    public void onDeadlockTimeout()
    {
    }

    public void setTimeout(long l)
    {
        if(!$assertionsDisabled)
            throw new AssertionError();
        else
            return;
    }

    public void prepare()
        throws CacheException
    {
        checkPrepareState();
        localPrepare();
        synchronized(mutex)
        {
            if(getState() == 17)
                throw new CacheTxRollbackException("Remote transaction marked for rollback [xid=" + xid + ']');
            setState(16);
        }
    }

    public void commit()
    {
        if(checkCommitState())
        {
            localCommit();
            setState(12);
            getAppRegistry().getAppTxMgr().removeTx(this);
        }
    }

    public void rollback()
    {
        if(checkRollbackState())
        {
            localRollback();
            setState(14);
            getAppRegistry().getAppTxMgr().removeTx(this);
        }
    }

    protected boolean checkPrepareState()
        throws CacheTxRollbackException
    {
        synchronized(mutex)
        {
            if(isRollbackOnly())
                throw new CacheTxRollbackException(L10n.format("SRVC.CACHE.ERR46", xid));
            if(!$assertionsDisabled && getState() != 10)
                throw new AssertionError("Invalid cache tx state: " + this);
            setState(15);
        }
        return true;
    }

    protected boolean checkCommitState()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        int i;
        i = getState();
        if(i == 16)
        {
            setState(11);
            return true;
        }
        if(!$assertionsDisabled && (i == 10 || i == 15 || i == 17))
            throw new AssertionError();
        false;
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    protected boolean checkRollbackState()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        getState();
        JVM INSTR tableswitch 10 17: default 77
    //                   10 59
    //                   11 69
    //                   12 73
    //                   13 69
    //                   14 73
    //                   15 59
    //                   16 56
    //                   17 56;
           goto _L1 _L2 _L3 _L4 _L3 _L4 _L2 _L5 _L5
_L2:
        setState(17);
        return false;
_L3:
        false;
        obj;
        JVM INSTR monitorexit ;
        return;
_L4:
        false;
        obj;
        JVM INSTR monitorexit ;
        return;
_L1:
        if(!$assertionsDisabled)
            throw new AssertionError("Unknown cache remote tx state: " + getState());
_L5:
        setState(13);
        true;
        obj;
        JVM INSTR monitorexit ;
        return;
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

    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(CacheAppRemoteTxImpl.class).desiredAssertionStatus();
    }
}
