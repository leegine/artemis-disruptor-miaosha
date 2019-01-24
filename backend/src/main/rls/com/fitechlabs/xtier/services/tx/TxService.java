// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.tx;

import com.fitechlabs.xtier.kernel.KernelService;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

public interface TxService
    extends KernelService
{

    public abstract TransactionManager getTransactionManager();

    public abstract UserTransaction userTx();

    public static final int XID_FORMAT_ID = 12609;
}
