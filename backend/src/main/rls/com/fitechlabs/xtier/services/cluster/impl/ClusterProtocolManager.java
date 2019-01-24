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
import com.fitechlabs.xtier.utils.*;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * @author <a href="mailto:dsetraky@fitechlabs.com">Dmitriy Setrakyan</a>
 */
class ClusterProtocolManager {
    /** */
    private Logger log;
    
    /** */
    private ClusterLockManager lockMgr;
    
    /** */
    private ClusterConfig cfg;
    
    /** */
    private ClusterTopologyManager topMgr;
    
    /** */
    private ReplyManager replyMgr;
    
    /** */
    private ClusterCommManager commMgr;
    
    /** */
    private ClusterEventManager evtMgr;
    
    /** */
    private boolean isStarted = false;
    
    /** */
    private boolean isRetryStart = false;
    
    /** */
    private final Object startMutex = new Object();
    
    /**
     * @param log
     * @param cfg
     */
    ClusterProtocolManager(Logger log, ClusterConfig cfg) {
        this.log = log;
        this.cfg = cfg;
        
        replyMgr = new ReplyManager();
    }

    /**
     * 
     * @param lockMgr
     * @param topMgr
     * @param commMgr
     * @param evtMgr
     * @throws ClusterException
     */
    void start(ClusterLockManager lockMgr, ClusterTopologyManager topMgr, ClusterCommManager commMgr,
        ClusterEventManager evtMgr) throws ClusterException {
        this.lockMgr = lockMgr;
        this.topMgr = topMgr;
        this.commMgr = commMgr;
        this.evtMgr = evtMgr;
    }
    
    /**
     * 
     */
    void stop() {
        // No-op.
    }
    
    /**
     * 
     * @throws ClusterException
     */
    void joinCluster() throws ClusterException {
        ClusterNodeImpl local = topMgr.getLocalNode();
        
        ClusterUdpMsg mcastDisco = new ClusterUdpMsg(ClusterUdpMsg.DISCOVER);
        
        mcastDisco.setAddress(local.getAddress());
        mcastDisco.setPort(local.getPort());

        while (true) {
            boolean retry = false;
            
            InetSocketAddress addr = null;
            
            // Try to send multicast request to discover lock manager.
            for (int i = 0, n = cfg.getRetries(); i < n && isRetryStart == false; i++) {
                int msgId = i + 1;
                
                mcastDisco.setMsgId(msgId);
                
                // 1. Prepare for sending multicast message.
                replyMgr.onSend(msgId);
                
                // 2. Send multicast message.
                commMgr.send(mcastDisco);
                
                // 3. Wait for TCP reply to multicast message.
                Utils.sleep(cfg.getTimeout());

                // 4. Get reply to multicast message, if any.
                ClusterTcpMsg reply = replyMgr.getLastReply();
                
                if (reply != null) {
                    if (reply.getBool("concurrent") == true) {
                        // In case of concurrent startup, node with lower IP gets to proceed.
                        if (isLower(local.getAddress(), local.getPort(), reply.getAddr(), reply.getInt32("port")) == false) {
                            // Restart from scratch.
                            retry = true;

                            // Break from 'for' loop.
                            break;
                        }
                        // This node was chosen to proceed.
                        // Move to the next multicast attempt.
                        continue;
                    }

                    // We got reply from started node.
                    addr = new InetSocketAddress(reply.getAddr(), reply.getInt32("port"));
                    
                    // Break from 'for' loop.
                    break;
                }
            }
            
            // If didn't get reply for multicast, try contacting seed nodes.
            if (addr == null) {
                List seeds = cfg.getSeedNodes();
                
                ClusterTcpMsg seedCheck = new ClusterTcpMsg(ClusterTcpMsg.SEED_CHECK);
                
                seedCheck.putAddr(local.getAddress());
                seedCheck.putInt32("port", local.getPort());
                
                // Contact all seed nodes, until we get a valid response.
                for (int i = 0, n = seeds.size(); i < n && retry == false; i++) {
                    InetSocketAddress seed = (InetSocketAddress)seeds.get(i);
                    
                    InetAddress seedAddr = seed.getAddress();
                    int seedPort = seed.getPort();
                    
                    // Skip yourself.
                    if (seedAddr.equals(local.getAddress()) == true && seedPort == local.getPort()) {
                        if (DebugFlags.CLUSTER == true) {
                            Utils.debug("Skipping itself from list of seed nodes: " + seed);
                        }
                        
                        // Move on to the next seed node.
                        continue;
                    }
                    
                    try {
                        // Note that we don't retry if connection fails (hence 'false').
                        ClusterTcpMsg reply = commMgr.sendWait(seedCheck, seedAddr, seedPort, false);
                        
                        if (reply.getBool("concurrent") == true) {
                            // Only nodes with lower IP address and port are chosen to proceed.
                            // We ignore all nodes with higher IP address and port.
                            if (isLower(local.getAddress(), local.getPort(), seedAddr, seedPort) == false) {
                                retry = true;
                            }
                        }
                        else {
                            // Other node is started, retrieve address of cluster manager.
                            addr = new InetSocketAddress(reply.getAddr(), reply.getInt32("port"));
                            
                            break;
                        }
                    }
                    catch (ClusterConnectException e) {
                        // If we could not connect, then assume that ping failed.
                        // Move on to the next node.
                        log.warning(L10n.format("SRVC.CLUSTER.WRN7", seed.getAddress().getHostAddress(),
                            new Integer(seed.getPort())));
                    }
                    catch (ClusterException e) {
                        // If we successfully connected, assume that node is alive.
                        retry = true;
                        
                        // In case of error, wait some interval.
                        Utils.sleep(cfg.getTimeout());
                    }
                }
            }
            
            // If lock manager was discovered.
            if (addr != null) {
                while (retry == false) {
                    ClusterTcpMsg reply = null;
                    
                    try {
                        reply = lockMgr.remoteLockForJoin(addr);
                    }
                    catch (ClusterLockException e) {
                        // Lock manager changed while acquiring lock.
                        retry = true;
                        
                        break;
                    }

                    if (reply == null) {
                        // Wait before retrying.
                        Utils.sleep(cfg.getTimeout());
                        
                        // Try acquiring lock again.
                        continue;
                    }
                        
                    try {
                        List nodes = reply.getList("nodes");
                        
                        // Note that while holding a lock on cluster, we don't care
                        // about other nodes concurrently starting.
                        synchronized (startMutex) {
                            isStarted = true;

                            topMgr.onJoinCluster(reply.getInt32("node-id"), nodes);
                        }
                        
                        ClusterTcpMsg addMsg = new ClusterTcpMsg(ClusterTcpMsg.ADD);
                        
                        addMsg.putMarshalObj("node", topMgr.getLocalNode());

                        commMgr.sendWait(addMsg, nodes);
                    
                        logTopologyChange(L10n.format("SRVC.CLUSTER.TXT9", new Integer(topMgr.getSize())));
                        
                        // Successfully joined cluster.
                        return;
                    }
                    finally {
                        lockMgr.remoteUnlock();
                    }
                }
            }
            
            synchronized (startMutex) {
                if (isRetryStart == false && retry == false) {
                    isStarted = true;
                    
                    // This node is first in cluster.
                    topMgr.onJoinCluster();
                    
                    logTopologyChange(L10n.format("SRVC.CLUSTER.TXT9", new Integer(topMgr.getSize())));
                    
                    return;
                }

                // Retry from scratch.
                Utils.waitOn(startMutex, cfg.getTimeout() * cfg.getRetries());

                isRetryStart = false;
            }
        }
    }
    
    /**
     * 
     * @throws ClusterException
     */
    void leaveCluster() throws ClusterException {
        int localId = topMgr.getLocalNode().getNodeId();
        
        while (true) {
            if (lockMgr.remoteLockForLeave() == true) {
                try {
                    ClusterTcpMsg leaveMsg = new ClusterTcpMsg(ClusterTcpMsg.REMOVE);
                    
                    leaveMsg.putInt32("node-id", localId);
                    
                    commMgr.sendWait(leaveMsg, topMgr.getNonLocalNodes());
                    
                    // Change topology while holding lock.
                    synchronized (startMutex) {
                        isStarted = false;
                        
                        topMgr.onRemoveNode(localId);
                    }
                    
                    logTopologyChange(L10n.format("SRVC.CLUSTER.TXT10", new Integer(topMgr.getSize())));
                    
                    return;
                }
                finally {
                    lockMgr.remoteUnlock();
                }
            }
                
            // Wait before retrying.
            Utils.sleep(cfg.getTimeout());
        }
    }

    /**
     * 
     * @param msg
     * @param out
     */
    void handleTcpMsg(ClusterTcpMsg msg, OutputStream out) {
        switch (msg.getMsgType()) {
            // Handle TCP add node request.
            case ClusterTcpMsg.ADD: {
                // Locally lock to dissallow any internal topology changes.
                lockMgr.localLock();
                
                try {
                    ClusterNodeImpl newNode = (ClusterNodeImpl)msg.getMarshalObjNotNull("node");
                    
                    if (topMgr.hasNode(newNode.getAddress(), newNode.getPort()) == true) {
                        log.warning(L10n.format("SRVC.CLUSTER.WRN8", newNode));
                    }
                    else {
                        topMgr.onAddNode(newNode);
                        
                        evtMgr.addEvent(ClusterListener.NODE_JOINED, newNode, topMgr.getVersion());
                    }
                    
                    try {
                        // Send reply.
                        commMgr.send(new ClusterTcpMsg(ClusterTcpMsg.ACK), out);
                        
                        logTopologyChange(L10n.format("SRVC.CLUSTER.TXT11", new Integer(topMgr.getSize()),
                            nodeToStr(newNode)));
                    }
                    catch (ClusterException e) {
                        topMgr.onRemoveNode(newNode.getNodeId());
                        
                        // Assume that remote node is failed.
                        evtMgr.addEvent(ClusterListener.NODE_FAILED, newNode, topMgr.getVersion());
                        
                        log.error(L10n.format("SRVC.CLUSTER.ERR31", newNode), e);
                    }
                }
                finally {
                    lockMgr.localUnlock();
                }
                
                // Dispatch 'add-node' event.
                evtMgr.dispatch();
                
                break;
            }
            
            // Handle TCP remove node request.
            case ClusterTcpMsg.REMOVE: {
                // Locally lock to dissallow any internal topology changes.
                lockMgr.localLock();

                try {
                    int nodeId = msg.getInt32("node-id");
                    
                    ClusterNodeImpl node = topMgr.getNode(nodeId);
                    
                    if (node == null) {
                        log.warning(L10n.format("SRVC.CLUSTER.WRN9", new Integer(nodeId)));
                    }
                    else {
                        topMgr.onRemoveNode(nodeId);
                        
                        evtMgr.addEvent(ClusterListener.NODE_LEFT, node, topMgr.getVersion());
                    }
                    
                    try {
                        // Send reply.
                        commMgr.send(new ClusterTcpMsg(ClusterTcpMsg.ACK), out);
                    }
                    catch (ClusterException e) {
                        // No attempt to rollback.
                        log.error(L10n.format("SRVC.CLUSTER.ERR33"), e);
                    }
                    
                    if (node != null) {
                        logTopologyChange(L10n.format("SRVC.CLUSTER.TXT12", new Integer(topMgr.getSize()),
                            nodeToStr(node)));
                    }
                }
                finally {
                    lockMgr.localUnlock();
                }
                
                // Dispatch 'remove-node' event.
                evtMgr.dispatch();
                
                break;
            }
            
            // Handle seed-check which may happen only during startup.
            case ClusterTcpMsg.SEED_CHECK: {
                ClusterNodeImpl local = topMgr.getLocalNode();
                
                ClusterTcpMsg reply = new ClusterTcpMsg(ClusterTcpMsg.SEED_CHECK);
                
                synchronized (startMutex) {
                    if (isStarted == false) {
                        reply.putBool("concurrent", true);
                        
                        if (isLower(local.getAddress(), local.getPort(), msg.getAddr(), msg.getInt32("port")) == false) {
                            isRetryStart = true;
                        }
                        
                        if (DebugFlags.CLUSTER == true) {
                            Utils.debug("Encountered concurrent startup [is-retry-start=" + isRetryStart + ']');
                        }
                    }
                    else {
                        reply.putBool("concurrent", false);
                        
                        ClusterNodeImpl mgr = topMgr.getMgr();
                        
                        // Encode address of cluster manager into the reply.
                        reply.putAddr(mgr.getAddress());
                        reply.putInt32("port", mgr.getPort());
                    }
                    
                    try {
                        commMgr.send(reply, out);
                    }
                    catch (ClusterException e) {
                        log.error(L10n.format("SRVC.CLUSTER.ERR20"), e);
                    }
                }
                
                break;
            }
            
            // Handle response to multicast advertisement which
            // may happen only during startup.
            case ClusterTcpMsg.DISCOVER: {
                replyMgr.onResponse(msg);
                
                break;
            }
            
            default: { assert false : "Invalid message type: " + msg.getMsgType(); }
        }
    }
    
    /**
     * Handles multicast discover requests.
     * 
     * @param msg Discover request.
     */
    void handleUdpMsg(ClusterUdpMsg msg) {
        assert msg.getMsgType() == ClusterUdpMsg.DISCOVER;
        
        ClusterTcpMsg reply = new ClusterTcpMsg(ClusterTcpMsg.DISCOVER);
        
        reply.putInt32("msg-id", msg.getMsgId());
        
        synchronized (startMutex) {
            if (isStarted == true) {
                if (topMgr.isMgr() == true) {
                    ClusterNodeImpl mgr = topMgr.getMgr();

                    assert mgr == topMgr.getLocalNode();
                    
                    reply.putAddr(mgr.getAddress());
                    reply.putInt32("port", mgr.getPort());
                    reply.putBool("concurrent", false);
                }
                else {
                    // Ignore discovery request if this node is not a cluster manager.
                    return;
                }
            }
            else {
                reply.putBool("concurrent", true);
                reply.putAddr(cfg.getLocalAddress());
                reply.putInt32("port", cfg.getLocalPort());
                
                if (isLower(cfg.getLocalAddress(), cfg.getLocalPort(), msg.getAddress(), msg.getPort()) == false) {
                    isRetryStart = true;
                }
                
                if (DebugFlags.CLUSTER == true) {
                    Utils.debug("Encountered concurrent startup [is-retry-start=" + isRetryStart + ']');
                }
            }
            
            try {
                commMgr.send(reply, msg.getAddress(), msg.getPort());
            }
            // Connection failure is not an error, since the starting node
            // could have stopped by now.
            catch (ClusterConnectException e) {
                if (DebugFlags.CLUSTER == true) {
                    Utils.debug("Starting node has stopped [address=" + msg.getAddress() + ", port=" +
                        msg.getPort() + ']');
                }
            }
            catch (ClusterException e) {
                log.error(L10n.format("SRVC.CLUSTER.ERR34", msg.getAddress().getHostAddress(),
                    new Integer(msg.getPort())), e);
            }
        }
    }
    
    /**
     * Returns <tt>true</tt> if address1/port1 are smaller than address2/port2,
     * and <tt>false</tt> if address1/port1 are greater than address2/port2.
     * 
     * @param addr1
     * @param port1
     * @param addr2
     * @param port2
     * @return TBD
     */
    private boolean isLower(InetAddress addr1, int port1, InetAddress addr2, int port2) {
        byte[] ip1 = addr1.getAddress();
        byte[] ip2 = addr2.getAddress();

        // First compare IP addresses - the one with smaller IP is chosen to proceed.
        for (int i = 0; i < ip1.length; i++) {
            if (ip1[i] < ip2[i]) {
                return true;
            }
            else if (ip1[i] > ip2[i]) {
                return false;
            }
        }

        // If IP addresses are equal, then the one with smaller
        // port is lower.
        return port1 < port2;
    }
    
    /**
     * 
     * @param node
     * @return TBD
     */
    private String nodeToStr(ClusterNodeImpl node) {
        return L10n.format("SRVC.CLUSTER.TXT7", node.getAddress().getHostAddress(), new Integer(node.getPort()));
    }
    
    /**
     * 
     * @param msg
     */
    private void logTopologyChange(String msg) {
        log.log(msg);
        
        log.log(L10n.format("SRVC.CLUSTER.TXT8", Utils.coll2Str(topMgr.getAllNodes())));
    }

    /**
     * 
     * @author <a href="mailto:dsetraky@fitechlabs.com">Dmitriy Setrakyan</a>
     */
    private class ReplyManager {
        /** */
        private ClusterTcpMsg lastRes;

        /** */
        private long msgId;
        
        /**
         * 
         * @param msgId
         */
        synchronized void onSend(int msgId) {
            lastRes = null;
            
            this.msgId = msgId;
        }
        
        /**
         * 
         * @param res
         */
        void onResponse(ClusterTcpMsg res) {
            synchronized (startMutex) {
                synchronized (this) {
                    // If response is not stale.
                    if (res.getMsgId() == msgId) {
                        // If already got a valid response.
                        if (lastRes != null) {
                            if (lastRes.getBool("concurrent") == false) {
                                return;
                            }
                            
                            if (res.getBool("concurrent") == true) {
                                // If new response has a lower IP/port combination,
                                // then replace the last response.
                                if (isLower(res.getAddr(), res.getInt32("port"), lastRes.getAddr(),
                                    lastRes.getInt32("port")) == true) {
                                    lastRes = res;
                                    
                                    return;
                                }
                            }
                        }
                        
                        lastRes = res;
    
                        // If this response if from started node.
                        if (res.getBool("concurrent") == false) {
                            startMutex.notifyAll();
                        }
                    }
                }
            }
        }
        
        /**
         * 
         * @return TBD
         */
        synchronized ClusterTcpMsg getLastReply() {
            return lastRes;
        }
    }
}
