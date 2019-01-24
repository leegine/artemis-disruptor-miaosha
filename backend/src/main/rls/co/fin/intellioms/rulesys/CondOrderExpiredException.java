// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   CondOrderExpiredException.java

package co.fin.intellioms.rulesys;


// Referenced classes of package com.com.fin.intellioms.rulesys:
//            CondOrderException, CondOrder

public class CondOrderExpiredException extends CondOrderException
{

    public CondOrderExpiredException(String message, Throwable cause, CondOrder ord)
    {
        super(message, cause, ord);
    }

    public CondOrderExpiredException(String message, CondOrder ord)
    {
        super(message, ord);
    }
}
