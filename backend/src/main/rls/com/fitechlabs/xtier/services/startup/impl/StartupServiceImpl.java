// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.startup.impl;

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
import com.fitechlabs.xtier.services.startup.StartupService;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.Utils;
import com.fitechlabs.xtier.utils.xml.XmlUtils;
import java.io.IOException;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import org.xml.sax.SAXException;

public class StartupServiceImpl extends ServiceProviderAdapter
    implements StartupService
{
    private static class XmlStartable
    {

        String getName()
        {
            return name;
        }

        void setIoc(IocDescriptor iocdescriptor)
        {
            ioc = iocdescriptor;
        }

        IocDescriptor getIoc()
        {
            return ioc;
        }

        private String name;
        private IocDescriptor ioc;

        XmlStartable(String s)
        {
            name = null;
            ioc = null;
            name = s;
        }
    }

    private static class XmlStartupRegion
    {

        void addStartable(XmlStartable xmlstartable)
        {
            xmlStartables.put(xmlstartable.getName(), xmlstartable);
        }

        boolean containsStartable(String s)
        {
            return xmlStartables.containsKey(s);
        }

        Map getStartables()
        {
            return xmlStartables;
        }

        String getName()
        {
            return name;
        }

        private Map xmlStartables;
        private String name;

        XmlStartupRegion(String s)
        {
            xmlStartables = new HashMap();
            name = s;
        }
    }


    public StartupServiceImpl()
    {
        regions = new HashMap();
        startables = new HashMap();
    }

    public String getName()
    {
        return "startup";
    }

    public Map getStartables()
    {
        return new HashMap(startables);
    }

    public Object getStartable(String s)
    {
        ArgAssert.nullArg(s, "name");
        return startables.get(s);
    }

    private void parseXmlConfig(String s, Set set)
        throws SAXException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        if(set.contains(s))
        {
            logger.warning(L10n.format("SRVC.STARTUP.WRN1", s));
            return;
        }
        set.add(s);
        String s1 = "file:///" + Utils.makeValidPath(Utils.makeValidPath(getXtierRoot(), getConfigPath()), s);
        try
        {
            XmlUtils.makeSaxParser().parse(s1, new XmlSaxHandler("xtier_startup.dtd", set) {

                protected void onTagStart(String s2, XmlAttrInterceptor xmlattrinterceptor)
                    throws SAXException
                {
                    if(s2.equals("region"))
                    {
                        String s3 = xmlattrinterceptor.getValue("name");
                        if(regions.containsKey(s3))
                            throw createSaxErr(L10n.format("SRVC.STARTUP.IMPL.ERR7", s3));
                        region = new XmlStartupRegion(s3);
                    } else
                    if(s2.equals("include"))
                    {
                        String s4 = xmlattrinterceptor.getValue("path");
                        parseXmlConfig(s4, includes);
                    } else
                    if(s2.equals("startable"))
                    {
                        String s5 = xmlattrinterceptor.getValue("name");
                        if(region.containsStartable(s5))
                            throw createSaxErr(L10n.format("SRVC.STARTUP.IMPL.ERR1", region.getName(), s5));
                        startable = new XmlStartable(s5);
                    }
                }

                protected void onIocDescriptor(IocDescriptor iocdescriptor)
                {
                    if(startable != null)
                        startable.setIoc(iocdescriptor);
                }

                protected void onTagEnd(String s2)
                {
                    if(s2.equals("region"))
                    {
                        regions.put(region.getName(), region);
                        region = null;
                    } else
                    if(s2.equals("startable"))
                    {
                        region.addStartable(startable);
                        startable = null;
                    }
                }

                private XmlStartable startable;
                private XmlStartupRegion region;


                throws SAXException
            {
                super(final_s, s1);
                startable = null;
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
            throw new SAXException(L10n.format("SRVC.STARTUP.IMPL.ERR2", s1), ioexception);
        }
    }

    protected void onStart()
        throws ServiceProviderException
    {
        logger = XtierKernel.getInstance().log().getLogger("startup");
        try
        {
            parseXmlConfig("xtier_startup.xml", new HashSet());
        }
        catch(SAXException saxexception)
        {
            throw new ServiceProviderException(L10n.format("SRVC.STARTUP.IMPL.ERR3"), saxexception);
        }
        String s = getRegionName();
        XmlStartupRegion xmlstartupregion = (XmlStartupRegion)regions.get(s);
        if(xmlstartupregion == null)
            throw new ServiceProviderException(L10n.format("SRVC.STARTUP.IMPL.ERR4", s));
        XmlStartable xmlstartable;
        Object obj;
        for(Iterator iterator = xmlstartupregion.getStartables().values().iterator(); iterator.hasNext(); startables.put(xmlstartable.getName(), obj))
        {
            xmlstartable = (XmlStartable)iterator.next();
            obj = null;
            try
            {
                obj = xmlstartable.getIoc().createNewObj();
            }
            catch(IocDescriptorException iocdescriptorexception)
            {
                throw new ServiceProviderException(L10n.format("SRVC.STARTUP.IMPL.ERR5", iocdescriptorexception.getMessage()), iocdescriptorexception);
            }
        }

    }

    protected void onStop()
        throws ServiceProviderException
    {
        startables.clear();
        regions.clear();
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

    private Map regions;
    private Map startables;
    private Logger logger;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(StartupServiceImpl.class).desiredAssertionStatus();
    }


}
