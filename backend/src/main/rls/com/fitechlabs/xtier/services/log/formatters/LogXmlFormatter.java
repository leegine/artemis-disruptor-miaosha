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

public class LogXmlFormatter
    implements LogFormatter
{

    public LogXmlFormatter()
    {
        formatter = DateFormat.getDateTimeInstance();
    }

    public String format(LogRecord logrecord)
    {
        if(!$assertionsDisabled && logrecord == null)
            throw new AssertionError();
        StringBuffer stringbuffer = new StringBuffer(logrecord.getMessage().length() * 4);
        stringbuffer.append("\t<record>" + Utils.NEW_LINE);
        stringbuffer.append("\t\t<seq-num>" + escape(Long.toString(logrecord.getSeqNum())) + "</seq-num>" + Utils.NEW_LINE);
        stringbuffer.append("\t\t<timestamp>" + escape(formatter.format(logrecord.getTimeStampDate())) + "</timestamp>" + Utils.NEW_LINE);
        stringbuffer.append("\t\t<thread>" + escape(logrecord.getThreadName()) + "</thread>" + Utils.NEW_LINE);
        stringbuffer.append("\t\t<logger>" + escape(logrecord.getLoggerCategory()) + "</logger>" + Utils.NEW_LINE);
        stringbuffer.append("\t\t<level>" + escape(LogUtils.getLevelStr(logrecord.getLevel())) + "</level>" + Utils.NEW_LINE);
        stringbuffer.append("\t\t<time-elapsed>" + escape(Long.toString(logrecord.getTimeElapsed())) + "</time-elapsed>" + Utils.NEW_LINE);
        String s = logrecord.getClassName();
        if(s != null)
        {
            stringbuffer.append("\t\t<class>" + escape(s) + "</class>" + Utils.NEW_LINE);
            stringbuffer.append("\t\t<method>" + escape(logrecord.getMethodName()) + "</method>" + Utils.NEW_LINE);
            stringbuffer.append("\t\t<file>" + escape(logrecord.getFileName()) + "</file>" + Utils.NEW_LINE);
            stringbuffer.append("\t\t<line-number>" + escape(Integer.toString(logrecord.getLineNumber())) + "</line-number>" + Utils.NEW_LINE);
        }
        stringbuffer.append("\t\t<message>" + Utils.NEW_LINE);
        stringbuffer.append("\t\t\t" + escape(logrecord.getMessage()) + Utils.NEW_LINE);
        stringbuffer.append("\t\t</message>" + Utils.NEW_LINE);
        Throwable throwable = logrecord.getThrowable();
        if(throwable != null)
        {
            stringbuffer.append("\t\t<throwable>" + Utils.NEW_LINE);
            stringbuffer.append("\t\t\t<exception>" + Utils.NEW_LINE);
            stringbuffer.append("\t\t\t\t" + escape(throwable.toString()) + Utils.NEW_LINE);
            stringbuffer.append("\t\t\t</exception>" + Utils.NEW_LINE);
            StackTraceElement astacktraceelement[] = throwable.getStackTrace();
            for(int i = 0; i < astacktraceelement.length; i++)
            {
                StackTraceElement stacktraceelement = astacktraceelement[i];
                String s1 = stacktraceelement.getFileName();
                stringbuffer.append("\t\t\t<element>" + Utils.NEW_LINE);
                stringbuffer.append("\t\t\t\t<trace-file>" + (s1 != null ? escape(s1) : "Unknown Source") + "</trace-file>" + Utils.NEW_LINE);
                stringbuffer.append("\t\t\t\t<trace-class>" + escape(stacktraceelement.getClassName()) + "</trace-class>" + Utils.NEW_LINE);
                stringbuffer.append("\t\t\t\t<trace-method>" + escape(stacktraceelement.getMethodName()) + "</trace-method>" + Utils.NEW_LINE);
                int j = stacktraceelement.getLineNumber();
                stringbuffer.append("\t\t\t\t<trace-line>" + escape(j >= 0 ? Integer.toString(j) : "unknown") + "</trace-line>" + Utils.NEW_LINE);
                stringbuffer.append("\t\t\t</element>" + Utils.NEW_LINE);
            }

            stringbuffer.append("\t\t</throwable>" + Utils.NEW_LINE);
        }
        stringbuffer.append("\t</record>" + Utils.NEW_LINE);
        return stringbuffer.toString();
    }

    private String escape(String s)
    {
        if(s == null || s.indexOf('&') == -1 && s.indexOf('<') == -1 && s.indexOf('>') == -1)
            return s;
        else
            return "<![CDATA[" + s + "]]>";
    }

    public String header()
    {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + Utils.NEW_LINE + "<log>";
    }

    public String footer()
    {
        return "</log>";
    }

    public String toString()
    {
        return L10n.format("SRVC.LOG.XML.FMT.TXT1");
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
        $assertionsDisabled = !(LogXmlFormatter.class).desiredAssertionStatus();
    }
}
