// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.db;

import java.net.URL;
import java.sql.*;
import java.util.Calendar;

public interface DbRow
{

    public abstract int getRowNum();

    public abstract int getColumnCount();

    public abstract Object getObject(int i);

    public abstract Object getObject(String s);

    public abstract int getInt(int i);

    public abstract int getInt(int i, int j);

    public abstract byte getByte(int i);

    public abstract byte getByte(int i, byte byte0);

    public abstract short getShort(int i);

    public abstract short getShort(int i, short word0);

    public abstract float getFloat(int i);

    public abstract float getFloat(int i, float f);

    public abstract double getDouble(int i);

    public abstract double getDouble(int i, double d);

    public abstract long getLong(int i);

    public abstract long getLong(int i, long l);

    public abstract Date getDate(int i);

    public abstract Date getDate(int i, Calendar calendar);

    public abstract Time getTime(int i);

    public abstract Time getTime(int i, Calendar calendar);

    public abstract Timestamp getTimestamp(int i);

    public abstract Timestamp getTimestamp(int i, Calendar calendar);

    public abstract URL getUrl(int i);

    public abstract String getString(int i);

    public abstract Ref getRef(int i);

    public abstract boolean getBool(int i);

    public abstract boolean getBool(int i, boolean flag);

    public abstract Clob getClob(int i);

    public abstract Blob getBlob(int i);

    public abstract byte[] getBytes(int i);

    public abstract int getInt(String s);

    public abstract int getInt(String s, int i);

    public abstract byte getByte(String s);

    public abstract byte getByte(String s, byte byte0);

    public abstract short getShort(String s);

    public abstract short getShort(String s, short word0);

    public abstract float getFloat(String s);

    public abstract float getFloat(String s, float f);

    public abstract double getDouble(String s);

    public abstract double getDouble(String s, double d);

    public abstract long getLong(String s);

    public abstract long getLong(String s, long l);

    public abstract Date getDate(String s);

    public abstract Date getDate(String s, Calendar calendar);

    public abstract Time getTime(String s);

    public abstract Time getTime(String s, Calendar calendar);

    public abstract Timestamp getTimestamp(String s);

    public abstract Timestamp getTimestamp(String s, Calendar calendar);

    public abstract URL getUrl(String s);

    public abstract String getString(String s);

    public abstract Ref getRef(String s);

    public abstract boolean getBool(String s);

    public abstract boolean getBool(String s, boolean flag);

    public abstract Clob getClob(String s);

    public abstract Blob getBlob(String s);

    public abstract byte[] getBytes(String s);

    public abstract boolean isNull(int i);

    public abstract boolean isNull(String s);
}
