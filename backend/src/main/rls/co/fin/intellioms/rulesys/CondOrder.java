// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   CondOrder.java

package co.fin.intellioms.rulesys;

import co.fin.intellioms.enums.CondOrderState;
import co.fin.intellioms.enums.CondOrderType;
import java.util.List;

// Referenced classes of package com.com.fin.intellioms.rulesys:
//            CondOrderException, Ratio

public interface CondOrder
{

    public abstract long getId();

    public abstract CondOrderType getType();

    public abstract Object getParam();

    public abstract CondOrderState getState();

    public abstract void setState(CondOrderState condorderstate);

    public abstract long getAccountId();

    public abstract Ratio getExecutedRatio();

    public abstract void processExecution(CondOrder condorder, Ratio ratio)
        throws CondOrderException;

    public abstract Ratio getActivatedRatio();

    public abstract void setActivatedRatio(Ratio ratio)
        throws CondOrderException;

    public abstract void setParent(CondOrder condorder);

    public abstract CondOrder getParent();

    public abstract void addSubOrder(CondOrder condorder);

    public abstract List getSubOrders();

    public abstract boolean hasSubOrders();
}
