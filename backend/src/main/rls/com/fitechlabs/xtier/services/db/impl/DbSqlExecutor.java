// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.db.impl;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.kernel.ioc.IocDescriptor;
import com.fitechlabs.xtier.kernel.ioc.IocDescriptorException;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.db.*;
import com.fitechlabs.xtier.services.db.adapters.DbListSqlParams;
import com.fitechlabs.xtier.services.log.LogService;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.utils.Utils;
import com.fitechlabs.xtier.utils.UtilsException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.db.impl:
//            DbLazyRowListImpl, DbRowImpl, DbManager, DbSqlManager,
//            DbDataSourceProps, DbPredefinedStatement

class DbSqlExecutor
{
    private class UpdateOperation
    {

        void exec()
            throws DbException
        {
            logSqlStmt(sql);
            try
            {
                if(autogenCols != null)
                    stmt = conn.prepareStatement(sql, autogenCols);
                else
                if(autogenColsIndexes != null)
                    stmt = conn.prepareStatement(sql, autogenColsIndexes);
                else
                    stmt = conn.prepareStatement(sql);
                setParams(stmt, params);
                result = stmt.executeUpdate();
                handleSqlWarns();
            }
            catch(SQLException sqlexception)
            {
                throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, props.getDbType());
            }
        }

        int getResult()
        {
            return result;
        }

        ResultSet getGenKeys()
            throws DbException
        {
            if(!$assertionsDisabled && stmt == null)
                throw new AssertionError();
            try
            {
                return stmt.getGeneratedKeys();
            }
            catch(SQLException sqlexception)
            {
                throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, props.getDbType());
            }
        }

        void close()
            throws DbException
        {
            Utils.close(stmt);
        }

        private String sql;
        private DbSqlParams params;
        private String autogenCols[];
        private int autogenColsIndexes[];
        private PreparedStatement stmt;
        private int result;
        static final boolean $assertionsDisabled; /* synthetic field */


        UpdateOperation(String s, DbSqlParams dbsqlparams, String as[], int ai[])
        {
            super();
            stmt = null;
            if(!$assertionsDisabled && s == null)
                throw new AssertionError();
            if(!$assertionsDisabled && as != null && ai != null)
            {
                throw new AssertionError();
            } else
            {
                sql = s;
                params = dbsqlparams;
                autogenCols = as;
                autogenColsIndexes = ai;
                return;
            }
        }
    }

    private class SelectOperation
    {

        void exec()
            throws DbException
        {
            logSqlStmt(sql);
            try
            {
                stmt = conn.prepareStatement(sql);
                setParams(stmt, params);
                rs = stmt.executeQuery();
                handleSqlWarns();
            }
            catch(SQLException sqlexception)
            {
                throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, props.getDbType());
            }
        }

        ResultSet getResultSet()
        {
            if(!$assertionsDisabled && rs == null)
                throw new AssertionError();
            else
                return rs;
        }

        void close()
            throws DbException
        {
            Utils.close(rs);
            Utils.close(stmt);
        }

        private String sql;
        private DbSqlParams params;
        private ResultSet rs;
        private PreparedStatement stmt;
        static final boolean $assertionsDisabled; /* synthetic field */


        SelectOperation(String s, DbSqlParams dbsqlparams)
        {
            super();
            rs = null;
            stmt = null;
            if(!$assertionsDisabled && s == null)
            {
                throw new AssertionError();
            } else
            {
                sql = s;
                params = dbsqlparams;
                return;
            }
        }
    }


    DbSqlExecutor(Connection connection, DbDataSourceProps dbdatasourceprops)
    {
        if(!$assertionsDisabled && (connection == null || dbdatasourceprops == null))
        {
            throw new AssertionError();
        } else
        {
            conn = connection;
            props = dbdatasourceprops;
            logger = XtierKernel.getInstance().log().getLogger("db").getLogger("sql-executor");
            dbMgr = DbManager.getInstance();
            sqlMgr = DbSqlManager.getInstance();
            return;
        }
    }

    Map getTypeMap()
        throws DbException
    {
        try
        {
            return conn.getTypeMap();
        }
        catch(SQLException sqlexception)
        {
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, props.getDbType());
        }
    }

    void setTypeMap(Map map)
        throws DbException
    {
        try
        {
            conn.setTypeMap(map);
        }
        catch(SQLException sqlexception)
        {
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, props.getDbType());
        }
        handleSqlWarns();
    }

    private ResultSetMetaData getMetaData(ResultSet resultset)
        throws DbException
    {
        if(!$assertionsDisabled && resultset == null)
            throw new AssertionError();
        try
        {
            return resultset.getMetaData();
        }
        catch(SQLException sqlexception)
        {
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, props.getDbType());
        }
    }

    private int getColumnCount(ResultSetMetaData resultsetmetadata)
        throws DbException
    {
        if(!$assertionsDisabled && resultsetmetadata == null)
            throw new AssertionError();
        try
        {
            return resultsetmetadata.getColumnCount();
        }
        catch(SQLException sqlexception)
        {
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, props.getDbType());
        }
    }

    DbRow select(String s, DbSqlParams dbsqlparams)
        throws DbException
    {
        return select0(s, dbsqlparams);
    }

    private DbRow select0(String s, DbSqlParams dbsqlparams)
        throws DbException
    {
        SelectOperation selectoperation;
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        selectoperation = new SelectOperation(s, dbsqlparams);
        DbRow dbrow1;
        selectoperation.exec();
        ResultSet resultset = selectoperation.getResultSet();
        if(!nextRow(resultset))
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR32"), 7);
        handleSqlWarns();
        ResultSetMetaData resultsetmetadata = getMetaData(resultset);
        handleSqlWarns();
        DbRow dbrow = createDbRow(resultset, resultsetmetadata);
        checkEOF(resultset);
        dbrow1 = dbrow;
        selectoperation.close();
        return dbrow1;
        Exception exception;
        exception;
        selectoperation.close();
        throw exception;
    }

    void select(String s, DbSqlParams dbsqlparams, DbRowHandler dbrowhandler)
        throws DbException
    {
        SelectOperation selectoperation = new SelectOperation(s, dbsqlparams);
        selectoperation.exec();
        ResultSet resultset = selectoperation.getResultSet();
        ResultSetMetaData resultsetmetadata = getMetaData(resultset);
        handleSqlWarns();
        do
        {
            if(!nextRow(resultset))
                break;
            handleSqlWarns();
            try
            {
                if(!dbrowhandler.handle(createDbRow(resultset, resultsetmetadata)))
                    break;
            }
            catch(SQLException sqlexception)
            {
                throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, props.getDbType());
            }
            handleSqlWarns();
        } while(true);
        selectoperation.close();
        break MISSING_BLOCK_LABEL_122;
        Exception exception;
        exception;
        selectoperation.close();
        throw exception;
    }

    void select(String s, DbSqlParams dbsqlparams, DbResultSetHandler dbresultsethandler)
        throws DbException
    {
        select0(s, dbsqlparams, dbresultsethandler);
    }

    private void select0(String s, DbSqlParams dbsqlparams, DbResultSetHandler dbresultsethandler)
        throws DbException
    {
        SelectOperation selectoperation;
        if(!$assertionsDisabled && (s == null || dbresultsethandler == null))
            throw new AssertionError();
        selectoperation = new SelectOperation(s, dbsqlparams);
        try
        {
            selectoperation.exec();
            dbresultsethandler.handle(selectoperation.getResultSet());
        }
        catch(SQLException sqlexception)
        {
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, props.getDbType());
        }
        selectoperation.close();
        break MISSING_BLOCK_LABEL_92;
        Exception exception;
        exception;
        selectoperation.close();
        throw exception;
    }

    void select(String s, DbSqlParams dbsqlparams, DbObjectHandler dbobjecthandler, Class class1)
        throws DbException
    {
        select0(s, dbsqlparams, dbobjecthandler, class1);
    }

    private void select0(String s, DbSqlParams dbsqlparams, DbObjectHandler dbobjecthandler, Class class1)
        throws DbException
    {
        SelectOperation selectoperation;
        if(!$assertionsDisabled && (s == null || class1 == null || dbobjecthandler == null))
            throw new AssertionError();
        selectoperation = new SelectOperation(s, dbsqlparams);
        selectoperation.exec();
        ResultSet resultset = selectoperation.getResultSet();
        ResultSetMetaData resultsetmetadata = getMetaData(resultset);
        handleSqlWarns();
        int i = getColumnCount(resultsetmetadata);
        handleSqlWarns();
        Method amethod[] = getMatchingSetters(class1, resultsetmetadata);
        do
        {
            if(!nextRow(resultset))
                break;
            handleSqlWarns();
            Object obj = createUserObject(resultset, i, class1, amethod);
            try
            {
                if(!dbobjecthandler.handle(obj))
                    break;
            }
            catch(SQLException sqlexception)
            {
                throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, props.getDbType());
            }
            handleSqlWarns();
        } while(true);
        selectoperation.close();
        break MISSING_BLOCK_LABEL_179;
        Exception exception;
        exception;
        selectoperation.close();
        throw exception;
    }

    Object select(String s, DbSqlParams dbsqlparams, Class class1)
        throws DbException
    {
        return select0(s, dbsqlparams, class1);
    }

    private Object select0(String s, DbSqlParams dbsqlparams, Class class1)
        throws DbException
    {
        SelectOperation selectoperation;
        if(!$assertionsDisabled && (s == null || class1 == null))
            throw new AssertionError();
        selectoperation = new SelectOperation(s, dbsqlparams);
        Object obj1;
        selectoperation.exec();
        ResultSet resultset = selectoperation.getResultSet();
        if(!nextRow(resultset))
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR32"), 7);
        handleSqlWarns();
        ResultSetMetaData resultsetmetadata = getMetaData(resultset);
        handleSqlWarns();
        Object obj = createUserObject(resultset, getColumnCount(resultsetmetadata), class1, getMatchingSetters(class1, resultsetmetadata));
        handleSqlWarns();
        checkEOF(resultset);
        if(!$assertionsDisabled && obj == null)
            throw new AssertionError();
        obj1 = obj;
        selectoperation.close();
        return obj1;
        Exception exception;
        exception;
        selectoperation.close();
        throw exception;
    }

    private Object select0(String s, DbSqlParams dbsqlparams, IocDescriptor iocdescriptor)
        throws DbException
    {
        SelectOperation selectoperation;
        if(!$assertionsDisabled && (s == null || iocdescriptor == null))
            throw new AssertionError();
        selectoperation = new SelectOperation(s, dbsqlparams);
        Object obj2;
        selectoperation.exec();
        ResultSet resultset = selectoperation.getResultSet();
        if(!nextRow(resultset))
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR32"), 7);
        handleSqlWarns();
        ResultSetMetaData resultsetmetadata = getMetaData(resultset);
        handleSqlWarns();
        Object obj = null;
        try
        {
            obj = iocdescriptor.createNewObj();
        }
        catch(IocDescriptorException iocdescriptorexception)
        {
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR3", iocdescriptor.getUid()), iocdescriptorexception);
        }
        Object obj1 = fillIocObject(obj, resultset, getColumnCount(resultsetmetadata), getMatchingSetters(obj.getClass(), resultsetmetadata));
        handleSqlWarns();
        checkEOF(resultset);
        obj2 = obj1;
        selectoperation.close();
        return obj2;
        Exception exception;
        exception;
        selectoperation.close();
        throw exception;
    }

    private List selectList0(String s, DbSqlParams dbsqlparams, IocDescriptor iocdescriptor)
        throws DbException
    {
        SelectOperation selectoperation;
        if(!$assertionsDisabled && (s == null || iocdescriptor == null))
            throw new AssertionError();
        selectoperation = new SelectOperation(s, dbsqlparams);
        ArrayList arraylist1;
        selectoperation.exec();
        ResultSet resultset = selectoperation.getResultSet();
        handleSqlWarns();
        ResultSetMetaData resultsetmetadata = getMetaData(resultset);
        handleSqlWarns();
        ArrayList arraylist = new ArrayList();
        for(; nextRow(resultset); handleSqlWarns())
        {
            Object obj = null;
            try
            {
                obj = iocdescriptor.createNewObj();
            }
            catch(IocDescriptorException iocdescriptorexception)
            {
                throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR3", iocdescriptor.getUid()), iocdescriptorexception);
            }
            arraylist.add(fillIocObject(obj, resultset, getColumnCount(resultsetmetadata), getMatchingSetters(obj.getClass(), resultsetmetadata)));
        }

        checkEOF(resultset);
        arraylist1 = arraylist;
        selectoperation.close();
        return arraylist1;
        Exception exception;
        exception;
        selectoperation.close();
        throw exception;
    }

    List selectList(String s, DbSqlParams dbsqlparams, int i, boolean flag)
        throws DbException
    {
        return selectList0(s, dbsqlparams, i, flag);
    }

    private List selectList0(String s, DbSqlParams dbsqlparams, int i, boolean flag)
        throws DbException
    {
        SelectOperation selectoperation;
        if(!$assertionsDisabled && (s == null || i <= 0))
            throw new AssertionError();
        selectoperation = new SelectOperation(s, dbsqlparams);
        ArrayList arraylist1;
        selectoperation.exec();
        ResultSet resultset = selectoperation.getResultSet();
        ResultSetMetaData resultsetmetadata = getMetaData(resultset);
        handleSqlWarns();
        ArrayList arraylist = new ArrayList();
        int j = 0;
        do
        {
            if(!nextRow(resultset))
                break;
            if(j >= i)
            {
                if(flag)
                    throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR43", String.valueOf(i)));
                break;
            }
            handleSqlWarns();
            arraylist.add(createDbRow(resultset, resultsetmetadata));
        } while(true);
        arraylist1 = arraylist;
        selectoperation.close();
        return arraylist1;
        Exception exception;
        exception;
        selectoperation.close();
        throw exception;
    }

    DbLazyRowList selectLazyList(String s, DbSqlParams dbsqlparams)
        throws DbException
    {
        return selectLazyList0(s, dbsqlparams);
    }

    private DbLazyRowList selectLazyList0(String s, DbSqlParams dbsqlparams)
        throws DbException
    {
        if(!$assertionsDisabled && s == null)
        {
            throw new AssertionError();
        } else
        {
            SelectOperation selectoperation = new SelectOperation(s, dbsqlparams);
            selectoperation.exec();
            ResultSet resultset = selectoperation.getResultSet();
            handleSqlWarns();
            DbLazyRowListImpl dblazyrowlistimpl = new DbLazyRowListImpl(resultset, props.getDbType());
            dblazyrowlistimpl.loadFirstPage();
            return dblazyrowlistimpl;
        }
    }

    DbLazyRowList selectLazyList(String s, DbSqlParams dbsqlparams, int i)
        throws DbException
    {
        return selectLazyList0(s, dbsqlparams, i);
    }

    private DbLazyRowList selectLazyList0(String s, DbSqlParams dbsqlparams, int i)
        throws DbException
    {
        if(!$assertionsDisabled && (s == null || i <= 0))
        {
            throw new AssertionError();
        } else
        {
            SelectOperation selectoperation = new SelectOperation(s, dbsqlparams);
            selectoperation.exec();
            ResultSet resultset = selectoperation.getResultSet();
            DbLazyRowListImpl dblazyrowlistimpl = new DbLazyRowListImpl(resultset, props.getDbType());
            dblazyrowlistimpl.setPageSize(i);
            dblazyrowlistimpl.loadFirstPage();
            return dblazyrowlistimpl;
        }
    }

    int delete(String s, DbSqlParams dbsqlparams)
        throws DbException
    {
        return delete0(s, dbsqlparams);
    }

    private int delete0(String s, DbSqlParams dbsqlparams)
        throws DbException
    {
        UpdateOperation updateoperation;
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        updateoperation = new UpdateOperation(s, dbsqlparams, null, null);
        int i;
        updateoperation.exec();
        i = updateoperation.getResult();
        updateoperation.close();
        return i;
        Exception exception;
        exception;
        updateoperation.close();
        throw exception;
    }

    int update(String s, DbSqlParams dbsqlparams)
        throws DbException
    {
        return update0(s, dbsqlparams);
    }

    void runSql(String s, DbSqlParams dbsqlparams)
        throws DbException
    {
        update0(s, dbsqlparams);
    }

    private int update0(String s, DbSqlParams dbsqlparams)
        throws DbException
    {
        UpdateOperation updateoperation;
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        updateoperation = new UpdateOperation(s, dbsqlparams, null, null);
        int i;
        updateoperation.exec();
        i = updateoperation.getResult();
        updateoperation.close();
        return i;
        Exception exception;
        exception;
        updateoperation.close();
        throw exception;
    }

    void insert(String s, DbSqlParams dbsqlparams)
        throws DbException
    {
        insert0(s, dbsqlparams, null, null);
    }

    DbRow insert(String s, DbSqlParams dbsqlparams, String as[])
        throws DbException
    {
        return insert0(s, dbsqlparams, as, null);
    }

    DbRow insert(String s, DbSqlParams dbsqlparams, int ai[])
        throws DbException
    {
        return insert0(s, dbsqlparams, null, ai);
    }

    private DbRow insert0(String s, DbSqlParams dbsqlparams, String as[], int ai[])
        throws DbException
    {
        UpdateOperation updateoperation;
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        if(!$assertionsDisabled && as != null && ai != null)
            throw new AssertionError();
        updateoperation = new UpdateOperation(s, dbsqlparams, as, ai);
        Object obj;
        updateoperation.exec();
        if(as != null && ai != null)
            break MISSING_BLOCK_LABEL_81;
        obj = null;
        updateoperation.close();
        return ((DbRow) (obj));
        DbRow dbrow;
        obj = updateoperation.getGenKeys();
        if(nextRow(((ResultSet) (obj))))
            break MISSING_BLOCK_LABEL_108;
        dbrow = null;
        updateoperation.close();
        return dbrow;
        dbrow = createDbRow(((ResultSet) (obj)), getMetaData(((ResultSet) (obj))));
        updateoperation.close();
        return dbrow;
        Exception exception;
        exception;
        updateoperation.close();
        throw exception;
    }

    private void exec(Statement statement, DbStatementHandler dbstatementhandler)
        throws DbException
    {
        if(!$assertionsDisabled && (statement == null || dbstatementhandler == null))
            throw new AssertionError();
        try
        {
            dbstatementhandler.execute(statement);
        }
        catch(SQLException sqlexception)
        {
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, props.getDbType());
        }
        Utils.close(statement);
        break MISSING_BLOCK_LABEL_67;
        Exception exception;
        exception;
        Utils.close(statement);
        throw exception;
    }

    void exec(DbStatementHandler dbstatementhandler)
        throws DbException
    {
        try
        {
            exec(conn.createStatement(), dbstatementhandler);
        }
        catch(SQLException sqlexception)
        {
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, props.getDbType());
        }
    }

    void exec(DbStatementHandler dbstatementhandler, int i, int j)
        throws DbException
    {
        try
        {
            exec(conn.createStatement(i, j), dbstatementhandler);
        }
        catch(SQLException sqlexception)
        {
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, props.getDbType());
        }
    }

    void exec(DbStatementHandler dbstatementhandler, int i, int j, int k)
        throws DbException
    {
        try
        {
            exec(conn.createStatement(i, j, k), dbstatementhandler);
        }
        catch(SQLException sqlexception)
        {
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, props.getDbType());
        }
    }

    private void exec(PreparedStatement preparedstatement, DbPreparedStatementHandler dbpreparedstatementhandler)
        throws DbException
    {
        if(!$assertionsDisabled && (preparedstatement == null || dbpreparedstatementhandler == null))
            throw new AssertionError();
        try
        {
            dbpreparedstatementhandler.handle(preparedstatement);
        }
        catch(SQLException sqlexception)
        {
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, props.getDbType());
        }
        Utils.close(preparedstatement);
        break MISSING_BLOCK_LABEL_67;
        Exception exception;
        exception;
        Utils.close(preparedstatement);
        throw exception;
    }

    void exec(String s, DbPreparedStatementHandler dbpreparedstatementhandler, String as[])
        throws DbException
    {
        try
        {
            exec(conn.prepareStatement(s, as), dbpreparedstatementhandler);
        }
        catch(SQLException sqlexception)
        {
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, props.getDbType());
        }
    }

    void exec(String s, DbPreparedStatementHandler dbpreparedstatementhandler, int i, int j)
        throws DbException
    {
        try
        {
            exec(conn.prepareStatement(s, i, j), dbpreparedstatementhandler);
        }
        catch(SQLException sqlexception)
        {
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, props.getDbType());
        }
    }

    void exec(String s, DbPreparedStatementHandler dbpreparedstatementhandler, int i, int j, int k)
        throws DbException
    {
        try
        {
            exec(conn.prepareStatement(s, i, j, k), dbpreparedstatementhandler);
        }
        catch(SQLException sqlexception)
        {
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, props.getDbType());
        }
    }

    void exec(String s, DbPreparedStatementHandler dbpreparedstatementhandler, int ai[])
        throws DbException
    {
        try
        {
            exec(conn.prepareStatement(s, ai), dbpreparedstatementhandler);
        }
        catch(SQLException sqlexception)
        {
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, props.getDbType());
        }
    }

    void exec(String s, DbPreparedStatementHandler dbpreparedstatementhandler)
        throws DbException
    {
        try
        {
            exec(conn.prepareStatement(s), dbpreparedstatementhandler);
        }
        catch(SQLException sqlexception)
        {
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, props.getDbType());
        }
    }

    void exec(String s, DbPreparedStatementHandler dbpreparedstatementhandler, int i)
        throws DbException
    {
        try
        {
            exec(conn.prepareStatement(s, i), dbpreparedstatementhandler);
        }
        catch(SQLException sqlexception)
        {
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, props.getDbType());
        }
    }

    private void exec(CallableStatement callablestatement, DbCallableStatementHandler dbcallablestatementhandler)
        throws DbException
    {
        if(!$assertionsDisabled && (callablestatement == null || dbcallablestatementhandler == null))
            throw new AssertionError();
        try
        {
            dbcallablestatementhandler.handle(callablestatement);
        }
        catch(SQLException sqlexception)
        {
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, props.getDbType());
        }
        Utils.close(callablestatement);
        break MISSING_BLOCK_LABEL_67;
        Exception exception;
        exception;
        Utils.close(callablestatement);
        throw exception;
    }

    void exec(String s, DbCallableStatementHandler dbcallablestatementhandler)
        throws DbException
    {
        try
        {
            exec(conn.prepareCall(s), dbcallablestatementhandler);
        }
        catch(SQLException sqlexception)
        {
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, props.getDbType());
        }
    }

    void exec(String s, DbCallableStatementHandler dbcallablestatementhandler, int i, int j)
        throws DbException
    {
        try
        {
            exec(conn.prepareCall(s, i, j), dbcallablestatementhandler);
        }
        catch(SQLException sqlexception)
        {
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, props.getDbType());
        }
    }

    void exec(String s, DbCallableStatementHandler dbcallablestatementhandler, int i, int j, int k)
        throws DbException
    {
        try
        {
            exec(conn.prepareCall(s, i, j, k), dbcallablestatementhandler);
        }
        catch(SQLException sqlexception)
        {
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, props.getDbType());
        }
    }

    private DbSqlParams convertSqlParams(Object obj, List list)
        throws DbException
    {
        if(!$assertionsDisabled && obj == null)
            throw new AssertionError();
        if(list == null)
            return null;
        ArrayList arraylist = new ArrayList();
        int i = list.size();
        if((obj instanceof Map))
        {
            Map map = (Map)obj;
            int j = 0;
            do
            {
                if(j >= i)
                    break;
                String s = (String)list.get(j);
                String s1 = null;
                for(Iterator iterator = map.keySet().iterator(); iterator.hasNext();)
                    try
                    {
                        String s3 = (String)iterator.next();
                        if(s3.equalsIgnoreCase(s))
                        {
                            if(s1 != null)
                                throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR63", s));
                            s1 = s3;
                        }
                    }
                    catch(ClassCastException classcastexception)
                    {
                        throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR45"), classcastexception);
                    }

                if(s1 == null)
                    throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR44", s));
                arraylist.add(j, map.get(s1));
                j++;
            } while(true);
        } else
        {
            Class class1 = obj.getClass();
            Method amethod[] = class1.getMethods();
            for(int k = 0; k < i; k++)
            {
                String s2 = (String)list.get(k);
                Method method = null;
                String s4 = "get" + s2;
                for(int l = 0; l < amethod.length; l++)
                {
                    Method method1 = amethod[l];
                    if(!method1.getName().equalsIgnoreCase(s4) || method1.getParameterTypes().length != 0)
                        continue;
                    if(method != null)
                        throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR65", s4));
                    method = method1;
                }

                if(method == null)
                    throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR46", s2));
                Object obj1 = null;
                try
                {
                    obj1 = Utils.invokeMethod(obj, method, null);
                }
                catch(UtilsException utilsexception)
                {
                    throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR47", s4), utilsexception);
                }
                arraylist.add(k, obj1);
            }

        }
        return new DbListSqlParams(arraylist);
    }

    int psqlDelete(String s, Object obj)
        throws DbException
    {
        DbPredefinedStatement dbpredefinedstatement = sqlMgr.getDbPredefinedStatement(s, 4);
        if(!$assertionsDisabled && dbpredefinedstatement == null)
            throw new AssertionError();
        else
            return delete0(dbpredefinedstatement.getJdbcSql(), convertSqlParams(obj, dbpredefinedstatement.getParams()));
    }

    int psqlUpdate(String s, Object obj)
        throws DbException
    {
        DbPredefinedStatement dbpredefinedstatement = sqlMgr.getDbPredefinedStatement(s, 3);
        if(!$assertionsDisabled && dbpredefinedstatement == null)
            throw new AssertionError();
        else
            return update0(dbpredefinedstatement.getJdbcSql(), convertSqlParams(obj, dbpredefinedstatement.getParams()));
    }

    DbRow psqlInsert(String s, Object obj)
        throws DbException
    {
        DbPredefinedStatement dbpredefinedstatement = sqlMgr.getDbPredefinedStatement(s, 2);
        if(!$assertionsDisabled && dbpredefinedstatement == null)
            throw new AssertionError();
        else
            return insert0(dbpredefinedstatement.getJdbcSql(), convertSqlParams(obj, dbpredefinedstatement.getParams()), dbpredefinedstatement.getAutogenKeys(), dbpredefinedstatement.getAutogenKeysIndexes());
    }

    void psqlExec(String s, Object obj, DbResultSetHandler dbresultsethandler)
        throws DbException
    {
        DbPredefinedStatement dbpredefinedstatement = sqlMgr.getDbPredefinedStatement(s, 1);
        if(!$assertionsDisabled && dbpredefinedstatement == null)
        {
            throw new AssertionError();
        } else
        {
            select0(dbpredefinedstatement.getJdbcSql(), convertSqlParams(obj, dbpredefinedstatement.getParams()), dbresultsethandler);
            return;
        }
    }

    Object psqlSelectObj(String s, Object obj)
        throws DbException
    {
        DbPredefinedStatement dbpredefinedstatement = sqlMgr.getDbPredefinedStatement(s, 1);
        if(!$assertionsDisabled && dbpredefinedstatement == null)
            throw new AssertionError();
        String s1 = dbpredefinedstatement.getResClass();
        if(s1 != null)
        {
            Class class1 = null;
            try
            {
                class1 = Class.forName(s1);
            }
            catch(ClassNotFoundException classnotfoundexception)
            {
                throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR48", s1), classnotfoundexception);
            }
            return select0(dbpredefinedstatement.getJdbcSql(), convertSqlParams(obj, dbpredefinedstatement.getParams()), class1);
        }
        IocDescriptor iocdescriptor = dbpredefinedstatement.getResIoc();
        if(!$assertionsDisabled && iocdescriptor == null)
            throw new AssertionError();
        else
            return select0(dbpredefinedstatement.getJdbcSql(), convertSqlParams(obj, dbpredefinedstatement.getParams()), iocdescriptor);
    }

    List psqlSelectObjs(String s, Object obj, final int maxRows, final boolean raiseOnOverflow)
        throws DbException
    {
        DbPredefinedStatement dbpredefinedstatement = sqlMgr.getDbPredefinedStatement(s, 1);
        if(!$assertionsDisabled && dbpredefinedstatement == null)
            throw new AssertionError();
        String s1 = dbpredefinedstatement.getResClass();
        if(s1 != null)
        {
            Class class1 = null;
            try
            {
                class1 = Class.forName(s1);
            }
            catch(ClassNotFoundException classnotfoundexception)
            {
                throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR48", s1), classnotfoundexception);
            }
            final ArrayList list = new ArrayList();
            select0(dbpredefinedstatement.getJdbcSql(), convertSqlParams(obj, dbpredefinedstatement.getParams()), new DbObjectHandler() {

                public boolean handle(Object obj1)
                    throws SQLException
                {
                    if(list.size() >= maxRows)
                    {
                        if(raiseOnOverflow)
                            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR43", String.valueOf(maxRows)));
                        else
                            return false;
                    } else
                    {
                        list.add(obj1);
                        return true;
                    }
                }


                throws DbException
            {
                super();
            }
            }
, class1);
            return list;
        }
        IocDescriptor iocdescriptor = dbpredefinedstatement.getResIoc();
        if(!$assertionsDisabled && iocdescriptor == null)
            throw new AssertionError();
        else
            return selectList0(dbpredefinedstatement.getJdbcSql(), convertSqlParams(obj, dbpredefinedstatement.getParams()), iocdescriptor);
    }

    DbRow psqlSelect(String s, Object obj)
        throws DbException
    {
        DbPredefinedStatement dbpredefinedstatement = sqlMgr.getDbPredefinedStatement(s, 1);
        if(!$assertionsDisabled && dbpredefinedstatement == null)
            throw new AssertionError();
        else
            return select0(dbpredefinedstatement.getJdbcSql(), convertSqlParams(obj, dbpredefinedstatement.getParams()));
    }

    List psqlSelectList(String s, Object obj, int i, boolean flag)
        throws DbException
    {
        DbPredefinedStatement dbpredefinedstatement = sqlMgr.getDbPredefinedStatement(s, 1);
        if(!$assertionsDisabled && dbpredefinedstatement == null)
            throw new AssertionError();
        else
            return selectList0(dbpredefinedstatement.getJdbcSql(), convertSqlParams(obj, dbpredefinedstatement.getParams()), i, flag);
    }

    DbLazyRowList psqlSelectLazyList(String s, Object obj)
        throws DbException
    {
        DbPredefinedStatement dbpredefinedstatement = sqlMgr.getDbPredefinedStatement(s, 1);
        if(!$assertionsDisabled && dbpredefinedstatement == null)
            throw new AssertionError();
        else
            return selectLazyList0(dbpredefinedstatement.getJdbcSql(), convertSqlParams(obj, dbpredefinedstatement.getParams()));
    }

    DbLazyRowList psqlSelectLazyList(String s, Object obj, int i)
        throws DbException
    {
        DbPredefinedStatement dbpredefinedstatement = sqlMgr.getDbPredefinedStatement(s, 1);
        if(!$assertionsDisabled && dbpredefinedstatement == null)
            throw new AssertionError();
        else
            return selectLazyList0(dbpredefinedstatement.getJdbcSql(), convertSqlParams(obj, dbpredefinedstatement.getParams()), i);
    }

    private void setParams(PreparedStatement preparedstatement, DbSqlParams dbsqlparams)
        throws SQLException
    {
        if(!$assertionsDisabled && preparedstatement == null)
            throw new AssertionError();
        if(dbsqlparams != null)
        {
            int i = dbsqlparams.getCount();
            for(int j = 0; j < i; j++)
                dbMgr.setNvl(preparedstatement, j + 1, dbsqlparams.getParam(j));

        }
    }

    void handleSqlWarns()
        throws DbException
    {
        SQLWarning sqlwarning = null;
        if(props.isLogWarns())
            try
            {
                sqlwarning = conn.getWarnings();
                if(sqlwarning != null)
                    logger.warning(L10n.format("SRVC.DB.TXT1", sqlwarning.getLocalizedMessage()), sqlwarning);
            }
            catch(SQLException sqlexception)
            {
                throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, props.getDbType());
            }
        if(props.isRaiseWarns())
        {
            if(sqlwarning == null)
                try
                {
                    sqlwarning = conn.getWarnings();
                }
                catch(SQLException sqlexception1)
                {
                    throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception1, props.getDbType());
                }
            if(sqlwarning != null)
                throw new DbWarning(sqlwarning, L10n.format("SRVC.DB.ERR56"));
        }
    }

    private void logSqlStmt(String s)
    {
        if(props.isLogStmts())
            logger.log(L10n.format("SRVC.DB.TXT2", s));
    }

    private DbRow createDbRow(ResultSet resultset, ResultSetMetaData resultsetmetadata)
        throws DbException
    {
        if(!$assertionsDisabled && (resultset == null || resultsetmetadata == null))
            throw new AssertionError();
        DbRowImpl dbrowimpl = null;
        int i = getColumnCount(resultsetmetadata);
        try
        {
            dbrowimpl = new DbRowImpl(resultset.getRow(), i);
        }
        catch(SQLException sqlexception)
        {
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, props.getDbType());
        }
        handleSqlWarns();
        for(int j = 1; j <= i; j++)
        {
            try
            {
                dbrowimpl.addObject(j, resultsetmetadata.getColumnName(j), resultsetmetadata.getColumnType(j), Utils.getDbValue(resultsetmetadata.getColumnType(j), resultset, j));
            }
            catch(SQLException sqlexception1)
            {
                throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception1, props.getDbType());
            }
            handleSqlWarns();
        }

        return dbrowimpl;
    }

    private void checkEOF(ResultSet resultset)
        throws DbException
    {
        if(!$assertionsDisabled && resultset == null)
            throw new AssertionError();
        boolean flag = false;
        try
        {
            flag = resultset.next();
        }
        catch(SQLException sqlexception)
        {
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, props.getDbType());
        }
        handleSqlWarns();
        if(flag)
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR33"), 5);
        else
            return;
    }

    private Method[] getMatchingSetters(Class class1, ResultSetMetaData resultsetmetadata)
        throws DbException
    {
        if(!$assertionsDisabled && (class1 == null || resultsetmetadata == null))
            throw new AssertionError();
        int i = getColumnCount(resultsetmetadata);
        Method amethod[] = new Method[i + 1];
        Method amethod1[] = class1.getMethods();
        for(int j = 1; j <= i; j++)
        {
            String s = null;
            try
            {
                s = resultsetmetadata.getColumnName(j);
            }
            catch(SQLException sqlexception)
            {
                throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, props.getDbType());
            }
            handleSqlWarns();
            String s1 = "set" + s;
            Method method = null;
            for(int k = 0; k < amethod1.length; k++)
            {
                Method method1 = amethod1[k];
                if(!method1.getName().equalsIgnoreCase(s1) || method1.getParameterTypes().length != 1)
                    continue;
                if(method != null)
                    throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR64", s1));
                method = method1;
            }

            if(method == null)
                throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR57", s1, class1.getName()));
            amethod[j] = method;
        }

        return amethod;
    }

    private Object createUserObject(ResultSet resultset, int i, Class class1, Method amethod[])
        throws DbException
    {
        if(!$assertionsDisabled && (resultset == null || class1 == null || amethod == null))
            throw new AssertionError();
        Object obj = null;
        try
        {
            obj = Utils.buildObj(class1);
        }
        catch(UtilsException utilsexception)
        {
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR58", class1.getName()), utilsexception);
        }
        Object obj1 = null;
        for(int j = 1; j <= i; j++)
        {
            Object obj2;
            try
            {
                obj2 = resultset.getObject(j);
            }
            catch(SQLException sqlexception)
            {
                throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, props.getDbType());
            }
            try
            {
                Utils.invokeMethod(obj, amethod[j], new Object[] {
                    convertNumeric(amethod[j].getParameterTypes()[0], obj2)
                });
            }
            catch(UtilsException utilsexception1)
            {
                throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR59", obj.getClass().getName(), amethod[j].getName()), utilsexception1);
            }
        }

        return obj;
    }

    private Object convertNumeric(Class class1, Object obj)
        throws UtilsException
    {
        if((obj instanceof Number))
            return Utils.convertNumeric(class1, obj);
        else
            return obj;
    }

    private Object fillIocObject(Object obj, ResultSet resultset, int i, Method amethod[])
        throws DbException
    {
        if(!$assertionsDisabled && (resultset == null || obj == null || amethod == null))
            throw new AssertionError();
        try
        {
            for(int j = 1; j <= i; j++)
            {
                Object obj1 = resultset.getObject(j);
                try
                {
                    Utils.invokeMethod(obj, amethod[j], new Object[] {
                        convertNumeric(amethod[j].getParameterTypes()[0], obj1)
                    });
                }
                catch(UtilsException utilsexception)
                {
                    throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR59", obj.getClass().getName(), amethod[j].getName()), utilsexception);
                }
            }

        }
        catch(SQLException sqlexception)
        {
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, props.getDbType());
        }
        return obj;
    }

    private boolean nextRow(ResultSet resultset)
        throws DbException
    {
        if(!$assertionsDisabled && resultset == null)
            throw new AssertionError();
        try
        {
            return resultset.next();
        }
        catch(SQLException sqlexception)
        {
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, props.getDbType());
        }
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

    private DbManager dbMgr;
    private DbSqlManager sqlMgr;
    private DbDataSourceProps props;
    private Logger logger;
    private Connection conn;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(DbSqlExecutor.class).desiredAssertionStatus();
    }





}
