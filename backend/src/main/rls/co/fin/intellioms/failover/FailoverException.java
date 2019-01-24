// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   FailoverException.java

package co.fin.intellioms.failover;


public class FailoverException extends Exception
{

    public FailoverException()
    {
    }

    public FailoverException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public FailoverException(String message)
    {
        super(message);
    }

    public FailoverException(Throwable cause)
    {
        super(cause);
    }
}
