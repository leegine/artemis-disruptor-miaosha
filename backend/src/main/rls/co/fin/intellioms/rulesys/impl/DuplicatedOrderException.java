// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   DuplicatedOrderException.java

package co.fin.intellioms.rulesys.impl;

import co.fin.intellioms.rulesys.CondOrder;
import co.fin.intellioms.rulesys.CondOrderException;

public class DuplicatedOrderException extends CondOrderException
{

    public DuplicatedOrderException(CondOrder ord)
    {
        super(ord);
    }

    public DuplicatedOrderException(String message, CondOrder ord)
    {
        super(message, ord);
    }

    public DuplicatedOrderException(String message, Throwable cause, CondOrder ord)
    {
        super(message, cause, ord);
    }

    public DuplicatedOrderException(Throwable cause, CondOrder ord)
    {
        super(cause, ord);
    }
}
