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
 * $Workfile:ClusterTopologyManager.java$
 * $Date:10/6/2004 11:01:06 AM$
 * $Revision:10$
 */
 
package com.fitechlabs.xtier.services.cluster.impl;

import com.fitechlabs.xtier.debug.*;
import com.fitechlabs.xtier.kernel.*;
import com.fitechlabs.xtier.l10n.*;
import com.fitechlabs.xtier.services.cluster.*;
import com.fitechlabs.xtier.services.cluster.filters.*;
import com.fitechlabs.xtier.services.os.*;
import com.fitechlabs.xtier.utils.*;
import java.net.*;
import java.util.*;

/**
 *
 * @author <a href="mailto:dsetraky@fitechlabs.com">Dmitriy Setrakyan</a>
 */
public class ClusterTopologyManager {
    /** */
    private int version = 0;
    
    /** */
    private Set allNodes;

    /** */
    private ClusterNodeImpl localNode;

    /** */
    private ClusterNodeImpl mgr;
    
    /** */
    private ClusterConfig cfg;
    
    /** */
    private String[] services;
    
    /** */
    private ClusterNodeCpuSensor cpuSensor;
    
    /** */
    private ClusterNodeTypeFilter nonLocalFilter = new ClusterNodeTypeFilter(false, true, true);
    
    /** */
    private int maxId;
    
    /** */
    private boolean isJoined = false;
    
    /**
     * @param cfg
     * @param services
     */
    ClusterTopologyManager(ClusterConfig cfg, String[] services) {
        this.cfg = cfg;
        this.services = services;
    }
    
    /**
     * 
     */
    void start() {
        cpuSensor = cfg.getCpuSensor();
        
        localNode = new ClusterNodeImpl(cfg) {
            /** */
            private Runtime runtime = Runtime.getRuntime();
            
            /**
             * @see ClusterNodeImpl#checkFailureStatus()
             */
            public boolean checkFailureStatus() {
                return false;
            }
            
            /**
             * @see ClusterNodeImpl#getCpuLoad()
             */
            public float getCpuLoad() {
                return cpuSensor.getCpuLoad();
            }
            
            /**
             * @see ClusterNodeImpl#setCpuLoad(float)
             */
            void setCpuLoad(float cpuLoad) {
                assert false : "Can't set CPU load for local node.";
            }

            /**
             * @see ClusterNodeImpl#getTotalMemory()
             */
            public long getTotalMemory() {
                return runtime.totalMemory();
            }

            /**
             * @see ClusterNodeImpl#getFreeMemory()
             */
            public long getFreeMemory() {
                return runtime.freeMemory();
            }

            /**
             * @see ClusterNodeImpl#setTotalMemory(long)
             */
            void setTotalMemory(long totalMemory) {
                assert false : "Can't set total memory for local node.";
            }

            /**
             * @see ClusterNodeImpl#setFreeMemory(long)
             */
            void setFreeMemory(long freeMemory) {
                assert false : "Can't set free memory for local node.";
            }
        };

        OsService os = XtierKernel.getInstance().os();
        
        // Set start time to current time.
        localNode.setStartTime(System.currentTimeMillis());
        localNode.setLocalNode(true);
        localNode.setLocalHostNode(false);
        localNode.setActive(true);
        localNode.setAddress(cfg.getLocalAddress());
        localNode.setPort(cfg.getLocalPort());
        localNode.setHeartbeatFreq(cfg.getHeartbeatFreq());
        localNode.setHeartbeatLossThreashold(cfg.getHeartbeatLossThreshold());
        localNode.setOsArch(os.getOsArch());
        localNode.setOsName(os.getOsName());
        localNode.setNumberOfCpus(Utils.getNumberOfCpus());

        Map grpConfigs = cfg.getGroupConfigs();

        Map grpMshps = grpConfigs == null ? Collections.EMPTY_MAP : new HashMap(grpConfigs.size());

        if (grpConfigs != null) {
            for (Iterator iter = cfg.getGroupConfigs().values().iterator(); iter.hasNext() == true;) {
                ClusterGroupMembershipImpl grpMshp = new ClusterGroupMembershipImpl((ClusterGroupConfig) iter.next());
    
                grpMshps.put(grpMshp.getGroupName(), grpMshp);
            }
        }
        
        localNode.setGroupMemberships(grpMshps);
        localNode.setServices(services);

        // Purposely initialize invalid node id until it successfully
        // joins cluster.
        localNode.setNodeId(-1);
        
        if (DebugFlags.CLUSTER == true) {
            Utils.debug("Local node before joining cluster: " + localNode);
        }
    }
    
    /**
     * 
     */
    void stop() {
        cpuSensor.stop();
    }

    /**
     * @param maxId
     */
    private void setMaxId(int maxId) {
        assert Thread.holdsLock(this) == true;
        
        this.maxId = maxId;
        
        if (DebugFlags.CLUSTER == true) {
            for (Iterator nodes = allNodes.iterator(); nodes.hasNext() == true;) {
                ClusterNodeImpl node = (ClusterNodeImpl)nodes.next();
                
                assert node.getNodeId() != maxId : "Max id is already in the list: " + maxId;
            }
        }
    }

    /**
     * 
     * @return TBD
     * @throws ClusterException
     */
    synchronized int createNodeId() throws ClusterException {
        if (maxId == Integer.MAX_VALUE) {
            throw new ClusterException(L10n.format("SRVC.CLUSTER.ERR15"));
        }
            
        return maxId + 1;
    }
    
    /**
     * 
     * @return TBD
     */
    synchronized int getVersion() {
        return version;
    }

    /**
     * This method is called whenever this node is first in cluster.
     */
    synchronized void onJoinCluster() {
        // Start node-id sequence from 1.
        onJoinCluster(1, new ArrayList(0));
    }

    /**
     * 
     * @param localNodeId
     * @param otherNodes
     */
    synchronized void onJoinCluster(int localNodeId, List otherNodes) {
        version++;
        
        // Assign node ID to local node.
        localNode.setNodeId(localNodeId);

        // Assign node ID to all group memberships local node belongs to.
        for (Iterator iter = localNode.getGroupMemberships().values().iterator(); iter.hasNext() == true;) {
            ((ClusterGroupMembershipImpl)iter.next()).setNodeId(localNodeId);
        }
        
        // Check if any of the nodes is a localhost node.
        for (int i = 0, n = otherNodes.size(); i < n; i++) {
            checkLocalHost((ClusterNodeImpl)otherNodes.get(i));
        }

        // Add local node to the list of all nodes.
        allNodes = new HashSet(otherNodes);
        
        // Set max id before adding local node, since it should be the maximum ID.
        setMaxId(localNodeId);
        
        allNodes.add(localNode);

        allNodes = Collections.unmodifiableSet(allNodes);

        // Find the cluster mgr.
        resetMgr();
        
        // If this node is not
        assert allNodes.size() == 1 || isMgr() == false;
        
        isJoined = true;

        // Notify all threads waiting for topology change.
        notifyAll();
    }

    /**
     * 
     * @param node
     */
    synchronized void onAddNode(ClusterNodeImpl node) {
        assert node != null;
        assert node.getNodeId() != localNode.getNodeId();

        version++;

        checkLocalHost(node);
        
        // Set max id before adding node to the list.
        setMaxId(node.getNodeId());
        
        // Since we cannot update existing lists because user may be
        // iterating through them, we must create a new list.
        Set set = new HashSet(allNodes);
        
        if (set.contains(node) == false) {
            set.add(node);
        }
        
        // Make the list unmodifiable.
        allNodes = Collections.unmodifiableSet(set);

        // Notify all threads waiting for topology change.
        notifyAll();
    }

    /**
     * 
     * @param nodeId
     */
    synchronized void onRemoveNode(int nodeId) {
        assert nodeId > 0;

        ClusterNodeImpl doomed = getNode(nodeId);
        
        if (doomed != null) {
            // If local node is leaving cluster.
            if (doomed.isLocalNode() == true) {
                isJoined = false;
            }
            
            // Remove node from the list of all nodes.
            allNodes = new HashSet(allNodes);
            
            allNodes.remove(doomed);
            
            // This node is no longer active.
            doomed.setActive(false);
            
            allNodes = Collections.unmodifiableSet(allNodes);
            
            version++;
            
            // Reset cluster mgr.
            resetMgr();
            
            // Notify all threads waiting for topology change.
            notifyAll();
        }
    }
    
    /**
     * 
     * @return TBD
     */
    synchronized int getSize() {
        return allNodes == null ? 0 : allNodes.size();
    }

    /**
     * 
     * @return TBD
     */
    synchronized Set getAllNodes() {
        return allNodes;
    }
    
    /**
     * 
     * @return TBD
     */
    synchronized boolean isJoined() {
        return isJoined == true;
    }
    
    /**
     * 
     * @param filter
     * @return TBD
     */
    synchronized Set getNodes(ClusterFilter filter) {
        assert filter != null;
        
        int size = allNodes.size();
        
        Set nodes = new HashSet(size);
        
        for (Iterator iter = allNodes.iterator(); iter.hasNext() == true;) {
            ClusterNodeImpl node = (ClusterNodeImpl)iter.next();
            
            if (filter.accept(node) == true) {
                nodes.add(node);
            }
        }
        
        return nodes;
    }
    
    /**
     * 
     * @return TBD
     */
    synchronized Set getNonLocalNodes() {
        return getNodes(nonLocalFilter);
    }

    /**
     * 
     * @param nodeId
     * @return TBD
     */
    synchronized ClusterNodeImpl getNode(int nodeId) {
        for (Iterator nodes = allNodes.iterator(); nodes.hasNext() == true;) {
            ClusterNodeImpl node = (ClusterNodeImpl)nodes.next();
            
            if (node.getNodeId() == nodeId) {
                return node;
            }
        }
        
        return null;
    }

    /**
     * 
     * @param addr
     * @param port
     * @return TBD
     */
    synchronized ClusterNodeImpl getNode(InetAddress addr, int port) {
        assert addr != null;
        assert port >= 0 && port <= 0xffff;

        for (Iterator nodes = allNodes.iterator(); nodes.hasNext() == true;) {
            ClusterNodeImpl node = (ClusterNodeImpl)nodes.next();
            
            if (node.getPort() == port && node.getAddress().equals(addr) == true) {
                return node;
            }
        }
        
        return null;
    }
    
    /**
     * 
     * @param addr
     * @param port
     * @return TBD
     */
    synchronized boolean hasNode(InetAddress addr, int port) {
        return getNode(addr, port) != null;
    }
    
    /**
     * 
     * @param nodeId
     * @return TBD
     */
    synchronized boolean hasNode(int nodeId) {
        return getNode(nodeId) != null;
    }

    /**
     * 
     * @return TBD
     */
    ClusterNodeImpl getLocalNode() {
        // This method does not synchronize because local node does not change.
        return localNode;
    }

    /**
     * 
     * @return TBD
     */
    synchronized ClusterNodeImpl getMgr() {
        return mgr;
    }

    /**
     * 
     * @return TBD
     */
    synchronized boolean isMgr() {
        return localNode == mgr;
    }
    
    /**
     * 
     * @return TBD
     */
    synchronized ClusterNodeImpl getNextMgr() {
        ClusterNodeImpl mgrNode = null;

        for (Iterator nodes = allNodes.iterator(); nodes.hasNext() == true;) {
            ClusterNodeImpl node = (ClusterNodeImpl)nodes.next();

            if (node != localNode) {
                if (mgrNode == null) {
                    mgrNode = node;
                }
                else if (node.getNodeId() < mgrNode.getNodeId()) {
                    mgrNode = node;
                }
            }
        }

        return mgrNode;
    }

    /**
     * 
     * @return TBD
     */
    synchronized boolean hasNonLocalNodes() {
        // Local node is always in the list of all nodes.
        return allNodes != null && allNodes.size() > 1;
    }

    /**
     * 
     * @param node
     */
    private void checkLocalHost(ClusterNodeImpl node) {
        assert node != localNode;
        
        if (node.getAddress().equals(localNode.getAddress()) == true) {
            assert node.getPort() != localNode.getPort();
            
            node.setLocalHostNode(true);
        }
    }
    
    /**
     * 
     */
    private void resetMgr() {
        assert Thread.holdsLock(this) == true;
        
        mgr = null;

        for (Iterator nodes = allNodes.iterator(); nodes.hasNext() == true;) {
            ClusterNodeImpl node = (ClusterNodeImpl)nodes.next();

            if (mgr == null) {
                mgr = node;
            }
            else if (node.getNodeId() < mgr.getNodeId()) {
                mgr = node;
            }
        }

        if (DebugFlags.CLUSTER == true) {
            Utils.debug("Reset cluster manager: " + mgr);
        }
    }
}
