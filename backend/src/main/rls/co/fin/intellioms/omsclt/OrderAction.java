// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   OrderAction.java

package co.fin.intellioms.omsclt;

import co.fin.intellioms.enums.OrderActionType;
import co.fin.intellioms.enums.OrderExecType;

// Referenced classes of package com.com.fin.intellioms.omsclt:
//            Quantity, Price

public interface OrderAction
{

    public abstract OrderActionType getOrderActionType();

    public abstract long getOrderActionId();

    public abstract long getOrderId();

    public abstract Quantity getQuantity();

    public abstract Quantity getExecutedQuantity();

    public abstract Quantity getOldQuantity();

    public abstract Price getPrice();

    public abstract Price getOldPrice();

    public abstract OrderExecType getExecType();

    public abstract OrderExecType getOldExecType();
}
