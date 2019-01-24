// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.log.impl;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.kernel.ioc.IocDescriptor;
import com.fitechlabs.xtier.kernel.ioc.IocDescriptorException;
import com.fitechlabs.xtier.kernel.mbean.DynamicMBeanFactory;
import com.fitechlabs.xtier.kernel.mbean.DynamicXtierMBean;
import com.fitechlabs.xtier.kernel.spi.ServiceProviderAdapter;
import com.fitechlabs.xtier.kernel.spi.ServiceProviderException;
import com.fitechlabs.xtier.kernel.xml.XmlAttrInterceptor;
import com.fitechlabs.xtier.kernel.xml.XmlSaxHandler;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.microkernel.MicroKernelContext;
import com.fitechlabs.xtier.microkernel.MicroKernelLogger;
import com.fitechlabs.xtier.services.jmx.JmxService;
import com.fitechlabs.xtier.services.log.*;
import com.fitechlabs.xtier.services.log.sinks.microkernel.LogMicroKernelSink;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.Utils;
import com.fitechlabs.xtier.utils.xml.XmlUtils;
import java.io.IOException;
import java.util.*;
import java.util.regex.*;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

// Referenced classes of package com.fitechlabs.xtier.services.log.impl:
//            LogSinkManager, LoggerImpl, LogRecordImpl, LogUtils

public class LogServiceImpl extends ServiceProviderAdapter
    implements LogService
{
    private class XmlLogSink
    {

        void setLevelFrom(int i)
        {
            mask = LogUtils.convertToMask(i);
        }

        void addMaskLevel(int i)
        {
            mask |= i;
        }

        int getMask()
        {
            return mask != 0 ? mask : 126;
        }

        IocDescriptor getIoc()
        {
            return ioc;
        }

        void setIoc(IocDescriptor iocdescriptor)
        {
            ioc = iocdescriptor;
        }

        public String toString()
        {
            if(!$assertionsDisabled)
                throw new AssertionError();
            else
                return "Log XML sink [ioc=" + ioc + ", mask=" + getMask() + ']';
        }

        private IocDescriptor ioc;
        private int mask;
        static final boolean $assertionsDisabled; /* synthetic field */


        private XmlLogSink()
        {
            super();
            mask = 0;
        }

    }

    private static class XmlLogRegion
    {

        void addSink(XmlLogSink xmllogsink)
        {
            xmlSinks.add(xmllogsink);
        }

        List getSinks()
        {
            return xmlSinks;
        }

        String getName()
        {
            return name;
        }

        public String toString()
        {
            if(!$assertionsDisabled)
                throw new AssertionError();
            else
                return "Log XML region: " + name;
        }

        private List xmlSinks;
        private String name;
        static final boolean $assertionsDisabled; /* synthetic field */


        XmlLogRegion(String s)
        {
            xmlSinks = new ArrayList();
            name = s;
        }
    }

    private class XmlLogCategoryMask
    {

        void setPattern(String s)
            throws PatternSyntaxException
        {
            pattern = Pattern.compile(s);
        }

        void addMaskLevel(int i)
        {
            mask |= i;
        }

        int getMask()
        {
            return mask;
        }

        Pattern getPattern()
        {
            return pattern;
        }

        public String toString()
        {
            if(!$assertionsDisabled)
                throw new AssertionError();
            else
                return "XML log category mask [mask=" + getMask() + ", pattern=" + getPattern() + ']';
        }

        private int mask;
        private Pattern pattern;
        static final boolean $assertionsDisabled; /* synthetic field */


        private XmlLogCategoryMask()
        {
            super();
            mask = 0;
        }

    }


    public LogServiceImpl()
    {
        sinks = new HashMap();
        mbeans = new HashMap();
        regions = new HashMap();
        global = null;
        ctgrMasks = new ArrayList();
    }

    private void parseXmlConfig(String s, Set set)
        throws SAXException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        if(set.contains(s))
        {
            getMicroKernelContext().getLogger().warning(L10n.format("SRVC.LOG.IMPL.WRN1", s));
            return;
        }
        set.add(s);
        String s1 = "file:///" + Utils.makeValidPath(Utils.makeValidPath(getXtierRoot(), getConfigPath()), s);
        try
        {
            XmlUtils.makeSaxParser().parse(s1, new XmlSaxHandler("xtier_log.dtd", set) {

                protected void onTagStart(String s2, XmlAttrInterceptor xmlattrinterceptor)
                    throws SAXException
                {
                    if(s2.equals("region"))
                    {
                        String s3 = xmlattrinterceptor.getValue("name");
                        if(regions.containsKey(s3))
                            throw createSaxErr(L10n.format("SRVC.LOG.IMPL.ERR15", s3));
                        region = new XmlLogRegion(s3);
                    } else
                    if(s2.equals("category"))
                        ctgrMask = new XmlLogCategoryMask();
                    else
                    if(s2.equals("sink"))
                        sink = new XmlLogSink();
                    else
                    if(s2.equals("include"))
                        parseXmlConfig(xmlattrinterceptor.getValue("path"), includes);
                    else
                    if(s2.equals("level-from"))
                        sink.setLevelFrom(getLevel(xmlattrinterceptor.getValue("value")));
                    else
                    if(s2.equals("level"))
                    {
                        int i = getLevel(xmlattrinterceptor.getValue("value"));
                        if(sink != null)
                            sink.addMaskLevel(i);
                        else
                        if(ctgrMask != null)
                            ctgrMask.addMaskLevel(i);
                        else
                        if(!$assertionsDisabled)
                            throw new AssertionError("Invalid parser state.");
                    }
                }

                private int getLevel(String s2)
                    throws SAXParseException
                {
                    if(s2.equals("debug"))
                        return 2;
                    if(s2.equals("error"))
                        return 64;
                    if(s2.equals("warning"))
                        return 32;
                    if(s2.equals("trace"))
                        return 4;
                    if(s2.equals("log"))
                        return 16;
                    if(s2.equals("info"))
                        return 8;
                    else
                        throw createSaxErr(L10n.format("SRVC.LOG.IMPL.ERR14", s2));
                }

                protected void onIocDescriptor(IocDescriptor iocdescriptor)
                {
                    if(sink != null)
                        sink.setIoc(iocdescriptor);
                }

                public void onTagEnd(String s2)
                    throws SAXException
                {
                    if(s2.equals("region"))
                    {
                        regions.put(region.getName(), region);
                        region = null;
                    } else
                    if(s2.equals("regexp"))
                    {
                        String s3 = getPcdata();
                        try
                        {
                            ctgrMask.setPattern(s3);
                        }
                        catch(PatternSyntaxException patternsyntaxexception)
                        {
                            throw createSaxErr(L10n.format("SRVC.LOG.IMPL.ERR18", s3), patternsyntaxexception);
                        }
                    } else
                    if(s2.equals("category"))
                    {
                        ctgrMasks.add(ctgrMask);
                        ctgrMask = null;
                    } else
                    if(s2.equals("sink"))
                    {
                        region.addSink(sink);
                        sink = null;
                    }
                }

                private XmlLogSink sink;
                private XmlLogRegion region;
                private XmlLogCategoryMask ctgrMask;
                static final boolean $assertionsDisabled; /* synthetic field */



                throws SAXException
            {
                super(final_s, s1);
                sink = null;
                region = null;
                ctgrMask = null;
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
            throw new SAXException(L10n.format("SRVC.LOG.IMPL.ERR16", s1), ioexception);
        }
    }

    protected void onStart()
        throws ServiceProviderException
    {
        try
        {
            parseXmlConfig("xtier_log.xml", new HashSet());
        }
        catch(SAXException saxexception)
        {
            throw new ServiceProviderException(L10n.format("SRVC.LOG.IMPL.ERR3"), saxexception);
        }
        XmlLogRegion xmllogregion = (XmlLogRegion)regions.get(getRegionName());
        if(xmllogregion == null)
            throw new ServiceProviderException(L10n.format("SRVC.LOG.IMPL.ERR4", getRegionName()));
        if(xmllogregion.getSinks().isEmpty())
            getMicroKernelContext().getLogger().warning(L10n.format("SRVC.LOG.IMPL.ERR22"));
        MBeanServer mbeanserver = XtierKernel.getInstance().jmx().getMBeanServer();
        LogSink logsink;
        String s;
        for(Iterator iterator = xmllogregion.getSinks().iterator(); iterator.hasNext(); sinks.put(s, logsink))
        {
            XmlLogSink xmllogsink = (XmlLogSink)iterator.next();
            logsink = null;
            try
            {
                logsink = (LogSink)xmllogsink.getIoc().createNewObj(com.fitechlabs.xtier.services.log.LogSink.class);
            }
            catch(IocDescriptorException iocdescriptorexception)
            {
                throw new ServiceProviderException(L10n.format("SRVC.LOG.IMPL.ERR19"), iocdescriptorexception);
            }
            logsink.setLevelMask(xmllogsink.getMask());
            s = logsink.getName();
            if((logsink instanceof LogMicroKernelSink))
                ((LogMicroKernelSink)logsink).setLogger(getMicroKernelContext().getLogger());
            try
            {
                DynamicXtierMBean dynamicxtiermbean = DynamicMBeanFactory.createDynMBean(new LogSinkManager(logsink), com.fitechlabs.xtier.services.log.impl.LogSinkManagerMBean.class, "sink", s);
                mbeanserver.registerMBean(dynamicxtiermbean, dynamicxtiermbean.getName());
                mbeans.put(s, dynamicxtiermbean);
            }
            catch(JMException jmexception)
            {
                throw new ServiceProviderException(L10n.format("SRVC.LOG.IMPL.ERR20"), jmexception);
            }
        }

        global = new LoggerImpl(this, null);
    }

    protected void onStop()
        throws ServiceProviderException
    {
        MBeanServer mbeanserver = XtierKernel.getInstance().jmx().getMBeanServer();
        for(Iterator iterator = mbeans.values().iterator(); iterator.hasNext();)
        {
            DynamicXtierMBean dynamicxtiermbean = (DynamicXtierMBean)iterator.next();
            try
            {
                mbeanserver.unregisterMBean(dynamicxtiermbean.getName());
            }
            catch(JMException jmexception) { }
        }

        for(Iterator iterator1 = sinks.values().iterator(); iterator1.hasNext(); ((LogSink)iterator1.next()).close());
        ((LoggerImpl)global).close();
        sinks.clear();
        regions.clear();
        mbeans.clear();
    }

    public Logger getLogger()
    {
        return global;
    }

    public Logger getLogger(String s)
    {
        ArgAssert.nullArg(s, "ctgr");
        return global.getLogger(s);
    }

    void say(Object obj, int i, String s, Throwable throwable, StackTraceElement stacktraceelement)
    {
        LogRecordImpl logrecordimpl = null;
        Iterator iterator = sinks.values().iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            LogSink logsink = (LogSink)iterator.next();
            if((i & logsink.getLevelMask()) != 0)
            {
                if(logrecordimpl == null)
                    logrecordimpl = new LogRecordImpl(obj != null ? obj.toString() : null, i, s, throwable, stacktraceelement);
                logsink.add(logrecordimpl);
            }
        } while(true);
    }

    int getCategoryMask(String s)
    {
        int i = ctgrMasks.size();
        for(int j = 0; j < i; j++)
        {
            XmlLogCategoryMask xmllogcategorymask = (XmlLogCategoryMask)ctgrMasks.get(j);
            if(xmllogcategorymask.getPattern().matcher(s).matches())
                return xmllogcategorymask.getMask();
        }

        return -1;
    }

    public String getName()
    {
        return "log";
    }

    private Map sinks;
    private Map mbeans;
    private Map regions;
    private Logger global;
    private List ctgrMasks;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(LogServiceImpl.class).desiredAssertionStatus();
    }



}
