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
 * $Workfile:ClusterGroupMembershipImpl.java$
 * $Date:2/22/2004 12:43:09 AM$
 * $Revision:2$
 */
 
package com.fitechlabs.xtier.services.cluster.impl;

import com.fitechlabs.xtier.l10n.*;
import com.fitechlabs.xtier.services.cluster.*;
import com.fitechlabs.xtier.services.marshal.*;
import com.fitechlabs.xtier.utils.*;
import java.util.*;

/**
 * TBD: Add comment for ClusterGroupMembershipImpl.
 * 
 * @author <a href="mailto:dsetraky@fitechlabs.com">Dmitriy Setrakyan</a>
 * @version 2.0
 */
class ClusterGroupMembershipImpl implements ClusterGroupMembership, Marshallable {
    /** */
    private int nodeId;
    
    /** */
    private String grpName;

    /** */
    private Map grpProps;
    
    /**
     * Empty constructor required for decoding.
     */
    ClusterGroupMembershipImpl() {
        // No op.    
    }
    
    /**
     * 
     * @param grpConfig
     */
    ClusterGroupMembershipImpl(ClusterGroupConfig grpConfig) {
        grpName = grpConfig.getGroupName();
        grpProps = Collections.unmodifiableMap(grpConfig.getGroupProps());
    }
    
    /**
     * @see ClusterGroupMembership#getNodeId()
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
     * @see ClusterGroupMembership#getGroupName()
     */
    public String getGroupName() {
        return grpName;
    }

    /**
     * 
     * @param grpName
     */
    void setGroupName(String grpName) {
        this.grpName = grpName;
    }

    /**
     * @see ClusterGroupMembership#getGroupProps()
     */
    public Map getGroupProps() {
        return grpProps;
    }
    
    /**
     * 
     * @param grpProps
     */
    void setGroupProps(Map grpProps) {
        this.grpProps = grpProps;
    }
    
    
    /**
     * @see Marshallable#typeGuid()
     */
    public short typeGuid() {
        return ClusterEncoder.GROUP;
    }
    
    /**
     * @see Marshallable#getObjs()
     */
    public Map getObjs() {
        Map map = new HashMap(Utils.getNonRehashCapacity(3));
        
        map.put("node-id", new Integer(nodeId));
        map.put("grp-name", grpName);
        map.put("grp-props", new HashMap(grpProps));
        
        return map;
    }
    
    /**
     * @see Marshallable#setObjs(Map)
     */
    public void setObjs(Map objs) {
        nodeId = ((Integer)objs.get("node-id")).intValue();
        grpName = (String)objs.get("grp-name");
        grpProps = Collections.unmodifiableMap((Map)objs.get("grp-props"));
    }
    
    /**
     * Marshallable#onDemarshal()
     */
    public void onDemarshal() {
        // No-op.
    }
    
    /**
     * @see Marshallable#onMarshal()
     */
    public void onMarshal() {
        // No-op.
    }
    
    /**
     * @see Object#toString()
     */
    public String toString() {
        return L10n.format("SRVC.CLUSTER.TXT5", new Integer(nodeId), grpName, Utils.map2Str(grpProps));
    }
}
