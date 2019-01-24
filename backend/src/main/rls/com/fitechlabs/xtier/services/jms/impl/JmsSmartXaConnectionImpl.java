// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.jms.impl;

import com.fitechlabs.xtier.services.jms.JmsSmartXaConnection;
import java.util.List;
import javax.jms.*;

// Referenced classes of package com.fitechlabs.xtier.services.jms.impl:
//            JmsSmartConnectionHelper

class JmsSmartXaConnectionImpl
    implements JmsSmartXaConnection
{

    public JmsSmartXaConnectionImpl(XAConnectionFactory xaconnectionfactory, String s, String s1, int i, long l)
        throws JMSException
    {
        helper = new JmsSmartConnectionHelper(xaconnectionfactory, s, s1, i, l);
    }

    public boolean addExceptionListener(ExceptionListener exceptionlistener)
    {
        return helper.addExceptionListener(exceptionlistener);
    }

    public void close()
    {
        helper.close();
    }

    public List getAllExceptionListeners()
    {
        return helper.getAllExceptionListeners();
    }

    public int getRetries()
    {
        return helper.getRetries();
    }

    public long getRetryTimeout()
    {
        return helper.getRetryTimeout();
    }

    public XAConnection getXaConn()
        throws JMSException
    {
        return (XAConnection)helper.getConn();
    }

    public boolean isOnline()
    {
        return helper.isOnline();
    }

    public boolean removeExceptionListener(ExceptionListener exceptionlistener)
    {
        return helper.removeExceptionListener(exceptionlistener);
    }

    public void reset()
        throws JMSException
    {
        helper.reset();
    }

    public void setRetries(int i)
    {
        helper.setRetries(i);
    }

    public void setRetryTimeout(long l)
    {
        helper.setRetryTimeout(l);
    }

    private JmsSmartConnectionHelper helper;
}
