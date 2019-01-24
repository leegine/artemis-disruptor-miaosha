// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.tx;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;

public interface TxXarRecoverable
{

    public abstract XAResource getXar(String s);

    public abstract void dispose()
        throws XAException;
}
