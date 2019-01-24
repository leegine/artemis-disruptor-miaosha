// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.db.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.db.*;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.Utils;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Referenced classes of package com.fitechlabs.xtier.services.db.impl:
//            DbRowImpl, DbManager

class DbLazyRowListImpl
    implements DbLazyRowList
{
    class DbRowIteratorImpl
        implements DbRowIterator
    {

        public boolean hasNext()
            throws DbException
        {
            Object obj = mutex;
            JVM INSTR monitorenter ;
            for(; !loaded && fetchedRows.size() <= cursor; loadNextPage());
            if(loaded && cursor >= fetchedRows.size())
                return false;
            true;
            obj;
            JVM INSTR monitorexit ;
            return;
            Exception exception;
            exception;
            throw exception;
        }

        public DbRow next()
            throws DbException
        {
            Object obj = mutex;
            JVM INSTR monitorenter ;
            return get0(cursor++);
            Exception exception;
            exception;
            throw exception;
        }

        int cursor;
        static final boolean $assertionsDisabled; /* synthetic field */


        DbRowIteratorImpl(int i)
        {
            super();
            if(!$assertionsDisabled && i < 0)
            {
                throw new AssertionError();
            } else
            {
                cursor = i;
                return;
            }
        }
    }


    DbLazyRowListImpl(ResultSet resultset, String s)
        throws DbException
    {
        rs = null;
        pageSize = -1;
        fetchedRows = new ArrayList();
        loaded = false;
        dbMgr = null;
        dbType = null;
        if(!$assertionsDisabled && (resultset == null || s == null))
            throw new AssertionError();
        rs = resultset;
        dbType = s;
        try
        {
            pageSize = resultset.getFetchSize();
        }
        catch(SQLException sqlexception)
        {
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, s);
        }
        if(pageSize < 1)
            pageSize = 1;
        dbMgr = DbManager.getInstance();
        try
        {
            metaData = resultset.getMetaData();
        }
        catch(SQLException sqlexception1)
        {
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception1, s);
        }
    }

    public int getSize()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return fetchedRows.size();
        Exception exception;
        exception;
        throw exception;
    }

    public DbRow get(int i)
        throws DbException
    {
        ArgAssert.illegalRange(i >= 0, "i", "i >= 0");
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return get0(i);
        Exception exception;
        exception;
        throw exception;
    }

    private DbRow get0(int i)
        throws DbException
    {
        if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            throw new AssertionError();
        for(; !loaded && fetchedRows.size() <= i; loadNextPage());
        if(i < fetchedRows.size())
            return (DbRow)fetchedRows.get(i);
        else
            throw new IndexOutOfBoundsException(L10n.format("SRVC.DB.ERR17", String.valueOf(i)));
    }

    public DbRowIterator iterator()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return new DbRowIteratorImpl(0);
        Exception exception;
        exception;
        throw exception;
    }

    public DbRowIterator iterator(int i)
    {
        ArgAssert.illegalRange(i >= 0, "pos", "pos >= 0");
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return new DbRowIteratorImpl(i);
        Exception exception;
        exception;
        throw exception;
    }

    public boolean isEmpty()
        throws DbException
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        if(fetchedRows.size() != 0)
            return false;
        if(!loaded) goto _L2; else goto _L1
_L1:
        true;
        obj;
        JVM INSTR monitorexit ;
        return;
_L2:
        loadNextPage();
        fetchedRows.size() == 0;
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public DbRow[] toArray()
        throws DbException
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        while(!loaded)
            loadNextPage();
        return (DbRow[])fetchedRows.toArray();
        Exception exception;
        exception;
        throw exception;
    }

    public void toArray(DbRow adbrow[])
        throws DbException
    {
        ArgAssert.nullArg(adbrow, "arr");
        synchronized(mutex)
        {
            while(!loaded)
                loadNextPage();
            fetchedRows.toArray(adbrow);
        }
    }

    public int toArray(DbRow adbrow[], int i)
        throws DbException
    {
        ArgAssert.nullArg(adbrow, "arr");
        ArgAssert.illegalRange(i >= 0, "off", "off >= 0");
        Object obj = mutex;
        JVM INSTR monitorenter ;
        while(!loaded)
            loadNextPage();
        int j = fetchedRows.size();
        for(int k = 0; k < j; k++)
            adbrow[i + k] = (DbRow)fetchedRows.get(k);

        return i + j;
        Exception exception;
        exception;
        throw exception;
    }

    public void setPageSize(int i)
        throws DbException
    {
        ArgAssert.illegalRange(i > 0, "pageSize", "pageSize > 0");
        synchronized(mutex)
        {
            try
            {
                rs.setFetchSize(i);
            }
            catch(SQLException sqlexception)
            {
                throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, dbType);
            }
            pageSize = i;
        }
    }

    public int getPageSize()
        throws DbException
    {
        if(!$assertionsDisabled && pageSize == -1)
            throw new AssertionError();
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return pageSize;
        Exception exception;
        exception;
        throw exception;
    }

    public void close()
    {
        synchronized(mutex)
        {
            Statement statement = null;
            try
            {
                statement = rs.getStatement();
            }
            catch(SQLException sqlexception) { }
            Utils.close(rs);
            Utils.close(statement);
        }
    }

    void loadFirstPage()
        throws DbException
    {
        synchronized(mutex)
        {
            loadNextPage();
        }
    }

    private void loadNextPage()
        throws DbException
    {
        if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            throw new AssertionError();
        int i = fetchedRows.size();
        for(int j = 0; j < pageSize; j++)
        {
            try
            {
                if(!rs.next())
                {
                    loaded = true;
                    return;
                }
            }
            catch(SQLException sqlexception)
            {
                throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception, dbType);
            }
            int k = 0;
            try
            {
                k = metaData.getColumnCount();
            }
            catch(SQLException sqlexception1)
            {
                throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception1, dbType);
            }
            DbRowImpl dbrowimpl = new DbRowImpl(i + j + 1, k);
            try
            {
                for(int l = 1; l <= k; l++)
                    dbrowimpl.addObject(l, metaData.getColumnName(l), metaData.getColumnType(l), Utils.getDbValue(metaData.getColumnType(l), rs, l));

            }
            catch(SQLException sqlexception2)
            {
                throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR16"), sqlexception2, dbType);
            }
            fetchedRows.add(dbrowimpl);
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

    private final Object mutex = new Object();
    private ResultSet rs;
    private int pageSize;
    private List fetchedRows;
    private ResultSetMetaData metaData;
    private boolean loaded;
    private DbManager dbMgr;
    private String dbType;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(DbLazyRowListImpl.class).desiredAssertionStatus();
    }





}
