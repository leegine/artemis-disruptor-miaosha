// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.db.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.db.DbRow;
import com.fitechlabs.xtier.utils.*;
import java.net.URL;
import java.sql.*;
import java.util.*;

class DbRowImpl
    implements DbRow
{
    private class Value
    {

        Object getObject()
        {
            return obj;
        }

        int getType()
        {
            return type;
        }

        public String toString()
        {
            return L10n.format("SRVC.DB.TXT6", obj, String.valueOf(type));
        }

        private Object obj;
        private int type;

        Value(Object obj1, int i)
        {
            super();
            obj = obj1;
            type = i;
        }
    }


    DbRowImpl(int i, int j)
    {
        namedValues = new HashMap();
        if(!$assertionsDisabled && (i <= 0 || j <= 0))
        {
            throw new AssertionError();
        } else
        {
            rowNum = i;
            colNames = new String[j];
            return;
        }
    }

    public int getRowNum()
    {
        return rowNum;
    }

    public int getColumnCount()
    {
        return colNames.length;
    }

    public Object getObject(int i)
    {
        Value value = (Value)namedValues.get(colNames[i - 1]);
        if(!$assertionsDisabled && value == null)
            throw new AssertionError();
        else
            return value.getObject();
    }

    public Object getObject(String s)
    {
        ArgAssert.nullArg(s, "col");
        s = s.toUpperCase();
        if(!namedValues.containsKey(s))
            throw new IllegalArgumentException(L10n.format("SRVC.DB.ERR25", s));
        Value value = (Value)namedValues.get(s);
        if(!$assertionsDisabled && value == null)
            throw new AssertionError();
        else
            return value.getObject();
    }

    public int getInt(int i)
    {
        return getInt(getObject0(i, 4, false));
    }

    public byte getByte(int i)
    {
        return getByte(getObject0(i, -6, false));
    }

    public short getShort(int i)
    {
        return getShort(getObject0(i, 5, false));
    }

    public float getFloat(int i)
    {
        return getFloat(getObject0(i, 7, false));
    }

    public double getDouble(int i)
    {
        return getDouble(getObject0(i, new int[] {
            6, 8
        }, false));
    }

    public long getLong(int i)
    {
        return getLong(getObject0(i, -5, false));
    }

    public Date getDate(int i)
    {
        Object obj = getObject0(i, 91, true);
        try
        {
            return (Date)Utils.convertDate(java.sql.Date.class, obj);
        }
        catch(UtilsException utilsexception)
        {
            throw new IllegalStateException(L10n.format("SRVC.DB.ERR26", "java.sql.Date", obj));
        }
    }

    public Date getDate(int i, Calendar calendar)
    {
        ArgAssert.nullArg(calendar, "cal");
        Object obj = getObject0(i, 91, true);
        try
        {
            return shiftDate((Date)Utils.convertDate(java.sql.Date.class, obj), calendar);
        }
        catch(UtilsException utilsexception)
        {
            throw new IllegalStateException(L10n.format("SRVC.DB.ERR26", "java.sql.Date", obj));
        }
    }

    public Time getTime(int i)
    {
        Object obj = getObject0(i, 92, true);
        try
        {
            return (Time)Utils.convertDate(Time.class, obj);
        }
        catch(UtilsException utilsexception)
        {
            throw new IllegalStateException(L10n.format("SRVC.DB.ERR26", "Time", obj));
        }
    }

    public Time getTime(int i, Calendar calendar)
    {
        ArgAssert.nullArg(calendar, "cal");
        Object obj = getObject0(i, 92, true);
        try
        {
            return shiftTime((Time)Utils.convertDate(Time.class, obj), calendar);
        }
        catch(UtilsException utilsexception)
        {
            throw new IllegalStateException(L10n.format("SRVC.DB.ERR26", "Time", obj));
        }
    }

    public Timestamp getTimestamp(int i)
    {
        Object obj = getObject0(i, 93, true);
        try
        {
            return (Timestamp)Utils.convertDate(Timestamp.class, obj);
        }
        catch(UtilsException utilsexception)
        {
            throw new IllegalStateException(L10n.format("SRVC.DB.ERR26", "Timestamp", obj));
        }
    }

    public Timestamp getTimestamp(int i, Calendar calendar)
    {
        ArgAssert.nullArg(calendar, "cal");
        Object obj = getObject0(i, 93, true);
        try
        {
            return shiftTimestamp((Timestamp)Utils.convertDate(Timestamp.class, obj), calendar);
        }
        catch(UtilsException utilsexception)
        {
            throw new IllegalStateException(L10n.format("SRVC.DB.ERR26", "Timestamp", obj));
        }
    }

    public URL getUrl(int i)
    {
        return (URL)getObject0(i, 70, true);
    }

    public String getString(int i)
    {
        return (String)getObject0(i, new int[] {
            1, 12, -1
        }, true);
    }

    public Ref getRef(int i)
    {
        return (Ref)getObject0(i, 2006, true);
    }

    public boolean getBool(int i)
    {
        return ((Boolean)getObject0(i, new int[] {
            16, -7
        }, false)).booleanValue();
    }

    public Clob getClob(int i)
    {
        return (Clob)getObject0(i, 2005, true);
    }

    public Blob getBlob(int i)
    {
        return (Blob)getObject0(i, 2004, true);
    }

    public byte[] getBytes(int i)
    {
        return (byte[])getObject0(i, new int[] {
            -2, -4, -3
        }, true);
    }

    public int getInt(String s)
    {
        ArgAssert.nullArg(s, "col");
        return getInt(getObject0(s, 4, false));
    }

    public byte getByte(String s)
    {
        ArgAssert.nullArg(s, "col");
        return getByte(getObject0(s, -6, false));
    }

    public short getShort(String s)
    {
        ArgAssert.nullArg(s, "col");
        return getShort(getObject0(s, 5, false));
    }

    public float getFloat(String s)
    {
        ArgAssert.nullArg(s, "col");
        return getFloat(getObject0(s, 7, false));
    }

    public double getDouble(String s)
    {
        ArgAssert.nullArg(s, "col");
        return getDouble(getObject0(s, new int[] {
            6, 8
        }, false));
    }

    public long getLong(String s)
    {
        ArgAssert.nullArg(s, "col");
        return getLong(getObject0(s, -5, false));
    }

    public Date getDate(String s)
    {
        ArgAssert.nullArg(s, "col");
        Object obj = getObject0(s, 91, true);
        try
        {
            return (Date)Utils.convertDate(java.sql.Date.class, obj);
        }
        catch(UtilsException utilsexception)
        {
            throw new IllegalStateException(L10n.format("SRVC.DB.ERR26", "java.sql.Date", obj));
        }
    }

    public Date getDate(String s, Calendar calendar)
    {
        ArgAssert.nullArg(s, "col");
        ArgAssert.nullArg(calendar, "cal");
        Object obj = getObject0(s, 91, true);
        try
        {
            return shiftDate((Date)Utils.convertDate(java.sql.Date.class, obj), calendar);
        }
        catch(UtilsException utilsexception)
        {
            throw new IllegalStateException(L10n.format("SRVC.DB.ERR26", "java.sql.Date", obj));
        }
    }

    public Time getTime(String s)
    {
        ArgAssert.nullArg(s, "col");
        Object obj = getObject0(s, 92, true);
        try
        {
            return (Time)Utils.convertDate(Time.class, obj);
        }
        catch(UtilsException utilsexception)
        {
            throw new IllegalStateException(L10n.format("SRVC.DB.ERR26", "Time", obj));
        }
    }

    public Time getTime(String s, Calendar calendar)
    {
        ArgAssert.nullArg(calendar, "cal");
        ArgAssert.nullArg(s, "col");
        Object obj = getObject0(s, 92, true);
        try
        {
            return shiftTime((Time)Utils.convertDate(Time.class, obj), calendar);
        }
        catch(UtilsException utilsexception)
        {
            throw new IllegalStateException(L10n.format("SRVC.DB.ERR26", "Time", obj));
        }
    }

    public Timestamp getTimestamp(String s)
    {
        ArgAssert.nullArg(s, "col");
        Object obj = getObject0(s, 93, true);
        try
        {
            return (Timestamp)Utils.convertDate(Timestamp.class, obj);
        }
        catch(UtilsException utilsexception)
        {
            throw new IllegalStateException(L10n.format("SRVC.DB.ERR26", "Timestamp", obj));
        }
    }

    public Timestamp getTimestamp(String s, Calendar calendar)
    {
        ArgAssert.nullArg(s, "col");
        ArgAssert.nullArg(calendar, "cal");
        Object obj = getObject0(s, 93, true);
        try
        {
            return shiftTimestamp((Timestamp)Utils.convertDate(Timestamp.class, obj), calendar);
        }
        catch(UtilsException utilsexception)
        {
            throw new IllegalStateException(L10n.format("SRVC.DB.ERR26", "Timestamp", obj));
        }
    }

    public URL getUrl(String s)
    {
        ArgAssert.nullArg(s, "col");
        return (URL)getObject0(s, 70, true);
    }

    public String getString(String s)
    {
        ArgAssert.nullArg(s, "col");
        return (String)getObject0(s, new int[] {
            1, 12, -1
        }, true);
    }

    public Ref getRef(String s)
    {
        ArgAssert.nullArg(s, "col");
        return (Ref)getObject0(s, 2006, true);
    }

    public boolean getBool(String s)
    {
        ArgAssert.nullArg(s, "col");
        return ((Boolean)getObject0(s, new int[] {
            16, -7
        }, false)).booleanValue();
    }

    public Clob getClob(String s)
    {
        ArgAssert.nullArg(s, "col");
        return (Clob)getObject0(s, 2005, true);
    }

    public Blob getBlob(String s)
    {
        ArgAssert.nullArg(s, "col");
        return (Blob)getObject0(s, 2004, true);
    }

    public byte[] getBytes(String s)
    {
        ArgAssert.nullArg(s, "col");
        return (byte[])getObject0(s, new int[] {
            -2, -4, -3
        }, true);
    }

    public boolean isNull(int i)
    {
        Value value = (Value)namedValues.get(colNames[i - 1]);
        if(!$assertionsDisabled && value == null)
            throw new AssertionError();
        else
            return value.getObject() == null;
    }

    public boolean isNull(String s)
    {
        ArgAssert.nullArg(s, "col");
        s = s.toUpperCase();
        if(!namedValues.containsKey(s))
            throw new IllegalArgumentException(L10n.format("SRVC.DB.ERR25", s));
        Value value = (Value)namedValues.get(s);
        if(!$assertionsDisabled && value == null)
            throw new AssertionError();
        else
            return value.getObject() == null;
    }

    void addObject(int i, String s, int j, Object obj)
    {
        if(!$assertionsDisabled && (s == null || i <= 0))
        {
            throw new AssertionError();
        } else
        {
            s = s.toUpperCase();
            namedValues.put(s, new Value(obj, j));
            colNames[i - 1] = s;
            return;
        }
    }

    private Date shiftDate(Date date, Calendar calendar)
    {
        if(!$assertionsDisabled && calendar == null)
            throw new AssertionError();
        if(date == null)
        {
            return null;
        } else
        {
            long l = date.getTime();
            return new Date((long)calendar.getTimeZone().getOffset(l) + l);
        }
    }

    private Time shiftTime(Time time, Calendar calendar)
    {
        if(!$assertionsDisabled && calendar == null)
            throw new AssertionError();
        if(time == null)
        {
            return null;
        } else
        {
            long l = time.getTime();
            return new Time((long)calendar.getTimeZone().getOffset(l) + l);
        }
    }

    private Timestamp shiftTimestamp(Timestamp timestamp, Calendar calendar)
    {
        if(!$assertionsDisabled && calendar == null)
            throw new AssertionError();
        if(timestamp == null)
        {
            return null;
        } else
        {
            long l = timestamp.getTime();
            return new Timestamp((long)calendar.getTimeZone().getOffset(l) + l);
        }
    }

    public int getInt(int i, int j)
    {
        Object obj = getObject0(i, 4, true);
        if(obj == null)
            return j;
        else
            return getInt(obj);
    }

    public byte getByte(int i, byte byte0)
    {
        Object obj = getObject0(i, -6, true);
        if(obj == null)
            return byte0;
        else
            return getByte(obj);
    }

    public short getShort(int i, short word0)
    {
        Object obj = getObject0(i, 5, true);
        if(obj == null)
            return word0;
        else
            return getShort(obj);
    }

    public float getFloat(int i, float f)
    {
        Object obj = getObject0(i, 7, true);
        if(obj == null)
            return f;
        else
            return getFloat(obj);
    }

    public double getDouble(int i, double d)
    {
        Object obj = getObject0(i, new int[] {
            6, 8
        }, true);
        if(obj == null)
            return d;
        else
            return getDouble(obj);
    }

    public long getLong(int i, long l)
    {
        Object obj = getObject0(i, -5, true);
        if(obj == null)
            return l;
        else
            return getLong(obj);
    }

    public boolean getBool(int i, boolean flag)
    {
        Object obj = getObject0(i, new int[] {
            16, -7
        }, true);
        if(obj == null)
            return flag;
        else
            return ((Boolean)obj).booleanValue();
    }

    public int getInt(String s, int i)
    {
        ArgAssert.nullArg(s, "col");
        Object obj = getObject0(s, 4, true);
        if(obj == null)
            return i;
        else
            return getInt(obj);
    }

    public byte getByte(String s, byte byte0)
    {
        Object obj = getObject0(s, -6, true);
        if(obj == null)
            return byte0;
        else
            return getByte(obj);
    }

    public short getShort(String s, short word0)
    {
        Object obj = getObject0(s, 5, true);
        if(obj == null)
            return word0;
        else
            return getShort(obj);
    }

    public float getFloat(String s, float f)
    {
        Object obj = getObject0(s, 7, true);
        if(obj == null)
            return f;
        else
            return getFloat(obj);
    }

    public double getDouble(String s, double d)
    {
        Object obj = getObject0(s, new int[] {
            6, 8
        }, true);
        if(obj == null)
            return d;
        else
            return getDouble(obj);
    }

    public long getLong(String s, long l)
    {
        Object obj = getObject0(s, -5, true);
        if(obj == null)
            return l;
        else
            return getLong(obj);
    }

    public boolean getBool(String s, boolean flag)
    {
        Object obj = getObject0(s, new int[] {
            16, -7
        }, true);
        if(obj == null)
            return flag;
        else
            return ((Boolean)obj).booleanValue();
    }

    private double getDouble(Object obj)
    {
        if(!$assertionsDisabled && obj == null)
            throw new AssertionError();
        try
        {
            return ((Double)Utils.convertNumeric(Double.class, obj)).doubleValue();
        }
        catch(UtilsException utilsexception)
        {
            throw new IllegalStateException(L10n.format("SRVC.DB.ERR26", "double", obj));
        }
    }

    private float getFloat(Object obj)
    {
        if(!$assertionsDisabled && obj == null)
            throw new AssertionError();
        try
        {
            return ((Float)Utils.convertNumeric(Float.class, obj)).floatValue();
        }
        catch(UtilsException utilsexception)
        {
            throw new IllegalStateException(L10n.format("SRVC.DB.ERR26", "float", obj));
        }
    }

    private long getLong(Object obj)
    {
        if(!$assertionsDisabled && obj == null)
            throw new AssertionError();
        try
        {
            return ((Long)Utils.convertNumeric(Long.class, obj)).longValue();
        }
        catch(UtilsException utilsexception)
        {
            throw new IllegalStateException(L10n.format("SRVC.DB.ERR26", "long", obj));
        }
    }

    private int getInt(Object obj)
    {
        if(!$assertionsDisabled && obj == null)
            throw new AssertionError();
        try
        {
            return ((Integer)Utils.convertNumeric(Integer.class, obj)).intValue();
        }
        catch(UtilsException utilsexception)
        {
            throw new IllegalStateException(L10n.format("SRVC.DB.ERR26", "int", obj));
        }
    }

    private short getShort(Object obj)
    {
        if(!$assertionsDisabled && obj == null)
            throw new AssertionError();
        try
        {
            return ((Short)Utils.convertNumeric(Short.class, obj)).shortValue();
        }
        catch(UtilsException utilsexception)
        {
            throw new IllegalStateException(L10n.format("SRVC.DB.ERR26", "short", obj));
        }
    }

    private byte getByte(Object obj)
    {
        if(!$assertionsDisabled && obj == null)
            throw new AssertionError();
        try
        {
            return ((Byte)Utils.convertNumeric(Byte.class, obj)).byteValue();
        }
        catch(UtilsException utilsexception)
        {
            throw new IllegalStateException(L10n.format("SRVC.DB.ERR26", "byte", obj));
        }
    }

    private boolean validateType(int i, int j)
    {
        if(translateType(i) == translateType(j))
            return true;
        if(isNumeric(i) && isNumeric(j))
            return true;
        return isDate(i) && isDate(j);
    }

    private boolean validateType(int ai[], int i)
    {
        for(int j = 0; j < ai.length; j++)
            if(validateType(ai[j], i))
                return true;

        return false;
    }

    private boolean isNumeric(int i)
    {
        return -5 == i || 3 == i || 8 == i || 6 == i || 4 == i || 2 == i || 7 == i || 5 == i || -6 == i;
    }

    private boolean isDate(int i)
    {
        return 91 == i || 92 == i || 93 == i;
    }

    private int translateType(int i)
    {
        if(-6 == i || 5 == i || 4 == i)
            return 4;
        if(1 == i || 12 == i)
            return 12;
        if(3 == i || 8 == i || 6 == i || 2 == i || 7 == i)
            return 2;
        else
            return i;
    }

    private Object getObject0(String s, int i, boolean flag)
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        Value value = (Value)namedValues.get(s.toUpperCase());
        if(value == null)
            throw new IllegalArgumentException(L10n.format("SRVC.DB.ERR25", s));
        if(!validateType(i, value.getType()))
            throw new IllegalStateException(L10n.format("SRVC.DB.ERR27", s, String.valueOf(value.getType())));
        Object obj = value.getObject();
        if(!flag && obj == null)
            throw new IllegalStateException(L10n.format("SRVC.DB.ERR28", s));
        else
            return obj;
    }

    private Object getObject0(String s, int ai[], boolean flag)
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        Value value = (Value)namedValues.get(s.toUpperCase());
        if(value == null)
            throw new IllegalArgumentException(L10n.format("SRVC.DB.ERR25", s));
        if(!validateType(ai, value.getType()))
            throw new IllegalStateException(L10n.format("SRVC.DB.ERR27", s, String.valueOf(value.getType())));
        Object obj = value.getObject();
        if(!flag && obj == null)
            throw new IllegalStateException(L10n.format("SRVC.DB.ERR28", s));
        else
            return obj;
    }

    private Object getObject0(int i, int j, boolean flag)
    {
        if(!$assertionsDisabled && i <= 0)
            throw new AssertionError();
        else
            return getObject0(colNames[i - 1], j, flag);
    }

    private Object getObject0(int i, int ai[], boolean flag)
    {
        if(!$assertionsDisabled && i <= 0)
            throw new AssertionError();
        else
            return getObject0(colNames[i - 1], ai, flag);
    }

    public String toString()
    {
        return L10n.format("SRVC.DB.TXT5", String.valueOf(rowNum), namedValues);
    }

    private int rowNum;
    private Map namedValues;
    private String colNames[];
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(DbRowImpl.class).desiredAssertionStatus();
    }
}
