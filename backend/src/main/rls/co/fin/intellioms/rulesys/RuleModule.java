// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   RuleModule.java

package co.fin.intellioms.rulesys;

import co.fin.intellioms.util.Startable;
import co.fin.intellioms.enums.CondOrderType;
import co.fin.intellioms.enums.EventType;
import co.fin.intellioms.event.Event;
import co.fin.intellioms.event.EventException;

import java.util.List;

// Referenced classes of package com.com.fin.intellioms.rulesys:
//            CondOrderException, CondOrderValidationException, OrderRule, CondOrder,
//            OmsRuleEngine

public interface RuleModule
    extends Startable
{

    public abstract String name();

    public abstract CondOrderType[] getApplicableCondOrderTypes();

    public abstract EventType[] getApplicableEventTypes();

    public abstract boolean applicableEvent(Event event);

    public abstract List getAllRules();

    public abstract List getApplicableRules(Event event);

    public abstract List getMatchingOrders(Event event)
        throws EventException;

    public abstract void matchAndExecute(Event event)
        throws EventException;

    public abstract boolean registerRule(OrderRule orderrule);

    public abstract void registerOrder(CondOrder condorder)
        throws CondOrderException;

    public abstract void load(CondOrder condorder)
        throws CondOrderException;

    public abstract void unload(CondOrder condorder);

    public abstract void validate(CondOrder condorder)
        throws CondOrderValidationException;

    public abstract void unregisterOrder(CondOrder condorder)
        throws CondOrderException;

    public abstract void onRegister(OmsRuleEngine omsruleengine);

    public abstract void onUnregister(OmsRuleEngine omsruleengine);

    public abstract OmsRuleEngine getRuleEngine();

    public abstract void modifyCondOrder(CondOrder condorder, CondOrder condorder1)
        throws CondOrderException;
}
