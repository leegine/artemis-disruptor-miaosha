// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   PriceOrderRuleManager.java

package co.fin.intellioms.jmx;

import co.fin.intellioms.price.PriceOrderRule;
import java.util.List;

// Referenced classes of package com.com.fin.intellioms.jmx:
//            PriceOrderRuleManagerMBean

public class PriceOrderRuleManager
    implements PriceOrderRuleManagerMBean
{

    public PriceOrderRuleManager(PriceOrderRule rule)
    {
        this.rule = rule;
    }

    public List listRegisteredTickers()
    {
        return rule.getRegisteredTickers();
    }

    private final PriceOrderRule rule;
}
