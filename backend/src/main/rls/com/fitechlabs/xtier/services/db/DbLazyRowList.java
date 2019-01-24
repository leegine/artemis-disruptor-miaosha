// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.db;


// Referenced classes of package com.fitechlabs.xtier.services.db:
//            DbException, DbRow, DbRowIterator

public interface DbLazyRowList
{

    public abstract int getSize();

    public abstract DbRow get(int i)
        throws DbException;

    public abstract DbRowIterator iterator();

    public abstract DbRowIterator iterator(int i);

    public abstract boolean isEmpty()
        throws DbException;

    public abstract DbRow[] toArray()
        throws DbException;

    public abstract void toArray(DbRow adbrow[])
        throws DbException;

    public abstract int toArray(DbRow adbrow[], int i)
        throws DbException;

    public abstract void setPageSize(int i)
        throws DbException;

    public abstract int getPageSize()
        throws DbException;

    public abstract void close();
}
