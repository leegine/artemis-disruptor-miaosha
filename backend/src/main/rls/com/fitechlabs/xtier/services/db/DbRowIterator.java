// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.db;


// Referenced classes of package com.fitechlabs.xtier.services.db:
//            DbException, DbRow

public interface DbRowIterator
{

    public abstract boolean hasNext()
        throws DbException;

    public abstract DbRow next()
        throws DbException;
}
