// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.db.adapters;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.db.DbSqlParams;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.Utils;
import java.util.List;

public class DbListSqlParams
    implements DbSqlParams
{

    public DbListSqlParams(List list1)
    {
        list = null;
        ArgAssert.nullArg(list1, "list");
        list = list1;
    }

    public void setParams(List list1)
    {
        ArgAssert.nullArg(list1, "list");
        list = list1;
    }

    public List getParams()
    {
        return list;
    }

    public int getCount()
    {
        return list.size();
    }

    public Object getParam(int i)
    {
        return list.get(i);
    }

    public String toString()
    {
        return L10n.format("SRVC.DB.TXT4", Utils.list2Str(list));
    }

    private List list;
}
