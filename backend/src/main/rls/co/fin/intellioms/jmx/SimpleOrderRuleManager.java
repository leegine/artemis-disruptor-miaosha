// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   SimpleOrderRuleManager.java

package co.fin.intellioms.jmx;

import co.fin.intellioms.simple.SimpleOrderRule;
import java.util.Collection;

// Referenced classes of package com.com.fin.intellioms.jmx:
//            SimpleOrderRuleManagerMBean

public class SimpleOrderRuleManager
    implements SimpleOrderRuleManagerMBean
{

    public SimpleOrderRuleManager(SimpleOrderRule rule)
    {
        this.rule = rule;
    }

    public Collection listRegisteredToOms()
    {
        return rule.getRegisteredOrders();
    }

    private final SimpleOrderRule rule;
}
