// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   QuoteFeederAdaptorException.java

package co.fin.intellioms.quote;

import co.fin.intellioms.event.EventException;

public class QuoteFeederAdaptorException extends EventException
{

    public QuoteFeederAdaptorException()
    {
    }

    public QuoteFeederAdaptorException(String msg)
    {
        super(msg);
    }

    public QuoteFeederAdaptorException(String msg, Throwable exp)
    {
        super(msg, exp);
    }
}
