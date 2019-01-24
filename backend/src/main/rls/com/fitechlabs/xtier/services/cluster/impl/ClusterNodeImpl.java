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
 * $Workfile:ClusterNodeImpl.java$
 * $Date:10/6/2004 11:01:04 AM$
 * $Revision:7$
 */
 
package com.fitechlabs.xtier.services.cluster.impl;

import com.fitechlabs.xtier.l10n.*;
import com.fitechlabs.xtier.services.cluster.*;
import com.fitechlabs.xtier.services.marshal.*;
import com.fitechlabs.xtier.utils.*;
import java.net.*;
import java.util.*;

/**
 * 
 * @author <a href="mailto:dsetraky@fitechlabs.com">Dmitriy Setrakyan</a>
 * @version 2.0
 */
class ClusterNodeImpl implements ClusterNode, Marshallable {
    /** */
    private int nodeId;

    /** */
    private InetAddress addr;
    
    /** */
    private int port;
    
    /** */
    private boolean isLocalNode;
    
    /** */
    private boolean isLocalHostNode;
    
    /** */
    private String osName;
    
    /** */
    private String osArch;
    
    /** */
    private int cpuNum;
    
    /** */
    private long startTime;

    /** */    
    private int hrbtLossThreshold;

    /** */
    private long totalMem;
    
    /** */
    private long freeMem;

    /** */
    private long hrbtFreq;
    
    /** */
    private long lastHrbt;
    
    /** */
    private float cpuLoad;
    
    /** */
    private String[] services;

    /** */
    private Map grpMshps;
    
    /** */
    private boolean isActive;
    
    /** */
    private ClusterConfig cfg;
    
    /** Synchronization lock. */    
    private final Object mutex = new Object();
    
    /**
     * 
     * @param cfg
     */
    ClusterNodeImpl(ClusterConfig cfg) {
        assert cfg != null;
        
        this.cfg = cfg;
    }
    
    /**
     * @see ClusterNode#checkFailureStatus()
     */
    public boolean checkFailureStatus() {
        assert isLocalNode == false;
        
        return cfg.getFailureResolver().isFailed(this);
    }
    
    /**
     * 
     * @param cpuLoad
     * @param totalMem
     * @param freeMem
     */
    void refresh(float cpuLoad, long totalMem, long freeMem) {
        synchronized (mutex) {
            lastHrbt = System.currentTimeMillis();
            
            this.cpuLoad = cpuLoad;
            this.totalMem = totalMem;
            this.freeMem = freeMem;
        }
    }
    
    /**
     * @see ClusterNode#isActive()
     */
    public boolean isActive() {
        synchronized (mutex) {
            return isActive;
        }
    }
    
    /**
     * @see ClusterNode#hasService(String)
     */
    public boolean hasService(String serviceName) {
        ArgAssert.nullArg(serviceName, "serviceName");

        assert services != null;
        
        return Utils.contains(services, serviceName) == true;
    }
    
    /**
     * 
     * @param isActive
     */
    void setActive(boolean isActive) {
        synchronized (mutex) {
            this.isActive = isActive;
        }
    }
    
    /**
     * @see ClusterNode#getCpuLoad()
     */
    public float getCpuLoad() {
        synchronized (mutex) {
            return cpuLoad;
        }
    }
    
    /**
     * 
     * @param cpuLoad
     */
    void setCpuLoad(float cpuLoad) {
        synchronized (mutex) {
            this.cpuLoad = cpuLoad;
        }
    }
    
    /**
     * 
     * @return TBD
     */
    long getLastHeartbeat() {
        synchronized (mutex) {
            return lastHrbt;
        }
    }
    
    /**
     * 
     * @param lastHrbt
     */
    void setLastHeartbeat(long lastHrbt) {
        synchronized (mutex) {
            this.lastHrbt = lastHrbt;
        }
    }
    
    /**
     * @see ClusterNode#getTotalMemory()
     */
    public long getTotalMemory() {
        synchronized (mutex) {
            return totalMem;
        }
    }
    
    /**
     * 
     * @param totalMem
     */
    void setTotalMemory(long totalMem) {
        synchronized (mutex) {
            this.totalMem = totalMem;
        }
    }

    /**
     * @see ClusterNode#getFreeMemory()
     */
    public long getFreeMemory() {
        synchronized (mutex) {
            return freeMem;
        }
    }
    
    /**
     * 
     * @param freeMem
     */
    void setFreeMemory(long freeMem) {
        synchronized (mutex) {
            this.freeMem = freeMem;
        }
    }
    
    /**
     * @see ClusterNode#getStartTime()
     */
    public long getStartTime() {
        synchronized (mutex) {
            return startTime;
        }
    }
    
    /**
     * 
     * @param startTime
     */
    void setStartTime(long startTime) {
        synchronized (mutex) {
            this.startTime = startTime;
        }
    }

    /**
     * @see ClusterNode#isGroupMember(String)
     */
    public boolean isGroupMember(String grpName) {
        ArgAssert.nullArg(grpName, "grpName");
        
        synchronized (mutex) {
            return grpMshps == null ? false : grpMshps.containsKey(grpName);
        }
    }
    
    /**
     * @see ClusterNode#getGroupMembership(String)
     */
    public ClusterGroupMembership getGroupMembership(String grpName) {
        ArgAssert.nullArg(grpName, "grpName");

        synchronized (mutex) {
            return grpMshps == null ? null : (ClusterGroupMembership)grpMshps.get(grpName);
        }
    }

    /**
     * @see ClusterNode#getGroupMemberships()
     */
    public Map getGroupMemberships() {
        return grpMshps;
    }
    
    /**
     * 
     * @param grpMshps
     */
    void setGroupMemberships(Map grpMshps) {
        assert grpMshps != null;
        
        this.grpMshps = Collections.unmodifiableMap(grpMshps);
    }
    
    /**
     * 
     * @param grpName
     * @return TBD
     */
    boolean containsGroupMshp(String grpName) {
        assert grpName != null;
        
        return grpMshps.containsKey(grpName);
    }
    
    /**
     * 
     * @param grpName
     * @return TBD
     */
    ClusterGroupMembershipImpl getGroupMshp(String grpName) {
        assert grpName != null;
        
        return (ClusterGroupMembershipImpl)grpMshps.get(grpName);
    }
    
    /**
     * @see ClusterNode#getServices()
     */
    public String[] getServices() {
        return services;
    }
    
    /**
     * 
     * @param services
     */
    void setServices(String[] services) {
        this.services = services;
    }
    
    /**
     * @see ClusterNode#getOsArch()
     */
    public String getOsArch() {
        return osArch;
    }
    
    /**
     * 
     * @param osArch
     */
    void setOsArch(String osArch) {
        this.osArch = osArch;
    }
    
    /**
     * @see ClusterNode#getOsName()
     */
    public String getOsName() {
        return osName;
    }
    
    /**
     * 
     * @param osName
     */
    void setOsName(String osName) {
        this.osName = osName;
    }
    
    /**
     * @see ClusterNode#isLocalNode()
     */
    public boolean isLocalNode() {
        return isLocalNode == true;
    }
    
    /**
     * 
     * @param isLocalNode
     */
    void setLocalNode(boolean isLocalNode) {
        this.isLocalNode = isLocalNode;
    }
    
    /**
     * @see ClusterNode#isLocalHostNode()
     */
    public boolean isLocalHostNode() {
        return isLocalHostNode == true;
    }
    
    /**
     * @see ClusterNode#isRemoteNode()
     */
    public boolean isRemoteNode() {
        return isLocalNode == false && isLocalHostNode == false;
    }
    
    /**
     * 
     * @param isLocalHostNode
     */
    void setLocalHostNode(boolean isLocalHostNode) {
        this.isLocalHostNode = isLocalHostNode;
    }
    
    /**
     * @return TBD
     */
    int getHeartbeatLossThreshold() {
        return hrbtLossThreshold;
    }
    
    /**
     * 
     * @param hrbtLossThreshold
     */
    void setHeartbeatLossThreashold(int hrbtLossThreshold) {
        this.hrbtLossThreshold = hrbtLossThreshold;
    }

    /**
     * @see ClusterNode#getNodeId()
     */
    public int getNodeId() {
        return nodeId;
    }
    
    /**
     * 
     * @param nodeId
     */
    void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    /**
     * @see ClusterNode#getAddress()
     */
    public InetAddress getAddress() {
        return addr;
    }
    
    /**
     * 
     * @param addr
     */
    void setAddress(InetAddress addr) {
        this.addr = addr;
    }

    /**
     * @see ClusterNode#getPort()
     */
    public int getPort() {
        return port;
    }
    
    /**
     * 
     * @param port
     */
    void setPort(int port) {
        this.port = port;
    }

    /**
     * @see ClusterNode#getNumberOfCpus()
     */
    public int getNumberOfCpus() {
        return cpuNum;
    }
    
    /**
     * 
     * @param cpuNum
     */
    void setNumberOfCpus(int cpuNum) {
        this.cpuNum = cpuNum;
    }
    
    /**
     * 
     * @return TBD
     */
    long getHeartbeatFreq() {
        return hrbtFreq;
    }
    
    /**
     * 
     * @param hrbtFreq
     */
    void setHeartbeatFreq(long hrbtFreq) {
        this.hrbtFreq = hrbtFreq;
    }

    /**
     * @see Marshallable#typeGuid()
     */
    public short typeGuid() {
        return ClusterEncoder.NODE;
    }
    
    /**
     * @see Marshallable#getObjs()
     */
    public Map getObjs() {
        Map map = new HashMap(Utils.getNonRehashCapacity(14));
        
        map.put("node-id", new Integer(nodeId));
        map.put("cpu-load", new Float(cpuLoad));
        map.put("cpu-num", new Integer(cpuNum));
        map.put("free-mem", new Long(freeMem));
        map.put("total-mem", new Long(totalMem));
        map.put("grp-mshps", new HashMap(grpMshps));
        map.put("hb-freq", new Long(hrbtFreq));
        map.put("hb-loss-threshold", new Integer(hrbtLossThreshold));
        map.put("os-arch", osArch);
        map.put("os-name", osName);
        map.put("port", new Integer(port));
        map.put("start-time", new Long(startTime));
        map.put("services", services);
        map.put("addr", addr.getAddress());
        
        return map;
    }
    
    /**
     * @see Marshallable#setObjs(Map)
     */
    public void setObjs(Map objs) throws MarshalException {
        nodeId = ((Integer)objs.get("node-id")).intValue();
        cpuLoad = ((Float)objs.get("cpu-load")).floatValue();
        cpuNum = ((Integer)objs.get("cpu-num")).intValue();
        freeMem = ((Long)objs.get("free-mem")).longValue();
        totalMem = ((Long)objs.get("total-mem")).longValue();
        grpMshps = Collections.unmodifiableMap((Map)objs.get("grp-mshps"));
        hrbtFreq = ((Long)objs.get("hb-freq")).longValue();
        hrbtLossThreshold = ((Integer)objs.get("hb-loss-threshold")).intValue();
        osArch = (String)objs.get("os-arch");
        osName = (String)objs.get("os-name");
        port = ((Integer)objs.get("port")).intValue();
        startTime = ((Long)objs.get("start-time")).longValue();
        services = (String[])objs.get("services");
        
        try {
            addr = InetAddress.getByAddress((byte[])objs.get("addr"));
        }
        catch (UnknownHostException e) {
            throw new MarshalException(L10n.format("SRVC.CLUSTER.ERR16"), e);
        }
    }
    
    /**
     * Marshallable#onDemarshal()
     */
    public void onDemarshal() {
        isLocalNode = false;
        isLocalHostNode = false;
        isActive = true;
        
        synchronized (mutex) {
            // Assume that last heartbeat was received as of now.
            lastHrbt = System.currentTimeMillis();
        }
    }
    
    /**
     * @see Marshallable#onMarshal()
     */
    public void onMarshal() {
        // No-op.
    }
    
    /**
     * @see Object#equals(Object)
     */
    public boolean equals(Object obj) {
        if (obj instanceof ClusterNodeImpl == false) {
            return false;
        }
        
        return ((ClusterNodeImpl)obj).getNodeId() == nodeId;
    }
    
    /**
     * @see Object#hashCode()
     */
    public int hashCode() {
        return nodeId;
    }

    /**
     * @see Object#toString()
     */
    public String toString() {
        return L10n.format("SRVC.CLUSTER.TXT4", 
            new Object[] { 
                new Integer(nodeId),
                Boolean.valueOf(isActive()),
                Boolean.valueOf(isLocalNode),
                Boolean.valueOf(isLocalHostNode()),
                Boolean.valueOf(isRemoteNode()),
                addr.getHostAddress(), 
                new Integer(port), 
                new Integer(cpuNum), 
                new Date(startTime), 
                osName, 
                osArch,
                new Float(cpuLoad),
                new Long(getTotalMemory()), 
                new Long(getFreeMemory()), 
                Utils.arr2Str(services),
                Utils.coll2Str(grpMshps.values())
            });
    }
}