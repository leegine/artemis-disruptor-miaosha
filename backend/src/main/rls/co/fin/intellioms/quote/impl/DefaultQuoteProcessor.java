// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   DefaultQuoteProcessor.java

package co.fin.intellioms.quote.impl;

import co.fin.intellioms.enums.EventType;
import co.fin.intellioms.event.Event;
import co.fin.intellioms.event.EventException;
import co.fin.intellioms.event.impl.EventImpl;
import co.fin.intellioms.quote.Quote;
import co.fin.intellioms.quote.QuoteFeederAdaptor;
import co.fin.intellioms.quote.QuoteProcessor;
import co.fin.intellioms.util.Log;

public class DefaultQuoteProcessor
    implements QuoteProcessor
{

    public DefaultQuoteProcessor()
    {
        log = Log.getLogger(DefaultQuoteProcessor.class);
    }

    public void processQuote(Quote qt)
    {
        if(feeder == null)
            throw new AssertionError("Quote processor was not registered in quote feeder adapter.");
        Event evt = new EventImpl(EventType.QUOTE, qt);
        try
        {
            feeder.raiseEvent(evt);
        }
        catch(EventException e)
        {
            if(log.isError())
                log.error((new StringBuilder()).append("Error while raising quote event ").append(evt).toString(), e);
        }
    }

    public void onRegister(QuoteFeederAdaptor adaptor)
    {
        feeder = adaptor;
    }

    private Log log;
    private QuoteFeederAdaptor feeder;
//    static final boolean $assertionsDisabled = !com/ com /fin/intellioms/quote/impl/DefaultQuoteProcessor.desiredAssertionStatus();

}
