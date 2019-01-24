// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.jms.impl;

import com.fitechlabs.xtier.utils.Utils;
import java.util.*;
import javax.jms.*;

class JmsSmartConnectionHelper
{

    JmsSmartConnectionHelper(ConnectionFactory connectionfactory, String s, String s1, int i, long l)
        throws JMSException
    {
        isOnline = false;
        if(!$assertionsDisabled && connectionfactory == null)
            throw new AssertionError();
        if(!$assertionsDisabled && s != null && s1 == null)
            throw new AssertionError();
        if(!$assertionsDisabled && i < 0)
            throw new AssertionError();
        if(!$assertionsDisabled && l < 0L)
        {
            throw new AssertionError();
        } else
        {
            factory = connectionfactory;
            retries = i;
            timeout = l;
            isXa = false;
            conn = getConn();
            return;
        }
    }

    JmsSmartConnectionHelper(XAConnectionFactory xaconnectionfactory, String s, String s1, int i, long l)
        throws JMSException
    {
        isOnline = false;
        if(!$assertionsDisabled && xaconnectionfactory == null)
            throw new AssertionError();
        if(!$assertionsDisabled && s != null && s1 == null)
            throw new AssertionError();
        if(!$assertionsDisabled && i < 0)
            throw new AssertionError();
        if(!$assertionsDisabled && l < 0L)
        {
            throw new AssertionError();
        } else
        {
            xaFactory = xaconnectionfactory;
            retries = i;
            timeout = l;
            isXa = true;
            return;
        }
    }

    boolean addExceptionListener(ExceptionListener exceptionlistener)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        if(listeners == null)
            listeners = new ArrayList();
        if(!listeners.contains(exceptionlistener))
        {
            listeners.add(exceptionlistener);
            return true;
        }
        false;
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    boolean removeExceptionListener(ExceptionListener exceptionlistener)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return listeners != null ? listeners.remove(exceptionlistener) : false;
        Exception exception;
        exception;
        throw exception;
    }

    List getAllExceptionListeners()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return listeners != null ? Collections.unmodifiableList(listeners) : Collections.EMPTY_LIST;
        Exception exception;
        exception;
        throw exception;
    }

    Connection getConn()
        throws JMSException
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        if(isOnline)
            return conn;
        reset();
        if(!$assertionsDisabled && !isOnline)
            throw new AssertionError();
        conn;
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    void reset()
        throws JMSException
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        JMSException jmsexception;
        int i;
        jmsexception = null;
        i = 0;
_L1:
        if(i > retries)
            break MISSING_BLOCK_LABEL_80;
        Utils.close(conn);
        conn = createConn();
        conn.setExceptionListener(new ExceptionListener() {

            public void onException(JMSException jmsexception2)
            {
                ArrayList arraylist = null;
                synchronized(JmsSmartConnectionHelper.mutex)
                {
                    isOnline = false;
                    arraylist = new ArrayList(listeners);
                }
                int j = 0;
                for(int k = arraylist.size(); j < k; j++)
                    ((ExceptionListener)arraylist.get(j)).onException(jmsexception2);

            }


            {
                super();
            }
        }
);
        isOnline = true;
        return;
        JMSException jmsexception1;
        jmsexception1;
        if(jmsexception == null)
            jmsexception = jmsexception1;
        Utils.sleep(timeout);
        i++;
          goto _L1
        isOnline = false;
        if(!$assertionsDisabled && jmsexception == null)
            throw new AssertionError();
        else
            throw jmsexception;
        Exception exception;
        exception;
        throw exception;
    }

    private Connection createConn()
        throws JMSException
    {
        if(isXa)
        {
            XAConnection xaconnection = username != null ? passwd != null ? xaFactory.createXAConnection(username, passwd) : xaFactory.createXAConnection(username, "") : xaFactory.createXAConnection();
            xaconnection.start();
            return xaconnection;
        } else
        {
            Connection connection = username != null ? passwd != null ? factory.createConnection(username, passwd) : factory.createConnection(username, "") : factory.createConnection();
            connection.start();
            return connection;
        }
    }

    void close()
    {
        synchronized(mutex)
        {
            Utils.close(conn);
        }
    }

    int getRetries()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return retries;
        Exception exception;
        exception;
        throw exception;
    }

    void setRetries(int i)
    {
        synchronized(mutex)
        {
            retries = i;
        }
    }

    long getRetryTimeout()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return timeout;
        Exception exception;
        exception;
        throw exception;
    }

    void setRetryTimeout(long l)
    {
        synchronized(mutex)
        {
            timeout = l;
        }
    }

    boolean isOnline()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return isOnline;
        Exception exception;
        exception;
        throw exception;
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

    private static final Object mutex = new Object();
    private final boolean isXa;
    private ConnectionFactory factory;
    private XAConnectionFactory xaFactory;
    private String username;
    private String passwd;
    private Connection conn;
    private List listeners;
    private int retries;
    private long timeout;
    private boolean isOnline;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(JmsSmartConnectionHelper.class).desiredAssertionStatus();
    }



}
