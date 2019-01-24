// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.log.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.log.LogRecord;
import java.util.Date;

// Referenced classes of package com.fitechlabs.xtier.services.log.impl:
//            LogUtils

class LogRecordImpl
    implements LogRecord
{

    LogRecordImpl(String s, int i, String s1, Throwable throwable, StackTraceElement stacktraceelement)
    {
        this(s, i, s1, throwable);
        if(stacktraceelement != null)
        {
            className = stacktraceelement.getClassName();
            methodName = stacktraceelement.getMethodName();
            fileName = stacktraceelement.getFileName();
            lineNum = stacktraceelement.getLineNumber();
        }
    }

    LogRecordImpl(String s, int i, String s1, Throwable throwable)
    {
        className = null;
        methodName = null;
        fileName = null;
        lineNum = 0;
        msg = s;
        ctgr = s1;
        level = i;
        thrown = throwable;
        timestamp = System.currentTimeMillis();
        thread = Thread.currentThread().getName();
        synchronized(mutex)
        {
            seqNum = seqNumGen++;
            timeElapsed = lastTimestamp != 0L ? timestamp - lastTimestamp : 0L;
            lastTimestamp = timestamp;
        }
    }

    public long getSeqNum()
    {
        return seqNum;
    }

    public String getMessage()
    {
        return msg;
    }

    public long getTimeElapsed()
    {
        return timeElapsed;
    }

    public long getTimeStamp()
    {
        return timestamp;
    }

    public Date getTimeStampDate()
    {
        if(date == null)
            date = new Date(timestamp);
        return date;
    }

    public int getLevel()
    {
        return level;
    }

    public String getThreadName()
    {
        return thread;
    }

    public Throwable getThrowable()
    {
        return thrown;
    }

    public String getLoggerCategory()
    {
        return ctgr;
    }

    public String getClassName()
    {
        return className;
    }

    public String getFileName()
    {
        return fileName;
    }

    public String getMethodName()
    {
        return methodName;
    }

    public int getLineNumber()
    {
        return lineNum;
    }

    public String toString()
    {
        return L10n.format("SRVC.LOG.RECORD.TXT1", new Object[] {
            new Date(timestamp), new Long(seqNum), thread, ctgr, msg, LogUtils.getLevelStr(level), new Long(timeElapsed), className, fileName, methodName, 
            new Integer(lineNum), thrown
        });
    }

    private static long seqNumGen = 0L;
    private static long lastTimestamp = 0L;
    private static Object mutex = new Object();
    private String msg;
    private int level;
    private String ctgr;
    private String thread;
    private long seqNum;
    private long timestamp;
    private Date date;
    private long timeElapsed;
    private Throwable thrown;
    private String className;
    private String methodName;
    private String fileName;
    private int lineNum;

}
