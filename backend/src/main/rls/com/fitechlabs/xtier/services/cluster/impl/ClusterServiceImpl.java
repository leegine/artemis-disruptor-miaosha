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
 * $Workfile:ClusterServiceImpl.java$
 * $Date:16.09.2005 17:02:00$
 * $Revision:38$
 */
 
package com.fitechlabs.xtier.services.cluster.impl;

import com.fitechlabs.xtier.debug.*;
import com.fitechlabs.xtier.kernel.*;
import com.fitechlabs.xtier.kernel.xml.*;
import com.fitechlabs.xtier.kernel.ioc.*;
import com.fitechlabs.xtier.kernel.spi.*;
import com.fitechlabs.xtier.l10n.*;
import com.fitechlabs.xtier.services.cluster.*;
import com.fitechlabs.xtier.services.cluster.sensors.*;
import com.fitechlabs.xtier.services.log.*;
import com.fitechlabs.xtier.utils.*;
import com.fitechlabs.xtier.utils.xml.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.xml.parsers.*;
import org.xml.sax.*;

/**
 * 
 * @author <a href="mailto:dsetraky@fitechlabs.com">Dmitriy Setrakyan</a>
 * @version 2.0
 */
public class ClusterServiceImpl extends ServiceProviderAdapter implements ClusterService {
    /** Configuration of cluster service. */
    private ClusterConfig cfg;
    
    /** Logger to use. */
    private Logger log;
    
    /** Cluster event notifications manager. */
    private ClusterEventManager evetMgr;
    
    /** */
    private ClusterTopologyManager topMgr;
    
    /** */
    private ClusterProtocolManager protoMgr;
    
    /** */
    private ClusterLockManager lockMgr;
    
    /** */
    private ClusterHeartbeatManager hrbtMgr;
    
    /** */
    private ClusterFailureManager failureMgr;
    
    /** */
    private ClusterCommManager commMgr;
    
    /** */
    private ClusterEncoder encoder;
    
    /**
     * @see KernelService#getName()
     */
    public String getName() {
        return "cluster";
    }
    
    /**
     * @see ClusterService#getTopologyVersion()
     */
    public int getTopologyVersion() {
        return topMgr.getVersion();
    }
    
    /**
     * @see ClusterService#getNodeFailureResolver()
     */
    public ClusterNodeFailureResolver getNodeFailureResolver() {
        return cfg.getFailureResolver();            
    }

    /**
     * @see ClusterService#getAllNodes()
     */
    public Set getAllNodes() {
        return topMgr.getAllNodes();            
    }

    /**
     * @see ClusterService#getNodes(ClusterFilter)
     */
    public Set getNodes(ClusterFilter filter) {
        return topMgr.getNodes(filter);
    }
    
    /**
     * @see ClusterService#getNode(InetAddress, int)
     */
    public ClusterNode getNode(InetAddress addr, int port) {
        ArgAssert.nullArg(addr, "addr");
        ArgAssert.illegalArg(port >= 0 && port <= 0xffff, "port");
        
        return topMgr.getNode(addr, port);
    }

    /**
     * @see ClusterService#getNode(int)
     */
    public ClusterNode getNode(int nodeId) {
        return topMgr.getNode(nodeId);
    }
    
    /**
     * @see ClusterService#getLocalNode()
     */
    public ClusterNode getLocalNode() {
        return topMgr.getLocalNode();
    }
    
    /**
     * @see ClusterService#getMulticastGroup()
     */
    public InetAddress getMulticastGroup() {
        return cfg.getMcastGroup();
    }
    
    /**
     * @see ClusterService#getMulticastPort()
     */
    public int getMulticastPort() {
        return cfg.getMcastPort();
    }

    /**
     * @see ClusterService#addListener(ClusterListener)
     */
    public boolean addListener(ClusterListener listener) {
        ArgAssert.nullArg(listener, "listener");
        
        return evetMgr.addListener(listener);
    }

    /**
     * @see ClusterService#getAllListeners()
     */
    public List getAllListeners() {
        return evetMgr.getAllListeners();
    }

    /**
     * @see ClusterService#removeListener(ClusterListener)
     */
    public boolean removeListener(ClusterListener listener) {
        ArgAssert.nullArg(listener, "listener");
        
        return evetMgr.removeListener(listener);
    }

    /**
     * @see ClusterService#addErrorListener(ClusterErrorListener)
     */
    public boolean addErrorListener(ClusterErrorListener listener) {
        ArgAssert.nullArg(listener, "listener");
        
        return evetMgr.addErrorListener(listener);
    }

    /**
     * @see ClusterService#getAllErrorListeners()
     */
    public List getAllErrorListeners() {
        return evetMgr.getAllErrorListeners();
    }

    /**
     * @see ClusterService#removeErrorListener(ClusterErrorListener)
     */
    public boolean removeErrorListener(ClusterErrorListener listener) {
        ArgAssert.nullArg(listener, "listener");
        
        return evetMgr.removeErrorListener(listener);
    }

    /**
     * @see ServiceProviderAdapter#onStart()
     */
    protected void onStart() throws ServiceProviderException {
        log = XtierKernel.getInstance().log().getLogger("cluster");
        
        try {
            parseXmlConfig("xtier_cluster.xml", new HashSet(), new HashSet());
        }
        catch (SAXException e) {
            throw new ServiceProviderException(L10n.format("SRVC.CLUSTER.ERR3", "xtier_cluster.xml"), e);
        }
        
        if (cfg == null) {
            throw new ServiceProviderException(L10n.format("SRVC.CLUSTER.ERR4", getRegionName()));
        }

        try {
            encoder = new ClusterEncoder(cfg);
            topMgr  = new ClusterTopologyManager(cfg, getDeclServices());
            evetMgr = new ClusterEventManager();
            lockMgr = new ClusterLockManager(log, cfg);
            commMgr = new ClusterCommManager(log, cfg, encoder);
            protoMgr = new ClusterProtocolManager(log, cfg);
            hrbtMgr = new ClusterHeartbeatManager(log, cfg, encoder, topMgr, evetMgr);
            failureMgr = new ClusterFailureManager(log, cfg);
        }
        catch (ClusterException e) {
            throw new ServiceProviderException(L10n.format("SRVC.CLUSTER.ERR1"), e);
        }
            
        try {
            // Order of starting is important.
            topMgr.start();
            evetMgr.start();
            lockMgr.start(topMgr, commMgr);
            protoMgr.start(lockMgr, topMgr, commMgr, evetMgr);
            commMgr.start(protoMgr, lockMgr, topMgr, hrbtMgr);

            protoMgr.joinCluster();
            
            hrbtMgr.start();
            failureMgr.start(lockMgr, topMgr, evetMgr);
        }
        catch (ClusterException e) {
            // In case of error we attempt to stop all started threads.
            // This is critical because any background thread left running
            // can make this cluster node seem alive from remote nodes.
            evetMgr.stop();
            hrbtMgr.stop();
            failureMgr.stop();
            commMgr.stop();
            lockMgr.stop();
            topMgr.stop();
            protoMgr.stop();

            // Give to GC.
            evetMgr = null;
            commMgr = null;
            protoMgr = null;
            lockMgr = null;
            hrbtMgr = null;
            failureMgr = null;
            topMgr = null;
            cfg = null;
            log = null;
            encoder = null;

            throw new ServiceProviderException(L10n.format("SRVC.CLUSTER.ERR1"), e);
        }
    }
    
    /**
     * @see ServiceProviderAdapter#onStop()
     */
    protected void onStop() throws ServiceProviderException {
        // Order of stopping is important.
        evetMgr.stop();
        hrbtMgr.stop();
        failureMgr.stop();
        
        ServiceProviderException error = null;
        
        try {
            protoMgr.leaveCluster();
        }
        catch (ClusterException e) {
            error = new ServiceProviderException(L10n.format("SRVC.CLUSTER.ERR2"), e);
        }

        commMgr.stop();
        lockMgr.stop();
        topMgr.stop();
        protoMgr.stop();
        
        // Give to GC.
        evetMgr = null;
        commMgr = null;
        protoMgr = null;
        lockMgr = null;
        hrbtMgr = null;
        failureMgr = null;
        topMgr = null;
        cfg = null;
        log = null;
        encoder = null;

        // Note that we allow cluster to gracefully stop before throwing exception.
        if (error != null) {
            throw error;
        }
    }

    /**
     * 
     * @param path
     * @param includes
     * @param regions
     * @throws SAXException
     */
    private void parseXmlConfig(String path, final Set includes, final Set regions) throws SAXException {
        assert path != null;
        assert includes != null;
        assert regions != null;
        
        // Prevent recursive inclusion.
        if (includes.contains(path) == true) {
            log.warning(L10n.format("SRVC.CLUSTER.WRN1", path));
            
            return;
        }
        
        includes.add(path);
        
        String url = "file:///" + Utils.makeValidPath(Utils.makeValidPath(getXtierRoot(), getConfigPath()), path);
        
        final String initRegion = getRegionName();
        
        try {
            XmlUtils.makeSaxParser().parse(url, new XmlSaxHandler(getXtierRoot(), "xtier_cluster.dtd") {
                /** Cluster configuration being currently parsed. */
                private ClusterConfig regionCfg;
                
                /** Cluster group configuration being currently parsed. */
                private ClusterGroupConfig grpCfg;
                
                /** Map of group memberships being currently parsed. */
                private Map grpCfgs;
                
                /** Map of group properties being currently parsed. */
                private Map grpProps;
                
                /** List of seed nodes being currently parsed. */
                private List seedNodes;
                
                /** Flag to indicate whether parsing initial region. */
                private boolean isInitRegion;
                
                /** Flag to indicate whether parsing failure resolver. */
                private boolean isResolver = false;
                
                /** Flag to indicate whether parsing CPU load sensor. */
                private boolean isCpuSensor = false;

                /** IoC descriptor for failure resolver. */
                private IocDescriptor resolverIoc;
        
                /** IoC descriptor for CPU load sensor. */
                private IocDescriptor cpuSensorIoc;

                /**
                 * @see XmlSaxHandler#onTagStart(String, XmlAttrInterceptor)
                 */
                protected void onTagStart(String tagName, XmlAttrInterceptor attrs) throws SAXException {
                    if (tagName.equals("include") == true) {
                        parseXmlConfig(attrs.getValue("path"), includes, regions);
                    }
                    else if (tagName.equals("region") == true) {
                        String regionName = attrs.getValue("name");
                        
                        // Make sure that there are no duplicate regions.
                        if (regions.contains(regionName) == true) {
                            throw createSaxErr(L10n.format("SRVC.CLUSTER.ERR5", regionName));
                        }
                        
                        regionCfg = new ClusterConfig();
                        
                        // If parsed init region, then save map of groups.
                        if (regionName.equals(initRegion) == true) {
                            cfg = regionCfg;
                            
                            isInitRegion = true;
                        }

                        regions.add(regionName);
                    }
                    else if (tagName.equals("memberships") == true) {
                        grpCfgs = new HashMap();
                        
                        regionCfg.setGroupConfigs(grpCfgs);
                    }
                    else if (tagName.equals("group") == true) {
                        String grpName = attrs.getValue("name");
                        
                        if (grpCfgs.containsKey(grpName) == true) {
                            throw createSaxErr(L10n.format("SRVC.CLUSTER.ERR6", grpName));
                        }
                        
                        grpProps = new HashMap();
                        
                        grpCfg = new ClusterGroupConfig(grpName, grpProps);
                        
                        grpCfgs.put(grpName, grpCfg);
                    }
                    else if (tagName.equals("prop") == true) {
                        grpProps.put(attrs.getValue("name"), attrs.getValue("value"));
                    }
                    else if (tagName.equals("local-node") == true) {
                        // Opetional parameter.
                        String addrStr = attrs.getValue("addr");
                        
                        // Only process IP addresses if this is init region.
                        if (isInitRegion == true) {
                            try {
                                // If local-addr is not specified, then grab 1st avialable IP address.
                                InetAddress localAddr = addrStr == null ? InetAddress.getLocalHost() : parseIp(addrStr);
                                
                                if (DebugFlags.CLUSTER == true) {
                                    Utils.debug("Localnode IP addres: " + localAddr);
                                }

                                regionCfg.setLocalAddress(localAddr);
                            }
                            catch (UnknownHostException e) {
                                throw new SAXException(L10n.format("SRVC.CLUSTER.ERR7"), e);
                            }
                        }

                        regionCfg.setLocalPort(parseIpPort(attrs.getValue("port")));
                    }
                    else if (tagName.equals("network") == true) {
                        int retries = parseInt(attrs.getValue("retries"));
                        
                        if (retries < 0) {
                            createSaxErr(L10n.format("SRVC.CLUSTER.ERR10", new Integer(1)));
                        }
                            
                        if (retries >= 5) {
                            log.warning(L10n.format("SRVC.CLUSTER.WRN3", new Integer(5), new Integer(retries)));
                        }

                        regionCfg.setRetries(retries);
                        
                        // Only process IP addresses if this is init region.
                        if (isInitRegion == true) {
                            // Assign IP-Multicast group address.
                            regionCfg.setMcastGroup(parseIp(attrs.getValue("mcast-group")));
                        }

                        regionCfg.setMcastPort(parseIpPort(attrs.getValue("mcast-port")));

                        // Check and assign multicast time-to-live.
                        int ttl = parseInt(attrs.getValue("mcast-ttl"));
                        
                        if (ttl < 0 || ttl > 255) {
                            throw createSaxErr(L10n.format("SRVC.CLUSTER.ERR8"));
                        }

                        regionCfg.setMcastTtl(ttl);
                        
                        int timeout = parseInt(attrs.getValue("timeout"));

                        // Check that timeout is valid.
                        if (timeout <= 0) {
                            throw createSaxErr(L10n.format("SRVC.CLUSTER.ERR9", new Long(0), new Long(timeout)));
                        }

                        if (timeout > 5000) {
                            log.warning(L10n.format("SRVC.CLUSTER.WRN2", new Long(5000), new Long(timeout)));
                        }
                        
                        regionCfg.setTimeout(timeout);
                    }
                    else if (tagName.equals("seed-nodes") == true) {
                        seedNodes = new ArrayList();

                        regionCfg.setSeedNodes(seedNodes);
                    }
                    else if (tagName.equals("node") == true) {
                        InetAddress addr = isInitRegion == true ? parseIp(attrs.getValue("addr")) : null;
                        int port = parseIpPort(attrs.getValue("port"));
                        
                        seedNodes.add(new InetSocketAddress(addr, port));
                    }
                    else if (tagName.equals("heartbeat") == true) {
                        // Check that heartbeat frequency is valid.
                        long freq = parseLong(attrs.getValue("frequency"));
                        
                        if (freq <= 0) {
                            throw createSaxErr(L10n.format("SRVC.CLUSTER.ERR11", new Long(freq)));
                        }
                        
                        if (freq < 1000 || freq > 100000) {
                            log.warning(L10n.format("SRVC.CLUSTER.WRN4", new Long(1000), new Long(100000),
                                new Long(freq)));
                        }

                        // Check that heartbeat loss threshold is valid.
                        int lossThreshold = parseInt(attrs.getValue("loss-threshold"));
                        
                        if (lossThreshold <= 0) {
                            throw createSaxErr(L10n.format("SRVC.CLUSTER.ERR12", new Integer(lossThreshold)));
                        }
                        
                        if (lossThreshold < 2 || lossThreshold > 5) {
                            log.warning(L10n.format("SRVC.CLUSTER.WRN5", new Integer(2), new Integer(5),
                                new Integer(lossThreshold)));
                        }
                        
                        regionCfg.setHeartbeatFreq(freq);
                        regionCfg.setHeartbeatLossThreshold(lossThreshold);
                    }
                    else if (tagName.equals("node-failure-resolver") == true) {
                        isResolver = true;
                    }
                    else if (tagName.equals("cpu-load-sensor") == true) {
                        isCpuSensor = true;
                    }
                }

                /**
                 * @see XmlSaxHandler#onIocDescriptor(IocDescriptor)
                 */
                protected void onIocDescriptor(IocDescriptor ioc) {
                    if (isResolver == true) {
                        resolverIoc = ioc;
                    }
                    else if (isCpuSensor == true) {
                        cpuSensorIoc = ioc;
                    }
                }

                /**
                 * @see XmlSaxHandler#onTagEnd(String)
                 */
                protected void onTagEnd(String tagName) throws SAXException {
                    if (tagName.equals("region") == true) {
                        if (isInitRegion == true) {
                            ClusterNodeFailureResolver resolver = null;
                            
                            try {
                                resolver = (ClusterNodeFailureResolver)resolverIoc.
                                    createNewObj(ClusterNodeFailureResolver.class);
                            }
                            catch (IocDescriptorException e) {
                                throw createSaxErr(L10n.format("SRVC.CLUSTER.ERR13"), e);
                            }
                            
                            regionCfg.setFailureResolver(resolver);

                            ClusterNodeCpuSensor cpuSensor = null;
                            
                            if (cpuSensorIoc != null) {                                    
                                try {
                                    cpuSensor = (ClusterNodeCpuSensor)cpuSensorIoc.
                                        createNewObj(ClusterNodeCpuSensor.class);
                                }
                                catch (IocDescriptorException e) {
                                    throw createSaxErr(L10n.format("SRVC.CLUSTER.ERR13"), e);
                                }
                            }
                            else {
                                cpuSensor = new ClusterNodeBasicCpuSensor(regionCfg.getHeartbeatFreq(), 1.0f);
                            }
                            
                            if (DebugFlags.CLUSTER == true) {
                                Utils.debug("CPU load sensor installed: " + cpuSensor);
                            }
                                
                            regionCfg.setCpuSensor(cpuSensor);
                        }
                        
                        isInitRegion = false;
                        
                        regionCfg = null;
                    }
                    else if (tagName.equals("node-failure-resolver") == true) {
                        isResolver = false;
                    }
                    else if (tagName.equals("cpu-load-sensor") == true) {
                        isCpuSensor = false;
                    }
                }
            });
        }
        catch (ParserConfigurationException e) {
            throw new SAXException(e);
        }
        catch (IOException e) {
            throw new SAXException(L10n.format("SRVC.CLUSTER.ERR3", url), e);
        }
    
        if (DebugFlags.CLUSTER == true) {
            Utils.debug("Cluster service XML file parsed OK: " + url);
        }            
    }
}
