// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.grid.impl;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.cluster.ClusterNode;
import com.fitechlabs.xtier.services.cluster.ClusterService;
import com.fitechlabs.xtier.services.log.LogService;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.services.marshal.*;
import com.fitechlabs.xtier.services.objpool.threads.ThreadPool;
import com.fitechlabs.xtier.threads.SysThread;
import com.fitechlabs.xtier.threads.SysThreadInterrupted;
import com.fitechlabs.xtier.utils.FifoQueue;
import com.fitechlabs.xtier.utils.Utils;
import java.io.*;
import java.net.*;

// Referenced classes of package com.fitechlabs.xtier.services.grid.impl:
//            GridConfig

class GridIoManager
{

    GridIoManager(GridConfig gridconfig, ThreadPool threadpool)
    {
        marshaller = null;
        log = null;
        mutex = new Object();
        inQ = new FifoQueue();
        stopped = false;
        config = null;
        listener = null;
        if(!$assertionsDisabled && gridconfig == null)
        {
            throw new AssertionError();
        } else
        {
            config = gridconfig;
            XtierKernel xtierkernel = XtierKernel.getInstance();
            ClusterNode clusternode = xtierkernel.cluster().getLocalNode();
            log = xtierkernel.log().getLogger("grid-io");
            marshaller = xtierkernel.marshal().getIoMarshaller();
            listener = new SysThread(threadpool) {

                protected void cleanup()
                {
                    synchronized(mutex)
                    {
                        stopped = true;
                        mutex.notifyAll();
                    }
                    Utils.close(server);
                }

                public void interrupt()
                {
                    super.interrupt();
                    cleanup();
                }

                protected void body()
                {
                    do
                    {
                        checkInterrupted();
                        if(server == null || server.isClosed())
                        {
                            InetSocketAddress inetsocketaddress = new InetSocketAddress(config.getIpAddr(), config.getPort(localNode));
                            try
                            {
                                server = new ServerSocket();
                                server.bind(inetsocketaddress);
                                log.log(L10n.format("SRVC.GRID.LOG1", inetsocketaddress));
                            }
                            catch(IOException ioexception1)
                            {
                                Utils.close(server);
                                checkInterrupted();
                                log.error(L10n.format("SRVC.GRID.ERR8", inetsocketaddress), ioexception1);
                                Utils.sleep(5000L);
                                continue;
                            }
                        }
                        try
                        {
                            do
                            {
                                final Socket sock = server.accept();
                                pool.addTask(new Runnable() {

                                    public void run()
                                    {
                                        BufferedInputStream bufferedinputstream = null;
                                        bufferedinputstream = new BufferedInputStream(sock.getInputStream());
                                        MarshalObject marshalobject = (MarshalObject)marshaller.demarshalObj(bufferedinputstream);
                                        synchronized(mutex)
                                        {
                                            if(!stopped)
                                            {
                                                inQ.add(marshalobject);
                                                mutex.notifyAll();
                                            }
                                        }
                                        Utils.close(bufferedinputstream);
                                        Utils.close(sock);
                                        break MISSING_BLOCK_LABEL_288;
                                        Object obj;
                                        obj;
                                        synchronized(mutex)
                                        {
                                            if(!stopped)
                                                log.error(L10n.format("SRVC.GRID.ERR32", sock), ((Throwable) (obj)));
                                        }
                                        Utils.close(bufferedinputstream);
                                        Utils.close(sock);
                                        break MISSING_BLOCK_LABEL_288;
                                        obj;
                                        synchronized(mutex)
                                        {
                                            if(!stopped)
                                                log.error(L10n.format("SRVC.GRID.ERR32", sock), ((Throwable) (obj)));
                                        }
                                        Utils.close(bufferedinputstream);
                                        Utils.close(sock);
                                        break MISSING_BLOCK_LABEL_288;
                                        Exception exception3;
                                        exception3;
                                        Utils.close(bufferedinputstream);
                                        Utils.close(sock);
                                        throw exception3;
                                    }


                    {
                        super();
                    }
                                }
);
                            } while(true);
                        }
                        catch(IOException ioexception)
                        {
                            Utils.close(server);
                            checkInterrupted();
                            log.error(L10n.format("SRVC.GRID.ERR33"), ioexception);
                        }
                    } while(true);
                }

                private ServerSocket server;



            {
                super(final_s);
                server = null;
            }
            }
;
            listener.start();
            return;
        }
    }

    void sendPacket(MarshalObject marshalobject, ClusterNode clusternode)
        throws MarshalException, IOException
    {
        Socket socket;
        BufferedOutputStream bufferedoutputstream;
        InetAddress inetaddress;
        int i;
        if(!$assertionsDisabled && (marshalobject == null || clusternode == null))
            throw new AssertionError();
        socket = null;
        bufferedoutputstream = null;
        inetaddress = clusternode.getAddress();
        i = config.getPort(clusternode);
        socket = new Socket(inetaddress, i);
        bufferedoutputstream = new BufferedOutputStream(socket.getOutputStream());
        marshaller.marshalObj(marshalobject, bufferedoutputstream);
        bufferedoutputstream.flush();
        Utils.close(bufferedoutputstream);
        Utils.close(socket);
        break MISSING_BLOCK_LABEL_113;
        Exception exception;
        exception;
        Utils.close(bufferedoutputstream);
        Utils.close(socket);
        throw exception;
    }

    MarshalObject receivePacket()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        if(stopped)
            throw new SysThreadInterrupted();
        while(inQ.isEmpty())
        {
            Utils.waitOn(mutex);
            if(stopped)
                throw new SysThreadInterrupted();
        }
        return (MarshalObject)inQ.get();
        Exception exception;
        exception;
        throw exception;
    }

    void stop()
    {
        Utils.stopThread(listener);
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

    private IoMarshaller marshaller;
    private Logger log;
    private Object mutex;
    private FifoQueue inQ;
    private boolean stopped;
    private GridConfig config;
    private SysThread listener;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(GridIoManager.class).desiredAssertionStatus();
    }






}
