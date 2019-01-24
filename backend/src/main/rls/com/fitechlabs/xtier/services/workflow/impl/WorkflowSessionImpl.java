// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.workflow.impl;

import com.fitechlabs.xtier.services.workflow.WorkflowSession;
import java.util.HashMap;
import java.util.Map;

class WorkflowSessionImpl
    implements WorkflowSession
{

    WorkflowSessionImpl(Map map)
    {
        values = ((Map) (map != null ? map : ((Map) (new HashMap()))));
    }

    public byte getInt8(String s)
    {
        return ((Byte)getValue(s)).byteValue();
    }

    public short getInt16(String s)
    {
        return ((Short)getValue(s)).shortValue();
    }

    public int getInt32(String s)
    {
        return ((Integer)getValue(s)).intValue();
    }

    public long getInt64(String s)
    {
        return ((Long)getValue(s)).longValue();
    }

    public char getChar16(String s)
    {
        return ((Character)getValue(s)).charValue();
    }

    public float getFloat32(String s)
    {
        return ((Float)getValue(s)).floatValue();
    }

    public double getFloat64(String s)
    {
        return ((Double)getValue(s)).doubleValue();
    }

    public boolean getBoolean(String s)
    {
        return ((Boolean)getValue(s)).booleanValue();
    }

    public Object getObject(String s)
    {
        return getValue(s);
    }

    public void addInt8(String s, byte byte0)
    {
        addValue(s, new Byte(byte0));
    }

    public void addInt16(String s, short word0)
    {
        addValue(s, new Short(word0));
    }

    public void addInt32(String s, int i)
    {
        addValue(s, new Integer(i));
    }

    public void addInt64(String s, long l)
    {
        addValue(s, new Long(l));
    }

    public void addChar16(String s, char c)
    {
        addValue(s, new Character(c));
    }

    public void addFloat32(String s, float f)
    {
        addValue(s, new Float(f));
    }

    public void addFloat64(String s, double d)
    {
        addValue(s, new Double(d));
    }

    public void addObject(String s, Object obj)
    {
        addValue(s, obj);
    }

    public void addBoolean(String s, boolean flag)
    {
        addValue(s, !flag ? ((Object) (Boolean.FALSE)) : ((Object) (Boolean.TRUE)));
    }

    public Map getAll()
    {
        return values;
    }

    void addValue(String s, Object obj)
    {
        values.put(s, obj);
    }

    Object getValue(String s)
    {
        return values.get(s);
    }

    private Map values;
}
