// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.tx.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.services.objpool.threads.ThreadPool;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.Utils;
import com.fitechlabs.xtier.utils.boxed.BoxedBoolean;
import com.fitechlabs.xtier.utils.boxed.BoxedInt32;
import java.util.*;
import javax.transaction.*;
import javax.transaction.xa.*;

// Referenced classes of package com.fitechlabs.xtier.services.tx.impl:
//            TxIdFactory, TxUtils, TxManagerImpl, TxXmlLogger,
//            TxRecoveryLogger

final class TxImpl
    implements Transaction, Status
{
    static interface XarStates
    {

        public static final short XA_RES_NEW = 0;
        public static final short XA_RES_ENLISTED = 1;
        public static final short XA_RES_SUSPENDED = 2;
        public static final short XA_RES_ENDED = 3;
        public static final short XA_RES_VOTED_RDONLY = 4;
        public static final short XA_RES_VOTED_OK = 5;
    }

    private static class XAR
    {

        XAResource getRes()
        {
            return car;
        }

        XAR getSameRmXar()
        {
            return sameRmXar;
        }

        short getState()
        {
            return state;
        }

        void setState(short word0)
        {
            state = word0;
        }

        Xid getBranchXid()
        {
            return branchXid;
        }

        public String toString()
        {
            if(!$assertionsDisabled)
                throw new AssertionError();
            else
                return "XAR [xar=" + car + ", state=" + TxUtils.getStrXaResState(state) + ", branch-xid=" + branchXid + ", same-rm=" + sameRmXar + ']';
        }

        private XAResource car;
        private short state;
        private Xid branchXid;
        private XAR sameRmXar;
        static final boolean $assertionsDisabled; /* synthetic field */


        XAR(XAResource xaresource, Xid xid1, XAR xar)
        {
            state = 0;
            car = xaresource;
            branchXid = xid1;
            sameRmXar = xar;
        }
    }


    TxImpl(Logger logger, int i, ThreadPool threadpool, TxXmlLogger txxmllogger, TxRecoveryLogger txrecoverylogger, boolean flag, Set set)
        throws XAException
    {
        syncs = new ArrayList();
        heuristic = 0x7ffffffe;
        branchIdGen = 0;
        ended = false;
        xars = new ArrayList();
        timerTask = null;
        forgetHeuristic = true;
        heuristicExceptionalXars = null;
        if(!$assertionsDisabled && (logger == null || i < 0 || threadpool == null || txxmllogger == null || txrecoverylogger == null))
        {
            throw new AssertionError();
        } else
        {
            log = logger;
            timeout = i;
            pool = threadpool;
            tXmlLog = txxmllogger;
            tBinLog = txrecoverylogger;
            forgetHeuristic = flag;
            heuristicExceptionalXars = set;
            xid = TxIdFactory.newXid();
            status = 0;
            timerTask = new TimerTask() {

                public void run()
                {
                    onTimeout();
                }


            {
                super();
            }
            }
;
            addTimeout(i);
            return;
        }
    }

    private synchronized void onTimeout()
    {
        if(status == 0 || status == 7 || status == 2)
            status = 1;
        else
        if(status == 8)
        {
            onHeuristic(null, 5);
            status = 1;
        }
    }

    public synchronized void commit()
        throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException
    {
        logTxRsrcs(xid.getGlobalTransactionId(), status, xars);
        removeTimeout();
        switch(status)
        {
        case 2: // '\002'
        case 5: // '\005'
        case 6: // '\006'
        case 7: // '\007'
        case 8: // '\b'
        case 9: // '\t'
            throw new IllegalStateException(L10n.format("SRVC.TX.ERR44", TxUtils.getStrStatus(status)));

        case 3: // '\003'
        case 4: // '\004'
            checkHeuristics();
            throw new IllegalStateException(L10n.format("SRVC.TX.ERR44", TxUtils.getStrStatus(status)));

        case 1: // '\001'
            beforeCompletion();
            endAllXars();
            rollbackAllXaRes();
            afterCompletion();
            checkHeuristics();
            throw new RollbackException(L10n.format("SRVC.TX.ERR10", this));

        default:
            throw new IllegalStateException(L10n.format("SRVC.TX.ERR11", TxUtils.getStrStatus(status)));

        case 0: // '\0'
            break;
        }
        if(!$assertionsDisabled && status != 0)
            throw new AssertionError();
        beforeCompletion();
        endAllXars();
        if(status == 0)
        {
            int i = xars.size();
            if(i == 0)
                status = 3;
            else
            if(i == 1)
                commitAllXaRes(true);
            else
            if(!prepareAllXaRes())
            {
                if(status == 2)
                {
                    commitAllXaRes(false);
                } else
                {
                    rollbackAllXaRes();
                    afterCompletion();
                    throw new RollbackException(L10n.format("SRVC.TX.ERR12", this));
                }
            } else
            {
                status = 3;
            }
        }
        afterCompletion();
        checkHeuristics();
        logTx(xid.getGlobalTransactionId(), status);
        break MISSING_BLOCK_LABEL_354;
        Exception exception;
        exception;
        logTx(xid.getGlobalTransactionId(), status);
        throw exception;
    }

    public synchronized void rollback()
        throws SecurityException
    {
        removeTimeout();
        status;
        JVM INSTR tableswitch 0 7: default 152
    //                   0 56
    //                   1 56
    //                   2 152
    //                   3 152
    //                   4 152
    //                   5 134
    //                   6 152
    //                   7 111;
           goto _L1 _L2 _L2 _L1 _L1 _L1 _L3 _L1 _L4
_L2:
        beforeCompletion();
        endAllXars();
        rollbackAllXaRes();
        afterCompletion();
        if(heuristic != 0x7ffffffe)
            status = 9;
        heuristic = 0x7ffffffe;
        logTx(xid.getGlobalTransactionId(), status);
        return;
_L4:
        status = 1;
        logTx(xid.getGlobalTransactionId(), status);
        return;
_L3:
        logTx(xid.getGlobalTransactionId(), status);
        return;
_L1:
        throw new IllegalStateException(L10n.format("SRVC.TX.ERR13", this));
        Exception exception;
        exception;
        logTx(xid.getGlobalTransactionId(), status);
        throw exception;
    }

    public synchronized boolean delistResource(XAResource xaresource, int i)
    {
        ArgAssert.nullArg(xaresource, "xar");
        ArgAssert.illegalArg(i == 0x4000000 || i == 0x2000000 || i == 0x20000000, "flag");
        XAR xar = findXaRes(xaresource);
        if(xar == null)
            throw new IllegalArgumentException(L10n.format("SRVC.TX.ERR16"));
        switch(status)
        {
        case 1: // '\001'
        case 2: // '\002'
        case 3: // '\003'
        case 4: // '\004'
        case 5: // '\005'
        case 6: // '\006'
        case 7: // '\007'
        case 8: // '\b'
        case 9: // '\t'
            throw new IllegalStateException(L10n.format("SRVC.TX.ERR44", TxUtils.getStrStatus(status)));

        default:
            throw new IllegalStateException(L10n.format("SRVC.TX.ERR24", TxUtils.getStrStatus(status)));

        case 0: // '\0'
            break;
        }
        try
        {
            endXar(xar, i);
            return true;
        }
        catch(XAException xaexception)
        {
            log.warning(L10n.format("SRVC.TX.WRN1", TxUtils.getStrXAErrCode(xaexception.errorCode), this, xaexception.getMessage()));
        }
        status = 1;
        return false;
    }

    public synchronized boolean enlistResource(XAResource xaresource)
    {
        ArgAssert.nullArg(xaresource, "xar");
        switch(status)
        {
        case 1: // '\001'
        case 2: // '\002'
        case 3: // '\003'
        case 4: // '\004'
        case 5: // '\005'
        case 6: // '\006'
        case 7: // '\007'
        case 8: // '\b'
        case 9: // '\t'
            throw new IllegalStateException(L10n.format("SRVC.TX.ERR44", TxUtils.getStrStatus(status)));

        default:
            throw new IllegalStateException(L10n.format("SRVC.TX.ERR27", TxUtils.getStrStatus(status)));

        case 0: // '\0'
            break;
        }
        if(ended)
            throw new IllegalStateException(L10n.format("SRVC.TX.ERR28"));
        XAR xar;
        xar = findXaRes(xaresource);
        if(xar == null)
            break MISSING_BLOCK_LABEL_155;
        if(xar.getState() == 1)
            return false;
        XAR xar1;
        Xid xid1;
        boolean flag;
        try
        {
            startXar(xar);
            return true;
        }
        catch(XAException xaexception)
        {
            log.warning(L10n.format("SRVC.TX.WRN2", xaresource, this, xaexception.getMessage()));
        }
        break MISSING_BLOCK_LABEL_287;
        xar1 = getSameRm(xaresource);
        if(xar1 != null)
            break MISSING_BLOCK_LABEL_246;
        xid1 = TxIdFactory.branchXid(xid, branchIdGen++);
        flag = xaresource.setTransactionTimeout(timeout / 1000);
        if(timeout > 0 && !flag)
        {
            log.warning(L10n.format("SRVC.TX.WRN14", xaresource, this));
            return false;
        }
        startXar(addXar(xaresource, xid1, null));
        break MISSING_BLOCK_LABEL_264;
        xid1 = xar1.getBranchXid();
        startXar(addXar(xaresource, xid1, xar1));
        return true;
        return false;
    }

    public synchronized int getStatus()
    {
        return status;
    }

    public synchronized void registerSynchronization(Synchronization synchronization)
    {
        ArgAssert.nullArg(synchronization, "s");
        switch(status)
        {
        case 1: // '\001'
        case 2: // '\002'
        case 3: // '\003'
        case 4: // '\004'
        case 5: // '\005'
        case 6: // '\006'
        case 7: // '\007'
        case 8: // '\b'
        case 9: // '\t'
            throw new IllegalStateException(L10n.format("SRVC.TX.ERR44", TxUtils.getStrStatus(status)));

        default:
            throw new IllegalStateException(L10n.format("SRVC.TX.ERR38", TxUtils.getStrStatus(status)));

        case 0: // '\0'
            syncs.add(synchronization);
            break;
        }
    }

    public synchronized void setRollbackOnly()
    {
        switch(status)
        {
        case 0: // '\0'
        case 2: // '\002'
        case 7: // '\007'
            status = 1;
            return;

        case 1: // '\001'
        case 9: // '\t'
            return;

        case 3: // '\003'
        case 4: // '\004'
        case 5: // '\005'
        case 6: // '\006'
        case 8: // '\b'
            throw new IllegalStateException(L10n.format("SRVC.TX.ERR44", TxUtils.getStrStatus(status)));
        }
        throw new IllegalStateException(L10n.format("SRVC.TX.ERR38", TxUtils.getStrStatus(status)));
    }

    public int hashCode()
    {
        return xid.hashCode();
    }

    public synchronized boolean equals(Object obj)
    {
        if(obj == this)
            return true;
        if(obj != null && (obj instanceof TxImpl))
            return xid.equals(((TxImpl)obj).xid);
        else
            return false;
    }

    private void afterCompletion()
    {
        if(!$assertionsDisabled && !Thread.holdsLock(this))
            throw new AssertionError();
        int i = syncs.size();
        for(int j = 0; j < i; j++)
            ((Synchronization)syncs.get(j)).afterCompletion(status);

    }

    private void beforeCompletion()
    {
        if(!$assertionsDisabled && !Thread.holdsLock(this))
            throw new AssertionError();
        int i = syncs.size();
        for(int j = 0; j < i; j++)
            ((Synchronization)syncs.get(j)).beforeCompletion();

    }

    synchronized void setTransactionTimeout(long l)
        throws SystemException
    {
        if(l < 0L)
        {
            throw new SystemException(L10n.format("SRVC.TX.ERR41", new Long(l)));
        } else
        {
            removeTimeout();
            addTimeout(l);
            return;
        }
    }

    synchronized boolean isFinished()
    {
        return status == 3 || status == 4;
    }

    private XAR addXar(XAResource xaresource, Xid xid1, XAR xar)
    {
        if(!$assertionsDisabled && !Thread.holdsLock(this))
            throw new AssertionError();
        if(!$assertionsDisabled && (xaresource == null || xid1 == null))
        {
            throw new AssertionError();
        } else
        {
            XAR xar1 = new XAR(xaresource, xid1, xar);
            xars.add(xar1);
            return xar1;
        }
    }

    private void checkHeuristics()
        throws HeuristicMixedException, HeuristicRollbackException
    {
        if(!$assertionsDisabled && !Thread.holdsLock(this))
            throw new AssertionError();
        switch(heuristic)
        {
        case 5: // '\005'
        case 8: // '\b'
            heuristic = 0x7ffffffe;
            throw new HeuristicMixedException();

        case 6: // '\006'
            heuristic = 0x7ffffffe;
            throw new HeuristicRollbackException();

        case 7: // '\007'
            heuristic = 0x7ffffffe;
            break;
        }
    }

    private void commitAllXaRes(final boolean onePhase)
    {
        if(!$assertionsDisabled && !Thread.holdsLock(this))
            throw new AssertionError();
        status = 8;
        int i = xars.size();
        final Object mutex = new Object();
        int j = 0;
        final BoxedInt32 finished = new BoxedInt32(0);
        for(int k = 0; k < i; k++)
        {
            final XAR xar = (XAR)xars.get(k);
            if(onePhase || xar.getState() != 4)
            {
                j++;
                pool.addTask(new Runnable() {

                    public void run()
                    {
                        try
                        {
                            xar.getRes().commit(xar.getBranchXid(), onePhase);
                            if(!$assertionsDisabled && status != 8)
                                throw new AssertionError();
                            break MISSING_BLOCK_LABEL_259;
                        }
                        catch(XAException xaexception)
                        {
                            synchronized(mutex)
                            {
                                if(status != 8)
                                    break MISSING_BLOCK_LABEL_194;
                                switch(xaexception.errorCode)
                                {
                                case 5: // '\005'
                                case 6: // '\006'
                                case 7: // '\007'
                                case 8: // '\b'
                                    onHeuristic(xar, xaexception.errorCode);
                                    status = 5;
                                    break MISSING_BLOCK_LABEL_194;
                                }
                                log.warning(L10n.format("SRVC.TX.WRN3", new Boolean(onePhase), xar, TxUtils.getStrXAErrCode(xaexception.errorCode), this, xaexception.getMessage()));
                                status = 1;
                            }
                        }
                        return;
                        obj1;
                        JVM INSTR monitorexit ;
                        if(true)
                            break MISSING_BLOCK_LABEL_259;
                        local;
                        synchronized(mutex)
                        {
                            finished.preIncr();
                            mutex.notifyAll();
                        }
                        JVM INSTR ret 5;
                    }

                    static final boolean $assertionsDisabled; /* synthetic field */



            {
                super();
            }
                }
);
            }
        }

        if(!$assertionsDisabled && j <= 0)
            throw new AssertionError();
        synchronized(mutex)
        {
            for(; finished.get() < j; Utils.waitOn(mutex));
        }
        if(status == 8)
            status = 3;
    }

    private void onHeuristic(XAR xar, int i)
    {
        if(!$assertionsDisabled && !Thread.holdsLock(this))
            throw new AssertionError();
        switch(i)
        {
        case 5: // '\005'
            heuristic = 5;
            break;

        case 6: // '\006'
            if(heuristic == 0x7ffffffe)
                heuristic = 6;
            else
            if(heuristic == 7 || heuristic == 8)
                heuristic = 5;
            break;

        case 7: // '\007'
            if(heuristic == 0x7ffffffe)
                heuristic = 7;
            else
            if(heuristic == 6 || heuristic == 8)
                heuristic = 5;
            break;

        case 8: // '\b'
            if(heuristic == 0x7ffffffe)
                heuristic = 8;
            else
            if(heuristic == 6 || heuristic == 7)
                heuristic = 5;
            break;

        default:
            throw new IllegalArgumentException(L10n.format("SRVC.TX.ERR42", xar, new Integer(i), this));
        }
        if(xar != null && (forgetHeuristic ^ heuristicExceptionalXars.contains(xar.getRes().getClass().getName())))
            try
            {
                xar.getRes().forget(xar.getBranchXid());
            }
            catch(XAException xaexception)
            {
                log.error(L10n.format("SRVC.TX.ERR45", xar, this, xaexception.getMessage()));
            }
    }

    private void endAllXars()
    {
        if(!$assertionsDisabled && !Thread.holdsLock(this))
            throw new AssertionError();
        try
        {
            int i = xars.size();
            for(int j = 0; j < i; j++)
            {
                XAR xar = (XAR)xars.get(j);
                if(xar.getState() == 1)
                    endXar(xar, 0x4000000);
            }

        }
        catch(XAException xaexception)
        {
            log.error(L10n.format("SRVC.TX.ERR46", this, xaexception.getMessage()));
            status = 1;
        }
        ended = true;
    }

    private void endXar(XAR xar, int i)
        throws XAException
    {
        if(!$assertionsDisabled && !Thread.holdsLock(this))
            throw new AssertionError();
        try
        {
            xar.getRes().end(xar.getBranchXid(), i);
        }
        catch(XAException xaexception)
        {
            throw xaexception;
        }
        xar.setState(i != 0x2000000 ? 3 : 2);
        if(i == 0x20000000)
            status = 1;
    }

    private XAR findXaRes(XAResource xaresource)
    {
        if(!$assertionsDisabled && !Thread.holdsLock(this))
            throw new AssertionError();
        int i = xars.size();
        for(int j = 0; j < i; j++)
        {
            XAR xar = (XAR)xars.get(j);
            if(xar.getRes().equals(xaresource))
                return xar;
        }

        return null;
    }

    private XAR getSameRm(XAResource xaresource)
        throws XAException
    {
        if(!$assertionsDisabled && !Thread.holdsLock(this))
            throw new AssertionError();
        int i = xars.size();
        for(int j = 0; j < i; j++)
        {
            XAR xar = (XAR)xars.get(j);
            XAResource xaresource1 = xar.getRes();
            if(xaresource1 != xaresource && xaresource.isSameRM(xaresource1))
                return xar;
        }

        return null;
    }

    private boolean prepareAllXaRes()
    {
        if(!$assertionsDisabled && !Thread.holdsLock(this))
            throw new AssertionError();
        status = 7;
        logTx(xid.getGlobalTransactionId(), status);
        int i = xars.size();
        final Object mutex = new Object();
        int j = 0;
        final BoxedInt32 finished = new BoxedInt32(0);
        final BoxedBoolean readOnly = new BoxedBoolean(true);
        for(int k = 0; k < i; k++)
        {
            final XAR xar = (XAR)xars.get(k);
            if(xar.getSameRmXar() == null)
            {
                j++;
                pool.addTask(new Runnable() {

                    public void run()
                    {
                        synchronized(mutex)
                        {
                            if(status == 7)
                                break MISSING_BLOCK_LABEL_25;
                        }
                        return;
                        obj1;
                        JVM INSTR monitorexit ;
                        int l = xar.getRes().prepare(xar.getBranchXid());
                        synchronized(mutex)
                        {
                            if(status == 7)
                                if(l == 0)
                                {
                                    readOnly.set(false);
                                    xar.setState((short)5);
                                } else
                                if(l == 3)
                                {
                                    xar.setState((short)4);
                                } else
                                {
                                    status = 1;
                                    readOnly.set(false);
                                }
                        }
                        break MISSING_BLOCK_LABEL_282;
                        XAException xaexception;
                        xaexception;
                        synchronized(mutex)
                        {
                            if(status == 7)
                            {
                                readOnly.set(false);
                                log.warning(L10n.format("SRVC.TX.WRN8", TxUtils.getStrXAErrCode(xaexception.errorCode), this, xaexception.getMessage()));
                                status = 1;
                            }
                        }
                        if(true)
                            break MISSING_BLOCK_LABEL_282;
                        local;
                        synchronized(mutex)
                        {
                            finished.preIncr();
                            mutex.notifyAll();
                        }
                        JVM INSTR ret 6;
                    }


            {
                super();
            }
                }
);
            }
        }

        if(!$assertionsDisabled && j <= 0)
            throw new AssertionError();
        synchronized(mutex)
        {
            for(; finished.get() < j; Utils.waitOn(mutex));
        }
        if(status == 7)
            status = 2;
        logTx(xid.getGlobalTransactionId(), status);
        return readOnly.get();
    }

    private void rollbackAllXaRes()
    {
        if(!$assertionsDisabled && !Thread.holdsLock(this))
            throw new AssertionError();
        status = 9;
        logTx(xid.getGlobalTransactionId(), status);
        int i = xars.size();
        final Object mutex = new Object();
        int j = 0;
        final BoxedInt32 finished = new BoxedInt32(0);
        for(int k = 0; k < i; k++)
        {
            final XAR xar = (XAR)xars.get(k);
            if(xar.getState() != 4)
            {
                j++;
                pool.addTask(new Runnable() {

                    public void run()
                    {
                        xar.getRes().rollback(xar.getBranchXid());
                        Object obj1 = mutex;
                        JVM INSTR monitorenter ;
                        obj1;
                        JVM INSTR monitorexit ;
                        break MISSING_BLOCK_LABEL_192;
                        XAException xaexception;
                        xaexception;
                        synchronized(mutex)
                        {
                            switch(xaexception.errorCode)
                            {
                            case 5: // '\005'
                            case 6: // '\006'
                            case 7: // '\007'
                            case 8: // '\b'
                                onHeuristic(xar, xaexception.errorCode);
                                break;

                            default:
                                log.warning(L10n.format("SRVC.TX.WRN10", xar, TxUtils.getStrXAErrCode(xaexception.errorCode), this, xaexception.getMessage()));
                                break;
                            }
                        }
                        if(true)
                            break MISSING_BLOCK_LABEL_192;
                        local;
                        synchronized(mutex)
                        {
                            finished.preIncr();
                            mutex.notifyAll();
                        }
                        JVM INSTR ret 5;
                    }


            {
                super();
            }
                }
);
            }
        }

        if(!$assertionsDisabled && j <= 0)
            throw new AssertionError();
        synchronized(mutex)
        {
            for(; finished.get() < j; Utils.waitOn(mutex));
        }
        status = 4;
    }

    private void startXar(XAR xar)
        throws XAException
    {
        if(!$assertionsDisabled && !Thread.holdsLock(this))
            throw new AssertionError();
        int i = 0x200000;
        if(xar.getSameRmXar() == null)
        {
            short word0 = xar.getState();
            if(word0 == 0)
                i = 0;
            else
            if(word0 == 2)
                i = 0x8000000;
        }
        xar.getRes().start(xar.getBranchXid(), i);
        xar.setState((short)1);
    }

    private void removeTimeout()
    {
        if(!$assertionsDisabled && timerTask == null)
        {
            throw new AssertionError();
        } else
        {
            timerTask.cancel();
            return;
        }
    }

    private void addTimeout(long l)
    {
        TxManagerImpl.getInstance().getTimer().schedule(timerTask, l);
    }

    private void logTx(byte abyte0[], int i)
    {
        if(!$assertionsDisabled && abyte0 == null)
            throw new AssertionError();
        if(!$assertionsDisabled && tXmlLog == null)
            throw new AssertionError();
        if(!$assertionsDisabled && tBinLog == null)
        {
            throw new AssertionError();
        } else
        {
            tXmlLog.logTx(abyte0, i);
            tBinLog.logTx(abyte0, i);
            return;
        }
    }

    private void logTxRsrcs(byte abyte0[], int i, List list)
    {
        if(!$assertionsDisabled && (abyte0 == null || list == null))
            throw new AssertionError();
        if(!$assertionsDisabled && tXmlLog == null)
            throw new AssertionError();
        tXmlLog.logTxRsrcs(abyte0, i, list);
        if(tBinLog != null)
            tBinLog.logTx(abyte0, i);
    }

    public synchronized String toString()
    {
        return L10n.format("SRVC.TX.IMPL.TXT1", xid, new Long(timeout), TxUtils.getStrStatus(status));
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

    private Logger log;
    private int status;
    private int timeout;
    private List syncs;
    private Xid xid;
    private static final int NO_HEUR_SEEN = 0x7ffffffe;
    private int heuristic;
    private int branchIdGen;
    private boolean ended;
    private List xars;
    private ThreadPool pool;
    private TxXmlLogger tXmlLog;
    private TxRecoveryLogger tBinLog;
    private TimerTask timerTask;
    private boolean forgetHeuristic;
    private Set heuristicExceptionalXars;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(TxImpl.class).desiredAssertionStatus();
    }





}
