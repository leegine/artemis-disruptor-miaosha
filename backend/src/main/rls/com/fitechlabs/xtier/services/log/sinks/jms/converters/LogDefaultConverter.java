// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.log.sinks.jms.converters;

import com.fitechlabs.xtier.services.log.LogRecord;
import com.fitechlabs.xtier.services.log.sinks.jms.LogJmsConverter;
import javax.jms.*;

public class LogDefaultConverter
    implements LogJmsConverter
{

    public LogDefaultConverter()
    {
    }

    public Message convert(LogRecord logrecord, Session session)
        throws JMSException
    {
        ObjectMessage objectmessage = session.createObjectMessage();
        objectmessage.setStringProperty("message", logrecord.getMessage());
        objectmessage.setStringProperty("threadName", logrecord.getThreadName());
        objectmessage.setIntProperty("level", logrecord.getLevel());
        objectmessage.setLongProperty("seqNum", logrecord.getSeqNum());
        objectmessage.setLongProperty("timeElapsed", logrecord.getTimeElapsed());
        objectmessage.setLongProperty("timestamp", logrecord.getTimeStamp());
        String s = logrecord.getLoggerCategory();
        objectmessage.setStringProperty("category", s != null ? s : "");
        String s1 = logrecord.getClassName();
        if(s1 != null)
        {
            objectmessage.setStringProperty("className", s1);
            objectmessage.setStringProperty("methodName", logrecord.getMethodName());
            objectmessage.setStringProperty("fileName", logrecord.getFileName());
            objectmessage.setIntProperty("lineNumber", logrecord.getLineNumber());
        }
        Throwable throwable = logrecord.getThrowable();
        if(throwable != null)
            objectmessage.setObject(throwable);
        return objectmessage;
    }
}
