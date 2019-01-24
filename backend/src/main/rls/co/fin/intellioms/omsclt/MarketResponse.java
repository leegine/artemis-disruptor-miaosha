// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   MarketResponse.java

package co.fin.intellioms.omsclt;

import co.fin.intellioms.enums.MarketResponseType;

// Referenced classes of package com.com.fin.intellioms.omsclt:
//            OrderId, Quantity

public interface MarketResponse
{

    public abstract MarketResponseType getMarketResponseType();

    public abstract boolean getRejectFlag();

    public abstract long getOrderActionId();

    public abstract OrderId getOrderId();

    public abstract Quantity getQuantity();

    public abstract Quantity getFailedQuantity();
}
