// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.db;

import com.fitechlabs.xtier.l10n.L10n;
import java.sql.SQLWarning;

// Referenced classes of package com.fitechlabs.xtier.services.db:
//            DbException

public class DbWarning extends DbException
{

    public DbWarning(SQLWarning sqlwarning, String s)
    {
        super(s);
        if(sqlwarning != null)
            setNextException(sqlwarning);
    }

    public SQLWarning getNextWarning()
    {
        java.sql.SQLException sqlexception = getNextException();
        if(sqlexception == null || !(sqlexception instanceof SQLWarning))
            return null;
        else
            return (SQLWarning)sqlexception;
    }

    public String toString()
    {
        return L10n.format("SRVC.DB.ERR49", getMessage(), getNextWarning());
    }
}
