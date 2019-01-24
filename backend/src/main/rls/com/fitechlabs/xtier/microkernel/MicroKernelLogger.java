// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.microkernel;


public interface MicroKernelLogger
{

    public abstract void log(Object obj);

    public abstract void log(Object obj, Throwable throwable);

    public abstract void error(Object obj);

    public abstract void error(Object obj, Throwable throwable);

    public abstract void trace(Object obj);

    public abstract void trace(Object obj, Throwable throwable);

    public abstract void info(Object obj);

    public abstract void info(Object obj, Throwable throwable);

    public abstract void warning(Object obj);

    public abstract void warning(Object obj, Throwable throwable);

    public abstract void debug(Object obj);

    public abstract void debug(Object obj, Throwable throwable);

    public abstract MicroKernelLogger getLogger(String s);
}
