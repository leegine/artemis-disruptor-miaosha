// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   Startable.java

package co.fin.intellioms.util;


// Referenced classes of package com.com.fin.intellioms.util:
//            InitializationException

public interface Startable
{

    public abstract void start()
        throws InitializationException;

    public abstract void stop();
}
