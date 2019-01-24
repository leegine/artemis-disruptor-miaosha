// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   CondOrderException.java

package co.fin.intellioms.rulesys;


// Referenced classes of package com.com.fin.intellioms.rulesys:
//            CondOrder

public class CondOrderException extends Exception
{

    public CondOrderException(CondOrder ord)
    {
        this.ord = ord;
    }

    public CondOrderException(String message, Throwable cause, CondOrder ord)
    {
        super(message, cause);
        this.ord = ord;
    }

    public CondOrderException(String message, CondOrder ord)
    {
        super(message);
        this.ord = ord;
    }

    public CondOrderException(Throwable cause, CondOrder ord)
    {
        super(cause);
        this.ord = ord;
    }

    public CondOrder getOrder()
    {
        return ord;
    }

    public String getMessage()
    {
        return (new StringBuilder()).append(super.getMessage()).append(" ").append(ord).toString();
    }

    private final CondOrder ord;
}
