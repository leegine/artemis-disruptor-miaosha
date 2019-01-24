// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.grid.impl;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.grid.GridTaskResult;
import com.fitechlabs.xtier.services.grid.GridTaskResultListener;
import com.fitechlabs.xtier.services.jms.JmsService;
import com.fitechlabs.xtier.services.jms.JmsSmartConnection;
import com.fitechlabs.xtier.services.log.LogService;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.services.marshal.*;
import com.fitechlabs.xtier.services.objpool.ObjectPoolService;
import com.fitechlabs.xtier.threads.SysThread;
import com.fitechlabs.xtier.threads.SysThreadGroup;
import java.io.Serializable;
import java.util.*;
import javax.jms.*;

// Referenced classes of package com.fitechlabs.xtier.services.grid.impl:
//            GridConfig, GridManager

class GridJmsClientManager
{
    private class ResultListener
        implements GridTaskResultListener
    {

        public void onResult(GridTaskResult gridtaskresult)
        {
            Session session = null;
            session = conn.getConn().createSession(false, 1);
            long l = msg.getLongProperty("timeout");
            MessageProducer messageproducer = session.createProducer(msg.getJMSReplyTo());
            ObjectMessage objectmessage = session.createObjectMessage();
            objectmessage.setLongProperty("msgId", msg.getLongProperty("msgId"));
            objectmessage.setBooleanProperty("isSuccessful", gridtaskresult.isSuccessful());
            objectmessage.setIntProperty("tid", gridtaskresult.getTaskId());
            objectmessage.setLongProperty("eid", gridtaskresult.getExecId());
            objectmessage.setLongProperty("startTime", gridtaskresult.getStartTime());
            objectmessage.setLongProperty("endTime", gridtaskresult.getEndTime());
            objectmessage.setObject((Serializable)gridtaskresult.getReturnValue().getObjs());
            messageproducer.send(objectmessage, msg.getJMSDeliveryMode(), msg.getJMSPriority(), l);
            jms.close(session);
            break MISSING_BLOCK_LABEL_295;
            Object obj;
            obj;
            onError(msg, ((Exception) (obj)));
            jms.close(session);
            break MISSING_BLOCK_LABEL_295;
            obj;
            onError(msg, ((Exception) (obj)));
            jms.close(session);
            break MISSING_BLOCK_LABEL_295;
            Exception exception;
            exception;
            jms.close(session);
            throw exception;
        }

        private Message msg;
        static final boolean $assertionsDisabled; /* synthetic field */


        ResultListener(Message message)
        {
            super();
            if(!$assertionsDisabled && message == null)
            {
                throw new AssertionError();
            } else
            {
                msg = message;
                return;
            }
        }
    }


    public GridJmsClientManager(GridManager gridmanager, GridConfig gridconfig)
        throws JMSException
    {
        sysGrp = SysThreadGroup.getNewGroup("grid-jms");
        if(!$assertionsDisabled && gridmanager == null)
            throw new AssertionError();
        if(!$assertionsDisabled && gridconfig.getJmsFactoryName() == null)
        {
            throw new AssertionError();
        } else
        {
            gridMgr = gridmanager;
            XtierKernel xtierkernel = XtierKernel.getInstance();
            jms = xtierkernel.jms();
            objpool = xtierkernel.objpool();
            logger = xtierkernel.log().getLogger("grid-jms");
            conn = jms.getSmartConn(gridconfig.getJmsFactoryName());
            reset();
            return;
        }
    }

    private synchronized void reset()
        throws JMSException
    {
        jms.close(rcvSes);
        rcvSes = conn.getConn().createSession(false, 1);
        MessageConsumer messageconsumer = rcvSes.createConsumer(rcvSes.createQueue("xtiergridclient"));
        messageconsumer.setMessageListener(new MessageListener() {

            public void onMessage(Message message)
            {
                message.getByteProperty("type");
                JVM INSTR tableswitch 1 3: default 277
            //                           1 36
            //                           2 239
            //                           3 255;
                   goto _L1 _L2 _L3 _L4
_L1:
                break; /* Loop/switch isn't completed */
_L2:
                String s;
                com.fitechlabs.xtier.services.grid.GridTask gridtask;
                int i = message.getIntProperty("tid");
                s = message.getStringProperty("poolName");
                gridtask = gridMgr.getTask(i);
                if(gridtask == null)
                {
                    onError(message, L10n.format("SRVC.GRID.ERR37", new Integer(i)));
                    return;
                }
                Map map;
                com.fitechlabs.xtier.services.objpool.threads.ThreadPool threadpool;
                map = (Map)((ObjectMessage)message).getObject();
                if(s == null)
                {
                    gridMgr.exec(gridtask, map != null ? ((Marshallable) (new MarshalObject(map))) : null, new ResultListener(message));
                    break; /* Loop/switch isn't completed */
                }
                threadpool = objpool.getThreadPool(s);
                if(threadpool == null)
                {
                    onError(message, L10n.format("SRVC.GRID.ERR38", s));
                    return;
                }
                gridMgr.exec(gridtask, map != null ? ((Marshallable) (new MarshalObject(map))) : null, new ResultListener(message), threadpool);
                break; /* Loop/switch isn't completed */
_L3:
                onError(message, L10n.format("SRVC.GRID.ERR39"));
                break; /* Loop/switch isn't completed */
_L4:
                (new SysThread(5, sysGrp, message) {

                    protected void body()
                    {
                        Session session = null;
                        long l = msg.getLongProperty("timeout");
                        List list = gridMgr.getExecTrace(msg.getLongProperty("eid"), l);
                        ArrayList arraylist = null;
                        if(list != null)
                        {
                            int j = list.size();
                            arraylist = new ArrayList(j);
                            for(int k = 0; k < j; k++)
                                arraylist.add(((Marshallable)list.get(k)).getObjs());

                        }
                        session = conn.getConn().createSession(false, 1);
                        MessageProducer messageproducer = session.createProducer(msg.getJMSReplyTo());
                        ObjectMessage objectmessage = session.createObjectMessage();
                        objectmessage.setLongProperty("msgId", msg.getLongProperty("msgId"));
                        objectmessage.setObject(arraylist);
                        messageproducer.send(objectmessage, msg.getJMSDeliveryMode(), msg.getJMSPriority(), l);
                        jms.close(session);
                        break MISSING_BLOCK_LABEL_323;
                        Object obj;
                        obj;
                        onError(msg, ((Exception) (obj)));
                        jms.close(session);
                        break MISSING_BLOCK_LABEL_323;
                        obj;
                        onError(msg, ((Exception) (obj)));
                        jms.close(session);
                        break MISSING_BLOCK_LABEL_323;
                        Exception exception;
                        exception;
                        jms.close(session);
                        throw exception;
                    }


                    {
                        super(final_s, i, threadgroup);
                    }
                }
).start();
                break; /* Loop/switch isn't completed */
                JMSException jmsexception;
                jmsexception;
                onError(message, jmsexception);
            }



            {
                super();
            }
        }
);
    }

    private void onError(Message message, Exception exception)
    {
        Session session;
        logger.error(L10n.format("SRVC.GRID.ERR41"), exception);
        session = null;
        reset();
        session = conn.getConn().createSession(false, 1);
        MessageProducer messageproducer = session.createProducer(message.getJMSReplyTo());
        ObjectMessage objectmessage = session.createObjectMessage();
        objectmessage.setLongProperty("msgId", message.getLongProperty("msgId"));
        objectmessage.setStringProperty("error", exception.getLocalizedMessage());
        messageproducer.send(objectmessage, message.getJMSDeliveryMode(), message.getJMSPriority(), message.getLongProperty("timeout"));
        jms.close(session);
        break MISSING_BLOCK_LABEL_178;
        JMSException jmsexception;
        jmsexception;
        logger.error(L10n.format("SRVC.GRID.ERR40"), jmsexception);
        jms.close(session);
        break MISSING_BLOCK_LABEL_178;
        Exception exception1;
        exception1;
        jms.close(session);
        throw exception1;
    }

    private void onError(Message message, String s)
    {
        Session session;
        logger.error(L10n.format("SRVC.GRID.ERR42", s));
        session = null;
        session = conn.getConn().createSession(false, 1);
        MessageProducer messageproducer = session.createProducer(message.getJMSReplyTo());
        ObjectMessage objectmessage = session.createObjectMessage();
        objectmessage.setLongProperty("msgId", message.getLongProperty("msgId"));
        objectmessage.setStringProperty("error", s);
        messageproducer.send(objectmessage, message.getJMSDeliveryMode(), message.getJMSPriority(), message.getLongProperty("timeout"));
        jms.close(session);
        break MISSING_BLOCK_LABEL_171;
        JMSException jmsexception;
        jmsexception;
        logger.error(L10n.format("SRVC.GRID.ERR40"), jmsexception);
        jms.close(session);
        break MISSING_BLOCK_LABEL_171;
        Exception exception;
        exception;
        jms.close(session);
        throw exception;
    }

    public void stop()
    {
        sysGrp.stopThreads();
        conn.close();
    }

    private String getStrMsgType(byte byte0)
    {
        switch(byte0)
        {
        case 1: // '\001'
            return "TASK_MESSAGE";

        case 2: // '\002'
            return "TASK_BATCH_MESSAGE";

        case 3: // '\003'
            return "EXEC_TRACE_MESSAGE";
        }
        if(!$assertionsDisabled)
            throw new AssertionError();
        else
            return null;
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

    private static final String GRID_QUEUE_NAME = "xtiergridclient";
    private static final byte TASK_MESSAGE = 1;
    private static final byte TASK_BATCH_MESSAGE = 2;
    private static final byte EXEC_TRACE_MESSAGE = 3;
    private JmsService jms;
    private ObjectPoolService objpool;
    private JmsSmartConnection conn;
    private Logger logger;
    private GridManager gridMgr;
    private Session rcvSes;
    private SysThreadGroup sysGrp;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(GridJmsClientManager.class).desiredAssertionStatus();
    }







}
