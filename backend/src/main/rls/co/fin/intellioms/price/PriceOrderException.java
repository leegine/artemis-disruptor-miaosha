// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   PriceOrderException.java

package co.fin.intellioms.price;


public class PriceOrderException extends Exception
{

    public PriceOrderException()
    {
    }

    public PriceOrderException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public PriceOrderException(String message)
    {
        super(message);
    }

    public PriceOrderException(Throwable cause)
    {
        super(cause);
    }
}
