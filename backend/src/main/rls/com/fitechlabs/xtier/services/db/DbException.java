// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.db;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.utils.ArgAssert;
import java.sql.SQLException;

public class DbException extends SQLException
{

    public DbException(String s)
    {
        this(null, s, 2);
    }

    public DbException(SQLException sqlexception, String s)
    {
        this(sqlexception, s, 2);
    }

    public DbException(String s, int i)
    {
        this(null, s, i);
    }

    public DbException(SQLException sqlexception, String s, int i)
    {
        super(s);
        checkType(i);
        type = i;
        if(sqlexception != null)
            setNextException(sqlexception);
    }

    private void checkType(int i)
    {
        switch(i)
        {
        case 1: // '\001'
        case 2: // '\002'
        case 3: // '\003'
        case 4: // '\004'
        case 5: // '\005'
        case 6: // '\006'
        case 7: // '\007'
        case 8: // '\b'
            return;
        }
        ArgAssert.illegalArg(false, "type");
    }

    public int getType()
    {
        return type;
    }

    public String toString()
    {
        return L10n.format("SRVC.DB.ERR15", getMessage(), String.valueOf(type), getNextException());
    }

    public static final int DB_SERVICE_ERR = 1;
    public static final int DB_UNDEFINED_ERR = 2;
    public static final int DB_BAD_SQL = 3;
    public static final int DB_INTEGRITY_ERR = 4;
    public static final int DB_TOO_MANY_ROWS = 5;
    public static final int DB_NOT_AVAIL = 6;
    public static final int DB_NO_DATA_FOUND = 7;
    public static final int DB_OPT_LOCK_FAIL = 8;
    private int type;
}
