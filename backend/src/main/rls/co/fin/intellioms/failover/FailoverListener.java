// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   FailoverListener.java

package co.fin.intellioms.failover;

import co.fin.intellioms.util.InitializationException;
import java.util.Set;

// Referenced classes of package com.com.fin.intellioms.failover:
//            RuleEngineNode

public interface FailoverListener
{

    public abstract void onNodeJoin(RuleEngineNode ruleenginenode, Set set);

    public abstract void onNodeFailure(RuleEngineNode ruleenginenode, Set set);

    public abstract void onNodeLeft(RuleEngineNode ruleenginenode, Set set);

    public abstract void start(Set set)
        throws InitializationException;

    public abstract void stop();
}
