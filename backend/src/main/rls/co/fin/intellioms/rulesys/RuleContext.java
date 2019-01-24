// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   RuleContext.java

package co.fin.intellioms.rulesys;


// Referenced classes of package com.com.fin.intellioms.rulesys:
//            CondOrderException, CondOrderExpiredException, RuleModule, OmsRuleEngine,
//            CondOrder

public interface RuleContext
{

    public abstract RuleModule getRuleModule();

    public abstract OmsRuleEngine getRuleEngine();

    public abstract void activateOrder(CondOrder condorder)
        throws CondOrderException, CondOrderExpiredException;

    public abstract Object getMutex(CondOrder condorder);

    public abstract void markCompleted(CondOrder condorder)
        throws CondOrderException;

    public abstract void cancel(CondOrder condorder)
        throws CondOrderException;
}
