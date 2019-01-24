// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   ClusterTcpNodeFailureResolver.java

package co.fin.intellioms.xtier;

import com.fitechlabs.xtier.kernel.*;
import com.fitechlabs.xtier.services.cluster.*;
import com.fitechlabs.xtier.services.log.LogService;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.services.marshal.IoMarshaller;
import com.fitechlabs.xtier.services.marshal.MarshalService;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.Utils;
import com.fitechlabs.xtier.utils.boxed.sync.BoxedBooleanSync;
import java.io.*;
import java.net.*;

public class ClusterTcpNodeFailureResolver
    implements ClusterNodeFailureResolver, KernelServiceListener, KernelLifeCycleListener
{
    private class FailureWorker extends Thread
    {

        private void createServer(boolean isServer)
            throws IOException
        {
            this.isServer = isServer;
            if(isServer)
                server = new ServerSocket(primaryPort, 50, localNode.getAddress());
            else
                server = new ServerSocket(secondaryPort, 50, localNode.getAddress());
        }

        public void interrupt()
        {
            interrupted.set(true);
            super.interrupt();
            Utils.close(server);
        }

        public boolean isInterrupted()
        {
            return super.isInterrupted() || interrupted.get();
        }

        public void run()
        {
_L2:
            if(isInterrupted())
                break; /* Loop/switch isn't completed */
            if(server == null)
            {
                createServer(isServer);
                Utils.sleep(2000L);
            }
            if(isInterrupted())
                break; /* Loop/switch isn't completed */
            Socket sock;
            InputStream in;
            OutputStream out;
            sock = server.accept();
            in = null;
            out = null;
            in = sock.getInputStream();
            out = sock.getOutputStream();
            int nodeId = marshaller.decodeInt32(in);
            marshaller.encodeBool(localNode.getNodeId() != nodeId, out);
            out.flush();
            Utils.close(in);
            Utils.close(out);
            Utils.close(sock);
            break MISSING_BLOCK_LABEL_180;
            IOException e;
            e;
            logger.warning("I/O error in cluster TCP failure resolver.", e);
            Utils.close(in);
            Utils.close(out);
            Utils.close(sock);
            break MISSING_BLOCK_LABEL_180;
            Exception exception;
            exception;
            Utils.close(in);
            Utils.close(out);
            Utils.close(sock);
            throw exception;
            isThrottle = false;
            continue; /* Loop/switch isn't completed */
            IOException e;
            e;
            if(!isInterrupted())
            {
                if(!isThrottle)
                {
                    logger.error("I/O error in cluster TCP failure resolver.", e);
                    isThrottle = true;
                }
                Utils.close(server);
                server = null;
            }
            if(true) goto _L2; else goto _L1
_L1:
            Utils.close(server);
            break MISSING_BLOCK_LABEL_260;
            Exception exception1;
            exception1;
            Utils.close(server);
            throw exception1;
        }

        private ServerSocket server;
        private boolean isThrottle;
        private boolean isServer;
        private BoxedBooleanSync interrupted;
        final ClusterTcpNodeFailureResolver this$0;

        FailureWorker(boolean isServer)
            throws IOException
        {
            this$0 = ClusterTcpNodeFailureResolver.this;
            super("tcp-failure-worker");
            server = null;
            isThrottle = false;
            this.isServer = false;
            interrupted = new BoxedBooleanSync(false);
            createServer(isServer);
        }
    }


    public ClusterTcpNodeFailureResolver(int primaryPort, int secondaryPort)
    {
        this(primaryPort, secondaryPort, 500L, 1);
    }

    public ClusterTcpNodeFailureResolver(int primaryPort, int secondaryPort, long timeout, long failoverInterval)
    {
        this(primaryPort, secondaryPort, timeout, 1, failoverInterval);
    }

    public ClusterTcpNodeFailureResolver(int primaryPort, int secondaryPort, long timeout)
    {
        this(primaryPort, secondaryPort, timeout, 1);
    }

    public ClusterTcpNodeFailureResolver(int primaryPort, int secondaryPort, long timeout, int attempts)
    {
        this(primaryPort, secondaryPort, timeout, attempts, 500L);
    }

    public ClusterTcpNodeFailureResolver(int primaryPort, int secondaryPort, long timeout, int attempts, long failoverInterval)
    {
        ArgAssert.illegalRange(primaryPort > 0, "primaryPort", "primaryPort >= 0");
        ArgAssert.illegalRange(secondaryPort > 0, "secondaryPort", "secondaryPort >= 0");
        ArgAssert.illegalRange(timeout >= 0L, "timeout", "timeout >= 0");
        ArgAssert.illegalRange(attempts > 0, "attempts", "attempts > 0");
        ArgAssert.illegalRange(failoverInterval > 0L, "failoverInterval", "failoverInterval > 0");
        this.primaryPort = primaryPort;
        this.secondaryPort = secondaryPort;
        this.timeout = (int)timeout;
        attemps = attempts;
        this.failoverInterval = failoverInterval;
        XtierKernel xtier = XtierKernel.getInstance();
        logger = xtier.log().getLogger("rbe-cluster-tcp-failure-resolver");
        marshaller = xtier.marshal().getIoMarshaller();
        xtier.addServiceListener("cluster", this);
        xtier.addKernelListener(this);
    }

    public int getPort()
    {
        if(localNode == null)
            return 0;
        if(localNode.isGroupMember("rule-engines"))
            return secondaryPort;
        else
            return primaryPort;
    }

    public void afterStart(String name)
        throws KernelServiceException
    {
        if(!$assertionsDisabled && !name.equals("cluster"))
            throw new AssertionError();
        localNode = XtierKernel.getInstance().cluster().getLocalNode();
        try
        {
            worker = new FailureWorker(localNode.isGroupMember("rule-engines"));
        }
        catch(IOException e)
        {
            throw new ClusterException("Error trying to start TCP node failure resolver.", e);
        }
        worker.start();
    }

    public void beforeStop(String s)
    {
    }

    public boolean isFailed(ClusterNode node)
    {
        Socket sock;
        InputStream in;
        OutputStream out;
        Exception err;
        int i;
        sock = null;
        in = null;
        out = null;
        err = null;
        i = attemps;
_L3:
        if(i <= 0) goto _L2; else goto _L1
_L1:
        boolean flag;
        sock = new Socket();
        if(localNode != null)
            sock.bind(new InetSocketAddress(localNode.getAddress(), 0));
        if(node.isGroupMember("rule-engines"))
            sock.connect(new InetSocketAddress(node.getAddress(), primaryPort), timeout);
        else
            sock.connect(new InetSocketAddress(node.getAddress(), secondaryPort), timeout);
        out = sock.getOutputStream();
        in = sock.getInputStream();
        marshaller.encodeInt32(node.getNodeId(), out);
        out.flush();
        boolean result = marshaller.decodeBool(in);
        if(i != attemps)
            logger.info((new StringBuilder()).append("Cluster failure resolver successfully checked node after ").append((attemps - i) + 1).append(" attempts [node-id=").append(node.getNodeId()).append(", address=").append(node.getAddress().getHostAddress()).append(", port=").append(node.getPort()).append(']').toString());
        flag = result;
        Utils.close(in);
        Utils.close(out);
        Utils.close(sock);
        return flag;
        ConnectException e;
        e;
        logger.warning((new StringBuilder()).append("Connect error while attempting to check for node failure [node-id=").append(node.getNodeId()).append(", address=").append(node.getAddress().getHostAddress()).append(", port=").append(node.getPort()).append("attempts-left").append(i).append(']').toString(), e);
        err = e;
        if(i > 1)
            Utils.sleep(failoverInterval);
        Utils.close(in);
        Utils.close(out);
        Utils.close(sock);
        continue; /* Loop/switch isn't completed */
        e;
        logger.warning((new StringBuilder()).append("Timeout attempting to check for node failure [node-id=").append(node.getNodeId()).append(", address=").append(node.getAddress().getHostAddress()).append(", port=").append(node.getPort()).append("attempts-left").append(i).append(']').toString(), e);
        err = e;
        Utils.close(in);
        Utils.close(out);
        Utils.close(sock);
        continue; /* Loop/switch isn't completed */
        e;
        logger.warning((new StringBuilder()).append("I/O error trying to check for node failure: ").append(node).toString(), e);
        flag = false;
        Utils.close(in);
        Utils.close(out);
        Utils.close(sock);
        return flag;
        Exception exception;
        exception;
        Utils.close(in);
        Utils.close(out);
        Utils.close(sock);
        throw exception;
        i--;
          goto _L3
_L2:
        if(!$assertionsDisabled && err == null)
            throw new AssertionError("Node failure reason is null.");
        if(err instanceof SocketTimeoutException)
            logger.warning((new StringBuilder()).append("*** Node is assumed failed because connection timed out waiting to connect to it [node-id=").append(node.getNodeId()).append(", address=").append(node.getAddress().getHostAddress()).append(", port=").append(node.getPort()).append(']').toString());
        else
            logger.error((new StringBuilder()).append("*** Node is assumed failed because connection to it could not be established [node-id=").append(node.getNodeId()).append(", address=").append(node.getAddress().getHostAddress()).append(", port=").append(node.getPort()).append(']').toString(), err);
        return true;
    }

    public String toString()
    {
        return (new StringBuilder()).append("Cluster TCP node failure resolver [port=").append(primaryPort).append(']').toString();
    }

    public void stateChanged(int state)
    {
        if(state == 2 && worker != null)
            Utils.stopThread(worker);
    }

    private static final int DFLT_ATTEMPS = 1;
    private static final int DFLT_TIMEOUT = 500;
    private static final int DFLT_FAILOVER_INTERVAL = 500;
    private final int primaryPort;
    private final int secondaryPort;
    private final int timeout;
    private final long failoverInterval;
    private Logger logger;
    private FailureWorker worker;
    private ClusterNode localNode;
    private IoMarshaller marshaller;
    private int attemps;
    static final boolean $assertionsDisabled = !com/ com /fin/intellioms/xtier/ClusterTcpNodeFailureResolver.desiredAssertionStatus();






}
