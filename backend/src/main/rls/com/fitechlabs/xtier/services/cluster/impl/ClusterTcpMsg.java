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
 * $Workfile:ClusterTcpMsg.java$
 * $Date:10/6/2004 11:01:05 AM$
 * $Revision:9$
 */

package com.fitechlabs.xtier.services.cluster.impl;

import com.fitechlabs.xtier.debug.*;
import com.fitechlabs.xtier.l10n.*;
import com.fitechlabs.xtier.services.marshal.*;
import java.net.*;

/**
 * 
 * @author <a href="mailto:dsetraky@fitechlabs.com">Dmitriy Setrakyan</a>
 * @version 2.0
 */
class ClusterTcpMsg extends MarshalObject {
    /** */
    static final byte LOCK = 1;
    
    /** */
    static final byte UNLOCK = 2;
    
    /** */
    static final byte ADD = 3;
    
    /** */
    static final byte REMOVE = 4;
    
    /** */
    static final byte DISCOVER = 5;
    
    /** */
    static final byte SEED_CHECK = 6;
    
    /** */
    static final byte PING = 7;
    
    /** */
    static final byte ACK = 8;
    
    /** */
    private InetAddress addr;
    
    /**
     * Empty constructor used for demarshalling.
     */
    ClusterTcpMsg() {
        // No-op.
    }

    /**
     * 
     * @param msgType
     */
    ClusterTcpMsg(byte msgType) {
        putInt8("msg-type", msgType);
    }

    /**
     * 
     * @param msgType
     * @param msgId
     * @param isResponse
     */
    ClusterTcpMsg(byte msgType, int msgId, boolean isResponse) {
        putInt8("msg-type", msgType);
        putInt32("msg-id", msgId);
        putBool("is-response", isResponse);
    }
    
    /**
     * @see MarshalObject#typeGuid()
     */
    public short typeGuid() {
        return ClusterEncoder.TCP_MSG;
    }
    
    /**
     * 
     * @return TBD
     */    
    byte getMsgType() {
        return getInt8("msg-type");
    }

    /**
     * 
     * @return TBD
     */
    int getMsgId() {
        return getInt32("msg-id");
    }
    
    /**
     * 
     * @return TBD
     */
    InetAddress getAddr() {
        return addr;
    }
    
    /**
     * 
     * @param addr
     */
    void putAddr(InetAddress addr) {
        this.addr = addr;
        
        putArr("addr", addr.getAddress());
    }
    
    /**
     * @see MarshalObject#onDemarshal()
     */
    public void onDemarshal() throws MarshalException {
        byte[] bytes = (byte[])getArr("addr");
        
        if (bytes != null) {
            try {
                addr = InetAddress.getByAddress(bytes);
            }
            catch (UnknownHostException e) {
                throw new MarshalException(L10n.format("SRVC.CLUSTER.ERR16"), e);
            }
        }
    }
    
    /**
     * @see Object#toString()
     */
    public String toString() {
        assert DebugFlags.CLUSTER == true;
        
        return "Cluster TCP message [addr=" + addr + ", marshallable=" + super.toString() + ']';
    }
}
