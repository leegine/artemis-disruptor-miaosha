// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.jms;

import com.fitechlabs.xtier.kernel.KernelService;
import javax.jms.*;

// Referenced classes of package com.fitechlabs.xtier.services.jms:
//            JmsSmartConnection, JmsSmartXaConnection, JmsObjectSender, JmsObjectReceiver

public interface JmsService
    extends KernelService
{

    public abstract void addConnFactory(String s, ConnectionFactory connectionfactory)
        throws JMSException;

    public abstract void addConnFactory(String s, ConnectionFactory connectionfactory, int i, long l, String s1, String s2)
        throws JMSException;

    public abstract void addXaConnFactory(String s, XAConnectionFactory xaconnectionfactory)
        throws JMSException;

    public abstract void addXaConnFactory(String s, XAConnectionFactory xaconnectionfactory, int i, long l, String s1, String s2)
        throws JMSException;

    public abstract ConnectionFactory getConnFactory(String s)
        throws JMSException;

    public abstract XAConnectionFactory getXaConnFactory(String s)
        throws JMSException;

    public abstract JmsSmartConnection getSmartConn(String s)
        throws JMSException;

    public abstract JmsSmartConnection getSmartConn(String s, String s1, String s2)
        throws JMSException;

    public abstract JmsSmartXaConnection getSmartXaConn(String s)
        throws JMSException;

    public abstract JmsSmartXaConnection getSmartXaConn(String s, String s1, String s2)
        throws JMSException;

    public abstract Connection getConn(String s)
        throws JMSException;

    public abstract Connection getConn(String s, String s1, String s2)
        throws JMSException;

    public abstract XAConnection getXaConn(String s)
        throws JMSException;

    public abstract XAConnection getXaConn(String s, String s1, String s2)
        throws JMSException;

    public abstract JmsObjectSender createSender(Connection connection, Destination destination)
        throws JMSException;

    public abstract JmsObjectSender createSender(Connection connection, String s, boolean flag)
        throws JMSException;

    public abstract JmsObjectReceiver createReceiver(Connection connection, Destination destination)
        throws JMSException;

    public abstract JmsObjectReceiver createReceiver(Connection connection, String s, boolean flag)
        throws JMSException;

    public abstract void close(Connection connection);

    public abstract void close(Session session);

    public abstract void close(MessageConsumer messageconsumer);

    public abstract void close(MessageProducer messageproducer);

    public abstract void close(JmsObjectSender jmsobjectsender);

    public abstract void close(JmsObjectReceiver jmsobjectreceiver);

    public static final String MSG_ID_NAME = "xtierMsgIdName";
    public static final int DFLT_CONN_RETRIES = 2;
    public static final long DFLT_CONN_RETRY_TIMEOUT = 2000L;
}
