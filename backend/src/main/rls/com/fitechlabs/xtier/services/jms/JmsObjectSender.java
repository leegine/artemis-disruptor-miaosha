// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.jms;

import javax.jms.JMSException;

// Referenced classes of package com.fitechlabs.xtier.services.jms:
//            JmsReplyListener, JmsObjectConverter

public interface JmsObjectSender
{

    public abstract void send(Object obj)
        throws JMSException;

    public abstract void send(Object obj, int i, int j, long l)
        throws JMSException;

    public abstract Object request(Object obj)
        throws JMSException;

    public abstract Object request(Object obj, int i, int j, long l)
        throws JMSException;

    public abstract void request(Object obj, JmsReplyListener jmsreplylistener)
        throws JMSException;

    public abstract void request(Object obj, int i, int j, long l, JmsReplyListener jmsreplylistener)
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
