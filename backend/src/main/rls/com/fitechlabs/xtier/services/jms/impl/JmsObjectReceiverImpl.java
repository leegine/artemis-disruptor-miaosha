// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.jms.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.jms.*;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.Utils;
import javax.jms.*;

// Referenced classes of package com.fitechlabs.xtier.services.jms.impl:
//            JmsMessageDefaults

class JmsObjectReceiverImpl
    implements JmsObjectReceiver
{

    public JmsObjectReceiverImpl(Connection connection, Destination destination, JmsMessageDefaults jmsmessagedefaults)
        throws JMSException
    {
        mutex = new Object();
        if(!$assertionsDisabled && connection == null)
            throw new AssertionError();
        if(!$assertionsDisabled && destination == null)
        {
            throw new AssertionError();
        } else
        {
            assignDefaults(jmsmessagedefaults);
            conn = connection;
            rcvDest = destination;
            return;
        }
    }

    public JmsObjectReceiverImpl(Connection connection, String s, boolean flag, JmsMessageDefaults jmsmessagedefaults)
        throws JMSException
    {
        Session session;
        mutex = new Object();
        if(!$assertionsDisabled && connection == null)
            throw new AssertionError();
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        assignDefaults(jmsmessagedefaults);
        conn = connection;
        session = connection.createSession(false, 1);
        if(flag)
            rcvDest = session.createQueue(s);
        else
            rcvDest = session.createTopic(s);
        Utils.close(session);
        break MISSING_BLOCK_LABEL_122;
        Exception exception;
        exception;
        Utils.close(session);
        throw exception;
    }

    private void assignDefaults(JmsMessageDefaults jmsmessagedefaults)
    {
        deliveryMode = jmsmessagedefaults.getDeliveryMode();
        priority = jmsmessagedefaults.getPriority();
        ttl = jmsmessagedefaults.getTtl();
        isDisableMsgId = jmsmessagedefaults.isDisableMstId();
        isDisableMsgTimestamp = jmsmessagedefaults.isDisableMsgTimestamp();
        converter = jmsmessagedefaults.getConverter();
    }

    public Object receive()
        throws JMSException
    {
        return receive(0L, null, false);
    }

    public Object receive(long l)
        throws JMSException
    {
        return receive(l, null, false);
    }

    public Object receive(long l, String s, boolean flag)
        throws JMSException
    {
        Session session = conn.createSession(false, 1);
        Object obj;
        Message message = session.createConsumer(rcvDest, s, flag).receive(l);
        obj = message != null ? getObjConverter().convert(message) : null;
        Utils.close(session);
        return obj;
        Exception exception;
        exception;
        Utils.close(session);
        throw exception;
    }

    public Object receiveNoWait()
        throws JMSException
    {
        return receiveNoWait(null, false);
    }

    public Object receiveNoWait(String s, boolean flag)
        throws JMSException
    {
        Session session = conn.createSession(false, 1);
        Object obj;
        Message message = session.createConsumer(rcvDest, s, flag).receiveNoWait();
        obj = message != null ? getObjConverter().convert(message) : null;
        Utils.close(session);
        return obj;
        Exception exception;
        exception;
        Utils.close(session);
        throw exception;
    }

    public JmsReplyObject receiveForReply()
        throws JMSException
    {
        return receiveForReply(0L, null, false);
    }

    public JmsReplyObject receiveForReply(long l)
        throws JMSException
    {
        return receiveForReply(l, null, false);
    }

    public JmsReplyObject receiveForReply(long l, String s, boolean flag)
        throws JMSException
    {
        Session session = conn.createSession(false, 1);
        JmsReplyObject jmsreplyobject;
        Message message = session.createConsumer(rcvDest, s, flag).receive(l);
        jmsreplyobject = message != null ? convertForReply(message) : null;
        Utils.close(session);
        return jmsreplyobject;
        Exception exception;
        exception;
        Utils.close(session);
        throw exception;
    }

    public JmsReplyObject receiveForReplyNoWait()
        throws JMSException
    {
        return receiveForReplyNoWait(null, false);
    }

    public JmsReplyObject receiveForReplyNoWait(String s, boolean flag)
        throws JMSException
    {
        Session session = conn.createSession(false, 1);
        JmsReplyObject jmsreplyobject;
        Message message = session.createConsumer(rcvDest, s, flag).receiveNoWait();
        jmsreplyobject = message != null ? convertForReply(message) : null;
        Utils.close(session);
        return jmsreplyobject;
        Exception exception;
        exception;
        Utils.close(session);
        throw exception;
    }

    private JmsReplyObject convertForReply(Message message)
        throws JMSException
    {
        final Long msgId = (Long)message.getObjectProperty("xtierMsgIdName");
        if(msgId == null)
            throw new JMSException(L10n.format("SRVC.JMS.ERR10", "xtierMsgIdName"));
        final Destination replyTo = message.getJMSReplyTo();
        if(replyTo == null)
        {
            throw new JMSException(L10n.format("SRVC.JMS.ERR11"));
        } else
        {
            final Object obj = getObjConverter().convert(message);
            final JmsReplyContext replyCtx = new JmsReplyContext() {

                public long getReplyId()
                {
                    return msgId.longValue();
                }

                public Destination getReplyTo()
                {
                    return replyTo;
                }


            {
                super();
            }
            }
;
            return new JmsReplyObject() {

                public JmsReplyContext getReplyCtx()
                {
                    return replyCtx;
                }

                public Object getObj()
                {
                    return obj;
                }


            {
                super();
            }
            }
;
        }
    }

    public void reply(Object obj, JmsReplyContext jmsreplycontext)
        throws JMSException
    {
        reply(obj, jmsreplycontext, deliveryMode, priority, ttl);
    }

    public void reply(Object obj, JmsReplyContext jmsreplycontext, int i, int j, long l)
        throws JMSException
    {
        Session session;
        ArgAssert.nullArg(obj, "obj");
        ArgAssert.nullArg(jmsreplycontext, "replyCtx");
        if(jmsreplycontext.getReplyTo() == null)
            throw new IllegalArgumentException(L10n.format("SRVC.JMS.ERR12"));
        session = conn.createSession(false, 1);
        Message message = converter.convert(session, obj);
        message.setLongProperty("xtierMsgIdName", jmsreplycontext.getReplyId());
        MessageProducer messageproducer = session.createProducer(jmsreplycontext.getReplyTo());
        messageproducer.setDisableMessageID(isDisableMsgId);
        messageproducer.setDisableMessageTimestamp(isDisableMsgTimestamp);
        messageproducer.send(message, i, j, ttl);
        Utils.close(session);
        break MISSING_BLOCK_LABEL_147;
        Exception exception;
        exception;
        Utils.close(session);
        throw exception;
    }

    public void close()
        throws JMSException
    {
    }

    public JmsObjectConverter getObjConverter()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return converter;
        Exception exception;
        exception;
        throw exception;
    }

    public void setObjConverter(JmsObjectConverter jmsobjectconverter)
    {
        synchronized(mutex)
        {
            converter = jmsobjectconverter;
        }
    }

    public boolean isDisableMsgId()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return isDisableMsgId;
        Exception exception;
        exception;
        throw exception;
    }

    public void setDisableMsgId(boolean flag)
    {
        synchronized(mutex)
        {
            isDisableMsgId = flag;
        }
    }

    public boolean isDisableMsgTimestamp()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return isDisableMsgTimestamp;
        Exception exception;
        exception;
        throw exception;
    }

    public void setDisableMsgTimestamp(boolean flag)
    {
        synchronized(mutex)
        {
            isDisableMsgTimestamp = flag;
        }
    }

    public int getDeliveryMode()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return deliveryMode;
        Exception exception;
        exception;
        throw exception;
    }

    public void setDeliveryMode(int i)
    {
        synchronized(mutex)
        {
            deliveryMode = i;
        }
    }

    public int getPriority()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return priority;
        Exception exception;
        exception;
        throw exception;
    }

    public void setPriority(int i)
    {
        synchronized(mutex)
        {
            priority = i;
        }
    }

    public long getTtl()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return ttl;
        Exception exception;
        exception;
        throw exception;
    }

    public void setTtl(long l)
    {
        synchronized(mutex)
        {
            ttl = l;
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

    private final Object mutex;
    private Connection conn;
    private JmsObjectConverter converter;
    private Destination rcvDest;
    private int deliveryMode;
    private int priority;
    private long ttl;
    private boolean isDisableMsgId;
    private boolean isDisableMsgTimestamp;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(JmsObjectReceiverImpl.class).desiredAssertionStatus();
    }
}
