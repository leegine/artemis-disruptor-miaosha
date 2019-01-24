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
 * $Workfile:ClusterConfig.java$
 * $Date:10/6/2004 11:01:03 AM$
 * $Revision:4$
 */
 
package com.fitechlabs.xtier.services.cluster.impl;

import com.fitechlabs.xtier.services.cluster.*;
import java.net.*;
import java.util.*;

/**
 * 
 * @author <a href="mailto:dsetraky@fitechlabs.com">Dmitriy Setrakyan</a>
 * @version 2.0
 */
class ClusterConfig {
    /** */
    private InetAddress localAddr;
        
    /** */
    private int localPort;
        
    /** */
    private boolean isAutoDiscovery;

    /** */
    private List seedNodes = Collections.EMPTY_LIST;
    
    /** */
    private int retries;
        
    /** */
    private InetAddress mcastGrp;
        
    /** */
    private int mcastPort;
    
    /** */
    private int mcastTtl;
    
    /** */
    private long hrbtFreq;
        
    /** */
    private int hrbtLossThreshold;
        
    /** */
    private int timeout;
        
    /** */
    private ClusterNodeFailureResolver failureResolver;
        
    /** */
    private ClusterNodeCpuSensor cpuSensor;

    /** */
    private Map groupConfigs;

    /**
     * 
     * @return TBD
     */
    int getTimeout() {
        return timeout;
    }
    
    /**
     * 
     * @return TBD
     */
    ClusterNodeCpuSensor getCpuSensor() {
        return cpuSensor;
    }

    /**
     * 
     * @return TBD
     */
    ClusterNodeFailureResolver getFailureResolver() {
        return failureResolver;
    }

    /**
     * 
     * @return TBD
     */
    Map getGroupConfigs() {
        return groupConfigs;
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
     * @return TBD
     */
    boolean isAutoDiscovery() {
        return isAutoDiscovery;
    }

    /**
     * 
     * @return TBD
     */
    InetAddress getLocalAddress() {
        return localAddr;
    }

    /**
     * 
     * @return TBD
     */
    int getLocalPort() {
        return localPort;
    }

    /**
     * 
     * @return TBD
     */
    int getHeartbeatLossThreshold() {
        return hrbtLossThreshold;
    }

    /**
     * 
     * @return TBD
     */
    InetAddress getMcastGroup() {
        return mcastGrp;
    }

    /**
     * 
     * @return TBD
     */
    int getMcastPort() {
        return mcastPort;
    }
    
    /**
     * 
     * @return TBD
     */
    int getMcastTtl() {
        return mcastTtl;
    }

    /**
     * 
     * @return TBD
     */        
    List getSeedNodes() {
        return seedNodes;
    }

    /**
     * 
     * @param timeout
     */
    void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * 
     * @param failureResolver
     */
    void setFailureResolver(ClusterNodeFailureResolver failureResolver) {
        this.failureResolver = failureResolver;
    }
    
    /**
     * @param cpuSensor
     */
    void setCpuSensor(ClusterNodeCpuSensor cpuSensor) {
        this.cpuSensor = cpuSensor;
    }

    /**
     * 
     * @param groupConfigs
     */
    void setGroupConfigs(Map groupConfigs) {
        this.groupConfigs = groupConfigs;
    }

    /**
     * 
     * @param heartbeatFreq
     */
    void setHeartbeatFreq(long heartbeatFreq) {
        this.hrbtFreq = heartbeatFreq;
    }

    /**
     * 
     * @param isAutoDiscovery
     */
    void setAutoDiscovery(boolean isAutoDiscovery) {
        this.isAutoDiscovery = isAutoDiscovery;
    }

    /**
     * 
     * @param localAddr
     */
    void setLocalAddress(InetAddress localAddr) {
        this.localAddr = localAddr;
    }

    /**
     * 
     * @param localPort
     */
    void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    /**
     * 
     * @param heartbeatLossThreshold
     */
    void setHeartbeatLossThreshold(int heartbeatLossThreshold) {
        this.hrbtLossThreshold = heartbeatLossThreshold;
    }

    /**
     * 
     * @param mcastGroup
     */
    void setMcastGroup(InetAddress mcastGroup) {
        this.mcastGrp = mcastGroup;
    }

    /**
     * 
     * @param mcastPort
     */
    void setMcastPort(int mcastPort) {
        this.mcastPort = mcastPort;
    }
    
    /**
     * 
     * @param mcastTtl
     */
    void setMcastTtl(int mcastTtl) {
        this.mcastTtl = mcastTtl;
    }

    /**
     * 
     * @param seedNodes
     */
    void setSeedNodes(List seedNodes) {
        this.seedNodes = seedNodes;
    }

    /**
     * 
     * @return TBD
     */
    int getRetries() {
        return retries;
    }

    /**
     * 
     * @param retries
     */
    void setRetries(int retries) {
        this.retries = retries;
    }
}
