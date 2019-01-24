// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.cluster.resolvers;

import com.fitechlabs.xtier.kernel.*;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.cluster.*;
import com.fitechlabs.xtier.services.log.LogService;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.services.marshal.IoMarshaller;
import com.fitechlabs.xtier.services.marshal.MarshalService;
import com.fitechlabs.xtier.threads.SysThread;
import com.fitechlabs.xtier.utils.Utils;
import java.io.IOException;
import java.io.OutputStream;
import java.net.*;

public class ClusterTcpNodeFailureResolver
    implements ClusterNodeFailureResolver, KernelServiceListener
{
    private class FailureWorker extends SysThread
    {

        private void createServer()
            throws IOException
        {
            server = new ServerSocket(port, 50, localNode.getAddress());
        }

        public void interrupt()
        {
            super.interrupt();
            Utils.close(server);
        }

        protected void body()
        {
_L2:
            checkInterrupted();
            Socket socket;
            java.io.InputStream inputstream;
            OutputStream outputstream;
            if(server == null)
            {
                createServer();
                Utils.sleep(2000L);
            }
            socket = server.accept();
            inputstream = null;
            outputstream = null;
            inputstream = socket.getInputStream();
            outputstream = socket.getOutputStream();
            int i = marshaller.decodeInt32(inputstream);
            marshaller.encodeBool(localNode.getNodeId() != i, outputstream);
            outputstream.flush();
            Utils.close(inputstream);
            Utils.close(outputstream);
            Utils.close(socket);
            break MISSING_BLOCK_LABEL_165;
            IOException ioexception1;
            ioexception1;
            logger.error(L10n.format("SRVC.CLUSTER.ERR22"), ioexception1);
            Utils.close(inputstream);
            Utils.close(outputstream);
            Utils.close(socket);
            break MISSING_BLOCK_LABEL_165;
            Exception exception;
            exception;
            Utils.close(inputstream);
            Utils.close(outputstream);
            Utils.close(socket);
            throw exception;
            isThrottle = false;
            continue; /* Loop/switch isn't completed */
            IOException ioexception;
            ioexception;
            if(!isInterrupted())
            {
                if(!isThrottle)
                {
                    logger.error(L10n.format("SRVC.CLUSTER.ERR22"), ioexception);
                    isThrottle = true;
                }
                Utils.close(server);
                server = null;
            }
            if(true) goto _L2; else goto _L1
_L1:
        }

        protected void cleanup()
        {
            Utils.close(server);
        }

        private ServerSocket server;
        private boolean isThrottle;

        FailureWorker()
            throws IOException
        {
            super("tcp-failure-worker");
            server = null;
            isThrottle = false;
            createServer();
        }
    }


    public ClusterTcpNodeFailureResolver(int i)
    {
        this(i, 500L);
    }

    public ClusterTcpNodeFailureResolver(int i, long l)
    {
        port = i;
        timeout = (int)l;
        XtierKernel xtierkernel = XtierKernel.getInstance();
        logger = xtierkernel.log().getLogger("cluster-tcp-failure-resolver");
        marshaller = xtierkernel.marshal().getIoMarshaller();
        xtierkernel.addServiceListener("cluster", this);
    }

    public int getPort()
    {
        return port;
    }

    public void afterStart(String s)
        throws KernelServiceException
    {
        if(!$assertionsDisabled && !s.equals("cluster"))
            throw new AssertionError();
        localNode = XtierKernel.getInstance().cluster().getLocalNode();
        try
        {
            worker = new FailureWorker();
        }
        catch(IOException ioexception)
        {
            throw new ClusterException(L10n.format("SRVC.CLUSTER.ERR27"), ioexception);
        }
        worker.start();
    }

    public void beforeStop(String s)
    {
        if(!$assertionsDisabled && !s.equals("cluster"))
            throw new AssertionError();
        if(worker != null)
            Utils.stopThread(worker);
    }

    public boolean isFailed(ClusterNode clusternode)
    {
        Socket socket;
        java.io.InputStream inputstream;
        OutputStream outputstream;
        socket = null;
        inputstream = null;
        outputstream = null;
        boolean flag;
        socket = new Socket();
        if(localNode != null)
            socket.bind(new InetSocketAddress(localNode.getAddress(), 0));
        socket.connect(new InetSocketAddress(clusternode.getAddress(), port), timeout);
        outputstream = socket.getOutputStream();
        inputstream = socket.getInputStream();
        marshaller.encodeInt32(clusternode.getNodeId(), outputstream);
        outputstream.flush();
        flag = marshaller.decodeBool(inputstream);
        Utils.close(inputstream);
        Utils.close(outputstream);
        Utils.close(socket);
        return flag;
        Object obj;
        obj;
        boolean flag1;
        logger.warning(L10n.format("SRVC.CLUSTER.WRN11", new Integer(clusternode.getNodeId()), clusternode.getAddress().getHostAddress(), new Integer(clusternode.getPort())));
        flag1 = true;
        Utils.close(inputstream);
        Utils.close(outputstream);
        Utils.close(socket);
        return flag1;
        obj;
        logger.warning(L10n.format("SRVC.CLUSTER.WRN10", new Integer(clusternode.getNodeId()), clusternode.getAddress().getHostAddress(), new Integer(clusternode.getPort())));
        flag1 = true;
        Utils.close(inputstream);
        Utils.close(outputstream);
        Utils.close(socket);
        return flag1;
        obj;
        logger.debug(L10n.format("SRVC.CLUSTER.ERR19", clusternode), ((Throwable) (obj)));
        flag1 = false;
        Utils.close(inputstream);
        Utils.close(outputstream);
        Utils.close(socket);
        return flag1;
        Exception exception;
        exception;
        Utils.close(inputstream);
        Utils.close(outputstream);
        Utils.close(socket);
        throw exception;
    }

    public String toString()
    {
        return L10n.format("SRVC.CLUSTER.TXT3", new Integer(port));
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

    private final int port;
    private final int timeout;
    private Logger logger;
    private FailureWorker worker;
    private ClusterNode localNode;
    private IoMarshaller marshaller;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(ClusterTcpNodeFailureResolver.class).desiredAssertionStatus();
    }




}
