// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.i18n.impl;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.kernel.ioc.IocDescriptor;
import com.fitechlabs.xtier.kernel.ioc.IocDescriptorException;
import com.fitechlabs.xtier.kernel.spi.ServiceProviderAdapter;
import com.fitechlabs.xtier.kernel.spi.ServiceProviderException;
import com.fitechlabs.xtier.kernel.xml.XmlAttrInterceptor;
import com.fitechlabs.xtier.kernel.xml.XmlSaxHandler;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.i18n.*;
import com.fitechlabs.xtier.services.log.LogService;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.threads.SysThread;
import com.fitechlabs.xtier.threads.SysThreadInterrupted;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.Utils;
import com.fitechlabs.xtier.utils.xml.XmlUtils;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import org.xml.sax.SAXException;

// Referenced classes of package com.fitechlabs.xtier.services.i18n.impl:
//            I18nGroup, I18nProp, I18nLocale, I18nRegion

public class I18nServiceImpl extends ServiceProviderAdapter
    implements I18nService
{

    public I18nServiceImpl()
    {
        reloadMsec = 0L;
        log = null;
        locale = Locale.getDefault();
        skin = null;
        listeners = new HashSet();
        region = null;
        reloader = null;
    }

    private void parseXmlConfig(String s, Map map, Set set)
        throws SAXException
    {
        if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            throw new AssertionError();
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        if(set.contains(s))
        {
            log.warning(L10n.format("SRVC.I18N.IMPL.WRN1", s));
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
                            throw createSaxErr(L10n.format("SRVC.I18N.ERR2", s3));
                        xmlRegion = new I18nRegion(s3);
                    } else
                    if(s2.equals("include"))
                    {
                        String s4 = xmlattrinterceptor.getValue("path");
                        parseXmlConfig(s4, regions, includes);
                    } else
                    if(s2.equals("group"))
                    {
                        String s5 = xmlattrinterceptor.getValue("name");
                        if(xmlRegion.containsGroup(s5))
                            throw createSaxErr(L10n.format("SRVC.I18N.ERR5", xmlRegion, s5));
                        group = new I18nGroup(s5);
                    } else
                    if(s2.equals("i18n"))
                    {
                        String s6 = xmlattrinterceptor.getValue("name");
                        if(group.containsProp(s6))
                            throw createSaxErr(L10n.format("SRVC.I18N.ERR6", xmlRegion, group, s6));
                        prop = new I18nProp(s6);
                    } else
                    if(s2.equals("reload"))
                    {
                        long l = parseLong(xmlattrinterceptor.getValue("msec"));
                        if(l <= 0L)
                            throw createSaxErr(L10n.format("SRVC.I18N.ERR20", xmlRegion, xmlattrinterceptor.getValue("msec")));
                        xmlRegion.setReloadMsec(l);
                    } else
                    if(s2.equals("forall"))
                    {
                        xmlLocale = new I18nLocale(null, null, xmlSkin);
                        if(prop.contains(xmlLocale))
                            throw createSaxErr(L10n.format("SRVC.I18N.ERR7", xmlRegion, group, prop));
                    } else
                    if(s2.equals("ioc-forall"))
                    {
                        xmlLocale = new I18nLocale(null, null, xmlSkin, true);
                        if(prop.contains(xmlLocale))
                            throw createSaxErr(L10n.format("SRVC.I18N.ERR7", xmlRegion, group, prop));
                    } else
                    if(s2.equals("skin"))
                    {
                        if(!$assertionsDisabled && (xmlSkin != null || prop == null))
                            throw new AssertionError();
                        xmlSkin = xmlattrinterceptor.getValue("name");
                    } else
                    if(s2.equals("locale"))
                    {
                        String s7 = xmlattrinterceptor.getValue("lang");
                        String s9 = xmlattrinterceptor.getValue("country");
                        xmlLocale = new I18nLocale(s7, s9, xmlSkin);
                        if(prop.contains(xmlLocale))
                            throw createSaxErr(L10n.format("SRVC.I18N.ERR8", xmlRegion, group, prop, xmlLocale));
                        I18nLocale ai18nlocale[] = null;
                        if(xmlSkin != null)
                            ai18nlocale = (new I18nLocale[] {
                                new I18nLocale(null, null, xmlSkin), new I18nLocale(null, null, null)
                            });
                        else
                            ai18nlocale = (new I18nLocale[] {
                                new I18nLocale(null, null, null)
                            });
                        for(int i = 0; i < ai18nlocale.length; i++)
                            if(prop.contains(ai18nlocale[i]))
                                throw createSaxErr(L10n.format("SRVC.I18N.ERR9", xmlRegion, group, prop));

                    } else
                    if(s2.equals("ioc-locale"))
                    {
                        String s8 = xmlattrinterceptor.getValue("lang");
                        String s10 = xmlattrinterceptor.getValue("country");
                        xmlLocale = new I18nLocale(s8, s10, xmlSkin, true);
                        if(prop.contains(xmlLocale))
                            throw createSaxErr(L10n.format("SRVC.I18N.ERR8", xmlRegion, group, prop, xmlLocale));
                        I18nLocale ai18nlocale1[] = null;
                        if(xmlSkin != null)
                            ai18nlocale1 = (new I18nLocale[] {
                                new I18nLocale(null, null, xmlSkin, true), new I18nLocale(null, null, null)
                            });
                        else
                            ai18nlocale1 = (new I18nLocale[] {
                                new I18nLocale(null, null, null, true)
                            });
                        for(int j = 0; j < ai18nlocale1.length; j++)
                            if(prop.contains(ai18nlocale1[j]))
                                throw createSaxErr(L10n.format("SRVC.I18N.ERR9", xmlRegion, group, prop));

                    }
                }

                protected void onIocDescriptor(IocDescriptor iocdescriptor)
                    throws SAXException
                {
                    xmlLocale.setIocDescriptor(iocdescriptor);
                }

                protected void onTagEnd(String s2)
                    throws SAXException
                {
                    if(s2.equals("region"))
                    {
                        regions.put(xmlRegion.getName(), xmlRegion);
                        xmlRegion = null;
                    } else
                    if(s2.equals("group"))
                    {
                        xmlRegion.addGroup(group);
                        group = null;
                    } else
                    if(s2.equals("locale"))
                    {
                        xmlLocale.setValue(getPcdata());
                        prop.addLocale(xmlLocale);
                        xmlLocale = null;
                    } else
                    if(s2.equals("ioc-locale"))
                    {
                        prop.addLocale(xmlLocale);
                        xmlLocale = null;
                    } else
                    if(s2.equals("forall"))
                    {
                        xmlLocale.setValue(getPcdata());
                        prop.addLocale(xmlLocale);
                        xmlLocale = null;
                    } else
                    if(s2.equals("ioc-forall"))
                    {
                        prop.addLocale(xmlLocale);
                        xmlLocale = null;
                    } else
                    if(s2.equals("reload"))
                        xmlRegion.setIsReload(parseBoolean(getPcdata()));
                    else
                    if(s2.equals("skin"))
                        xmlSkin = null;
                    else
                    if(s2.equals("i18n"))
                    {
                        group.addProp(prop);
                        prop = null;
                    }
                }

                private I18nGroup group;
                private I18nProp prop;
                private I18nRegion xmlRegion;
                private I18nLocale xmlLocale;
                private String xmlSkin;
                static final boolean $assertionsDisabled; /* synthetic field */



                throws SAXException
            {
                super(final_s, final_s1);
                group = null;
                prop = null;
                xmlRegion = null;
                xmlLocale = null;
                xmlSkin = null;
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
            throw new SAXException(L10n.format("SRVC.I18N.ERR16", s1), ioexception);
        }
    }

    private void expandVars(I18nRegion i18nregion)
        throws I18nException
    {
        if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            throw new AssertionError();
        for(boolean flag = true; flag;)
        {
            flag = false;
            Iterator iterator = i18nregion.getAllGroups();
            while(iterator.hasNext())
            {
                I18nGroup i18ngroup = (I18nGroup)iterator.next();
                Iterator iterator1 = i18ngroup.getAllProps();
                while(iterator1.hasNext())
                {
                    I18nProp i18nprop = (I18nProp)iterator1.next();
                    Iterator iterator2 = i18nprop.getAllLocales();
                    while(iterator2.hasNext())
                    {
                        I18nLocale i18nlocale = (I18nLocale)iterator2.next();
                        if(!i18nlocale.isIocLocale())
                        {
                            String s = i18nlocale.getValue();
                            Matcher matcher = varRegex.matcher(s);
                            StringBuffer stringbuffer = new StringBuffer();
                            StringBuffer stringbuffer1;
                            for(; matcher.find(); matcher.appendReplacement(stringbuffer, stringbuffer1.toString()))
                            {
                                String s1 = matcher.group();
                                int i = s1.indexOf(':');
                                if(i == s1.length() - 1)
                                    throw new I18nException(L10n.format("SRVC.I18N.ERR17", s1));
                                String s3 = null;
                                String s4 = null;
                                if(i == -1)
                                {
                                    s3 = i18ngroup.getName();
                                    s4 = s1.substring(2, s1.length() - 1);
                                } else
                                {
                                    s3 = s1.substring(2, i);
                                    s4 = s1.substring(i + 1, s1.length() - 1);
                                }
                                if(s3.equals(i18ngroup.getName()) && s4.equals(i18nprop.getName()))
                                    throw new I18nException(L10n.format("SRVC.I18N.ERR18", s1, i18nprop, i18nlocale));
                                String s5 = findVarSubst(i18nregion, s3, s4, i18nlocale);
                                if(s5 == null)
                                    throw new I18nException(L10n.format("SRVC.I18N.ERR19", s1, i18nprop, i18nlocale));
                                stringbuffer1 = new StringBuffer(s5.length());
                                for(int j = 0; j < s5.length(); j++)
                                {
                                    char c = s5.charAt(j);
                                    switch(c)
                                    {
                                    case 92: // '\\'
                                        stringbuffer1.append('\\').append('\\');
                                        break;

                                    case 36: // '$'
                                        stringbuffer1.append('\\').append('$');
                                        break;

                                    default:
                                        stringbuffer1.append(c);
                                        break;
                                    }
                                }

                            }

                            matcher.appendTail(stringbuffer);
                            String s2 = stringbuffer.toString();
                            if(!s2.equals(s))
                            {
                                i18nlocale.setValue(s2);
                                flag = true;
                            }
                        }
                    }
                }
            }
        }

    }

    private String findVarSubst(I18nRegion i18nregion, String s, String s1, I18nLocale i18nlocale)
    {
        if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            throw new AssertionError();
        if(!$assertionsDisabled && (s == null || s1 == null || i18nlocale == null))
            throw new AssertionError();
        I18nGroup i18ngroup = i18nregion.getGroup(s);
        if(i18ngroup != null)
        {
            I18nProp i18nprop = i18ngroup.getProp(s1);
            if(i18nprop != null)
            {
                I18nLocale i18nlocale1 = i18nprop.findLocale(i18nlocale.getLang(), i18nlocale.getCountry(), skin);
                if(i18nlocale1 != null)
                    if(!i18nlocale1.isIocLocale())
                        return i18nlocale1.getValue();
                    else
                        throw new I18nMissingResourceException(L10n.format("SRVC.I18N.ERR24", s, s1, i18nlocale, skin));
            }
        }
        return null;
    }

    private String findProp(String s, String s1, Locale locale1, String s2)
    {
        if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            throw new AssertionError();
        if(!$assertionsDisabled && (s == null || s1 == null || locale1 == null))
            throw new AssertionError();
        String s3 = null;
        I18nGroup i18ngroup = region.getGroup(s);
        if(i18ngroup != null)
        {
            I18nProp i18nprop = i18ngroup.getProp(s1);
            if(i18nprop != null)
            {
                I18nLocale i18nlocale = i18nprop.findLocale(locale1, s2);
                if(i18nlocale != null)
                {
                    if(!i18nlocale.isIocLocale())
                        return i18nlocale.getValue();
                    s3 = L10n.format("SRVC.I18N.ERR23", s, s1, locale1, s2);
                } else
                {
                    s3 = L10n.format("SRVC.I18N.ERR12", s, s1, locale1, s2);
                }
            } else
            {
                s3 = L10n.format("SRVC.I18N.ERR13", s, s1);
            }
        } else
        {
            s3 = L10n.format("SRVC.I18N.ERR14", s);
        }
        if(!$assertionsDisabled && s3 == null)
            throw new AssertionError();
        else
            throw new I18nMissingResourceException(s3);
    }

    private Object findIocProp(String s, String s1, Locale locale1, String s2)
    {
        if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            throw new AssertionError();
        if(!$assertionsDisabled && (s == null || s1 == null || locale1 == null))
            throw new AssertionError();
        String s3 = null;
        I18nGroup i18ngroup = region.getGroup(s);
        if(i18ngroup != null)
        {
            I18nProp i18nprop = i18ngroup.getProp(s1);
            if(i18nprop != null)
            {
                I18nLocale i18nlocale = i18nprop.findLocale(locale1, s2);
                if(i18nlocale != null)
                {
                    if(i18nlocale.isIocLocale())
                        try
                        {
                            return i18nlocale.getIocValue();
                        }
                        catch(IocDescriptorException iocdescriptorexception)
                        {
                            throw new I18nMissingResourceException(L10n.format("SRVC.I18N.ERR21", s, s1, locale1, s2), iocdescriptorexception);
                        }
                    s3 = L10n.format("SRVC.I18N.ERR22", s, s1, locale1, s2);
                } else
                {
                    s3 = L10n.format("SRVC.I18N.ERR12", s, s1, locale1, s2);
                }
            } else
            {
                s3 = L10n.format("SRVC.I18N.ERR13", s, s1);
            }
        } else
        {
            s3 = L10n.format("SRVC.I18N.ERR14", s);
        }
        if(!$assertionsDisabled && s3 == null)
            throw new AssertionError();
        else
            throw new I18nMissingResourceException(s3);
    }

    private Object findIocProp(String s, String s1, Locale locale1, String s2, Object obj)
    {
        if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            throw new AssertionError();
        if(!$assertionsDisabled && (s == null || s1 == null || locale1 == null))
            throw new AssertionError();
        String s3 = null;
        I18nGroup i18ngroup = region.getGroup(s);
        if(i18ngroup != null)
        {
            I18nProp i18nprop = i18ngroup.getProp(s1);
            if(i18nprop != null)
            {
                I18nLocale i18nlocale = i18nprop.findLocale(locale1, s2);
                if(i18nlocale != null)
                {
                    if(i18nlocale.isIocLocale())
                        try
                        {
                            return i18nlocale.getIocValue(obj);
                        }
                        catch(IocDescriptorException iocdescriptorexception)
                        {
                            throw new I18nMissingResourceException(L10n.format("SRVC.I18N.ERR21", s, s1, locale1, s2), iocdescriptorexception);
                        }
                    s3 = L10n.format("SRVC.I18N.ERR22", s, s1, locale1, s2);
                } else
                {
                    s3 = L10n.format("SRVC.I18N.ERR12", s, s1, locale1, s2);
                }
            } else
            {
                s3 = L10n.format("SRVC.I18N.ERR13", s, s1);
            }
        } else
        {
            s3 = L10n.format("SRVC.I18N.ERR14", s);
        }
        if(!$assertionsDisabled && s3 == null)
            throw new AssertionError();
        else
            throw new I18nMissingResourceException(s3);
    }

    private void startReloader()
    {
        if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            throw new AssertionError();
        if(reloader == null)
        {
            reloader = new SysThread("i18n-reloader") {

                protected void body()
                {
                    if(!$assertionsDisabled && reloadMsec <= 0L)
                        throw new AssertionError("Invalid reload msec: " + reloadMsec);
                    File file = new File(Utils.makeValidPath(Utils.makeValidPath(getXtierRoot(), getConfigPath()), "xtier_i18n.xml"));
                    long l = file.lastModified();
                    do
                    {
                        long l1;
                        do
                        {
                            checkInterrupted();
                            Utils.sleep(reloadMsec);
                            l1 = file.lastModified();
                        } while(l1 == l);
                        l = l1;
                        try
                        {
                            synchronized(mutex)
                            {
                                reload0();
                            }
                            log.trace(L10n.format("SRVC.I18N.TRC1", file));
                        }
                        catch(I18nException i18nexception)
                        {
                            log.error(L10n.format("SRVC.I18N.ERR1"), i18nexception);
                        }
                    } while(true);
                }

                static final boolean $assertionsDisabled; /* synthetic field */



            {
                super(s);
            }
            }
;
            reloader.start();
        }
    }

    private void reload0()
        throws I18nException
    {
        if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            throw new AssertionError();
        load();
        for(Iterator iterator = listeners.iterator(); iterator.hasNext(); ((I18nChangeListener)iterator.next()).i18nChanged());
    }

    private void load()
        throws I18nException
    {
        if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            throw new AssertionError();
        HashMap hashmap = new HashMap();
        try
        {
            parseXmlConfig("xtier_i18n.xml", hashmap, new HashSet());
        }
        catch(SAXException saxexception)
        {
            throw new I18nException(L10n.format("SRVC.I18N.ERR10", saxexception.getMessage()), saxexception);
        }
        String s = getRegionName();
        I18nRegion i18nregion = (I18nRegion)hashmap.get(s);
        if(i18nregion == null)
            throw new I18nException(L10n.format("SRVC.I18N.ERR11", s));
        expandVars(i18nregion);
        region = i18nregion;
        reloadMsec = region.getReloadMsec();
        if(region.isReload())
            startReloader();
        else
        if(reloader != null)
        {
            if(Thread.currentThread() == reloader)
            {
                reloader = null;
                throw new SysThreadInterrupted();
            }
            Utils.stopThread(reloader);
            reloader = null;
        }
    }

    protected void onStart()
        throws ServiceProviderException
    {
        log = XtierKernel.getInstance().log().getLogger("i18n");
        try
        {
            synchronized(mutex)
            {
                load();
            }
        }
        catch(I18nException i18nexception)
        {
            throw new ServiceProviderException(L10n.format("SRVC.I18N.ERR15", i18nexception.getMessage()), i18nexception);
        }
    }

    protected void onStop()
    {
        SysThread systhread = null;
        synchronized(mutex)
        {
            systhread = reloader;
        }
        Utils.stopThread(systhread);
    }

    public void reload()
        throws I18nException
    {
        synchronized(mutex)
        {
            reload0();
        }
    }

    public void setLocale(Locale locale1)
    {
        ArgAssert.nullArg(locale1, "locale");
        synchronized(mutex)
        {
            locale = locale1;
        }
    }

    public void setSkin(String s)
    {
        synchronized(mutex)
        {
            skin = s;
        }
    }

    public String getSkin()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return skin;
        Exception exception;
        exception;
        throw exception;
    }

    public Locale getLocale()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return locale;
        Exception exception;
        exception;
        throw exception;
    }

    public void addListener(I18nChangeListener i18nchangelistener)
    {
        ArgAssert.nullArg(i18nchangelistener, "listener");
        synchronized(mutex)
        {
            listeners.add(i18nchangelistener);
        }
    }

    public List getAllListeners()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return Collections.unmodifiableList(new ArrayList(listeners));
        Exception exception;
        exception;
        throw exception;
    }

    public boolean removeListener(I18nChangeListener i18nchangelistener)
    {
        ArgAssert.nullArg(i18nchangelistener, "listener");
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return listeners.remove(i18nchangelistener);
        Exception exception;
        exception;
        throw exception;
    }

    public String format(String s, String s1)
    {
        if(!$assertionsDisabled && locale == null)
            throw new AssertionError();
        else
            return format(locale, s, s1, EMPTY_ARR);
    }

    public String format(Locale locale1, String s, String s1)
    {
        return format(locale1, s, s1, EMPTY_ARR);
    }

    public String format(String s, String s1, Object obj)
    {
        if(!$assertionsDisabled && locale == null)
            throw new AssertionError();
        else
            return format(locale, s, s1, new Object[] {
                obj
            });
    }

    public String format(Locale locale1, String s, String s1, Object obj)
    {
        return format(locale1, s, s1, new Object[] {
            obj
        });
    }

    public String format(String s, String s1, Object obj, Object obj1)
    {
        if(!$assertionsDisabled && locale == null)
            throw new AssertionError();
        else
            return format(locale, s, s1, new Object[] {
                obj, obj1
            });
    }

    public String format(Locale locale1, String s, String s1, Object obj, Object obj1)
    {
        return format(locale1, s, s1, new Object[] {
            obj, obj1
        });
    }

    public String format(String s, String s1, Object obj, Object obj1, Object obj2)
    {
        if(!$assertionsDisabled && locale == null)
            throw new AssertionError();
        else
            return format(locale, s, s1, new Object[] {
                obj, obj1, obj2
            });
    }

    public String format(Locale locale1, String s, String s1, Object obj, Object obj1, Object obj2)
    {
        return format(locale1, s, s1, new Object[] {
            obj, obj1, obj2
        });
    }

    public String format(String s, String s1, Object obj, Object obj1, Object obj2, Object obj3)
    {
        if(!$assertionsDisabled && locale == null)
            throw new AssertionError();
        else
            return format(locale, s, s1, new Object[] {
                obj, obj1, obj2, obj3
            });
    }

    public String format(Locale locale1, String s, String s1, Object obj, Object obj1, Object obj2, Object obj3)
    {
        return format(locale1, s, s1, new Object[] {
            obj, obj1, obj2, obj3
        });
    }

    public String format(String s, String s1, Object obj, Object obj1, Object obj2, Object obj3, Object obj4)
    {
        if(!$assertionsDisabled && locale == null)
            throw new AssertionError();
        else
            return format(locale, s, s1, new Object[] {
                obj, obj1, obj2, obj3, obj4
            });
    }

    public String format(Locale locale1, String s, String s1, Object obj, Object obj1, Object obj2, Object obj3,
            Object obj4)
    {
        return format(locale1, s, s1, new Object[] {
            obj, obj1, obj2, obj3, obj4
        });
    }

    public String format(String s, String s1, Object aobj[])
    {
        if(!$assertionsDisabled && locale == null)
            throw new AssertionError();
        else
            return format(locale, s, s1, aobj);
    }

    public String format(Locale locale1, String s, String s1, Object aobj[])
    {
        ArgAssert.nullArg(locale1, "locale");
        ArgAssert.nullArg(s, "group");
        ArgAssert.nullArg(s1, "prop");
        ArgAssert.nullArg(((Object) (aobj)), "params");
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return MessageFormat.format(findProp(s, s1, locale1, skin), aobj);
        Exception exception;
        exception;
        throw exception;
    }

    public String getName()
    {
        return "i18n";
    }

    public Object getObject(String s, String s1)
    {
        if(!$assertionsDisabled && locale == null)
            throw new AssertionError();
        else
            return getObject(locale, s, s1);
    }

    public Object getObject(Locale locale1, String s, String s1)
    {
        ArgAssert.nullArg(locale1, "locale");
        ArgAssert.nullArg(s, "group");
        ArgAssert.nullArg(s1, "prop");
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return findIocProp(s, s1, locale1, skin);
        Exception exception;
        exception;
        throw exception;
    }

    public Object getObject(String s, String s1, Object obj)
    {
        if(!$assertionsDisabled && locale == null)
            throw new AssertionError();
        else
            return getObject(locale, s, s1, obj);
    }

    public Object getObject(Locale locale1, String s, String s1, Object obj)
    {
        ArgAssert.nullArg(locale1, "locale");
        ArgAssert.nullArg(s, "group");
        ArgAssert.nullArg(s1, "prop");
        ArgAssert.nullArg(obj, "key");
        Object obj1 = mutex;
        JVM INSTR monitorenter ;
        return findIocProp(s, s1, locale1, skin, obj);
        Exception exception;
        exception;
        throw exception;
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

    private static final Object EMPTY_ARR[] = new Object[0];
    private long reloadMsec;
    private Logger log;
    private Locale locale;
    private String skin;
    private Set listeners;
    private I18nRegion region;
    private final Object mutex = new Object();
    private final Pattern varRegex = Pattern.compile("\\$\\{([^:}]+:)?[^:}]+\\}");
    private SysThread reloader;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(I18nServiceImpl.class).desiredAssertionStatus();
    }






}
