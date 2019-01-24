// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.jms.impl;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.kernel.ioc.IocDescriptor;
import com.fitechlabs.xtier.kernel.ioc.IocDescriptorException;
import com.fitechlabs.xtier.kernel.spi.ServiceProviderAdapter;
import com.fitechlabs.xtier.kernel.spi.ServiceProviderException;
import com.fitechlabs.xtier.kernel.xml.XmlAttrInterceptor;
import com.fitechlabs.xtier.kernel.xml.XmlSaxHandler;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.jms.*;
import com.fitechlabs.xtier.services.jms.converters.JmsDefaultConverter;
import com.fitechlabs.xtier.services.jndi.JndiService;
import com.fitechlabs.xtier.services.log.LogService;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.Utils;
import com.fitechlabs.xtier.utils.xml.XmlUtils;
import java.io.IOException;
import java.util.*;
import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import org.xml.sax.SAXException;

// Referenced classes of package com.fitechlabs.xtier.services.jms.impl:
//            JmsMessageDefaults, JmsObjectReceiverImpl, JmsObjectSenderImpl, JmsSmartConnectionImpl,
//            JmsSmartXaConnectionImpl, JmsUtils

public class JmsServiceImpl extends ServiceProviderAdapter
    implements JmsService
{
    private class XaConnFactory extends AbstractConnFactory
    {

        XAConnectionFactory getFactory()
            throws JMSException
        {
            if(!$assertionsDisabled && !Thread.holdsLock(mutex))
                throw new AssertionError();
            if(factory == null)
                if(isJndi())
                    try
                    {
                        factory = (XAConnectionFactory)XtierKernel.getInstance().jndi().getInitialContext().lookup(getJndiName());
                    }
                    catch(NamingException namingexception)
                    {
                        JMSException jmsexception = new JMSException(L10n.format("SRVC.JMS.ERR5", getName(), getJndiName()));
                        jmsexception.setLinkedException(namingexception);
                        throw jmsexception;
                    }
                else
                    try
                    {
                        factory = (XAConnectionFactory)getIoc().createNewObj(JmsServiceImpl.class$javax$jms$XAConnectionFactory != null ? JmsServiceImpl.class$javax$jms$XAConnectionFactory : (JmsServiceImpl.class$javax$jms$XAConnectionFactory = JmsServiceImpl._mthclass$("javax.jms.XAConnectionFactory")));
                    }
                    catch(IocDescriptorException iocdescriptorexception)
                    {
                        JMSException jmsexception1 = new JMSException(L10n.format("SRVC.JMS.ERR4", getName()));
                        jmsexception1.setLinkedException(iocdescriptorexception);
                        throw jmsexception1;
                    }
            return factory;
        }

        XAConnection createConn()
            throws JMSException
        {
            return createConn(getUsername(), getPasswd());
        }

        XAConnection createConn(String s, String s1)
            throws JMSException
        {
            XAConnection xaconnection = s != null ? s1 != null ? getFactory().createXAConnection(s, s1) : getFactory().createXAConnection(s, "") : getFactory().createXAConnection();
            xaconnection.start();
            return xaconnection;
        }

        private XAConnectionFactory factory;
        static final boolean $assertionsDisabled; /* synthetic field */


        XaConnFactory(String s, String s1, String s2)
        {
            super(s, s1, s2);
            factory = null;
        }

        XaConnFactory(String s, XAConnectionFactory xaconnectionfactory, int i, long l, String s1,
                String s2)
        {
            super(s, i, l, s1, s2);
            if(!$assertionsDisabled && xaconnectionfactory == null)
            {
                throw new AssertionError();
            } else
            {
                factory = xaconnectionfactory;
                return;
            }
        }
    }

    private class ConnFactory extends AbstractConnFactory
    {

        ConnectionFactory getFactory()
            throws JMSException
        {
            if(!$assertionsDisabled && !Thread.holdsLock(mutex))
                throw new AssertionError();
            if(factory == null)
                if(isJndi())
                    try
                    {
                        factory = (ConnectionFactory)XtierKernel.getInstance().jndi().getInitialContext().lookup(getJndiName());
                    }
                    catch(NamingException namingexception)
                    {
                        JMSException jmsexception = new JMSException(L10n.format("SRVC.JMS.ERR5", getName(), getJndiName()));
                        jmsexception.setLinkedException(namingexception);
                        throw jmsexception;
                    }
                else
                    try
                    {
                        factory = (ConnectionFactory)getIoc().createNewObj(JmsServiceImpl.class$javax$jms$ConnectionFactory != null ? JmsServiceImpl.class$javax$jms$ConnectionFactory : (JmsServiceImpl.class$javax$jms$ConnectionFactory = JmsServiceImpl._mthclass$("javax.jms.ConnectionFactory")));
                    }
                    catch(IocDescriptorException iocdescriptorexception)
                    {
                        JMSException jmsexception1 = new JMSException(L10n.format("SRVC.JMS.ERR4", getName()));
                        jmsexception1.setLinkedException(iocdescriptorexception);
                        throw jmsexception1;
                    }
            return factory;
        }

        Connection createConn()
            throws JMSException
        {
            return createConn(getUsername(), getPasswd());
        }

        Connection createConn(String s, String s1)
            throws JMSException
        {
            Connection connection = s != null ? s1 != null ? getFactory().createConnection(s, s1) : getFactory().createConnection(s, "") : getFactory().createConnection();
            connection.start();
            return connection;
        }

        private ConnectionFactory factory;
        static final boolean $assertionsDisabled; /* synthetic field */


        ConnFactory(String s, String s1, String s2)
        {
            super(s, s1, s2);
            factory = null;
        }

        ConnFactory(String s, ConnectionFactory connectionfactory, int i, long l, String s1,
                String s2)
        {
            super(s, s1, s2);
            if(!$assertionsDisabled && connectionfactory == null)
            {
                throw new AssertionError();
            } else
            {
                factory = connectionfactory;
                return;
            }
        }
    }

    private abstract class AbstractConnFactory
    {

        String getName()
        {
            return name;
        }

        boolean isJndi()
        {
            return jndiName != null;
        }

        String getJndiName()
        {
            return jndiName;
        }

        void setJndiName(String s)
        {
            jndiName = s;
        }

        int getRetries()
        {
            return retries;
        }

        void setRetries(int i)
        {
            retries = i;
        }

        long getTimeout()
        {
            return timeout;
        }

        void setTimeout(long l)
        {
            timeout = l;
        }

        String getPasswd()
        {
            return passwd;
        }

        String getUsername()
        {
            return username;
        }

        void setIoc(IocDescriptor iocdescriptor)
        {
            ioc = iocdescriptor;
        }

        IocDescriptor getIoc()
        {
            return ioc;
        }

        private final String name;
        private final String username;
        private final String passwd;
        private int retries;
        private long timeout;
        private IocDescriptor ioc;
        private String jndiName;
        static final boolean $assertionsDisabled; /* synthetic field */


        AbstractConnFactory(String s, String s1, String s2)
        {
            super();
            retries = 2;
            timeout = 2000L;
            if(!$assertionsDisabled && s == null)
                throw new AssertionError();
            if(!$assertionsDisabled && s1 == null && s2 != null)
            {
                throw new AssertionError();
            } else
            {
                name = s;
                username = s1;
                passwd = s2;
                return;
            }
        }

        AbstractConnFactory(String s, int i, long l, String s1, String s2)
        {
            super();
            retries = 2;
            timeout = 2000L;
            if(!$assertionsDisabled && s == null)
                throw new AssertionError();
            if(!$assertionsDisabled && i < 0)
                throw new AssertionError();
            if(!$assertionsDisabled && l < 0L)
                throw new AssertionError();
            if(!$assertionsDisabled && s1 == null && s2 != null)
            {
                throw new AssertionError();
            } else
            {
                name = s;
                retries = i;
                timeout = l;
                username = s1;
                passwd = s2;
                return;
            }
        }
    }

    private class XmlMessageDefaults
    {

        IocDescriptor getConverter()
        {
            return converter;
        }

        void setConverter(IocDescriptor iocdescriptor)
        {
            converter = iocdescriptor;
        }

        int getDeliveryMode()
        {
            return deliveryMode;
        }

        void setDeliveryMode(int i)
        {
            deliveryMode = i;
        }

        boolean isDisableMsgTimestamp()
        {
            return isDisableMsgTimestamp;
        }

        void setDisableMsgTimestamp(boolean flag)
        {
            isDisableMsgTimestamp = flag;
        }

        boolean isDisableMsgId()
        {
            return isDisableMsgId;
        }

        void setDisableMsgId(boolean flag)
        {
            isDisableMsgId = flag;
        }

        int getPriority()
        {
            return priority;
        }

        void setPriority(int i)
        {
            priority = i;
        }

        long getTtl()
        {
            return ttl;
        }

        void setTtl(long l)
        {
            ttl = l;
        }

        private int deliveryMode;
        private int priority;
        private long ttl;
        private boolean isDisableMsgId;
        private boolean isDisableMsgTimestamp;
        private IocDescriptor converter;

        private XmlMessageDefaults()
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

        XmlMessageDefaults getDflts()
        {
            return dflts;
        }

        void setDefaults(XmlMessageDefaults xmlmessagedefaults)
        {
            if(!$assertionsDisabled && xmlmessagedefaults == null)
            {
                throw new AssertionError();
            } else
            {
                dflts = xmlmessagedefaults;
                return;
            }
        }

        Map getFactories()
        {
            return factories;
        }

        void addFactory(String s, ConnFactory connfactory)
        {
            if(!$assertionsDisabled && factories.containsKey(s))
            {
                throw new AssertionError();
            } else
            {
                factories.put(s, connfactory);
                return;
            }
        }

        boolean hasFactory(String s)
        {
            return factories.containsKey(s);
        }

        void addXaFactory(String s, XaConnFactory xaconnfactory)
        {
            if(!$assertionsDisabled && xaFactories.containsKey(s))
            {
                throw new AssertionError();
            } else
            {
                xaFactories.put(s, xaconnfactory);
                return;
            }
        }

        boolean hasXaFactory(String s)
        {
            return xaFactories.containsKey(s);
        }

        Map getXaFactories()
        {
            return xaFactories;
        }

        private final String name;
        private XmlMessageDefaults dflts;
        private Map factories;
        private Map xaFactories;
        static final boolean $assertionsDisabled; /* synthetic field */


        XmlRegion(String s)
        {
            super();
            dflts = new XmlMessageDefaults();
            factories = new HashMap();
            xaFactories = new HashMap();
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


    public JmsServiceImpl()
    {
        factories = new HashMap();
        xaFactories = new HashMap();
    }

    protected void onStart()
        throws ServiceProviderException
    {
        XtierKernel xtierkernel = XtierKernel.getInstance();
        logger = xtierkernel.log().getLogger("jms");
        HashMap hashmap = new HashMap();
        try
        {
            parseXmlConfig("xtier_jms.xml", hashmap, new HashSet());
        }
        catch(SAXException saxexception)
        {
            throw new ServiceProviderException(L10n.format("SRVC.JMS.ERR1", "xtier_jms.xml"), saxexception);
        }
        XmlRegion xmlregion = (XmlRegion)hashmap.get(getRegionName());
        if(xmlregion == null)
            throw new ServiceProviderException(L10n.format("SRVC.JMS.ERR2", getRegionName()));
        XmlMessageDefaults xmlmessagedefaults = xmlregion.getDflts();
        IocDescriptor iocdescriptor = xmlmessagedefaults.getConverter();
        try
        {
            dflts = new JmsMessageDefaults(xmlmessagedefaults.getDeliveryMode(), xmlmessagedefaults.getPriority(), xmlmessagedefaults.getTtl(), xmlmessagedefaults.isDisableMsgId(), xmlmessagedefaults.isDisableMsgTimestamp(), ((JmsObjectConverter) (iocdescriptor != null ? (JmsObjectConverter)iocdescriptor.createNewObj(com.fitechlabs.xtier.services.jms.JmsObjectConverter.class) : ((JmsObjectConverter) (new JmsDefaultConverter())))));
        }
        catch(IocDescriptorException iocdescriptorexception)
        {
            throw new ServiceProviderException(L10n.format("SRVC.JMS.ERR3"), iocdescriptorexception);
        }
        factories = xmlregion.getFactories();
        xaFactories = xmlregion.getXaFactories();
    }

    protected void onStop()
        throws ServiceProviderException
    {
        dflts = null;
        factories = null;
        xaFactories = null;
        logger = null;
    }

    private void parseXmlConfig(String s, Map map, Set set)
        throws SAXException
    {
        if(!$assertionsDisabled && (s == null || map == null || set == null))
            throw new AssertionError();
        if(set.contains(s))
        {
            logger.warning(L10n.format("SRVC.JMS.WRN1", s));
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
                            throw createSaxErr(L10n.format("SRVC.JMS.ERR14", s3));
                        region = new XmlRegion(s3);
                        regions.put(s3, region);
                    } else
                    if(s2.equals("msg-defaults"))
                    {
                        isDflts = true;
                        dflts = new XmlMessageDefaults();
                        region.setDefaults(dflts);
                        String s4 = xmlattrinterceptor.getValue("priority");
                        String s8 = xmlattrinterceptor.getValue("delivery-mode");
                        String s12 = xmlattrinterceptor.getValue("ttl");
                        String s15 = xmlattrinterceptor.getValue("disable-msg-id");
                        String s16 = xmlattrinterceptor.getValue("disable-msg-timestamp");
                        if(s4 == null)
                        {
                            dflts.setPriority(4);
                        } else
                        {
                            int j = parseInt(s4);
                            if(j < 0 || j > 9)
                                throw createSaxErr(L10n.format("SRVC.JMS.ERR15", s4));
                            dflts.setPriority(j);
                        }
                        if(s8 == null)
                            dflts.setDeliveryMode(2);
                        else
                        if(s8.equals("persistent"))
                        {
                            dflts.setDeliveryMode(2);
                        } else
                        {
                            if(!$assertionsDisabled && !s8.equals("non-persistent"))
                                throw new AssertionError();
                            dflts.setDeliveryMode(1);
                        }
                        if(s12 == null)
                        {
                            dflts.setTtl(0L);
                        } else
                        {
                            long l1 = parseLong(s12);
                            if(l1 < 0L)
                                throw createSaxErr(L10n.format("SRVC.JMS.ERR16", s12));
                            dflts.setTtl(l1);
                        }
                        dflts.setDisableMsgId(s15 != null ? parseBoolean(s15) : false);
                        dflts.setDisableMsgTimestamp(s16 != null ? parseBoolean(s16) : false);
                    } else
                    if(s2.equals("conn-factory"))
                    {
                        String s5 = xmlattrinterceptor.getValue("name");
                        String s9 = xmlattrinterceptor.getValue("username");
                        String s13 = xmlattrinterceptor.getValue("password");
                        if(s13 != null && s9 == null)
                            throw createSaxErr(L10n.format("SRVC.JMS.ERR7"));
                        if(region.hasFactory(s5))
                            throw createSaxErr(L10n.format("SRVC.JMS.ERR17", s5));
                        factory = new ConnFactory(s5, s9, s13);
                        region.addFactory(s5, (ConnFactory)factory);
                    } else
                    if(s2.equals("xa-conn-factory"))
                    {
                        String s6 = xmlattrinterceptor.getValue("name");
                        String s10 = xmlattrinterceptor.getValue("username");
                        String s14 = xmlattrinterceptor.getValue("password");
                        if(s14 != null && s10 == null)
                            throw createSaxErr(L10n.format("SRVC.JMS.ERR7"));
                        if(region.hasXaFactory(s6))
                            throw createSaxErr(L10n.format("SRVC.JMS.ERR18", s6));
                        factory = new XaConnFactory(s6, s10, s14);
                        region.addXaFactory(s6, (XaConnFactory)factory);
                    } else
                    if(s2.equals("jndi-factory"))
                        factory.setJndiName(xmlattrinterceptor.getValue("name"));
                    else
                    if(s2.equals("ioc-factory"))
                        isIocFactory = true;
                    else
                    if(s2.equals("smart-conn"))
                    {
                        String s7 = xmlattrinterceptor.getValue("retries");
                        String s11 = xmlattrinterceptor.getValue("retry-timeout");
                        int i = parseInt(s7);
                        if(i < 0)
                            throw createSaxErr(L10n.format("SRVC.JMS.ERR19", s7));
                        factory.setRetries(i);
                        long l = parseLong(s11);
                        if(l < 0L)
                            throw createSaxErr(L10n.format("SRVC.JMS.ERR20", s11));
                        factory.setTimeout(l);
                    }
                }

                protected void onIocDescriptor(IocDescriptor iocdescriptor)
                    throws SAXException
                {
                    if(isDflts)
                    {
                        dflts.setConverter(iocdescriptor);
                    } else
                    {
                        if(!$assertionsDisabled && !isIocFactory)
                            throw new AssertionError();
                        factory.setIoc(iocdescriptor);
                    }
                }

                protected void onTagEnd(String s2)
                    throws SAXException
                {
                    if(s2.equals("region"))
                        region = null;
                    else
                    if(s2.equals("msg-defaults"))
                        isDflts = false;
                    else
                    if(s2.equals("ioc-factory"))
                        isIocFactory = false;
                }

                private XmlRegion region;
                private AbstractConnFactory factory;
                private XmlMessageDefaults dflts;
                private boolean isDflts;
                private boolean isIocFactory;
                static final boolean $assertionsDisabled; /* synthetic field */



                throws SAXException
            {
                super(final_s, final_s1);
                region = null;
                factory = null;
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
            throw new SAXException(L10n.format("SRVC.JMS.ERR6"), ioexception);
        }
    }

    public void close(Connection connection)
    {
        Utils.close(connection);
    }

    public void close(MessageConsumer messageconsumer)
    {
        Utils.close(messageconsumer);
    }

    public void close(MessageProducer messageproducer)
    {
        Utils.close(messageproducer);
    }

    public void close(Session session)
    {
        Utils.close(session);
    }

    public void close(JmsObjectSender jmsobjectsender)
    {
        JmsUtils.close(jmsobjectsender);
    }

    public void close(JmsObjectReceiver jmsobjectreceiver)
    {
        JmsUtils.close(jmsobjectreceiver);
    }

    public JmsObjectReceiver createReceiver(Connection connection, Destination destination)
        throws JMSException
    {
        ArgAssert.nullArg(connection, "conn");
        ArgAssert.nullArg(destination, "rcvDest");
        return new JmsObjectReceiverImpl(connection, destination, dflts);
    }

    public JmsObjectReceiver createReceiver(Connection connection, String s, boolean flag)
        throws JMSException
    {
        ArgAssert.nullArg(connection, "conn");
        ArgAssert.nullArg(s, "rcvDest");
        return new JmsObjectReceiverImpl(connection, s, flag, dflts);
    }

    public JmsObjectSender createSender(Connection connection, Destination destination)
        throws JMSException
    {
        ArgAssert.nullArg(connection, "conn");
        ArgAssert.nullArg(destination, "sendDest");
        return new JmsObjectSenderImpl(connection, destination, dflts);
    }

    public JmsObjectSender createSender(Connection connection, String s, boolean flag)
        throws JMSException
    {
        ArgAssert.nullArg(connection, "conn");
        ArgAssert.nullArg(s, "sendDest");
        return new JmsObjectSenderImpl(connection, s, flag, dflts);
    }

    public Connection getConn(String s, String s1, String s2)
        throws JMSException
    {
        return getConn0(s, s1, s2);
    }

    public Connection getConn(String s)
        throws JMSException
    {
        return getConn0(s, null, null);
    }

    private Connection getConn0(String s, String s1, String s2)
        throws JMSException
    {
        ArgAssert.nullArg(s, "name");
        if(s1 == null && s2 != null)
            throw new IllegalArgumentException(L10n.format("SRVC.JMS.ERR7"));
        Object obj = mutex;
        JVM INSTR monitorenter ;
        if(!factories.containsKey(s))
            throw new JMSException(L10n.format("SRVC.JMS.ERR8", s));
        return ((ConnFactory)factories.get(s)).createConn();
        Exception exception;
        exception;
        throw exception;
    }

    public XAConnection getXaConn(String s, String s1, String s2)
        throws JMSException
    {
        return getXaConn0(s, s1, s2);
    }

    public XAConnection getXaConn(String s)
        throws JMSException
    {
        return getXaConn0(s, null, null);
    }

    private XAConnection getXaConn0(String s, String s1, String s2)
        throws JMSException
    {
        ArgAssert.nullArg(s, "name");
        if(s1 == null && s2 != null)
            throw new IllegalArgumentException(L10n.format("SRVC.JMS.ERR7"));
        Object obj = mutex;
        JVM INSTR monitorenter ;
        if(!xaFactories.containsKey(s))
            throw new JMSException(L10n.format("SRVC.JMS.ERR8", s));
        return ((XaConnFactory)xaFactories.get(s)).createConn(s1, s2);
        Exception exception;
        exception;
        throw exception;
    }

    public ConnectionFactory getConnFactory(String s)
        throws JMSException
    {
        ArgAssert.nullArg(s, "name");
        Object obj = mutex;
        JVM INSTR monitorenter ;
        ConnFactory connfactory = (ConnFactory)factories.get(s);
        return connfactory != null ? connfactory.getFactory() : null;
        Exception exception;
        exception;
        throw exception;
    }

    public void addConnFactory(String s, ConnectionFactory connectionfactory)
        throws JMSException
    {
        addConnFactory(s, connectionfactory, 2, 2000L, null, null);
    }

    public void addConnFactory(String s, ConnectionFactory connectionfactory, int i, long l, String s1, String s2)
        throws JMSException
    {
        ArgAssert.nullArg(s, "name");
        ArgAssert.nullArg(connectionfactory, "factory");
        ArgAssert.illegalRange(i >= 0, "retries", "retries >= 0");
        ArgAssert.illegalRange(l > 0L, "retryTimeout", "retryTimeout > 0");
        if(s1 == null && s2 != null)
            throw new IllegalArgumentException(L10n.format("SRVC.JMS.ERR7"));
        synchronized(mutex)
        {
            if(factories.containsKey(s))
                throw new JMSException(L10n.format("SRVC.JMS.ERR9", s));
            factories.put(s, new ConnFactory(s, connectionfactory, i, l, s1, s2));
        }
    }

    public XAConnectionFactory getXaConnFactory(String s)
        throws JMSException
    {
        ArgAssert.nullArg(s, "name");
        Object obj = mutex;
        JVM INSTR monitorenter ;
        XaConnFactory xaconnfactory = (XaConnFactory)xaFactories.get(s);
        return xaconnfactory != null ? xaconnfactory.getFactory() : null;
        Exception exception;
        exception;
        throw exception;
    }

    public void addXaConnFactory(String s, XAConnectionFactory xaconnectionfactory)
        throws JMSException
    {
        addXaConnFactory(s, xaconnectionfactory, 2, 2000L, null, null);
    }

    public void addXaConnFactory(String s, XAConnectionFactory xaconnectionfactory, int i, long l, String s1, String s2)
        throws JMSException
    {
        ArgAssert.nullArg(s, "name");
        ArgAssert.nullArg(xaconnectionfactory, "factory");
        ArgAssert.illegalRange(i >= 0, "retries", "retries >= 0");
        ArgAssert.illegalRange(l > 0L, "retryTimeout", "retryTimeout > 0");
        if(s1 == null && s2 != null)
            throw new IllegalArgumentException(L10n.format("SRVC.JMS.ERR7"));
        synchronized(mutex)
        {
            if(xaFactories.containsKey(s))
                throw new JMSException(L10n.format("SRVC.JMS.ERR9", s));
            xaFactories.put(s, new XaConnFactory(s, xaconnectionfactory, i, l, s1, s2));
        }
    }

    public JmsSmartConnection getSmartConn(String s, String s1, String s2)
        throws JMSException
    {
        return getSmartConn0(s, s1, s2);
    }

    public JmsSmartConnection getSmartConn(String s)
        throws JMSException
    {
        return getSmartConn0(s, null, null);
    }

    private JmsSmartConnection getSmartConn0(String s, String s1, String s2)
        throws JMSException
    {
        ArgAssert.nullArg(s, "name");
        if(s1 == null && s2 != null)
            throw new IllegalArgumentException(L10n.format("SRVC.JMS.ERR7"));
        Object obj = mutex;
        JVM INSTR monitorenter ;
        ConnFactory connfactory = (ConnFactory)factories.get(s);
        if(connfactory == null)
            throw new JMSException(L10n.format("SRVC.JMS.ERR8", s));
        return new JmsSmartConnectionImpl(connfactory.getFactory(), s1, s2, connfactory.getRetries(), connfactory.getTimeout());
        Exception exception;
        exception;
        throw exception;
    }

    public JmsSmartXaConnection getSmartXaConn(String s)
        throws JMSException
    {
        return getSmartXaConn0(s, null, null);
    }

    public JmsSmartXaConnection getSmartXaConn(String s, String s1, String s2)
        throws JMSException
    {
        return getSmartXaConn0(s, s1, s2);
    }

    private JmsSmartXaConnection getSmartXaConn0(String s, String s1, String s2)
        throws JMSException
    {
        ArgAssert.nullArg(s, "name");
        if(s1 == null && s2 != null)
            throw new IllegalArgumentException(L10n.format("SRVC.JMS.ERR7"));
        Object obj = mutex;
        JVM INSTR monitorenter ;
        XaConnFactory xaconnfactory = (XaConnFactory)xaFactories.get(s);
        if(xaconnfactory == null)
            throw new JMSException(L10n.format("SRVC.JMS.ERR8", s));
        return new JmsSmartXaConnectionImpl(xaconnfactory.getFactory(), s1, s2, xaconnfactory.getRetries(), xaconnfactory.getTimeout());
        Exception exception;
        exception;
        throw exception;
    }

    public String getName()
    {
        return "jms";
    }

    private JmsMessageDefaults dflts;
    private Map factories;
    private Map xaFactories;
    private Logger logger;
    private final Object mutex = new Object();
    static final boolean $assertionsDisabled; /* synthetic field */
    static Class class$javax$jms$ConnectionFactory; /* synthetic field */
    static Class class$javax$jms$XAConnectionFactory; /* synthetic field */

    static
    {
        $assertionsDisabled = !(JmsServiceImpl.class).desiredAssertionStatus();
    }


}
