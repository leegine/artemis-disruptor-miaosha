// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.grid.impl;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.cluster.ClusterNode;
import com.fitechlabs.xtier.services.cluster.ClusterService;
import com.fitechlabs.xtier.services.grid.*;
import com.fitechlabs.xtier.services.log.LogService;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.services.marshal.*;
import com.fitechlabs.xtier.services.objpool.ObjectPoolService;
import com.fitechlabs.xtier.threads.SysThread;
import com.fitechlabs.xtier.threads.SysThreadGroup;
import com.fitechlabs.xtier.utils.Utils;
import java.io.IOException;
import java.net.*;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.grid.impl:
//            GridManager, GridConfig

public class GridTcpClientManager
{
    private class TcpListener extends SysThread
    {

        private ServerSocket createServer()
            throws IOException
        {
            Object obj = mutex;
            JVM INSTR monitorenter ;
            if(!$assertionsDisabled && server != null)
                throw new AssertionError();
            server = new ServerSocket();
            if(!$assertionsDisabled && config == null)
                throw new AssertionError();
            server.bind(new InetSocketAddress(localNode.getAddress(), config.getTcpClientPort()));
            return server;
            Exception exception;
            exception;
            throw exception;
        }

        public void interrupt()
        {
            super.interrupt();
            closeServer();
        }

        protected void cleanup()
        {
            closeServer();
        }

        private void closeServer()
        {
            synchronized(mutex)
            {
                Utils.close(server);
                server = null;
            }
        }

        private ServerSocket getServer()
        {
            Object obj = mutex;
            JVM INSTR monitorenter ;
            return server;
            Exception exception;
            exception;
            throw exception;
        }

        protected void body()
        {
_L1:
            Socket socket;
            do
            {
label0:
                {
                    checkInterrupted();
                    socket = null;
                    ServerSocket serversocket = getServer();
                    try
                    {
                        if(serversocket == null)
                            serversocket = createServer();
                        socket = serversocket.accept();
                        break label0;
                    }
                    catch(IOException ioexception)
                    {
                        if(!isInterrupted())
                        {
                            logger.error(L10n.format("SRVC.GRID.ERR45"), ioexception);
                            closeServer();
                            Utils.sleep(2000L);
                        }
                    }
                }
            } while(true);
            MarshalObject marshalobject = (MarshalObject)ioMarshaller.demarshalObj(socket.getInputStream());
            Utils.close(socket);
            break MISSING_BLOCK_LABEL_159;
            Object obj;
            obj;
            logger.error(L10n.format("SRVC.GRID.ERR45"), ((Throwable) (obj)));
            Utils.close(socket);
              goto _L1
            obj;
            logger.error(L10n.format("SRVC.GRID.ERR46"), ((Throwable) (obj)));
            Utils.close(socket);
              goto _L1
            Exception exception;
            exception;
            Utils.close(socket);
            throw exception;
            switch(marshalobject.getInt8("msgType"))
            {
            default:
                break; /* Loop/switch isn't completed */

            case 1: // '\001'
                int i = marshalobject.getInt32("tid");
                com.fitechlabs.xtier.services.grid.GridTask gridtask = gridMgr.getTask(i);
                if(gridtask == null)
                {
                    onError(marshalobject, L10n.format("SRVC.GRID.ERR47", new Integer(i)));
                } else
                {
                    Marshallable marshallable = marshalobject.getMarshalObj("args");
                    String s = marshalobject.getUtf8Str("poolName");
                    if(s == null)
                    {
                        gridMgr.exec(gridtask, marshallable, new ResultListener(marshalobject));
                    } else
                    {
                        com.fitechlabs.xtier.services.objpool.threads.ThreadPool threadpool = objpool.getThreadPool(s);
                        if(threadpool == null)
                            onError(marshalobject, L10n.format("SRVC.GRID.ERR48", s));
                        else
                            gridMgr.exec(gridtask, marshallable, new ResultListener(marshalobject), threadpool);
                    }
                }
                break;

            case 2: // '\002'
                onError(marshalobject, L10n.format("SRVC.GRID.ERR49"));
                break;

            case 3: // '\003'
                (new SysThread(5, sysGrp, marshalobject) {

                    protected void body()
                    {
                        long l = msg.getInt64("timeout");
                        try
                        {
                            List list = gridMgr.getExecTrace(msg.getInt64("eid"), l);
                            ArrayList arraylist = null;
                            if(list != null)
                            {
                                int j = list.size();
                                arraylist = new ArrayList(j);
                                for(int k = 0; k < j; k++)
                                    arraylist.add(((Marshallable)list.get(k)).getObjs());

                            }
                            MarshalObject marshalobject1 = new MarshalObject();
                            marshalobject1.putInt64("msgId", msg.getInt64("msgId"));
                            marshalobject1.putList("execTraces", arraylist);
                            send(marshalobject1, InetAddress.getByAddress((byte[])msg.getArr("addr")), msg.getInt32("port"));
                        }
                        catch(IOException ioexception1)
                        {
                            onError(msg, ioexception1);
                        }
                        catch(MarshalException marshalexception)
                        {
                            onError(msg, marshalexception);
                        }
                    }


                {
                    super(final_s, i, threadgroup);
                }
                }
).start();
                break;
            }
            if(true) goto _L1; else goto _L2
_L2:
            if($assertionsDisabled) goto _L1; else goto _L3
_L3:
            throw new AssertionError("Invalid TCP message recevied: " + marshalobject);
        }

        private ServerSocket server;
        private final Object mutex = new Object();
        static final boolean $assertionsDisabled; /* synthetic field */



        TcpListener()
            throws IOException
        {
            super("grid-client-tcp-listener", 7);
            server = null;
            createServer();
        }
    }

    private class ResultListener
        implements GridTaskResultListener
    {

        public void onResult(GridTaskResult gridtaskresult)
        {
            MarshalObject marshalobject = new MarshalObject();
            marshalobject.putInt64("msgId", msg.getInt64("msgId"));
            marshalobject.putBool("isSuccessful", gridtaskresult.isSuccessful());
            marshalobject.putInt32("tid", gridtaskresult.getTaskId());
            marshalobject.putInt64("eid", gridtaskresult.getExecId());
            marshalobject.putDate("startTime", new Date(gridtaskresult.getStartTime()));
            marshalobject.putDate("endTime", new Date(gridtaskresult.getEndTime()));
            marshalobject.putMarshalObj("result", gridtaskresult.getReturnValue());
            try
            {
                send(marshalobject, InetAddress.getByAddress((byte[])msg.getArr("addr")), msg.getInt32("port"));
            }
            catch(IOException ioexception)
            {
                onError(msg, ioexception);
            }
            catch(MarshalException marshalexception)
            {
                onError(msg, marshalexception);
            }
        }

        private MarshalObject msg;
        static final boolean $assertionsDisabled; /* synthetic field */


        ResultListener(MarshalObject marshalobject)
        {
            super();
            if(!$assertionsDisabled && marshalobject == null)
            {
                throw new AssertionError();
            } else
            {
                msg = marshalobject;
                return;
            }
        }
    }


    public GridTcpClientManager(GridManager gridmanager, GridConfig gridconfig)
        throws GridException
    {
        sysGrp = SysThreadGroup.getNewGroup("grid-tcp");
        config = null;
        if(!$assertionsDisabled && gridmanager == null)
            throw new AssertionError();
        if(!$assertionsDisabled && gridconfig == null)
            throw new AssertionError();
        gridMgr = gridmanager;
        config = gridconfig;
        XtierKernel xtierkernel = XtierKernel.getInstance();
        objpool = xtierkernel.objpool();
        localNode = xtierkernel.cluster().getLocalNode();
        ioMarshaller = xtierkernel.marshal().getIoMarshaller();
        logger = xtierkernel.log().getLogger("grid-tcp");
        try
        {
            listener = new TcpListener();
            listener.start();
        }
        catch(IOException ioexception)
        {
            throw new GridException(L10n.format("SRVC.GRID.ERR44", ioexception.getMessage()), ioexception);
        }
    }

    private void onError(MarshalObject marshalobject, Exception exception)
    {
        logger.error(L10n.format("SRVC.GRID.ERR41"), exception);
        try
        {
            MarshalObject marshalobject1 = new MarshalObject();
            marshalobject1.putInt64("msgId", marshalobject.getInt64("msgId"));
            marshalobject1.putUtf8Str("error", exception.getLocalizedMessage());
            send(marshalobject1, InetAddress.getByAddress((byte[])marshalobject.getArr("addr")), marshalobject.getInt32("port"));
        }
        catch(IOException ioexception)
        {
            logger.error(L10n.format("SRVC.GRID.ERR40"), ioexception);
        }
        catch(MarshalException marshalexception)
        {
            logger.error(L10n.format("SRVC.GRID.ERR40"), marshalexception);
        }
    }

    private void onError(MarshalObject marshalobject, String s)
    {
        logger.error(L10n.format("SRVC.GRID.ERR51", s));
        try
        {
            MarshalObject marshalobject1 = new MarshalObject();
            marshalobject1.putInt64("msgId", marshalobject.getInt64("msgId"));
            marshalobject1.putUtf8Str("error", s);
            send(marshalobject1, InetAddress.getByAddress((byte[])marshalobject.getArr("addr")), marshalobject.getInt32("port"));
        }
        catch(IOException ioexception)
        {
            logger.error(L10n.format("SRVC.GRID.ERR40"), ioexception);
        }
        catch(MarshalException marshalexception)
        {
            logger.error(L10n.format("SRVC.GRID.ERR40"), marshalexception);
        }
    }

    private void send(MarshalObject marshalobject, InetAddress inetaddress, int i)
        throws IOException, MarshalException
    {
        Socket socket;
        if(!$assertionsDisabled && marshalobject == null)
            throw new AssertionError();
        if(!$assertionsDisabled && inetaddress == null)
            throw new AssertionError();
        socket = new Socket(inetaddress, i, localNode.getAddress(), 0);
        ioMarshaller.marshalObj(marshalobject, socket.getOutputStream());
        Utils.close(socket);
        break MISSING_BLOCK_LABEL_90;
        Exception exception;
        exception;
        Utils.close(socket);
        throw exception;
    }

    public void stop()
    {
        sysGrp.stopThreads();
        listener.interrupt();
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

    private static final byte TASK_MESSAGE = 1;
    private static final byte TASK_BATCH_MESSAGE = 2;
    private static final byte EXEC_TRACE_MESSAGE = 3;
    private static final long ERR_WAIT_TIME = 2000L;
    private ObjectPoolService objpool;
    private ClusterNode localNode;
    private IoMarshaller ioMarshaller;
    private Logger logger;
    private GridManager gridMgr;
    private SysThreadGroup sysGrp;
    private GridConfig config;
    private TcpListener listener;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(GridTcpClientManager.class).desiredAssertionStatus();
    }










}
