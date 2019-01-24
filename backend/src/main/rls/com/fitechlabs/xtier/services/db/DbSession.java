// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.db;

import java.util.List;
import java.util.Map;

// Referenced classes of package com.fitechlabs.xtier.services.db:
//            DbException, DbSqlParams, DbRow, DbRowHandler, 
//            DbResultSetHandler, DbObjectHandler, DbLazyRowList, DbStatementHandler, 
//            DbPreparedStatementHandler, DbCallableStatementHandler

public interface DbSession
{

    public abstract void rollback()
        throws DbException;

    public abstract String getDsName();

    public abstract String getName();

    public abstract Map getTypeMap()
        throws DbException;

    public abstract void setTypeMap(Map map)
        throws DbException;

    public abstract int getTxIsolationLevel();

    public abstract int getTxCommitType();

    public abstract void close()
        throws DbException;

    public abstract DbRow select(String s, DbSqlParams dbsqlparams)
        throws DbException;

    public abstract void select(String s, DbSqlParams dbsqlparams, DbRowHandler dbrowhandler)
        throws DbException;

    public abstract void select(String s, DbSqlParams dbsqlparams, DbResultSetHandler dbresultsethandler)
        throws DbException;

    public abstract void select(String s, DbSqlParams dbsqlparams, DbObjectHandler dbobjecthandler, Class class1)
        throws DbException;

    public abstract Object select(String s, DbSqlParams dbsqlparams, Class class1)
        throws DbException;

    public abstract List selectList(String s, DbSqlParams dbsqlparams, int i, boolean flag)
        throws DbException;

    public abstract DbLazyRowList selectLazyList(String s, DbSqlParams dbsqlparams)
        throws DbException;

    public abstract DbLazyRowList selectLazyList(String s, DbSqlParams dbsqlparams, int i)
        throws DbException;

    public abstract int delete(String s, DbSqlParams dbsqlparams)
        throws DbException;

    public abstract int update(String s, DbSqlParams dbsqlparams)
        throws DbException;

    public abstract void insert(String s, DbSqlParams dbsqlparams)
        throws DbException;

    public abstract void sql(String s, DbSqlParams dbsqlparams)
        throws DbException;

    public abstract void exec(DbStatementHandler dbstatementhandler)
        throws DbException;

    public abstract void exec(DbStatementHandler dbstatementhandler, int i, int j)
        throws DbException;

    public abstract void exec(String s, DbPreparedStatementHandler dbpreparedstatementhandler)
        throws DbException;

    public abstract void exec(String s, DbPreparedStatementHandler dbpreparedstatementhandler, int i, int j)
        throws DbException;

    public abstract void exec(String s, DbCallableStatementHandler dbcallablestatementhandler)
        throws DbException;

    public abstract void exec(String s, DbCallableStatementHandler dbcallablestatementhandler, int i, int j)
        throws DbException;

    public abstract int psqlDelete(String s, Object obj)
        throws DbException;

    public abstract int psqlUpdate(String s, Object obj)
        throws DbException;

    public abstract DbRow psqlInsert(String s, Object obj)
        throws DbException;

    public abstract void psqlExec(String s, Object obj, DbResultSetHandler dbresultsethandler)
        throws DbException;

    public abstract Object psqlSelectObj(String s, Object obj)
        throws DbException;

    public abstract List psqlSelectObjs(String s, Object obj, int i, boolean flag)
        throws DbException;

    public abstract DbRow psqlSelect(String s, Object obj)
        throws DbException;

    public abstract List psqlSelectList(String s, Object obj, int i, boolean flag)
        throws DbException;

    public abstract DbLazyRowList psqlSelectLazyList(String s, Object obj)
        throws DbException;

    public abstract DbLazyRowList psqlSelectLazyList(String s, Object obj, int i)
        throws DbException;

    public abstract void commit()
        throws DbException;
}
