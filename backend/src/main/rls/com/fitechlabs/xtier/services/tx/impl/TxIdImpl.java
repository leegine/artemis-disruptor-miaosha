// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.tx.impl;

import com.fitechlabs.xtier.utils.Utils;
import javax.transaction.xa.Xid;

class TxIdImpl
    implements Xid
{

    TxIdImpl(int i, byte abyte0[], byte abyte1[])
    {
        bqual = null;
        gtrid = null;
        if(!$assertionsDisabled && (abyte0 == null || abyte1 == null))
        {
            throw new AssertionError();
        } else
        {
            formatId = i;
            bqual = abyte0;
            gtrid = abyte1;
            return;
        }
    }

    public int getFormatId()
    {
        return formatId;
    }

    public byte[] getBranchQualifier()
    {
        return bqual;
    }

    public byte[] getGlobalTransactionId()
    {
        return gtrid;
    }

    public String toString()
    {
        if(!$assertionsDisabled)
            throw new AssertionError();
        else
            return "XID [format-id=" + formatId + ", bqual=" + Utils.arr2Str(bqual) + ", gtrid=" + Utils.arr2Str(gtrid) + ']';
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

    private int formatId;
    private byte bqual[];
    private byte gtrid[];
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(TxIdImpl.class).desiredAssertionStatus();
    }
}
