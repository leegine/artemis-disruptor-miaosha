// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   DbConnectionImpl.java

package co.fin.intellioms.dbutil;

import co.fin.intellioms.util.Log;
import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.services.objpool.*;
import com.fitechlabs.xtier.utils.Utils;
import java.sql.*;
import java.util.Map;
import java.util.Properties;

// Referenced classes of package com.com.fin.intellioms.dbutil:
//            InvalidConnectionException, DbConnection

class DbConnectionImpl
    implements DbConnection, ObjectPoolNotifyable
{

    DbConnectionImpl(Connection conn, ObjectPool pool, String validationQuery)
    {
        log = Log.getLogger(com/ com /fin/intellioms/dbutil/DbConnectionImpl);
        if(!$assertionsDisabled && conn == null)
            throw new AssertionError("Connection is null.");
        if(!$assertionsDisabled && pool == null)
            throw new AssertionError("Object pool is null.");
        if(!$assertionsDisabled && validationQuery == null)
        {
            throw new AssertionError("Validation query is null.");
        } else
        {
            this.conn = conn;
            this.pool = pool;
            this.validationQuery = validationQuery;
            return;
        }
    }

    public void onAcquire(ObjectPool pool)
        throws ObjectPoolException
    {
        Statement st;
        ResultSet rs;
        if(log.isDebug())
            log.debug("Validating connection.");
        st = null;
        rs = null;
        try
        {
            st = conn.createStatement();
            rs = st.executeQuery(validationQuery);
        }
        catch(SQLException e)
        {
            pool.invalidate(this);
            throw new InvalidConnectionException((new StringBuilder()).append("Connection validation failed (validation query = '").append(validationQuery).append("'): ").append(e.getMessage()).toString(), e);
        }
        Utils.close(rs);
        Utils.close(st);
        break MISSING_BLOCK_LABEL_123;
        Exception exception;
        exception;
        Utils.close(rs);
        Utils.close(st);
        throw exception;
    }

    public void onRelease(ObjectPool pool)
        throws ObjectPoolException
    {
        try
        {
            conn.clearWarnings();
        }
        catch(SQLException e)
        {
            throw new ObjectPoolException("Unable to clean warnings before releasing connection back to the pool.", e);
        }
    }

    public void close()
        throws SQLException
    {
        if(log.isDebug())
            log.debug("Returning connection back to the pool.");
        if(XtierKernel.getInstance().isStarted("objpool"))
            try
            {
                pool.release(this);
            }
            catch(ObjectPoolException e)
            {
                throw new SQLException((new StringBuilder()).append("Unable to return database connection back to the pool: ").append(e.getMessage()).toString());
            }
        else
            Utils.close(conn);
    }

    public Connection getRealConnection()
    {
        return conn;
    }

    public int getHoldability()
        throws SQLException
    {
        return conn.getHoldability();
    }

    public int getTransactionIsolation()
        throws SQLException
    {
        return conn.getTransactionIsolation();
    }

    public void clearWarnings()
        throws SQLException
    {
        conn.clearWarnings();
    }

    public void commit()
        throws SQLException
    {
        conn.commit();
    }

    public void rollback()
        throws SQLException
    {
        conn.rollback();
    }

    public boolean getAutoCommit()
        throws SQLException
    {
        return conn.getAutoCommit();
    }

    public boolean isClosed()
        throws SQLException
    {
        return conn.isClosed();
    }

    public boolean isReadOnly()
        throws SQLException
    {
        return conn.isReadOnly();
    }

    public void setHoldability(int holdability)
        throws SQLException
    {
        conn.setHoldability(holdability);
    }

    public void setTransactionIsolation(int level)
        throws SQLException
    {
        conn.setTransactionIsolation(level);
    }

    public void setAutoCommit(boolean autoCommit)
        throws SQLException
    {
        conn.setAutoCommit(autoCommit);
    }

    public void setReadOnly(boolean readOnly)
        throws SQLException
    {
        conn.setReadOnly(readOnly);
    }

    public String getCatalog()
        throws SQLException
    {
        return conn.getCatalog();
    }

    public void setCatalog(String catalog)
        throws SQLException
    {
        conn.setCatalog(catalog);
    }

    public DatabaseMetaData getMetaData()
        throws SQLException
    {
        return conn.getMetaData();
    }

    public SQLWarning getWarnings()
        throws SQLException
    {
        return conn.getWarnings();
    }

    public Savepoint setSavepoint()
        throws SQLException
    {
        return conn.setSavepoint();
    }

    public void releaseSavepoint(Savepoint savepoint)
        throws SQLException
    {
        conn.releaseSavepoint(savepoint);
    }

    public void rollback(Savepoint savepoint)
        throws SQLException
    {
        conn.rollback(savepoint);
    }

    public Statement createStatement()
        throws SQLException
    {
        return conn.createStatement();
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency)
        throws SQLException
    {
        return conn.createStatement(resultSetType, resultSetConcurrency);
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
        throws SQLException
    {
        return conn.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    public Map getTypeMap()
        throws SQLException
    {
        return conn.getTypeMap();
    }

    public void setTypeMap(Map map)
        throws SQLException
    {
        conn.setTypeMap(map);
    }

    public String nativeSQL(String sql)
        throws SQLException
    {
        return conn.nativeSQL(sql);
    }

    public CallableStatement prepareCall(String sql)
        throws SQLException
    {
        return conn.prepareCall(sql);
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
        throws SQLException
    {
        return conn.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
        throws SQLException
    {
        return conn.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    public PreparedStatement prepareStatement(String sql)
        throws SQLException
    {
        return conn.prepareStatement(sql);
    }

    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
        throws SQLException
    {
        return conn.prepareStatement(sql, autoGeneratedKeys);
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
        throws SQLException
    {
        return conn.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
        throws SQLException
    {
        return conn.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    public PreparedStatement prepareStatement(String sql, int columnIndexes[])
        throws SQLException
    {
        return conn.prepareStatement(sql, columnIndexes);
    }

    public Savepoint setSavepoint(String name)
        throws SQLException
    {
        return conn.setSavepoint(name);
    }

    public PreparedStatement prepareStatement(String sql, String columnNames[])
        throws SQLException
    {
        return conn.prepareStatement(sql, columnNames);
    }

    public Clob createClob()
        throws SQLException
    {
        return conn.createClob();
    }

    public Blob createBlob()
        throws SQLException
    {
        return conn.createBlob();
    }

    public NClob createNClob()
        throws SQLException
    {
        return conn.createNClob();
    }

    public SQLXML createSQLXML()
        throws SQLException
    {
        return conn.createSQLXML();
    }

    public boolean isValid(int timeout)
        throws SQLException
    {
        return conn.isValid(timeout);
    }

    public void setClientInfo(String name, String value)
        throws SQLClientInfoException
    {
        conn.setClientInfo(name, value);
    }

    public void setClientInfo(Properties properties)
        throws SQLClientInfoException
    {
        conn.setClientInfo(properties);
    }

    public String getClientInfo(String name)
        throws SQLException
    {
        return conn.getClientInfo(name);
    }

    public Properties getClientInfo()
        throws SQLException
    {
        return conn.getClientInfo();
    }

    public Array createArrayOf(String typeName, Object elements[])
        throws SQLException
    {
        return conn.createArrayOf(typeName, elements);
    }

    public Struct createStruct(String typeName, Object attributes[])
        throws SQLException
    {
        return conn.createStruct(typeName, attributes);
    }

    public boolean isWrapperFor(Class arg0)
        throws SQLException
    {
        return conn.isWrapperFor(arg0);
    }

    public Object unwrap(Class arg0)
        throws SQLException
    {
        return conn.unwrap(arg0);
    }

    private Log log;
    private final Connection conn;
    private final ObjectPool pool;
    private final String validationQuery;
    static final boolean $assertionsDisabled = !com/ com /fin/intellioms/dbutil/DbConnectionImpl.desiredAssertionStatus();

}
