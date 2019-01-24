// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.jms.adapters;

import com.fitechlabs.xtier.services.jms.JmsReplyListener;
import javax.jms.JMSException;

public abstract class JmsReplyListenerAdapter
    implements JmsReplyListener
{

    public JmsReplyListenerAdapter()
    {
    }

    public void onReply(Object obj)
    {
    }

    public void onTimeout()
    {
    }

    public void onError(JMSException jmsexception)
    {
        jmsexception.printStackTrace();
    }
}
