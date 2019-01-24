// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   InvalidConnectionException.java

package co.fin.intellioms.dbutil;

import com.fitechlabs.xtier.services.objpool.ObjectPoolException;

class InvalidConnectionException extends ObjectPoolException
{

    public InvalidConnectionException(String msg)
    {
        super(msg);
    }

    public InvalidConnectionException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
}
