// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.db.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.db.DbException;
import java.util.HashMap;
import java.util.Map;

// Referenced classes of package com.fitechlabs.xtier.services.db.impl:
//            DbPredefinedStatement, DbManager

class DbSqlManager
{

    private DbSqlManager()
    {
        stmts = new HashMap();
        dbMgr = DbManager.getInstance();
    }

    static DbSqlManager getInstance()
    {
        return sqlMgr;
    }

    DbPredefinedStatement getDbPredefinedStatement(String s, int i)
        throws DbException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        Object obj = mutex;
        JVM INSTR monitorenter ;
        DbPredefinedStatement dbpredefinedstatement = (DbPredefinedStatement)stmts.get(s);
        if(dbpredefinedstatement == null)
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR50", s));
        if(i != dbpredefinedstatement.getType())
            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR51", s));
        return dbpredefinedstatement;
        Exception exception;
        exception;
        throw exception;
    }

    void addDbPredefinedStmt(DbPredefinedStatement dbpredefinedstatement)
    {
        stmts.put(dbpredefinedstatement.getName(), dbpredefinedstatement);
    }

    void close()
    {
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

    private Map stmts;
    private final Object mutex = new Object();
    private DbManager dbMgr;
    private static DbSqlManager sqlMgr = new DbSqlManager();
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(DbSqlManager.class).desiredAssertionStatus();
    }
}
