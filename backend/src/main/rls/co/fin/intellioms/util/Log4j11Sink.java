// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   Log4j11Sink.java

package co.fin.intellioms.util;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.log.*;
import com.fitechlabs.xtier.services.log.adapters.LogSinkAdapter;
import org.apache.log4j.Category;
import org.apache.log4j.Priority;

public class Log4j11Sink extends LogSinkAdapter
{

    public Log4j11Sink(String name, LogFilter filter, LogErrorHandler handler)
    {
        super(name, null, filter, handler);
    }

    public Log4j11Sink(String name)
    {
        this(name, null, null);
    }

    public void close()
    {
    }

    public void add(LogRecord record)
    {
        synchronized(getMutex())
        {
            String ctgr = record.getLoggerCategory();
            Category logger = null;
            try
            {
                if(ctgr == null)
                    logger = Category.getRoot();
                else
                    logger = Category.getInstance(ctgr);
                LogFilter filter = getFilter();
                if(filter == null || filter.isLoggable(this, record))
                {
                    Priority level = logLevel2log4j(record.getLevel());
                    Throwable thrown = record.getThrowable();
                    if(thrown == null)
                        logger.log(level, record.getMessage());
                    else
                        logger.log(level, record.getMessage(), thrown);
                }
            }
            catch(Exception e)
            {
                getErrorHandler().onLogError(this, record, e);
            }
        }
    }

    private Priority logLevel2log4j(int logLevel)
    {
        switch(logLevel)
        {
        case 64: // '@'
            return Priority.ERROR;

        case 32: // ' '
            return Priority.WARN;

        case 4: // '\004'
            return Priority.INFO;

        case 8: // '\b'
            return Priority.INFO;

        case 16: // '\020'
            return Priority.INFO;

        case 2: // '\002'
            return Priority.DEBUG;
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

    static final boolean $assertionsDisabled = !com/ com /fin/intellioms/util/Log4j11Sink.desiredAssertionStatus();

}
