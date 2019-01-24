// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   Log.java

package co.fin.intellioms.util;

import org.apache.log4j.Category;
import org.apache.log4j.Priority;

public class Log
{

    private Log(Category log)
    {
        this.log = log;
    }

    public static Log getLogger(Class cl)
    {
        return getLogger(cl.getName());
    }

    public static Log getLogger(String category)
    {
        return new Log(Category.getInstance(category));
    }

    public boolean isDebug()
    {
        return log.isDebugEnabled();
    }

    public boolean isTrace()
    {
        return log.isInfoEnabled();
    }

    public boolean isInfo()
    {
        return log.isInfoEnabled();
    }

    public boolean isWarn()
    {
        return log.isEnabledFor(Priority.WARN);
    }

    public boolean isError()
    {
        return log.isEnabledFor(Priority.ERROR);
    }

    public void debug(String msg)
    {
        log.debug(msg);
    }

    public void debug(String msg, Throwable t)
    {
        log.debug(msg, t);
    }

    public void trace(String msg)
    {
        log.info(msg);
    }

    public void trace(String msg, Throwable t)
    {
        log.info(msg, t);
    }

    public void info(String msg)
    {
        log.info(msg);
    }

    public void info(String msg, Throwable t)
    {
        log.info(msg, t);
    }

    public void warn(String msg)
    {
        log.warn(msg);
    }

    public void warn(String msg, Throwable t)
    {
        log.warn(msg, t);
    }

    public void error(String msg)
    {
        log.error(msg);
    }

    public void error(String msg, Throwable t)
    {
        log.error(msg, t);
    }

    private final Category log;
}
