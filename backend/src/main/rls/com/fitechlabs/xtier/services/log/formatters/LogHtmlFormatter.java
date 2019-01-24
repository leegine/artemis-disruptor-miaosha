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

public class LogHtmlFormatter
    implements LogFormatter
{

    public LogHtmlFormatter()
    {
        formatter = DateFormat.getDateTimeInstance();
    }

    public String format(LogRecord logrecord)
    {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("\t\t\t<tr>" + Utils.NEW_LINE);
        stringbuffer.append("\t\t\t\t<td style='border: 1px dotted #aaa' align=\"center\" valign=\"top\">" + Long.toString(logrecord.getSeqNum()) + "</td>" + Utils.NEW_LINE);
        stringbuffer.append("\t\t\t\t<td style='border: 1px dotted #aaa' align=\"center\" valign=\"top\">" + formatter.format(logrecord.getTimeStampDate()) + "</td>" + Utils.NEW_LINE);
        stringbuffer.append("\t\t\t\t<td style='border: 1px dotted #aaa' align=\"center\" valign=\"top\">" + logrecord.getThreadName() + "</td>" + Utils.NEW_LINE);
        stringbuffer.append("\t\t\t\t<td style='border: 1px dotted #aaa' align=\"center\" valign=\"top\">" + logrecord.getLoggerCategory() + "</td>" + Utils.NEW_LINE);
        int i = logrecord.getLevel();
        if(i == 64 || i == 32)
            stringbuffer.append("\t\t\t\t<td style='border: 1px dotted #aaa' align=\"center\" valign=\"top\"><font color=red>" + LogUtils.getLevelStr(logrecord.getLevel()) + "</font></td>" + Utils.NEW_LINE);
        else
            stringbuffer.append("\t\t\t\t<td style='border: 1px dotted #aaa' align=\"center\" valign=\"top\">" + LogUtils.getLevelStr(logrecord.getLevel()) + "</td>" + Utils.NEW_LINE);
        String s = logrecord.getClassName();
        if(s != null)
        {
            stringbuffer.append("\t\t\t\t<td style='border: 1px dotted #aaa' align=\"center\" valign=\"top\">" + s + "</td>" + Utils.NEW_LINE);
            stringbuffer.append("\t\t\t\t<td style='border: 1px dotted #aaa' align=\"center\" valign=\"top\">" + logrecord.getMethodName() + "</td>" + Utils.NEW_LINE);
            stringbuffer.append("\t\t\t\t<td style='border: 1px dotted #aaa' align=\"center\" valign=\"top\">" + logrecord.getFileName() + "</td>" + Utils.NEW_LINE);
            stringbuffer.append("\t\t\t\t<td style='border: 1px dotted #aaa' align=\"center\" valign=\"top\">" + logrecord.getLineNumber() + "</td>" + Utils.NEW_LINE);
        } else
        {
            stringbuffer.append("\t\t\t\t<td style='border: 1px dotted #aaa' align=\"center\" valign=\"top\">&nbsp;</td>" + Utils.NEW_LINE);
            stringbuffer.append("\t\t\t\t<td style='border: 1px dotted #aaa' align=\"center\" valign=\"top\">&nbsp;</td>" + Utils.NEW_LINE);
            stringbuffer.append("\t\t\t\t<td style='border: 1px dotted #aaa' align=\"center\" valign=\"top\">&nbsp;</td>" + Utils.NEW_LINE);
            stringbuffer.append("\t\t\t\t<td style='border: 1px dotted #aaa' align=\"center\" valign=\"top\">&nbsp;</td>" + Utils.NEW_LINE);
        }
        stringbuffer.append("\t\t\t\t<td style='border: 1px dotted #aaa' align=\"center\" valign=\"top\">" + Long.toString(logrecord.getTimeElapsed()) + "</td>" + Utils.NEW_LINE);
        stringbuffer.append("\t\t\t\t<td style='border: 1px dotted #aaa' align=\"left\" valign=\"top\">" + logrecord.getMessage() + "</td>" + Utils.NEW_LINE);
        stringbuffer.append("\t\t\t\t<td style='border: 1px dotted #aaa' align=\"left\" valign=\"top\">");
        Throwable throwable = logrecord.getThrowable();
        if(throwable != null)
        {
            stringbuffer.append(throwable.toString() + "<br>" + Utils.NEW_LINE);
            StackTraceElement astacktraceelement[] = throwable.getStackTrace();
            for(int j = 0; j < astacktraceelement.length; j++)
            {
                StackTraceElement stacktraceelement = astacktraceelement[j];
                stringbuffer.append("\t\t\t\t\t at " + stacktraceelement.getClassName() + '.' + stacktraceelement.getMethodName());
                String s1 = stacktraceelement.getFileName();
                if(s1 == null)
                {
                    stringbuffer.append("(Unknown Source)");
                } else
                {
                    int k = stacktraceelement.getLineNumber();
                    if(k < 0)
                        stringbuffer.append("(" + s1 + ")");
                    else
                        stringbuffer.append("(" + s1 + ":" + Integer.toString(k) + ")");
                }
                stringbuffer.append("<br>");
                stringbuffer.append(Utils.NEW_LINE);
            }

            stringbuffer.append("\t\t\t\t");
        } else
        {
            stringbuffer.append("&nbsp;");
        }
        stringbuffer.append("</td>" + Utils.NEW_LINE);
        stringbuffer.append("\t\t\t</tr>" + Utils.NEW_LINE);
        return stringbuffer.toString();
    }

    public String header()
    {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("<!doctype HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\" >" + Utils.NEW_LINE);
        stringbuffer.append("<html>" + Utils.NEW_LINE);
        stringbuffer.append("\t<head>" + Utils.NEW_LINE);
        stringbuffer.append("\t</head>" + Utils.NEW_LINE);
        stringbuffer.append("\t<body>" + Utils.NEW_LINE);
        stringbuffer.append("\t<font face=\"Arial, Helvetica, sans-serif\" size=\"-2\">" + Utils.NEW_LINE);
        stringbuffer.append("\t\t<table style='border: 1px dotted #aaa' width='100%'  cellpadding='1' cellspacing='0'>" + Utils.NEW_LINE);
        stringbuffer.append("\t\t\t<tr bgcolor='#efefef'>" + Utils.NEW_LINE);
        stringbuffer.append("\t\t\t\t<td style='border: 1px dotted #aaa' align=\"center\" valign=\"top\"><b>Number</b></td>" + Utils.NEW_LINE);
        stringbuffer.append("\t\t\t\t<td style='border: 1px dotted #aaa' align=\"center\" valign=\"top\"><b>Timestamp</b></td>" + Utils.NEW_LINE);
        stringbuffer.append("\t\t\t\t<td style='border: 1px dotted #aaa' align=\"center\" valign=\"top\"><b>Thread</b></td>" + Utils.NEW_LINE);
        stringbuffer.append("\t\t\t\t<td style='border: 1px dotted #aaa' align=\"center\" valign=\"top\"><b>Category</b></td>" + Utils.NEW_LINE);
        stringbuffer.append("\t\t\t\t<td style='border: 1px dotted #aaa' align=\"center\" valign=\"top\"><b>Level</b></td>" + Utils.NEW_LINE);
        stringbuffer.append("\t\t\t\t<td style='border: 1px dotted #aaa' align=\"center\" valign=\"top\"><b>Class</b></td>" + Utils.NEW_LINE);
        stringbuffer.append("\t\t\t\t<td style='border: 1px dotted #aaa' align=\"center\" valign=\"top\"><b>Method</b></td>" + Utils.NEW_LINE);
        stringbuffer.append("\t\t\t\t<td style='border: 1px dotted #aaa' align=\"center\" valign=\"top\"><b>File</b></td>" + Utils.NEW_LINE);
        stringbuffer.append("\t\t\t\t<td style='border: 1px dotted #aaa' align=\"center\" valign=\"top\"><b>Line</b></td>" + Utils.NEW_LINE);
        stringbuffer.append("\t\t\t\t<td style='border: 1px dotted #aaa' align=\"center\" valign=\"top\"><b>Time Since Last</b></td>" + Utils.NEW_LINE);
        stringbuffer.append("\t\t\t\t<td style='border: 1px dotted #aaa' align=\"center\" valign=\"top\"><b>Message</b></td></td>" + Utils.NEW_LINE);
        stringbuffer.append("\t\t\t\t<td style='border: 1px dotted #aaa' align=\"center\" valign=\"top\"><b>Error Traces</b></td>" + Utils.NEW_LINE);
        stringbuffer.append("\t\t\t</tr>" + Utils.NEW_LINE);
        return stringbuffer.toString();
    }

    public String footer()
    {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("\t\t</table>" + Utils.NEW_LINE);
        stringbuffer.append("\t</font>" + Utils.NEW_LINE);
        stringbuffer.append("\t</body>" + Utils.NEW_LINE);
        stringbuffer.append("</html>" + Utils.NEW_LINE);
        return stringbuffer.toString();
    }

    public String toString()
    {
        return L10n.format("SRVC.LOG.HTML.FMT.TXT1");
    }

    private static final String CSS = "style='border: 1px dotted #aaa'";
    private static final String TD_CENTER = "\t\t\t\t<td style='border: 1px dotted #aaa' align=\"center\" valign=\"top\">";
    private static final String TD_LEFT = "\t\t\t\t<td style='border: 1px dotted #aaa' align=\"left\" valign=\"top\">";
    private DateFormat formatter;
}
