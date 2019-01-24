// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.db.impl;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.db.*;
import com.fitechlabs.xtier.services.log.LogService;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.Utils;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

// Referenced classes of package com.fitechlabs.xtier.services.db.impl:
//            DbDataSourceProps, DbSqlExecutor, DbManager

class DbSessionImpl
    implements DbSession
{

    DbSessionImpl(String s, DbDataSourceProps dbdatasourceprops, int i, int j, boolean flag)
        throws DbException
    {
        conn = null;
        executor = null;
        if(!$assertionsDisabled && (s == null || dbdatasourceprops == null))
        {
            throw new AssertionError();
        } else
        {
            name = s;
            txIsoLevel = i;
            txCmtType = j;
            rollbackOnException = flag;
            props = new DbDataSourceProps(dbdatasourceprops);
            dbMgr = DbManager.getInstance();
            state = 1;
            return;
        }
    }

    protected void init(int i)
        throws DbException
    {
        if(!$assertionsDisabled && i != 2 && i != 3)
            throw new AssertionError();
        if(state == 4)
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR34"));
        if(state == 1)
        {
            String s = props.getUser();
            String s1 = props.getPassword();
            if(!$assertionsDisabled && s == null && s1 != null)
                throw new AssertionError();
            DataSource datasource = dbMgr.getStartedDs(props.getName());
            try
            {
                conn = s != null ? datasource.getConnection(s, s1 != null ? s1 : "") : datasource.getConnection();
            }
            catch(SQLException sqlexception)
            {
                throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, 6, props.getDbType());
            }
            try
            {
                switch(txCmtType)
                {
                case 101: // 'e'
                    conn.setAutoCommit(true);
                    break;

                case 102: // 'f'
                    conn.setAutoCommit(false);
                    break;

                default:
                    if(!$assertionsDisabled)
                        throw new AssertionError();
                    break;
                }
                switch(txIsoLevel)
                {
                case 2: // '\002'
                    conn.setTransactionIsolation(1);
                    break;

                case 5: // '\005'
                    break;

                case 1: // '\001'
                    conn.setTransactionIsolation(2);
                    break;

                case 3: // '\003'
                    conn.setTransactionIsolation(4);
                    break;

                case 4: // '\004'
                    conn.setTransactionIsolation(8);
                    break;

                default:
                    if(!$assertionsDisabled)
                        throw new AssertionError();
                    break;
                }
            }
            catch(SQLException sqlexception1)
            {
                throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception1, props.getDbType());
            }
            executor = new DbSqlExecutor(conn, props);
            state = i;
        }
    }

    public void commit()
        throws DbException
    {
        checkState();
        checkNotAutoCommit();
        try
        {
            conn.commit();
            state = 3;
        }
        catch(SQLException sqlexception)
        {
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, props.getDbType());
        }
        executor.handleSqlWarns();
    }

    public void rollback()
        throws DbException
    {
        checkState();
        checkNotAutoCommit();
        try
        {
            conn.rollback();
            state = 3;
        }
        catch(SQLException sqlexception)
        {
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, props.getDbType());
        }
        executor.handleSqlWarns();
    }

    public String getDsName()
    {
        return props.getName();
    }

    public String getName()
    {
        return name;
    }

    public Map getTypeMap()
        throws DbException
    {
        init(3);
        return executor.getTypeMap();
    }

    public void setTypeMap(Map map)
        throws DbException
    {
        ArgAssert.nullArg(map, "map");
        init(3);
        executor.setTypeMap(map);
    }

    public int getTxIsolationLevel()
    {
        return txIsoLevel;
    }

    public int getTxCommitType()
    {
        return txCmtType;
    }

    public void close()
        throws DbException
    {
        if(state == 4)
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR62", name));
        if(state == 2 && txCmtType != 101)
            XtierKernel.getInstance().log().getLogger("db").getLogger(name).warning(L10n.format("SRVC.DB.WRN2", name));
        Utils.close(conn);
        state = 4;
        dbMgr.deleteSession(name);
    }

    void closeImpl()
    {
        if(state == 2 && txCmtType != 101)
            XtierKernel.getInstance().log().getLogger("db").getLogger(name).warning(L10n.format("SRVC.DB.WRN2", name));
        Utils.close(conn);
        state = 4;
    }

    public DbRow select(String s, DbSqlParams dbsqlparams)
        throws DbException
    {
        ArgAssert.nullArg(s, "sql");
        init(2);
        try
        {
            return executor.select(s, dbsqlparams);
        }
        catch(DbException dbexception)
        {
            throw rollbackBeforeException(dbexception);
        }
    }

    public void select(String s, DbSqlParams dbsqlparams, DbRowHandler dbrowhandler)
        throws DbException
    {
        ArgAssert.nullArg(s, "sql");
        ArgAssert.nullArg(dbrowhandler, "handler");
        init(2);
        try
        {
            executor.select(s, dbsqlparams, dbrowhandler);
        }
        catch(DbException dbexception)
        {
            throw rollbackBeforeException(dbexception);
        }
    }

    public void select(String s, DbSqlParams dbsqlparams, DbResultSetHandler dbresultsethandler)
        throws DbException
    {
        ArgAssert.nullArg(s, "sql");
        ArgAssert.nullArg(dbresultsethandler, "handler");
        init(2);
        try
        {
            executor.select(s, dbsqlparams, dbresultsethandler);
        }
        catch(DbException dbexception)
        {
            throw rollbackBeforeException(dbexception);
        }
    }

    public void select(String s, DbSqlParams dbsqlparams, DbObjectHandler dbobjecthandler, Class class1)
        throws DbException
    {
        ArgAssert.nullArg(s, "sql");
        ArgAssert.nullArg(dbobjecthandler, "handler");
        ArgAssert.nullArg(class1, "objType");
        init(2);
        try
        {
            executor.select(s, dbsqlparams, dbobjecthandler, class1);
        }
        catch(DbException dbexception)
        {
            throw rollbackBeforeException(dbexception);
        }
    }

    public Object select(String s, DbSqlParams dbsqlparams, Class class1)
        throws DbException
    {
        ArgAssert.nullArg(s, "sql");
        ArgAssert.nullArg(class1, "objType");
        init(2);
        try
        {
            return executor.select(s, dbsqlparams, class1);
        }
        catch(DbException dbexception)
        {
            throw rollbackBeforeException(dbexception);
        }
    }

    public List selectList(String s, DbSqlParams dbsqlparams, int i, boolean flag)
        throws DbException
    {
        ArgAssert.nullArg(s, "sql");
        ArgAssert.illegalRange(i > 0, "maxRows", "maxRows > 0");
        init(2);
        try
        {
            return executor.selectList(s, dbsqlparams, i, flag);
        }
        catch(DbException dbexception)
        {
            throw rollbackBeforeException(dbexception);
        }
    }

    public DbLazyRowList selectLazyList(String s, DbSqlParams dbsqlparams)
        throws DbException
    {
        ArgAssert.nullArg(s, "sql");
        init(2);
        try
        {
            return executor.selectLazyList(s, dbsqlparams);
        }
        catch(DbException dbexception)
        {
            throw rollbackBeforeException(dbexception);
        }
    }

    public DbLazyRowList selectLazyList(String s, DbSqlParams dbsqlparams, int i)
        throws DbException
    {
        ArgAssert.nullArg(s, "sql");
        ArgAssert.illegalRange(i > 0, "pageSize", "pageSize > 0");
        init(2);
        try
        {
            return executor.selectLazyList(s, dbsqlparams, i);
        }
        catch(DbException dbexception)
        {
            throw rollbackBeforeException(dbexception);
        }
    }

    public int delete(String s, DbSqlParams dbsqlparams)
        throws DbException
    {
        ArgAssert.nullArg(s, "sql");
        init(2);
        try
        {
            return executor.delete(s, dbsqlparams);
        }
        catch(DbException dbexception)
        {
            throw rollbackBeforeException(dbexception);
        }
    }

    public int update(String s, DbSqlParams dbsqlparams)
        throws DbException
    {
        ArgAssert.nullArg(s, "sql");
        init(2);
        try
        {
            return executor.update(s, dbsqlparams);
        }
        catch(DbException dbexception)
        {
            throw rollbackBeforeException(dbexception);
        }
    }

    public void sql(String s, DbSqlParams dbsqlparams)
        throws DbException
    {
        ArgAssert.nullArg(s, "sql");
        init(2);
        try
        {
            executor.runSql(s, dbsqlparams);
        }
        catch(DbException dbexception)
        {
            throw rollbackBeforeException(dbexception);
        }
    }

    public void insert(String s, DbSqlParams dbsqlparams)
        throws DbException
    {
        ArgAssert.nullArg(s, "sql");
        init(2);
        try
        {
            executor.insert(s, dbsqlparams);
        }
        catch(DbException dbexception)
        {
            throw rollbackBeforeException(dbexception);
        }
    }

    protected void checkParams(int i, int j)
    {
        ArgAssert.illegalArg(i == 1003 || i == 1004 || i == 1005, "rsType");
        ArgAssert.illegalArg(j == 1007 || j == 1008, "rsCcy");
    }

    public void exec(DbStatementHandler dbstatementhandler)
        throws DbException
    {
        ArgAssert.nullArg(dbstatementhandler, "handler");
        init(2);
        try
        {
            executor.exec(dbstatementhandler);
        }
        catch(DbException dbexception)
        {
            throw rollbackBeforeException(dbexception);
        }
    }

    public void exec(DbStatementHandler dbstatementhandler, int i, int j)
        throws DbException
    {
        ArgAssert.nullArg(dbstatementhandler, "handler");
        checkParams(i, j);
        init(2);
        try
        {
            executor.exec(dbstatementhandler, i, j);
        }
        catch(DbException dbexception)
        {
            throw rollbackBeforeException(dbexception);
        }
    }

    public void exec(String s, DbPreparedStatementHandler dbpreparedstatementhandler, int i, int j)
        throws DbException
    {
        ArgAssert.nullArg(s, "sql");
        ArgAssert.nullArg(dbpreparedstatementhandler, "handler");
        checkParams(i, j);
        init(2);
        try
        {
            executor.exec(s, dbpreparedstatementhandler, i, j);
        }
        catch(DbException dbexception)
        {
            throw rollbackBeforeException(dbexception);
        }
    }

    public void exec(String s, DbPreparedStatementHandler dbpreparedstatementhandler)
        throws DbException
    {
        ArgAssert.nullArg(s, "sql");
        ArgAssert.nullArg(dbpreparedstatementhandler, "handler");
        init(2);
        try
        {
            executor.exec(s, dbpreparedstatementhandler);
        }
        catch(DbException dbexception)
        {
            throw rollbackBeforeException(dbexception);
        }
    }

    public void exec(String s, DbCallableStatementHandler dbcallablestatementhandler)
        throws DbException
    {
        ArgAssert.nullArg(s, "sql");
        ArgAssert.nullArg(dbcallablestatementhandler, "handler");
        init(2);
        try
        {
            executor.exec(s, dbcallablestatementhandler);
        }
        catch(DbException dbexception)
        {
            throw rollbackBeforeException(dbexception);
        }
    }

    public void exec(String s, DbCallableStatementHandler dbcallablestatementhandler, int i, int j)
        throws DbException
    {
        ArgAssert.nullArg(s, "sql");
        ArgAssert.nullArg(dbcallablestatementhandler, "handler");
        checkParams(i, j);
        init(2);
        try
        {
            executor.exec(s, dbcallablestatementhandler, i, j);
        }
        catch(DbException dbexception)
        {
            throw rollbackBeforeException(dbexception);
        }
    }

    public int psqlDelete(String s, Object obj)
        throws DbException
    {
        ArgAssert.nullArg(s, "name");
        ArgAssert.nullArg(obj, "obj");
        init(2);
        try
        {
            return executor.psqlDelete(s, obj);
        }
        catch(DbException dbexception)
        {
            throw rollbackBeforeException(dbexception);
        }
    }

    public int psqlUpdate(String s, Object obj)
        throws DbException
    {
        ArgAssert.nullArg(s, "name");
        ArgAssert.nullArg(obj, "obj");
        init(2);
        try
        {
            return executor.psqlUpdate(s, obj);
        }
        catch(DbException dbexception)
        {
            throw rollbackBeforeException(dbexception);
        }
    }

    public DbRow psqlInsert(String s, Object obj)
        throws DbException
    {
        ArgAssert.nullArg(s, "name");
        ArgAssert.nullArg(obj, "obj");
        init(2);
        try
        {
            return executor.psqlInsert(s, obj);
        }
        catch(DbException dbexception)
        {
            throw rollbackBeforeException(dbexception);
        }
    }

    public void psqlExec(String s, Object obj, DbResultSetHandler dbresultsethandler)
        throws DbException
    {
        ArgAssert.nullArg(s, "name");
        ArgAssert.nullArg(obj, "obj");
        ArgAssert.nullArg(dbresultsethandler, "handler");
        init(2);
        try
        {
            executor.psqlExec(s, obj, dbresultsethandler);
        }
        catch(DbException dbexception)
        {
            throw rollbackBeforeException(dbexception);
        }
    }

    public Object psqlSelectObj(String s, Object obj)
        throws DbException
    {
        ArgAssert.nullArg(s, "name");
        ArgAssert.nullArg(obj, "obj");
        init(2);
        try
        {
            return executor.psqlSelectObj(s, obj);
        }
        catch(DbException dbexception)
        {
            throw rollbackBeforeException(dbexception);
        }
    }

    public List psqlSelectObjs(String s, Object obj, int i, boolean flag)
        throws DbException
    {
        ArgAssert.nullArg(s, "name");
        ArgAssert.nullArg(obj, "obj");
        ArgAssert.illegalRange(i > 0, "maxRows", "maxRows > 0");
        init(2);
        try
        {
            return executor.psqlSelectObjs(s, obj, i, flag);
        }
        catch(DbException dbexception)
        {
            throw rollbackBeforeException(dbexception);
        }
    }

    public DbRow psqlSelect(String s, Object obj)
        throws DbException
    {
        ArgAssert.nullArg(s, "name");
        ArgAssert.nullArg(obj, "obj");
        init(2);
        try
        {
            return executor.psqlSelect(s, obj);
        }
        catch(DbException dbexception)
        {
            throw rollbackBeforeException(dbexception);
        }
    }

    public List psqlSelectList(String s, Object obj, int i, boolean flag)
        throws DbException
    {
        ArgAssert.nullArg(s, "name");
        ArgAssert.nullArg(obj, "obj");
        ArgAssert.illegalRange(i > 0, "maxRows", "maxRows > 0");
        init(2);
        try
        {
            return executor.psqlSelectList(s, obj, i, flag);
        }
        catch(DbException dbexception)
        {
            throw rollbackBeforeException(dbexception);
        }
    }

    public DbLazyRowList psqlSelectLazyList(String s, Object obj)
        throws DbException
    {
        ArgAssert.nullArg(s, "name");
        ArgAssert.nullArg(obj, "obj");
        init(2);
        try
        {
            return executor.psqlSelectLazyList(s, obj);
        }
        catch(DbException dbexception)
        {
            throw rollbackBeforeException(dbexception);
        }
    }

    public DbLazyRowList psqlSelectLazyList(String s, Object obj, int i)
        throws DbException
    {
        ArgAssert.nullArg(s, "name");
        ArgAssert.nullArg(obj, "obj");
        ArgAssert.illegalRange(i > 0, "pageSize", "pageSize > 0");
        init(2);
        try
        {
            return executor.psqlSelectLazyList(s, obj, i);
        }
        catch(DbException dbexception)
        {
            throw rollbackBeforeException(dbexception);
        }
    }

    protected void checkNotAutoCommit()
        throws DbException
    {
        if(txCmtType == 101)
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR35"));
        else
            return;
    }

    protected void checkState()
        throws DbException
    {
        if(state == 1 || state == 4)
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR36"));
        else
            return;
    }

    protected DbException rollbackBeforeException(DbException dbexception)
    {
        if(txCmtType == 102 && rollbackOnException)
            try
            {
                conn.rollback();
                state = 3;
                executor.handleSqlWarns();
            }
            catch(SQLException sqlexception)
            {
                DbException dbexception1 = dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, props.getDbType());
                dbexception1.initCause(dbexception);
                return dbexception1;
            }
        return dbexception;
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

    protected static final int NEW_STATE = 1;
    protected static final int TX_STATE = 2;
    protected static final int NO_TX_STATE = 3;
    protected static final int CLOSED_STATE = 4;
    private String name;
    private int txCmtType;
    private int txIsoLevel;
    protected DbDataSourceProps props;
    protected DbManager dbMgr;
    private int state;
    protected Connection conn;
    protected DbSqlExecutor executor;
    protected boolean rollbackOnException;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(DbSessionImpl.class).desiredAssertionStatus();
    }
}
