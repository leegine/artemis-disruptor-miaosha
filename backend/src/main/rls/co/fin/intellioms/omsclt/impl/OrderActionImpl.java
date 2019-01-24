// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   OrderActionImpl.java

package co.fin.intellioms.omsclt.impl;

import co.fin.intellioms.enums.OrderActionType;
import co.fin.intellioms.enums.OrderExecType;
import com.fitechlabs.fin.intellioms.omsclt.*;

public class OrderActionImpl
    implements OrderAction
{

    public OrderActionImpl()
    {
    }

    public OrderExecType getExecType()
    {
        return execType;
    }

    public void setExecType(OrderExecType execType)
    {
        this.execType = execType;
    }

    public Quantity getExecutedQuantity()
    {
        return executedQuantity;
    }

    public void setExecutedQuantity(Quantity executedQuantity)
    {
        this.executedQuantity = executedQuantity;
    }

    public OrderExecType getOldExecType()
    {
        return oldExecType;
    }

    public void setOldExecType(OrderExecType oldExecType)
    {
        this.oldExecType = oldExecType;
    }

    public Price getOldPrice()
    {
        return oldPrice;
    }

    public void setOldPrice(Price oldPrice)
    {
        this.oldPrice = oldPrice;
    }

    public Quantity getOldQuantity()
    {
        return oldQuantity;
    }

    public void setOldQuantity(Quantity oldQuantity)
    {
        this.oldQuantity = oldQuantity;
    }

    public long getOrderActionId()
    {
        return orderActionId;
    }

    public void setOrderActionId(long orderActionId)
    {
        this.orderActionId = orderActionId;
    }

    public OrderActionType getOrderActionType()
    {
        return orderActionType;
    }

    public void setOrderActionType(OrderActionType orderActionType)
    {
        this.orderActionType = orderActionType;
    }

    public long getOrderId()
    {
        return orderId;
    }

    public void setOrderId(long orderId)
    {
        this.orderId = orderId;
    }

    public Price getPrice()
    {
        return price;
    }

    public void setPrice(Price price)
    {
        this.price = price;
    }

    public Quantity getQuantity()
    {
        return quantity;
    }

    public void setQuantity(Quantity quantity)
    {
        this.quantity = quantity;
    }

    public String toString()
    {
        return (new StringBuilder()).append("OrderAction[id=").append(orderActionId).append(", orderId=").append(orderId).append(", execQty=").append(executedQuantity).append("]").toString();
    }

    private OrderActionType orderActionType;
    private long orderActionId;
    private long orderId;
    private Quantity quantity;
    private Quantity executedQuantity;
    private Quantity oldQuantity;
    private Price price;
    private Price oldPrice;
    private OrderExecType execType;
    private OrderExecType oldExecType;
}
