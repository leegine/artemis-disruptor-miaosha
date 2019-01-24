// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.log.formatters;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.log.LogFormatter;
import com.fitechlabs.xtier.services.log.LogRecord;
import com.fitechlabs.xtier.services.log.impl.LogUtils;
import com.fitechlabs.xtier.utils.Utils;
import java.text.DateFormat;

public class LogConsoleFormatter
    implements LogFormatter
{

    public LogConsoleFormatter()
    {
        formatter = DateFormat.getDateTimeInstance();
    }

    public String format(LogRecord logrecord)
    {
        if(!$assertionsDisabled && logrecord == null)
            throw new AssertionError();
        StringBuffer stringbuffer = new StringBuffer(logrecord.getMessage().length() * 2);
        stringbuffer.append(formatter.format(logrecord.getTimeStampDate()));
        stringbuffer.append(" ");
        stringbuffer.append("<" + logrecord.getSeqNum() + ">");
        stringbuffer.append("<" + LogUtils.getLevelStr(logrecord.getLevel()) + ":" + logrecord.getThreadName() + ">");
        stringbuffer.append("<" + logrecord.getLoggerCategory() + ">");
        String s = logrecord.getClassName();
        if(s != null)
        {
            stringbuffer.append("<" + s + ":" + logrecord.getMethodName() + ">");
            stringbuffer.append("<" + logrecord.getFileName() + ":" + logrecord.getLineNumber() + ">");
        }
        stringbuffer.append(" ");
        stringbuffer.append(logrecord.getMessage());
        Throwable throwable = logrecord.getThrowable();
        if(throwable != null)
            stringbuffer.append(Utils.NEW_LINE + Utils.getStackTrace(throwable));
        return stringbuffer.toString();
    }

    public String header()
    {
        return null;
    }

    public String footer()
    {
        return null;
    }

    public String toString()
    {
        return L10n.format("SRVC.LOG.CNSL.FMT.TXT1");
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

    private DateFormat formatter;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(LogConsoleFormatter.class).desiredAssertionStatus();
    }
}
