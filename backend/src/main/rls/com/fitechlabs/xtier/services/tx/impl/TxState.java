// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.tx.impl;

import java.util.Date;

// Referenced classes of package com.fitechlabs.xtier.services.tx.impl:
//            TxUtils

class TxState
{

    TxState(int i, long l)
    {
        if(!$assertionsDisabled && l < 0L)
            throw new AssertionError();
        if(!$assertionsDisabled && i != 0 && i != 3 && i != 8 && i != 1 && i != 2 && i != 7 && i != 4 && i != 9 && i != 5)
        {
            throw new AssertionError();
        } else
        {
            status = i;
            timestamp = l;
            return;
        }
    }

    int getStatus()
    {
        return status;
    }

    long getTimestamp()
    {
        return timestamp;
    }

    public String toString()
    {
        return "Tx state [status=" + TxUtils.getStrStatus(status) + ", time=" + new Date(timestamp) + ']';
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

    private int status;
    private long timestamp;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(TxState.class).desiredAssertionStatus();
    }
}
