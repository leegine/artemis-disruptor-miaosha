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
 * $Workfile:ClusterFailureManager.java$
 * $Date:16.09.2005 17:02:01$
 * $Revision:13$
 */

package com.fitechlabs.xtier.services.cluster.impl;

import com.fitechlabs.xtier.debug.*;
import com.fitechlabs.xtier.l10n.*;
import com.fitechlabs.xtier.services.cluster.*;
import com.fitechlabs.xtier.services.log.*;
import com.fitechlabs.xtier.threads.*;
import com.fitechlabs.xtier.utils.*;
import java.util.*;

/**
 * @author <a href="mailto:dsetraky@fitechlabs.com">Dmitriy Setrakyan</a>
 */
class ClusterFailureManager {
    /** */
    private Logger log;
    
    /** */
    private ClusterTopologyManager topMgr;
    
    /** */
    private ClusterLockManager lockMgr;

    /** */
    private ClusterEventManager evtMgr;
    
    /** */
    private ClusterConfig cfg;
    
    /** */
    private FailureDetector detector;

    /**
     * 
     * @param cfg
     * @param log
     * @throws ClusterException
     */
    ClusterFailureManager(Logger log, ClusterConfig cfg) throws ClusterException {
        this.cfg = cfg;
        this.log = log;
        
        detector = new FailureDetector();
    }

    /**
     * 
     * @param lockMgr
     * @param topMgr
     * @param evtMgr
     * @throws ClusterException
     */
    void start(ClusterLockManager lockMgr, ClusterTopologyManager topMgr, ClusterEventManager evtMgr) 
        throws ClusterException {
        this.lockMgr = lockMgr;
        this.topMgr = topMgr;
        this.evtMgr = evtMgr;
        
        detector.start();
    }
    
    /**
     * 
     */
    void stop() {
        Utils.stopThread(detector);
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
     * @author <a href="mailto:dsetraky@fitechlabs.com">Dmitriy Setrakyan</a>
     */
    private class FailureDetector extends SysThread {
        /**
         * 
         */
        FailureDetector() {
            super("cluster-failure-detector", SYS_ABOVE_NORMAL_PRIORITY);
        }

        /**
         * @see SysThread#body()
         */
        protected void body() {
            while (true) {
                checkInterrupted();

                Set nonLocalNodes;

                synchronized (topMgr) {
                    while (topMgr.hasNonLocalNodes() == false) {
                        if (DebugFlags.CLUSTER == true) {
                            Utils.debug("Failure detector will wait.");
                        }
                        
                        Utils.waitOn(topMgr);
                    }

                    nonLocalNodes = topMgr.getNonLocalNodes();
                }

                long wait = Long.MAX_VALUE;

                long start = System.currentTimeMillis();

                // Check every remote node for failure.
                for (Iterator iter = nonLocalNodes.iterator(); iter.hasNext() == true;) {
                    final ClusterNodeImpl node = (ClusterNodeImpl)iter.next();

                    long lastHrtbeat = node.getLastHeartbeat();
                    long hrtbeatFreq = node.getHeartbeatFreq();
                    int lossTreshold = node.getHeartbeatLossThreshold();

                    assert lastHrtbeat != 0 : "Invalid remote node: " + node;
                    
                    long idleTime = System.currentTimeMillis() - lastHrtbeat;
                    int missed = (int)(idleTime / hrtbeatFreq);

                    if (missed >= lossTreshold) {
                        // Check for failure without holding any locks.
                        if (cfg.getFailureResolver().isFailed(node) == true) {
                            lockMgr.localLock();
                            
                            try {
                                topMgr.onRemoveNode(node.getNodeId());
                            }
                            finally {
                                lockMgr.localUnlock();
                            }
                            
                            // Notify cluster listener.
                            evtMgr.addEvent(ClusterListener.NODE_FAILED, node, topMgr.getVersion());
                            
                            evtMgr.dispatch();
                            
                            // Warn about failed node.
                            log.warning(L10n.format("SRVC.CLUSTER.WRN6", new Integer(topMgr.getSize()),
                                nodeToStr(node)));
                            
                            // Print out new topology.
                            log.log(L10n.format("SRVC.CLUSTER.TXT8", Utils.coll2Str(topMgr.getAllNodes())));
                        
                            // Move on to the next node.
                            continue;
                        }

                        // Node is not failed.
                        // Act as we did get a heartbeat from this node.
                        node.setLastHeartbeat(System.currentTimeMillis());

                        idleTime = 0;
                    }

                    long nextHrtbeat = hrtbeatFreq * lossTreshold - idleTime;
                    long now = System.currentTimeMillis();
                    
                    // Take time elapsed since start of the loop
                    // into considiration.
                    if (nextHrtbeat < wait - (now - start)) {
                        wait = nextHrtbeat;

                        start = now;
                    }
                }

                if (wait < Long.MAX_VALUE) {
                    // Take time it took for checking every node into
                    // considiration.
                    wait -= System.currentTimeMillis() - start;

                    if (wait > 0) {
                        Utils.sleep(wait);
                    }
                }
            }
        }
    }
}
