// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.tx.adapters;

import com.fitechlabs.xtier.services.tx.TxXarRecoveryHandler;
import javax.transaction.xa.*;

public class TxXarRecoveryHandlerAdapter
    implements TxXarRecoveryHandler
{

    public TxXarRecoveryHandlerAdapter()
    {
    }

    public void handleRecovery(Xid xid, XAResource xaresource, int i)
        throws XAException
    {
        if(!$assertionsDisabled && xid == null)
            throw new AssertionError();
        if(!$assertionsDisabled && xaresource == null)
            throw new AssertionError();
        if(!$assertionsDisabled && i != 2 && i != 1 && i != 3)
            throw new AssertionError();
        if(i == 2)
            xaresource.forget(xid);
        else
        if(i == 1)
            xaresource.commit(xid, false);
        else
        if(i == 3)
            xaresource.rollback(xid);
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

    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(TxXarRecoveryHandlerAdapter.class).desiredAssertionStatus();
    }
}
