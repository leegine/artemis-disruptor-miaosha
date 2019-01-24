// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.jms.impl;

import com.fitechlabs.xtier.services.jms.JmsObjectConverter;

class JmsMessageDefaults
{

    JmsMessageDefaults(int i, int j, long l, boolean flag, boolean flag1, JmsObjectConverter jmsobjectconverter)
    {
        if(!$assertionsDisabled && i != 1 && i != 2)
            throw new AssertionError();
        if(!$assertionsDisabled && (j < 0 || j > 9))
            throw new AssertionError();
        if(!$assertionsDisabled && l < 0L)
            throw new AssertionError();
        if(!$assertionsDisabled && jmsobjectconverter == null)
        {
            throw new AssertionError();
        } else
        {
            deliveryMode = i;
            priority = j;
            ttl = l;
            isDisableMstId = flag;
            isDisableMsgTimestamp = flag1;
            converter = jmsobjectconverter;
            return;
        }
    }

    JmsObjectConverter getConverter()
    {
        return converter;
    }

    int getDeliveryMode()
    {
        return deliveryMode;
    }

    boolean isDisableMsgTimestamp()
    {
        return isDisableMsgTimestamp;
    }

    boolean isDisableMstId()
    {
        return isDisableMstId;
    }

    int getPriority()
    {
        return priority;
    }

    long getTtl()
    {
        return ttl;
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

    private int deliveryMode;
    private int priority;
    private long ttl;
    private boolean isDisableMstId;
    private boolean isDisableMsgTimestamp;
    private JmsObjectConverter converter;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(JmsMessageDefaults.class).desiredAssertionStatus();
    }
}
