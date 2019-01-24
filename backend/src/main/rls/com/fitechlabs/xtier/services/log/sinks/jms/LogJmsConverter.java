// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.log.sinks.jms;

import com.fitechlabs.xtier.services.log.LogRecord;
import javax.jms.*;

public interface LogJmsConverter
{

    public abstract Message convert(LogRecord logrecord, Session session)
        throws JMSException;
}
