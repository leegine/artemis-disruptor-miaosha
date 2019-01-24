// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   AccountsInfo.java

package co.fin.intellioms.account;


// Referenced classes of package com.com.fin.intellioms.account:
//            AccountIDRange

public class AccountsInfo
{

    public AccountsInfo(AccountIDRange range)
    {
        if(!$assertionsDisabled && range == null)
        {
            throw new AssertionError("Account ID range is null.");
        } else
        {
            this.range = range;
            return;
        }
    }

    public AccountIDRange getRange()
    {
        return range;
    }

    public String toString()
    {
        return (new StringBuilder()).append("AccountsInfo [range=").append(range).append(']').toString();
    }

    private final AccountIDRange range;
    static final boolean $assertionsDisabled = !com/ com /fin/intellioms/account/AccountsInfo.desiredAssertionStatus();

}
