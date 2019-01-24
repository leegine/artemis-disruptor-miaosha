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
 * $Workfile:ClusterUdpMsg.java$
 * $Date:10/6/2004 11:01:06 AM$
 * $Revision:5$
 */
 
package com.fitechlabs.xtier.services.cluster.impl;

import com.fitechlabs.xtier.debug.*;
import java.net.*;

/**
 * TBD: Add comment for ClusterUdpMsg.
 * 
 * @author <a href="mailto:dsetraky@fitechlabs.com">Dmitriy Setrakyan</a>
 * @version 2.0
 */
class ClusterUdpMsg {
    /**
     * Maximum possible byte size of UDP message. This size is equal to the size of heartbeat.
     */
    static final int BYTE_SIZE = /*message type*/1 + /*address*/ 17 + /*port*/4 + 32/*cpu load*/ + 64/*total memory*/ +
        64/*free memory*/;
    
    /** */
    static final byte HEARTBEAT = 1;
    
    /** */
    static final byte DISCOVER = 2;
    
    /** */
    private InetAddress addr;
    
    /** */
    private int port;
    
    /** */
    private long totalMemory;
    
    /** */
    private long freeMemory;
    
    /** */
    private int msgId;
    
    /** */
    private byte msgType;
    
    /** */
    private int nodeId;
    
    /** */
    private float cpuLoad;
    
    /**
     * 
     */
    ClusterUdpMsg() {
        // No op.
    }

    /**
     * 
     * 
     * @param msgType
     */
    ClusterUdpMsg(byte msgType) {
        this.msgType = msgType;
    }
    
    /**
     * 
     * @return TBD
     */
    public int getNodeId() {
        return nodeId;
    }

    /**
     * Sets the nodeId value.
     * 
     * @param nodeId Value to set.
     */
    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    /**
     * @return TBD
     */
    long getFreeMemory() {
        return freeMemory;
    }

    /**
     * 
     * @param freeMemory
     */
    void setFreeMemory(long freeMemory) {
        this.freeMemory = freeMemory;
    }

    /**
     * @return TBD
     */
    int getMsgId() {
        return msgId;
    }

    /**
     * 
     * @param msgId
     */
    void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    /**
     * @return TBD
     */
    int getPort() {
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
     * @return TBD
     */
    long getTotalMemory() {
        return totalMemory;
    }

    /**
     *
     * @param totalMemory
     */
    void setTotalMemory(long totalMemory) {
        this.totalMemory = totalMemory;
    }

    /**
     * @return TBD
     */
    float getCpuLoad() {
        return cpuLoad;
    }

    /**
     * Sets the cpuLoad value.
     * 
     * @param cpuLoad Value to set.
     */
    void setCpuLoad(float cpuLoad) {
        this.cpuLoad = cpuLoad;
    }

    /**
     * @return TBD
     */
    InetAddress getAddress() {
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
     * @return TBD
     */
    byte getMsgType() {
        return msgType;
    }
    
    /**
     * 
     * @param msgType
     */
    void setMsgType(byte msgType) {
        assert msgType == DISCOVER || msgType == HEARTBEAT;
        
        this.msgType = msgType;
    }

    /**
     * TBD: Add comment for getStrMsgType.
     * 
     * @return TBD
     */
    String getStrMsgType() {
        return msgType == HEARTBEAT ? "HEARTBEAT" : "DISCOVER";
    }
    
    /**
     * @see Object#toString()
     */
    public String toString() {
        assert DebugFlags.CLUSTER == true;
        
        return msgType == HEARTBEAT ? "Cluster heartbeat [address=" +  addr.getHostAddress() + ", port=" + port +
            ", cpu-load=" + cpuLoad + ", total-memory=" + totalMemory + ", free-memory=" + freeMemory + ']' :
            "Cluster discover request [message-id=" + msgId + ", address=" +  addr.getHostAddress() +
                ", port=" + port + ']';
    }
}
