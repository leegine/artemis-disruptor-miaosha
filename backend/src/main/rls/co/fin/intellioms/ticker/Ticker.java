// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   Ticker.java

package co.fin.intellioms.ticker;


public class Ticker
{

    public Ticker(long prodId, long markId)
    {
        productId = prodId;
        marketId = markId;
    }

    public long getMarketId()
    {
        return marketId;
    }

    public long getProductId()
    {
        return productId;
    }

    public int hashCode()
    {
        long hash = marketId + productId;
        return (int)(hash ^ hash >>> 32);
    }

    public boolean equals(Object o)
    {
        if(o instanceof Ticker)
        {
            Ticker k = (Ticker)o;
            return k.marketId == marketId && k.productId == productId;
        } else
        {
            return false;
        }
    }

    public String toString()
    {
        return (new StringBuilder()).append("Ticker[prodId=").append(productId).append(", marketId=").append(marketId).append("]").toString();
    }

    private final long marketId;
    private final long productId;
}
