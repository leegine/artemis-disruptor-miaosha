// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   OmsAdaptor.java

package co.fin.intellioms.omsclt;

import co.fin.intellioms.event.EventSource;

// Referenced classes of package com.com.fin.intellioms.omsclt:
//            OmsAdaptorException, Order, MarketResponseProcessor

public interface OmsAdaptor
    extends EventSource
{

    /**
     * @deprecated Method connect is deprecated
     */

    public abstract boolean connect(String s)
        throws OmsAdaptorException;

    /**
     * @deprecated Method disconnect is deprecated
     */

    public abstract boolean disconnect()
        throws OmsAdaptorException;

    public abstract boolean submitOrder(Order order)
        throws OmsAdaptorException;

    public abstract boolean modifyOrder(Order order)
        throws OmsAdaptorException;

    public abstract boolean cancelOrder(Order order)
        throws OmsAdaptorException;

    public abstract void registerMarketResponseProcessor(MarketResponseProcessor marketresponseprocessor);
}
