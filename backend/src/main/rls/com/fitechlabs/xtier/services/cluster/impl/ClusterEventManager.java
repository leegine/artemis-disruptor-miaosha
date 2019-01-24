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
 * $Workfile:ClusterEventManager.java$
 * $Date:10/6/2004 11:01:04 AM$
 * $Revision:14$
 */

package com.fitechlabs.xtier.services.cluster.impl;

import com.fitechlabs.xtier.services.cluster.*;
import com.fitechlabs.xtier.utils.*;
import java.net.*;
import java.util.*;

/**
 * @author <a href="mailto:dsetraky@fitechlabs.com">Dmitriy Setrakyan</a>
 * @version 2.0
 */
class ClusterEventManager {
    /** */
    private List listeners = new ArrayList();
    
    /** */
    private List errListeners = new ArrayList();
    
    /** */
    private FifoQueue events = new FifoQueue();
    
    /** */
    private final Object mutex = new Object();
    
    /**
     * 
     */
    void start() {
        // No-op.
    }
    
    /**
     * 
     */
    void stop() {
        // No-op.
    }
    
    /**
     * 
     * @param l
     * @return TBD
     */
    boolean addListener(ClusterListener l) {
        synchronized (mutex) {
            if (listeners.contains(l) == false) {
                listeners.add(l);
                
                return true;
            }
            
            return false;
        }
    }
    
    /**
     * 
     * @param l
     * @return TBD
     */
    boolean removeListener(ClusterListener l) {
        synchronized (mutex) {
            return listeners.remove(l);
        }
    }
    
    /**
     * @return TBD
     */
    List getAllListeners() {
        synchronized (mutex) {
            return Collections.unmodifiableList(new ArrayList(listeners));
        }
    }
    
    /**
     * 
     * @param l
     * @return TBD
     */
    boolean addErrorListener(ClusterErrorListener l) {
        synchronized (mutex) {
            if (errListeners.contains(l) == false) {
                errListeners.add(l);
                
                return true;
            }
            
            return false;
        }
    }
    
    /**
     * 
     * @param l
     * @return TBD
     */
    boolean removeErrorListener(ClusterErrorListener l) {
        synchronized (mutex) {
            return errListeners.remove(l);
        }
    }
    
    /**
     * @return TBD
     */
    List getAllErrorListeners() {
        synchronized (mutex) {
            return Collections.unmodifiableList(new ArrayList(errListeners));
        }
    }
    
    /**
     * 
     * @param addr
     */
    void dispatchUknownNodeError(InetSocketAddress addr) {
        assert addr != null;

        synchronized (mutex) {
            for (int i = 0; i < errListeners.size(); i++) {
                ((ClusterErrorListener)errListeners.get(i)).onUnknownNode(addr);
            }
        }
    }
    
    /**
     * 
     * @param eventId
     * @param node
     * @param topVer
     */
    void addEvent(int eventId, ClusterNodeImpl node, int topVer) {
        synchronized (events) {
            events.add(new Event(node, eventId, topVer));
        }
    }
    
    /**
     * 
     *
     */
    void dispatch() {
        int count;
        
        synchronized (events) {
            count = events.getCount();
        }
        
        Event event;
        
        // Note that we don't iterate until queue is empty, since
        // it potentially may result into an infinite loop.
        for (int i = 0; i < count; i++) {
            synchronized (events) {
                if (events.isEmpty() == true) {
                    break;
                }
                
                event = (Event)events.get();
            }
            
            // Dispatch event without holding any locks.
            dispatch(event);
        }
    }
    
    /**
     * 
     * @param evt
     */
    private void dispatch(Event evt) {
        synchronized (mutex) {
            for (int i = 0, n = listeners.size(); i < n; i++) {
                ((ClusterListener)listeners.get(i)).onNodeEvent(evt.getEventId(), evt.getNode(),
                    evt.getTopologyVersion());
            }
        }
    }
    
    /**
     * 
     * @author <a href="mailto:dsetraky@fitechlabs.com">Dmitriy Setrakyan</a>
     * @version 2.0
     */
    private static class Event {
        /** */
        private ClusterNodeImpl node;
        
        /** */
        private int eventId;
        
        /** */
        private int topVer;
        
        /**
         * @param node
         * @param eventId
         * @param topVer
         */
        Event(ClusterNodeImpl node, int eventId, int topVer) {
            this.node = node;
            this.eventId = eventId;
            this.topVer = topVer;
        }
        
        /**
         * Gets the event id value.
         * 
         * @return TBD
         */
        int getEventId() {
            return eventId;
        }

        /**
         * Gets the node value.
         * 
         * @return Node affected by event.
         */
        ClusterNodeImpl getNode() {
            return node;
        }
        
        /**
         * @return Topology version associated with this event.
         */
        int getTopologyVersion() {
            return topVer;
        }
    }
}
