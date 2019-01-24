// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.db;

import java.sql.SQLException;
import java.sql.Statement;

public interface DbStatementHandler
{

    public abstract void execute(Statement statement)
        throws SQLException;
}
