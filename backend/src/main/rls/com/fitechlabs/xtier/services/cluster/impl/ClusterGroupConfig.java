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
 * $Workfile:ClusterGroupConfig.java$
 * $Date:10/6/2004 11:01:04 AM$
 * $Revision:3$
 */
 
package com.fitechlabs.xtier.services.cluster.impl;

import com.fitechlabs.xtier.debug.*;
import java.util.*;

/**
 * 
 * @author <a href="mailto:dsetraky@fitechlabs.com">Dmitriy Setrakyan</a>
 * @version 2.0
 */
class ClusterGroupConfig {
    /** TBD: Add comment. */
    private String grpName;
        
    /** TBD: Add comment. */
    private Map grpProps;
    
    /**
     * 
     * @param grpName
     * @param grpProps
     */
    ClusterGroupConfig(String grpName, Map grpProps) {
        assert grpName != null;
        assert grpProps != null;
        
        this.grpName = grpName;
        this.grpProps = grpProps;
    }

    /**
     * 
     * @return TBD
     */
    String getGroupName() {
        return grpName;
    }

    /**
     * 
     * @return TBD
     */
    Map getGroupProps() {
        return grpProps;
    }
    
    /**
     * @see Object#toString()
     */
    public String toString() {
        assert DebugFlags.CLUSTER == true;
        
        return "Cluster group config [group-name=" + grpName + ", group-props=" + grpProps + ']';
    }
}
