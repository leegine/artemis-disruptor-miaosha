// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.cache.impl;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.kernel.ioc.IocDescriptor;
import com.fitechlabs.xtier.kernel.ioc.IocDescriptorException;
import com.fitechlabs.xtier.kernel.spi.ServiceProviderAdapter;
import com.fitechlabs.xtier.kernel.spi.ServiceProviderException;
import com.fitechlabs.xtier.kernel.xml.XmlAttrInterceptor;
import com.fitechlabs.xtier.kernel.xml.XmlSaxHandler;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.cache.*;
import com.fitechlabs.xtier.services.cache.impl.app.CacheAppCommManager;
import com.fitechlabs.xtier.services.cache.impl.app.CacheAppConfig;
import com.fitechlabs.xtier.services.cache.impl.app.CacheAppRegistry;
import com.fitechlabs.xtier.services.cache.impl.app.CacheAppTxManager;
import com.fitechlabs.xtier.services.cache.jcache.JCache;
import com.fitechlabs.xtier.services.log.LogService;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.services.objpool.ObjectPoolService;
import com.fitechlabs.xtier.utils.Utils;
import com.fitechlabs.xtier.utils.xml.XmlUtils;
import java.io.IOException;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import org.xml.sax.SAXException;

// Referenced classes of package com.fitechlabs.xtier.services.cache.impl:
//            CacheStartupManager, CacheConfig, JCacheImpl, CacheImpl,
//            CacheDataManager, CacheEventManager, CacheChannel

public class CacheServiceImpl extends ServiceProviderAdapter
    implements CacheService
{
    private class XmlIocConfig
    {

        IocDescriptor getAttrsResolver()
        {
            return attrsResolver;
        }

        void setAttrsResolver(IocDescriptor iocdescriptor)
        {
            attrsResolver = iocdescriptor;
        }

        IocDescriptor getExpPolicy()
        {
            return expPolicy;
        }

        void setExpPolicy(IocDescriptor iocdescriptor)
        {
            if(!$assertionsDisabled && iocdescriptor == null)
            {
                throw new AssertionError();
            } else
            {
                expPolicy = iocdescriptor;
                return;
            }
        }

        IocDescriptor getLoader()
        {
            return loader;
        }

        void setLoader(IocDescriptor iocdescriptor)
        {
            loader = iocdescriptor;
        }

        IocDescriptor getSpooler()
        {
            return spooler;
        }

        void setSpooler(IocDescriptor iocdescriptor)
        {
            spooler = iocdescriptor;
        }

        IocDescriptor getStore()
        {
            return store;
        }

        void setStore(IocDescriptor iocdescriptor)
        {
            store = iocdescriptor;
        }

        IocDescriptor getTopology()
        {
            return topology;
        }

        void setTopology(IocDescriptor iocdescriptor)
        {
            topology = iocdescriptor;
        }

        private IocDescriptor loader;
        private IocDescriptor store;
        private IocDescriptor topology;
        private IocDescriptor attrsResolver;
        private IocDescriptor spooler;
        private IocDescriptor expPolicy;
        static final boolean $assertionsDisabled; /* synthetic field */


        private XmlIocConfig()
        {
            super();
        }

    }

    private class XmlRegion
    {

        String getName()
        {
            return name;
        }

        int getPort()
        {
            return port;
        }

        void setPort(int i)
        {
            port = i;
        }

        void addConfig(String s, CacheConfig cacheconfig)
        {
            if(!$assertionsDisabled && s == null)
                throw new AssertionError();
            if(!$assertionsDisabled && cacheconfig == null)
                throw new AssertionError();
            if(!$assertionsDisabled && configs.containsKey(s))
            {
                throw new AssertionError();
            } else
            {
                configs.put(s, cacheconfig);
                return;
            }
        }

        CacheConfig getConfig(String s)
        {
            if(!$assertionsDisabled && s == null)
                throw new AssertionError();
            else
                return (CacheConfig)configs.get(s);
        }

        boolean hasConfig(String s)
        {
            if(!$assertionsDisabled && s == null)
                throw new AssertionError();
            else
                return configs.containsKey(s);
        }

        Map getConfigs()
        {
            return configs;
        }

        void addIocConfig(String s, XmlIocConfig xmliocconfig)
        {
            if(!$assertionsDisabled && s == null)
                throw new AssertionError();
            if(!$assertionsDisabled && xmliocconfig == null)
                throw new AssertionError();
            if(!$assertionsDisabled && iocConfigs.containsKey(s))
            {
                throw new AssertionError();
            } else
            {
                iocConfigs.put(s, xmliocconfig);
                return;
            }
        }

        XmlIocConfig getIocConfig(String s)
        {
            if(!$assertionsDisabled && s == null)
                throw new AssertionError();
            else
                return (XmlIocConfig)iocConfigs.get(s);
        }

        private String name;
        private int port;
        private Map configs;
        private Map iocConfigs;
        static final boolean $assertionsDisabled; /* synthetic field */


        XmlRegion(String s)
        {
            super();
            configs = new HashMap();
            iocConfigs = new HashMap();
            if(!$assertionsDisabled && s == null)
            {
                throw new AssertionError();
            } else
            {
                name = s;
                return;
            }
        }
    }


    public CacheServiceImpl()
    {
    }

    public String getName()
    {
        return "cache";
    }

    public Cache getCache()
    {
        return dfltCache;
    }

    public Cache getCache(String s)
    {
        return ((Cache) (s != null ? (Cache)caches.get(s) : dfltCache));
    }

    public JCache getJCache()
    {
        return dfltJCache;
    }

    public JCache getJCache(String s)
    {
        return s != null ? (JCache)jcaches.get(s) : dfltJCache;
    }

    protected void onStart()
        throws ServiceProviderException
    {
        log = XtierKernel.getInstance().log().getLogger("xtier-cache");
        HashMap hashmap = new HashMap();
        try
        {
            parseXmlConfig("xtier_cache.xml", hashmap, new HashSet());
        }
        catch(SAXException saxexception)
        {
            throw new ServiceProviderException(L10n.format("SRVC.CACHE.ERR44"), saxexception);
        }
        region = (XmlRegion)hashmap.get(getRegionName());
        if(region == null)
        {
            throw new ServiceProviderException(L10n.format("SRVC.CACHE.ERR4", getRegionName()));
        } else
        {
            startAll();
            return;
        }
    }

    private Object createIoc(IocDescriptor iocdescriptor, Class class1)
        throws ServiceProviderException
    {
        try
        {
            return iocdescriptor != null ? iocdescriptor.createNewObj(class1) : null;
        }
        catch(IocDescriptorException iocdescriptorexception)
        {
            throw new ServiceProviderException(L10n.format("SRVC.CACHE.ERR6"), iocdescriptorexception);
        }
    }

    private void startAll()
        throws ServiceProviderException
    {
        Map map = region.getConfigs();
        try
        {
            caches = new HashMap();
            jcaches = new HashMap();
            startupMgr = new CacheStartupManager(region.getPort(), caches);
            CacheImpl cacheimpl;
            for(Iterator iterator = map.values().iterator(); iterator.hasNext(); jcaches.put(cacheimpl.getName(), new JCacheImpl(cacheimpl)))
            {
                cacheimpl = startCache((CacheConfig)iterator.next());
                caches.put(cacheimpl.getName(), cacheimpl);
            }

            dfltCache = (CacheImpl)caches.get("xtier-dflt-cache");
            dfltJCache = dfltCache != null ? ((JCache) (new JCacheImpl(dfltCache))) : null;
            startupMgr.start();
        }
        catch(CacheException cacheexception)
        {
            throw new ServiceProviderException(cacheexception.getMessage(), cacheexception);
        }
    }

    private CacheImpl startCache(CacheConfig cacheconfig)
        throws CacheException, ServiceProviderException
    {
        if(!$assertionsDisabled && cacheconfig.getCacheType() != 1)
            throw new AssertionError();
        CacheAppRegistry cacheappregistry = new CacheAppRegistry();
        cacheappregistry.setConfig(cacheconfig);
        cacheappregistry.setStartupMgr(startupMgr);
        CacheImpl cacheimpl = new CacheImpl(cacheappregistry);
        cacheappregistry.setCache(cacheimpl);
        cacheappregistry.setDataMgr(new CacheDataManager(cacheappregistry));
        cacheappregistry.setCommMgr(new CacheAppCommManager(cacheappregistry));
        cacheappregistry.setTxMgr(new CacheAppTxManager(cacheappregistry));
        cacheappregistry.setEventMgr(new CacheEventManager(cacheappregistry));
        cacheimpl.start();
        XmlIocConfig xmliocconfig = region.getIocConfig(cacheconfig.getCacheName());
        cacheconfig.setLoader((CacheLoader)createIoc(xmliocconfig.getLoader(), com.fitechlabs.xtier.services.cache.CacheLoader.class));
        cacheconfig.setStore((CacheStore)createIoc(xmliocconfig.getStore(), com.fitechlabs.xtier.services.cache.CacheStore.class));
        if(cacheconfig.getLoader() != null && cacheconfig.getStore() != null)
            throw new ServiceProviderException(L10n.format("SRVC.CACHE.ERR5"));
        if(cacheconfig.getStore() != null)
            cacheconfig.setLoader(cacheconfig.getStore());
        final CacheExpirationPolicy policy = (CacheExpirationPolicy)createIoc(xmliocconfig.getExpPolicy(), com.fitechlabs.xtier.services.cache.CacheExpirationPolicy.class);
        if(policy != null)
        {
            cacheconfig.setExpPolicy(policy);
            cacheimpl.addEventListener(new CacheListener() {

                public void onEntryEvent(Cache cache, int i, CacheEntry cacheentry)
                {
                    policy.onEntryEvent(cache, i, cacheentry);
                }


            {
                super();
            }
            }
);
        }
        cacheconfig.setTopology((CacheTopology)createIoc(xmliocconfig.getTopology(), com.fitechlabs.xtier.services.cache.CacheTopology.class));
        cacheconfig.setSpooler((CacheSpooler)createIoc(xmliocconfig.getSpooler(), com.fitechlabs.xtier.services.cache.CacheSpooler.class));
        cacheconfig.setAttrsResolver((CacheKeyAttrsResolver)createIoc(xmliocconfig.getAttrsResolver(), com.fitechlabs.xtier.services.cache.CacheKeyAttrsResolver.class));
        return cacheimpl;
    }

    protected void onStop()
        throws ServiceProviderException
    {
        Object obj = null;
        try
        {
            startupMgr.stop();
        }
        catch(CacheException cacheexception)
        {
            log.error(L10n.format("SRVC.CACHE.ERR48"), cacheexception);
            obj = cacheexception;
        }
        Iterator iterator = caches.values().iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            CacheImpl cacheimpl = (CacheImpl)iterator.next();
            try
            {
                cacheimpl.stop();
            }
            catch(CacheException cacheexception1)
            {
                log.error(L10n.format("SRVC.CACHE.ERR49", cacheimpl.getName()), cacheexception1);
                if(obj == null)
                    obj = cacheexception1;
            }
        } while(true);
        if(obj != null)
        {
            throw new ServiceProviderException(L10n.format("SRVC.CACHE.ERR50"), ((Throwable) (obj)));
        } else
        {
            caches = null;
            return;
        }
    }

    private void parseXmlConfig(String s, Map map, Set set)
        throws SAXException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        if(set.contains(s))
        {
            log.warning(L10n.format("SRVC.CACHE.WRN1", s));
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
                        parseXmlConfig(xmlattrinterceptor.getValue("path"), regions, includes);
                    else
                    if(s2.equals("region"))
                    {
                        String s3 = xmlattrinterceptor.getValue("name");
                        if(regions.containsKey(s3))
                            throw createSaxErr(L10n.format("SRVC.CACHE.ERR7", s3));
                        xmlRegion = new XmlRegion(s3);
                        regions.put(s3, xmlRegion);
                        config = new CacheAppConfig();
                    } else
                    if(s2.equals("startup"))
                        xmlRegion.setPort(parseIpPort(xmlattrinterceptor.getValue("port")));
                    else
                    if(s2.equals("app-cache"))
                    {
                        iocConfig = new XmlIocConfig();
                        String s4 = xmlattrinterceptor.getValue("name");
                        if(s4 == null)
                            s4 = "xtier-dflt-cache";
                        config.setCacheName(s4);
                        int k1 = parseInt(xmlattrinterceptor.getValue("init-size"));
                        if(k1 <= 0)
                            createSaxErr(L10n.format("SRVC.CACHE.ERR8", "init-size", new Integer(k1)));
                        config.setInitSize(k1);
                        xmlRegion.addConfig(s4, config);
                        xmlRegion.addIocConfig(s4, iocConfig);
                        isApp = true;
                    } else
                    if(s2.equals("app-tx"))
                    {
                        long l = parseLong(xmlattrinterceptor.getValue("default-tx-timeout"));
                        if(l <= 0L)
                            throw createSaxErr(L10n.format("SRVC.CACHE.ERR8", "dflt-tx-timeout", new Long(l)));
                        if(isApp)
                            ((CacheAppConfig)config).setDfltTxTimeout(l);
                    } else
                    if(s2.equals("phase-one"))
                    {
                        int i = parseIpPort(xmlattrinterceptor.getValue("reply-port"));
                        if(isApp)
                            ((CacheAppConfig)config).setPhaseOneReplyPort(i);
                    } else
                    if(s2.equals("deadlock-detection"))
                    {
                        int j = parseIpPort(xmlattrinterceptor.getValue("reply-port"));
                        if(isApp)
                        {
                            ((CacheAppConfig)config).setDeadlockDetection(true);
                            ((CacheAppConfig)config).setDeadlockReplyPort(j);
                            long l1 = parseLong(xmlattrinterceptor.getValue("timeout"));
                            if(l1 <= 0L)
                                throw createSaxErr(L10n.format("SRVC.CACHE.ERR8", "timeout", new Long(l1)));
                            ((CacheAppConfig)config).setTxDeadlockTimeout(l1);
                        }
                    } else
                    if(s2.equals("channel"))
                    {
                        int k = parseIpPort(xmlattrinterceptor.getValue("port"));
                        int i2 = parseInt(xmlattrinterceptor.getValue("min"));
                        int k2 = parseInt(xmlattrinterceptor.getValue("max"));
                        int i3 = parseInt(xmlattrinterceptor.getValue("attempts"));
                        long l3 = parseLong(xmlattrinterceptor.getValue("timeout"));
                        if(i2 < 0)
                            throw createSaxErr(L10n.format("SRVC.CACHE.ERR8", "min", new Integer(i2)));
                        if(k2 < 0)
                            throw createSaxErr(L10n.format("SRVC.CACHE.ERR8", "max", new Integer(k2)));
                        if(l3 <= 0L)
                            throw createSaxErr(L10n.format("SRVC.CACHE.ERR8", "timeout", new Long(l3)));
                        if(k2 < i2)
                            throw createSaxErr(L10n.format("SRVC.CACHE.ERR9", new Integer(i2), new Integer(k2)));
                        if(i3 < 1)
                            throw createSaxErr(L10n.format("SRVC.CACHE.ERR10"));
                        if(isApp)
                            ((CacheAppConfig)config).addChannel(new CacheChannel(k, k2, i2, i3, l3));
                    } else
                    if(s2.equals("phase-two"))
                    {
                        int i1 = parseIpPort(xmlattrinterceptor.getValue("port"));
                        if(isApp)
                            ((CacheAppConfig)config).setPhaseTwoPort(i1);
                    } else
                    if(s2.equals("dgc"))
                    {
                        int j1 = parseIpPort(xmlattrinterceptor.getValue("dgc-port"));
                        int j2 = parseIpPort(xmlattrinterceptor.getValue("reply-port"));
                        long l2 = parseLong(xmlattrinterceptor.getValue("timeout"));
                        long l4 = parseLong(xmlattrinterceptor.getValue("tx-age"));
                        if(l2 <= 0L)
                            throw createSaxErr(L10n.format("SRVC.CACHE.ERR8", "timeout", new Long(l2)));
                        if(l4 <= 0L)
                            throw createSaxErr(L10n.format("SRVC.CACHE.ERR8", "tx-age", new Long(l4)));
                        if(isApp)
                        {
                            CacheAppConfig cacheappconfig = (CacheAppConfig)config;
                            cacheappconfig.setDgcPort(j1);
                            cacheappconfig.setDgcReplyPort(j2);
                            cacheappconfig.setDgcTimeout(l2);
                            cacheappconfig.setDgcTxAge(l4);
                        }
                    } else
                    if(s2.equals("cache-loader"))
                    {
                        if(!$assertionsDisabled && isLoader)
                            throw new AssertionError();
                        isLoader = true;
                    } else
                    if(s2.equals("cache-store"))
                    {
                        if(!$assertionsDisabled && isStore)
                            throw new AssertionError();
                        isStore = true;
                    } else
                    if(s2.equals("cache-spooler"))
                    {
                        if(!$assertionsDisabled && isSpooler)
                            throw new AssertionError();
                        isSpooler = true;
                    } else
                    if(s2.equals("cache-topology"))
                    {
                        if(!$assertionsDisabled && isTopology)
                            throw new AssertionError();
                        isTopology = true;
                    } else
                    if(s2.equals("cache-key-attrs-resolver"))
                    {
                        if(!$assertionsDisabled && isAttrsResolver)
                            throw new AssertionError();
                        isAttrsResolver = true;
                    } else
                    if(s2.equals("cache-expiration-policy"))
                    {
                        if(!$assertionsDisabled && isExpPolicy)
                            throw new AssertionError();
                        isExpPolicy = true;
                    }
                }

                protected void onIocDescriptor(IocDescriptor iocdescriptor)
                    throws SAXException
                {
                    if(isLoader)
                    {
                        if(iocConfig.getLoader() != null)
                            throw createSaxErr(L10n.format("SRVC.CACHE.ERR11", "cache-loader"));
                        iocConfig.setLoader(iocdescriptor);
                    } else
                    if(isStore)
                    {
                        if(iocConfig.getStore() != null)
                            throw createSaxErr(L10n.format("SRVC.CACHE.ERR11", "cache-store"));
                        iocConfig.setStore(iocdescriptor);
                    } else
                    if(isSpooler)
                    {
                        if(iocConfig.getSpooler() != null)
                            throw createSaxErr(L10n.format("SRVC.CACHE.ERR11", "cache-spooler"));
                        iocConfig.setSpooler(iocdescriptor);
                    } else
                    if(isTopology)
                    {
                        if(iocConfig.getTopology() != null)
                            throw createSaxErr(L10n.format("SRVC.CACHE.ERR11", "cache-topology"));
                        iocConfig.setTopology(iocdescriptor);
                    } else
                    if(isAttrsResolver)
                    {
                        if(iocConfig.getAttrsResolver() != null)
                            throw createSaxErr(L10n.format("SRVC.CACHE.ERR11", "cache-key-attrs-resolver"));
                        iocConfig.setAttrsResolver(iocdescriptor);
                    } else
                    if(isExpPolicy)
                    {
                        if(iocConfig.getExpPolicy() != null)
                            throw createSaxErr(L10n.format("SRVC.CACHE.ERR11", "cache-expiration-policy"));
                        iocConfig.setExpPolicy(iocdescriptor);
                    }
                }

                protected void onTagEnd(String s2)
                    throws SAXException
                {
                    if(s2.equals("region"))
                        xmlRegion = null;
                    else
                    if(s2.equals("app-cache"))
                    {
                        if(!$assertionsDisabled && !isApp)
                            throw new AssertionError();
                        isApp = false;
                        CacheAppConfig cacheappconfig = (CacheAppConfig)config;
                        List list = cacheappconfig.getChannels();
                        Collections.sort(list);
                        int i = 0;
                        for(int j = list.size() - 1; i < j; i++)
                        {
                            CacheChannel cachechannel = (CacheChannel)list.get(i);
                            CacheChannel cachechannel1 = (CacheChannel)list.get(i + 1);
                            if(!$assertionsDisabled && cachechannel1.getMin() < cachechannel.getMin())
                                throw new AssertionError();
                            if(cachechannel.getMin() == cachechannel1.getMin() || cachechannel1.getMin() != cachechannel.getMax() + 1)
                                throw createSaxErr(L10n.format("SRVC.CACHE.ERR12"));
                        }

                    } else
                    if(s2.equals("thread-pool-name"))
                    {
                        String s3 = getPcdata().trim();
                        config.setThreadPoolName(s3);
                        if(XtierKernel.getInstance().objpool().getThreadPool(s3) == null)
                            throw createSaxErr(L10n.format("SRVC.CACHE.ERR59", s3));
                    } else
                    if(s2.equals("cache-loader"))
                    {
                        if(!$assertionsDisabled && !isLoader)
                            throw new AssertionError();
                        isLoader = false;
                    } else
                    if(s2.equals("cache-store"))
                    {
                        if(!$assertionsDisabled && !isStore)
                            throw new AssertionError();
                        isStore = false;
                    } else
                    if(s2.equals("cache-spooler"))
                    {
                        if(!$assertionsDisabled && !isSpooler)
                            throw new AssertionError();
                        isSpooler = false;
                    } else
                    if(s2.equals("cache-topology"))
                    {
                        if(!$assertionsDisabled && !isTopology)
                            throw new AssertionError();
                        isTopology = false;
                    } else
                    if(s2.equals("cache-key-attrs-resolver"))
                    {
                        if(!$assertionsDisabled && !isAttrsResolver)
                            throw new AssertionError();
                        isAttrsResolver = false;
                    } else
                    if(s2.equals("cache-expiration-policy"))
                    {
                        if(!$assertionsDisabled && !isExpPolicy)
                            throw new AssertionError();
                        isExpPolicy = false;
                    }
                }

                private XmlRegion xmlRegion;
                private boolean isApp;
                private CacheConfig config;
                private XmlIocConfig iocConfig;
                private boolean isLoader;
                private boolean isStore;
                private boolean isSpooler;
                private boolean isAttrsResolver;
                private boolean isTopology;
                private boolean isExpPolicy;
                static final boolean $assertionsDisabled; /* synthetic field */



                throws SAXException
            {
                super(final_s, final_s1);
                xmlRegion = null;
                isApp = false;
                isLoader = false;
                isStore = false;
                isSpooler = false;
                isAttrsResolver = false;
                isTopology = false;
                isExpPolicy = false;
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
            throw new SAXException(L10n.format("SRVC.CACHE.ERR62", s1), ioexception);
        }
    }

    private Logger log;
    private CacheImpl dfltCache;
    private JCache dfltJCache;
    private Map caches;
    private Map jcaches;
    private XmlRegion region;
    private CacheStartupManager startupMgr;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(CacheServiceImpl.class).desiredAssertionStatus();
    }

}
