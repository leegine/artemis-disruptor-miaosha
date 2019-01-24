// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.log;


public interface Logger
{

    public abstract int getLevelMask();

    public abstract void setLevelMask(int i);

    public abstract void setLevelFrom(int i);

    public abstract void rawLog(int i, Object obj, Throwable throwable);

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

    public abstract Logger getLogger(String s);

    public abstract String getCategory();

    public abstract void setTraceLocation(boolean flag);

    public abstract boolean isTraceLocation();

    public static final int DEBUG = 2;
    public static final int TRACE = 4;
    public static final int INFO = 8;
    public static final int LOG = 16;
    public static final int WARNING = 32;
    public static final int ERROR = 64;
    public static final int ALL_MASK = 126;
    public static final int NONE_MASK = 0;
}
