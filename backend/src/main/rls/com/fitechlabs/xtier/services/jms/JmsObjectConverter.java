// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.jms;

import javax.jms.*;

public interface JmsObjectConverter
{

    public abstract Message convert(Session session, Object obj)
        throws JMSException;

    public abstract Object convert(Message message)
        throws JMSException;
}
