// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   OrderImpl.java

package co.fin.intellioms.omsclt.impl;

import co.fin.intellioms.ticker.Ticker;
import com.fitechlabs.fin.intellioms.enums.*;
import com.fitechlabs.fin.intellioms.omsclt.*;
import com.fitechlabs.xtier.services.marshal.Marshallable;
import java.util.HashMap;
import java.util.Map;

public class OrderImpl
    implements Order, Marshallable
{
    private static class MarshalConst
    {

        static final Integer OMS_ORD_ID = new Integer(10001);
        static final Integer OMS_ORD_ACC_ID = new Integer(10002);
        static final Integer OMS_ORD_PROD_ID = new Integer(10003);
        static final Integer OMS_ORD_MARKET_ID = new Integer(10004);
        static final Integer OMS_ORD_EXEC_TYPE = new Integer(10005);
        static final Integer OMS_ORD_SIDE = new Integer(10006);
        static final Integer OMS_ORD_TYPE = new Integer(10007);
        static final Integer OMS_ORD_PRICE = new Integer(10008);
        static final Integer OMS_ORD_QTY = new Integer(10009);
        static final Integer OMS_ORD_ORIG_QTY = new Integer(10010);
        static final Integer OMS_ORD_EXEC_QTY = new Integer(10011);
        static final Integer OMS_INIT_ORD_ID = new Integer(10012);
        static final Integer OMS_INIT_ORD_TYPE = new Integer(10013);
        static final Integer OMS_ORD_MEMO = new Integer(10014);


        private MarshalConst()
        {
        }
    }


    public OrderImpl()
    {
    }

    public OrderImpl(OrderId orderid, long accountid, Side side, Price price, Quantity quantity, Ticker ticker,
            OrderExecType execType, Quantity execQuantity, Quantity origQuantity)
    {
        if(!$assertionsDisabled && orderid == null)
            throw new AssertionError("Order ID is null.");
        if(!$assertionsDisabled && quantity == null)
            throw new AssertionError("Quantity is null.");
        if(!$assertionsDisabled && origQuantity == null)
            throw new AssertionError("Original quantity is null.");
        if(!$assertionsDisabled && execQuantity == null)
            throw new AssertionError("Executed quantity is null.");
        if(!$assertionsDisabled && (origQuantity == quantity || origQuantity == execQuantity || execQuantity == quantity))
        {
            throw new AssertionError("Original quantity, executed quantity and order quantity must have different JVM identities.");
        } else
        {
            accountId = accountid;
            this.execQuantity = execQuantity;
            this.quantity = quantity;
            this.execType = execType;
            orderId = orderid;
            this.origQuantity = origQuantity;
            this.price = price;
            this.side = side;
            this.ticker = ticker;
            return;
        }
    }

    public OrderImpl(Order o)
    {
        accountId = o.getAccountId();
        execQuantity = new Quantity(o.getExecutedQuantity());
        quantity = new Quantity(o.getQuantity());
        execType = o.getExecType();
        orderId = o.getOrderId();
        origQuantity = new Quantity(o.getOriginalQuantity());
        price = o.getPrice() == null ? null : new Price(o.getPrice());
        side = o.getSide();
        ticker = o.getTicker();
        memo = o.getMemo();
        if(!$assertionsDisabled && orderId == null)
            throw new AssertionError();
        if(!$assertionsDisabled && quantity == null)
            throw new AssertionError();
        if(!$assertionsDisabled && origQuantity == null)
            throw new AssertionError();
        if(!$assertionsDisabled && execQuantity == null)
            throw new AssertionError();
        if(!$assertionsDisabled && (origQuantity == quantity || origQuantity == execQuantity || execQuantity == quantity))
            throw new AssertionError();
        else
            return;
    }

    public long getAccountId()
    {
        return accountId;
    }

    public void execute(Quantity qty)
    {
        if(!$assertionsDisabled && !qty.isBelowEq(quantity))
            throw new AssertionError((new StringBuilder()).append(qty).append(" ").append(quantity).toString());
        execQuantity.add(qty);
        quantity.subtruct(qty);
        if(!$assertionsDisabled && !quantity.isAboveEq(Quantity.ZERO))
            throw new AssertionError();
        if(!$assertionsDisabled && !quantity.equals((new Quantity(origQuantity)).subtruct(execQuantity)))
            throw new AssertionError();
        else
            return;
    }

    public OrderId getOrderId()
    {
        return orderId;
    }

    public OrderExecType getExecType()
    {
        return execType;
    }

    public Side getSide()
    {
        return side;
    }

    public OrderType getOrderType()
    {
        return orderId.getType();
    }

    public Price getPrice()
    {
        return price;
    }

    public Quantity getQuantity()
    {
        return quantity;
    }

    public Quantity getOriginalQuantity()
    {
        return origQuantity;
    }

    public void setOriginalQuantity(Quantity qty)
    {
        if(!$assertionsDisabled && !qty.isAboveEq(execQuantity))
        {
            throw new AssertionError((new StringBuilder()).append("New original quantity is below executed quantity :").append(qty).append(" ").append(this).toString());
        } else
        {
            origQuantity = qty;
            quantity = (Quantity)(new Quantity(origQuantity)).subtruct(execQuantity);
            return;
        }
    }

    public void setExecType(OrderExecType type)
    {
        execType = type;
    }

    public void setPrice(Price price)
    {
        this.price = price;
    }

    public Quantity getExecutedQuantity()
    {
        return execQuantity;
    }

    public void setExecutedQuantity(Quantity qty)
    {
        if(!$assertionsDisabled && !qty.isBelowEq(origQuantity))
        {
            throw new AssertionError((new StringBuilder()).append("Executed quantity is above original quantity: execQty=").append(qty).append(", origQty=").append(origQuantity).toString());
        } else
        {
            execQuantity = qty;
            quantity = (Quantity)(new Quantity(origQuantity)).subtruct(qty);
            return;
        }
    }

    public Ticker getTicker()
    {
        return ticker;
    }

    public OrderId getInitialOrderId()
    {
        return initialOrderId;
    }

    public void setInitialOrderId(OrderId initialOrderId)
    {
        this.initialOrderId = initialOrderId;
    }

    public String getMemo()
    {
        return memo;
    }

    public void setMemo(String memo)
    {
        this.memo = memo;
    }

    public boolean equals(Object o)
    {
        if(this == o)
            return true;
        if(o == null || getClass() != o.getClass())
        {
            return false;
        } else
        {
            OrderImpl ord = (OrderImpl)o;
            return orderId.equals(ord.orderId);
        }
    }

    public int hashCode()
    {
        return orderId.hashCode();
    }

    public short typeGuid()
    {
        return 100;
    }

    public Map getObjs()
    {
        Map param = new HashMap(11, 1.0F);
        param.put(MarshalConst.OMS_ORD_ID, new Long(orderId.getId()));
        param.put(MarshalConst.OMS_ORD_ACC_ID, new Long(accountId));
        param.put(MarshalConst.OMS_ORD_PROD_ID, new Long(ticker.getProductId()));
        param.put(MarshalConst.OMS_ORD_MARKET_ID, new Long(ticker.getMarketId()));
        param.put(MarshalConst.OMS_ORD_EXEC_TYPE, new Integer(execType.toValue()));
        param.put(MarshalConst.OMS_ORD_SIDE, new Integer(side.toValue()));
        param.put(MarshalConst.OMS_ORD_TYPE, new Integer(orderId.getType().toValue()));
        param.put(MarshalConst.OMS_ORD_QTY, quantity.toString());
        param.put(MarshalConst.OMS_ORD_ORIG_QTY, origQuantity.toString());
        if(price != null)
            param.put(MarshalConst.OMS_ORD_PRICE, price.toString());
        if(execQuantity != null)
            param.put(MarshalConst.OMS_ORD_EXEC_QTY, execQuantity.toString());
        if(initialOrderId != null)
        {
            param.put(MarshalConst.OMS_INIT_ORD_ID, new Long(initialOrderId.getId()));
            param.put(MarshalConst.OMS_INIT_ORD_TYPE, new Integer(initialOrderId.getType().toValue()));
        }
        if(memo != null)
            param.put(MarshalConst.OMS_ORD_MEMO, memo);
        return param;
    }

    public void setObjs(Map param)
    {
        accountId = ((Long)param.get(MarshalConst.OMS_ORD_ACC_ID)).longValue();
        execType = OrderExecType.getEnum(((Integer)param.get(MarshalConst.OMS_ORD_EXEC_TYPE)).intValue());
        side = Side.getEnum(((Integer)param.get(MarshalConst.OMS_ORD_SIDE)).intValue());
        quantity = new Quantity((String)param.get(MarshalConst.OMS_ORD_QTY));
        origQuantity = new Quantity((String)param.get(MarshalConst.OMS_ORD_ORIG_QTY));
        orderId = new OrderId(((Long)param.get(MarshalConst.OMS_ORD_ID)).longValue(), OrderType.getEnum(((Integer)param.get(MarshalConst.OMS_ORD_TYPE)).intValue()));
        memo = (String)param.get(MarshalConst.OMS_ORD_MEMO);
        if(param.containsKey(MarshalConst.OMS_ORD_EXEC_QTY))
            execQuantity = new Quantity((String)param.get(MarshalConst.OMS_ORD_EXEC_QTY));
        else
            execQuantity = new Quantity(0.0D);
        if(param.containsKey(MarshalConst.OMS_ORD_PRICE))
            price = new Price((String)param.get(MarshalConst.OMS_ORD_PRICE));
        else
            price = new Price(0.0D);
        if(param.containsKey(MarshalConst.OMS_INIT_ORD_ID))
            initialOrderId = new OrderId(((Long)param.get(MarshalConst.OMS_INIT_ORD_ID)).longValue(), OrderType.getEnum(((Integer)param.get(MarshalConst.OMS_INIT_ORD_TYPE)).intValue()));
        long prodId = ((Long)param.get(MarshalConst.OMS_ORD_PROD_ID)).longValue();
        long marketId = ((Long)param.get(MarshalConst.OMS_ORD_MARKET_ID)).longValue();
        ticker = new Ticker(prodId, marketId);
    }

    public void onMarshal()
    {
    }

    public void onDemarshal()
    {
    }

    public String toString()
    {
        return (new StringBuilder()).append("Order[id=").append(orderId).append(", ticker=").append(ticker).append(", accountId=").append(accountId).append(", origQty=").append(origQuantity).append(", qty=").append(quantity).append(", execQty=").append(execQuantity).append(", price=").append(price).append(", execType=").append(execType).append(", side=").append(side).append(", initialOrderId=").append(initialOrderId).append(", memo=").append(memo).append(']').toString();
    }

    public static final short GUID = 100;
    private long accountId;
    private OrderId orderId;
    private OrderId initialOrderId;
    private Ticker ticker;
    private OrderExecType execType;
    private Side side;
    private Quantity quantity;
    private Price price;
    private Quantity origQuantity;
    private Quantity execQuantity;
    private String memo;
    static final boolean $assertionsDisabled = !com/ com /fin/intellioms/omsclt/impl/OrderImpl.desiredAssertionStatus();

}
