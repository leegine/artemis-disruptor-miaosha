// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.db.multipool;

import com.fitechlabs.xtier.services.objpool.ObjectPoolException;

class InvalidConnectionException extends ObjectPoolException
{

    public InvalidConnectionException(String s)
    {
        super(s);
    }

    public InvalidConnectionException(String s, Throwable throwable)
    {
        super(s, throwable);
    }
}
