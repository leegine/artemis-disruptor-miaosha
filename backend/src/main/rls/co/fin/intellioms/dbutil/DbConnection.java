// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   DbConnection.java

package co.fin.intellioms.dbutil;

import java.sql.*;
import java.util.Map;

public interface DbConnection
    extends Connection
{

    public abstract void close()
        throws SQLException;

    public abstract Connection getRealConnection();

    public abstract int getHoldability()
        throws SQLException;

    public abstract int getTransactionIsolation()
        throws SQLException;

    public abstract void clearWarnings()
        throws SQLException;

    public abstract void commit()
        throws SQLException;

    public abstract void rollback()
        throws SQLException;

    public abstract boolean getAutoCommit()
        throws SQLException;

    public abstract boolean isClosed()
        throws SQLException;

    public abstract boolean isReadOnly()
        throws SQLException;

    public abstract void setHoldability(int i)
        throws SQLException;

    public abstract void setTransactionIsolation(int i)
        throws SQLException;

    public abstract void setAutoCommit(boolean flag)
        throws SQLException;

    public abstract void setReadOnly(boolean flag)
        throws SQLException;

    public abstract String getCatalog()
        throws SQLException;

    public abstract void setCatalog(String s)
        throws SQLException;

    public abstract DatabaseMetaData getMetaData()
        throws SQLException;

    public abstract SQLWarning getWarnings()
        throws SQLException;

    public abstract Savepoint setSavepoint()
        throws SQLException;

    public abstract void releaseSavepoint(Savepoint savepoint)
        throws SQLException;

    public abstract void rollback(Savepoint savepoint)
        throws SQLException;

    public abstract Statement createStatement()
        throws SQLException;

    public abstract Statement createStatement(int i, int j)
        throws SQLException;

    public abstract Statement createStatement(int i, int j, int k)
        throws SQLException;

    public abstract Map getTypeMap()
        throws SQLException;

    public abstract void setTypeMap(Map map)
        throws SQLException;

    public abstract String nativeSQL(String s)
        throws SQLException;

    public abstract CallableStatement prepareCall(String s)
        throws SQLException;

    public abstract CallableStatement prepareCall(String s, int i, int j)
        throws SQLException;

    public abstract CallableStatement prepareCall(String s, int i, int j, int k)
        throws SQLException;

    public abstract PreparedStatement prepareStatement(String s)
        throws SQLException;

    public abstract PreparedStatement prepareStatement(String s, int i)
        throws SQLException;

    public abstract PreparedStatement prepareStatement(String s, int i, int j)
        throws SQLException;

    public abstract PreparedStatement prepareStatement(String s, int i, int j, int k)
        throws SQLException;

    public abstract PreparedStatement prepareStatement(String s, int ai[])
        throws SQLException;

    public abstract Savepoint setSavepoint(String s)
        throws SQLException;

    public abstract PreparedStatement prepareStatement(String s, String as[])
        throws SQLException;
}
