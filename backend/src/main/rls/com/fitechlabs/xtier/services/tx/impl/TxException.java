// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.tx.impl;

import com.fitechlabs.xtier.l10n.L10n;

class TxException extends Exception
{

    public TxException(String s)
    {
        super(s);
    }

    public TxException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public String toString()
    {
        return L10n.format("SRVC.TX.TXT1", getMessage());
    }
}
