/*
 * Copyright 2000-2005 Fitech Laboratories, Inc. All Rights Reserved.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. FITECH LABORATORIES AND ITS LICENSORS
 * SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL
 * FITECH LABORATORIES OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT
 * OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE
 * DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT
 * OF THE USE OF OR INABILITY TO USE SOFTWARE, EVEN IF FITECH LABORATORIES HAS
 * BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 *
 * $Project:xtier-2.0$
 * $Workfile:ClusterCommManager.java$
 * $Date:17.04.2006 11:05:55$
 * $Revision:37$
 */

package com.fitechlabs.xtier.services.cluster.impl;

import com.fitechlabs.xtier.debug.*;
import com.fitechlabs.xtier.kernel.*;
import com.fitechlabs.xtier.l10n.*;
import com.fitechlabs.xtier.services.cluster.*;
import com.fitechlabs.xtier.services.log.*;
import com.fitechlabs.xtier.services.marshal.*;
import com.fitechlabs.xtier.services.objpool.*;
import com.fitechlabs.xtier.services.objpool.threads.*;
import com.fitechlabs.xtier.services.objpool.policies.*;
import com.fitechlabs.xtier.threads.*;
import com.fitechlabs.xtier.utils.*;
import com.fitechlabs.xtier.utils.boxed.sync.*;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * @author <a href="mailto:dsetraky@fitechlabs.com">Dmitriy Setrakyan</a>
 */
class ClusterCommManager {
    /** */
    private static final long ERR_WAIT_TIME = 2000;
    
    /** */
    private Logger logger;
    
    /** */
    private ClusterHeartbeatManager hrtbeatMgr;
    
    /** */
    private ClusterProtocolManager protocolMgr;
    
    /** */
    private ClusterLockManager lockMgr;
    
    /** */
    private ClusterConfig config;
    
    /** */
    private ClusterTopologyManager topologyMgr;
    
    /** */
    private ClusterEncoder encoder;
    
    /** */
    private TcpListener tcpListener;
    
    /** */
    private McastListener mcastListener;
    
    /** */
    private ThreadPool msgHandler;
    
    /** */
    private ObjectPoolService objpool;

    /**
     * 
     * @param logger
     * @param config
     * @param encoder
     * @throws ClusterException
     */
    ClusterCommManager(Logger logger, ClusterConfig config, ClusterEncoder encoder) throws ClusterException {
        this.logger = logger;
        this.config = config;
        this.encoder = encoder;
        
        objpool = XtierKernel.getInstance().objpool();
    }

    /**
     * 
     * @param protocolMgr
     * @param lockMgr
     * @param topologyMgr
     * @param hrtbeatMgr
     * @throws ClusterException
     */
    void start(ClusterProtocolManager protocolMgr, ClusterLockManager lockMgr, ClusterTopologyManager topologyMgr,
        ClusterHeartbeatManager hrtbeatMgr) throws ClusterException {
        this.protocolMgr = protocolMgr;
        this.lockMgr = lockMgr;
        this.topologyMgr = topologyMgr;
        this.hrtbeatMgr = hrtbeatMgr;

        try {
            // Create one-threaded pool to serially handle messages.
            msgHandler = objpool.createThreadPool("cluster-msg-handler", 1, false, SysThread.SYS_ABOVE_NORMAL_PRIORITY, 
                new ThreadPoolStrictPolicy());
        }
        catch (ObjectPoolException e) {
            throw new ClusterException(L10n.format("SRVC.CLUSTER.ERR38", "cluster-msg-handler"), e);
        }
        
        tcpListener = new TcpListener();
        mcastListener = new McastListener();

        tcpListener.start();
        mcastListener.start();
    }
    
    /**
     * 
     */
    void stop() {
        Utils.stopThread(tcpListener);
        Utils.stopThread(mcastListener);
        
        if (msgHandler != null) {
            objpool.deleteWaitThreadPool(msgHandler.getName());
        }
    }
    
    /**
     * 
     * @param msg
     * @param out
     * @throws ClusterException
     */
    void send(ClusterTcpMsg msg, OutputStream out) throws ClusterException {
        try {
            encoder.encodeTcpMsg(msg, out);
            
            out.flush();
            
            if (DebugFlags.CLUSTER == true) {
                Utils.debug("Sent TCP message: " + msg);
            }
        }
        catch (IOException e) {
            throw new ClusterException(L10n.format("SRVC.CLUSTER.ERR20"), e);
        }
        catch (MarshalException e) {
            throw new ClusterException(L10n.format("SRVC.CLUSTER.ERR21"), e);
        }
    }
    
    /**
     * 
     * @param msg
     * @param addr
     * @param port
     * @throws ClusterException
     */
    void send(ClusterTcpMsg msg, InetAddress addr, int port) throws ClusterException {
        Socket sock = null;
        OutputStream out = null;
        
        try {
            sock = connect(addr, port);
            
            out = sock.getOutputStream();
            
            if (DebugFlags.CLUSTER == true) {
                Utils.debug("Sending TCP message [address=" + addr + ", port=" + port + ']');
            }
            
            send(msg, out);
        }
        catch (IOException e) {
            throw new ClusterException(L10n.format("SRVC.CLUSTER.ERR20"), e);
        }
        finally {
            Utils.close(out);
            Utils.close(sock);
        }
    }
    
    /**
     * 
     * @param msg
     * @throws ClusterException
     */
    void send(ClusterUdpMsg msg) throws ClusterException {
        MulticastSocket sock = null;
        
        try {
            sock = new MulticastSocket(new InetSocketAddress(config.getLocalAddress(), config.getLocalPort()));
            
            sock.setTimeToLive(config.getMcastTtl());
            
            byte[] buf = new byte[ClusterUdpMsg.BYTE_SIZE];
            
            encoder.encodeUdpMsg(msg, buf, 0);
            
            sock.send(new DatagramPacket(buf, buf.length, config.getMcastGroup(), config.getMcastPort()));
            
            if (DebugFlags.CLUSTER == true) {
                Utils.debug("Sent UDP message: " + msg);
            }
        }
        catch (IOException e) {
            throw new ClusterException(L10n.format("SRVC.CLUSTER.ERR20"), e);
        }
        finally {
            Utils.close(sock);
        }
    }
    
    /**
     * 
     * @param msg
     * @param addr
     * @param port
     * @param retryConnect
     * @return TBD
     * @throws ClusterException
     */
    ClusterTcpMsg sendWait(ClusterTcpMsg msg, InetAddress addr, int port, boolean retryConnect) throws ClusterException {
        for (int i = 0, n = config.getRetries(); i < n; i++) {
            Socket sock = null;
            
            OutputStream out = null;
            InputStream in = null;
            
            try {
                sock = connect(addr, port);
                
                out = sock.getOutputStream();
                in = sock.getInputStream();
                
                if (DebugFlags.CLUSTER == true) {
                    Utils.debug("Sending message to node [address=" + addr + ", port=" + port + ']');
                }
                
                send(msg, out);
                
                if (DebugFlags.CLUSTER == true) {
                    Utils.debug("Receving message from node [address=" + addr + ", port=" + port + ']');
                }
    
                return receive(in);
            }
            catch (ClusterConnectException e) {
                if (retryConnect == false || i + 1 == n) {
                    throw e;
                }
                
                Utils.sleep(config.getTimeout());
            }
            catch (ClusterException e) {
                if (i + 1 == n) {
                    throw e;
                }
                
                Utils.sleep(config.getTimeout());
            }
            catch (IOException e) {
                if (i + 1 == n) {
                    throw new ClusterException(L10n.format("SRVC.CLUSTER.ERR20"), e);
                }
                
                Utils.sleep(config.getTimeout());
            }
            finally {
                Utils.close(in);
                Utils.close(out);
                Utils.close(sock);
            }
        }
        
        assert false;
        
        // Never reached.
        return null;
    }
    
    /**
     * 
     * @param msg
     * @param nodes
     * @throws ClusterException
     */
    void sendWait(ClusterTcpMsg msg, Collection nodes) throws ClusterException {
        if (nodes.isEmpty() == false) {
            // Send message to every node serially.
            for (Iterator iter = nodes.iterator(); iter.hasNext() == true;) {
                ClusterNodeImpl node = (ClusterNodeImpl)iter.next();
                
                for (int i = 0, n = config.getRetries(); i < n; i++) {
                    Socket sock = null; 
                    
                    InputStream in = null;
                    OutputStream out = null;
                    
                    try {
                        sock = connect(node.getAddress(), node.getPort());
                    
                        in = sock.getInputStream();
                        out = sock.getOutputStream();
                        
                        if (DebugFlags.CLUSTER == true) {
                            Utils.debug("Sending message to node [address=" + node.getAddress() + ", port=" +
                                node.getPort() + ']');
                        }
                        
                        // Send the message.
                        send(msg, out);
                        
                        if (DebugFlags.CLUSTER == true) {
                            Utils.debug("Receving ACK from node [address=" + node.getAddress() + ", port=" +
                                node.getPort() + ']');
                        }
                        
                        // Receive acknowledgement.
                        receive(in);
                        
                        break;
                    }
                    // When message is sent to a collection of nodes, we are aware
                    // that the nodes are up and, therefore, connetion should be successful.
                    catch (ClusterException e) {
                        if (i + 1 == n) {
                            throw e;
                        }
                        
                        Utils.sleep(config.getTimeout());
                    }
                    catch (IOException e) {
                        if (i + 1 == n) {
                            throw new ClusterException(L10n.format("SRVC.CLUSTER.ERR20"), e);
                        }
                        
                        Utils.sleep(config.getTimeout());
                    }
                    finally {
                        Utils.close(in);
                        Utils.close(out);
                        Utils.close(sock);
                    }
                }
            }
        }
    }
    
    /**
     * 
     * @param addr
     * @param port
     * @return TBD
     * @throws ClusterConnectException
     */
    private Socket connect(InetAddress addr, int port) throws ClusterConnectException {
        Socket sock = null;
        
        try {
            sock = new Socket();
            
            sock.bind(new InetSocketAddress(topologyMgr.getLocalNode().getAddress(), /*any port*/0));
            
            // Set time limit for trying to connect.
            sock.connect(new InetSocketAddress(addr, port), config.getTimeout());
            
            return sock;
        }
        catch (IOException e) {
            Utils.close(sock);
            
            throw new ClusterConnectException(L10n.format("SRVC.CLUSTER.ERR14", addr.getHostAddress(), new Integer(port)));
        }
    }

    /**
     * 
     * @param in
     * @return TBD
     * @throws ClusterException
     */
    private ClusterTcpMsg receive(InputStream in) throws ClusterException {
        try {
            ClusterTcpMsg msg = encoder.decodeTcpMsg(in);
            
            if (DebugFlags.CLUSTER == true) {
                Utils.debug("Received TCP message: " + msg);
            }
            
            return msg;
        }
        catch (IOException e) {
            throw new ClusterException(L10n.format("SRVC.CLUSTER.ERR20"), e);
        }
        catch (MarshalException e) {
            throw new ClusterException(L10n.format("SRVC.CLUSTER.ERR21"), e);
        }
    }

    /**
     * 
     * @author <a href="mailto:dsetraky@fitechlabs.com">Dmitriy Setrakyan</a>
     */
    private class McastListener extends SysThread {
        /** */
        private BoxedRefSync syncSock = new BoxedRefSync(null);

        /** */
        private boolean isThrottle = false;

        /** */
        private ClusterNodeImpl localNode;
        
        /**
         * @throws ClusterException
         */
        McastListener() throws ClusterException {
            // Give this thread a slightly higher priority than
            // other application threads have.
            super("cluster-multicast-listener", SYS_ABOVE_NORMAL_PRIORITY);

            try {
                createSocket();
            }
            catch (IOException e) {
                throw new ClusterException(L10n.format("SRVC.CLUSTER.ERR23", config.getMcastGroup()), e);
            }
            
            localNode = topologyMgr.getLocalNode();
        }

        /**
         * @see Thread#interrupt()
         */
        public void interrupt() {
            super.interrupt();

            // Unblock the socket if it's blocked waiting for I/O.
            Utils.close((MulticastSocket)syncSock.get());
        }

        /**
         * @see SysThread#cleanup()
         */
        protected void cleanup() {
            // Unblock the socket if it's blocked waiting for I/O.
            Utils.close((MulticastSocket)syncSock.get());
        }
        
        /**
         * 
         * @throws IOException
         */
        private void createSocket() throws IOException {
            MulticastSocket sock;
            
            // Note that we don't disable loopback
            // because it does not work properly with
            // multiple nodes on the same host.
            if (Utils.getLocalIpAddrs().length == 1) {
                sock = new MulticastSocket(config.getMcastPort());
            }
            else {
                sock = new MulticastSocket(new InetSocketAddress(config.getLocalAddress(), config.getMcastPort()));
            }
            
            sock.setSoTimeout(config.getHeartbeatLossThreshold() * (int)config.getHeartbeatFreq());
            
            sock.joinGroup(config.getMcastGroup());
            
            logger.log(L10n.format("SRVC.CLUSTER.TXT6", sock.getLocalAddress(), new Integer(sock.getLocalPort())));
            
            syncSock.set(sock);
        }

        /**
         * @see SysThread#body()
         */
        protected void body() {
            DatagramPacket packet = new DatagramPacket(new byte[ClusterUdpMsg.BYTE_SIZE], ClusterUdpMsg.BYTE_SIZE);
            
            ClusterUdpMsg msg = new ClusterUdpMsg();

            while (true) {
                checkInterrupted();
                
                try {
                    if (syncSock.get() == null) {
                        createSocket();
                    }
                    
                    checkInterrupted();
                    
                    // Block waiting for the next multicast message.
                    ((MulticastSocket)syncSock.get()).receive(packet);

                    try {
                        encoder.decodeUdpMsg(msg, packet.getData(), 0);
                    }
                    catch (ClusterException e) {
                        logger.error(L10n.format("SRVC.CLUSTER.ERR20"), e);

                        // Go on to the next iteration.
                        continue;
                    }

                    // If message is not a from this node, then handle it.
                    if (isSameNode(msg) == false) {
                        if (DebugFlags.CLUSTER == true) {
                            Utils.debug("Received multicast message: " + msg);
                        }
                        
                        switch (msg.getMsgType()) {
                            case ClusterUdpMsg.DISCOVER: {
                                protocolMgr.handleUdpMsg(msg);
                                
                                break;
                            }
                            
                            case ClusterUdpMsg.HEARTBEAT: {
                                // Heartbeats are processed in the same thread.
                                hrtbeatMgr.handleHeartbeat(msg);
                                
                                break;
                            }
                        }

                        if (DebugFlags.CLUSTER == true) {
                            Utils.debug("Processed multicast message: " + msg);
                        }
                    }

                    isThrottle = false;
                } 
                catch(SocketTimeoutException e) {
                    // No-op.
                }
                catch (IOException e) {
                    // If thread has been interrupted, then it will be
                    // cancelled at the beginning of the loop.
                    if (isInterrupted() == false) {
                        if (isThrottle == false) {
                            logger.error(L10n.format("SRVC.CLUSTER.ERR20"), e);
            
                            isThrottle = true;
                        }
            
                        Utils.close((MulticastSocket)syncSock.get());
            
                        // Socket will be recreated on the next iteration.
                        syncSock.set(null);
            
                        Utils.sleep(ERR_WAIT_TIME);
                    }
                }
            }
        }
        
        /**
         * 
         * @param msg
         * @return TBD
         */
        private boolean isSameNode(ClusterUdpMsg msg) {
            return msg.getAddress().equals(localNode.getAddress()) == true && msg.getPort() == localNode.getPort();
        }
    }

    /**
     * 
     * @author <a href="mailto:dsetraky@fitechlabs.com">Dmitriy Setrakyan</a>
     */
    private class TcpListener extends SysThread {
        /** Socket to accept TCP connections on. */
        private BoxedRefSync syncServer = new BoxedRefSync(null);

        /** */
        private boolean isThrottle = false;

        /**
         * @throws ClusterException
         */
        TcpListener() throws ClusterException {
            // Give this thread a slightly higher priority than
            // other application threads have.
            super("cluster-tcp-listener", SYS_ABOVE_NORMAL_PRIORITY);

            try {
                createServer();
            }
            catch (IOException e) {
                throw new ClusterException(L10n.format("SRVC.CLUSTER.ERR25",
                    config.getLocalAddress().getHostAddress(), new Integer(config.getLocalPort())), e);
            }
        }

        /**
         * @see Thread#interrupt()
         */
        public void interrupt() {
            super.interrupt();

            // Unblock the socket if it's blocked waiting for I/O.
            Utils.close((ServerSocket)syncServer.get());
        }

        /**
         * @see SysThread#cleanup()
         */
        protected void cleanup() {
            // Unblock the socket if it's blocked waiting for I/O.
            Utils.close((ServerSocket)syncServer.get());
        }

        /**
         * 
         * @throws IOException
         */
        private void createServer() throws IOException {
            ServerSocket server = new ServerSocket();

            server.bind(new InetSocketAddress(config.getLocalAddress(), config.getLocalPort()));
            
            syncServer.set(server);
        }
        
        /**
         * @see SysThread#body()
         */
        protected void body() {
            while (true) {
                checkInterrupted();
                
                try {
                    // Server can only be null in case of error.
                    if (syncServer.get() == null) {
                        createServer();
                    }
                    
                    checkInterrupted();
                    
                    final Socket sock = ((ServerSocket)syncServer.get()).accept();
                    
                    InputStream in = null;
                    
                    final ClusterTcpMsg msg;
                    
                    try {
                        if (DebugFlags.CLUSTER == true) {
                            Utils.debug("Receiving message from: " + sock.getInetAddress());
                        }

                        in = sock.getInputStream();
                        
                        // Read data right away to avoid filling up the internal buffer.
                        msg = encoder.decodeTcpMsg(in);
                        
                        if (DebugFlags.CLUSTER == true) {
                            Utils.debug("Received TCP message: " + msg);
                        }
                    }
                    catch (IOException e) {
                        Utils.close(in);
                        Utils.close(sock);
                        
                        logger.error(L10n.format("SRVC.CLUSTER.ERR20"), e);
                        
                        continue;
                    }
                    catch (MarshalException e) {
                        Utils.close(in);
                        Utils.close(sock);
                        
                        logger.error(L10n.format("SRVC.CLUSTER.ERR21"), e);
                        
                        continue;
                    }
                    
                    // Need final variable to be used in inner class.
                    final InputStream inStream = in;

                    // Process in handler thread.
                    msgHandler.addTask(new Runnable() {
                        /**
                         * @see Runnable#run()
                         */
                        public void run() {
                            OutputStream out = null;
                            
                            try {
                                out = sock.getOutputStream();

                                switch (msg.getMsgType()) {
                                    case ClusterTcpMsg.SEED_CHECK:
                                    case ClusterTcpMsg.ADD:
                                    case ClusterTcpMsg.REMOVE:
                                    case ClusterTcpMsg.DISCOVER: {
                                        protocolMgr.handleTcpMsg(msg, out);
                                        
                                        break;
                                    }
                                    
                                    case ClusterTcpMsg.PING:
                                    case ClusterTcpMsg.LOCK:
                                    case ClusterTcpMsg.UNLOCK: {
                                        lockMgr.handleTcpMsg(msg, out);
                                        
                                        break;
                                    }
                                    
                                    default: { assert false : "Invalid TCP message recevied: " + msg; }
                                }
                                
                                if (DebugFlags.CLUSTER == true) {
                                    Utils.debug("Processed TCP message: " + msg);
                                }
                            }
                            catch (IOException e) {
                                logger.error(L10n.format("SRVC.CLUSTER.ERR20"), e);
                            }
                            finally {
                                Utils.close(inStream);
                                Utils.close(out);
                                Utils.close(sock);
                            }
                        }
                    });
                    
                    isThrottle = false;
                }
                catch (IOException e) {
                    // If thread has been interrupted, then it will be
                    // cancelled at the beginning of the loop.
                    if (isInterrupted() == false) {
                        if (isThrottle == false) {
                            logger.error(L10n.format("SRVC.CLUSTER.ERR20"), e);

                            isThrottle = true;
                        }
                
                        Utils.close((ServerSocket)syncServer.get());

                        // Socket will be recreated on the next iteration.
                        syncServer.set(null);

                        // Only wait if this is the second error in the row.
                        Utils.sleep(ERR_WAIT_TIME);
                    }
                }
            }
        }
    }
}
