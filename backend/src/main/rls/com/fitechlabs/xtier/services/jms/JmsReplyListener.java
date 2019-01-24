// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.jms;

import javax.jms.JMSException;

public interface JmsReplyListener
{

    public abstract void onReply(Object obj);

    public abstract void onTimeout();

    public abstract void onError(JMSException jmsexception);
}
