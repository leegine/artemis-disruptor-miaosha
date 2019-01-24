// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   PersistException.java

package co.fin.intellioms.persist;

import co.fin.intellioms.event.EventException;

public class PersistException extends EventException
{

    public PersistException()
    {
    }

    public PersistException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public PersistException(String message)
    {
        super(message);
    }

    public PersistException(Throwable cause)
    {
        super(cause);
    }
}
