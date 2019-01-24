// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   MultiPoolDataSource.java

package co.fin.intellioms.dbutil;

import co.fin.intellioms.util.Log;
import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.services.objpool.*;
import com.fitechlabs.xtier.services.objpool.policies.ObjectPoolStrictPolicy;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.boxed.sync.BoxedInt32Sync;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

// Referenced classes of package com.com.fin.intellioms.dbutil:
//            BasicFailoverCallback, InvalidConnectionException, DataSourcePoolFactory, PoolFailoverCallback

public class MultiPoolDataSource
    implements DataSource
{
    private static class PoolWrapper
    {

        public DataSource getDs()
        {
            return ds;
        }

        public ObjectPool getPool()
        {
            return pool;
        }

        public String getDsName()
        {
            return dsName;
        }

        private final ObjectPool pool;
        private final DataSource ds;
        private final String dsName;

        public PoolWrapper(String dsName, ObjectPool pool, DataSource ds)
        {
            this.pool = pool;
            this.ds = ds;
            this.dsName = dsName;
        }
    }


    public MultiPoolDataSource(int size, String validationQuery, int mode, PoolFailoverCallback callback, ObjectPoolResizePolicy poolResizePolicy)
    {
        log = Log.getLogger(com/ com /fin/intellioms/dbutil/MultiPoolDataSource);
        pools = new ArrayList();
        marker = new BoxedInt32Sync(0);
        ArgAssert.nullArg(validationQuery, "validationQuery");
        ArgAssert.nullArg(poolResizePolicy, "poolResizePolicy");
        ArgAssert.nullArg(callback, "callback");
        ArgAssert.illegalArg(size > 0, "poolSize.");
        ArgAssert.illegalArg(mode == 2 || mode == 1, "mode");
        poolServ = XtierKernel.getInstance().objpool();
        this.validationQuery = validationQuery;
        resizePolicy = poolResizePolicy;
        this.callback = callback;
        poolSize = size;
        this.mode = mode;
    }

    public MultiPoolDataSource(int size, String validationQuery)
    {
        this(size, 2, validationQuery);
    }

    public MultiPoolDataSource(int size, int mode, String validationQuery)
    {
        this(size, validationQuery, mode, ((PoolFailoverCallback) (new BasicFailoverCallback())), ((ObjectPoolResizePolicy) (new ObjectPoolStrictPolicy())));
    }

    public Connection getConnection()
        throws SQLException
    {
        Connection conn = null;
        int curPool = mode != 1 ? 0 : getStartPosition();
        int i = 0;
        do
        {
            if(i >= pools.size())
                break;
            PoolWrapper cur = (PoolWrapper)pools.get(curPool);
            int nexpPoolPos = curPool != pools.size() - 1 ? curPool + 1 : 0;
            String next = i >= pools.size() - 1 ? null : ((PoolWrapper)pools.get(nexpPoolPos)).getDsName();
            if(log.isDebug())
                log.debug((new StringBuilder()).append("Trying to acquire connection using pool #").append(cur.getDsName()).toString());
            if(cur.getPool().getFree() == 0 && doCallback(104, cur.getDsName(), next))
            {
                if(curPool == pools.size() - 1)
                    curPool = 0;
                else
                    curPool++;
                i++;
                continue;
            }
            try
            {
                conn = (Connection)cur.getPool().acquireWait();
                break;
            }
            catch(ObjectPoolException e)
            {
                if(!(e instanceof InvalidConnectionException) && !(e.getCause() instanceof SQLException))
                    throw new SQLException((new StringBuilder()).append("Unable to acquire database connection from pool: ").append(e.getMessage()).toString());
                if(log.isWarn() && !(e instanceof InvalidConnectionException))
                    log.warn((new StringBuilder()).append("Failed to acquire database connection from pool: ").append(e.getMessage()).toString());
                if(doCallback(103, cur.getDsName(), next))
                {
                    if(curPool == pools.size() - 1)
                        curPool = 0;
                    else
                        curPool++;
                    i++;
                }
            }
        } while(true);
        if(log.isDebug())
            log.debug("Connection was successfully acquired.");
        return conn;
    }

    private boolean doCallback(int op, String pool, String next)
        throws SQLException
    {
        int des = callback.allowFailover(op, pool, next);
        switch(des)
        {
        case 0: // '\0'
            if(next == null)
                throw new SQLException("Unable to acquire database connection from pool: All pools failed to provide connection.");
            else
                return true;

        case 2: // '\002'
            if(op == 104)
                throw new SQLException("Unable to acquire database connection from pool (callback decision): Pool is busy.");
            else
                throw new SQLException("Unable to acquire database connection from pool (callback decision): Connection validation failure.");

        case 1: // '\001'
            return false;
        }
        throw new IllegalArgumentException((new StringBuilder()).append("Unable to resolve callback response: ").append(des).toString());
    }

    private int getStartPosition()
    {
        Object obj = marker.getMutex();
        JVM INSTR monitorenter ;
        int val = marker.postIncr();
        if(marker.get() >= pools.size())
            marker.set(0);
        return val;
        Exception exception;
        exception;
        throw exception;
    }

    public void addDatasource(String name, DataSource ds)
        throws ObjectPoolException
    {
        ArgAssert.nullArg(name, "datasoureName");
        ArgAssert.nullArg(ds, "datasoure");
        DataSourcePoolFactory factory = new DataSourcePoolFactory(name, ds, validationQuery);
        ObjectPool pool = poolServ.createPool((new StringBuilder()).append("multipool-ds-").append(pools.size()).toString(), poolSize, factory, true, resizePolicy);
        factory.setPool(pool);
        pools.add(new PoolWrapper(name, pool, ds));
        if(log.isInfo())
            log.info((new StringBuilder()).append("Registered data source ").append(ds).toString());
    }

    public String getValidationQuery()
    {
        return validationQuery;
    }

    public PoolFailoverCallback getCallback()
    {
        return callback;
    }

    public ObjectPoolResizePolicy getResizePolicy()
    {
        return resizePolicy;
    }

    public int getLoginTimeout()
    {
        throw new UnsupportedOperationException("getLoginTimeout() is not supported.");
    }

    public void setLoginTimeout(int seconds)
    {
        throw new UnsupportedOperationException("getLoginTimeout() is not supported.");
    }

    public PrintWriter getLogWriter()
    {
        throw new UnsupportedOperationException("getLoginTimeout() is not supported.");
    }

    public void setLogWriter(PrintWriter out)
    {
        throw new UnsupportedOperationException("getLoginTimeout() is not supported.");
    }

    public Connection getConnection(String username, String password)
    {
        throw new UnsupportedOperationException("getLoginTimeout() is not supported.");
    }

    public String toString()
    {
        return (new StringBuilder()).append("Multidatasource[size=").append(poolSize).append(", callback=").append(callback).append(", resizePolicy=").append(resizePolicy).append(", validationQry=").append(validationQuery).append("]").toString();
    }

    public boolean isWrapperFor(Class arg0)
        throws SQLException
    {
        throw new UnsupportedOperationException("isWrapperFor() is not supported.");
    }

    public Object unwrap(Class arg0)
        throws SQLException
    {
        throw new UnsupportedOperationException("unwrap() is not supported.");
    }

    public static final int LOAD_BALANCING = 1;
    public static final int HIGH_AVAILABILITY = 2;
    private Log log;
    private final List pools;
    private final ObjectPoolService poolServ;
    private final String validationQuery;
    private final ObjectPoolResizePolicy resizePolicy;
    private final PoolFailoverCallback callback;
    private final int poolSize;
    private BoxedInt32Sync marker;
    private final int mode;
}
