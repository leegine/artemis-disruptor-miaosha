// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   BasicFailoverCallback.java

package co.fin.intellioms.dbutil;


// Referenced classes of package com.com.fin.intellioms.dbutil:
//            PoolFailoverCallback

public class BasicFailoverCallback
    implements PoolFailoverCallback
{

    public BasicFailoverCallback()
    {
    }

    public int allowFailover(int evt, String curPool, String nextPool)
    {
        return nextPool == null ? 1 : 0;
    }
}
