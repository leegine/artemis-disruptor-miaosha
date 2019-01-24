// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.jms.impl;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.jms.*;
import com.fitechlabs.xtier.services.log.LogService;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.utils.Utils;
import java.util.*;
import javax.jms.*;

// Referenced classes of package com.fitechlabs.xtier.services.jms.impl:
//            JmsMessageDefaults, JmsIdGenerator

public class JmsObjectSenderImpl
    implements JmsObjectSender
{
    private class MessageContext
    {

        boolean isAsync()
        {
            return isAsync;
        }

        AsyncListener getListener()
        {
            return listener;
        }

        JmsObjectConverter getConverter()
        {
            return converter;
        }

        Message getReply()
        {
            if(!$assertionsDisabled && !Thread.holdsLock(this))
                throw new AssertionError();
            else
                return reply;
        }

        void setReply(Message message)
        {
            if(!$assertionsDisabled && !Thread.holdsLock(this))
            {
                throw new AssertionError();
            } else
            {
                reply = message;
                return;
            }
        }

        boolean hasReply()
        {
            if(!$assertionsDisabled && !Thread.holdsLock(this))
                throw new AssertionError();
            else
                return reply != null;
        }

        private final AsyncListener listener;
        private final boolean isAsync;
        private final JmsObjectConverter converter;
        private Message reply;
        static final boolean $assertionsDisabled; /* synthetic field */


        MessageContext()
        {
            super();
            listener = null;
            converter = null;
            isAsync = false;
        }

        MessageContext(AsyncListener asynclistener, JmsObjectConverter jmsobjectconverter)
        {
            super();
            listener = asynclistener;
            converter = jmsobjectconverter;
            isAsync = true;
        }
    }

    private class AsyncListener extends TimerTask
    {

        public JmsReplyListener getListener()
        {
            return listener;
        }

        public void run()
        {
            synchronized(mutex)
            {
                msgCtxMap.remove(msgId);
            }
            listener.onTimeout();
        }

        private JmsReplyListener listener;
        private Long msgId;

        AsyncListener(Long long1, JmsReplyListener jmsreplylistener)
        {
            super();
            msgId = long1;
            listener = jmsreplylistener;
        }
    }


    public JmsObjectSenderImpl(Connection connection, Destination destination, JmsMessageDefaults jmsmessagedefaults)
        throws JMSException
    {
        mutex = new Object();
        msgCtxMap = new HashMap();
        isClosed = false;
        log = XtierKernel.getInstance().log().getLogger("jms");
        conn = connection;
        sendDest = destination;
        assignDflts(jmsmessagedefaults);
        isQueue = (destination instanceof Queue);
        replySes = connection.createSession(false, 1);
        if(isQueue)
            replyDest = replySes.createTemporaryQueue();
        else
            replyDest = replySes.createTemporaryTopic();
        createListener(replySes);
    }

    public JmsObjectSenderImpl(Connection connection, String s, boolean flag, JmsMessageDefaults jmsmessagedefaults)
        throws JMSException
    {
        mutex = new Object();
        msgCtxMap = new HashMap();
        isClosed = false;
        log = XtierKernel.getInstance().log().getLogger("jms");
        conn = connection;
        isQueue = flag;
        assignDflts(jmsmessagedefaults);
        replySes = connection.createSession(false, 1);
        if(flag)
        {
            sendDest = replySes.createQueue(s);
            replyDest = replySes.createTemporaryQueue();
        } else
        {
            sendDest = replySes.createTopic(s);
            replyDest = replySes.createTemporaryTopic();
        }
        createListener(replySes);
    }

    private void assignDflts(JmsMessageDefaults jmsmessagedefaults)
    {
        deliveryMode = jmsmessagedefaults.getDeliveryMode();
        priority = jmsmessagedefaults.getPriority();
        ttl = jmsmessagedefaults.getTtl();
        isDisableMsgId = jmsmessagedefaults.isDisableMstId();
        isDisableMsgTimestamp = jmsmessagedefaults.isDisableMsgTimestamp();
        converter = jmsmessagedefaults.getConverter();
    }

    private void createListener(Session session)
        throws JMSException
    {
        MessageConsumer messageconsumer = session.createConsumer(replyDest);
        messageconsumer.setMessageListener(new MessageListener() {

            public void onMessage(Message message)
            {
                Long long1 = null;
                try
                {
                    long1 = (Long)message.getObjectProperty("xtierMsgIdName");
                    if(long1 == null)
                    {
                        log.error(L10n.format("SRVC.JMS.ERR10", "xtierMsgIdName"));
                        return;
                    }
                }
                catch(JMSException jmsexception)
                {
                    log.error(L10n.format("SRVC.JMS.ERR28"), jmsexception);
                    return;
                }
                MessageContext messagecontext = null;
                synchronized(mutex)
                {
                    messagecontext = (MessageContext)msgCtxMap.remove(long1);
                }
                if(messagecontext == null)
                {
                    log.warning(L10n.format("SRVC.JMS.ERR29", long1));
                    return;
                }
                AsyncListener asynclistener = messagecontext.getListener();
                if(asynclistener != null)
                {
                    if(!$assertionsDisabled && !messagecontext.isAsync())
                        throw new AssertionError();
                    asynclistener.cancel();
                    try
                    {
                        asynclistener.getListener().onReply(messagecontext.getConverter().convert(message));
                    }
                    catch(JMSException jmsexception1)
                    {
                        asynclistener.getListener().onError(jmsexception1);
                    }
                } else
                {
                    synchronized(messagecontext)
                    {
                        messagecontext.setReply(message);
                        messagecontext.notifyAll();
                    }
                }
            }

            static final boolean $assertionsDisabled; /* synthetic field */


            
            {
                super();
            }
        }
);
    }

    public void send(Object obj)
        throws JMSException
    {
        send(obj, deliveryMode, priority, ttl);
    }

    public void send(Object obj, int i, int j, long l)
        throws JMSException
    {
        Session session = conn.createSession(false, 1);
        MessageProducer messageproducer = session.createProducer(sendDest);
        messageproducer.setDisableMessageID(isDisableMsgId());
        messageproducer.setDisableMessageTimestamp(isDisableMsgTimestamp());
        messageproducer.send(converter.convert(session, obj), i, j, l);
        Utils.close(session);
        break MISSING_BLOCK_LABEL_89;
        Exception exception;
        exception;
        Utils.close(session);
        throw exception;
    }

    public Object request(Object obj)
        throws JMSException
    {
        return request(obj, deliveryMode, priority, ttl);
    }

    public Object request(Object obj, int i, int j, long l)
        throws JMSException
    {
        Session session = conn.createSession(false, 1);
        MessageProducer messageproducer;
        long l1;
        long l2;
        Long long1;
        JmsObjectConverter jmsobjectconverter;
        MessageContext messagecontext;
        messageproducer = session.createProducer(sendDest);
        messageproducer.setDisableMessageID(isDisableMsgId());
        messageproducer.setDisableMessageTimestamp(isDisableMsgTimestamp());
        l1 = l != 0L ? l : 0x7fffffffffffffffL;
        l2 = System.currentTimeMillis();
        long1 = new Long(JmsIdGenerator.genId());
        jmsobjectconverter = getObjConverter();
        messagecontext = new MessageContext();
        synchronized(mutex)
        {
            msgCtxMap.put(long1, messagecontext);
        }
        Message message = jmsobjectconverter.convert(session, obj);
        message.setLongProperty("xtierMsgIdName", long1.longValue());
        message.setJMSReplyTo(replyDest);
        messageproducer.send(message, i, j, l);
_L3:
        synchronized(mutex)
        {
            if(isClosed)
                throw new JMSException(L10n.format("SRVC.JMS.ERR13"));
        }
        MessageContext messagecontext1 = messagecontext;
        JVM INSTR monitorenter ;
        if(!messagecontext.hasReply()) goto _L2; else goto _L1
_L1:
        Object obj3 = jmsobjectconverter.convert(messagecontext.getReply());
        Utils.close(session);
        return obj3;
_L2:
        Object obj4;
        long l3 = l1 - (System.currentTimeMillis() - l2);
        if(l3 <= 0L)
            throw new JmsTimeoutException(L10n.format("SRVC.JMS.ERR30", long1, new Long(l1)), l1);
        Utils.waitOn(messagecontext, l3);
        if(!messagecontext.hasReply())
            break MISSING_BLOCK_LABEL_358;
        obj4 = jmsobjectconverter.convert(messagecontext.getReply());
        messagecontext1;
        JVM INSTR monitorexit ;
        Utils.close(session);
        return obj4;
        messagecontext1;
        JVM INSTR monitorexit ;
          goto _L3
        Exception exception2;
        exception2;
        throw exception2;
        synchronized(mutex)
        {
            msgCtxMap.remove(long1);
        }
        JVM INSTR ret 22;
        Exception exception5;
        exception5;
        Utils.close(session);
        throw exception5;
    }

    public void request(Object obj, JmsReplyListener jmsreplylistener)
        throws JMSException
    {
        request(obj, deliveryMode, priority, ttl, jmsreplylistener);
    }

    public void request(Object obj, int i, int j, long l, JmsReplyListener jmsreplylistener)
        throws JMSException
    {
        Long long1;
        AsyncListener asynclistener;
        MessageContext messagecontext;
        Session session;
        long1 = new Long(JmsIdGenerator.genId());
        asynclistener = new AsyncListener(long1, jmsreplylistener);
        messagecontext = new MessageContext(asynclistener, getObjConverter());
        session = conn.createSession(false, 1);
        MessageProducer messageproducer = session.createProducer(sendDest);
        messageproducer.setDisableMessageID(isDisableMsgId());
        messageproducer.setDisableMessageTimestamp(isDisableMsgTimestamp());
        synchronized(mutex)
        {
            msgCtxMap.put(long1, messagecontext);
        }
        Message message = getObjConverter().convert(session, obj);
        message.setLongProperty("xtierMsgIdName", long1.longValue());
        message.setJMSReplyTo(replyDest);
        messageproducer.send(message, i, j, l);
        if(l > 0L)
            timer.schedule(asynclistener, l);
        Utils.close(session);
        break MISSING_BLOCK_LABEL_214;
        Exception exception1;
        exception1;
        Utils.close(session);
        throw exception1;
    }

    public void close()
        throws JMSException
    {
label0:
        {
            synchronized(mutex)
            {
                if(!isClosed)
                    break label0;
            }
            return;
        }
        isClosed = true;
        for(Iterator iterator = msgCtxMap.values().iterator(); iterator.hasNext();)
        {
            MessageContext messagecontext = (MessageContext)iterator.next();
            synchronized(messagecontext)
            {
                messagecontext.notifyAll();
            }
        }

        JMSException jmsexception = null;
        try
        {
            replySes.close();
        }
        catch(JMSException jmsexception1)
        {
            jmsexception = jmsexception1;
        }
        if(isQueue)
            try
            {
                ((TemporaryQueue)replyDest).delete();
            }
            catch(JMSException jmsexception2)
            {
                if(jmsexception == null)
                    jmsexception = jmsexception2;
            }
        else
            try
            {
                ((TemporaryTopic)replyDest).delete();
            }
            catch(JMSException jmsexception3)
            {
                if(jmsexception == null)
                    jmsexception = jmsexception3;
            }
        if(jmsexception != null)
            throw jmsexception;
        obj;
        JVM INSTR monitorexit ;
          goto _L1
        exception1;
        throw exception1;
_L1:
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
    private Session replySes;
    private Destination sendDest;
    private Destination replyDest;
    private boolean isQueue;
    private int deliveryMode;
    private int priority;
    private long ttl;
    private boolean isDisableMsgId;
    private boolean isDisableMsgTimestamp;
    private Map msgCtxMap;
    private Timer timer;
    private boolean isClosed;
    private Logger log;
    static Class class$com$fitechlabs$xtier$services$jms$impl$JmsObjectSenderImpl; /* synthetic field */



}
