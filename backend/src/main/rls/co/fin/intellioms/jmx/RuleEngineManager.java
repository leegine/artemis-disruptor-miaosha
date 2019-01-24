// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   RuleEngineManager.java

package co.fin.intellioms.jmx;

import co.fin.intellioms.enums.CondOrderType;
import co.fin.intellioms.enums.EventType;
import co.fin.intellioms.rulesys.CondOrder;
import co.fin.intellioms.rulesys.impl.OmsRuleEngineImpl;
import co.fin.intellioms.util.InitializationException;
import java.util.Collection;

// Referenced classes of package com.com.fin.intellioms.jmx:
//            RuleEngineManagerMBean

public class RuleEngineManager
    implements RuleEngineManagerMBean
{

    public RuleEngineManager(OmsRuleEngineImpl rulesys)
    {
        impl = rulesys;
    }

    public boolean isStarted()
    {
        return impl.isStarted();
    }

    public void start()
        throws InitializationException
    {
        impl.start();
    }

    public void stop()
    {
        impl.stop();
    }

    public long getActiveOrdersCount()
    {
        return impl.getActiveOrdersCount();
    }

    public CondOrder getCondOrder(long id)
    {
        return impl.getCondOrder(id);
    }

    public Collection activeCondOrders()
    {
        return impl.getAllOrders().values();
    }

    public long getCondOrderOperationsCount()
    {
        return impl.getOperationsCount();
    }

    public long getRegisteredOrdersCount()
    {
        return impl.getRegisteredOrdersCount();
    }

    public EventType[] getRegisteredEventTypes()
    {
        return impl.getRegisteredEventTypes();
    }

    public CondOrderType[] getRegistredCondOrderTypes()
    {
        return impl.getRegisteredCondOrderTypes();
    }

    private OmsRuleEngineImpl impl;
}
