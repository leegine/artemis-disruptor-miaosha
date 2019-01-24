// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.db.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.db.*;
import com.fitechlabs.xtier.utils.ArgAssert;
import java.sql.*;

// Referenced classes of package com.fitechlabs.xtier.services.db.impl:
//            DbSessionImpl, DbDataSourceProps, DbManager, DbSqlExecutor

class DbJdbc3SessionImpl extends DbSessionImpl
    implements DbJdbc3Session
{

    DbJdbc3SessionImpl(String s, DbDataSourceProps dbdatasourceprops, int i, int j, boolean flag)
        throws DbException
    {
        super(s, dbdatasourceprops, i, j, flag);
    }

    public Savepoint setSavepoint()
        throws DbException
    {
        Savepoint savepoint = null;
        checkState();
        checkNotAutoCommit();
        try
        {
            savepoint = conn.setSavepoint();
        }
        catch(SQLException sqlexception)
        {
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, props.getDbType());
        }
        executor.handleSqlWarns();
        return savepoint;
    }

    public Savepoint setSavepoint(String s)
        throws DbException
    {
        ArgAssert.nullArg(s, "name");
        Savepoint savepoint = null;
        checkState();
        checkNotAutoCommit();
        try
        {
            savepoint = conn.setSavepoint(s);
        }
        catch(SQLException sqlexception)
        {
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, props.getDbType());
        }
        executor.handleSqlWarns();
        return savepoint;
    }

    public void rollback(Savepoint savepoint)
        throws DbException
    {
        ArgAssert.nullArg(savepoint, "savepoint");
        checkState();
        checkNotAutoCommit();
        try
        {
            conn.rollback(savepoint);
        }
        catch(SQLException sqlexception)
        {
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, props.getDbType());
        }
        executor.handleSqlWarns();
    }

    public void releaseSavepoint(Savepoint savepoint)
        throws DbException
    {
        ArgAssert.nullArg(savepoint, "savepoint");
        checkState();
        checkNotAutoCommit();
        try
        {
            conn.releaseSavepoint(savepoint);
        }
        catch(SQLException sqlexception)
        {
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, props.getDbType());
        }
        executor.handleSqlWarns();
    }

    public void exec(String s, DbPreparedStatementHandler dbpreparedstatementhandler, String as[])
        throws DbException
    {
        ArgAssert.nullArg(s, "sql");
        ArgAssert.nullArg(dbpreparedstatementhandler, "handler");
        ArgAssert.nullArg(as, "autogenCols");
        init(2);
        try
        {
            executor.exec(s, dbpreparedstatementhandler, as);
        }
        catch(DbException dbexception)
        {
            throw rollbackBeforeException(dbexception);
        }
    }

    public void exec(String s, DbPreparedStatementHandler dbpreparedstatementhandler, int ai[])
        throws DbException
    {
        ArgAssert.nullArg(s, "sql");
        ArgAssert.nullArg(dbpreparedstatementhandler, "handler");
        ArgAssert.nullArg(ai, "autogenCols");
        init(2);
        try
        {
            executor.exec(s, dbpreparedstatementhandler, ai);
        }
        catch(DbException dbexception)
        {
            throw rollbackBeforeException(dbexception);
        }
    }

    public DbRow insert(String s, DbSqlParams dbsqlparams, int ai[])
        throws DbException
    {
        ArgAssert.nullArg(s, "sql");
        ArgAssert.nullArg(ai, "autogenCols");
        init(2);
        try
        {
            return executor.insert(s, dbsqlparams, ai);
        }
        catch(DbException dbexception)
        {
            throw rollbackBeforeException(dbexception);
        }
    }

    public DbRow insert(String s, DbSqlParams dbsqlparams, String as[])
        throws DbException
    {
        ArgAssert.nullArg(s, "sql");
        ArgAssert.nullArg(as, "autogenCols");
        init(2);
        try
        {
            return executor.insert(s, dbsqlparams, as);
        }
        catch(DbException dbexception)
        {
            throw rollbackBeforeException(dbexception);
        }
    }

    public void exec(String s, DbPreparedStatementHandler dbpreparedstatementhandler, int i, int j, int k)
        throws DbException
    {
        ArgAssert.nullArg(s, "sql");
        ArgAssert.nullArg(dbpreparedstatementhandler, "handler");
        checkParams(i, j, k);
        init(2);
        try
        {
            executor.exec(s, dbpreparedstatementhandler, i, j, k);
        }
        catch(DbException dbexception)
        {
            throw rollbackBeforeException(dbexception);
        }
    }

    public void exec(String s, DbPreparedStatementHandler dbpreparedstatementhandler, int i)
        throws DbException
    {
        ArgAssert.nullArg(s, "sql");
        ArgAssert.nullArg(dbpreparedstatementhandler, "handler");
        ArgAssert.illegalArg(i == 1 || i == 2, "autoGeneratedKeys");
        init(2);
        try
        {
            executor.exec(s, dbpreparedstatementhandler, i);
        }
        catch(DbException dbexception)
        {
            throw rollbackBeforeException(dbexception);
        }
    }

    public void exec(String s, DbCallableStatementHandler dbcallablestatementhandler, int i, int j, int k)
        throws DbException
    {
        ArgAssert.nullArg(s, "sql");
        ArgAssert.nullArg(dbcallablestatementhandler, "handler");
        checkParams(i, j, k);
        init(2);
        try
        {
            executor.exec(s, dbcallablestatementhandler, i, j, k);
        }
        catch(DbException dbexception)
        {
            throw rollbackBeforeException(dbexception);
        }
    }

    public void exec(DbStatementHandler dbstatementhandler, int i, int j, int k)
        throws DbException
    {
        ArgAssert.nullArg(dbstatementhandler, "handler");
        checkParams(i, j, k);
        init(2);
        try
        {
            executor.exec(dbstatementhandler, i, j, k);
        }
        catch(DbException dbexception)
        {
            throw rollbackBeforeException(dbexception);
        }
    }

    private void checkParams(int i, int j, int k)
    {
        checkParams(i, j);
        ArgAssert.illegalArg(k == 1 || k == 2, "rsHold");
    }
}
