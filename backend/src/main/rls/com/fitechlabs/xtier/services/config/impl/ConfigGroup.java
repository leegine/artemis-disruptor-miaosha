// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.config.impl;

import com.fitechlabs.xtier.kernel.ioc.IocDescriptor;
import com.fitechlabs.xtier.kernel.ioc.IocDescriptorException;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.config.ConfigException;
import com.fitechlabs.xtier.services.config.ConfigMissingPropException;
import java.util.*;

class ConfigGroup
{

    ConfigGroup(String s)
    {
        props = new HashMap();
        lists = new HashMap();
        name = s;
    }

    boolean containsProp(String s)
    {
        return props.containsKey(s);
    }

    private Object getProp(String s)
    {
        Object obj = props.get(s);
        if(obj == null)
            throw new ConfigMissingPropException(L10n.format("SRVC.CNFG.ERR25", name, s));
        else
            return obj;
    }

    void addProp(String s, Object obj)
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        if(!$assertionsDisabled && obj == null)
        {
            throw new AssertionError();
        } else
        {
            props.put(s, obj);
            return;
        }
    }

    void addList(String s, List list)
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        if(!$assertionsDisabled && list == null)
        {
            throw new AssertionError();
        } else
        {
            lists.put(s, list);
            return;
        }
    }

    int getInt(String s)
    {
        return ((Integer)getProp(s)).intValue();
    }

    int[] getIntArr(String s)
    {
        return (int[])getProp(s);
    }

    char getChar(String s)
    {
        return ((Character)getProp(s)).charValue();
    }

    char[] getCharArr(String s)
    {
        return (char[])getProp(s);
    }

    short getShort(String s)
    {
        return ((Short)getProp(s)).shortValue();
    }

    short[] getShortArr(String s)
    {
        return (short[])getProp(s);
    }

    byte getByte(String s)
    {
        return ((Byte)getProp(s)).byteValue();
    }

    byte[] getByteArr(String s)
    {
        return (byte[])getProp(s);
    }

    double getDouble(String s)
    {
        return ((Double)getProp(s)).doubleValue();
    }

    double[] getDoubleArr(String s)
    {
        return (double[])getProp(s);
    }

    float getFloat(String s)
    {
        return ((Float)getProp(s)).floatValue();
    }

    float[] getFloatArr(String s)
    {
        return (float[])getProp(s);
    }

    long getLong(String s)
    {
        return ((Long)getProp(s)).longValue();
    }

    long[] getLongArr(String s)
    {
        return (long[])getProp(s);
    }

    String getString(String s)
    {
        return (String)getProp(s);
    }

    String[] getStringArr(String s)
    {
        return (String[])getProp(s);
    }

    boolean getBoolean(String s)
    {
        return ((Boolean)getProp(s)).booleanValue();
    }

    boolean[] getBooleanArr(String s)
    {
        return (boolean[])getProp(s);
    }

    Date getDate(String s)
    {
        return (Date)getProp(s);
    }

    Date[] getDateArr(String s)
    {
        return (Date[])getProp(s);
    }

    Map getMap(String s)
    {
        return (Map)getProp(s);
    }

    List getList(String s)
    {
        List list = (List)lists.get(s);
        if(list == null)
            throw new ConfigMissingPropException(L10n.format("SRVC.CNFG.ERR25", name, s));
        else
            return list;
    }

    Object getIocObj(String s)
        throws ConfigException
    {
        IocDescriptor iocdescriptor = (IocDescriptor)getProp(s);
        try
        {
            return iocdescriptor.createNewObj();
        }
        catch(IocDescriptorException iocdescriptorexception)
        {
            throw new ConfigException(L10n.format("SRVC.CNFG.ERR30", s), iocdescriptorexception);
        }
    }

    Object getIocObj(String s, Object obj)
        throws ConfigException
    {
        IocDescriptor iocdescriptor = (IocDescriptor)getProp(s);
        try
        {
            return iocdescriptor.createNewObj(obj);
        }
        catch(IocDescriptorException iocdescriptorexception)
        {
            throw new ConfigException(L10n.format("SRVC.CNFG.ERR30", s), iocdescriptorexception);
        }
    }

    public String toString()
    {
        return L10n.format("SRVC.CNFG.TXT9", name);
    }

    static Class _mthclass$(String s)
    {
        try
        {
            return Class.forName(s);
        }
        catch(ClassNotFoundException classnotfoundexception)
        {
            throw (new NoClassDefFoundError()).initCause(classnotfoundexception);
        }
    }

    private HashMap props;
    private HashMap lists;
    private String name;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(ConfigGroup.class).desiredAssertionStatus();
    }
}
