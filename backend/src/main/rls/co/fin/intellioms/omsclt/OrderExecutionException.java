// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   OrderExecutionException.java

package co.fin.intellioms.omsclt;


public class OrderExecutionException extends Exception
{

    public OrderExecutionException()
    {
    }

    public OrderExecutionException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public OrderExecutionException(String message)
    {
        super(message);
    }

    public OrderExecutionException(Throwable cause)
    {
        super(cause);
    }
}
