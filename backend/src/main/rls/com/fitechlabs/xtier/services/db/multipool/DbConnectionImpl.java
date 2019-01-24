// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.db.multipool;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.services.objpool.*;
import com.fitechlabs.xtier.utils.Utils;
import java.sql.*;
import java.util.Map;

// Referenced classes of package com.fitechlabs.xtier.services.db.multipool:
//            InvalidConnectionException, DbConnection

class DbConnectionImpl
    implements DbConnection, ObjectPoolNotifyable
{

    DbConnectionImpl(Connection connection, ObjectPool objectpool, String s)
    {
        if(!$assertionsDisabled && connection == null)
            throw new AssertionError("Connection is null.");
        if(!$assertionsDisabled && objectpool == null)
            throw new AssertionError("Object pool is null.");
        if(!$assertionsDisabled && s == null)
        {
            throw new AssertionError("Validation query is null.");
        } else
        {
            conn = connection;
            pool = objectpool;
            validationQuery = s;
            return;
        }
    }

    public void onAcquire(ObjectPool objectpool)
        throws ObjectPoolException
    {
        Statement statement;
        ResultSet resultset;
        statement = null;
        resultset = null;
        try
        {
            statement = conn.createStatement();
            resultset = statement.executeQuery(validationQuery);
        }
        catch(SQLException sqlexception)
        {
            objectpool.invalidate(this);
            throw new InvalidConnectionException("Connection validation failed (validation query = '" + validationQuery + "'): " + sqlexception.getMessage(), sqlexception);
        }
        Utils.close(resultset);
        Utils.close(statement);
        break MISSING_BLOCK_LABEL_103;
        Exception exception;
        exception;
        Utils.close(resultset);
        Utils.close(statement);
        throw exception;
    }

    public void onRelease(ObjectPool objectpool)
        throws ObjectPoolException
    {
        try
        {
            conn.clearWarnings();
        }
        catch(SQLException sqlexception)
        {
            throw new ObjectPoolException("Unable to clean warnings before releasing connection back to the pool.", sqlexception);
        }
    }

    public void close()
        throws SQLException
    {
        if(XtierKernel.getInstance().isStarted("objpool"))
            try
            {
                pool.release(this);
            }
            catch(ObjectPoolException objectpoolexception)
            {
                throw new SQLException("Unable to return database connection back to the pool: " + objectpoolexception.getMessage());
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

    public void setHoldability(int i)
        throws SQLException
    {
        conn.setHoldability(i);
    }

    public void setTransactionIsolation(int i)
        throws SQLException
    {
        conn.setTransactionIsolation(i);
    }

    public void setAutoCommit(boolean flag)
        throws SQLException
    {
        conn.setAutoCommit(flag);
    }

    public void setReadOnly(boolean flag)
        throws SQLException
    {
        conn.setReadOnly(flag);
    }

    public String getCatalog()
        throws SQLException
    {
        return conn.getCatalog();
    }

    public void setCatalog(String s)
        throws SQLException
    {
        conn.setCatalog(s);
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

    public Statement createStatement(int i, int j)
        throws SQLException
    {
        return conn.createStatement(i, j);
    }

    public Statement createStatement(int i, int j, int k)
        throws SQLException
    {
        return conn.createStatement(i, j, k);
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

    public String nativeSQL(String s)
        throws SQLException
    {
        return conn.nativeSQL(s);
    }

    public CallableStatement prepareCall(String s)
        throws SQLException
    {
        return conn.prepareCall(s);
    }

    public CallableStatement prepareCall(String s, int i, int j)
        throws SQLException
    {
        return conn.prepareCall(s, i, j);
    }

    public CallableStatement prepareCall(String s, int i, int j, int k)
        throws SQLException
    {
        return conn.prepareCall(s, i, j, k);
    }

    public PreparedStatement prepareStatement(String s)
        throws SQLException
    {
        return conn.prepareStatement(s);
    }

    public PreparedStatement prepareStatement(String s, int i)
        throws SQLException
    {
        return conn.prepareStatement(s, i);
    }

    public PreparedStatement prepareStatement(String s, int i, int j)
        throws SQLException
    {
        return conn.prepareStatement(s, i, j);
    }

    public PreparedStatement prepareStatement(String s, int i, int j, int k)
        throws SQLException
    {
        return conn.prepareStatement(s, i, j, k);
    }

    public PreparedStatement prepareStatement(String s, int ai[])
        throws SQLException
    {
        return conn.prepareStatement(s, ai);
    }

    public Savepoint setSavepoint(String s)
        throws SQLException
    {
        return conn.setSavepoint(s);
    }

    public PreparedStatement prepareStatement(String s, String as[])
        throws SQLException
    {
        return conn.prepareStatement(s, as);
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

    private final Connection conn;
    private final ObjectPool pool;
    private final String validationQuery;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(DbConnectionImpl.class).desiredAssertionStatus();
    }
}
