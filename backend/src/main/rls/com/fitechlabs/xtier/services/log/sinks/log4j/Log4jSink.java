// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.log.sinks.log4j;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.log.*;
import com.fitechlabs.xtier.services.log.adapters.LogSinkAdapter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Log4jSink extends LogSinkAdapter
{

    public Log4jSink(String s, LogFilter logfilter, LogErrorHandler logerrorhandler)
    {
        super(s, null, logfilter, logerrorhandler);
    }

    public Log4jSink(String s)
    {
        this(s, null, null);
    }

    public void close()
    {
    }

    public void add(LogRecord logrecord)
    {
        synchronized(getMutex())
        {
            String s = logrecord.getLoggerCategory();
            Object obj1 = null;
            try
            {
                Logger logger;
                if(s == null)
                    logger = Logger.getRootLogger();
                else
                    logger = Logger.getLogger(s);
                LogFilter logfilter = getFilter();
                if(logfilter == null || logfilter.isLoggable(this, logrecord))
                {
                    Level level = logLevel2log4j(logrecord.getLevel());
                    Throwable throwable = logrecord.getThrowable();
                    if(throwable == null)
                        logger.log(level, logrecord.getMessage());
                    else
                        logger.log(level, logrecord.getMessage(), throwable);
                }
            }
            catch(Exception exception)
            {
                getErrorHandler().onLogError(this, logrecord, exception);
            }
        }
    }

    private Level logLevel2log4j(int i)
    {
        switch(i)
        {
        case 64: // '@'
            return Level.ERROR;

        case 32: // ' '
            return Level.WARN;

        case 4: // '\004'
            return Level.INFO;

        case 8: // '\b'
            return Level.INFO;

        case 16: // '\020'
            return Level.INFO;

        case 2: // '\002'
            return Level.DEBUG;
        }
        if(!$assertionsDisabled)
            throw new AssertionError();
        else
            return null;
    }

    public String toString()
    {
        return L10n.format("SRVC.LOG.LSINK.TXT1", getName(), getFilter(), getErrorHandler());
    }

    static Class _mthclass$(String s)
    {
        try
        {
            return Class.forName(s);
        }
        catch(ClassNotFoundException classnotfoundexception)
        {
            throw (new NoClassDefFoundError()).initCause(classnotfoundexception);
        }
    }

    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(Log4jSink.class).desiredAssertionStatus();
    }
}
