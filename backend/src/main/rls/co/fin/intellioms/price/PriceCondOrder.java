// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   PriceCondOrder.java

package co.fin.intellioms.price;

import co.fin.intellioms.omsclt.Price;
import co.fin.intellioms.rulesys.impl.AbstractCondOrder;
import co.fin.intellioms.ticker.Ticker;
import co.fin.intellioms.util.CondOrderUtill;
import co.fin.intellioms.enums.CondOrderType;
import co.fin.intellioms.enums.PriceDirection;
import com.fitechlabs.fin.intellioms.rulesys.*;

import java.util.Map;

// Referenced classes of package com.com.fin.intellioms.price:
//            PriceCondition

public class PriceCondOrder extends AbstractCondOrder
{
    private static class MarshalConst
    {

        private static final Integer TARGET_PRICE = new Integer(6001);
        private static final Integer PRICE_DIRECTION = new Integer(6002);
        private static final Integer TICKER_PROD_ID = new Integer(6003);
        private static final Integer TICKER_MARKET_ID = new Integer(6004);






        private MarshalConst()
        {
        }
    }


    public PriceCondOrder(long condOrderId, long accId, PriceCondition condition)
    {
        super(condOrderId, accId, CondOrderType.PRICE, condition);
    }

    public PriceCondOrder()
    {
        super(CondOrderType.PRICE);
    }

    public PriceCondition getPriceCondition()
    {
        return (PriceCondition)getParam();
    }

    public Ratio getActivatedRatio()
    {
        if(subOrders == null)
            return null;
        else
            return getFirstChild().getActivatedRatio();
    }

    public void setActivatedRatio(Ratio ratio)
        throws CondOrderException
    {
        for(int i = 0; i < subOrders.size(); i++)
        {
            CondOrder ord = (CondOrder)subOrders.get(i);
            ord.setActivatedRatio(ratio);
        }

    }

    public Ratio getExecutedRatio()
    {
        return CondOrderUtill.findLargestExecutedRatio(subOrders);
    }

    private CondOrder getFirstChild()
    {
        if(!hasSubOrders())
            throw new NullPointerException((new StringBuilder()).append("Missing suborder in conditional price order. ").append(this).toString());
        else
            return (CondOrder)subOrders.get(0);
    }

    protected void marshal(Map params)
    {
        PriceCondition cond = getPriceCondition();
        params.put(MarshalConst.PRICE_DIRECTION, new Integer(cond.getDirection().toValue()));
        params.put(MarshalConst.TARGET_PRICE, cond.getTargetPrice().toString());
        params.put(MarshalConst.TICKER_MARKET_ID, new Long(cond.getTicker().getMarketId()));
        params.put(MarshalConst.TICKER_PROD_ID, new Long(cond.getTicker().getProductId()));
    }

    protected void demarshal(Map params)
    {
        int intDir = ((Integer)params.get(MarshalConst.PRICE_DIRECTION)).intValue();
        PriceDirection dir = (PriceDirection)PriceDirection.getEnum(intDir);
        String priceDbl = (String)params.get(MarshalConst.TARGET_PRICE);
        Price price = new Price(priceDbl);
        long prodId = ((Long)params.get(MarshalConst.TICKER_PROD_ID)).longValue();
        long markId = ((Long)params.get(MarshalConst.TICKER_MARKET_ID)).longValue();
        setParam(new PriceCondition(price, dir, new Ticker(prodId, markId)));
    }

    public short typeGuid()
    {
        return 2;
    }

    public static final short GUID = 2;
}
