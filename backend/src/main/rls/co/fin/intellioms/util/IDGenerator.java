// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   IDGenerator.java

package co.fin.intellioms.util;


// Referenced classes of package com.com.fin.intellioms.util:
//            IDGeneratorException

public interface IDGenerator
{

    public abstract long next()
        throws IDGeneratorException;

    public abstract Long nextLong()
        throws IDGeneratorException;

    public abstract long current();
}
