// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   Order.java

package co.fin.intellioms.omsclt;

import com.fitechlabs.fin.intellioms.enums.*;
import co.fin.intellioms.ticker.Ticker;

// Referenced classes of package com.com.fin.intellioms.omsclt:
//            OrderId, Price, Quantity

public interface Order
{

    public abstract OrderId getOrderId();

    public abstract long getAccountId();

    public abstract Ticker getTicker();

    public abstract OrderId getInitialOrderId();

    public abstract OrderExecType getExecType();

    public abstract void setExecType(OrderExecType orderexectype);

    public abstract Side getSide();

    public abstract OrderType getOrderType();

    public abstract Price getPrice();

    public abstract void setPrice(Price price);

    public abstract Quantity getQuantity();

    public abstract Quantity getOriginalQuantity();

    public abstract void setOriginalQuantity(Quantity quantity);

    public abstract Quantity getExecutedQuantity();

    public abstract String getMemo();

    public abstract void setMemo(String s);
}
