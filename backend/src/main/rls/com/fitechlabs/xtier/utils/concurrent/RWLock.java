// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.utils.concurrent;


public interface RWLock
{

    public abstract void acquireRead();

    public abstract boolean acquireRead(long l);

    public abstract void acquireWrite();

    public abstract boolean acquireWrite(long l);

    public abstract void releaseRead();

    public abstract void releaseWrite();
}
