// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   CondOrderValidationException.java

package co.fin.intellioms.rulesys;


// Referenced classes of package com.com.fin.intellioms.rulesys:
//            CondOrderException, CondOrder

public class CondOrderValidationException extends CondOrderException
{

    public CondOrderValidationException(CondOrder ord)
    {
        super(ord);
    }

    public CondOrderValidationException(String message, CondOrder ord)
    {
        super(message, ord);
    }

    public CondOrderValidationException(String message, Throwable cause, CondOrder ord)
    {
        super(message, cause, ord);
    }

    public CondOrderValidationException(Throwable cause, CondOrder ord)
    {
        super(cause, ord);
    }
}
