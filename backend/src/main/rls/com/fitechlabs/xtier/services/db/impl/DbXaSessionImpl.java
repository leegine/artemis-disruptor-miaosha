// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.db.impl;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.db.*;
import com.fitechlabs.xtier.services.tx.TxService;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.Utils;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.*;

// Referenced classes of package com.fitechlabs.xtier.services.db.impl:
//            DbDataSourceProps, DbSqlExecutor, DbManager

class DbXaSessionImpl
    implements DbXaSession
{

    DbXaSessionImpl(DbDataSourceProps dbdatasourceprops)
        throws DbException
    {
        executor = null;
        conn = null;
        tm = null;
        if(!$assertionsDisabled && dbdatasourceprops == null)
        {
            throw new AssertionError();
        } else
        {
            props = new DbDataSourceProps(dbdatasourceprops);
            thread = Thread.currentThread();
            dbMgr = DbManager.getInstance();
            return;
        }
    }

    protected void init()
        throws DbException
    {
        if(thread != Thread.currentThread())
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR41"));
        if(executor == null)
        {
            if(tm == null)
                tm = XtierKernel.getInstance().tx().getTransactionManager();
            Transaction transaction = null;
            try
            {
                transaction = tm.getTransaction();
            }
            catch(SystemException systemexception)
            {
                throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR37"), systemexception);
            }
            if(transaction == null)
                throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR38"));
            String s = props.getUser();
            String s1 = props.getPassword();
            if(!$assertionsDisabled && s == null && s1 != null)
                throw new AssertionError();
            XADataSource xadatasource = dbMgr.getStartedXaDs(props.getName());
            try
            {
                conn = s != null ? xadatasource.getXAConnection(s, s1 != null ? s1 : "") : xadatasource.getXAConnection();
            }
            catch(SQLException sqlexception)
            {
                throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, props.getDbType());
            }
            try
            {
                transaction.enlistResource(conn.getXAResource());
                executor = new DbSqlExecutor(conn.getConnection(), props);
                transaction.registerSynchronization(new Synchronization() {

                    public void beforeCompletion()
                    {
                    }

                    public void afterCompletion(int i)
                    {
                        Utils.close(conn);
                        executor = null;
                    }


            {
                super();
            }
                }
);
            }
            catch(SystemException systemexception1)
            {
                throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR39"), systemexception1);
            }
            catch(RollbackException rollbackexception)
            {
                throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR40"), rollbackexception);
            }
            catch(SQLException sqlexception1)
            {
                throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception1, props.getDbType());
            }
        }
    }

    public String getDsName()
    {
        return props.getName();
    }

    public Map getTypeMap()
        throws DbException
    {
        init();
        return executor.getTypeMap();
    }

    public void setTypeMap(Map map)
        throws DbException
    {
        ArgAssert.nullArg(map, "map");
        init();
        executor.setTypeMap(map);
    }

    public DbRow select(String s, DbSqlParams dbsqlparams)
        throws DbException
    {
        ArgAssert.nullArg(s, "sql");
        init();
        return executor.select(s, dbsqlparams);
    }

    public void select(String s, DbSqlParams dbsqlparams, DbRowHandler dbrowhandler)
        throws DbException
    {
        ArgAssert.nullArg(s, "sql");
        ArgAssert.nullArg(dbrowhandler, "handler");
        init();
        executor.select(s, dbsqlparams, dbrowhandler);
    }

    public void select(String s, DbSqlParams dbsqlparams, DbResultSetHandler dbresultsethandler)
        throws DbException
    {
        ArgAssert.nullArg(s, "sql");
        ArgAssert.nullArg(dbresultsethandler, "handler");
        init();
        executor.select(s, dbsqlparams, dbresultsethandler);
    }

    public void select(String s, DbSqlParams dbsqlparams, DbObjectHandler dbobjecthandler, Class class1)
        throws DbException
    {
        ArgAssert.nullArg(s, "sql");
        ArgAssert.nullArg(dbobjecthandler, "handler");
        ArgAssert.nullArg(class1, "objType");
        init();
        executor.select(s, dbsqlparams, dbobjecthandler, class1);
    }

    public Object select(String s, DbSqlParams dbsqlparams, Class class1)
        throws DbException
    {
        ArgAssert.nullArg(s, "sql");
        ArgAssert.nullArg(class1, "objType");
        init();
        return executor.select(s, dbsqlparams, class1);
    }

    public List selectList(String s, DbSqlParams dbsqlparams, int i, boolean flag)
        throws DbException
    {
        ArgAssert.nullArg(s, "sql");
        ArgAssert.illegalRange(i > 0, "maxRows", "maxRows > 0");
        init();
        return executor.selectList(s, dbsqlparams, i, flag);
    }

    public DbLazyRowList selectLazyList(String s, DbSqlParams dbsqlparams)
        throws DbException
    {
        ArgAssert.nullArg(s, "sql");
        init();
        return executor.selectLazyList(s, dbsqlparams);
    }

    public DbLazyRowList selectLazyList(String s, DbSqlParams dbsqlparams, int i)
        throws DbException
    {
        ArgAssert.nullArg(s, "sql");
        ArgAssert.illegalRange(i > 0, "pageSize", "pageSize > 0");
        init();
        return executor.selectLazyList(s, dbsqlparams, i);
    }

    public int delete(String s, DbSqlParams dbsqlparams)
        throws DbException
    {
        ArgAssert.nullArg(s, "sql");
        init();
        return executor.delete(s, dbsqlparams);
    }

    public int update(String s, DbSqlParams dbsqlparams)
        throws DbException
    {
        ArgAssert.nullArg(s, "sql");
        init();
        return executor.update(s, dbsqlparams);
    }

    public void sql(String s, DbSqlParams dbsqlparams)
        throws DbException
    {
        ArgAssert.nullArg(s, "sql");
        init();
        executor.runSql(s, dbsqlparams);
    }

    public void insert(String s, DbSqlParams dbsqlparams)
        throws DbException
    {
        ArgAssert.nullArg(s, "sql");
        init();
        executor.insert(s, dbsqlparams);
    }

    public void exec(DbStatementHandler dbstatementhandler)
        throws DbException
    {
        ArgAssert.nullArg(dbstatementhandler, "handler");
        init();
        executor.exec(dbstatementhandler);
    }

    public void exec(DbStatementHandler dbstatementhandler, int i, int j)
        throws DbException
    {
        ArgAssert.nullArg(dbstatementhandler, "handler");
        checkParams(i, j);
        init();
        executor.exec(dbstatementhandler, i, j);
    }

    public void exec(String s, DbPreparedStatementHandler dbpreparedstatementhandler, int i, int j)
        throws DbException
    {
        ArgAssert.nullArg(s, "sql");
        ArgAssert.nullArg(dbpreparedstatementhandler, "handler");
        checkParams(i, j);
        init();
        executor.exec(s, dbpreparedstatementhandler, i, j);
    }

    public void exec(String s, DbPreparedStatementHandler dbpreparedstatementhandler)
        throws DbException
    {
        ArgAssert.nullArg(s, "sql");
        ArgAssert.nullArg(dbpreparedstatementhandler, "handler");
        init();
        executor.exec(s, dbpreparedstatementhandler);
    }

    public void exec(String s, DbCallableStatementHandler dbcallablestatementhandler)
        throws DbException
    {
        ArgAssert.nullArg(s, "sql");
        ArgAssert.nullArg(dbcallablestatementhandler, "handler");
        init();
        executor.exec(s, dbcallablestatementhandler);
    }

    public void exec(String s, DbCallableStatementHandler dbcallablestatementhandler, int i, int j)
        throws DbException
    {
        ArgAssert.nullArg(s, "sql");
        ArgAssert.nullArg(dbcallablestatementhandler, "handler");
        checkParams(i, j);
        init();
        executor.exec(s, dbcallablestatementhandler, i, j);
    }

    public int psqlDelete(String s, Object obj)
        throws DbException
    {
        ArgAssert.nullArg(s, "name");
        ArgAssert.nullArg(obj, "obj");
        init();
        return executor.psqlDelete(s, obj);
    }

    public int psqlUpdate(String s, Object obj)
        throws DbException
    {
        ArgAssert.nullArg(s, "name");
        ArgAssert.nullArg(obj, "obj");
        init();
        return executor.psqlUpdate(s, obj);
    }

    public DbRow psqlInsert(String s, Object obj)
        throws DbException
    {
        ArgAssert.nullArg(s, "name");
        ArgAssert.nullArg(obj, "obj");
        init();
        return executor.psqlInsert(s, obj);
    }

    public void psqlExec(String s, Object obj, DbResultSetHandler dbresultsethandler)
        throws DbException
    {
        ArgAssert.nullArg(s, "name");
        ArgAssert.nullArg(obj, "obj");
        ArgAssert.nullArg(dbresultsethandler, "handler");
        init();
        executor.psqlExec(s, obj, dbresultsethandler);
    }

    public Object psqlSelectObj(String s, Object obj)
        throws DbException
    {
        ArgAssert.nullArg(s, "name");
        ArgAssert.nullArg(obj, "obj");
        init();
        return executor.psqlSelectObj(s, obj);
    }

    public List psqlSelectObjs(String s, Object obj, int i, boolean flag)
        throws DbException
    {
        ArgAssert.nullArg(s, "name");
        ArgAssert.nullArg(obj, "obj");
        ArgAssert.illegalRange(i > 0, "maxRows", "maxRows > 0");
        init();
        return executor.psqlSelectObjs(s, obj, i, flag);
    }

    public DbRow psqlSelect(String s, Object obj)
        throws DbException
    {
        ArgAssert.nullArg(s, "name");
        ArgAssert.nullArg(obj, "obj");
        init();
        return executor.psqlSelect(s, obj);
    }

    public List psqlSelectList(String s, Object obj, int i, boolean flag)
        throws DbException
    {
        ArgAssert.nullArg(s, "name");
        ArgAssert.nullArg(obj, "obj");
        ArgAssert.illegalRange(i > 0, "maxRows", "maxRows > 0");
        init();
        return executor.psqlSelectList(s, obj, i, flag);
    }

    public DbLazyRowList psqlSelectLazyList(String s, Object obj)
        throws DbException
    {
        ArgAssert.nullArg(s, "name");
        ArgAssert.nullArg(obj, "obj");
        init();
        return executor.psqlSelectLazyList(s, obj);
    }

    public DbLazyRowList psqlSelectLazyList(String s, Object obj, int i)
        throws DbException
    {
        ArgAssert.nullArg(s, "name");
        ArgAssert.nullArg(obj, "obj");
        ArgAssert.illegalRange(i > 0, "pageSize", "pageSize > 0");
        init();
        return executor.psqlSelectLazyList(s, obj, i);
    }

    protected void checkParams(int i, int j)
    {
        ArgAssert.illegalArg(i == 1003 || i == 1004 || i == 1005, "rsType");
        ArgAssert.illegalArg(j == 1007 || j == 1008, "rsCcy");
    }

    void closeImpl()
    {
        Utils.close(conn);
        executor = null;
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

    private DbDataSourceProps props;
    private DbManager dbMgr;
    private Thread thread;
    protected DbSqlExecutor executor;
    private XAConnection conn;
    private TransactionManager tm;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(DbXaSessionImpl.class).desiredAssertionStatus();
    }

}
