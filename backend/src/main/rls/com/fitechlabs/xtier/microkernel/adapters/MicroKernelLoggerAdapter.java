// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.microkernel.adapters;

import com.fitechlabs.xtier.microkernel.MicroKernelLogger;

public abstract class MicroKernelLoggerAdapter
    implements MicroKernelLogger
{

    public MicroKernelLoggerAdapter()
    {
    }

    protected abstract void say(int i, String s, Object obj, Throwable throwable);

    public void log(Object obj)
    {
        say(1, "log", obj, null);
    }

    public void log(Object obj, Throwable throwable)
    {
        say(1, "log", obj, throwable);
    }

    public void error(Object obj)
    {
        say(4, "error", obj, null);
    }

    public void error(Object obj, Throwable throwable)
    {
        say(4, "error", obj, throwable);
    }

    public void trace(Object obj)
    {
        say(5, "trace", obj, null);
    }

    public void trace(Object obj, Throwable throwable)
    {
        say(5, "trace", obj, throwable);
    }

    public void info(Object obj)
    {
        say(3, "info", obj, null);
    }

    public void info(Object obj, Throwable throwable)
    {
        say(3, "info", obj, null);
    }

    public void warning(Object obj)
    {
        say(6, "warning", obj, null);
    }

    public void warning(Object obj, Throwable throwable)
    {
        say(6, "warning", obj, throwable);
    }

    public void debug(Object obj)
    {
        say(2, "debug", obj, null);
    }

    public void debug(Object obj, Throwable throwable)
    {
        say(2, "debug", obj, throwable);
    }

    protected static final int LOG = 1;
    protected static final int DEBUG = 2;
    protected static final int INFO = 3;
    protected static final int ERROR = 4;
    protected static final int TRACE = 5;
    protected static final int WARNING = 6;
}
