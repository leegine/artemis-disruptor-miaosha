// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   OrderRule.java

package co.fin.intellioms.rulesys;

import co.fin.intellioms.util.Startable;
import co.fin.intellioms.event.Event;
import co.fin.intellioms.event.EventException;

import java.util.List;

// Referenced classes of package com.com.fin.intellioms.rulesys:
//            CondOrderException, CondOrderValidationException, CondOrder, RuleContext

public interface OrderRule
    extends Startable
{

    public abstract void execute(Event event, CondOrder condorder)
        throws EventException;

    public abstract List findMatchingOrders(Event event)
        throws EventException;

    public abstract void matchAndExecute(Event event)
        throws EventException;

    public abstract void registerOrder(CondOrder condorder)
        throws CondOrderException;

    public abstract void load(CondOrder condorder)
        throws CondOrderException;

    public abstract void unload(CondOrder condorder);

    public abstract void validate(CondOrder condorder)
        throws CondOrderValidationException;

    public abstract void modifyOrder(CondOrder condorder, CondOrder condorder1)
        throws CondOrderException;

    public abstract void unregisterOrder(CondOrder condorder)
        throws CondOrderException;

    public abstract void onRegister(RuleContext rulecontext);
}
