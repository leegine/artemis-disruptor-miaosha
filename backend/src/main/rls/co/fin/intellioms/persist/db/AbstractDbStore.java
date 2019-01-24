// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   AbstractDbStore.java

package co.fin.intellioms.persist.db;

import co.fin.intellioms.persist.PersistException;
import co.fin.intellioms.tx.TxManager;
import co.fin.intellioms.tx.TxManagerException;
import co.fin.intellioms.util.Log;

import java.sql.*;

public abstract class AbstractDbStore
{

    public AbstractDbStore()
    {
    }

    protected Connection getConnection()
        throws TxManagerException
    {
        return TxManager.getConnection();
    }

    protected void close(ResultSet rs)
    {
        if(rs != null)
            try
            {
                rs.close();
            }
            catch(SQLException e)
            {
                if(log.isWarn())
                    log.warn("Unable to close database result set.", e);
            }
    }

    protected void rollback()
        throws PersistException
    {
        try
        {
            TxManager.rollback();
        }
        catch(TxManagerException e)
        {
            throw new PersistException((new StringBuilder()).append("Unable to rollback transaction: ").append(e.getMessage()).toString(), e);
        }
    }

    protected void close(Statement st)
    {
        if(st != null)
            try
            {
                st.close();
            }
            catch(SQLException e)
            {
                if(log.isWarn())
                    log.warn("Unable to close database statement.", e);
            }
    }

    protected final Log log = Log.getLogger(getClass());
}
