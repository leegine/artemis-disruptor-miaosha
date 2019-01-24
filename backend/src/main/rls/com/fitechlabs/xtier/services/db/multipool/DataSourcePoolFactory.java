// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.db.multipool;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.services.log.LogService;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.services.objpool.*;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

// Referenced classes of package com.fitechlabs.xtier.services.db.multipool:
//            DbConnectionImpl, DbConnection

class DataSourcePoolFactory
    implements ObjectPoolFactory
{

    public DataSourcePoolFactory(String s, DataSource datasource, String s1)
    {
        if(!$assertionsDisabled && datasource == null)
            throw new AssertionError("Datasource is null.");
        if(!$assertionsDisabled && s == null)
            throw new AssertionError("Datasource name is null.");
        if(!$assertionsDisabled && s1 == null)
        {
            throw new AssertionError("Validation query is null.");
        } else
        {
            dsName = s;
            ds = datasource;
            validationQuery = s1;
            log = XtierKernel.getInstance().log().getLogger("db-multipool");
            return;
        }
    }

    public Object createObj()
        throws ObjectPoolException
    {
        DbConnectionImpl dbconnectionimpl = null;
        try
        {
            Connection connection = ds.getConnection();
            dbconnectionimpl = new DbConnectionImpl(connection, pool, validationQuery);
        }
        catch(SQLException sqlexception)
        {
            throw new ObjectPoolException("Unable to open new database connection using datasource #" + dsName + ": " + sqlexception.getMessage(), sqlexception);
        }
        return dbconnectionimpl;
    }

    public void disposeObj(Object obj)
    {
        DbConnection dbconnection = (DbConnection)obj;
        try
        {
            dbconnection.getRealConnection().close();
        }
        catch(SQLException sqlexception)
        {
            log.error("Unable to close database connection using datasource #" + dsName + ": " + sqlexception.getMessage(), sqlexception);
        }
    }

    public ObjectPool getPool()
    {
        return pool;
    }

    public void setPool(ObjectPool objectpool)
    {
        if(!$assertionsDisabled && objectpool == null)
        {
            throw new AssertionError("Object pool is null.");
        } else
        {
            pool = objectpool;
            return;
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

    private final Logger log;
    private final String dsName;
    private final DataSource ds;
    private final String validationQuery;
    private ObjectPool pool;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(DataSourcePoolFactory.class).desiredAssertionStatus();
    }
}
