// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.jndi.impl;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.kernel.spi.ServiceProviderAdapter;
import com.fitechlabs.xtier.kernel.spi.ServiceProviderException;
import com.fitechlabs.xtier.kernel.xml.XmlAttrInterceptor;
import com.fitechlabs.xtier.kernel.xml.XmlSaxHandler;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.microkernel.MicroKernelContext;
import com.fitechlabs.xtier.services.jndi.JndiService;
import com.fitechlabs.xtier.services.log.LogService;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.Utils;
import com.fitechlabs.xtier.utils.xml.XmlUtils;
import java.io.IOException;
import java.util.*;
import javax.naming.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import org.xml.sax.SAXException;

public class JndiServiceImpl extends ServiceProviderAdapter
    implements JndiService
{

    public JndiServiceImpl()
    {
        includes = new HashSet();
        regionFound = false;
        regions = new HashSet();
    }

    protected void onStart()
        throws ServiceProviderException
    {
        hostEnvId = getMicroKernelContext().getHostEnvId();
        log = XtierKernel.getInstance().log().getLogger("jndi");
        regions.clear();
        try
        {
            parseXmlConfig("xtier_jndi.xml", getRegionName());
        }
        catch(SAXException saxexception)
        {
            throw new ServiceProviderException(L10n.format("SRVC.JNDI.IMPL.ERR1"), saxexception);
        }
    }

    protected void onStop()
    {
        includes.clear();
    }

    public String getName()
    {
        return "jndi";
    }

    public InitialContext getInitialContext()
        throws NamingException
    {
        if(hostEnvId == 1)
            return new InitialContext();
        else
            return new InitialContext(env);
    }

    public InitialContext getInitialContext(Map map)
        throws NamingException
    {
        ArgAssert.nullArg(map, "props");
        Hashtable hashtable = (Hashtable)env.clone();
        hashtable.putAll(map);
        return new InitialContext(hashtable);
    }

    private void parseXmlConfig(String s, String s1)
        throws SAXException
    {
        if(!$assertionsDisabled && (s == null || s1 == null))
            throw new AssertionError();
        if(includes.contains(s))
        {
            log.warning(L10n.format("SRVC.JNDI.IMPL.WRN1", s));
            return;
        }
        includes.add(s);
        String s2 = "file:///" + Utils.makeValidPath(Utils.makeValidPath(getXtierRoot(), getConfigPath()), s);
        try
        {
            XmlUtils.makeSaxParser().parse(s2, new XmlSaxHandler("xtier_jndi.dtd", s1) {

                protected void onTagStart(String s3, XmlAttrInterceptor xmlattrinterceptor)
                    throws SAXException
                {
                    if(s3.equals("region"))
                    {
                        currRegion = xmlattrinterceptor.getValue("name");
                        if(regions.contains(currRegion))
                            throw createSaxErr(L10n.format("SRVC.JNDI.IMPL.ERR5", currRegion));
                        regions.add(currRegion);
                        regionEnv = new Hashtable();
                    } else
                    if(s3.equals("include"))
                        parseXmlConfig(xmlattrinterceptor.getValue("path"), targetRegion);
                }

                protected void onTagEnd(String s3)
                    throws SAXException
                {
                    if(s3.equals("xtier-jndi"))
                    {
                        if(!regionFound)
                            throw createSaxErr(L10n.format("SRVC.JNDI.IMPL.ERR2", targetRegion));
                    } else
                    if(s3.equals("region"))
                    {
                        if(currRegion.equals(targetRegion))
                        {
                            regionFound = true;
                            env = regionEnv;
                        }
                    } else
                    {
                        String s4 = getPcdata();
                        if(s4.length() != 0)
                            if(s3.equals("authoritative"))
                                regionEnv.put("java.naming.authoritative", s4);
                            else
                            if(s3.equals("batchsize"))
                            {
                                parseInt(s4);
                                regionEnv.put("java.naming.batchsize", s4);
                            } else
                            if(s3.equals("dns-url"))
                                regionEnv.put("java.naming.dns.url", s4);
                            else
                            if(s3.equals("init-context-factory"))
                                regionEnv.put("java.naming.factory.initial", s4);
                            else
                            if(s3.equals("language"))
                                regionEnv.put("java.naming.language", s4);
                            else
                            if(s3.equals("obj-factories"))
                                regionEnv.put("java.naming.factory.object", s4);
                            else
                            if(s3.equals("provider-url"))
                                regionEnv.put("java.naming.provider.url", s4);
                            else
                            if(s3.equals("referral"))
                            {
                                if(!JndiServiceImpl.REFERALS.contains(s4))
                                    throw createSaxErr(L10n.format("SRVC.JNDI.IMPL.ERR4", s3, currRegion));
                                regionEnv.put("java.naming.referral", s4);
                            } else
                            if(s3.equals("authentication"))
                            {
                                if(!JndiServiceImpl.AUTHS.contains(s4))
                                    throw createSaxErr(L10n.format("SRVC.JNDI.IMPL.ERR4", s3, currRegion));
                                regionEnv.put("java.naming.security.authentication", s4);
                            } else
                            if(s3.equals("credentials"))
                                regionEnv.put("java.naming.security.credentials", s4);
                            else
                            if(s3.equals("principal"))
                                regionEnv.put("java.naming.security.principal", s4);
                            else
                            if(s3.equals("protocol"))
                                regionEnv.put("java.naming.security.protocol", s4);
                            else
                            if(s3.equals("factories"))
                                regionEnv.put("java.naming.factory.state", s4);
                            else
                            if(s3.equals("url-pkg-prefixes"))
                                regionEnv.put("java.naming.factory.url.pkgs", s4);
                    }
                }

                private String currRegion;
                private Hashtable regionEnv;


                throws SAXException
            {
                super(final_s, s1);
                currRegion = null;
                regionEnv = null;
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
            throw new SAXException(L10n.format("SRVC.JNDI.IMPL.ERR3", s2), ioexception);
        }
    }

    public void bindEx(Context context, Name name, Object obj, boolean flag)
        throws NamingException
    {
        ArgAssert.nullArg(context, "ctx");
        ArgAssert.nullArg(name, "name");
        try
        {
            context.bind(name, obj);
        }
        catch(NameAlreadyBoundException namealreadyboundexception)
        {
            if(flag)
                context.rebind(name, obj);
        }
    }

    public void bindEx(Context context, String s, Object obj, boolean flag)
        throws NamingException
    {
        ArgAssert.nullArg(context, "ctx");
        ArgAssert.nullArg(s, "name");
        try
        {
            context.bind(s, obj);
        }
        catch(NameAlreadyBoundException namealreadyboundexception)
        {
            if(flag)
                context.rebind(s, obj);
        }
    }

    public void bindEx(Name name, Object obj, boolean flag)
        throws NamingException
    {
        ArgAssert.nullArg(name, "name");
        bindEx(((Context) (getInitialContext())), name, obj, flag);
    }

    public void bindEx(String s, Object obj, boolean flag)
        throws NamingException
    {
        bindEx(((Context) (getInitialContext())), s, obj, flag);
    }

    public boolean contains(Context context, Name name)
        throws NamingException
    {
        ArgAssert.nullArg(context, "ctx");
        ArgAssert.nullArg(name, "name");
        try
        {
            context.lookup(name);
            return true;
        }
        catch(NameNotFoundException namenotfoundexception)
        {
            return false;
        }
    }

    public boolean contains(Context context, String s)
        throws NamingException
    {
        ArgAssert.nullArg(context, "ctx");
        ArgAssert.nullArg(s, "name");
        try
        {
            context.lookup(s);
            return true;
        }
        catch(NameNotFoundException namenotfoundexception)
        {
            return false;
        }
    }

    public boolean contains(Name name)
        throws NamingException
    {
        return contains(((Context) (getInitialContext())), name);
    }

    public boolean contains(String s)
        throws NamingException
    {
        return contains(((Context) (getInitialContext())), s);
    }

    public Object lookupEx(Context context, Name name, boolean flag)
        throws NamingException
    {
        ArgAssert.nullArg(context, "ctx");
        ArgAssert.nullArg(name, "name");
        Object obj;
        try
        {
            obj = context.lookup(name);
            if(obj != null)
                break MISSING_BLOCK_LABEL_47;
            if(flag)
                return null;
        }
        catch(NameNotFoundException namenotfoundexception)
        {
            if(flag)
                return null;
            else
                throw namenotfoundexception;
        }
        throw new NameNotFoundException(L10n.format("SRVC.JNDI.IMPL.ERR6", name));
        return obj;
    }

    public Object lookupEx(Context context, String s, boolean flag)
        throws NamingException
    {
        ArgAssert.nullArg(context, "ctx");
        ArgAssert.nullArg(s, "name");
        Object obj;
        try
        {
            obj = context.lookup(s);
            if(obj != null)
                break MISSING_BLOCK_LABEL_47;
            if(flag)
                return null;
        }
        catch(NameNotFoundException namenotfoundexception)
        {
            if(flag)
                return null;
            else
                throw namenotfoundexception;
        }
        throw new NameNotFoundException(L10n.format("SRVC.JNDI.IMPL.ERR6", s));
        return obj;
    }

    public Object lookupEx(Name name, boolean flag)
        throws NamingException
    {
        return lookupEx(((Context) (getInitialContext())), name, flag);
    }

    public Object lookupEx(String s, boolean flag)
        throws NamingException
    {
        return lookupEx(((Context) (getInitialContext())), s, flag);
    }

    public Object lookupNvl(Context context, Name name, Object obj)
        throws NamingException
    {
        ArgAssert.nullArg(context, "ctx");
        ArgAssert.nullArg(name, "name");
        try
        {
            return context.lookup(name);
        }
        catch(NameNotFoundException namenotfoundexception)
        {
            return obj;
        }
    }

    public Object lookupNvl(Context context, String s, Object obj)
        throws NamingException
    {
        ArgAssert.nullArg(context, "ctx");
        ArgAssert.nullArg(s, "name");
        try
        {
            return context.lookup(s);
        }
        catch(NameNotFoundException namenotfoundexception)
        {
            return obj;
        }
    }

    public Object lookupNvl(Name name, Object obj)
        throws NamingException
    {
        return lookupNvl(((Context) (getInitialContext())), name, obj);
    }

    public Object lookupNvl(String s, Object obj)
        throws NamingException
    {
        return lookupNvl(((Context) (getInitialContext())), s, obj);
    }

    public void renameEx(Context context, Name name, Name name1)
        throws NamingException
    {
        ArgAssert.nullArg(context, "ctx");
        ArgAssert.nullArg(name, "oldName");
        ArgAssert.nullArg(name1, "newName");
        try
        {
            context.rename(name, name1);
        }
        catch(NameAlreadyBoundException namealreadyboundexception)
        {
            try
            {
                context.unbind(name);
            }
            catch(NameNotFoundException namenotfoundexception1) { }
            break MISSING_BLOCK_LABEL_75;
        }
        try
        {
            context.unbind(name);
        }
        catch(NameNotFoundException namenotfoundexception) { }
        break MISSING_BLOCK_LABEL_75;
        Exception exception;
        exception;
        try
        {
            context.unbind(name);
        }
        catch(NameNotFoundException namenotfoundexception2) { }
        throw exception;
    }

    public void renameEx(Context context, String s, String s1)
        throws NamingException
    {
        ArgAssert.nullArg(context, "ctx");
        ArgAssert.nullArg(s, "oldName");
        ArgAssert.nullArg(s1, "newName");
        try
        {
            context.rename(s, s1);
        }
        catch(NameAlreadyBoundException namealreadyboundexception)
        {
            try
            {
                context.unbind(s);
            }
            catch(NameNotFoundException namenotfoundexception1) { }
            break MISSING_BLOCK_LABEL_75;
        }
        try
        {
            context.unbind(s);
        }
        catch(NameNotFoundException namenotfoundexception) { }
        break MISSING_BLOCK_LABEL_75;
        Exception exception;
        exception;
        try
        {
            context.unbind(s);
        }
        catch(NameNotFoundException namenotfoundexception2) { }
        throw exception;
    }

    public void renameEx(Name name, Name name1)
        throws NamingException
    {
        renameEx(((Context) (getInitialContext())), name, name1);
    }

    public void renameEx(String s, String s1)
        throws NamingException
    {
        renameEx(((Context) (getInitialContext())), s, s1);
    }

    public void unbindEx(Context context, Name name)
        throws NamingException
    {
        ArgAssert.nullArg(context, "ctx");
        ArgAssert.nullArg(name, "name");
        try
        {
            context.unbind(name);
        }
        catch(NameNotFoundException namenotfoundexception) { }
    }

    public void unbindEx(Context context, String s)
        throws NamingException
    {
        ArgAssert.nullArg(context, "ctx");
        ArgAssert.nullArg(s, "name");
        try
        {
            context.unbind(s);
        }
        catch(NameNotFoundException namenotfoundexception) { }
    }

    public void unbindEx(Name name)
        throws NamingException
    {
        unbindEx(((Context) (getInitialContext())), name);
    }

    public void unbindEx(String s)
        throws NamingException
    {
        unbindEx(((Context) (getInitialContext())), s);
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

    private Set includes;
    private int hostEnvId;
    private Hashtable env;
    private boolean regionFound;
    private Set regions;
    private Logger log;
    private static final Set REFERALS;
    private static final Set AUTHS;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(JndiServiceImpl.class).desiredAssertionStatus();
        REFERALS = new HashSet();
        REFERALS.add("follow");
        REFERALS.add("ignore");
        REFERALS.add("throw");
        AUTHS = new HashSet();
        AUTHS.add("none");
        AUTHS.add("simple");
        AUTHS.add("strong");
    }







}
