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
import com.fitechlabs.xtier.utils.concurrent.*;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * @author <a href="mailto:dsetraky@fitechlabs.com">Dmitriy Setrakyan</a>
 */
class ClusterLockManager {
    /** */
    private static final int LOCK_CHANGED = 1;

    /** */
    private static final int LOCK_RETRY = 2;

    /** */
    private static final int LOCK_SUCCESS = 3;

    /** */
    private static final int LOCK_ERROR = 4;

    /** */
    private Logger log;

    /** */
    private ClusterConfig cfg;

    /** */
    private ClusterTopologyManager topMgr;

    /** */
    private ClusterCommManager commMgr;

    /** */
    private RWLock clusterLock = new WritePrefRWLock();

    /** */
    private LockOwner lockOwner = null;

    /** */
    private ClusterNodeImpl mgrNode = null;

    /** */
    private LockChecker lockChecker = null;

    /** */
    private final Object mutex = new Object();

    /**
     * @param log
     * @param cfg
     */
    ClusterLockManager(Logger log, ClusterConfig cfg) {
        this.log = log;
        this.cfg = cfg;

        lockChecker = new LockChecker();
    }

    /**
     *
     * @param topMgr
     * @param commMgr
     * @throws ClusterException
     */
    void start(ClusterTopologyManager topMgr, ClusterCommManager commMgr) throws ClusterException {
        this.topMgr = topMgr;
        this.commMgr = commMgr;

        lockChecker.start();
    }

    /**
     *
     */
    void stop() {
        Utils.stopThread(lockChecker);
    }

    /**
     *
     */
    void localLock() {
        clusterLock.acquireWrite();
    }

    /**
     *
     */
    void localUnlock() {
        clusterLock.releaseWrite();
    }

    /**
     * This method is called for acquiring lock during joining cluster.

     * @param addr
     * @return TBD
     * @throws ClusterException
     * @throws ClusterLockException
     */
    ClusterTcpMsg remoteLockForJoin(InetSocketAddress addr) throws ClusterException, ClusterLockException {
        ClusterNodeImpl local = topMgr.getLocalNode();

        ClusterTcpMsg lockReq = new ClusterTcpMsg(ClusterTcpMsg.LOCK);

        lockReq.putInt32("node-id", local.getNodeId());
        lockReq.putAddr(local.getAddress());
        lockReq.putInt32("port", local.getPort());
        lockReq.putInt64("timestamp", local.getStartTime());
        lockReq.putBool("is-next-mgr", false);

        try {
            // Don't retry connection, node could have left the cluster.
            ClusterTcpMsg reply = commMgr.sendWait(lockReq, addr.getAddress(), addr.getPort(), false);

            switch (reply.getInt32("status")) {
                case LOCK_SUCCESS: {
                    if (DebugFlags.CLUSTER == true) {
                        Utils.debug("Successfully acquired cluster lock from cluster manager: " + addr);
                    }

                    List nodes = reply.getList("nodes");

                    // Find the manager node.
                    for (int i = 0, n = nodes.size(); i < n; i++) {
                        ClusterNodeImpl node = (ClusterNodeImpl)nodes.get(i);

                        if (node.getAddress().equals(addr.getAddress()) == true && node.getPort() == addr.getPort()) {
                            synchronized (mutex) {
                                // Record where the lock was acquired from.
                                mgrNode = node;

                                return reply;
                            }
                        }
                    }

                    assert false;

                    // Never reached.
                    return null;
                }

                case LOCK_ERROR: { throw new ClusterException(reply.getUtf8Str("err-msg")); }

                // Null returned value singnifies that remote node should retry.
                case LOCK_RETRY: { return null; }

                // If cluster manager changed, the this node should retry starting routine.
                case LOCK_CHANGED: { throw new ClusterLockException(); }

                default: {
                    assert false : "Invalid lock status reply: " + reply.getInt32("status");

                    // Never reached.
                    return null;
                }
            }
        }
        catch (ClusterConnectException e) {
            // Remote node has shutdown.
            throw new ClusterLockException();
        }
    }

    /**
     * This method is called for acquiring cluster lock duing leaving cluster.

     * @return TBD
     * @throws ClusterException
     */
    boolean remoteLockForLeave() throws ClusterException {
        ClusterNodeImpl local = topMgr.getLocalNode();

        ClusterTcpMsg lockReq = new ClusterTcpMsg(ClusterTcpMsg.LOCK);

        ClusterNodeImpl mgr = null;

        boolean isLocalMgr = false;

        synchronized (mutex) {
            if (topMgr.isMgr() == true) {
                if (lockOwner == null) {
                    // First acquire lock locally.
                    acquireLock(local.getNodeId(), local.getAddress(), local.getPort(), local.getStartTime());

                    isLocalMgr = true;

                    // Find next inline manager and try to acquire lock from it.
                    mgr = topMgr.getNextMgr();

                    lockReq.putBool("is-next-mgr", true);

                    if (mgr == null) {
                        mgrNode = local;

                        // This node is the only one in cluster.
                        return true;
                    }
                }
                else {
                    assert lockOwner.isLocal() == false : "Local node already owns cluster lock.";

                    // Some remote node owns the lock.
                    return false;
                }
            }
            else {
                mgr = topMgr.getMgr();

                lockReq.putBool("is-next-mgr", false);
            }
        }

        lockReq.putInt32("node-id", local.getNodeId());
        lockReq.putAddr(local.getAddress());
        lockReq.putInt32("port", local.getPort());
        lockReq.putInt64("timestamp", local.getStartTime());

        try {
            ClusterTcpMsg reply = commMgr.sendWait(lockReq, mgr.getAddress(), mgr.getPort(), true);

            switch (reply.getInt32("status")) {
                case LOCK_SUCCESS: {
                    assert topMgr.hasNode(mgr.getNodeId()) == true : "Acquired lock from non-existent node: " + mgr;

                    synchronized (mutex) {
                        // Record where the lock was acquired from.
                        mgrNode = mgr;
                    }

                    if (DebugFlags.CLUSTER == true) {
                        Utils.debug("Successfully acquired cluster lock from cluster manager: " + mgr);
                    }

                    return true;
                }

                case LOCK_ERROR: { throw new ClusterException(reply.getUtf8Str("err-msg")); }

                case LOCK_CHANGED:
                case LOCK_RETRY: {
                    // If cluster manager is leaving cluser then reply should
                    // only be success or error.
                    assert isLocalMgr == false : "Invalid status reply: " + reply.getInt32("status");

                    // Either cluster manager changed, or somebody else is holding the lock.
                    return false;
                }

                default: {
                    assert false : "Invalid lock status: " + reply.getInt32("status");

                    // Never reached.
                    return false;
                }
            }
        }
        catch (ClusterConnectException e) {
            // If were acquiring a lock from next would-be manager.
            if (isLocalMgr == true) {
                synchronized (mutex) {
                    releaseLock();
                }

                throw e;
            }

            // If could not connect to manager, then
            // return failure to acquire lock.
            return false;
        }
        catch (ClusterException e) {
            // If were acquiring a lock from next would-be manager.
            if (isLocalMgr == true) {
                synchronized (mutex) {
                    releaseLock();
                }
            }

            throw e;
        }
    }

    /**
     *
     * @throws ClusterException
     */
    void remoteUnlock() throws ClusterException {
        ClusterNodeImpl local = topMgr.getLocalNode();

        ClusterNodeImpl mgr = null;

        synchronized (mutex) {
            assert mgrNode != null;

            if (lockOwner != null) {
                // If this node is cluster manager and is leaving cluster.
                if (lockOwner.isLocal() == true) {
                    releaseLock();
                }
            }

            if (mgrNode.isLocalNode() == true) {
                mgrNode = null;

                // This node is last in cluster.
                return;
            }

            mgr = mgrNode;

            // Successfully unlocked.
            mgrNode = null;
        }

        ClusterTcpMsg msg = new ClusterTcpMsg(ClusterTcpMsg.UNLOCK);

        msg.putAddr(local.getAddress());
        msg.putInt32("port", local.getPort());

        ClusterTcpMsg reply = commMgr.sendWait(msg, mgr.getAddress(), mgr.getPort(), true);

        switch (reply.getInt32("status")) {
            // Successfully released the lock.
            case LOCK_SUCCESS: {
                if (DebugFlags.CLUSTER == true) {
                    Utils.debug("Successfully released remote lock from cluster manager: " + mgr);
                }

                return;
            }

            case LOCK_ERROR: { throw new ClusterException(reply.getUtf8Str("err-msg")); }

            default: { assert false : "Invalid lock status reply: " + reply.getInt32("status"); }
        }
    }

    /**
     *
     * @param addr
     * @param port
     * @param timestamp
     * @return TBD
     */
    private boolean ping(InetAddress addr, int port, long timestamp) {
        ClusterTcpMsg ping = new ClusterTcpMsg(ClusterTcpMsg.PING);

        ping.putInt64("timestamp", timestamp);

        try {
            return commMgr.sendWait(ping, addr, port, true).getBool("success") == true;
        }
        catch (ClusterConnectException e) {
            // If we could not connect, then assume that ping failed.
            return false;
        }
        catch (ClusterException e) {
            // If we successfully connected, assume that node is alive.
            return true;
        }
    }

    /**
     *
     * @param nodeId
     * @param addr
     * @param port
     * @param timestamp
     */
    private void acquireLock(int nodeId, InetAddress addr, int port, long timestamp) {
        assert Thread.holdsLock(mutex) == true;
        assert lockOwner == null;

        lockOwner = new LockOwner(nodeId, addr, port, timestamp);

        mutex.notifyAll();
    }

    /**
     *
     */
    private void releaseLock() {
        assert Thread.holdsLock(mutex) == true;
        assert lockOwner != null;

        lockOwner = null;

        mutex.notifyAll();
    }

    /**
     *
     * @param msg
     * @param out
     */
    void handleTcpMsg(ClusterTcpMsg msg, OutputStream out) {
        switch (msg.getMsgType()) {
            case ClusterTcpMsg.LOCK: {
                boolean isNextMgr = msg.getBool("is-next-mgr");

                ClusterTcpMsg reply = new ClusterTcpMsg(ClusterTcpMsg.LOCK);

                synchronized (mutex) {
                    int nodeId = msg.getInt32("node-id");

                    boolean err = false;

                    if (topMgr.isJoined() == true) {
                        // If node is joining, check that it is not in the cluster.
                        if (nodeId == -1) {
                            InetAddress addr = msg.getAddr();
                            int port = msg.getInt32("port");

                            // Make sure that this node is the topology manager indeed and does not
                            // have the joining node in the topology.
                            if (topMgr.hasNode(addr, port) == true) {
                                err = true;

                                reply.putInt32("status", LOCK_ERROR);
                                reply.putUtf8Str("err-msg", L10n.format("SRVC.CLUSTER.ERR37", nodeToStr(addr, port)));
                            }
                        }
                        // If node is leaving, check that it is in the cluster.
                        else if (topMgr.hasNode(nodeId) == false) {
                            err = true;

                            reply.putInt32("status", LOCK_ERROR);
                            reply.putUtf8Str("err-msg", L10n.format("SRVC.CLUSTER.ERR17", new Integer(nodeId)));
                        }
                    }

                    if (err == false) {
                        // If this node is not manager or another node owns the lock.
                        if (topMgr.isJoined() == false || (topMgr.isMgr() == false && isNextMgr == false)) {
                            reply.putInt32("status", LOCK_CHANGED);
                        }
                        else if (lockOwner != null) {
                            assert isNextMgr == false;

                            reply.putInt32("status", LOCK_RETRY);
                        }
                        // No one owns the lock.
                        else {
                            try {
                                // If a new node is trying to join.
                                if (nodeId == -1) {
                                    assert topMgr.hasNode(msg.getAddr(), msg.getInt32("port")) == false;

                                    // put new node ID and the whole topology into reply.
                                    reply.putInt32("node-id", topMgr.createNodeId());
                                    reply.putList("nodes", new ArrayList(topMgr.getAllNodes()));
                                    reply.putInt32("status", LOCK_SUCCESS);

                                    acquireLock(nodeId, msg.getAddr(), msg.getInt32("port"), msg.getInt64("timestamp"));
                                }
                                else {
                                    ClusterNodeImpl node = topMgr.getNode(nodeId);

                                    assert node != null;

                                    reply.putInt32("status", LOCK_SUCCESS);

                                    acquireLock(nodeId, node.getAddress(), node.getPort(), node.getStartTime());
                                }
                            }
                            // If new ID for the joining node could not be created.
                            catch (ClusterException e) {
                                reply.putInt32("status", LOCK_ERROR);
                                reply.putUtf8Str("err-msg", e.getMessage());
                            }
                        }
                    }
                }

                try {
                    commMgr.send(reply, out);
                }
                catch (ClusterException e) {
                    // If failed to acknowledge acquired lock, we release lock.
                    if (reply.getInt32("status") == LOCK_SUCCESS) {
                        synchronized (mutex) {
                            // Check again since we were sending message outside of synchronization.
                            if (lockOwner != null && lockOwner.isSame(msg.getAddr(), msg.getInt32("port")) == true) {
                                releaseLock();
                            }
                        }
                    }

                    log.error(L10n.format("SRVC.CLUSTER.ERR26", nodeToStr(msg.getAddr(), msg.getInt32("port"))), e);
                }

                break;
            }

            case ClusterTcpMsg.UNLOCK: {
                ClusterTcpMsg reply = new ClusterTcpMsg(ClusterTcpMsg.UNLOCK);

                synchronized (mutex) {
                    // Since any message can be retried, we must make sure that node
                    // is legally releasing the lock.
                    if (lockOwner != null && lockOwner.isSame(msg.getAddr(), msg.getInt32("port")) == true) {
                        releaseLock();
                    }

                    reply.putInt32("status", LOCK_SUCCESS);
                }

                try {
                    commMgr.send(reply, out);
                }
                catch (ClusterException e) {
                    log.error(L10n.format("SRVC.CLUSTER.ERR36", nodeToStr(msg.getAddr(), msg.getInt32("port"))), e);
                }

                break;
            }

            // Handle ping request.
            case ClusterTcpMsg.PING: {
                ClusterTcpMsg reply = new ClusterTcpMsg(ClusterTcpMsg.PING);

                reply.putBool("success", topMgr.getLocalNode().getStartTime() == msg.getInt64("timestamp"));

                try {
                    commMgr.send(reply, out);
                }
                catch (ClusterException e) {
                    log.error(L10n.format("SRVC.CLUSTER.ERR20"), e);
                }

                break;
            }

            default: { assert false : "Invalid lock message recevied: " + msg.getMsgType(); }
        }
    }

    /**
     *
     * @param addr
     * @param port
     * @return TBD
     */
    private String nodeToStr(InetAddress addr, int port) {
        return L10n.format("SRVC.CLUSTER.TXT7", addr.getHostAddress(), new Integer(port));
    }

    /**
     *
     * @author <a href="mailto:dsetraky@fitechlabs.com">Dmitriy Setrakyan</a>
     */
    private class LockOwner {
        /** */
        private InetAddress addr;

        /** */
        private int port;

        /** */
        private long timestamp;

        /** */
        private int nodeId;

        /**
         * @param nodeId
         * @param addr
         * @param port
         * @param timestamp
         */
        LockOwner(int nodeId, InetAddress addr, int port, long timestamp) {
            this.nodeId = nodeId;
            this.addr = addr;
            this.port = port;
            this.timestamp = timestamp;
        }

        /**
         *
         * @param addr
         * @param port
         * @return TBD
         */
        boolean isSame(InetAddress addr, int port) {
            assert addr != null;

            return this.addr.equals(addr) == true && this.port == port;
        }

        /**
         *
         * @return TBD
         */
        boolean isLocal() {
            ClusterNodeImpl local = topMgr.getLocalNode();

            return local.getAddress().equals(addr) == true && local.getPort() == port;
        }

        /**
         * @return TBD
         */
        InetAddress getAddr() {
            return addr;
        }

        /**
         * @return TBD
         */
        long getTimestamp() {
            return timestamp;
        }

        /**
         * @return TBD
         */
        int getPort() {
            return port;
        }

        /**
         * Gets the value of node ID.
         *
         * @return Value of node ID.
         */
        int getNodeId() {
            return nodeId;
        }

        /**
         * @see Object#equals(Object)
         */
        public boolean equals(Object obj) {
            LockOwner other = (LockOwner)obj;

            return nodeId == other.nodeId && timestamp == other.timestamp && port == other.port &&
                addr.equals(other.addr) == true;
        }

        /**
         * @see Object#toString()
         */
        public String toString() {
            return L10n.format("SRVC.CLUSTER.TXT13", addr.getHostAddress(), new Integer(port));
        }
    }

    /**
     *
     * @author <a href="mailto:dsetraky@fitechlabs.com">Dmitriy Setrakyan</a>
     */
    private class LockChecker extends SysThread {
        /**
         *
         */
        LockChecker() {
            super("cluster-lock-checker", SYS_ABOVE_NORMAL_PRIORITY);
        }

        /**
         * @see SysThread#body()
         */
        protected void body() {
            ClusterNodeImpl local = topMgr.getLocalNode();

            while (true) {
                checkInterrupted();

                LockOwner temp = null;

                synchronized (mutex) {
                    while (lockOwner == null) {
                        Utils.waitOn(mutex);
                    }

                    assert lockOwner != null;

                    // Wait for timeout before pinging the lock owner.
                    Utils.waitOn(mutex, cfg.getTimeout());

                    if (lockOwner != null && lockOwner.isSame(local.getAddress(), local.getPort()) == false) {
                        temp = new LockOwner(lockOwner.getNodeId(), lockOwner.getAddr(), lockOwner.getPort(),
                            lockOwner.getTimestamp());
                    }
                }

                // Ping outside of mutex scope.
                if (temp != null && ping(temp.getAddr(), temp.getPort(), temp.getTimestamp()) == false) {
                    synchronized (mutex) {
                        // Check again because lock owner could have changed while the
                        // lock was not held.
                        if (lockOwner != null && lockOwner.equals(temp) == true) {
                            if (DebugFlags.CLUSTER == true) {
                                Utils.debug("Unlocking cluster lock because lock owner is failed [lock-owner=" +
                                    lockOwner + ']');
                            }

                            lockOwner = null;
                        }
                    }
                }
            }
        }
    }
}
