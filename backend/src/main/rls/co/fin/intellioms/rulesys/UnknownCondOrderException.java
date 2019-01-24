// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   UnknownCondOrderException.java

package co.fin.intellioms.rulesys;


// Referenced classes of package com.com.fin.intellioms.rulesys:
//            CondOrderException, CondOrder

public class UnknownCondOrderException extends CondOrderException
{

    public UnknownCondOrderException(CondOrder ord)
    {
        super(ord);
    }

    public UnknownCondOrderException(String message, CondOrder ord)
    {
        super(message, ord);
    }

    public UnknownCondOrderException(String message, Throwable cause, CondOrder ord)
    {
        super(message, cause, ord);
    }

    public UnknownCondOrderException(Throwable cause, CondOrder ord)
    {
        super(cause, ord);
    }
}
