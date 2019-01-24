// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   QuoteImpl.java

package co.fin.intellioms.quote.impl;

import co.fin.intellioms.omsclt.Price;
import co.fin.intellioms.quote.BARecord;
import co.fin.intellioms.quote.Quote;
import co.fin.intellioms.ticker.Ticker;

import java.util.Date;

public class QuoteImpl
    implements Quote
{

    public QuoteImpl(Ticker ticker, Price base)
    {
        this.ticker = ticker;
        basePrice = base;
    }

    public Price getAskPrice()
    {
        return askPrice;
    }

    public void setAskPrice(Price askPrice)
    {
        this.askPrice = askPrice;
    }

    public Price getBasePrice()
    {
        return basePrice;
    }

    public Price getBidPrice()
    {
        return bidPrice;
    }

    public void setBidPrice(Price bidPrice)
    {
        this.bidPrice = bidPrice;
    }

    public long getEndTime()
    {
        return endTime;
    }

    public void setEndTime(long endTime)
    {
        this.endTime = endTime;
    }

    public Price getHighPrice()
    {
        return highPrice;
    }

    public void setHighPrice(Price highPrice)
    {
        this.highPrice = highPrice;
    }

    public Price getLastPrice()
    {
        return lastPrice;
    }

    public void setLastPrice(Price lastPrice)
    {
        this.lastPrice = lastPrice;
    }

    public Price getLowPrice()
    {
        return lowPrice;
    }

    public void setLowPrice(Price lowPrice)
    {
        this.lowPrice = lowPrice;
    }

    public Price getOpenPrice()
    {
        return openPrice;
    }

    public void setOpenPrice(Price openPrice)
    {
        this.openPrice = openPrice;
    }

    public long getStartTime()
    {
        return startTime;
    }

    public void setStartTime(long startTime)
    {
        this.startTime = startTime;
    }

    public Ticker getTicker()
    {
        return ticker;
    }

    public void setTicker(Ticker ticker)
    {
        this.ticker = ticker;
    }

    public BARecord[] getAsks()
    {
        return new BARecord[0];
    }

    public BARecord[] getBids()
    {
        return new BARecord[0];
    }

    public String toString()
    {
        return (new StringBuilder()).append("Quote[ticker=").append(ticker).append(", lastPrice=").append(lastPrice).append(", openPrice=").append(openPrice).append(", basePrice=").append(basePrice).append(", highPrice=").append(highPrice).append(", lowPrice=").append(lowPrice).append(", askPrice=").append(askPrice).append(", bidPrice=").append(bidPrice).append(", startTime=").append(new Date(startTime)).append(", endTime=").append(new Date(endTime)).append("]").toString();
    }

    private Ticker ticker;
    private long startTime;
    private long endTime;
    private Price lastPrice;
    private Price openPrice;
    private final Price basePrice;
    private Price highPrice;
    private Price lowPrice;
    private Price askPrice;
    private Price bidPrice;
}
