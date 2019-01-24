// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.log.adapters;

import com.fitechlabs.xtier.services.log.*;
import com.fitechlabs.xtier.services.log.handlers.LogDefaultErrorHandler;
import com.fitechlabs.xtier.services.log.impl.LogUtils;
import com.fitechlabs.xtier.utils.ArgAssert;

public abstract class LogSinkAdapter
    implements LogSink
{

    public LogSinkAdapter(String s, LogFormatter logformatter, LogFilter logfilter, LogErrorHandler logerrorhandler)
    {
        mask = 126;
        filter = null;
        formatter = null;
        handler = null;
        mutex = new Object();
        ArgAssert.nullArg(s, "name");
        name = s;
        formatter = logformatter;
        filter = logfilter;
        handler = ((LogErrorHandler) (logerrorhandler == null ? ((LogErrorHandler) (new LogDefaultErrorHandler())) : logerrorhandler));
    }

    protected Object getMutex()
    {
        return mutex;
    }

    public String getName()
    {
        return name;
    }

    public LogErrorHandler getErrorHandler()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return handler;
        Exception exception;
        exception;
        throw exception;
    }

    public LogFormatter getFormatter()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return formatter;
        Exception exception;
        exception;
        throw exception;
    }

    public LogFilter getFilter()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return filter;
        Exception exception;
        exception;
        throw exception;
    }

    public int getLevelMask()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return mask;
        Exception exception;
        exception;
        throw exception;
    }

    public void setLevelMask(int i)
    {
        LogUtils.checkMask(i);
        synchronized(mutex)
        {
            mask = i;
        }
    }

    public void setLevelFrom(int i)
    {
        LogUtils.checkLevel(i);
        synchronized(mutex)
        {
            mask = LogUtils.convertToMask(i);
        }
    }

    private int mask;
    private LogFilter filter;
    private LogFormatter formatter;
    private LogErrorHandler handler;
    private String name;
    private Object mutex;
}
