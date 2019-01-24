// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.jms;

import java.util.List;
import javax.jms.*;

public interface JmsSmartXaConnection
{

    public abstract XAConnection getXaConn()
        throws JMSException;

    public abstract List getAllExceptionListeners();

    public abstract boolean addExceptionListener(ExceptionListener exceptionlistener);

    public abstract boolean removeExceptionListener(ExceptionListener exceptionlistener);

    public abstract int getRetries();

    public abstract void setRetries(int i);

    public abstract long getRetryTimeout();

    public abstract void setRetryTimeout(long l);

    public abstract boolean isOnline();

    public abstract void close();

    public abstract void reset()
        throws JMSException;
}
