// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.db.impl;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.kernel.ioc.IocDescriptor;
import com.fitechlabs.xtier.kernel.ioc.IocDescriptorException;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.db.*;
import com.fitechlabs.xtier.services.jndi.JndiService;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.sql.XADataSource;

// Referenced classes of package com.fitechlabs.xtier.services.db.impl:
//            DbSessionImpl, DbJdbc3SessionImpl, DbDataSourceProps, DbXaSessionImpl,
//            DbJdbc3XaSessionImpl

class DbManager
{
    private class DsHolder
    {

        void setIocDs(IocDescriptor iocdescriptor)
        {
            iocDs = iocdescriptor;
        }

        IocDescriptor getIocDs()
        {
            return iocDs;
        }

        void setJndiDsName(String s)
        {
            jndiDsName = s;
        }

        String getJndiDsName()
        {
            return jndiDsName;
        }

        private IocDescriptor iocDs;
        private String jndiDsName;
        static final boolean $assertionsDisabled; /* synthetic field */


        DsHolder(IocDescriptor iocdescriptor, String s)
        {
            super();
            if(!$assertionsDisabled && iocdescriptor != null && s != null)
            {
                throw new AssertionError();
            } else
            {
                setIocDs(iocdescriptor);
                setJndiDsName(s);
                return;
            }
        }
    }

    private class PredefinedDatabase
    {

        private void sort(int ai[])
        {
            if(ai != null)
                Arrays.sort(ai);
        }

        private boolean hasCode(int ai[], int i)
        {
            if(ai != null)
                return Arrays.binarySearch(ai, i) >= 0;
            else
                return false;
        }

        String getName()
        {
            return name;
        }

        boolean isBadSqlErrCode(int i)
        {
            return hasCode(badSqlErrCodes, i);
        }

        boolean isIntegrityErrCode(int i)
        {
            return hasCode(integrityErrCodes, i);
        }

        boolean isDbNotAvailErrCode(int i)
        {
            return hasCode(dbNotAvailErrCodes, i);
        }

        boolean isOptLockFailErrCode(int i)
        {
            return hasCode(optLockFailErrCodes, i);
        }

        boolean isNoDataFoundErrCode(int i)
        {
            return hasCode(noDataFoundErrCodes, i);
        }

        boolean isTooManyRowsErrCode(int i)
        {
            return hasCode(tooManyRowsErrCodes, i);
        }

        private String name;
        private int badSqlErrCodes[];
        private int integrityErrCodes[];
        private int dbNotAvailErrCodes[];
        private int optLockFailErrCodes[];
        private int noDataFoundErrCodes[];
        private int tooManyRowsErrCodes[];
        static final boolean $assertionsDisabled; /* synthetic field */


        PredefinedDatabase(String s, int ai[], int ai1[], int ai2[], int ai3[], int ai4[],
                int ai5[])
        {
            super();
            if(!$assertionsDisabled && s == null)
            {
                throw new AssertionError();
            } else
            {
                name = s;
                badSqlErrCodes = ai;
                integrityErrCodes = ai1;
                dbNotAvailErrCodes = ai2;
                optLockFailErrCodes = ai3;
                noDataFoundErrCodes = ai4;
                tooManyRowsErrCodes = ai5;
                sort(badSqlErrCodes);
                sort(integrityErrCodes);
                sort(dbNotAvailErrCodes);
                sort(optLockFailErrCodes);
                sort(noDataFoundErrCodes);
                sort(tooManyRowsErrCodes);
                return;
            }
        }
    }


    private DbManager()
    {
        dbs = new HashMap();
        dss = new HashMap();
        xaDss = new HashMap();
        dsProps = new HashMap();
        dsHolders = new HashMap();
        sessions = new HashMap();
        xaSessions = new HashMap();
        idGen = 0L;
        ctx = null;
    }

    static DbManager getInstance()
    {
        return dbMgr;
    }

    Set getSupportedDbs()
    {
        return Collections.unmodifiableSet(new HashSet(dbs.keySet()));
    }

    DbSession createSession(String s, String s1, int i, int j, boolean flag)
        throws DbException
    {
        if(!$assertionsDisabled && (s == null || s1 == null))
            throw new AssertionError();
        Object obj = mutex;
        JVM INSTR monitorenter ;
        checkSessName(s);
        DbSessionImpl dbsessionimpl = new DbSessionImpl(s, getDsProps(s1), i, j, flag);
        sessions.put(s, dbsessionimpl);
        return dbsessionimpl;
        Exception exception;
        exception;
        throw exception;
    }

    private void checkSessName(String s)
        throws DbException
    {
        if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            throw new AssertionError();
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        if(sessions.containsKey(s))
            throw newDbException(L10n.format("SRVC.DB.ERR19", s));
        if(s.indexOf("xtier") == 0)
            throw newDbException(L10n.format("SRVC.DB.ERR20"));
        else
            return;
    }

    DbJdbc3Session createJdbc3Session(String s, String s1, int i, int j, boolean flag)
        throws DbException
    {
        if(!$assertionsDisabled && (s == null || s1 == null))
            throw new AssertionError();
        Object obj = mutex;
        JVM INSTR monitorenter ;
        checkSessName(s);
        DbJdbc3SessionImpl dbjdbc3sessionimpl = new DbJdbc3SessionImpl(s, getDsProps(s1), i, j, flag);
        sessions.put(s, dbjdbc3sessionimpl);
        return dbjdbc3sessionimpl;
        Exception exception;
        exception;
        throw exception;
    }

    DbSession createSession(String s)
        throws DbException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        Object obj = mutex;
        JVM INSTR monitorenter ;
        String s1 = "xtier" + idGen++;
        DbSessionImpl dbsessionimpl = new DbSessionImpl(s1, getDsProps(s), 5, 101, false);
        sessions.put(s1, dbsessionimpl);
        return dbsessionimpl;
        Exception exception;
        exception;
        throw exception;
    }

    private DbDataSourceProps getDsProps(String s)
        throws DbException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        DbDataSourceProps dbdatasourceprops = (DbDataSourceProps)dsProps.get(s);
        if(dbdatasourceprops == null)
            throw newDbException(L10n.format("SRVC.DB.ERR18", s));
        else
            return dbdatasourceprops;
    }

    DbJdbc3Session createJdbc3Session(String s)
        throws DbException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        Object obj = mutex;
        JVM INSTR monitorenter ;
        String s1 = "xtier" + idGen++;
        DbJdbc3SessionImpl dbjdbc3sessionimpl = new DbJdbc3SessionImpl(s1, getDsProps(s), 5, 101, false);
        sessions.put(s1, dbjdbc3sessionimpl);
        return dbjdbc3sessionimpl;
        Exception exception;
        exception;
        throw exception;
    }

    DbXaSession createXaSession(String s)
        throws DbException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        Thread thread = Thread.currentThread();
        Object obj = mutex;
        JVM INSTR monitorenter ;
        Object obj1 = (Map)xaSessions.get(thread);
        if(obj1 == null)
            obj1 = new HashMap();
        else
        if(((Map) (obj1)).containsKey(s))
            throw newDbException(L10n.format("SRVC.DB.ERR21", s));
        DbXaSessionImpl dbxasessionimpl = new DbXaSessionImpl(getDsProps(s));
        ((Map) (obj1)).put(s, dbxasessionimpl);
        xaSessions.put(thread, obj1);
        return dbxasessionimpl;
        Exception exception;
        exception;
        throw exception;
    }

    DbJdbc3XaSession createJdbc3XaSession(String s)
        throws DbException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        Thread thread = Thread.currentThread();
        Object obj = mutex;
        JVM INSTR monitorenter ;
        Object obj1 = (Map)xaSessions.get(thread);
        if(obj1 == null)
            obj1 = new HashMap();
        else
        if(((Map) (obj1)).containsKey(s))
            throw newDbException(L10n.format("SRVC.DB.ERR21", s));
        DbJdbc3XaSessionImpl dbjdbc3xasessionimpl = new DbJdbc3XaSessionImpl(getDsProps(s));
        ((Map) (obj1)).put(s, dbjdbc3xasessionimpl);
        xaSessions.put(thread, obj1);
        return dbjdbc3xasessionimpl;
        Exception exception;
        exception;
        throw exception;
    }

    DbSession getSession(String s)
        throws DbException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        Object obj = null;
        Object obj1 = mutex;
        JVM INSTR monitorenter ;
        obj = sessions.get(s);
        if(obj == null)
            return null;
        if((obj instanceof DbJdbc3Session))
            throw newDbException(L10n.format("SRVC.DB.ERR61", s));
        (DbSession)obj;
        obj1;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    DbJdbc3Session getJdbc3Session(String s)
        throws DbException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        Object obj = null;
        Object obj1 = mutex;
        JVM INSTR monitorenter ;
        obj = sessions.get(s);
        if(obj == null)
            return null;
        if(!(obj instanceof DbJdbc3Session)) goto _L2; else goto _L1
_L1:
        (DbJdbc3Session)obj;
        obj1;
        JVM INSTR monitorexit ;
        return;
_L2:
        obj1;
        JVM INSTR monitorexit ;
          goto _L3
        Exception exception;
        exception;
        throw exception;
_L3:
        throw newDbException(L10n.format("SRVC.DB.ERR61", s));
    }

    DbXaSession getXaSession(String s)
        throws DbException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        Object obj = mutex;
        JVM INSTR monitorenter ;
        Map map;
        map = (Map)xaSessions.get(Thread.currentThread());
        if(map == null)
            return null;
        Object obj1 = map.get(s);
        if(obj1 != null) goto _L2; else goto _L1
_L1:
        null;
        obj;
        JVM INSTR monitorexit ;
        return;
_L2:
        if((obj1 instanceof DbJdbc3XaSession))
            throw newDbException(L10n.format("SRVC.DB.ERR61", s));
        (DbXaSession)obj1;
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    DbJdbc3XaSession getJdbc3XaSession(String s)
        throws DbException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        Object obj = mutex;
        JVM INSTR monitorenter ;
        Map map;
        map = (Map)xaSessions.get(Thread.currentThread());
        if(map == null)
            return null;
        Object obj1 = map.get(s);
        if(obj1 != null) goto _L2; else goto _L1
_L1:
        null;
        obj;
        JVM INSTR monitorexit ;
        return;
_L2:
        if(!(obj1 instanceof DbJdbc3XaSession)) goto _L4; else goto _L3
_L3:
        (DbJdbc3XaSession)obj1;
        obj;
        JVM INSTR monitorexit ;
        return;
_L4:
        obj;
        JVM INSTR monitorexit ;
          goto _L5
        Exception exception;
        exception;
        throw exception;
_L5:
        throw newDbException(L10n.format("SRVC.DB.ERR61", s));
    }

    Set getAllSessions()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return Collections.unmodifiableSet(new HashSet(sessions.values()));
        Exception exception;
        exception;
        throw exception;
    }

    Set getAllXaSessions()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        Map map = (Map)xaSessions.get(Thread.currentThread());
        return map != null ? Collections.unmodifiableSet(new HashSet(map.values())) : null;
        Exception exception;
        exception;
        throw exception;
    }

    void changeDsProps(String s, String s1, String s2, boolean flag, boolean flag1, boolean flag2)
        throws DbException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        if(!$assertionsDisabled && s1 == null && s2 != null)
            throw new AssertionError();
        synchronized(mutex)
        {
            DbDataSourceProps dbdatasourceprops = getDsProps(s);
            dbdatasourceprops.setUser(s1);
            dbdatasourceprops.setPassword(s2);
            dbdatasourceprops.setLogWarns(flag);
            dbdatasourceprops.setLogStmts(flag1);
            dbdatasourceprops.setRaiseWarns(flag2);
        }
    }

    void addPredefinedDatabase(String s, int ai[], int ai1[], int ai2[], int ai3[], int ai4[], int ai5[])
    {
        dbs.put(s, new PredefinedDatabase(s, ai, ai1, ai2, ai3, ai4, ai5));
    }

    private Object getDsObj(String s)
        throws DbException
    {
        if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            throw new AssertionError();
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        DsHolder dsholder = (DsHolder)dsHolders.remove(s);
        if(dsholder == null)
            return null;
        IocDescriptor iocdescriptor = dsholder.getIocDs();
        String s1 = dsholder.getJndiDsName();
        Object obj = null;
        if(iocdescriptor != null)
        {
            try
            {
                obj = iocdescriptor.createNewObj();
            }
            catch(IocDescriptorException iocdescriptorexception)
            {
                throw newDbException(L10n.format("SRVC.DB.ERR3", iocdescriptor.getUid()), iocdescriptorexception);
            }
        } else
        {
            if(!$assertionsDisabled && s1 == null)
                throw new AssertionError();
            try
            {
                if(ctx == null)
                    ctx = XtierKernel.getInstance().jndi().getInitialContext();
            }
            catch(NamingException namingexception)
            {
                throw newDbException(L10n.format("SRVC.DB.ERR2"), namingexception);
            }
            try
            {
                obj = ctx.lookup(s1);
            }
            catch(NamingException namingexception1)
            {
                throw newDbException(L10n.format("SRVC.DB.ERR4", s1), namingexception1);
            }
        }
        if(!$assertionsDisabled && obj == null)
            throw new AssertionError();
        else
            return obj;
    }

    private DataSource getDsImpl(String s)
        throws DbException
    {
        if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            throw new AssertionError();
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        DataSource datasource = (DataSource)dss.get(s);
        if(datasource != null)
            return datasource;
        Object obj = getDsObj(s);
        if(obj == null)
            return null;
        if(!(obj instanceof DataSource))
            throw new DbException(L10n.format("SRVC.DB.ERR4", s));
        datasource = (DataSource)obj;
        DbDataSourceProps dbdatasourceprops = (DbDataSourceProps)dsProps.get(s);
        if(!$assertionsDisabled && dbdatasourceprops == null)
        {
            throw new AssertionError();
        } else
        {
            dss.put(s, datasource);
            return datasource;
        }
    }

    private XADataSource getXaDsImpl(String s)
        throws DbException
    {
        if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            throw new AssertionError();
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        XADataSource xadatasource = (XADataSource)xaDss.get(s);
        if(xadatasource != null)
            return xadatasource;
        Object obj = getDsObj(s);
        if(obj == null)
            return null;
        if(!(obj instanceof XADataSource))
            throw new DbException(L10n.format("SRVC.DB.ERR4", s));
        xadatasource = (XADataSource)obj;
        DbDataSourceProps dbdatasourceprops = (DbDataSourceProps)dsProps.get(s);
        if(!$assertionsDisabled && dbdatasourceprops == null)
        {
            throw new AssertionError();
        } else
        {
            xaDss.put(s, xadatasource);
            return xadatasource;
        }
    }

    DataSource getDs(String s)
        throws DbException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return getDsImpl(s);
        Exception exception;
        exception;
        throw exception;
    }

    XADataSource getXaDs(String s)
        throws DbException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return getXaDsImpl(s);
        Exception exception;
        exception;
        throw exception;
    }

    DataSource getStartedDs(String s)
        throws DbException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        Object obj = mutex;
        JVM INSTR monitorenter ;
        DataSource datasource = getDsImpl(s);
        if(datasource == null)
            throw new DbException(L10n.format("SRVC.DB.ERR18", s));
        return datasource;
        Exception exception;
        exception;
        throw exception;
    }

    XADataSource getStartedXaDs(String s)
        throws DbException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        Object obj = mutex;
        JVM INSTR monitorenter ;
        XADataSource xadatasource = getXaDsImpl(s);
        if(xadatasource == null)
            throw new DbException(L10n.format("SRVC.DB.ERR18", s));
        return xadatasource;
        Exception exception;
        exception;
        throw exception;
    }

    boolean removeDs(String s)
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        Object obj = mutex;
        JVM INSTR monitorenter ;
        boolean flag = dsProps.remove(s) != null;
        if(flag)
        {
            dss.remove(s);
            xaDss.remove(s);
        }
        return flag;
        Exception exception;
        exception;
        throw exception;
    }

    void removeAllDss()
    {
        synchronized(mutex)
        {
            dss.clear();
            xaDss.clear();
            dsProps.clear();
        }
    }

    void setNvl(PreparedStatement preparedstatement, int i, Object obj)
        throws DbException
    {
        try
        {
            if(obj != null)
                preparedstatement.setObject(i, obj);
            else
                preparedstatement.setNull(i, 0);
        }
        catch(SQLException sqlexception)
        {
            throw new DbException(sqlexception, L10n.format("SRVC.DB.ERR16"));
        }
    }

    Set getAllDss()
        throws DbException
    {
        HashSet hashset = new HashSet();
        Object obj = mutex;
        JVM INSTR monitorenter ;
        for(Iterator iterator = dsProps.keySet().iterator(); iterator.hasNext();)
        {
            String s = (String)iterator.next();
            DataSource datasource = getDsImpl(s);
            if(datasource != null)
            {
                hashset.add(datasource);
            } else
            {
                XADataSource xadatasource = getXaDsImpl(s);
                if(!$assertionsDisabled && xadatasource == null)
                    throw new AssertionError();
                hashset.add(xadatasource);
            }
        }

        return hashset;
        Exception exception;
        exception;
        throw exception;
    }

    DbException newDbException(String s)
    {
        return new DbException(s);
    }

    DbException newDbException(String s, int i)
    {
        return new DbException(s, i);
    }

    DbException newDbException(String s, SQLException sqlexception, String s1)
    {
        return newDbException(s, sqlexception, 2, s1);
    }

    DbException newDbException(String s, Throwable throwable)
    {
        DbException dbexception = newDbException(s);
        dbexception.initCause(throwable);
        return dbexception;
    }

    DbException newDbException(String s, SQLException sqlexception, int i, String s1)
    {
        int j = sqlexception.getErrorCode();
        PredefinedDatabase predefineddatabase = (PredefinedDatabase)dbs.get(s1);
        if(!$assertionsDisabled && predefineddatabase == null)
            throw new AssertionError();
        if(predefineddatabase.isBadSqlErrCode(j))
            return new DbException(sqlexception, s, 3);
        if(predefineddatabase.isIntegrityErrCode(j))
            return new DbException(sqlexception, s, 4);
        if(predefineddatabase.isDbNotAvailErrCode(j))
            return new DbException(sqlexception, s, 6);
        if(predefineddatabase.isOptLockFailErrCode(j))
            return new DbException(sqlexception, s, 8);
        if(predefineddatabase.isNoDataFoundErrCode(j))
            return new DbException(sqlexception, s, 7);
        if(predefineddatabase.isTooManyRowsErrCode(j))
            return new DbException(sqlexception, s, 5);
        else
            return new DbException(sqlexception, s, 2);
    }

    void close()
    {
        synchronized(mutex)
        {
            for(Iterator iterator = sessions.values().iterator(); iterator.hasNext(); ((DbSessionImpl)iterator.next()).closeImpl());
            for(Iterator iterator1 = xaSessions.values().iterator(); iterator1.hasNext();)
            {
                Map map = (Map)iterator1.next();
                Iterator iterator2 = map.values().iterator();
                while(iterator2.hasNext())
                    ((DbXaSessionImpl)iterator2.next()).closeImpl();
            }

            dbs.clear();
            dss.clear();
            xaDss.clear();
            dsProps.clear();
            dsHolders.clear();
            sessions.clear();
            xaSessions.clear();
        }
    }

    void deleteSession(String s)
    {
        synchronized(mutex)
        {
            sessions.remove(s);
        }
    }

    String getDsDbType(String s)
        throws DbException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return getDsProps(s).getDbType();
        Exception exception;
        exception;
        throw exception;
    }

    String getDsUser(String s)
        throws DbException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return getDsProps(s).getUser();
        Exception exception;
        exception;
        throw exception;
    }

    boolean isDsLogWarns(String s)
        throws DbException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return getDsProps(s).isLogWarns();
        Exception exception;
        exception;
        throw exception;
    }

    boolean isDsLogStmts(String s)
        throws DbException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return getDsProps(s).isLogStmts();
        Exception exception;
        exception;
        throw exception;
    }

    boolean isDsRaiseWarns(String s)
        throws DbException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return getDsProps(s).isRaiseWarns();
        Exception exception;
        exception;
        throw exception;
    }

    private void validateDs(String s, String s1)
        throws DbException
    {
        if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            throw new AssertionError();
        if(!$assertionsDisabled && (s == null || s1 == null))
            throw new AssertionError();
        if(dsProps.containsKey(s))
            throw newDbException(L10n.format("SRVC.DB.ERR22", s));
        if(!$assertionsDisabled && (dss.containsKey(s) || xaDss.containsKey(s)))
            throw new AssertionError();
        if(!dbs.containsKey(s1))
            throw newDbException(L10n.format("SRVC.DB.ERR23", s1));
        else
            return;
    }

    void addDs(String s, DataSource datasource, DbDataSourceProps dbdatasourceprops)
        throws DbException
    {
        if(!$assertionsDisabled && (s == null || datasource == null || dbdatasourceprops == null))
            throw new AssertionError();
        synchronized(mutex)
        {
            validateDs(s, dbdatasourceprops.getDbType());
            dss.put(s, datasource);
            dsProps.put(s, dbdatasourceprops);
        }
    }

    void addXaDs(String s, XADataSource xadatasource, DbDataSourceProps dbdatasourceprops)
        throws DbException
    {
        if(!$assertionsDisabled && (s == null || xadatasource == null || dbdatasourceprops == null))
            throw new AssertionError();
        synchronized(mutex)
        {
            validateDs(s, dbdatasourceprops.getDbType());
            xaDss.put(s, xadatasource);
            dsProps.put(s, dbdatasourceprops);
        }
    }

    void addDsHolder(String s, IocDescriptor iocdescriptor, String s1, DbDataSourceProps dbdatasourceprops)
    {
        if(!$assertionsDisabled && (s == null || dbdatasourceprops == null))
            throw new AssertionError();
        if(!$assertionsDisabled && iocdescriptor != null && s1 != null)
        {
            throw new AssertionError();
        } else
        {
            dsHolders.put(s, new DsHolder(iocdescriptor, s1));
            dsProps.put(s, dbdatasourceprops);
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

    private static final String DEFAULT_SESSION_PREFIX = "xtier";
    private Map dbs;
    private Map dss;
    private Map xaDss;
    private Map dsProps;
    private Map dsHolders;
    private Map sessions;
    private Map xaSessions;
    private final Object mutex = new Object();
    private long idGen;
    private static DbManager dbMgr = new DbManager();
    private InitialContext ctx;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(DbManager.class).desiredAssertionStatus();
    }
}
