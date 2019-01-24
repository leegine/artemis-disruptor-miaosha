// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   Quote.java

package co.fin.intellioms.quote;

import co.fin.intellioms.omsclt.Price;
import co.fin.intellioms.ticker.Ticker;

// Referenced classes of package com.com.fin.intellioms.quote:
//            BARecord

public interface Quote
{

    public abstract Ticker getTicker();

    public abstract long getStartTime();

    public abstract long getEndTime();

    public abstract Price getLastPrice();

    public abstract Price getOpenPrice();

    public abstract Price getBasePrice();

    public abstract Price getHighPrice();

    public abstract Price getLowPrice();

    public abstract Price getAskPrice();

    public abstract Price getBidPrice();

    public abstract BARecord[] getAsks();

    public abstract BARecord[] getBids();
}
