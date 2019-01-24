// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.log.sinks.jms.converters;

import com.fitechlabs.xtier.services.log.LogRecord;
import com.fitechlabs.xtier.services.log.sinks.jms.LogJmsConverter;
import java.io.Serializable;
import java.util.Date;
import javax.jms.*;

public class LogRecordConverter
    implements LogJmsConverter
{
    private static class SerLogRecord
        implements LogRecord, Serializable
    {

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
            return recStr;
        }

        private String msg;
        private int level;
        private String ctgr;
        private String thread;
        private long seqNum;
        private long timestamp;
        private long timeElapsed;
        private Throwable thrown;
        private String className;
        private String methodName;
        private String fileName;
        private int lineNum;
        private String recStr;
        private transient Date date;

        SerLogRecord()
        {
            className = null;
            methodName = null;
            fileName = null;
            lineNum = 0;
        }

        SerLogRecord(LogRecord logrecord)
        {
            className = null;
            methodName = null;
            fileName = null;
            lineNum = 0;
            msg = logrecord.getMessage();
            ctgr = logrecord.getLoggerCategory();
            level = logrecord.getLevel();
            thrown = logrecord.getThrowable();
            timestamp = logrecord.getTimeStamp();
            thread = logrecord.getThreadName();
            timeElapsed = logrecord.getTimeElapsed();
            className = logrecord.getClassName();
            methodName = logrecord.getMethodName();
            fileName = logrecord.getFileName();
            lineNum = logrecord.getLineNumber();
            seqNum = logrecord.getSeqNum();
            recStr = logrecord.toString();
        }
    }


    public LogRecordConverter()
    {
    }

    public Message convert(LogRecord logrecord, Session session)
        throws JMSException
    {
        ObjectMessage objectmessage = session.createObjectMessage();
        objectmessage.setObject(new SerLogRecord(logrecord));
        return objectmessage;
    }
}
