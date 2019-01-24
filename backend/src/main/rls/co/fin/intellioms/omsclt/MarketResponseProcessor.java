// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   MarketResponseProcessor.java

package co.fin.intellioms.omsclt;


// Referenced classes of package com.com.fin.intellioms.omsclt:
//            OmsAdaptorException, MarketResponse, OmsAdaptor

public interface MarketResponseProcessor
{

    public abstract boolean process(MarketResponse marketresponse)
        throws OmsAdaptorException;

    public abstract void onRegister(OmsAdaptor omsadaptor);
}
