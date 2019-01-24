// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.grid.impl;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.kernel.ioc.IocDescriptor;
import com.fitechlabs.xtier.kernel.ioc.IocDescriptorException;
import com.fitechlabs.xtier.kernel.spi.ServiceProviderAdapter;
import com.fitechlabs.xtier.kernel.spi.ServiceProviderException;
import com.fitechlabs.xtier.kernel.xml.XmlAttrInterceptor;
import com.fitechlabs.xtier.kernel.xml.XmlSaxHandler;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.cluster.ClusterNode;
import com.fitechlabs.xtier.services.cluster.ClusterService;
import com.fitechlabs.xtier.services.grid.*;
import com.fitechlabs.xtier.services.log.LogService;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.services.marshal.Marshallable;
import com.fitechlabs.xtier.services.objpool.threads.ThreadPool;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.Utils;
import com.fitechlabs.xtier.utils.xml.XmlUtils;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.*;
import javax.jms.JMSException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import org.xml.sax.SAXException;

// Referenced classes of package com.fitechlabs.xtier.services.grid.impl:
//            GridConfig, GridManager, GridJmsClientManager, GridTcpClientManager,
//            GridTaskBatchImpl

public class GridServiceImpl extends ServiceProviderAdapter
    implements GridService
{
    private class XmlRegion
    {

        String getName()
        {
            return name;
        }

        void setJmsFactoryName(String s)
        {
            jmsFactoryName = s;
        }

        String getJmsFactoryName()
        {
            return jmsFactoryName;
        }

        void setTcpLocalBind(InetAddress inetaddress)
        {
            tcpLocalBind = inetaddress;
        }

        InetAddress getTcpLocalBind()
        {
            return tcpLocalBind;
        }

        void setTcpClientPort(int i)
        {
            tcpClientPort = i;
        }

        int getTcpClientPort()
        {
            return tcpClientPort;
        }

        void addNodePort(InetAddress inetaddress, int i, int j)
        {
            nodePorts.put(new InetSocketAddress(inetaddress, i), new Integer(j));
        }

        boolean containsNodePort(InetAddress inetaddress, int i)
        {
            return nodePorts.containsKey(new InetSocketAddress(inetaddress, i));
        }

        Map getNodePorts()
        {
            return nodePorts;
        }

        boolean containsTid(int i)
        {
            return tasks.containsKey(new Integer(i));
        }

        void addTask(XmlGridTask xmlgridtask)
        {
            tasks.put(new Integer(xmlgridtask.getId()), xmlgridtask);
        }

        Map getTasks()
        {
            return tasks;
        }

        int getMaxExecTraces()
        {
            if(!$assertionsDisabled && maxExecTraces == -1)
                throw new AssertionError();
            else
                return maxExecTraces;
        }

        int getDfltPort()
        {
            if(!$assertionsDisabled && dfltPort == -1)
                throw new AssertionError();
            else
                return dfltPort;
        }

        IocDescriptor getTaxonomy()
        {
            return taxonomy;
        }

        String getPoolName()
        {
            return poolName;
        }

        void setMaxExecTraces(int i)
        {
            maxExecTraces = i;
        }

        void setDfltPort(int i)
        {
            dfltPort = i;
        }

        void setTaxonomy(IocDescriptor iocdescriptor)
        {
            taxonomy = iocdescriptor;
        }

        void setPoolName(String s)
        {
            poolName = s;
        }

        boolean isExecLocalNoSplit()
        {
            return execLocalNoSplit;
        }

        void setExecLocalNoSplit(boolean flag)
        {
            execLocalNoSplit = flag;
        }

        public String toString()
        {
            if(!$assertionsDisabled)
                throw new AssertionError();
            else
                return "XML grid region [default-port=" + dfltPort + ", max-exec-traces=" + maxExecTraces + ", pool-name=" + poolName + ", tasks=" + Utils.map2Str(tasks) + ", nodes=" + Utils.map2Str(nodePorts) + "exec-local-no-split=" + execLocalNoSplit + ']';
        }

        private String name;
        private int dfltPort;
        private Map nodePorts;
        private int maxExecTraces;
        private String poolName;
        private IocDescriptor taxonomy;
        private String jmsFactoryName;
        private InetAddress tcpLocalBind;
        private int tcpClientPort;
        private Map tasks;
        private boolean execLocalNoSplit;
        static final boolean $assertionsDisabled; /* synthetic field */


        XmlRegion(String s)
        {
            super();
            dfltPort = -1;
            nodePorts = new HashMap();
            maxExecTraces = -1;
            poolName = null;
            taxonomy = null;
            tasks = new HashMap();
            execLocalNoSplit = true;
            name = s;
        }
    }

    private class XmlGridTask
    {

        int getId()
        {
            return tid;
        }

        IocDescriptor getFactory()
        {
            return factory;
        }

        IocDescriptor getTopology()
        {
            return topology;
        }

        IocDescriptor getFailoverResolver()
        {
            return failover;
        }

        void setFactory(IocDescriptor iocdescriptor)
        {
            factory = iocdescriptor;
        }

        void setFailoverResolver(IocDescriptor iocdescriptor)
        {
            failover = iocdescriptor;
        }

        void setTopology(IocDescriptor iocdescriptor)
        {
            topology = iocdescriptor;
        }

        IocDescriptor getRouter()
        {
            return router;
        }

        void setRouter(IocDescriptor iocdescriptor)
        {
            router = iocdescriptor;
        }

        public String toString()
        {
            if(!$assertionsDisabled)
                throw new AssertionError();
            else
                return "XML grid task [tid=" + tid + ']';
        }

        private int tid;
        private IocDescriptor factory;
        private IocDescriptor topology;
        private IocDescriptor router;
        private IocDescriptor failover;
        static final boolean $assertionsDisabled; /* synthetic field */


        XmlGridTask(int i)
        {
            super();
            factory = null;
            topology = null;
            router = null;
            failover = null;
            tid = i;
        }
    }


    public GridServiceImpl()
    {
        gridMgr = null;
        jmsClientMgr = null;
        tcpClientMgr = null;
        log = null;
    }

    private void parseXmlConfig(String s, Map map, Set set)
        throws SAXException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        if(set.contains(s))
        {
            log.warning(L10n.format("SRVC.GRID.WRN1", s));
            return;
        }
        set.add(s);
        String s1 = "file:///" + Utils.makeValidPath(Utils.makeValidPath(getXtierRoot(), getConfigPath()), s);
        try
        {
            XmlUtils.makeSaxParser().parse(s1, new XmlSaxHandler(map, set) {

                protected void onTagStart(String s2, XmlAttrInterceptor xmlattrinterceptor)
                    throws SAXException
                {
                    if(s2.equals("include"))
                    {
                        String s3 = xmlattrinterceptor.getValue("path");
                        parseXmlConfig(s3, regions, includes);
                    } else
                    if(s2.equals("task"))
                    {
                        if(!$assertionsDisabled && task != null)
                            throw new AssertionError();
                        int i = parseInt(xmlattrinterceptor.getValue("id"));
                        if(region.containsTid(i))
                            throw createSaxErr(L10n.format("SRVC.GRID.ERR17", new Integer(i)));
                        task = new XmlGridTask(i);
                    } else
                    if(s2.equals("jms-client"))
                        region.setJmsFactoryName(xmlattrinterceptor.getValue("conn-factory"));
                    else
                    if(s2.equals("tcp-client"))
                        region.setTcpClientPort(parseIpPort(xmlattrinterceptor.getValue("ip-port")));
                    else
                    if(s2.equals("ip-port"))
                        region.setDfltPort(parseIpPort(xmlattrinterceptor.getValue("default")));
                    else
                    if(s2.equals("node"))
                    {
                        InetAddress inetaddress = parseIp(xmlattrinterceptor.getValue("cluster-addr"));
                        int j = parseIpPort(xmlattrinterceptor.getValue("cluster-port"));
                        if(region.containsNodePort(inetaddress, j))
                            throw createSaxErr(L10n.format("SRVC.GRID.ERR30", inetaddress, new Integer(j)));
                        region.addNodePort(inetaddress, j, parseIpPort(xmlattrinterceptor.getValue("grid-port")));
                    } else
                    if(s2.equals("region"))
                    {
                        if(!$assertionsDisabled && region != null)
                            throw new AssertionError();
                        String s4 = xmlattrinterceptor.getValue("name");
                        if(regions.containsKey(s4))
                            throw createSaxErr(L10n.format("SRVC.GRID.ERR18", s4));
                        region = new XmlRegion(s4);
                    } else
                    if(s2.equals("factory"))
                        isFactory = true;
                    else
                    if(s2.equals("topology"))
                        isTopology = true;
                    else
                    if(s2.equals("taxonomy"))
                        isTaxonomy = true;
                    else
                    if(s2.equals("failover"))
                        isFailover = true;
                    else
                    if(s2.equals("router"))
                        isRouter = true;
                }

                protected void onIocDescriptor(IocDescriptor iocdescriptor)
                {
                    if(isFactory)
                        task.setFactory(iocdescriptor);
                    else
                    if(isTopology)
                        task.setTopology(iocdescriptor);
                    else
                    if(isRouter)
                        task.setRouter(iocdescriptor);
                    else
                    if(isFailover)
                        task.setFailoverResolver(iocdescriptor);
                    else
                    if(isTaxonomy)
                        region.setTaxonomy(iocdescriptor);
                }

                protected void onTagEnd(String s2)
                    throws SAXException
                {
                    if(s2.equals("task"))
                    {
                        region.addTask(task);
                        task = null;
                    } else
                    if(s2.equals("region"))
                    {
                        regions.put(region.getName(), region);
                        region = null;
                    } else
                    if(s2.equals("factory"))
                        isFactory = false;
                    else
                    if(s2.equals("topology"))
                        isTopology = false;
                    else
                    if(s2.equals("failover"))
                        isFailover = false;
                    else
                    if(s2.equals("taxonomy"))
                        isTaxonomy = false;
                    else
                    if(s2.equals("router"))
                        isRouter = false;
                    else
                    if(s2.equals("max-exec-traces"))
                        region.setMaxExecTraces(parseInt(getPcdata()));
                    else
                    if(s2.equals("thread-pool-name"))
                        region.setPoolName(getPcdata());
                    else
                    if(s2.equals("exec-local-no-split"))
                        region.setExecLocalNoSplit(parseBoolean(getPcdata()));
                }

                private XmlRegion region;
                private XmlGridTask task;
                private boolean isFactory;
                private boolean isTopology;
                private boolean isTaxonomy;
                private boolean isFailover;
                private boolean isRouter;
                static final boolean $assertionsDisabled; /* synthetic field */



                throws SAXException
            {
                super(final_s, final_s1);
                region = null;
                task = null;
                isFactory = false;
                isTopology = false;
                isTaxonomy = false;
                isFailover = false;
                isRouter = false;
            }
            }
);
        }
        catch(ParserConfigurationException parserconfigurationexception)
        {
            throw new SAXException(parserconfigurationexception);
        }
        catch(IOException ioexception)
        {
            throw new SAXException(L10n.format("SRVC.GRID.ERR19", s1), ioexception);
        }
    }

    protected void onStart()
        throws ServiceProviderException
    {
        XtierKernel xtierkernel = XtierKernel.getInstance();
        log = xtierkernel.log().getLogger("grid");
        HashMap hashmap = new HashMap();
        try
        {
            parseXmlConfig("xtier_grid.xml", hashmap, new HashSet());
        }
        catch(SAXException saxexception)
        {
            throw new ServiceProviderException(L10n.format("SRVC.GRID.ERR20"), saxexception);
        }
        XmlRegion xmlregion = (XmlRegion)hashmap.get(getRegionName());
        if(xmlregion == null)
            throw new ServiceProviderException(L10n.format("SRVC.GRID.ERR21", getRegionName()));
        GridConfig gridconfig = new GridConfig();
        gridconfig.setIpAddr(xtierkernel.cluster().getLocalNode().getAddress());
        gridconfig.setMaxExecTraces(xmlregion.getMaxExecTraces());
        gridconfig.setDfltPort(xmlregion.getDfltPort());
        gridconfig.setPoolName(xmlregion.getPoolName());
        gridconfig.setNodePorts(xmlregion.getNodePorts());
        gridconfig.setJmsFactoryName(xmlregion.getJmsFactoryName());
        gridconfig.setTcpClientPort(xmlregion.getTcpClientPort());
        gridconfig.setExecLocalNoSplit(xmlregion.isExecLocalNoSplit());
        IocDescriptor iocdescriptor = xmlregion.getTaxonomy();
        if(iocdescriptor != null)
            try
            {
                gridconfig.setTaxonomy((GridTaxonomy)iocdescriptor.createNewObj(com.fitechlabs.xtier.services.grid.GridTaxonomy.class));
            }
            catch(IocDescriptorException iocdescriptorexception)
            {
                throw new ServiceProviderException(L10n.format("SRVC.GRID.ERR22", iocdescriptor), iocdescriptorexception);
            }
        try
        {
            gridMgr = new GridManager(gridconfig);
        }
        catch(IOException ioexception)
        {
            throw new ServiceProviderException(L10n.format("SRVC.GRID.ERR25"), ioexception);
        }
        for(Iterator iterator = xmlregion.getTasks().values().iterator(); iterator.hasNext();)
        {
            XmlGridTask xmlgridtask = (XmlGridTask)iterator.next();
            IocDescriptor iocdescriptor1 = xmlgridtask.getFactory();
            IocDescriptor iocdescriptor2 = xmlgridtask.getTopology();
            IocDescriptor iocdescriptor3 = xmlgridtask.getRouter();
            IocDescriptor iocdescriptor4 = xmlgridtask.getFailoverResolver();
            GridTask gridtask = null;
            try
            {
                gridtask = new GridTask(xmlgridtask.getId(), (GridTaskUnitFactory)iocdescriptor1.createNewObj(com.fitechlabs.xtier.services.grid.GridTaskUnitFactory.class), (GridTaskTopology)iocdescriptor2.createNewObj(com.fitechlabs.xtier.services.grid.GridTaskTopology.class), (GridTaskRouter)iocdescriptor3.createNewObj(com.fitechlabs.xtier.services.grid.GridTaskRouter.class), (GridTaskFailoverResolver)iocdescriptor4.createNewObj(com.fitechlabs.xtier.services.grid.GridTaskFailoverResolver.class));
            }
            catch(IocDescriptorException iocdescriptorexception1)
            {
                throw new ServiceProviderException(L10n.format("SRVC.GRID.ERR23", new Integer(xmlgridtask.getId()), iocdescriptor1, iocdescriptor2, iocdescriptor3), iocdescriptorexception1);
            }
            if(!$assertionsDisabled && gridtask == null)
                throw new AssertionError();
            try
            {
                gridMgr.regsiterTask(gridtask);
            }
            catch(GridException gridexception1)
            {
                throw new ServiceProviderException(L10n.format("SRVC.GRID.ERR24", gridtask), gridexception1);
            }
        }

        if(gridconfig.getJmsFactoryName() != null)
            try
            {
                jmsClientMgr = new GridJmsClientManager(gridMgr, gridconfig);
            }
            catch(JMSException jmsexception)
            {
                throw new ServiceProviderException(L10n.format("SRVC.GRID.ERR36"), jmsexception);
            }
        try
        {
            tcpClientMgr = new GridTcpClientManager(gridMgr, gridconfig);
        }
        catch(GridException gridexception)
        {
            throw new ServiceProviderException(L10n.format("SRVC.GRID.ERR36"), gridexception);
        }
    }

    protected void onStop()
        throws ServiceProviderException
    {
        if(jmsClientMgr != null)
            jmsClientMgr.stop();
        if(tcpClientMgr != null)
            tcpClientMgr.stop();
        gridMgr.stop();
        gridMgr = null;
        log = null;
    }

    public GridTaxonomy getGridTaxonomy()
    {
        return gridMgr.getTaxonomy();
    }

    public GridTaskResult exec(GridTask gridtask, Marshallable marshallable)
    {
        ArgAssert.nullArg(gridtask, "task");
        return gridMgr.exec(gridtask, marshallable);
    }

    public GridTaskResult exec(GridTask gridtask, long l, Marshallable marshallable)
    {
        ArgAssert.nullArg(gridtask, "task");
        return gridMgr.exec(gridtask, l, marshallable);
    }

    public void exec(GridTask gridtask, Marshallable marshallable, GridTaskResultListener gridtaskresultlistener, ThreadPool threadpool)
    {
        ArgAssert.nullArg(gridtask, "task");
        ArgAssert.nullArg(gridtaskresultlistener, "listener");
        ArgAssert.nullArg(threadpool, "pool");
        gridMgr.exec(gridtask, marshallable, gridtaskresultlistener, threadpool);
    }

    public void exec(GridTask gridtask, long l, Marshallable marshallable, GridTaskResultListener gridtaskresultlistener, ThreadPool threadpool)
    {
        ArgAssert.nullArg(gridtask, "task");
        ArgAssert.nullArg(gridtaskresultlistener, "listener");
        ArgAssert.nullArg(threadpool, "pool");
        gridMgr.exec(gridtask, l, marshallable, gridtaskresultlistener, threadpool);
    }

    public void exec(GridTask gridtask, Marshallable marshallable, GridTaskResultListener gridtaskresultlistener)
    {
        ArgAssert.nullArg(gridtask, "task");
        ArgAssert.nullArg(gridtaskresultlistener, "listener");
        gridMgr.exec(gridtask, marshallable, gridtaskresultlistener);
    }

    public void exec(GridTask gridtask, long l, Marshallable marshallable, GridTaskResultListener gridtaskresultlistener)
    {
        ArgAssert.nullArg(gridtask, "task");
        ArgAssert.nullArg(gridtaskresultlistener, "listener");
        gridMgr.exec(gridtask, l, marshallable, gridtaskresultlistener);
    }

    public List exec(GridTaskBatch gridtaskbatch)
    {
        ArgAssert.nullArg(gridtaskbatch, "batch");
        return gridMgr.exec(gridtaskbatch);
    }

    public List exec(GridTaskBatch gridtaskbatch, long l)
    {
        ArgAssert.nullArg(gridtaskbatch, "batch");
        return gridMgr.exec(gridtaskbatch, l);
    }

    public GridTaskBatch createBatch(boolean flag)
    {
        return new GridTaskBatchImpl(flag);
    }

    public void regsiterTask(GridTask gridtask)
        throws GridException
    {
        ArgAssert.nullArg(gridtask, "task");
        gridMgr.regsiterTask(gridtask);
    }

    public boolean unregisterTask(int i)
    {
        return gridMgr.unregisterTask(i);
    }

    public void unregisterAll()
    {
        gridMgr.unregisterAll();
    }

    public GridTask getTask(int i)
    {
        return gridMgr.getTask(i);
    }

    public List getExecTrace(long l, long l1)
    {
        return gridMgr.getExecTrace(l, l1);
    }

    public List getAllTasks()
    {
        return gridMgr.getAllTasks();
    }

    public String getName()
    {
        return "grid";
    }

    public GridTaskResult exec(int i, Marshallable marshallable)
    {
        return gridMgr.exec(i, marshallable);
    }

    public GridTaskResult exec(int i, long l, Marshallable marshallable)
    {
        return gridMgr.exec(i, l, marshallable);
    }

    public void exec(int i, Marshallable marshallable, GridTaskResultListener gridtaskresultlistener, ThreadPool threadpool)
    {
        ArgAssert.nullArg(gridtaskresultlistener, "listener");
        ArgAssert.nullArg(threadpool, "pool");
        gridMgr.exec(i, marshallable, gridtaskresultlistener, threadpool);
    }

    public void exec(int i, long l, Marshallable marshallable, GridTaskResultListener gridtaskresultlistener, ThreadPool threadpool)
    {
        ArgAssert.nullArg(gridtaskresultlistener, "listener");
        ArgAssert.nullArg(threadpool, "pool");
        gridMgr.exec(i, l, marshallable, gridtaskresultlistener, threadpool);
    }

    public void exec(int i, Marshallable marshallable, GridTaskResultListener gridtaskresultlistener)
    {
        ArgAssert.nullArg(gridtaskresultlistener, "listener");
        gridMgr.exec(i, marshallable, gridtaskresultlistener);
    }

    public void exec(int i, long l, Marshallable marshallable, GridTaskResultListener gridtaskresultlistener)
    {
        ArgAssert.nullArg(gridtaskresultlistener, "listener");
        gridMgr.exec(i, l, marshallable, gridtaskresultlistener);
    }

    private GridManager gridMgr;
    private GridJmsClientManager jmsClientMgr;
    private GridTcpClientManager tcpClientMgr;
    private Logger log;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(GridServiceImpl.class).desiredAssertionStatus();
    }

}
