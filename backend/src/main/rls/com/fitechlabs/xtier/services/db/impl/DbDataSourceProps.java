// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.db.impl;


class DbDataSourceProps
{

    DbDataSourceProps(String s, String s1)
    {
        user = null;
        passwd = null;
        logWarns = false;
        logStmts = false;
        raiseWarns = false;
        if(!$assertionsDisabled && (s == null || s1 == null))
        {
            throw new AssertionError();
        } else
        {
            name = s;
            dbType = s1;
            return;
        }
    }

    DbDataSourceProps(DbDataSourceProps dbdatasourceprops)
    {
        user = null;
        passwd = null;
        logWarns = false;
        logStmts = false;
        raiseWarns = false;
        name = dbdatasourceprops.name;
        dbType = dbdatasourceprops.dbType;
        user = dbdatasourceprops.user;
        passwd = dbdatasourceprops.passwd;
        logWarns = dbdatasourceprops.logWarns;
        logStmts = dbdatasourceprops.logStmts;
        raiseWarns = dbdatasourceprops.raiseWarns;
    }

    String getName()
    {
        return name;
    }

    String getDbType()
    {
        return dbType;
    }

    void setUser(String s)
    {
        user = s;
    }

    String getUser()
    {
        return user;
    }

    void setPassword(String s)
    {
        passwd = s;
    }

    String getPassword()
    {
        return passwd;
    }

    void setLogWarns(boolean flag)
    {
        logWarns = flag;
    }

    boolean isLogWarns()
    {
        return logWarns;
    }

    void setLogStmts(boolean flag)
    {
        logStmts = flag;
    }

    boolean isLogStmts()
    {
        return logStmts;
    }

    void setRaiseWarns(boolean flag)
    {
        raiseWarns = flag;
    }

    boolean isRaiseWarns()
    {
        return raiseWarns;
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

    private String name;
    private String dbType;
    private String user;
    private String passwd;
    private boolean logWarns;
    private boolean logStmts;
    private boolean raiseWarns;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(DbDataSourceProps.class).desiredAssertionStatus();
    }
}
