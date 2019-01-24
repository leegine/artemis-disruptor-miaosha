// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   QuoteFeederAdaptor.java

package co.fin.intellioms.quote;

import co.fin.intellioms.event.EventSource;

// Referenced classes of package com.com.fin.intellioms.quote:
//            QuoteFeederAdaptorException, QuoteProcessor

public interface QuoteFeederAdaptor
    extends EventSource
{

    /**
     * @deprecated Method connect is deprecated
     */

    public abstract boolean connect(String s)
        throws QuoteFeederAdaptorException;

    /**
     * @deprecated Method disconnect is deprecated
     */

    public abstract boolean disconnect()
        throws QuoteFeederAdaptorException;

    /**
     * @deprecated Method subscribe is deprecated
     */

    public abstract boolean subscribe(String s)
        throws QuoteFeederAdaptorException;

    /**
     * @deprecated Method subscribe is deprecated
     */

    public abstract boolean subscribe(String as[])
        throws QuoteFeederAdaptorException;

    /**
     * @deprecated Method unsubscribe is deprecated
     */

    public abstract boolean unsubscribe(String s)
        throws QuoteFeederAdaptorException;

    /**
     * @deprecated Method unsubscribe is deprecated
     */

    public abstract boolean unsubscribe(String as[])
        throws QuoteFeederAdaptorException;

    public abstract void registerQuoteProcessor(QuoteProcessor quoteprocessor);
}
