// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.ioc.impl;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.kernel.ioc.IocDescriptor;
import com.fitechlabs.xtier.kernel.spi.ServiceProviderAdapter;
import com.fitechlabs.xtier.kernel.spi.ServiceProviderException;
import com.fitechlabs.xtier.kernel.xml.XmlAttrInterceptor;
import com.fitechlabs.xtier.kernel.xml.XmlSaxHandler;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.ioc.*;
import com.fitechlabs.xtier.services.log.LogService;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.Utils;
import com.fitechlabs.xtier.utils.xml.XmlUtils;
import java.io.IOException;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import org.xml.sax.SAXException;

// Referenced classes of package com.fitechlabs.xtier.services.ioc.impl:
//            IocObjectProxyImpl

public class IocServiceImpl extends ServiceProviderAdapter
    implements IocService
{
    private class XmlRegion
    {

        String getName()
        {
            return name;
        }

        void addIoc(IocDescriptor iocdescriptor)
        {
            if(!$assertionsDisabled && iocdescriptor == null)
                throw new AssertionError();
            if(!$assertionsDisabled && iocdescriptor.getUid() == null)
            {
                throw new AssertionError();
            } else
            {
                IocObjectProxyImpl iocobjectproxyimpl = new IocObjectProxyImpl(iocdescriptor);
                regionIocs.put(iocobjectproxyimpl.getUid(), iocobjectproxyimpl);
                return;
            }
        }

        Map getIocs()
        {
            return regionIocs;
        }

        private Map regionIocs;
        private String name;
        static final boolean $assertionsDisabled; /* synthetic field */


        XmlRegion(String s)
        {
            super();
            regionIocs = new HashMap();
            name = s;
        }
    }


    public IocServiceImpl()
    {
        log = null;
        iocs = new HashMap();
    }

    protected void onStart()
        throws ServiceProviderException
    {
        log = XtierKernel.getInstance().log().getLogger("ioc");
        HashMap hashmap = new HashMap();
        try
        {
            parseXmlConfig("xtier_ioc.xml", hashmap, new HashSet());
        }
        catch(SAXException saxexception)
        {
            throw new ServiceProviderException(L10n.format("SRVC.IOC.ERR1"), saxexception);
        }
        XmlRegion xmlregion = (XmlRegion)hashmap.get(getRegionName());
        if(xmlregion == null)
        {
            throw new ServiceProviderException(L10n.format("SRVC.IOC.ERR2", getRegionName()));
        } else
        {
            iocs = xmlregion.getIocs();
            return;
        }
    }

    protected void onStop()
    {
        iocs.clear();
        log = null;
    }

    private void parseXmlConfig(String s, Map map, Set set)
        throws SAXException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        if(set.contains(s))
        {
            log.warning(L10n.format("SRVC.IOC.WRN1", s));
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
                    if(s2.equals("region"))
                    {
                        String s3 = xmlattrinterceptor.getValue("name");
                        if(regions.containsKey(s3))
                            throw createSaxErr(L10n.format("SRVC.IOC.ERR3", s3));
                        region = new XmlRegion(s3);
                    } else
                    if(s2.equals("include"))
                    {
                        String s4 = xmlattrinterceptor.getValue("path");
                        parseXmlConfig(s4, regions, includes);
                    }
                }

                protected void onIocDescriptor(IocDescriptor iocdescriptor)
                {
                    String s2 = iocdescriptor.getUid();
                    if(s2 != null)
                        region.addIoc(iocdescriptor);
                }

                protected void onTagEnd(String s2)
                {
                    if(s2.equals("region"))
                    {
                        regions.put(region.getName(), region);
                        region = null;
                    }
                }

                private XmlRegion region;


                throws SAXException
            {
                super(final_s, final_s1);
                region = null;
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
            throw new SAXException(L10n.format("SRVC.IOC.ERR4", s1), ioexception);
        }
    }

    public Object makeIocObject(String s)
        throws IocServiceException
    {
        ArgAssert.nullArg(s, "uid");
        IocObjectProxy iocobjectproxy = (IocObjectProxy)iocs.get(s);
        return iocobjectproxy != null ? iocobjectproxy.makeIocObject() : null;
    }

    public Object makeIocObject(String s, Object obj)
        throws IocServiceException
    {
        ArgAssert.nullArg(s, "uid");
        ArgAssert.nullArg(obj, "key");
        IocObjectProxy iocobjectproxy = (IocObjectProxy)iocs.get(s);
        return iocobjectproxy != null ? iocobjectproxy.makeIocObject(obj) : null;
    }

    public String getName()
    {
        return "ioc";
    }

    public Map getIocProxies()
    {
        return Collections.unmodifiableMap(iocs);
    }

    public IocObjectProxy getIocProxy(String s)
    {
        ArgAssert.nullArg(s, "uid");
        return (IocObjectProxy)iocs.get(s);
    }

    static Class _mthclass$(String s)
    {
        try
        {
            return Class.forName(s);
        }
        catch(ClassNotFoundException classnotfoundexception)
        {
            throw (new NoClassDefFoundError()).initCause(classnotfoundexception);
        }
    }

    private Logger log;
    private Map iocs;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(IocServiceImpl.class).desiredAssertionStatus();
    }

}
