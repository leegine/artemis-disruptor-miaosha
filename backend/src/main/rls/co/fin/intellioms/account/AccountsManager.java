// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   AccountsManager.java

package co.fin.intellioms.account;

import java.util.List;

// Referenced classes of package com.com.fin.intellioms.account:
//            AccountException, AccountIDRange

public interface AccountsManager
{

    public abstract List getAllAccountIds()
        throws AccountException;

    public abstract AccountIDRange getAccountsCount()
        throws AccountException;
}
