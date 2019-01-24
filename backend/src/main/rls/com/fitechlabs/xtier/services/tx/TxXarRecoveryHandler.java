// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.tx;

import javax.transaction.xa.*;

public interface TxXarRecoveryHandler
{

    public abstract void handleRecovery(Xid xid, XAResource xaresource, int i)
        throws XAException;

    public static final int RCVR_PREP = 1;
    public static final int RCVR_HEUR = 2;
    public static final int RCVR_RB = 3;
}
