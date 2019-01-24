// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.db.multipool;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.services.log.LogService;
import com.fitechlabs.xtier.services.log.Logger;
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

// Referenced classes of package com.fitechlabs.xtier.services.db.multipool:
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

        public PoolWrapper(String s, ObjectPool objectpool, DataSource datasource)
        {
            pool = objectpool;
            ds = datasource;
            dsName = s;
        }
    }

    private final class ObjectPoolAdapter
        implements ObjectPool
    {

        public Object acquire()
            throws ObjectPoolException
        {
            if(!$assertionsDisabled && realPool == null)
                throw new AssertionError();
            else
                return realPool.acquire();
        }

        public Object acquireWait()
            throws ObjectPoolException
        {
            if(!$assertionsDisabled && realPool == null)
                throw new AssertionError();
            else
                return realPool.acquireWait();
        }

        public Object acquireWait(long l)
            throws ObjectPoolException
        {
            if(!$assertionsDisabled && realPool == null)
                throw new AssertionError();
            else
                return realPool.acquireWait(l);
        }

        public boolean addListener(ObjectPoolListener objectpoollistener)
        {
            if(!$assertionsDisabled && realPool == null)
                throw new AssertionError();
            else
                return realPool.addListener(objectpoollistener);
        }

        public int getAcquired()
        {
            if(!$assertionsDisabled && realPool == null)
                throw new AssertionError();
            else
                return realPool.getAcquired();
        }

        public List getAllListeners()
        {
            if(!$assertionsDisabled && realPool == null)
                throw new AssertionError();
            else
                return realPool.getAllListeners();
        }

        public ObjectPoolFactory getFactory()
        {
            if(!$assertionsDisabled && realPool == null)
                throw new AssertionError();
            else
                return realPool.getFactory();
        }

        public int getFree()
        {
            if(!$assertionsDisabled && realPool == null)
                throw new AssertionError();
            else
                return realPool.getFree();
        }

        public String getName()
        {
            if(!$assertionsDisabled && realPool == null)
                throw new AssertionError();
            else
                return realPool.getName();
        }

        public ObjectPoolResizePolicy getResizePolicy()
        {
            if(!$assertionsDisabled && realPool == null)
                throw new AssertionError();
            else
                return realPool.getResizePolicy();
        }

        public int getSize()
        {
            if(!$assertionsDisabled && realPool == null)
                throw new AssertionError();
            else
                return realPool.getSize();
        }

        public ObjectPoolStats getStats()
        {
            if(!$assertionsDisabled && realPool == null)
                throw new AssertionError();
            else
                return realPool.getStats();
        }

        public void invalidate(Object obj)
            throws ObjectPoolException
        {
            if(!$assertionsDisabled && realPool == null)
            {
                throw new AssertionError();
            } else
            {
                realPool.invalidate(obj);
                return;
            }
        }

        public boolean isLazy()
        {
            if(!$assertionsDisabled && realPool == null)
                throw new AssertionError();
            else
                return realPool.isLazy();
        }

        public void release(Object obj)
            throws ObjectPoolException
        {
            if(!$assertionsDisabled && realPool == null)
            {
                throw new AssertionError();
            } else
            {
                realPool.release(obj);
                return;
            }
        }

        public boolean removeListener(ObjectPoolListener objectpoollistener)
        {
            if(!$assertionsDisabled && realPool == null)
                throw new AssertionError();
            else
                return realPool.removeListener(objectpoollistener);
        }

        public void resize(int i)
            throws ObjectPoolException
        {
            if(!$assertionsDisabled && realPool == null)
            {
                throw new AssertionError();
            } else
            {
                realPool.resize(i);
                return;
            }
        }

        public void resize(float f)
            throws ObjectPoolException
        {
            if(!$assertionsDisabled && realPool == null)
            {
                throw new AssertionError();
            } else
            {
                realPool.resize(f);
                return;
            }
        }

        void setRealPool(ObjectPool objectpool)
        {
            realPool = objectpool;
        }

        private ObjectPool realPool;
        static final boolean $assertionsDisabled; /* synthetic field */


        private ObjectPoolAdapter()
        {
            super();
            realPool = null;
        }

    }


    public MultiPoolDataSource(int i, String s, int j, PoolFailoverCallback poolfailovercallback, ObjectPoolResizePolicy objectpoolresizepolicy)
    {
        pools = new ArrayList();
        marker = new BoxedInt32Sync(0);
        ArgAssert.nullArg(s, "validationQuery");
        ArgAssert.nullArg(objectpoolresizepolicy, "poolResizePolicy");
        ArgAssert.nullArg(poolfailovercallback, "callback");
        ArgAssert.illegalArg(i > 0, "poolSize.");
        ArgAssert.illegalArg(j == 2 || j == 1, "mode");
        poolServ = XtierKernel.getInstance().objpool();
        validationQuery = s;
        resizePolicy = objectpoolresizepolicy;
        callback = poolfailovercallback;
        poolSize = i;
        mode = j;
        log = XtierKernel.getInstance().log().getLogger("db-multipool");
    }

    public MultiPoolDataSource(int i, String s)
    {
        this(i, 2, s);
    }

    public MultiPoolDataSource(int i, int j, String s)
    {
        this(i, s, j, ((PoolFailoverCallback) (new BasicFailoverCallback())), ((ObjectPoolResizePolicy) (new ObjectPoolStrictPolicy())));
    }

    public Connection getConnection()
        throws SQLException
    {
        Connection connection = null;
        int i = mode != 1 ? 0 : getStartPosition();
        int j = 0;
        do
        {
            if(j >= pools.size())
                break;
            PoolWrapper poolwrapper = (PoolWrapper)pools.get(i);
            int k = i != pools.size() - 1 ? i + 1 : 0;
            String s = j >= pools.size() - 1 ? null : ((PoolWrapper)pools.get(k)).getDsName();
            if(poolwrapper.getPool().getFree() == 0 && doCallback(104, poolwrapper.getDsName(), s))
            {
                if(i == pools.size() - 1)
                    i = 0;
                else
                    i++;
                j++;
                continue;
            }
            try
            {
                connection = (Connection)poolwrapper.getPool().acquireWait();
                break;
            }
            catch(ObjectPoolException objectpoolexception)
            {
                if(!(objectpoolexception instanceof InvalidConnectionException) && !(objectpoolexception.getCause() instanceof SQLException))
                    throw new SQLException("Unable to acquire database connection from pool: " + objectpoolexception.getMessage());
                if(!(objectpoolexception instanceof InvalidConnectionException))
                    log.debug("Failed to acquire database connection from pool[ds=" + poolwrapper.getDsName() + "]: " + objectpoolexception.getMessage());
                if(doCallback(103, poolwrapper.getDsName(), s))
                {
                    if(i == pools.size() - 1)
                        i = 0;
                    else
                        i++;
                    j++;
                }
            }
        } while(true);
        return connection;
    }

    private boolean doCallback(int i, String s, String s1)
        throws SQLException
    {
        int j = callback.allowFailover(i, s, s1);
        switch(j)
        {
        case 0: // '\0'
            if(s1 == null)
                throw new SQLException("Unable to acquire database connection from pool: All pools failed to provide connection.");
            else
                return true;

        case 2: // '\002'
            if(i == 104)
                throw new SQLException("Unable to acquire database connection from pool (callback decision): Pool is busy.");
            else
                throw new SQLException("Unable to acquire database connection from pool (callback decision): Connection validation failure.");

        case 1: // '\001'
            return false;
        }
        throw new IllegalArgumentException("Unable to resolve callback response: " + j);
    }

    private int getStartPosition()
    {
        Object obj = marker.getMutex();
        JVM INSTR monitorenter ;
        int i = marker.postIncr();
        if(marker.get() >= pools.size())
            marker.set(0);
        return i;
        Exception exception;
        exception;
        throw exception;
    }

    public void addDatasource(String s, DataSource datasource)
        throws ObjectPoolException
    {
        ArgAssert.nullArg(s, "datasoureName");
        ArgAssert.nullArg(datasource, "datasoure");
        log.info("Initializing datasource [ds=" + s + ", size=" + poolSize + "].");
        DataSourcePoolFactory datasourcepoolfactory = new DataSourcePoolFactory(s, datasource, validationQuery);
        ObjectPoolAdapter objectpooladapter = new ObjectPoolAdapter();
        datasourcepoolfactory.setPool(objectpooladapter);
        boolean flag = false;
        ObjectPool objectpool = null;
        try
        {
            objectpool = poolServ.createPool("multipool-ds-" + pools.size(), poolSize, datasourcepoolfactory, false, resizePolicy);
        }
        catch(ObjectPoolException objectpoolexception)
        {
            log.info("Failed to initialize datasource connections. Switched to lazy mode [ds=" + s + ", failure=" + objectpoolexception.getMessage() + "].");
            flag = true;
            objectpool = poolServ.createPool("multipool-ds-" + pools.size(), poolSize, datasourcepoolfactory, true, resizePolicy);
        }
        objectpooladapter.setRealPool(objectpool);
        pools.add(new PoolWrapper(s, objectpool, datasource));
        log.info("Initialized datasource [ds=" + s + ", size=" + poolSize + ", lazy=" + flag + ", pool-callback=" + callback.getClass().getName() + ", pool-resize-policy=" + resizePolicy.getClass().getName() + "].");
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

    public void setLoginTimeout(int i)
    {
        throw new UnsupportedOperationException("setLoginTimeout(int) is not supported.");
    }

    public PrintWriter getLogWriter()
    {
        throw new UnsupportedOperationException("getLoginTimeout() is not supported.");
    }

    public void setLogWriter(PrintWriter printwriter)
    {
        throw new UnsupportedOperationException("setLogWriter(PrintWriter) is not supported.");
    }

    public Connection getConnection(String s, String s1)
    {
        throw new UnsupportedOperationException("getConnection(String, String) is not supported.");
    }

    public String toString()
    {
        return "MultiPoolDataSource [size=" + poolSize + ", callback=" + callback + ", resizePolicy=" + resizePolicy + ", validationQry=" + validationQuery + "]";
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

    public static final int LOAD_BALANCING = 1;
    public static final int HIGH_AVAILABILITY = 2;
    private final Logger log;
    private final List pools;
    private final ObjectPoolService poolServ;
    private final String validationQuery;
    private final ObjectPoolResizePolicy resizePolicy;
    private final PoolFailoverCallback callback;
    private final int poolSize;
    private BoxedInt32Sync marker;
    private final int mode;
    static Class class$com$fitechlabs$xtier$services$db$multipool$MultiPoolDataSource; /* synthetic field */
}
