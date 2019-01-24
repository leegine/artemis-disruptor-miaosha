// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.objpool.impl;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.kernel.ioc.IocDescriptor;
import com.fitechlabs.xtier.kernel.ioc.IocDescriptorException;
import com.fitechlabs.xtier.kernel.spi.ServiceProviderAdapter;
import com.fitechlabs.xtier.kernel.spi.ServiceProviderException;
import com.fitechlabs.xtier.kernel.xml.XmlAttrInterceptor;
import com.fitechlabs.xtier.kernel.xml.XmlSaxHandler;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.log.LogService;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.services.objpool.*;
import com.fitechlabs.xtier.services.objpool.policies.ObjectPoolGrowPolicy;
import com.fitechlabs.xtier.services.objpool.policies.ThreadPoolGrowPolicy;
import com.fitechlabs.xtier.services.objpool.threads.ThreadPool;
import com.fitechlabs.xtier.services.objpool.threads.ThreadPoolResizePolicy;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.Utils;
import com.fitechlabs.xtier.utils.xml.XmlUtils;
import java.io.IOException;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import org.xml.sax.SAXException;

// Referenced classes of package com.fitechlabs.xtier.services.objpool.impl:
//            ThreadPoolImpl, ObjectPoolImpl

public class ObjectPoolServiceImpl extends ServiceProviderAdapter
    implements ObjectPoolService
{
    private static class XmlObjPool
    {

        void setFactory(IocDescriptor iocdescriptor)
        {
            factory = iocdescriptor;
        }

        void setResizePolicy(IocDescriptor iocdescriptor)
        {
            resizePolicy = iocdescriptor;
        }

        IocDescriptor getFactory()
        {
            return factory;
        }

        IocDescriptor getResizePolicy()
        {
            return resizePolicy;
        }

        boolean isLazy()
        {
            return lazy;
        }

        String getName()
        {
            return name;
        }

        int getSize()
        {
            return size;
        }

        public boolean isThreadPool()
        {
            return isThreadPool;
        }

        public int getPriority()
        {
            return priority;
        }

        private String name;
        private int size;
        private boolean lazy;
        private int priority;
        private boolean isThreadPool;
        private IocDescriptor factory;
        private IocDescriptor resizePolicy;

        XmlObjPool(String s, int i, boolean flag)
        {
            name = null;
            factory = null;
            resizePolicy = null;
            name = s;
            size = i;
            lazy = flag;
            isThreadPool = false;
        }

        XmlObjPool(String s, int i, boolean flag, int j)
        {
            name = null;
            factory = null;
            resizePolicy = null;
            name = s;
            size = i;
            lazy = flag;
            priority = j;
            isThreadPool = true;
        }
    }

    private static class XmlObjPoolRegion
    {

        void addObjPool(XmlObjPool xmlobjpool)
        {
            xmlPools.put(xmlobjpool.getName(), xmlobjpool);
        }

        boolean containsPool(String s)
        {
            return xmlPools.containsKey(s);
        }

        Map getPool()
        {
            return xmlPools;
        }

        String getName()
        {
            return name;
        }

        private Map xmlPools;
        private String name;

        XmlObjPoolRegion(String s)
        {
            xmlPools = new HashMap();
            name = s;
        }
    }


    public ObjectPoolServiceImpl()
    {
        pools = new HashMap();
        regions = new HashMap();
    }

    private void parseXmlConfig(String s, Set set)
        throws SAXException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        if(set.contains(s))
        {
            log.warning(L10n.format("SRVC.OBJPOOL.WRN1", s));
            return;
        }
        set.add(s);
        String s1 = "file:///" + Utils.makeValidPath(Utils.makeValidPath(getXtierRoot(), getConfigPath()), s);
        try
        {
            XmlUtils.makeSaxParser().parse(s1, new XmlSaxHandler("xtier_objpool.dtd", set) {

                protected void onTagStart(String s2, XmlAttrInterceptor xmlattrinterceptor)
                    throws SAXException
                {
                    if(s2.equals("region"))
                        region = new XmlObjPoolRegion(xmlattrinterceptor.getValue("name"));
                    else
                    if(s2.equals("pool"))
                    {
                        String s3 = xmlattrinterceptor.getValue("name");
                        if(region.containsPool(s3))
                            throw createSaxErr(L10n.format("SRVC.OBJPOOL.ERR2", region.getName(), s3));
                        int i = parseInt(xmlattrinterceptor.getValue("size"));
                        boolean flag = parseBoolean(xmlattrinterceptor.getValue("lazy"));
                        pool = new XmlObjPool(s3, i, flag);
                    } else
                    if(s2.equals("thread-pool"))
                    {
                        String s4 = xmlattrinterceptor.getValue("name");
                        if(region.containsPool(s4))
                            throw createSaxErr(L10n.format("SRVC.OBJPOOL.ERR2", region.getName(), s4));
                        int j = parseInt(xmlattrinterceptor.getValue("size"));
                        boolean flag1 = parseBoolean(xmlattrinterceptor.getValue("lazy"));
                        int k = parseInt(xmlattrinterceptor.getValue("priority"));
                        pool = new XmlObjPool(s4, j, flag1, k);
                    } else
                    if(s2.equals("include"))
                    {
                        String s5 = xmlattrinterceptor.getValue("path");
                        parseXmlConfig(s5, includes);
                    } else
                    if(s2.equals("factory"))
                        isFactory = true;
                    else
                    if(s2.equals("resize-policy"))
                    {
                        isResizePolicy = true;
                        if(!$assertionsDisabled && pool == null)
                            throw new AssertionError();
                    }
                }

                protected void onIocDescriptor(IocDescriptor iocdescriptor)
                {
                    if(isFactory)
                        pool.setFactory(iocdescriptor);
                    else
                    if(isResizePolicy)
                        pool.setResizePolicy(iocdescriptor);
                }

                protected void onTagEnd(String s2)
                {
                    if(s2.equals("region"))
                    {
                        regions.put(region.getName(), region);
                        region = null;
                    } else
                    if(s2.equals("pool") || s2.equals("thread-pool"))
                    {
                        region.addObjPool(pool);
                        pool = null;
                    } else
                    if(s2.equals("factory"))
                        isFactory = false;
                    else
                    if(s2.equals("resize-policy"))
                        isResizePolicy = false;
                }

                private XmlObjPool pool;
                private XmlObjPoolRegion region;
                boolean isFactory;
                boolean isResizePolicy;
                static final boolean $assertionsDisabled; /* synthetic field */



                throws SAXException
            {
                super(final_s, s1);
                pool = null;
                region = null;
                isFactory = false;
                isResizePolicy = false;
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
            throw new SAXException(L10n.format("SRVC.OBJPOOL.ERR3", s1), ioexception);
        }
    }

    public ObjectPool createPool(String s, int i, ObjectPoolFactory objectpoolfactory, boolean flag, ObjectPoolResizePolicy objectpoolresizepolicy)
        throws ObjectPoolException
    {
        ArgAssert.nullArg(s, "name");
        ArgAssert.illegalArg(i > 0, "size");
        ArgAssert.nullArg(objectpoolfactory, "factory");
        ArgAssert.nullArg(objectpoolresizepolicy, "resizePolicy");
        Map map = pools;
        JVM INSTR monitorenter ;
        if(pools.containsKey(s))
            throw new ObjectPoolException(L10n.format("SRVC.OBJPOOL.ERR4", s));
        return createAddPool(s, i, objectpoolfactory, flag, objectpoolresizepolicy);
        Exception exception;
        exception;
        throw exception;
    }

    public ObjectPool createPool(String s, ObjectPoolFactory objectpoolfactory)
        throws ObjectPoolException
    {
        return createPool(s, 10, objectpoolfactory, true, ((ObjectPoolResizePolicy) (new ObjectPoolGrowPolicy())));
    }

    public ThreadPool createThreadPool(String s)
        throws ObjectPoolException
    {
        return createThreadPool(s, 10, true, 5, ((ThreadPoolResizePolicy) (new ThreadPoolGrowPolicy())));
    }

    public boolean deleteThreadPool(String s)
    {
        ArgAssert.nullArg(s, "name");
        Map map = pools;
        JVM INSTR monitorenter ;
        Object obj;
        obj = pools.get(s);
        if(obj == null)
            return false;
        ArgAssert.illegalArg((obj instanceof ThreadPoolImpl), s);
        pools.remove(s);
        ThreadPoolImpl threadpoolimpl = (ThreadPoolImpl)obj;
        threadpoolimpl.stop();
        true;
        map;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public boolean deleteWaitThreadPool(String s)
    {
        ArgAssert.nullArg(s, "name");
        Map map = pools;
        JVM INSTR monitorenter ;
        Object obj;
        obj = pools.get(s);
        if(obj == null)
            return false;
        ArgAssert.illegalArg((obj instanceof ThreadPoolImpl), s);
        pools.remove(s);
        ThreadPoolImpl threadpoolimpl = (ThreadPoolImpl)obj;
        threadpoolimpl.stopWait();
        true;
        map;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public ThreadPool getThreadPool(String s)
    {
        ArgAssert.nullArg(s, "name");
        Map map = pools;
        JVM INSTR monitorenter ;
        Object obj;
        obj = pools.get(s);
        if(obj == null)
            return null;
        ArgAssert.illegalArg((obj instanceof ThreadPool), "name");
        (ThreadPool)obj;
        map;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    private ObjectPool createAddPool(String s, int i, ObjectPoolFactory objectpoolfactory, boolean flag, ObjectPoolResizePolicy objectpoolresizepolicy)
        throws ObjectPoolException
    {
        ObjectPoolImpl objectpoolimpl = new ObjectPoolImpl(s, i, objectpoolfactory, flag, objectpoolresizepolicy);
        pools.put(s, objectpoolimpl);
        return objectpoolimpl;
    }

    private ThreadPool createAddThreadPool(String s, int i, boolean flag, int j, ThreadPoolResizePolicy threadpoolresizepolicy)
        throws ObjectPoolException
    {
        if(pools.containsKey(s))
        {
            throw new ObjectPoolException(L10n.format("SRVC.OBJPOOL.ERR4", s));
        } else
        {
            ThreadPoolImpl threadpoolimpl = new ThreadPoolImpl(s, i, flag, j, threadpoolresizepolicy);
            pools.put(s, threadpoolimpl);
            return threadpoolimpl;
        }
    }

    public ObjectPool getPool(String s)
    {
        ArgAssert.nullArg(s, "name");
        Map map = pools;
        JVM INSTR monitorenter ;
        return (ObjectPool)pools.get(s);
        Exception exception;
        exception;
        throw exception;
    }

    public boolean deletePool(String s)
        throws ObjectPoolException
    {
        ArgAssert.nullArg(s, "name");
        Map map = pools;
        JVM INSTR monitorenter ;
        ObjectPoolImpl objectpoolimpl;
        objectpoolimpl = (ObjectPoolImpl)pools.remove(s);
        if(objectpoolimpl == null)
            return false;
        objectpoolimpl.discard();
        true;
        map;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public String getName()
    {
        return "objpool";
    }

    protected void onStart()
        throws ServiceProviderException
    {
        log = XtierKernel.getInstance().log().getLogger("objpool");
        try
        {
            parseXmlConfig("xtier_objpool.xml", new HashSet());
        }
        catch(SAXException saxexception)
        {
            throw new ServiceProviderException(L10n.format("SRVC.OBJPOOL.ERR5"), saxexception);
        }
        XmlObjPoolRegion xmlobjpoolregion = (XmlObjPoolRegion)regions.get(getRegionName());
        if(xmlobjpoolregion == null)
            throw new ServiceProviderException(L10n.format("SRVC.OBJPOOL.ERR6", getRegionName()));
        for(Iterator iterator = xmlobjpoolregion.getPool().values().iterator(); iterator.hasNext();)
        {
            XmlObjPool xmlobjpool = (XmlObjPool)iterator.next();
            if(!xmlobjpool.isThreadPool())
            {
                ObjectPoolFactory objectpoolfactory = null;
                try
                {
                    objectpoolfactory = (ObjectPoolFactory)xmlobjpool.getFactory().createNewObj(com.fitechlabs.xtier.services.objpool.ObjectPoolFactory.class);
                }
                catch(IocDescriptorException iocdescriptorexception)
                {
                    throw new ServiceProviderException(L10n.format("SRVC.OBJPOOL.ERR7", iocdescriptorexception.getMessage()), iocdescriptorexception);
                }
                Object obj1 = null;
                try
                {
                    IocDescriptor iocdescriptor1 = xmlobjpool.getResizePolicy();
                    obj1 = iocdescriptor1 != null ? ((Object) ((ObjectPoolResizePolicy)xmlobjpool.getResizePolicy().createNewObj(com.fitechlabs.xtier.services.objpool.ObjectPoolResizePolicy.class))) : ((Object) (new ObjectPoolGrowPolicy()));
                }
                catch(IocDescriptorException iocdescriptorexception2)
                {
                    throw new ServiceProviderException(L10n.format("SRVC.OBJPOOL.ERR13", iocdescriptorexception2.getMessage()), iocdescriptorexception2);
                }
                ObjectPool objectpool;
                try
                {
                    objectpool = createAddPool(xmlobjpool.getName(), xmlobjpool.getSize(), objectpoolfactory, xmlobjpool.isLazy(), ((ObjectPoolResizePolicy) (obj1)));
                }
                catch(ObjectPoolException objectpoolexception1)
                {
                    throw new ServiceProviderException(L10n.format("SRVC.OBJPOOL.ERR8", xmlobjpool.getName()), objectpoolexception1);
                }
            } else
            {
                Object obj = null;
                try
                {
                    IocDescriptor iocdescriptor = xmlobjpool.getResizePolicy();
                    obj = iocdescriptor != null ? ((Object) ((ThreadPoolResizePolicy)xmlobjpool.getResizePolicy().createNewObj(com.fitechlabs.xtier.services.objpool.threads.ThreadPoolResizePolicy.class))) : ((Object) (new ThreadPoolGrowPolicy()));
                }
                catch(IocDescriptorException iocdescriptorexception1)
                {
                    throw new ServiceProviderException(L10n.format("SRVC.OBJPOOL.ERR14", iocdescriptorexception1.getMessage()), iocdescriptorexception1);
                }
                ThreadPool threadpool;
                try
                {
                    threadpool = createAddThreadPool(xmlobjpool.getName(), xmlobjpool.getSize(), xmlobjpool.isLazy(), xmlobjpool.getPriority(), ((ThreadPoolResizePolicy) (obj)));
                }
                catch(ObjectPoolException objectpoolexception)
                {
                    throw new ServiceProviderException(L10n.format("SRVC.OBJPOOL.ERR8", xmlobjpool.getName()), objectpoolexception);
                }
            }
        }

    }

    private void clearPools()
        throws ObjectPoolException
    {
        ObjectPoolException objectpoolexception = null;
        Iterator iterator = pools.values().iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            Object obj = iterator.next();
            if((obj instanceof ObjectPool))
            {
                try
                {
                    ((ObjectPoolImpl)obj).discard();
                }
                catch(ObjectPoolException objectpoolexception1)
                {
                    if(objectpoolexception == null)
                        objectpoolexception = objectpoolexception1;
                }
            } else
            {
                if(!$assertionsDisabled && !(obj instanceof ThreadPool))
                    throw new AssertionError();
                ((ThreadPoolImpl)obj).stop();
            }
        } while(true);
        pools.clear();
        if(objectpoolexception != null)
            throw objectpoolexception;
        else
            return;
    }

    protected void onStop()
        throws ServiceProviderException
    {
        try
        {
            clearPools();
        }
        catch(ObjectPoolException objectpoolexception)
        {
            throw new ServiceProviderException(L10n.format("SRVC.OBJPOOL.ERR9", objectpoolexception.getMessage()), objectpoolexception);
        }
    }

    public ThreadPool createThreadPool(String s, int i, boolean flag, int j, ThreadPoolResizePolicy threadpoolresizepolicy)
        throws ObjectPoolException
    {
        ArgAssert.nullArg(s, "name");
        ArgAssert.illegalArg(i > 0, "size");
        ArgAssert.illegalArg(j >= 1 && j <= 10, "priority");
        return createAddThreadPool(s, i, flag, j, threadpoolresizepolicy);
    }

    private Map pools;
    private Map regions;
    private Logger log;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(ObjectPoolServiceImpl.class).desiredAssertionStatus();
    }


}
