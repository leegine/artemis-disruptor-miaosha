// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.l10n;

import com.fitechlabs.xtier.utils.Utils;
import com.fitechlabs.xtier.utils.xml.SaxAbstractHandler;
import com.fitechlabs.xtier.utils.xml.XmlUtils;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

// Referenced classes of package com.fitechlabs.xtier.l10n:
//            L10nException

public class L10n
{

    public L10n()
    {
    }

    public static void load(String s, Locale locale)
        throws L10nException
    {
        if(!$assertionsDisabled && (s == null || locale == null))
            throw new AssertionError();
        synchronized(mutex)
        {
            includes.clear();
            props.clear();
            Locale locale1 = null;
            int i = 0;
            do
            {
                if(i >= SUPPORTED_LOCALES.length)
                    break;
                if(SUPPORTED_LOCALES[i].equals(locale))
                {
                    locale1 = SUPPORTED_LOCALES[i];
                    break;
                }
                i++;
            } while(true);
            if(locale1 == null)
            {
                int j = 0;
                do
                {
                    if(j >= SUPPORTED_LOCALES.length)
                        break;
                    if(SUPPORTED_LOCALES[j].getLanguage().equals(locale.getLanguage()))
                    {
                        locale1 = SUPPORTED_LOCALES[j];
                        break;
                    }
                    j++;
                } while(true);
            }
            if(locale1 == null)
            {
                int k = 0;
                do
                {
                    if(k >= SUPPORTED_LOCALES.length)
                        break;
                    if(SUPPORTED_LOCALES[k].getCountry().equals(locale.getCountry()))
                    {
                        locale1 = SUPPORTED_LOCALES[k];
                        break;
                    }
                    k++;
                } while(true);
            }
            if(locale1 == null)
                locale1 = Locale.US;
            try
            {
                parseXml(s, "xtier_l10n.xml", locale1);
            }
            catch(SAXException saxexception)
            {
                throw new L10nException("SAX error.", saxexception);
            }
            started = true;
        }
    }

    private static String getXmlUrl(String s, String s1)
        throws SAXException
    {
        File file = new File(Utils.makeValidPath(Utils.makeValidPath(s1, "config"), s));
        if(file.exists())
            return "file:///" + file;
        URL url = (L10n.class).getResource(s);
        if(url == null)
            throw new SAXException("Resource not found: " + s);
        else
            return url.toString();
    }

    private static void parseXml(String s, String s1, Locale locale)
        throws SAXException, L10nException
    {
        if(!$assertionsDisabled && (!Thread.holdsLock(mutex) || s1 == null || locale == null))
            throw new AssertionError();
        if(includes.contains(s1))
            throw new L10nException("Recursive file inclusion detected: " + s1);
        includes.add(s1);
        String s2 = getXmlUrl(s1, s);
        try
        {
            SAXParser saxparser = XmlUtils.makeSaxParser();
            saxparser.parse(s2, new SaxAbstractHandler(s, "xtier_l10n.dtd", locale) {

                public final void characters(char ac[], int i, int j)
                {
                    buf.append(ac, i, j);
                }

                private String getPcdata()
                {
                    String s3 = buf.toString();
                    buf.setLength(0);
                    return s3;
                }

                public void startElement(String s3, String s4, String s5, Attributes attributes)
                    throws SAXException
                {
                    String s6 = s4.length() <= 0 ? s5 : s4;
                    if(!s6.equals("l10n"))
                        if(s6.equals("include"))
                            L10n.parseXml(root, attributes.getValue("path"), locale);
                        else
                        if(s6.equals("locale"))
                        {
                            if(!$assertionsDisabled && currLocale != null)
                                throw new AssertionError();
                            String s7 = attributes.getValue("lang");
                            String s8 = attributes.getValue("country");
                            if(!$assertionsDisabled && s7 == null)
                                throw new AssertionError();
                            currLocale = s8 != null ? new Locale(s7, s8) : new Locale(s7);
                        } else
                        if(s6.equals("prop"))
                        {
                            if(!$assertionsDisabled && (name != null || value != null))
                                throw new AssertionError();
                            name = attributes.getValue("name");
                            if(!L10n.NAME_REGEX.matcher(name).matches())
                                throw new SAXException("L10n name does not match pattern [name=" + name + ", pattern=" + "^[A-Z0-9]+(\\.[A-Z0-9]+)*$" + ']');
                            if(L10n.props.containsKey(name))
                                throw new SAXException("Double L10n property name: " + name);
                        } else
                        if(!$assertionsDisabled)
                            throw new AssertionError("Invalid tag: " + s6);
                }

                public void endElement(String s3, String s4, String s5)
                    throws SAXException
                {
                    String s6 = s4.length() <= 0 ? s5 : s4;
                    if(!s6.equals("l10n") && !s6.equals("include"))
                        if(s6.equals("locale"))
                        {
                            if(!$assertionsDisabled && (currLocale == null || name == null))
                                throw new AssertionError();
                            if(locale.equals(currLocale))
                            {
                                if(value != null)
                                    throw new SAXException("Double locale definition for the property: " + name);
                                value = getPcdata();
                            }
                            currLocale = null;
                        } else
                        if(s6.equals("prop"))
                        {
                            if(!$assertionsDisabled && name == null)
                                throw new AssertionError();
                            if(value != null)
                            {
                                L10n.props.put(name, value);
                                value = null;
                            } else
                            {
                                throw new SAXException("Property is missing locale [name=" + name + ", locale=" + locale + ']');
                            }
                            name = null;
                        } else
                        if(!$assertionsDisabled)
                            throw new AssertionError("Invalid tag: " + s6);
                }

                private String name;
                private String value;
                private Locale currLocale;
                private StringBuffer buf;
                static final boolean $assertionsDisabled; /* synthetic field */



                throws SAXException
            {
                super(s, s1);
                name = null;
                value = null;
                currLocale = null;
                buf = new StringBuffer();
            }
            }
);
        }
        catch(IOException ioexception)
        {
            throw new SAXException("I/O error parsing: " + s2, ioexception);
        }
        catch(ParserConfigurationException parserconfigurationexception)
        {
            throw new SAXException("XML parser configuration error.", parserconfigurationexception);
        }
    }

    private static String getValue(String s)
        throws L10nException
    {
        if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            throw new AssertionError();
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        String s1 = (String)props.get(s);
        if(s1 == null)
            throw new L10nException("Missing L10n resource: " + s);
        else
            return s1;
    }

    private static void checkStarted()
        throws L10nException
    {
        if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            throw new AssertionError();
        if(!started)
        {
            String s = System.getProperty("XTIER_ROOT");
            if(s == null)
                throw new L10nException("xTier root property XTIER_ROOT is not specified. L10n service cannot be implicitly started without this property.");
            load(s, Locale.getDefault());
        }
    }

    public static String format(String s)
        throws L10nException
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        checkStarted();
        return MessageFormat.format(getValue(s), EMPTY_ARR);
        Exception exception;
        exception;
        throw exception;
    }

    public static String format(String s, Object aobj[])
        throws L10nException
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        checkStarted();
        return MessageFormat.format(getValue(s), aobj);
        Exception exception;
        exception;
        throw exception;
    }

    public static String format(String s, Object obj)
        throws L10nException
    {
        Object obj1 = mutex;
        JVM INSTR monitorenter ;
        checkStarted();
        return MessageFormat.format(getValue(s), new Object[] {
            obj
        });
        Exception exception;
        exception;
        throw exception;
    }

    public static String format(String s, Object obj, Object obj1)
        throws L10nException
    {
        Object obj2 = mutex;
        JVM INSTR monitorenter ;
        checkStarted();
        return MessageFormat.format(getValue(s), new Object[] {
            obj, obj1
        });
        Exception exception;
        exception;
        throw exception;
    }

    public static String format(String s, Object obj, Object obj1, Object obj2)
        throws L10nException
    {
        Object obj3 = mutex;
        JVM INSTR monitorenter ;
        checkStarted();
        return MessageFormat.format(getValue(s), new Object[] {
            obj, obj1, obj2
        });
        Exception exception;
        exception;
        throw exception;
    }

    public static String format(String s, Object obj, Object obj1, Object obj2, Object obj3)
        throws L10nException
    {
        Object obj4 = mutex;
        JVM INSTR monitorenter ;
        checkStarted();
        return MessageFormat.format(getValue(s), new Object[] {
            obj, obj1, obj2, obj3
        });
        Exception exception;
        exception;
        throw exception;
    }

    public static String format(String s, Object obj, Object obj1, Object obj2, Object obj3, Object obj4)
        throws L10nException
    {
        Object obj5 = mutex;
        JVM INSTR monitorenter ;
        checkStarted();
        return MessageFormat.format(getValue(s), new Object[] {
            obj, obj1, obj2, obj3, obj4
        });
        Exception exception;
        exception;
        throw exception;
    }

    private static final Object EMPTY_ARR[] = new Object[0];
    private static final String REGEX_TEXT = "^[A-Z0-9]+(\\.[A-Z0-9]+)*$";
    private static final Pattern NAME_REGEX = Pattern.compile("^[A-Z0-9]+(\\.[A-Z0-9]+)*$");
    private static final Object mutex = new Object();
    private static final Locale SUPPORTED_LOCALES[];
    private static boolean started = false;
    private static Map props = new HashMap();
    private static Set includes = new HashSet();
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(L10n.class).desiredAssertionStatus();
        SUPPORTED_LOCALES = (new Locale[] {
            Locale.US
        });
    }



}
