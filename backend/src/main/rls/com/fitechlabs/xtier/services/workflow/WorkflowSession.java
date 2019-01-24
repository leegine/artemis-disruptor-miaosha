// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.workflow;

import java.util.Map;

public interface WorkflowSession
{

    public abstract boolean getBoolean(String s);

    public abstract byte getInt8(String s);

    public abstract short getInt16(String s);

    public abstract int getInt32(String s);

    public abstract long getInt64(String s);

    public abstract char getChar16(String s);

    public abstract float getFloat32(String s);

    public abstract double getFloat64(String s);

    public abstract Object getObject(String s);

    public abstract void addBoolean(String s, boolean flag);

    public abstract void addInt8(String s, byte byte0);

    public abstract void addInt16(String s, short word0);

    public abstract void addInt32(String s, int i);

    public abstract void addInt64(String s, long l);

    public abstract void addChar16(String s, char c);

    public abstract void addFloat32(String s, float f);

    public abstract void addFloat64(String s, double d);

    public abstract void addObject(String s, Object obj);

    public abstract Map getAll();
}
