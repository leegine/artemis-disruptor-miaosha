// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   DefaultMarketResponseProcessor.java

package co.fin.intellioms.omsclt.impl;

import co.fin.intellioms.enums.EventType;
import co.fin.intellioms.enums.MarketResponseType;
import co.fin.intellioms.event.Event;
import co.fin.intellioms.event.EventException;
import co.fin.intellioms.event.impl.EventImpl;
import com.fitechlabs.fin.intellioms.omsclt.*;

public class DefaultMarketResponseProcessor
    implements MarketResponseProcessor
{

    public DefaultMarketResponseProcessor()
    {
    }

    public boolean process(MarketResponse resp)
        throws OmsAdaptorException
    {
        if(!$assertionsDisabled && adapter == null)
            throw new AssertionError("Market response processor was not registered in OMS adapter.");
        if(!$assertionsDisabled && resp == null)
            throw new AssertionError("Market response is null.");
        Event evt;
        if(resp.getMarketResponseType() == MarketResponseType.EXECUTE)
            evt = new EventImpl(EventType.EXECUTION, resp);
        else
            evt = new EventImpl(EventType.MARKET_RESP, resp);
        try
        {
            adapter.raiseEvent(evt);
        }
        catch(EventException e)
        {
            throw new OmsAdaptorException(e.getMessage(), e);
        }
        return false;
    }

    public void onRegister(OmsAdaptor adapter)
    {
        this.adapter = adapter;
    }

    private OmsAdaptor adapter;
    static final boolean $assertionsDisabled = !com/ com /fin/intellioms/omsclt/impl/DefaultMarketResponseProcessor.desiredAssertionStatus();

}
