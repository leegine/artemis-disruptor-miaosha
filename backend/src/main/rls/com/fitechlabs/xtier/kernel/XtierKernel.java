// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.kernel;

import com.fitechlabs.xtier.jmxrmi.spi.JmxRmiServer;
import com.fitechlabs.xtier.kernel.license.LicenseDescriptor;
import com.fitechlabs.xtier.kernel.license.LicenseException;
import com.fitechlabs.xtier.kernel.license.LicenseFeatureSet;
import com.fitechlabs.xtier.kernel.license.LicenseManager;
import com.fitechlabs.xtier.kernel.manage.ManageMBeanImpl;
import com.fitechlabs.xtier.kernel.mbean.DynamicMBeanFactory;
import com.fitechlabs.xtier.kernel.mbean.DynamicXtierMBean;
import com.fitechlabs.xtier.kernel.spi.ServiceProvider;
import com.fitechlabs.xtier.kernel.spi.ServiceProviderException;
import com.fitechlabs.xtier.kernel.xml.XmlAttrInterceptor;
import com.fitechlabs.xtier.kernel.xml.XmlSaxHandler;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.microkernel.*;
import com.fitechlabs.xtier.services.cache.CacheService;
import com.fitechlabs.xtier.services.cluster.ClusterNode;
import com.fitechlabs.xtier.services.cluster.ClusterService;
import com.fitechlabs.xtier.services.config.ConfigService;
import com.fitechlabs.xtier.services.db.DbService;
import com.fitechlabs.xtier.services.email.EmailService;
import com.fitechlabs.xtier.services.fs.FsService;
import com.fitechlabs.xtier.services.grid.GridService;
import com.fitechlabs.xtier.services.i18n.I18nService;
import com.fitechlabs.xtier.services.info.InfoService;
import com.fitechlabs.xtier.services.ioc.IocService;
import com.fitechlabs.xtier.services.jms.JmsService;
import com.fitechlabs.xtier.services.jmx.JmxService;
import com.fitechlabs.xtier.services.jndi.JndiService;
import com.fitechlabs.xtier.services.jobs.JobsService;
import com.fitechlabs.xtier.services.log.LogService;
import com.fitechlabs.xtier.services.marshal.MarshalService;
import com.fitechlabs.xtier.services.objpool.ObjectPoolService;
import com.fitechlabs.xtier.services.os.OsService;
import com.fitechlabs.xtier.services.security.SecurityService;
import com.fitechlabs.xtier.services.startup.StartupService;
import com.fitechlabs.xtier.services.tx.TxService;
import com.fitechlabs.xtier.services.uidgen.UidGenService;
import com.fitechlabs.xtier.services.workflow.WorkflowService;
import com.fitechlabs.xtier.threads.SysThreadGroup;
import com.fitechlabs.xtier.utils.*;
import com.fitechlabs.xtier.utils.xml.XmlUtils;
import java.io.IOException;
import java.net.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.util.*;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

// Referenced classes of package com.fitechlabs.xtier.kernel:
//            KernelException, KernelLifeCycleListener, KernelServiceListener, KernelServiceException,
//            KernelService

public class XtierKernel
{
    private static class KernelRegion
    {

        void clear()
        {
            descriptors.clear();
            name = null;
            featureSet = null;
            cfgPath = null;
        }

        void addServiceDescriptor(ServiceDescriptor servicedescriptor)
        {
            if(!$assertionsDisabled && servicedescriptor == null)
            {
                throw new AssertionError();
            } else
            {
                descriptors.put(servicedescriptor.getName(), servicedescriptor);
                return;
            }
        }

        String getName()
        {
            return name;
        }

        String getFeatureSet()
        {
            return featureSet;
        }

        String getConfigPath()
        {
            return cfgPath;
        }

        Map getServiceDescriptors()
        {
            return descriptors;
        }

        public String toString()
        {
            return L10n.format("KRNL.KRNL.TXT14", name, featureSet, cfgPath);
        }

        private String name;
        private String featureSet;
        private String cfgPath;
        private Map descriptors;
        static final boolean $assertionsDisabled; /* synthetic field */


        KernelRegion(String s, String s1, String s2)
        {
            descriptors = new HashMap();
            if(!$assertionsDisabled && (s == null || s1 == null || s2 == null))
            {
                throw new AssertionError();
            } else
            {
                name = s;
                featureSet = s1;
                cfgPath = s2;
                return;
            }
        }
    }

    private static class ServiceDescriptor
    {

        String getName()
        {
            return name;
        }

        String getRegion()
        {
            return xmlRegion;
        }

        String getConfigPath()
        {
            return cfgRegion;
        }

        public String toString()
        {
            return L10n.format("KRNL.KRNL.TXT13", name, xmlRegion, cfgRegion);
        }

        private String name;
        private String xmlRegion;
        private String cfgRegion;
        static final boolean $assertionsDisabled; /* synthetic field */


        ServiceDescriptor(String s, String s1, String s2)
        {
            if(!$assertionsDisabled && s == null)
            {
                throw new AssertionError();
            } else
            {
                name = s;
                xmlRegion = s1;
                cfgRegion = s2;
                return;
            }
        }
    }


    public static XtierKernel getInstance()
    {
        Object obj = instanceMutex;
        JVM INSTR monitorenter ;
        if(instance == null)
            instance = new XtierKernel();
        return instance;
        Exception exception;
        exception;
        throw exception;
    }

    private XtierKernel()
    {
        state = 2;
        ctx = null;
        log = null;
        region = null;
        hook = null;
        startStack = null;
        startedServices = null;
        transientServices = null;
        registry = null;
        classes = new HashMap();
        kernelListeners = new HashSet();
        serviceListeners = null;
        declServices = null;
        localBind = null;
        rmiReg = null;
        rmiRegPort = 10991;
        startTime = -1L;
        if(!$assertionsDisabled && !Thread.holdsLock(instanceMutex))
        {
            throw new AssertionError();
        } else
        {
            classes.put("os", com.fitechlabs.xtier.services.os.impl.OsServiceImpl.class);
            classes.put("fs", com.fitechlabs.xtier.services.fs.impl.FsServiceImpl.class);
            classes.put("db", com.fitechlabs.xtier.services.db.impl.DbServiceImpl.class);
            classes.put("info", com.fitechlabs.xtier.services.info.impl.InfoServiceImpl.class);
            classes.put("jmx", com.fitechlabs.xtier.services.jmx.impl.JmxServiceImpl.class);
            classes.put("log", com.fitechlabs.xtier.services.log.impl.LogServiceImpl.class);
            classes.put("email", com.fitechlabs.xtier.services.email.impl.EmailServiceImpl.class);
            classes.put("jndi", com.fitechlabs.xtier.services.jndi.impl.JndiServiceImpl.class);
            classes.put("i18n", com.fitechlabs.xtier.services.i18n.impl.I18nServiceImpl.class);
            classes.put("tx", com.fitechlabs.xtier.services.tx.impl.TxServiceImpl.class);
            classes.put("objpool", com.fitechlabs.xtier.services.objpool.impl.ObjectPoolServiceImpl.class);
            classes.put("workflow", com.fitechlabs.xtier.services.workflow.impl.WorkflowServiceImpl.class);
            classes.put("marshal", com.fitechlabs.xtier.services.marshal.impl.MarshalServiceImpl.class);
            classes.put("startup", com.fitechlabs.xtier.services.startup.impl.StartupServiceImpl.class);
            classes.put("cluster", com.fitechlabs.xtier.services.cluster.impl.ClusterServiceImpl.class);
            classes.put("security", com.fitechlabs.xtier.services.security.impl.SecurityServiceImpl.class);
            classes.put("uidgen", com.fitechlabs.xtier.services.uidgen.impl.UidGenServiceImpl.class);
            classes.put("ioc", com.fitechlabs.xtier.services.ioc.impl.IocServiceImpl.class);
            classes.put("config", com.fitechlabs.xtier.services.config.impl.ConfigServiceImpl.class);
            classes.put("jobs", com.fitechlabs.xtier.services.jobs.impl.JobsServiceImpl.class);
            classes.put("cache", com.fitechlabs.xtier.services.cache.impl.CacheServiceImpl.class);
            classes.put("grid", com.fitechlabs.xtier.services.grid.impl.GridServiceImpl.class);
            classes.put("jms", com.fitechlabs.xtier.services.jms.impl.JmsServiceImpl.class);
            return;
        }
    }

    public boolean isStarted(String s)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        state;
        JVM INSTR tableswitch 1 5: default 64
    //                   1 57
    //                   2 60
    //                   3 44
    //                   4 57
    //                   5 57;
           goto _L1 _L2 _L3 _L4 _L2 _L2
_L4:
        throw new IllegalStateException(L10n.format("KRNL.KRNL.ERR49"));
_L3:
        return false;
_L1:
        if(!$assertionsDisabled)
            throw new AssertionError("Unknown kernel state: " + state);
_L2:
        registry.containsKey(s);
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public void start(Map map)
        throws KernelException
    {
        if(map == null)
            throw new IllegalArgumentException("Parameter 'props' is null.");
        MicroKernelContext microkernelcontext = (MicroKernelContext)map.get("micro.kernel.context");
        if(microkernelcontext == null)
            throw new KernelException("Micro kernel did not provide micro kernel context to the kernel.");
        String s = microkernelcontext.getXtierRoot();
        if(s == null)
        {
            throw new KernelException("Micro kernel context does not specify xTier root.");
        } else
        {
            System.setProperty("XTIER_ROOT", s);
            System.setProperty("xtier.root", s);
            start0(microkernelcontext);
            return;
        }
    }

    private void start0(MicroKernelContext microkernelcontext)
        throws KernelException
    {
        ctx = microkernelcontext;
        boolean flag = false;
        Object obj = mutex;
        JVM INSTR monitorenter ;
        int i = state;
label0:
        {
            String s = microkernelcontext.getLocalBindHost();
            if(s != null)
            {
                localBind = new InetSocketAddress(s, 0);
                if(localBind.isUnresolved())
                    throw new KernelException(L10n.format("KRNL.KRNL.ERR24", s));
                System.setProperty("xtier.local.host", s);
            }
            rmiRegPort = microkernelcontext.getRmiRegPort();
            Locale locale = microkernelcontext.getLocale();
            if(locale != null)
                Locale.setDefault(locale);
            System.setProperty("xtier.locale", Locale.getDefault().toString());
            switch(state)
            {
            case 2: // '\002'
                break;

            case 3: // '\003'
                throw new IllegalStateException(L10n.format("KRNL.KRNL.ERR13"));

            case 1: // '\001'
                throw new IllegalStateException(L10n.format("KRNL.KRNL.ERR2"));

            case 5: // '\005'
                if(!$assertionsDisabled)
                    throw new AssertionError("Invalid kernel state transition.");
                break;

            case 4: // '\004'
                if(!$assertionsDisabled)
                    throw new AssertionError("Invalid kernel state transition.");
                break;

            default:
                if(!$assertionsDisabled)
                    throw new AssertionError("Unknown kernel state: " + state);
                break;
            }
            state = 5;
            log = microkernelcontext.getLogger().getLogger("kernel");
            log.trace(L10n.format("KRNL.KRNL.TXT15"));
            String s1 = microkernelcontext.getXtierRoot();
            if(!$assertionsDisabled && s1 == null)
                throw new AssertionError();
            Map map = parseKernelRegions(s1, "config/xtier_kernel.xml");
            String s2 = microkernelcontext.getKernelRegion();
            region = (KernelRegion)map.get(s2);
            if(region == null)
                throw new KernelException(L10n.format("KRNL.KRNL.ERR7", s2));
            System.setProperty("xtier.kernel.region", region.getName());
            try
            {
                LicenseManager.init(s1, region.getFeatureSet());
            }
            catch(LicenseException licenseexception)
            {
                throw new KernelException(licenseexception.getMessage());
            }
            region.getServiceDescriptors().keySet().toArray(declServices = new String[region.getServiceDescriptors().size()]);
            boolean flag1 = true;
            for(int j = 0; j < declServices.length; j++)
            {
                String s3 = declServices[j];
                boolean flag3 = false;
                int l = 0;
                do
                {
                    if(l >= DEPENDENCIES.length)
                        break;
                    if(DEPENDENCIES[l][0].equals(s3))
                    {
                        flag3 = true;
                        for(int j1 = 1; j1 < DEPENDENCIES[l].length; j1++)
                        {
                            String s5 = DEPENDENCIES[l][j1];
                            if(!Utils.contains(declServices, s5))
                            {
                                flag1 = false;
                                log.error(L10n.format("KRNL.KRNL.ERR55", s3, s5));
                            }
                        }

                        break;
                    }
                    l++;
                } while(true);
                if(!flag3 && !$assertionsDisabled)
                    throw new AssertionError("Service has no dependency record: " + s3);
            }

            if(!flag1)
                throw new KernelException(L10n.format("KRNL.KRNL.ERR54"));
            startStack = new Stack();
            registry = new HashMap();
            startedServices = new ArrayList();
            transientServices = new LinkedList();
            serviceListeners = new HashMap();
            LicenseFeatureSet licensefeatureset = LicenseManager.getActiveFeatureSet();
            boolean flag2 = false;
            for(int k = 0; k < declServices.length; k++)
            {
                String s4 = declServices[k];
                if(s4.equals("startup"))
                {
                    flag2 = true;
                    continue;
                }
                if(!registry.containsKey(s4))
                    startService(s4, licensefeatureset);
            }

            if(flag2)
                startService("startup", licensefeatureset);
            if(!$assertionsDisabled && !transientServices.isEmpty())
                throw new AssertionError();
            if(!isStarted("cluster"))
                break label0;
            Set set = cluster().getAllNodes();
            int i1 = 0;
            int k1 = set.size();
            HashSet hashset = new HashSet();
            Iterator iterator = set.iterator();
            do
            {
                if(!iterator.hasNext())
                    break;
                ClusterNode clusternode = (ClusterNode)iterator.next();
                if(!hashset.contains(clusternode.getAddress().getHostAddress()))
                {
                    i1 += clusternode.getNumberOfCpus();
                    hashset.add(clusternode.getAddress().getHostAddress());
                }
            } while(true);
            iterator = registry.keySet().iterator();
            String s6;
            LicenseDescriptor licensedescriptor;
            do
            {
                if(!iterator.hasNext())
                    break label0;
                s6 = (String)iterator.next();
                licensedescriptor = licensefeatureset.getDescriptor(s6);
                if(!$assertionsDisabled && licensedescriptor == null)
                    throw new AssertionError();
                if(licensedescriptor.getNodes() < k1)
                    throw new KernelException(L10n.format("KRNL.KRNL.ERR50", s6, new Integer(k1)));
            } while(licensedescriptor.getCpus() >= i1);
            throw new KernelException(L10n.format("KRNL.KRNL.ERR51", s6, new Integer(i1)));
        }
        addManageMBean(microkernelcontext.getMBeanServer());
        startRmiReg();
        bindRmiJmxServer(microkernelcontext.getMBeanServer());
        break MISSING_BLOCK_LABEL_1289;
        Object obj1;
        obj1;
        logStartupErrBanner();
        log.error(L10n.format("KRNL.KRNL.ERR26"), ((Throwable) (obj1)));
        state = 3;
        throw obj1;
        obj1;
        logStartupErrBanner();
        log.error(L10n.format("KRNL.KRNL.ERR26"), ((Throwable) (obj1)));
        try
        {
            state = 4;
            stopAllServices();
            if(!$assertionsDisabled && transientServices != null && !transientServices.isEmpty())
                throw new AssertionError();
            if(!$assertionsDisabled && startedServices != null && !startedServices.isEmpty())
                throw new AssertionError();
            try
            {
                microkernelcontext.onKernelShutdown();
            }
            catch(MicroKernelException microkernelexception) { }
        }
        catch(RuntimeException runtimeexception)
        {
            releaseRefs();
            break MISSING_BLOCK_LABEL_1281;
        }
        catch(KernelException kernelexception)
        {
            releaseRefs();
            break MISSING_BLOCK_LABEL_1281;
        }
        releaseRefs();
        break MISSING_BLOCK_LABEL_1281;
        Exception exception;
        exception;
        releaseRefs();
        throw exception;
        state = 3;
        throw obj1;
        if(!$assertionsDisabled && !startStack.isEmpty())
            throw new AssertionError();
        hook = new Thread("xtier-shutdown-hook") {

            public void run()
            {
                Object obj2 = mutex;
                JVM INSTR monitorenter ;
label0:
                {
                    switch(state)
                    {
                    case 1: // '\001'
                        break label0;

                    case 2: // '\002'
                    case 3: // '\003'
                    case 4: // '\004'
                    case 5: // '\005'
                        return;
                    }
                    if(!$assertionsDisabled)
                        throw new AssertionError("Unknown kernel state: " + state);
                }
                break MISSING_BLOCK_LABEL_107;
                Exception exception3;
                exception3;
                throw exception3;
                try
                {
                    XtierKernel.instance.stop();
                }
                catch(KernelException kernelexception1) { }
                return;
            }

            static final boolean $assertionsDisabled; /* synthetic field */



            {
                super(s);
            }
        }
;
        enableShutdownHook(true);
        state = 1;
        startTime = System.currentTimeMillis();
        log.trace(L10n.format("KRNL.KRNL.TXT2", new Long(System.currentTimeMillis())));
        flag = state != i;
        break MISSING_BLOCK_LABEL_1407;
        Exception exception1;
        exception1;
        flag = state != i;
        throw exception1;
        if(flag)
            notifyKernelListeners();
        break MISSING_BLOCK_LABEL_1428;
        Exception exception2;
        exception2;
        throw exception2;
    }

    private void addManageMBean(MBeanServer mbeanserver)
        throws KernelException
    {
        try
        {
            DynamicXtierMBean dynamicxtiermbean = DynamicMBeanFactory.createDynMBean(new ManageMBeanImpl(this), com.fitechlabs.xtier.kernel.XtierKernelManageMBean.class, "kernel", "true");
            javax.management.ObjectName objectname = dynamicxtiermbean.getName();
            if(!mbeanserver.isRegistered(objectname))
                mbeanserver.registerMBean(dynamicxtiermbean, objectname);
        }
        catch(JMException jmexception)
        {
            throw new KernelException(L10n.format("KRNL.KRNL.ERR60"), jmexception);
        }
    }

    public void addKernelListener(KernelLifeCycleListener kernellifecyclelistener)
    {
        ArgAssert.nullArg(kernellifecyclelistener, "listener");
        synchronized(kernelListeners)
        {
            kernelListeners.add(kernellifecyclelistener);
        }
    }

    public List getAllKernelListeners()
    {
        Set set = kernelListeners;
        JVM INSTR monitorenter ;
        return new ArrayList(kernelListeners);
        Exception exception;
        exception;
        throw exception;
    }

    public boolean removeKernelListener(KernelLifeCycleListener kernellifecyclelistener)
    {
        ArgAssert.nullArg(kernellifecyclelistener, "listener");
        Set set = kernelListeners;
        JVM INSTR monitorenter ;
        return kernelListeners.remove(kernellifecyclelistener);
        Exception exception;
        exception;
        throw exception;
    }

    public boolean removeServiceListener(String s, KernelServiceListener kernelservicelistener)
    {
        ArgAssert.nullArg(s, "name");
        ArgAssert.nullArg(kernelservicelistener, "listener");
        Map map = serviceListeners;
        JVM INSTR monitorenter ;
        List list = (List)serviceListeners.get(s);
        if(list != null)
            return list.remove(kernelservicelistener);
        false;
        map;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public List getAllServiceListeners(String s)
    {
        ArgAssert.nullArg(s, "name");
        Map map = serviceListeners;
        JVM INSTR monitorenter ;
        List list = (List)serviceListeners.get(s);
        return list != null ? new ArrayList(list) : null;
        Exception exception;
        exception;
        throw exception;
    }

    public void addServiceListener(String s, KernelServiceListener kernelservicelistener)
    {
        ArgAssert.nullArg(s, "name");
        ArgAssert.nullArg(kernelservicelistener, "listener");
        synchronized(serviceListeners)
        {
            Object obj = (List)serviceListeners.get(s);
            if(obj == null)
                serviceListeners.put(s, obj = new ArrayList());
            ((List) (obj)).add(kernelservicelistener);
        }
    }

    private void notifyKernelListeners()
    {
        if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            throw new AssertionError();
        int i = getState();
        synchronized(kernelListeners)
        {
            for(Iterator iterator = kernelListeners.iterator(); iterator.hasNext(); ((KernelLifeCycleListener)iterator.next()).stateChanged(i));
        }
    }

    private void notifyServiceListeners(String s, boolean flag)
        throws KernelException
    {
        if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            throw new AssertionError();
        synchronized(serviceListeners)
        {
            List list = (List)serviceListeners.get(s);
            if(list != null)
            {
                int i = list.size();
                for(int j = 0; j < i; j++)
                {
                    KernelServiceListener kernelservicelistener = (KernelServiceListener)list.get(j);
                    try
                    {
                        if(flag)
                            kernelservicelistener.afterStart(s);
                        else
                            kernelservicelistener.beforeStop(s);
                    }
                    catch(KernelServiceException kernelserviceexception)
                    {
                        throw new KernelException(L10n.format("KRNL.KRNL.ERR53", s), kernelserviceexception);
                    }
                }

            }
        }
    }

    private void logStartupErrBanner()
    {
        log.error("");
        log.error("***");
        log.error("*** " + L10n.format("KRNL.KRNL.ERR12.LINE1"));
        log.error("*** " + L10n.format("KRNL.KRNL.ERR12.LINE2"));
        log.error("*** " + L10n.format("KRNL.KRNL.ERR12.LINE3"));
        log.error("***");
        log.error("");
    }

    private void expiredLicenseErrBanner(String s)
    {
        log.error("");
        log.error("***");
        log.error("*** " + L10n.format("KRNL.KRNL.ERR58.LINE1", s));
        log.error("*** " + L10n.format("KRNL.KRNL.ERR58.LINE2"));
        log.error("*** " + L10n.format("KRNL.KRNL.ERR58.LINE3"));
        log.error("***");
        log.error("");
    }

    private void logShutdownErrBanner()
    {
        log.error("");
        log.error("***");
        log.error("*** " + L10n.format("KRNL.KRNL.ERR14.LINE1"));
        log.error("*** " + L10n.format("KRNL.KRNL.ERR14.LINE2"));
        log.error("*** " + L10n.format("KRNL.KRNL.ERR14.LINE3"));
        log.error("***");
        log.error("");
    }

    public void stop()
        throws KernelException
    {
        boolean flag = false;
        Object obj = mutex;
        JVM INSTR monitorenter ;
        int i = state;
        stopRmiReg();
        state;
        JVM INSTR tableswitch 1 5: default 106
    //                   1 103
    //                   2 86
    //                   3 56
    //                   4 70
    //                   5 70;
           goto _L1 _L2 _L3 _L4 _L5 _L5
_L2:
        break; /* Loop/switch isn't completed */
_L4:
        throw new IllegalStateException(L10n.format("KRNL.KRNL.ERR15"));
_L5:
        if(!$assertionsDisabled)
            throw new AssertionError("Invalid kernel state transition.");
        break; /* Loop/switch isn't completed */
_L3:
        flag = state != i;
        return;
_L1:
        if(!$assertionsDisabled)
            throw new AssertionError("Unknown kernel state: " + state);
        try
        {
            state = 4;
            log.trace(L10n.format("KRNL.KRNL.TXT16"));
            try
            {
                try
                {
                    stopAllServices();
                }
                catch(KernelException kernelexception)
                {
                    try
                    {
                        ctx.onKernelShutdown();
                    }
                    catch(MicroKernelException microkernelexception1)
                    {
                        log.error(L10n.format("KRNL.KRNL.ERR1"), microkernelexception1);
                    }
                    throw kernelexception;
                }
                try
                {
                    ctx.onKernelShutdown();
                }
                catch(MicroKernelException microkernelexception)
                {
                    throw new KernelException(L10n.format("KRNL.KRNL.ERR1"), microkernelexception);
                }
                state = 2;
                log.trace(L10n.format("KRNL.KRNL.TXT3", new Long(System.currentTimeMillis())));
            }
            finally
            {
                if(!$assertionsDisabled && !transientServices.isEmpty())
                    throw new AssertionError();
                if(!$assertionsDisabled && !startedServices.isEmpty())
                    throw new AssertionError();
                stopGhostThreads();
            }
        }
        catch(RuntimeException runtimeexception)
        {
            logShutdownErrBanner();
            state = 3;
            throw runtimeexception;
        }
        catch(KernelException kernelexception1)
        {
            logShutdownErrBanner();
            state = 3;
            throw kernelexception1;
        }
        finally
        {
            if(hook != null)
                enableShutdownHook(false);
            releaseRefs();
            startTime = -1L;
        }
        flag = state != i;
        break MISSING_BLOCK_LABEL_442;
        Exception exception2;
        exception2;
        flag = state != i;
        throw exception2;
        if(flag)
            notifyKernelListeners();
        break MISSING_BLOCK_LABEL_463;
        Exception exception3;
        exception3;
        throw exception3;
    }

    private void stopGhostThreads()
    {
        SysThreadGroup systhreadgroup = SysThreadGroup.getDfltGroup();
        int i = systhreadgroup.activeCount();
        if(i > 0)
        {
            Thread athread[] = new Thread[i];
            int j = systhreadgroup.enumerate(athread, true);
            if(j > 0)
            {
                int k = 0;
                int l = 0;
                for(int i1 = 0; i1 < j; i1++)
                    if(!athread[i1].isDaemon())
                        k++;
                    else
                        l++;

                if(l > 0)
                {
                    log.info("Daemon threads detected: " + l);
                    for(int j1 = 0; j1 < j; j1++)
                        if(athread[j1].isDaemon())
                            log.info("Daemon thread: " + athread[j1]);

                }
                if(k > 0)
                {
                    log.error("Unexpected ghost threads detected: " + k);
                    Thread.currentThread().getThreadGroup().list();
                    for(int k1 = 0; k1 < j; k1++)
                        if(!athread[k1].isDaemon())
                            log.error("Ghost system thread: " + athread[k1]);

                }
            }
        }
    }

    private void releaseRefs()
    {
        if(!$assertionsDisabled && !Thread.holdsLock(mutex))
        {
            throw new AssertionError();
        } else
        {
            ctx = null;
            log = null;
            hook = null;
            region = null;
            startStack = null;
            startedServices = null;
            serviceListeners = null;
            registry = null;
            transientServices = null;
            localBind = null;
            return;
        }
    }

    private void testProbe()
        throws Exception
    {
        if(!$assertionsDisabled)
            throw new AssertionError("This method can only be called from junit.");
        synchronized(mutex)
        {
            if(!$assertionsDisabled && state != 1)
                throw new AssertionError();
            if(startedServices != null)
            {
                ArrayList arraylist = new ArrayList(startedServices);
                int i = arraylist.size();
                for(int j = 0; j < 5; j++)
                {
                    for(int k = i - 1; k >= 0; k--)
                        ((ServiceProvider)arraylist.get(k)).stop();

                    for(int l = 0; l < i; l++)
                    {
                        ServiceProvider serviceprovider = (ServiceProvider)arraylist.get(l);
                        ServiceDescriptor servicedescriptor = (ServiceDescriptor)region.getServiceDescriptors().get(serviceprovider.getName());
                        if(!$assertionsDisabled && servicedescriptor == null)
                            throw new AssertionError("Can't find service descriptor: " + serviceprovider.getName());
                        HashMap hashmap = new HashMap();
                        hashmap.put("micro.kernel.context", ctx);
                        hashmap.put("config.path", region.getConfigPath());
                        hashmap.put("feature.set", region.getFeatureSet());
                        hashmap.put("region.name", servicedescriptor.getRegion());
                        hashmap.put("decl.services", declServices);
                        serviceprovider.start(hashmap);
                    }

                }

            }
        }
    }

    private void stopAllServices()
        throws KernelException
    {
        if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            throw new AssertionError();
        boolean flag = true;
        if(startedServices != null)
        {
            for(int i = startedServices.size() - 1; i >= 0; i--)
            {
                boolean flag1 = stopService((ServiceProvider)startedServices.remove(i));
                if(!flag1)
                    flag = false;
            }

        }
        if(!flag)
            throw new KernelException(L10n.format("KRNL.KRNL.ERR59"));
        else
            return;
    }

    private boolean stopService(ServiceProvider serviceprovider)
        throws KernelException
    {
        String s;
        if(!$assertionsDisabled && (!Thread.holdsLock(mutex) || state != 4))
            throw new AssertionError();
        s = serviceprovider.getName();
        int i = serviceprovider.getState();
        switch(i)
        {
        case 3: // '\003'
            if(!$assertionsDisabled)
                throw new AssertionError("Attempt to double stop the service: " + s);
            // fall through

        case 2: // '\002'
            if(!$assertionsDisabled)
                throw new AssertionError("Attempt to stop service in transitional starting state: " + s);
            // fall through

        case 4: // '\004'
            if(!$assertionsDisabled)
                throw new AssertionError("Attempt to stop service in transitional stopping state: " + s);
            // fall through

        default:
            if(!$assertionsDisabled)
                throw new AssertionError("Unknown service state: " + i);
            // fall through

        case 1: // '\001'
            transientServices.addFirst(s);
            break;
        }
        boolean flag;
        notifyServiceListeners(s, false);
        serviceprovider.stop();
        log.trace(L10n.format("KRNL.KRNL.TXT7", s));
        flag = true;
        registry.remove(s);
        transientServices.removeFirst();
        return flag;
        Exception exception;
        exception;
        boolean flag1;
        log.error(L10n.format("KRNL.KRNL.ERR32", s), exception);
        flag1 = false;
        registry.remove(s);
        transientServices.removeFirst();
        return flag1;
        Exception exception1;
        exception1;
        registry.remove(s);
        transientServices.removeFirst();
        throw exception1;
    }

    private void verifyLocalLicense(String s, LicenseFeatureSet licensefeatureset)
        throws KernelException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        if(!$assertionsDisabled && licensefeatureset == null)
            throw new AssertionError();
        LicenseDescriptor licensedescriptor = licensefeatureset.getDescriptor(s);
        if(licensedescriptor == null)
            throw new KernelException(L10n.format("KRNL.KRNL.ERR44", s));
        if(licensedescriptor.getExpDate().before(new Date()))
        {
            expiredLicenseErrBanner(s);
            throw new KernelException(L10n.format("KRNL.KRNL.ERR45", s));
        }
        if(licensedescriptor.getIps() != null)
        {
            InetAddress ainetaddress[] = Utils.getLocalIpAddrs();
            boolean flag = false;
            for(int i = 0; i < ainetaddress.length && !flag; i++)
                flag = licensedescriptor.isIpLicensed(ainetaddress[i]);

            if(!flag)
                throw new KernelException(L10n.format("KRNL.KRNL.ERR46", s));
        }
        if(licensedescriptor.getCpus() < Utils.getNumberOfCpus())
            throw new KernelException(L10n.format("KRNL.KRNL.ERR47", s));
        else
            return;
    }

    private ServiceProvider startService(String s, LicenseFeatureSet licensefeatureset)
        throws KernelException
    {
        if(!$assertionsDisabled && (!Thread.holdsLock(mutex) || state != 5))
            throw new AssertionError();
        if(!$assertionsDisabled && (s == null || licensefeatureset == null))
            throw new AssertionError();
        verifyLocalLicense(s, licensefeatureset);
        if(startStack.search(s) != -1)
            throw new KernelException(L10n.format("KRNL.KRNL.ERR9", s));
        startStack.push(s);
        transientServices.addFirst(s);
        boolean flag = false;
        ServiceProvider serviceprovider1;
        try
        {
            Class class1 = (Class)classes.get(s);
            if(!$assertionsDisabled && class1 == null)
                throw new AssertionError("Class not found for service: " + s);
            ServiceProvider serviceprovider = null;
            try
            {
                serviceprovider = (ServiceProvider)Utils.buildObj(class1);
            }
            catch(UtilsException utilsexception)
            {
                throw new KernelException(L10n.format("KRNL.KRNL.ERR10", s, class1), utilsexception);
            }
            ServiceDescriptor servicedescriptor = (ServiceDescriptor)region.getServiceDescriptors().get(s);
            String s1 = servicedescriptor.getRegion();
            String s2 = servicedescriptor.getConfigPath();
            HashMap hashmap = new HashMap();
            hashmap.put("micro.kernel.context", ctx);
            hashmap.put("feature.set", region.getFeatureSet());
            hashmap.put("decl.services", declServices);
            hashmap.put("local.bind", localBind);
            hashmap.put("config.path", s2 != null ? ((Object) (s2)) : ((Object) (region.getConfigPath())));
            hashmap.put("region.name", s1 != null ? ((Object) (s1)) : ((Object) (region.getName())));
            try
            {
                serviceprovider.start(hashmap);
            }
            catch(ServiceProviderException serviceproviderexception)
            {
                throw new KernelException(L10n.format("KRNL.KRNL.ERR33", s), serviceproviderexception);
            }
            if(!$assertionsDisabled && !serviceprovider.getName().equals(s))
                throw new AssertionError();
            registry.put(s, serviceprovider);
            startedServices.add(serviceprovider);
            log.trace(L10n.format("KRNL.KRNL.TXT5", s));
            flag = true;
            serviceprovider1 = serviceprovider;
        }
        finally
        {
            startStack.pop();
            transientServices.removeFirst();
            if(flag)
                notifyServiceListeners(s, true);
        }
        return serviceprovider1;
    }

    private KernelService lookupService(String s)
    {
        if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            throw new AssertionError();
        switch(state)
        {
        case 3: // '\003'
            throw new IllegalStateException(L10n.format("KRNL.KRNL.ERR16"));

        case 2: // '\002'
            throw new IllegalStateException(L10n.format("KRNL.KRNL.ERR17"));

        default:
            if(!$assertionsDisabled)
                throw new AssertionError("Unknown kernel state: " + state);
            else
                return null;

        case 1: // '\001'
        case 4: // '\004'
        case 5: // '\005'
            break;
        }
        if(transientServices.contains(s))
            throw new IllegalArgumentException(L10n.format("KRNL.KRNL.ERR38", s));
        Object obj = (KernelService)registry.get(s);
        if(obj == null)
        {
            switch(state)
            {
            case 1: // '\001'
                throw new IllegalArgumentException(L10n.format("KRNL.KRNL.ERR20", s));

            case 4: // '\004'
                throw new IllegalArgumentException(L10n.format("KRNL.KRNL.ERR27", s));

            case 2: // '\002'
            case 3: // '\003'
            default:
                if(!$assertionsDisabled)
                    throw new AssertionError("Invalid kernel state: " + state);
                else
                    return null;

            case 5: // '\005'
                break;
            }
            if(s.equals("startup"))
                throw new IllegalArgumentException(L10n.format("KRNL.KRNL.ERR18", s));
            if(region.getServiceDescriptors().get(s) == null)
                throw new IllegalArgumentException(L10n.format("KRNL.KRNL.ERR39", s));
            try
            {
                obj = startService(s, LicenseManager.getActiveFeatureSet());
            }
            catch(KernelException kernelexception)
            {
                IllegalArgumentException illegalargumentexception = new IllegalArgumentException(L10n.format("KRNL.KRNL.ERR11", s));
                illegalargumentexception.initCause(kernelexception);
                throw illegalargumentexception;
            }
        }
        if(!$assertionsDisabled && ((KernelService) (obj)).getState() != 1)
            throw new AssertionError("Service is available but not properly started: " + s);
        else
            return ((KernelService) (obj));
    }

    public KernelService[] getAllServices()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        switch(state)
        {
        case 3: // '\003'
            throw new IllegalStateException(L10n.format("KRNL.KRNL.ERR25"));

        default:
            if(!$assertionsDisabled)
                throw new AssertionError("Unknown kernel state: " + state);
            return null;

        case 1: // '\001'
        case 2: // '\002'
        case 4: // '\004'
        case 5: // '\005'
            break;
        }
        KernelService akernelservice[] = new KernelService[registry.size()];
        (KernelService[])registry.values().toArray(akernelservice);
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    private Map parseKernelRegions(final String final_s, String s)
        throws KernelException
    {
        if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            throw new AssertionError();
        String s1 = "file:///" + Utils.makeValidPath(final_s, s);
        try
        {
            HashMap hashmap = new HashMap();
            XmlUtils.makeSaxParser().parse(s1, new XmlSaxHandler("xtier_kernel.dtd", hashmap) {

                protected void onTagStart(String s2, XmlAttrInterceptor xmlattrinterceptor)
                    throws SAXException
                {
                    if(s2.equals("region") || s2.equals("app-region"))
                    {
                        String s3 = xmlattrinterceptor.getValue("name");
                        if(s2.equals("region"))
                            log.warning(L10n.format("KRNL.KRNL.ERR56", s3));
                        String s5 = xmlattrinterceptor.getValue("feature-set");
                        String s7 = xmlattrinterceptor.getValue("config-path");
                        if(!$assertionsDisabled && (s3 == null || s5 == null || s7 == null))
                            throw new AssertionError();
                        if(regions.containsKey(s3))
                            throw createSaxErr(L10n.format("KRNL.KRNL.ERR28", s3));
                        currRegion = new KernelRegion(s3, s5, s7);
                    } else
                    if(s2.equals("start"))
                    {
                        String s4 = xmlattrinterceptor.getValue("service");
                        String s6 = xmlattrinterceptor.getValue("region");
                        String s8 = xmlattrinterceptor.getValue("config-path");
                        if(!$assertionsDisabled && s4 == null)
                            throw new AssertionError();
                        if(s6 != null && noXmlConfig.contains(s4))
                            throw createSaxErr(L10n.format("KRNL.KRNL.ERR36", s4));
                        ServiceDescriptor servicedescriptor = new ServiceDescriptor(s4, s6, s8);
                        if(currRegion.getServiceDescriptors().containsKey(servicedescriptor.getName()))
                            throw createSaxErr(L10n.format("KRNL.KRNL.ERR8", currRegion, s4));
                        currRegion.addServiceDescriptor(servicedescriptor);
                    }
                }

                protected void onTagEnd(String s2)
                {
                    if(s2.equals("region") || s2.equals("app-region"))
                    {
                        regions.put(currRegion.getName(), currRegion);
                        currRegion = null;
                    }
                }

                private KernelRegion currRegion;
                private Set noXmlConfig;
                static final boolean $assertionsDisabled; /* synthetic field */



                throws SAXException
            {
                super(final_s, s1);
                currRegion = null;
                noXmlConfig = new HashSet();
                noXmlConfig.add("os");
                noXmlConfig.add("fs");
                noXmlConfig.add("info");
                noXmlConfig.add("jmx");
            }
            }
);
            return hashmap;
        }
        catch(SAXParseException saxparseexception)
        {
            throw new KernelException(L10n.format("KRNL.KRNL.ERR6", saxparseexception.toString()), saxparseexception);
        }
        catch(ParserConfigurationException parserconfigurationexception)
        {
            throw new KernelException(L10n.format("KRNL.KRNL.ERR5"), parserconfigurationexception);
        }
        catch(SAXException saxexception)
        {
            throw new KernelException(L10n.format("KRNL.KRNL.ERR4", s1), saxexception);
        }
        catch(IOException ioexception)
        {
            throw new KernelException(L10n.format("KRNL.KRNL.ERR3", s1), ioexception);
        }
    }

    private void stopRmiReg()
        throws KernelException
    {
        if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            throw new AssertionError();
        if(rmiReg != null)
            try
            {
                UnicastRemoteObject.unexportObject(rmiReg, true);
                rmiReg = null;
            }
            catch(RemoteException remoteexception)
            {
                throw new KernelException(L10n.format("KRNL.KRNL.ERR65", remoteexception.getMessage()), remoteexception);
            }
    }

    private void startRmiReg()
        throws KernelException
    {
        if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            throw new AssertionError();
        if(!$assertionsDisabled && rmiReg != null)
            throw new AssertionError();
        try
        {
            if(localBind == null)
                rmiReg = LocateRegistry.createRegistry(rmiRegPort);
            else
                rmiReg = LocateRegistry.createRegistry(rmiRegPort, new RMIClientSocketFactory() {

                    public Socket createSocket(String s, int i)
                        throws IOException
                    {
                        Socket socket = new Socket(s, i);
                        socket.bind(localBind);
                        return socket;
                    }


                throws IOException
            {
                super();
            }
                }
, new RMIServerSocketFactory() {

                    public ServerSocket createServerSocket(int i)
                        throws IOException
                    {
                        ServerSocket serversocket = new ServerSocket();
                        serversocket.bind(new InetSocketAddress(localBind.getAddress(), i));
                        return serversocket;
                    }


                throws IOException
            {
                super();
            }
                }
);
        }
        catch(RemoteException remoteexception)
        {
            throw new KernelException(L10n.format("KRNL.KRNL.ERR64", remoteexception.getMessage()), remoteexception);
        }
    }

    private void bindRmiJmxServer(MBeanServer mbeanserver)
        throws KernelException
    {
        if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            throw new AssertionError();
        if(!$assertionsDisabled && rmiReg == null)
            throw new AssertionError();
        try
        {
            rmiReg.rebind("xtier.jmx.rmi.agent", new JmxRmiServer(mbeanserver));
        }
        catch(RemoteException remoteexception)
        {
            throw new KernelException(L10n.format("KRNL.KRNL.ERR61", remoteexception.getMessage()), remoteexception);
        }
    }

    public int getState()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return state;
        Exception exception;
        exception;
        throw exception;
    }

    public TxService tx()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return (TxService)lookupService("tx");
        Exception exception;
        exception;
        throw exception;
    }

    public StartupService startup()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return (StartupService)lookupService("startup");
        Exception exception;
        exception;
        throw exception;
    }

    public InfoService info()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return (InfoService)lookupService("info");
        Exception exception;
        exception;
        throw exception;
    }

    public SecurityService security()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return (SecurityService)lookupService("security");
        Exception exception;
        exception;
        throw exception;
    }

    public MarshalService marshal()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return (MarshalService)lookupService("marshal");
        Exception exception;
        exception;
        throw exception;
    }

    public LogService log()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return (LogService)lookupService("log");
        Exception exception;
        exception;
        throw exception;
    }

    public JobsService jobs()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return (JobsService)lookupService("jobs");
        Exception exception;
        exception;
        throw exception;
    }

    public JmxService jmx()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return (JmxService)lookupService("jmx");
        Exception exception;
        exception;
        throw exception;
    }

    public I18nService i18n()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return (I18nService)lookupService("i18n");
        Exception exception;
        exception;
        throw exception;
    }

    public EmailService email()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return (EmailService)lookupService("email");
        Exception exception;
        exception;
        throw exception;
    }

    public ConfigService config()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return (ConfigService)lookupService("config");
        Exception exception;
        exception;
        throw exception;
    }

    public UidGenService uidgen()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return (UidGenService)lookupService("uidgen");
        Exception exception;
        exception;
        throw exception;
    }

    public IocService ioc()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return (IocService)lookupService("ioc");
        Exception exception;
        exception;
        throw exception;
    }

    public ObjectPoolService objpool()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return (ObjectPoolService)lookupService("objpool");
        Exception exception;
        exception;
        throw exception;
    }

    public WorkflowService workflow()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return (WorkflowService)lookupService("workflow");
        Exception exception;
        exception;
        throw exception;
    }

    public OsService os()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return (OsService)lookupService("os");
        Exception exception;
        exception;
        throw exception;
    }

    public FsService fs()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return (FsService)lookupService("fs");
        Exception exception;
        exception;
        throw exception;
    }

    public DbService db()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return (DbService)lookupService("db");
        Exception exception;
        exception;
        throw exception;
    }

    public GridService grid()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return (GridService)lookupService("grid");
        Exception exception;
        exception;
        throw exception;
    }

    public JndiService jndi()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return (JndiService)lookupService("jndi");
        Exception exception;
        exception;
        throw exception;
    }

    public JmsService jms()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return (JmsService)lookupService("jms");
        Exception exception;
        exception;
        throw exception;
    }

    public ClusterService cluster()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return (ClusterService)lookupService("cluster");
        Exception exception;
        exception;
        throw exception;
    }

    public CacheService cache()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return (CacheService)lookupService("cache");
        Exception exception;
        exception;
        throw exception;
    }

    public long getUpTime()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return startTime != -1L ? System.currentTimeMillis() - startTime : -1L;
        Exception exception;
        exception;
        throw exception;
    }

    public int getHostEnvironment()
    {
        return ctx.getHostEnvId();
    }

    public void enableShutdownHook(boolean flag)
    {
        if(!$assertionsDisabled && hook == null)
            throw new AssertionError();
        synchronized(mutex)
        {
            if(!flag)
                try
                {
                    if(Runtime.getRuntime().removeShutdownHook(hook));
                }
                catch(IllegalStateException illegalstateexception) { }
            else
                try
                {
                    Runtime.getRuntime().addShutdownHook(hook);
                }
                catch(IllegalStateException illegalstateexception1) { }
                catch(IllegalArgumentException illegalargumentexception) { }
        }
    }

    public static final int KERNEL_STARTED = 1;
    public static final int KERNEL_STOPPED = 2;
    public static final int KERNEL_FATAL = 3;
    private static final int KERNEL_STOPPING = 4;
    private static final int KERNEL_STARTING = 5;
    private static XtierKernel instance = null;
    private static final Object instanceMutex = new Object();
    private final Object mutex = new Object();
    private static final String DEPENDENCIES[][] = {
        {
            "os"
        }, {
            "cache", "log", "cluster", "marshal", "objpool"
        }, {
            "cluster", "log", "marshal", "objpool"
        }, {
            "config", "log"
        }, {
            "email", "log"
        }, {
            "fs", "os"
        }, {
            "grid", "log", "cluster", "marshal", "objpool"
        }, {
            "i18n", "log"
        }, {
            "info"
        }, {
            "ioc", "log"
        }, {
            "jmx"
        }, {
            "jndi", "log"
        }, {
            "jobs", "log", "objpool"
        }, {
            "log"
        }, {
            "marshal", "log"
        }, {
            "objpool", "log"
        }, {
            "security", "log"
        }, {
            "startup"
        }, {
            "tx", "log", "objpool", "cluster"
        }, {
            "uidgen", "log"
        }, {
            "workflow", "log"
        }, {
            "db", "log", "jndi"
        }, {
            "jms", "log"
        }
    };
    private int state;
    private MicroKernelContext ctx;
    private MicroKernelLogger log;
    private KernelRegion region;
    private Thread hook;
    private Stack startStack;
    private ArrayList startedServices;
    private LinkedList transientServices;
    private Map registry;
    private final String KERNEL_XML = "config/xtier_kernel.xml";
    private Map classes;
    private Set kernelListeners;
    private Map serviceListeners;
    private String declServices[];
    private InetSocketAddress localBind;
    private Registry rmiReg;
    private int rmiRegPort;
    private long startTime;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(XtierKernel.class).desiredAssertionStatus();
    }





}
