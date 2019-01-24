// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.log.sinks.microkernel;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.microkernel.MicroKernelLogger;
import com.fitechlabs.xtier.services.log.*;
import com.fitechlabs.xtier.services.log.adapters.LogSinkAdapter;
import com.fitechlabs.xtier.utils.ArgAssert;

public class LogMicroKernelSink extends LogSinkAdapter
{

    public LogMicroKernelSink(String s, LogFormatter logformatter, LogFilter logfilter, LogErrorHandler logerrorhandler)
    {
        super(s, logformatter, logfilter, logerrorhandler);
        logger = null;
        ArgAssert.nullArg(logformatter, "formatter");
    }

    public void close()
    {
        String s = getFormatter().footer();
        if(s != null)
            logger.info(s);
    }

    public void setLogger(MicroKernelLogger microkernellogger)
    {
        if(!$assertionsDisabled && microkernellogger == null)
            throw new AssertionError();
        logger = microkernellogger;
        String s = getFormatter().header();
        if(s != null)
            microkernellogger.info(s);
    }

    public void add(LogRecord logrecord)
    {
        if(!$assertionsDisabled && logger == null)
            throw new AssertionError();
        synchronized(getMutex())
        {
            try
            {
                LogFilter logfilter = getFilter();
                if(logfilter == null || logfilter.isLoggable(this, logrecord))
                {
                    String s = getFormatter().format(logrecord);
                    Throwable throwable = logrecord.getThrowable();
                    int i = logrecord.getLevel();
                    switch(i)
                    {
                    case 2: // '\002'
                        logger.debug(s, throwable);
                        break;

                    case 4: // '\004'
                        logger.trace(s, throwable);
                        break;

                    case 16: // '\020'
                        logger.log(s, throwable);
                        break;

                    case 32: // ' '
                        logger.warning(s, throwable);
                        break;

                    case 64: // '@'
                        logger.error(s, throwable);
                        break;

                    case 8: // '\b'
                        logger.info(s, throwable);
                        break;

                    default:
                        if(!$assertionsDisabled)
                            throw new AssertionError("Invalid level: " + i);
                        break;
                    }
                }
            }
            catch(Exception exception)
            {
                getErrorHandler().onLogError(this, logrecord, exception);
            }
        }
    }

    public String toString()
    {
        return L10n.format("SRVC.LOG.MKSINK.TXT1", getName(), getFormatter(), getFilter(), getErrorHandler());
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

    private MicroKernelLogger logger;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(LogMicroKernelSink.class).desiredAssertionStatus();
    }
}
