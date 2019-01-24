// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.db;


// Referenced classes of package com.fitechlabs.xtier.services.db:
//            DbXaSession, DbException, DbStatementHandler, DbCallableStatementHandler, 
//            DbPreparedStatementHandler, DbSqlParams, DbRow

public interface DbJdbc3XaSession
    extends DbXaSession
{

    public abstract void exec(DbStatementHandler dbstatementhandler, int i, int j, int k)
        throws DbException;

    public abstract void exec(String s, DbCallableStatementHandler dbcallablestatementhandler, int i, int j, int k)
        throws DbException;

    public abstract void exec(String s, DbPreparedStatementHandler dbpreparedstatementhandler, int i, int j, int k)
        throws DbException;

    public abstract void exec(String s, DbPreparedStatementHandler dbpreparedstatementhandler, int ai[])
        throws DbException;

    public abstract void exec(String s, DbPreparedStatementHandler dbpreparedstatementhandler, String as[])
        throws DbException;

    public abstract void exec(String s, DbPreparedStatementHandler dbpreparedstatementhandler, int i)
        throws DbException;

    public abstract DbRow insert(String s, DbSqlParams dbsqlparams, String as[])
        throws DbException;

    public abstract DbRow insert(String s, DbSqlParams dbsqlparams, int ai[])
        throws DbException;
}
