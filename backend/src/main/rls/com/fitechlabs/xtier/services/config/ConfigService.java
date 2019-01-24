// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.config;

import com.fitechlabs.xtier.kernel.KernelService;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.config:
//            ConfigException, ConfigChangeListener

public interface ConfigService
    extends KernelService
{

    public abstract void reload()
        throws ConfigException;

    public abstract int getInt(String s, String s1);

    public abstract int[] getIntArr(String s, String s1);

    public abstract char getChar(String s, String s1);

    public abstract char[] getCharArr(String s, String s1);

    public abstract short getShort(String s, String s1);

    public abstract short[] getShortArr(String s, String s1);

    public abstract byte getByte(String s, String s1);

    public abstract byte[] getByteArr(String s, String s1);

    public abstract double getDouble(String s, String s1);

    public abstract double[] getDoubleArr(String s, String s1);

    public abstract float getFloat(String s, String s1);

    public abstract float[] getFloatArr(String s, String s1);

    public abstract long getLong(String s, String s1);

    public abstract long[] getLongArr(String s, String s1);

    public abstract String getString(String s, String s1);

    public abstract String[] getStringArr(String s, String s1);

    public abstract boolean getBoolean(String s, String s1);

    public abstract boolean[] getBooleanArr(String s, String s1);

    public abstract Date getDate(String s, String s1);

    public abstract Date[] getDateArr(String s, String s1);

    public abstract Map getMap(String s, String s1);

    public abstract List getList(String s, String s1);

    public abstract Object getIocObj(String s, String s1)
        throws ConfigException;

    public abstract Object getIocObj(String s, String s1, Object obj)
        throws ConfigException;

    public abstract boolean containsProp(String s, String s1);

    public abstract boolean containsGroup(String s);

    public abstract void addListener(ConfigChangeListener configchangelistener);

    public abstract List getAllListeners();

    public abstract boolean removeListener(ConfigChangeListener configchangelistener);
}
