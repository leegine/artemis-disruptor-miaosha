// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   PriceCluster.java

package co.fin.intellioms.price;

import co.fin.intellioms.enums.CondOrderType;
import co.fin.intellioms.omsclt.Price;
import co.fin.intellioms.quote.Quote;
import co.fin.intellioms.rulesys.CondOrder;
import co.lib.collection.SortedList;
import co.lib.collection.impl.SortedListImpl;
import co.fin.intellioms.enums.PriceDirection;

import java.util.*;

// Referenced classes of package com.com.fin.intellioms.price:
//            PriceCondition, PriceCondOrder

class PriceCluster
{

    PriceCluster()
    {
        up = new SortedListImpl(new Comparator() {

            public int compare(Object o1, Object o2)
            {
                PriceCondOrder ord1 = (PriceCondOrder)o1;
                if(o2 instanceof Price)
                {
                    return ord1.getPriceCondition().getTargetPrice().compareTo(o2);
                } else
                {
                    PriceCondOrder ord2 = (PriceCondOrder)o2;
                    return ord1.getPriceCondition().getTargetPrice().compareTo(ord2.getPriceCondition().getTargetPrice());
                }
            }

            final PriceCluster this$0;


            {
                this$0 = PriceCluster.this;
                super();
            }
        }
);
        down = new SortedListImpl(new Comparator() {

            public int compare(Object o1, Object o2)
            {
                PriceCondOrder ord1 = (PriceCondOrder)o1;
                if(o2 instanceof Price)
                {
                    return -ord1.getPriceCondition().getTargetPrice().compareTo(o2);
                } else
                {
                    PriceCondOrder ord2 = (PriceCondOrder)o2;
                    return -ord1.getPriceCondition().getTargetPrice().compareTo(ord2.getPriceCondition().getTargetPrice());
                }
            }

            final PriceCluster this$0;


            {
                this$0 = PriceCluster.this;
                super();
            }
        }
);
    }

    boolean isMatchesLastQuote(CondOrder ord)
    {
        PriceCondition cond = (PriceCondition)ord.getParam();
        Object obj = qtMux;
        JVM INSTR monitorenter ;
        if(lastQuote == null) goto _L2; else goto _L1
_L1:
        if(cond.getDirection() != PriceDirection.UP) goto _L4; else goto _L3
_L3:
        Price price;
        price = lastQuote.getHighPrice() == null ? lastQuote.getBasePrice() : lastQuote.getHighPrice();
        if(price.isAboveEq(cond.getTargetPrice()))
            return true;
          goto _L2
_L4:
        price = lastQuote.getLowPrice() == null ? lastQuote.getBasePrice() : lastQuote.getLowPrice();
        if(!price.isBelowEq(cond.getTargetPrice())) goto _L2; else goto _L5
_L5:
        true;
        obj;
        JVM INSTR monitorexit ;
        return;
_L2:
        obj;
        JVM INSTR monitorexit ;
          goto _L6
        Exception exception;
        exception;
        throw exception;
_L6:
        return false;
    }

    void addOrder(CondOrder ord)
    {
        if(!$assertionsDisabled && ord.getType() != CondOrderType.PRICE)
            throw new AssertionError((new StringBuilder()).append("Unexpected order type: ").append(ord).toString());
        PriceCondition cond = (PriceCondition)ord.getParam();
        if(cond.getDirection() == PriceDirection.UP)
            synchronized(up)
            {
                up.add(ord);
            }
        else
            synchronized(down)
            {
                down.add(ord);
            }
    }

    List findMatches(Quote qt)
    {
        synchronized(qtMux)
        {
            lastQuote = qt;
        }
        List result = null;
        Price price = qt.getHighPrice() == null ? qt.getBasePrice() : qt.getHighPrice();
        if(!$assertionsDisabled && price == null)
            throw new AssertionError("Unable to get price value from quote event. HighPrice and BasePrice are null.");
        synchronized(up)
        {
            int idx = up.findLast(price);
            List prices = up.subList(0, idx);
            if(prices.size() > 0)
                result = new ArrayList(prices);
        }
        if(!$assertionsDisabled && result != null && result.size() <= 0)
            throw new AssertionError();
        price = qt.getLowPrice() == null ? qt.getBasePrice() : qt.getLowPrice();
        synchronized(down)
        {
            int idx = down.findLast(price);
            List prices = down.subList(0, idx);
            if(prices.size() > 0)
                if(result == null)
                    result = new ArrayList(prices);
                else
                    result.addAll(prices);
        }
        if(!$assertionsDisabled && result != null && result.size() <= 0)
            throw new AssertionError();
        else
            return result;
    }

    boolean remove(CondOrder ord)
    {
        PriceCondition cond = (PriceCondition)ord.getParam();
        SortedList prices = cond.getDirection() != PriceDirection.UP ? down : up;
        SortedList sortedlist = prices;
        JVM INSTR monitorenter ;
        int i;
        int idx = prices.findFirst(ord);
        if(idx < 0)
            break MISSING_BLOCK_LABEL_109;
        i = idx;
_L1:
        if(i >= prices.size())
            break MISSING_BLOCK_LABEL_109;
        CondOrder o = (CondOrder)prices.get(i);
        if(o.equals(ord))
        {
            prices.remove(i);
            return true;
        }
        i++;
          goto _L1
        sortedlist;
        JVM INSTR monitorexit ;
          goto _L2
        Exception exception;
        exception;
        throw exception;
_L2:
        return false;
    }

    List getUpList()
    {
        SortedList sortedlist = up;
        JVM INSTR monitorenter ;
        return up.asList();
        Exception exception;
        exception;
        throw exception;
    }

    List getDownList()
    {
        SortedList sortedlist = down;
        JVM INSTR monitorenter ;
        return down.asList();
        Exception exception;
        exception;
        throw exception;
    }

    private SortedList up;
    private SortedList down;
    private Quote lastQuote;
    private final Object qtMux = new Object();
    static final boolean $assertionsDisabled = !com/ com /fin/intellioms/price/PriceCluster.desiredAssertionStatus();

}
