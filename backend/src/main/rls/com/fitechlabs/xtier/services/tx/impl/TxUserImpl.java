// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.tx.impl;

import javax.transaction.*;

final class TxUserImpl
    implements UserTransaction
{

    static UserTransaction getInstance()
    {
        if(!$assertionsDisabled && singleton == null)
            throw new AssertionError();
        else
            return singleton;
    }

    static UserTransaction newInstance(TransactionManager transactionmanager)
    {
        if(!$assertionsDisabled && singleton != null)
        {
            throw new AssertionError();
        } else
        {
            singleton = new TxUserImpl(transactionmanager);
            return singleton;
        }
    }

    static void shutdown()
    {
        singleton = null;
    }

    private TxUserImpl(TransactionManager transactionmanager)
    {
        xaTm = transactionmanager;
    }

    public void begin()
        throws NotSupportedException, SystemException
    {
        xaTm.begin();
    }

    public void commit()
        throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException
    {
        xaTm.commit();
    }

    public void rollback()
        throws SecurityException, IllegalStateException, SystemException
    {
        xaTm.rollback();
    }

    public void setRollbackOnly()
        throws IllegalStateException, SystemException
    {
        xaTm.setRollbackOnly();
    }

    public int getStatus()
        throws SystemException
    {
        return xaTm.getStatus();
    }

    public void setTransactionTimeout(int i)
        throws SystemException
    {
        xaTm.setTransactionTimeout(i);
    }

    static Class _mthclass$(String s)
    {
        try
        {
            return Class.forName(s);
        }
        catch(ClassNotFoundException classnotfoundexception)
        {
            throw (new NoClassDefFoundError()).initCause(classnotfoundexception);
        }
    }

    private static TxUserImpl singleton;
    private TransactionManager xaTm;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(TxUserImpl.class).desiredAssertionStatus();
    }
}
