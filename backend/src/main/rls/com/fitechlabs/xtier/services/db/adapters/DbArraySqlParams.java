// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.db.adapters;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.db.DbSqlParams;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.Utils;

public class DbArraySqlParams
    implements DbSqlParams
{

    public DbArraySqlParams(Object aobj[])
    {
        arr = null;
        ArgAssert.nullArg(((Object) (aobj)), "arr");
        arr = aobj;
    }

    public void setParams(Object aobj[])
    {
        ArgAssert.nullArg(((Object) (aobj)), "arr");
        arr = aobj;
    }

    public Object[] getParams()
    {
        return arr;
    }

    public int getCount()
    {
        return arr.length;
    }

    public Object getParam(int i)
    {
        return arr[i];
    }

    public String toString()
    {
        return L10n.format("SRVC.DB.TXT3", Utils.arr2Str(((Object) (arr))));
    }

    private Object arr[];
}
