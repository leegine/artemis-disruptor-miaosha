// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   OmsAdaptorException.java

package co.fin.intellioms.omsclt;

import co.fin.intellioms.event.EventException;

public class OmsAdaptorException extends EventException
{

    public OmsAdaptorException(String msg)
    {
        super(msg);
    }

    public OmsAdaptorException()
    {
    }

    public OmsAdaptorException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public OmsAdaptorException(Throwable cause)
    {
        super(cause);
    }
}
