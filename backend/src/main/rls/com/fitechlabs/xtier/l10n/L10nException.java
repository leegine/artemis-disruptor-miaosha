// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.l10n;


public class L10nException extends RuntimeException
{

    public L10nException(String s)
    {
        super(s);
    }

    public L10nException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public String toString()
    {
        return "L10n error: " + getMessage();
    }
}
