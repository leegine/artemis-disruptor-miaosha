// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   MarketResponseImpl.java

package co.fin.intellioms.omsclt.impl;

import co.fin.intellioms.enums.MarketResponseType;
import com.fitechlabs.fin.intellioms.omsclt.*;

public class MarketResponseImpl
    implements MarketResponse
{

    public MarketResponseImpl(MarketResponseType type, long actionId, Quantity qty, OrderId orderId, Quantity failedQty, boolean reject)
    {
        this.actionId = actionId;
        this.failedQty = failedQty;
        this.orderId = orderId;
        this.qty = qty;
        this.reject = reject;
        this.type = type;
    }

    public MarketResponseType getMarketResponseType()
    {
        return type;
    }

    public boolean getRejectFlag()
    {
        return reject;
    }

    public long getOrderActionId()
    {
        return actionId;
    }

    public OrderId getOrderId()
    {
        return orderId;
    }

    public Quantity getQuantity()
    {
        return qty;
    }

    public Quantity getFailedQuantity()
    {
        return failedQty;
    }

    public String toString()
    {
        return (new StringBuilder()).append("MarketResponse[type=").append(type).append(", actionId=").append(actionId).append(", failedQty=").append(failedQty).append(", orderId=").append(orderId).append(", qty=").append(qty).append(", reject=").append(reject).append("]").toString();
    }

    private MarketResponseType type;
    private boolean reject;
    private long actionId;
    private OrderId orderId;
    private Quantity qty;
    private Quantity failedQty;
}
