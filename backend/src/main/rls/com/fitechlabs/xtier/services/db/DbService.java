// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.db;

import com.fitechlabs.xtier.kernel.KernelService;
import java.sql.*;
import java.util.Set;
import javax.sql.*;

// Referenced classes of package com.fitechlabs.xtier.services.db:
//            DbException, DbSession, DbJdbc3Session, DbXaSession,
//            DbJdbc3XaSession

public interface DbService
    extends KernelService
{

    public abstract void addDs(String s, DataSource datasource, String s1)
        throws DbException;

    public abstract void addDs(String s, XADataSource xadatasource, String s1)
        throws DbException;

    public abstract void addDs(String s, DataSource datasource, String s1, String s2, String s3, boolean flag, boolean flag1,
                               boolean flag2)
        throws DbException;

    public abstract void addDs(String s, XADataSource xadatasource, String s1, String s2, String s3, boolean flag, boolean flag1,
                               boolean flag2)
        throws DbException;

    public abstract void changeDsProps(String s, String s1, String s2, boolean flag, boolean flag1, boolean flag2)
        throws DbException;

    public abstract DataSource getDs(String s)
        throws DbException;

    public abstract XADataSource getXaDs(String s)
        throws DbException;

    public abstract boolean removeDs(String s);

    public abstract void removeAllDss();

    public abstract Set getAllDss()
        throws DbException;

    public abstract Set getSupportedDbs();

    public abstract DbSession createSession(String s, String s1, int i, int j, boolean flag)
        throws DbException;

    public abstract DbJdbc3Session createJdbc3Session(String s, String s1, int i, int j, boolean flag)
        throws DbException;

    public abstract DbSession createSession(String s)
        throws DbException;

    public abstract DbJdbc3Session createJdbc3Session(String s)
        throws DbException;

    public abstract DbXaSession createXaSession(String s)
        throws DbException;

    public abstract DbJdbc3XaSession createJdbc3XaSession(String s)
        throws DbException;

    public abstract DbSession getSession(String s)
        throws DbException;

    public abstract DbJdbc3Session getJdbc3Session(String s)
        throws DbException;

    public abstract DbXaSession getXaSession(String s)
        throws DbException;

    public abstract DbJdbc3XaSession getJdbc3XaSession(String s)
        throws DbException;

    public abstract Set getAllSessions();

    public abstract Set getAllXaSessions();

    public abstract void close(ResultSet resultset);

    public abstract void close(Connection connection);

    public abstract void close(PooledConnection pooledconnection);

    public abstract void close(Statement statement);

    public abstract void setNvl(PreparedStatement preparedstatement, int i, Object obj)
        throws DbException;

    public abstract String escapeSql(String s);

    public abstract String getSqlCall(String s, int i, boolean flag);

    public abstract String getDsDbType(String s)
        throws DbException;

    public abstract String getDsUser(String s)
        throws DbException;

    public abstract boolean isDsLogWarns(String s)
        throws DbException;

    public abstract boolean isDsLogStmts(String s)
        throws DbException;

    public abstract boolean isDsRaiseWarns(String s)
        throws DbException;

    public static final int ISOLATION_READ_COMMITTED = 1;
    public static final int ISOLATION_READ_UNCOMMITTED = 2;
    public static final int ISOLATION_REPEATABLE_READ = 3;
    public static final int ISOLATION_SERIALIZABLE = 4;
    public static final int ISOLATION_DEFAULT = 5;
    public static final int COMMIT_AUTO = 101;
    public static final int COMMIT_USER = 102;
}
