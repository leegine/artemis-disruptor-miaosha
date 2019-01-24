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
 * $Project$
 * $Workfile$
 * $Date$
 * $Revision$
 */

package com.fitechlabs.xtier.services.cluster.impl;

import com.fitechlabs.xtier.debug.*;
import com.fitechlabs.xtier.l10n.*;
import com.fitechlabs.xtier.services.cluster.*;
import com.fitechlabs.xtier.services.log.*;
import com.fitechlabs.xtier.threads.*;
import com.fitechlabs.xtier.utils.*;
import java.io.*;
import java.net.*;

/**
 * @author <a href="mailto:dsetraky@fitechlabs.com">Dmitriy Setrakyan</a>
 */
class ClusterHeartbeatManager {
    /** */
    private static final long ERR_WAIT_TIME = 2000;
    
    /** */
    private Logger log;
    
    /** */
    private ClusterConfig cfg;
    
    /** */
    private HeartbeatSender sender;
    
    /** */
    private ClusterTopologyManager topMgr;
    
    /** */
    private ClusterEncoder encoder;
    
    /** */
    private ClusterEventManager evtMgr;

    /**
     * 
     * @param logger
     * @param cfg
     * @param encoder
     * @param topMgr
     * @param evtMgr
     * @throws ClusterException
     */
    ClusterHeartbeatManager(Logger logger, ClusterConfig cfg, ClusterEncoder encoder,
        ClusterTopologyManager topMgr, ClusterEventManager evtMgr) throws ClusterException {
        this.encoder = encoder;
        this.cfg = cfg;
        this.log = logger;
        this.topMgr = topMgr;
        this.evtMgr = evtMgr;

        sender = new HeartbeatSender();
    }

    /**
     * 
     * @throws ClusterException
     */
    void start() throws ClusterException {
        sender.start();
    }
    
    /**
     * 
     */
    void stop() {
        Utils.stopThread(sender);
    }
    
    /**
     * @param hrbt
     */
    void handleHeartbeat(ClusterUdpMsg hrbt) {
        if (topMgr.isJoined() == true) {
            ClusterNodeImpl node = topMgr.getNode(hrbt.getAddress(), hrbt.getPort());
    
            if (node != null) {
                node.refresh(hrbt.getCpuLoad(), hrbt.getTotalMemory(), hrbt.getFreeMemory());
            }
            else {
                // Dispatch callbacks for unknown node.
                evtMgr.dispatchUknownNodeError(new InetSocketAddress(hrbt.getAddress(), hrbt.getPort()));
            }
        }
    }

    /**
     * 
     * @author <a href="mailto:dsetraky@fitechlabs.com">Dmitriy Setrakyan</a>
     */
    private class HeartbeatSender extends SysThread {
        /** */
        private MulticastSocket socket;

        /** */
        private boolean isThrottleErrs = false;

        /**
         * 
         * @throws ClusterException
         */
        HeartbeatSender() throws ClusterException {
            // Give this thread a slightly higher priority than
            // other application threads have.
            super("cluster-heartbeat-sender", SYS_ABOVE_NORMAL_PRIORITY);

            try {
                createSocket();
            }
            catch (IOException e) {
                throw new ClusterException(L10n.format("SRVC.CLUSTER.ERR24"), e);
            }
        }

        /**
         * 
         * @throws IOException
         */
        private void createSocket() throws IOException {
            // Note that we don't disable loopback because it does not
            // work properly with multiple nodes on the same host.
            socket = new MulticastSocket(new InetSocketAddress(cfg.getLocalAddress(), 0));

            socket.setTimeToLive(cfg.getMcastTtl());
        }

        /**
         * @see SysThread#body()
         */
        protected void body() {
            long freq = cfg.getHeartbeatFreq();

            byte[] data = new byte[ClusterUdpMsg.BYTE_SIZE];

            // Create datagram packet to send heartbeat in.
            DatagramPacket packet = new DatagramPacket(data, data.length);

            packet.setAddress(cfg.getMcastGroup());
            packet.setPort(cfg.getMcastPort());

            ClusterUdpMsg hrtbeat = new ClusterUdpMsg(ClusterUdpMsg.HEARTBEAT);

            ClusterNodeImpl localNode = topMgr.getLocalNode();

            // Preset local address and port.
            hrtbeat.setAddress(localNode.getAddress());
            hrtbeat.setPort(localNode.getPort());

            while (true) {
                checkInterrupted();
                
                synchronized (topMgr) {
                    while (topMgr.hasNonLocalNodes() == false) {
                        if (DebugFlags.CLUSTER == true) {
                            Utils.debug("Heartbeat sender will wait until there are more than one nodes.");
                        }
                        
                        Utils.waitOn(topMgr);
                    }
                }

                try {
                    if (socket == null) {
                        createSocket();
                    }

                    // Set total and free memory for local node into heartbeat.
                    hrtbeat.setTotalMemory(localNode.getTotalMemory());
                    hrtbeat.setFreeMemory(localNode.getFreeMemory());
                    hrtbeat.setCpuLoad(localNode.getCpuLoad());

                    encoder.encodeUdpMsg(hrtbeat, data, 0);

                    // Send heartbeat.
                    socket.send(packet);
                    
                    isThrottleErrs = false;

                    Utils.sleep(freq);
                }
                catch (IOException e) {
                    // Throttle error messages.
                    if (isThrottleErrs == false) {
                        log.error(L10n.format("SRVC.CLUSTER.ERR28"), e);
                    }

                    socket = null;

                    Utils.sleep(ERR_WAIT_TIME);

                    isThrottleErrs = true;
                }
            }
        }
    }
}
