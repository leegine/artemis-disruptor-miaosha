// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   TxManager.java

package co.fin.intellioms.tx;

import co.fin.intellioms.util.Log;
//import com.com.xtier.kernel.XtierKernel;
//import com.com.xtier.services.db.DbException;
//import com.com.xtier.services.db.DbService;
//import com.com.xtier.utils.boxed.sync.BoxedBooleanSync;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

// Referenced classes of package com.com.fin.intellioms.tx:
//            TxManagerException, CommitCallback

public class TxManager
{
    private static class TxContext
    {

        Connection getConnection()
            throws TxManagerException
        {
            if(rollbackOnly)
                throw new TxManagerException("Unable to get database connection: Transaction rollbacked.");
            if(connection == null)
            {
                if(TxManager.log.isDebug())
                    TxManager.log.debug("Getting database connection from the pool.");
                try
                {
                    connection = getDs().getConnection();
                    connection.setAutoCommit(false);
                }
                catch(SQLException e)
                {
                    throw new TxManagerException((new StringBuilder()).append("Unable to acquire database connection: ").append(e.getMessage()).toString(), e);
                }
                catch(IllegalStateException e)
                {
                    throw new TxManagerException((new StringBuilder()).append("Unable to acquire database connection: ").append(e.getMessage()).toString(), e);
                }
            }
            return connection;
        }

        void addCallback(CommitCallback cb)
        {
            if(commitCallbacks == null)
                commitCallbacks = new ArrayList();
            commitCallbacks.add(cb);
        }

        void begin()
            throws TxManagerException
        {
            if(rollbackOnly)
                throw new TxManagerException("Unable to begin: transaction already rollbacked.");
            deepthCounter++;
            if(TxManager.log.isDebug())
                TxManager.log.debug((new StringBuilder()).append("Incremented deepth counter to ").append(deepthCounter).toString());
        }

        void commit()
            throws TxManagerException
        {
            if(rollbackOnly)
                throw new TxManagerException("Unable to commit: Transaction already rollbacked.");
            deepthCounter--;
            if(TxManager.log.isDebug())
                TxManager.log.debug((new StringBuilder()).append("Decremented deepth counter to ").append(deepthCounter).toString());
//            if(deepthCounter != 0)
//                break MISSING_BLOCK_LABEL_408;
//            if(TxManager.log.isDebug())
//                TxManager.log.debug("Commiting transaction...");
//            if(connection == null)
//                break MISSING_BLOCK_LABEL_174;
//            if(TxManager.log.isDebug())
//                TxManager.log.debug("Committing database connection.");
            try
            {
                connection.commit();
            }
            catch(SQLException e)
            {
                throw new TxManagerException((new StringBuilder()).append("Unable to commit transaction: ").append(e.getMessage()).toString(), e);
            }
            releaseConnection();
//            break MISSING_BLOCK_LABEL_192;
//            Exception exception;
//            exception;
//            releaseConnection();
//            throw exception;
            if(TxManager.log.isDebug())
                TxManager.log.debug("Skipping database connection commit: connection not bound to current thread.");
            TxManager.local.set(null);
            if(TxManager.log.isDebug())
                TxManager.log.debug("Transaction context disposed.");
//            break MISSING_BLOCK_LABEL_248;
//            Exception exception1;
//            exception1;
            TxManager.local.set(null);
            if(TxManager.log.isDebug())
                TxManager.log.debug("Transaction context disposed.");
//            throw exception1;
            if(commitCallbacks != null)
            {
                if(TxManager.log.isDebug())
                    TxManager.log.debug((new StringBuilder()).append("Processing ").append(commitCallbacks.size()).append(" transaction commit callbacks.").toString());
                for(int i = 0; i < commitCallbacks.size(); i++)
                {
                    CommitCallback cb = (CommitCallback)commitCallbacks.get(i);
                    try
                    {
                        cb.onCommit();
                        continue;
                    }
                    catch(RuntimeException e)
                    {
                        if(TxManager.log.isError())
                            TxManager.log.error((new StringBuilder()).append("Caught unexpected runtime exception while processing transaction commit callbacks: ").append(e.getMessage()).toString(), e);
                    }
                }

            }
            if(TxManager.log.isDebug())
                TxManager.log.debug("Transaction committed.");
//            break MISSING_BLOCK_LABEL_451;
            if(TxManager.log.isDebug())
                TxManager.log.debug((new StringBuilder()).append("Skipped commit: transction is not root (deepth counter = ").append(deepthCounter).append(").").toString());
        }

        boolean isRootContext()
        {
            return deepthCounter == 0;
        }

        private void releaseConnection()
        {
            if(TxManager.log.isDebug())
                TxManager.log.debug("Releasing database connection.");
            try
            {
                connection.close();
                connection = null;
            }
            catch(IllegalStateException e)
            {
                if(TxManager.log.isError())
                    TxManager.log.error("Unable to close database connection.", e);
            }
            catch(SQLException e)
            {
                if(TxManager.log.isError())
                    TxManager.log.error("Unable to close database connection.", e);
            }
        }

        private DataSource getDs()
        {
            Object obj = TxManager.dsMux;
//            JVM INSTR monitorenter ;
            return TxManager.ds;
//            Exception exception;
//            exception;
//            throw exception;
        }

        void rollback()
            throws TxManagerException
        {
            deepthCounter--;
            if(TxManager.log.isDebug())
                TxManager.log.debug((new StringBuilder()).append("Decremented deepth counter to ").append(deepthCounter).toString());
            if(TxManager.log.isDebug())
                TxManager.log.debug("Rollbacking transaction...");
//            if(rollbackOnly)
//                break MISSING_BLOCK_LABEL_168;
//            if(connection == null || connection.isClosed())
//                break MISSING_BLOCK_LABEL_186;
//            if(TxManager.log.isDebug())
//                TxManager.log.debug("Rollbacking database connection.");
//            SQLException e;
            try
            {
                connection.rollback();
            }
            // Misplaced declaration of an exception variable
            catch(SQLException e)
            {
                throw new TxManagerException((new StringBuilder()).append("Unable to rollback transaction: ").append(e.getMessage()).toString(), e);
            }
            releaseConnection();
//            break MISSING_BLOCK_LABEL_186;
//            Exception exception;
//            exception;
//            releaseConnection();
//            throw exception;
            if(TxManager.log.isDebug())
                TxManager.log.debug("Skipping database connection rollback: transaction already rollbacked.");
            if(deepthCounter == 0)
            {
                TxManager.local.set(null);
                if(TxManager.log.isDebug())
                {
                    TxManager.log.debug("Transaction context disposed.");
                    TxManager.log.debug("Transaction rollbacked.");
                }
            }
//            break MISSING_BLOCK_LABEL_355;
//            e;
//            if(TxManager.log.isError())
//                TxManager.log.error((new StringBuilder()).append("Unable to rollback: ").append(e.getMessage()).toString(), e);
            if(deepthCounter == 0)
            {
                TxManager.local.set(null);
                if(TxManager.log.isDebug())
                {
                    TxManager.log.debug("Transaction context disposed.");
                    TxManager.log.debug("Transaction rollbacked.");
                }
            }
//            break MISSING_BLOCK_LABEL_355;
//            Exception exception1;
//            exception1;
            if(deepthCounter == 0)
            {
                TxManager.local.set(null);
                if(TxManager.log.isDebug())
                {
                    TxManager.log.debug("Transaction context disposed.");
                    TxManager.log.debug("Transaction rollbacked.");
                }
            }
//            throw exception1;
//            rollbackOnly = true;
//            return;
        }

        private int deepthCounter;
        private boolean rollbackOnly;
        private Connection connection;
        private List commitCallbacks;

        private TxContext()
        {
        }

    }


    public TxManager()
    {
    }

    private static void checkState()
    {
//        if(!initialized.get())
//            throw new IllegalStateException("Transaction manager is not initialized.");
//        else
//            return;
    }

    public static void initialize()
    {
//        try
//        {
//            initialize(XtierKernel.getInstance().db().getDs("rulesys-ds"));
//        }
//        catch(DbException e)
//        {
//            throw new IllegalStateException((new StringBuilder()).append("Unable to initialize Tx manager: ").append(e.getClass().getName()).append(": ").append(e.getMessage()).toString());
//        }
    }

    public static void initialize(DataSource ds)
    {
//        if(initialized.get())
//            throw new IllegalStateException("Transaction manager already initialized.");
//        initialized.set(true);
//        log = Log.getLogger(com/com/fin/intellioms/tx/TxManager);
//        synchronized(dsMux)
//        {
//            ds = ds;
//        }
    }

    public static void dispose()
    {
        checkState();
//        initialized.set(false);
        log = null;
        synchronized(dsMux)
        {
            ds = null;
        }
    }

    public static void begin()
        throws TxManagerException
    {
        checkState();
        TxContext ctx = (TxContext)local.get();
        if(ctx == null)
        {
            ctx = new TxContext();
            local.set(ctx);
            if(log.isDebug())
                log.debug("Created new transaction context.");
        } else
        if(log.isDebug())
            log.debug("Joined existing transaction.");
        ctx.begin();
    }

    public static void commit()
        throws TxManagerException
    {
        checkState();
        TxContext ctx = (TxContext)local.get();
        if(ctx == null)
        {
            throw new TxManagerException("Transaction context not bound to current thread.");
        } else
        {
            ctx.commit();
            return;
        }
    }

    public static Connection getConnection()
        throws TxManagerException
    {
        checkState();
        TxContext ctx = (TxContext)local.get();
        if(ctx == null)
            throw new TxManagerException("Transaction context not bound to current thread.");
        else
            return ctx.getConnection();
    }

    public static void rollback()
        throws TxManagerException
    {
        checkState();
        TxContext ctx = (TxContext)local.get();
        if(ctx == null)
        {
            throw new TxManagerException("Transaction context not bound to current thread.");
        } else
        {
            ctx.rollback();
            return;
        }
    }

    public static void addCallback(CommitCallback cb)
        throws TxManagerException
    {
//        if(!$assertionsDisabled && cb == null)
//            throw new AssertionError("Callback is null.");
        checkState();
        TxContext ctx = (TxContext)local.get();
        if(ctx == null)
        {
            throw new TxManagerException("Transaction context not bound to current thread.");
        } else
        {
            ctx.addCallback(cb);
            return;
        }
    }

    private static Log log;
    private static final ThreadLocal local = new ThreadLocal();
//    private static BoxedBooleanSync initialized = new BoxedBooleanSync(false);
    private static DataSource ds;
    private static final Object dsMux = new Object();
//    static final boolean $assertionsDisabled = !com/com/fin/intellioms/tx/TxManager.desiredAssertionStatus();





}
