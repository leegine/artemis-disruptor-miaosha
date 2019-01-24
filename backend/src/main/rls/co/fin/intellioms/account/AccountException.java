// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   AccountException.java

package co.fin.intellioms.account;


public class AccountException extends Exception
{

    public AccountException()
    {
    }

    public AccountException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public AccountException(String message)
    {
        super(message);
    }

    public AccountException(Throwable cause)
    {
        super(cause);
    }
}
