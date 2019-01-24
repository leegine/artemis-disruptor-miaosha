// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.jms;

import javax.jms.JMSException;

// Referenced classes of package com.fitechlabs.xtier.services.jms:
//            JmsReplyObject, JmsReplyContext, JmsObjectConverter

public interface JmsObjectReceiver
{

    public abstract Object receive()
        throws JMSException;

    public abstract Object receive(long l)
        throws JMSException;

    public abstract Object receive(long l, String s, boolean flag)
        throws JMSException;

    public abstract Object receiveNoWait()
        throws JMSException;

    public abstract Object receiveNoWait(String s, boolean flag)
        throws JMSException;

    public abstract JmsReplyObject receiveForReply()
        throws JMSException;

    public abstract JmsReplyObject receiveForReply(long l)
        throws JMSException;

    public abstract JmsReplyObject receiveForReply(long l, String s, boolean flag)
        throws JMSException;

    public abstract JmsReplyObject receiveForReplyNoWait()
        throws JMSException;

    public abstract JmsReplyObject receiveForReplyNoWait(String s, boolean flag)
        throws JMSException;

    public abstract void reply(Object obj, JmsReplyContext jmsreplycontext)
        throws JMSException;

    public abstract void reply(Object obj, JmsReplyContext jmsreplycontext, int i, int j, long l)
        throws JMSException;

    public abstract void close()
        throws JMSException;

    public abstract JmsObjectConverter getObjConverter();

    public abstract void setObjConverter(JmsObjectConverter jmsobjectconverter);

    public abstract boolean isDisableMsgId();

    public abstract void setDisableMsgId(boolean flag);

    public abstract boolean isDisableMsgTimestamp();

    public abstract void setDisableMsgTimestamp(boolean flag);

    public abstract int getDeliveryMode();

    public abstract void setDeliveryMode(int i);

    public abstract int getPriority();

    public abstract void setPriority(int i);

    public abstract long getTtl();

    public abstract void setTtl(long l);
}
