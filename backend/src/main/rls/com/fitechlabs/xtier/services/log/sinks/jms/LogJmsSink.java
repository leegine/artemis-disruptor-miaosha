// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.log.sinks.jms;

import com.fitechlabs.xtier.services.log.*;
import com.fitechlabs.xtier.services.log.adapters.LogSinkAdapter;
import com.fitechlabs.xtier.services.log.sinks.jms.converters.LogDefaultConverter;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.Utils;
import javax.jms.*;

// Referenced classes of package com.fitechlabs.xtier.services.log.sinks.jms:
//            LogJmsConverter

public class LogJmsSink extends LogSinkAdapter
{

    public LogJmsSink(String s, ConnectionFactory connectionfactory, String s1, boolean flag)
        throws JMSException
    {
        this(s, null, null, ((LogJmsConverter) (new LogDefaultConverter())), connectionfactory, s1, flag, 2, 4, 0L, null, null);
    }

    public LogJmsSink(String s, LogFilter logfilter, LogErrorHandler logerrorhandler, LogJmsConverter logjmsconverter, ConnectionFactory connectionfactory, String s1, boolean flag,
            int i, int j, long l, String s2, String s3)
        throws JMSException
    {
        super(s, null, logfilter, logerrorhandler);
        username = null;
        passwd = null;
        conn = null;
        ArgAssert.nullArg(connectionfactory, "factory");
        ArgAssert.nullArg(s1, "destName");
        ArgAssert.nullArg(logjmsconverter, "converter");
        ArgAssert.illegalRange(i == 1 || i == 2, "deliveryMode", "deliveryMode == DeliveryMode.NON_PERSISTENT || deliveryMode == DeliveryMode.PERSISTENT");
        ArgAssert.illegalRange(j >= 0 && j <= 9, "priority", "between 0 and 9");
        ArgAssert.illegalRange(l >= 0L, "ttl", "ttl >= 0");
        converter = ((LogJmsConverter) (logjmsconverter == null ? ((LogJmsConverter) (new LogDefaultConverter())) : logjmsconverter));
        factory = connectionfactory;
        deliveryMode = i;
        priority = j;
        ttl = l;
        destName = s1;
        isQueue = flag;
    }

    private void connect()
        throws JMSException
    {
        if(!$assertionsDisabled && factory == null)
            throw new AssertionError();
        Object obj = getMutex();
        JVM INSTR monitorenter ;
        Session session;
        if(conn != null)
            break MISSING_BLOCK_LABEL_144;
        conn = username != null ? factory.createConnection(username, passwd) : factory.createConnection();
        session = conn.createSession(false, 1);
        if(isQueue)
            dest = session.createQueue(destName);
        else
            dest = session.createTopic(destName);
        session.close();
        break MISSING_BLOCK_LABEL_144;
        Exception exception;
        exception;
        session.close();
        throw exception;
        Exception exception1;
        exception1;
        throw exception1;
    }

    public void add(LogRecord logrecord)
    {
        Session session;
        LogFilter logfilter = getFilter();
        if(logfilter != null && !logfilter.isLoggable(this, logrecord))
            break MISSING_BLOCK_LABEL_115;
        connect();
        session = conn.createSession(false, 1);
        session.createProducer(dest).send(converter.convert(logrecord, session), deliveryMode, priority, ttl);
        session.close();
        break MISSING_BLOCK_LABEL_115;
        Exception exception;
        exception;
        session.close();
        throw exception;
        JMSException jmsexception;
        jmsexception;
        getErrorHandler().onLogError(this, logrecord, jmsexception);
        close();
    }

    public void close()
    {
        synchronized(getMutex())
        {
            Utils.close(conn);
            conn = null;
        }
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

    private String username;
    private String passwd;
    private ConnectionFactory factory;
    private Connection conn;
    private int deliveryMode;
    private int priority;
    private long ttl;
    private String destName;
    private boolean isQueue;
    private Destination dest;
    private LogJmsConverter converter;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(LogJmsSink.class).desiredAssertionStatus();
    }
}
