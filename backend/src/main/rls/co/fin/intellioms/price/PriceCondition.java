// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   PriceCondition.java

package co.fin.intellioms.price;

import co.fin.intellioms.omsclt.Price;
import co.fin.intellioms.ticker.Ticker;
import co.fin.intellioms.enums.PriceDirection;

public class PriceCondition
{

    public PriceCondition(Price targetPrice, PriceDirection direction, Ticker ticker)
    {
        if(!$assertionsDisabled && targetPrice == null)
            throw new AssertionError("target price is null.");
        if(!$assertionsDisabled && direction == null)
        {
            throw new AssertionError("Price direction is null.");
        } else
        {
            this.targetPrice = targetPrice;
            this.direction = direction;
            this.ticker = ticker;
            return;
        }
    }

    public PriceDirection getDirection()
    {
        return direction;
    }

    public Price getTargetPrice()
    {
        return targetPrice;
    }

    public Ticker getTicker()
    {
        return ticker;
    }

    public boolean equals(Object o)
    {
        if(o instanceof PriceCondition)
        {
            PriceCondition cond = (PriceCondition)o;
            return targetPrice.equals(cond.getTargetPrice()) && direction.equals(cond.getDirection()) && ticker.equals(cond.getTicker());
        } else
        {
            return false;
        }
    }

    /**
     * @deprecated Method isSameAs is deprecated
     */

    public boolean isSameAs(PriceCondition cond)
    {
        return equals(cond);
    }

    public String toString()
    {
        return (new StringBuilder()).append("PriceCondition[price=").append(targetPrice).append(", dir=").append(direction).append(", ticker=").append(ticker).append("]").toString();
    }

    private final Price targetPrice;
    private final PriceDirection direction;
    private final Ticker ticker;
    static final boolean $assertionsDisabled = !com/ com /fin/intellioms/price/PriceCondition.desiredAssertionStatus();

}
