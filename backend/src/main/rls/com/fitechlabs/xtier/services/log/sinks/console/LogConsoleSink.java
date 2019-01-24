// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.log.sinks.console;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.log.*;
import com.fitechlabs.xtier.services.log.adapters.LogSinkAdapter;
import com.fitechlabs.xtier.utils.ArgAssert;
import java.io.PrintStream;

public class LogConsoleSink extends LogSinkAdapter
{

    public LogConsoleSink(String s, LogFormatter logformatter, LogFilter logfilter, LogErrorHandler logerrorhandler)
    {
        super(s, logformatter, logfilter, logerrorhandler);
        ArgAssert.nullArg(logformatter, "formatter");
        String s1 = getFormatter().header();
        if(s1 != null)
            System.out.println(s1);
    }

    public LogConsoleSink(String s, LogFormatter logformatter)
    {
        this(s, logformatter, null, null);
    }

    public void close()
    {
        String s = getFormatter().footer();
        if(s != null)
            System.out.println(s);
    }

    public void add(LogRecord logrecord)
    {
        synchronized(getMutex())
        {
            try
            {
                LogFilter logfilter = getFilter();
                if(logfilter == null || logfilter.isLoggable(this, logrecord))
                {
                    String s = getFormatter().format(logrecord);
                    switch(logrecord.getLevel())
                    {
                    case 32: // ' '
                    case 64: // '@'
                        System.err.println(s);
                        break;

                    case 2: // '\002'
                    case 4: // '\004'
                    case 8: // '\b'
                    case 16: // '\020'
                        System.out.println(s);
                        break;

                    default:
                        if(!$assertionsDisabled)
                            throw new AssertionError("Invalid record level: " + logrecord.getLevel());
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
        return L10n.format("SRVC.LOG.CSINK.TXT1", getName(), getFormatter(), getFilter(), getErrorHandler());
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
        $assertionsDisabled = !(LogConsoleSink.class).desiredAssertionStatus();
    }
}
