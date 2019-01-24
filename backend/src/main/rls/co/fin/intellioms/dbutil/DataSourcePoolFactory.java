// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   DataSourcePoolFactory.java

package co.fin.intellioms.dbutil;

import co.fin.intellioms.util.Log;
import com.fitechlabs.xtier.services.objpool.*;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

// Referenced classes of package com.com.fin.intellioms.dbutil:
//            DbConnectionImpl, DbConnection

class DataSourcePoolFactory
    implements ObjectPoolFactory
{

    public DataSourcePoolFactory(String dsName, DataSource ds, String validationQuery)
    {
        log = Log.getLogger(com/ com /fin/intellioms/dbutil/DataSourcePoolFactory);
        if(!$assertionsDisabled && ds == null)
            throw new AssertionError("Datasource is null.");
        if(!$assertionsDisabled && dsName == null)
            throw new AssertionError("Datasource name is null.");
        if(!$assertionsDisabled && validationQuery == null)
        {
            throw new AssertionError("Validation query is null.");
        } else
        {
            this.dsName = dsName;
            this.ds = ds;
            this.validationQuery = validationQuery;
            return;
        }
    }

    public Object createObj()
        throws ObjectPoolException
    {
        if(log.isDebug())
            log.debug((new StringBuilder()).append("Creating new database connection using datasource #").append(dsName).toString());
        DbConnection conn = null;
        try
        {
            Connection sqlConn = ds.getConnection();
            conn = new DbConnectionImpl(sqlConn, pool, validationQuery);
        }
        catch(SQLException e)
        {
            throw new ObjectPoolException((new StringBuilder()).append("Unable to open new database connection using datasource #").append(dsName).append(": ").append(e.getMessage()).toString(), e);
        }
        return conn;
    }

    public void disposeObj(Object obj)
    {
        if(log.isDebug())
            log.debug((new StringBuilder()).append("Disposing database connection using datasource #").append(dsName).toString());
        DbConnection conn = (DbConnection)obj;
        try
        {
            conn.getRealConnection().close();
        }
        catch(SQLException e)
        {
            if(log.isError())
                log.error((new StringBuilder()).append("Unable to close database connection using datasource #").append(dsName).append(": ").append(e.getMessage()).toString(), e);
        }
    }

    public ObjectPool getPool()
    {
        return pool;
    }

    public void setPool(ObjectPool pool)
    {
        if(!$assertionsDisabled && pool == null)
        {
            throw new AssertionError("Object pool is null.");
        } else
        {
            this.pool = pool;
            return;
        }
    }

    private Log log;
    private final String dsName;
    private final DataSource ds;
    private final String validationQuery;
    private ObjectPool pool;
    static final boolean $assertionsDisabled = !com/ com /fin/intellioms/dbutil/DataSourcePoolFactory.desiredAssertionStatus();

}
