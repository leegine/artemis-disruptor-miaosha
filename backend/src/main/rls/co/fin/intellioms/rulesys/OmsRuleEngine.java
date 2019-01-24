// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   OmsRuleEngine.java

package co.fin.intellioms.rulesys;

import co.fin.intellioms.account.AccountsInfo;
import co.fin.intellioms.event.Event;
import co.fin.intellioms.event.EventException;
import java.util.List;

// Referenced classes of package com.com.fin.intellioms.rulesys:
//            RuleEngineException, CondOrderOperationException, OrderRule, RuleModule,
//            RuleContext, CondOrder, CondOrderOperation

public interface OmsRuleEngine
{

    public abstract RuleContext createRuleContext(OrderRule orderrule, RuleModule rulemodule);

    public abstract boolean registerRuleModule(RuleModule rulemodule);

    public abstract AccountsInfo getAccountsInfo();

    public abstract void registerAccounts(AccountsInfo accountsinfo)
        throws RuleEngineException;

    public abstract boolean unregisterRuleModule(RuleModule rulemodule);

    public abstract List getApplicableRuleModuleForEvent(Event event);

    public abstract void registerCondOrder(CondOrder condorder)
        throws CondOrderOperationException;

    public abstract void unregisterCondOrder(long l)
        throws CondOrderOperationException;

    public abstract void operateCondOrder(CondOrderOperation condorderoperation)
        throws CondOrderOperationException;

    public abstract CondOrder getCondOrder(long l);

    public abstract void processEvent(Event event)
        throws EventException;
}
