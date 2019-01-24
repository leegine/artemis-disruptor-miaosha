// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.tx.impl;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.services.objpool.ObjectPoolService;
import com.fitechlabs.xtier.services.objpool.threads.ThreadPool;
import com.fitechlabs.xtier.utils.ArgAssert;
import java.util.Set;
import java.util.Timer;
import javax.transaction.*;
import javax.transaction.xa.XAException;

// Referenced classes of package com.fitechlabs.xtier.services.tx.impl:
//            TxException, TxImpl, TxXmlLogger, TxRecoveryLogger

final class TxManagerImpl
    implements TransactionManager, Status
{
    private final class ThreadData
    {

        int getTimeout()
        {
            return timeout;
        }

        void setTimeout(int i)
        {
            timeout = i;
        }

        TxImpl getTx()
        {
            return tx;
        }

        void setTx(TxImpl tximpl)
        {
            tx = tximpl;
        }

        public String toString()
        {
            if(!$assertionsDisabled)
                throw new AssertionError();
            else
                return "XA TX data [timeout=" + timeout + ", tx=" + tx + ']';
        }

        private int timeout;
        private TxImpl tx;
        static final boolean $assertionsDisabled; /* synthetic field */


        private ThreadData()
        {
            super();
            timeout = 0;
            tx = null;
        }

    }


    private TxManagerImpl(Logger logger, TxXmlLogger txxmllogger, TxRecoveryLogger txrecoverylogger, boolean flag, Set set)
    {
        dfltTimeout = 0;
        txThreadMap = new ThreadLocal();
        timer = null;
        forgetHeuristic = true;
        heuristicExceptionalXars = null;
        log = logger;
        tXmlLog = txxmllogger;
        tBinLog = txrecoverylogger;
        forgetHeuristic = flag;
        heuristicExceptionalXars = set;
        timer = new Timer();
    }

    static TransactionManager newInstance(Logger logger, String s, TxXmlLogger txxmllogger, TxRecoveryLogger txrecoverylogger, boolean flag, Set set)
        throws TxException
    {
        if(!$assertionsDisabled && instance != null)
            throw new AssertionError();
        pool = XtierKernel.getInstance().objpool().getThreadPool(s);
        if(pool == null)
        {
            throw new TxException(L10n.format("SRVC.TX.ERR53", s));
        } else
        {
            instance = new TxManagerImpl(logger, txxmllogger, txrecoverylogger, flag, set);
            return instance;
        }
    }

    static TxManagerImpl getInstance()
    {
        if(!$assertionsDisabled && instance == null)
            throw new AssertionError();
        else
            return instance;
    }

    void setDefaultTimeout(int i)
    {
        dfltTimeout = i;
    }

    long getDefaultTimeout()
    {
        return (long)dfltTimeout;
    }

    public void begin()
        throws NotSupportedException, SystemException
    {
        ThreadData threaddata = getThreadData();
        if(threaddata.getTx() != null && !threaddata.getTx().isFinished())
            throw new NotSupportedException(L10n.format("SRVC.TX.MGR.IMPL.ERR1"));
        if(!$assertionsDisabled && threaddata.getTimeout() <= 0)
            throw new AssertionError();
        try
        {
            threaddata.setTx(new TxImpl(log, threaddata.getTimeout(), pool, tXmlLog, tBinLog, forgetHeuristic, heuristicExceptionalXars));
        }
        catch(XAException xaexception)
        {
            SystemException systemexception = new SystemException(L10n.format("SRVC.TX.ERR43"));
            systemexception.initCause(xaexception);
            throw systemexception;
        }
    }

    public void commit()
        throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException
    {
        TxImpl tximpl = getCurrTx();
        if(tximpl != null)
        {
            tximpl.commit();
            cleanThreadData();
        } else
        {
            throw new IllegalStateException(L10n.format("SRVC.TX.MGR.IMPL.ERR2"));
        }
    }

    public int getStatus()
    {
        TxImpl tximpl = getCurrTx();
        return tximpl != null ? tximpl.getStatus() : 6;
    }

    public Transaction getTransaction()
    {
        return getCurrTx();
    }

    public void resume(Transaction transaction)
        throws IllegalStateException
    {
        ArgAssert.nullArg(transaction, "tx");
        ThreadData threaddata = getThreadData();
        TxImpl tximpl = getThreadData().getTx();
        if(tximpl != null)
            throw new IllegalStateException(L10n.format("SRVC.TX.MGR.IMPL.ERR4"));
        if(tximpl != transaction)
            threaddata.setTx((TxImpl)transaction);
    }

    public Transaction suspend()
    {
        ThreadData threaddata = getExistingThreadData();
        if(threaddata == null)
        {
            return null;
        } else
        {
            TxImpl tximpl = threaddata.getTx();
            threaddata.setTx(null);
            return tximpl;
        }
    }

    public void rollback()
        throws IllegalStateException, SecurityException
    {
        TxImpl tximpl = getCurrTx();
        if(tximpl != null)
        {
            tximpl.rollback();
            cleanThreadData();
        } else
        {
            throw new IllegalStateException(L10n.format("SRVC.TX.MGR.IMPL.ERR5"));
        }
    }

    public void setRollbackOnly()
        throws IllegalStateException
    {
        TxImpl tximpl = getCurrTx();
        if(tximpl != null)
            tximpl.setRollbackOnly();
        else
            throw new IllegalStateException(L10n.format("SRVC.TX.MGR.IMPL.ERR5"));
    }

    public void setTransactionTimeout(int i)
    {
        getThreadData().setTimeout(1000 * i);
    }

    private ThreadData getThreadData()
    {
        ThreadData threaddata = (ThreadData)txThreadMap.get();
        if(threaddata == null)
        {
            threaddata = new ThreadData();
            threaddata.setTimeout(dfltTimeout * 1000);
            txThreadMap.set(threaddata);
        }
        return threaddata;
    }

    private ThreadData getExistingThreadData()
    {
        return (ThreadData)txThreadMap.get();
    }

    private void cleanThreadData()
    {
        ThreadData threaddata = (ThreadData)txThreadMap.get();
        if(threaddata != null)
            threaddata.setTx(null);
    }

    private TxImpl getCurrTx()
    {
        ThreadData threaddata = (ThreadData)txThreadMap.get();
        return threaddata != null ? threaddata.getTx() : null;
    }

    Timer getTimer()
    {
        if(!$assertionsDisabled && timer == null)
            throw new AssertionError();
        else
            return timer;
    }

    static void shutdown()
    {
        getInstance().getTimer().cancel();
        instance = null;
        XtierKernel.getInstance().objpool().deleteWaitThreadPool(pool.getName());
        pool = null;
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

    private static TxManagerImpl instance;
    private static ThreadPool pool = null;
    private final Logger log;
    private int dfltTimeout;
    private ThreadLocal txThreadMap;
    private TxXmlLogger tXmlLog;
    private TxRecoveryLogger tBinLog;
    private Timer timer;
    private boolean forgetHeuristic;
    private Set heuristicExceptionalXars;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(TxManagerImpl.class).desiredAssertionStatus();
    }
}
