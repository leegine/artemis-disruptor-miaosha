// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.objpool;

import com.fitechlabs.xtier.kernel.KernelServiceException;
import com.fitechlabs.xtier.l10n.L10n;

public class ObjectPoolException extends KernelServiceException
{

    public ObjectPoolException(String s)
    {
        super(s);
    }

    public ObjectPoolException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public String toString()
    {
        return L10n.format("SRVC.OBJPOOL.TXT2", getMessage());
    }
}
