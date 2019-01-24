// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   RuleEngineManagerMBean.java

package co.fin.intellioms.jmx;

import co.fin.intellioms.enums.CondOrderType;
import co.fin.intellioms.enums.EventType;
import co.fin.intellioms.rulesys.CondOrder;
import co.fin.intellioms.util.InitializationException;
import java.util.Collection;

public interface RuleEngineManagerMBean
{

    public abstract long getActiveOrdersCount();

    public abstract long getCondOrderOperationsCount();

    public abstract long getRegisteredOrdersCount();

    public abstract EventType[] getRegisteredEventTypes();

    public abstract CondOrderType[] getRegistredCondOrderTypes();

    public abstract CondOrder getCondOrder(long l);

    public abstract Collection activeCondOrders();

    public abstract boolean isStarted();

    public abstract void start()
        throws InitializationException;

    public abstract void stop();
}
