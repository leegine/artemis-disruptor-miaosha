// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   DBConnectionPoolFactory.java

package co.fin.intellioms.util;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.services.db.DbException;
import com.fitechlabs.xtier.services.db.DbService;
import com.fitechlabs.xtier.services.objpool.ObjectPoolException;
import com.fitechlabs.xtier.services.objpool.ObjectPoolFactory;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

public class DBConnectionPoolFactory
    implements ObjectPoolFactory
{

    public DBConnectionPoolFactory(String dataSourceName)
        throws DbException
    {
        ds = XtierKernel.getInstance().db().getDs(dataSourceName);
    }

    public Object createObj()
        throws ObjectPoolException
    {
        try
        {
            return ds.getConnection();
        }
        catch(SQLException e)
        {
            throw new ObjectPoolException(e.getMessage(), e);
        }
    }

    public void disposeObj(Object obj)
        throws ObjectPoolException
    {
        try
        {
            ((Connection)obj).close();
        }
        catch(SQLException e)
        {
            throw new ObjectPoolException("Unable to close database connection.", e);
        }
    }

    private DataSource ds;
}
