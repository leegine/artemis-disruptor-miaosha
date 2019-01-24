// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   QuoteProcessor.java

package co.fin.intellioms.quote;


// Referenced classes of package com.com.fin.intellioms.quote:
//            Quote, QuoteFeederAdaptor

public interface QuoteProcessor
{

    public abstract void processQuote(Quote quote);

    public abstract void onRegister(QuoteFeederAdaptor quotefeederadaptor);
}
