// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.kernel.xml;

import com.fitechlabs.xtier.kernel.ioc.*;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.utils.Utils;
import com.fitechlabs.xtier.utils.UtilsException;
import com.fitechlabs.xtier.utils.xml.SaxAbstractHandler;
import com.fitechlabs.xtier.utils.xml.XmlLocation;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.*;
import java.util.*;
import org.xml.sax.*;

// Referenced classes of package com.fitechlabs.xtier.kernel.xml:
//            XmlAppPropsException, XmlSysPropsException, XmlAttrInterceptor, XmlSaxCallStack,
//            XmlAppPropsManager, XmlSysPropsResolver

public abstract class XmlSaxHandler extends SaxAbstractHandler
{

    public XmlSaxHandler(String s, String s1)
    {
        super(s, s1);
        buf = new StringBuffer();
        ioc = null;
        call = null;
        globalRefMap = new HashMap();
        regionRefMap = new HashMap();
        globalIocBuf = new ArrayList();
        regionIocBuf = new ArrayList();
        isGlobal = true;
        javaEnv = false;
        propName = null;
        propValue = null;
        isArgNull = false;
        isArgBoxed = false;
        argType = null;
        argRefUid = null;
        argConst = null;
    }

    public void startDocument()
    {
        XmlSaxHandler xmlsaxhandler = XmlSaxCallStack.peek();
        if(xmlsaxhandler != null)
        {
            setGlobalIocBuf(xmlsaxhandler.getGlobalIocBuf());
            setGlobalRefMap(xmlsaxhandler.getGlobalRefMap());
        }
        XmlSaxCallStack.push(this);
    }

    public final void characters(char ac[], int i, int j)
    {
        buf.append(ac, i, j);
    }

    protected final String getPcdata()
        throws SAXException
    {
        String s = buf.toString();
        buf.setLength(0);
        try
        {
            s = XmlAppPropsManager.resolve(s);
        }
        catch(XmlAppPropsException xmlapppropsexception)
        {
            throw createSaxErr(xmlapppropsexception.getMessage(), xmlapppropsexception);
        }
        try
        {
            s = XmlSysPropsResolver.resolve(s);
        }
        catch(XmlSysPropsException xmlsyspropsexception)
        {
            throw createSaxErr(xmlsyspropsexception.getMessage(), xmlsyspropsexception);
        }
        return s;
    }

    protected final void resetPcdata()
    {
        buf.setLength(0);
    }

    public void endDocument()
    {
        XmlSaxHandler xmlsaxhandler = XmlSaxCallStack.pop();
        if(!$assertionsDisabled && xmlsaxhandler != this)
            throw new AssertionError();
        XmlSaxHandler xmlsaxhandler1 = XmlSaxCallStack.peek();
        if(xmlsaxhandler1 != null)
        {
            xmlsaxhandler1.setGlobalIocBuf(getGlobalIocBuf());
            xmlsaxhandler1.setGlobalRefMap(getGlobalRefMap());
        }
    }

    void setGlobalIocBuf(List list)
    {
        if(!$assertionsDisabled && list == null)
        {
            throw new AssertionError();
        } else
        {
            globalIocBuf.clear();
            globalIocBuf.addAll(list);
            return;
        }
    }

    void setGlobalRefMap(Map map)
    {
        if(!$assertionsDisabled && map == null)
        {
            throw new AssertionError();
        } else
        {
            globalRefMap.clear();
            globalRefMap.putAll(map);
            return;
        }
    }

    List getGlobalIocBuf()
    {
        return globalIocBuf;
    }

    Map getGlobalRefMap()
    {
        return globalRefMap;
    }

    private void resetForRegion()
    {
        ioc = null;
        call = null;
        isArgNull = false;
        isArgBoxed = false;
        argType = null;
        argRefUid = null;
        argConst = null;
        regionRefMap.clear();
        regionIocBuf.clear();
    }

    protected final SAXParseException createSaxErr(String s)
    {
        Locator locator = getLocator();
        String s1 = XmlLocation.toStrLocation(locator);
        return new SAXParseException((s1 != null ? s1 + ": " : "") + s, locator);
    }

    protected final SAXParseException createSaxErr(String s, Exception exception)
    {
        Locator locator = getLocator();
        String s1 = XmlLocation.toStrLocation(locator);
        SAXParseException saxparseexception = new SAXParseException((s1 != null ? s1 + ": " : "") + s, locator);
        saxparseexception.initCause(exception);
        return saxparseexception;
    }

    public final InputSource resolveEntity(String s, String s1)
        throws SAXException
    {
        InputSource inputsource = super.resolveEntity(s, s1);
        if(inputsource == null && s1.endsWith(".dtd"))
        {
            int i = s1.lastIndexOf('/');
            if(i == -1)
                i = s1.lastIndexOf('\\');
            String s2 = i != -1 ? s1.substring(i) : s1;
            String s3 = Utils.makeValidPath(getRoot(), "config/dtd/" + s2);
            if((new File(s3)).exists())
                try
                {
                    inputsource = new InputSource(new FileInputStream(s3));
                }
                catch(FileNotFoundException filenotfoundexception)
                {
                    throw new SAXException(L10n.format("KRNL.XML.ERR1", s3));
                }
        }
        return inputsource;
    }

    protected abstract void onTagStart(String s, XmlAttrInterceptor xmlattrinterceptor)
        throws SAXException;

    protected abstract void onTagEnd(String s)
        throws SAXException;

    protected void onIocDescriptor(IocDescriptor iocdescriptor)
        throws SAXException
    {
    }

    private int getPolicy(String s)
    {
        if(s.equals("new"))
            return 2;
        if(s.equals("keyed"))
            return 3;
        if(s.equals("singleton"))
            return 1;
        if(!$assertionsDisabled)
            throw new AssertionError("Invalid policy: " + s);
        else
            return -1;
    }

    public final void startElement(String s, String s1, String s2, Attributes attributes)
        throws SAXException
    {
        String s3 = s1.length() <= 0 ? s2 : s1;
        XmlAttrInterceptor xmlattrinterceptor = new XmlAttrInterceptor(attributes, this);
        if(s3.equals("ioc"))
        {
            if(!$assertionsDisabled && ioc != null)
                throw new AssertionError();
            ioc = new IocDescriptor();
            ioc.setLocator(getLocator());
            String s4 = xmlattrinterceptor.getValue("uid");
            if(s4 != null)
                ioc.setUid(s4);
            String s6 = xmlattrinterceptor.getValue("ref-uid");
            String s7 = xmlattrinterceptor.getValue("policy");
            if(s6 != null)
            {
                if(s7 != null)
                    throw createSaxErr(L10n.format("KRNL.IOC.ERR16", s6));
                ioc.setIocRefUid(s6);
            } else
            {
                if(s7 == null)
                    throw createSaxErr(L10n.format("KRNL.IOC.ERR21", s4));
                ioc.setPolicy(getPolicy(s7));
            }
        } else
        if(s3.equals("call"))
        {
            if(javaEnv)
                call = new IocCall(xmlattrinterceptor.getValue("method"));
        } else
        if(s3.equals("java"))
        {
            javaEnv = true;
            ioc.setJavaImpl(xmlattrinterceptor.getValue("class"));
            ioc.setFactoryUid(xmlattrinterceptor.getValue("factory-uid"));
            ioc.setFactoryMethod(xmlattrinterceptor.getValue("method"));
        } else
        if(!s3.equals("clr"))
            if(s3.equals("arg"))
            {
                if(javaEnv)
                {
                    isArgNull = parseBoolean(xmlattrinterceptor.getValue("null"));
                    isArgBoxed = parseBoolean(xmlattrinterceptor.getValue("boxed"));
                    argRefUid = xmlattrinterceptor.getValue("ref-uid");
                    argConst = xmlattrinterceptor.getValue("const");
                    String s5 = xmlattrinterceptor.getValue("type");
                    argType = s5 != null ? getTypeClass(s5, isArgBoxed) : null;
                }
            } else
            if(!s3.equals("ctor"))
                if(s3.equals("property"))
                {
                    propName = xmlattrinterceptor.getValue("name");
                    if(!$assertionsDisabled && propName == null)
                        throw new AssertionError();
                    if(XmlAppPropsManager.contains(propName))
                        throw createSaxErr(L10n.format("KRNL.IOC.ERR23", propName));
                    propValue = xmlattrinterceptor.getValue("value");
                } else
                {
                    try
                    {
                        if(s3.equals("region"))
                            isGlobal = false;
                        if(s3.startsWith("xtier-"))
                            XmlAppPropsManager.enterServiceConfig(s3);
                        onTagStart(s3, xmlattrinterceptor);
                    }
                    catch(RuntimeException runtimeexception)
                    {
                        throw runtimeexception;
                    }
                }
    }

    private String attrs2Str(XmlAttrInterceptor xmlattrinterceptor)
        throws SAXException
    {
        StringBuffer stringbuffer = new StringBuffer();
        int i = xmlattrinterceptor.getLength();
        for(int j = 0; j < i; j++)
        {
            if(j == 0)
                stringbuffer.append(' ');
            stringbuffer.append(xmlattrinterceptor.getLocalName(j));
            stringbuffer.append("=\"");
            stringbuffer.append(xmlattrinterceptor.getValue(j));
            stringbuffer.append('"');
            if(j < i - 1)
                stringbuffer.append(", ");
        }

        return stringbuffer.toString();
    }

    private Object makeObjValue(Class class1, String s)
        throws SAXParseException
    {
        if(!$assertionsDisabled && class1 == null)
            throw new AssertionError();
        if(class1 == Integer.TYPE || class1 == (Integer.class))
            return new Integer(parseInt(s));
        if(class1 == Byte.TYPE || class1 == (Byte.class))
            return new Byte(parseByte(s));
        if(class1 == Short.TYPE || class1 == (Short.class))
            return new Short(parseShort(s));
        if(class1 == Long.TYPE || class1 == (Long.class))
            return new Long(parseLong(s));
        if(class1 == Character.TYPE || class1 == (Character.class))
            return new Character(parseChar(s));
        if(class1 == Boolean.TYPE || class1 == (Boolean.class))
            return new Boolean(parseBoolean(s));
        if(class1 == Float.TYPE || class1 == (Float.class))
            return new Float(parseFloat(s));
        if(class1 == Double.TYPE || class1 == (Double.class))
            return new Double(parseDouble(s));
        if(class1 == (String.class))
            return s;
        if(class1 == (Date.class))
            return parseDate(s);
        if(!$assertionsDisabled)
            throw new AssertionError("Unknown type: " + class1);
        else
            return null;
    }

    private Class getTypeClass(String s, boolean flag)
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        if(s.equals("int8"))
            return !flag ? Byte.TYPE : Byte.class;
        if(s.equals("int16"))
            return !flag ? Short.TYPE : Short.class;
        if(s.equals("int32"))
            return !flag ? Integer.TYPE : Integer.class;
        if(s.equals("int64"))
            return !flag ? Long.TYPE : Long.class;
        if(s.equals("float32"))
            return !flag ? Float.TYPE : Float.class;
        if(s.equals("float64"))
            return !flag ? Double.TYPE : Double.class;
        if(s.equals("char"))
            return !flag ? Character.TYPE : Character.class;
        if(s.equals("boolean"))
            return !flag ? Boolean.TYPE : Boolean.class;
        if(s.equals("string"))
            return String.class;
        if(s.equals("date"))
            return Date.class;
        if(!$assertionsDisabled)
            throw new AssertionError("Unknown type: " + s);
        else
            return null;
    }

    public final void endElement(String s, String s1, String s2)
        throws SAXException
    {
        String s3 = s1.length() <= 0 ? s2 : s1;
        if(s3.equals("ioc"))
        {
            if(!$assertionsDisabled && ioc == null)
                throw new AssertionError();
            String s4 = ioc.getUid();
            if(isGlobal)
            {
                globalIocBuf.add(ioc);
                HashMap hashmap = new HashMap(globalRefMap);
                if(s4 != null)
                {
                    if(globalRefMap.containsKey(s4))
                        throw createSaxErr(L10n.format("KRNL.IOC.ERR14", s4));
                    globalRefMap.put(s4, ioc);
                    int i = globalIocBuf.size();
                    for(int l = 0; l < i; l++)
                        ((IocDescriptor)globalIocBuf.get(l)).setRefMap(hashmap);

                } else
                {
                    ioc.setRefMap(hashmap);
                }
            } else
            {
                regionIocBuf.add(ioc);
                HashMap hashmap1 = new HashMap(regionRefMap);
                hashmap1.putAll(globalRefMap);
                if(s4 != null)
                {
                    if(regionRefMap.containsKey(s4) || globalRefMap.containsKey(s4))
                        throw createSaxErr(L10n.format("KRNL.IOC.ERR14", s4));
                    regionRefMap.put(s4, ioc);
                    int j = regionIocBuf.size();
                    hashmap1.put(s4, ioc);
                    for(int i1 = 0; i1 < j; i1++)
                        ((IocDescriptor)regionIocBuf.get(i1)).setRefMap(hashmap1);

                } else
                {
                    ioc.setRefMap(hashmap1);
                }
            }
            if(!isGlobal)
                try
                {
                    onIocDescriptor(ioc);
                }
                catch(RuntimeException runtimeexception1)
                {
                    throw runtimeexception1;
                }
            ioc = null;
        } else
        if(s3.equals("java"))
        {
            javaEnv = false;
            if(ioc.getJavaImpl() != null)
            {
                if(ioc.getFactoryUid() != null || ioc.getFactoryMethod() != null)
                    throw createSaxErr(L10n.format("KRNL.IOC.ERR25"));
            } else
            if(ioc.getFactoryUid() == null || ioc.getFactoryMethod() == null)
                throw createSaxErr(L10n.format("KRNL.IOC.ERR26"));
        } else
        if(!s3.equals("clr"))
            if(s3.equals("arg"))
            {
                if(javaEnv)
                {
                    IocArg iocarg = new IocArg();
                    String s5 = getPcdata();
                    if(argConst != null)
                    {
                        if(s5.length() > 0 || argRefUid != null || argType != null || isArgBoxed || isArgNull)
                            throw createSaxErr(L10n.format("KRNL.IOC.ERR27"));
                        int k = argConst.indexOf('#');
                        if(k == -1)
                            throw createSaxErr(L10n.format("KRNL.IOC.ERR28", argConst));
                        String s6 = argConst.substring(0, k);
                        String s7 = argConst.substring(k + 1);
                        if(s6.length() == 0 || s7.length() == 0)
                            throw createSaxErr(L10n.format("KRNL.IOC.ERR28", argConst));
                        try
                        {
                            Field field = Class.forName(s6).getField(s7);
                            if(!Modifier.isStatic(field.getModifiers()))
                                throw createSaxErr(L10n.format("KRNL.IOC.ERR29", argConst));
                            iocarg.setType(field.getType());
                            iocarg.setValue(field.get(null));
                        }
                        catch(Exception exception)
                        {
                            throw createSaxErr(L10n.format("KRNL.IOC.ERR30", argConst), exception);
                        }
                    } else
                    if(argRefUid != null)
                    {
                        if(argType != null || s5.length() > 0 || isArgBoxed)
                            throw createSaxErr(L10n.format("KRNL.IOC.ERR12"));
                        if(isArgNull)
                        {
                            iocarg.setType(null);
                            iocarg.setValue(null);
                        } else
                        {
                            iocarg.setRefUid(argRefUid);
                        }
                    } else
                    if(isArgNull)
                    {
                        iocarg.setType(null);
                        iocarg.setValue(null);
                    } else
                    {
                        if(argType == null)
                            throw createSaxErr(L10n.format("KRNL.IOC.ERR13"));
                        iocarg.setType(argType);
                        iocarg.setValue(makeObjValue(argType, s5));
                    }
                    if(call != null)
                        call.addArg(iocarg);
                    else
                        ioc.addCtorArg(iocarg);
                    iocarg = null;
                } else
                {
                    resetPcdata();
                }
            } else
            if(!s3.equals("ctor"))
                if(s3.equals("call"))
                {
                    if(javaEnv)
                    {
                        ioc.addCall(call);
                        call = null;
                    }
                } else
                if(s3.equals("property"))
                {
                    if(propValue == null)
                        propValue = getPcdata();
                    XmlAppPropsManager.addProp(propName, propValue);
                    propName = null;
                    propValue = null;
                } else
                {
                    if(s3.equals("region"))
                    {
                        resetForRegion();
                        isGlobal = true;
                    }
                    if(s3.startsWith("xtier-"))
                        XmlAppPropsManager.leaveServiceConfig(s3);
                    try
                    {
                        onTagEnd(s3);
                    }
                    catch(RuntimeException runtimeexception)
                    {
                        throw runtimeexception;
                    }
                }
    }

    protected final int parseInt(String s)
        throws SAXParseException
    {
        try
        {
            return Utils.parseInt(s);
        }
        catch(UtilsException utilsexception)
        {
            throw createSaxErr(L10n.format("KRNL.SAX.ERR1", s), utilsexception);
        }
    }

    protected final byte parseByte(String s)
        throws SAXParseException
    {
        try
        {
            return Utils.parseByte(s);
        }
        catch(UtilsException utilsexception)
        {
            throw createSaxErr(L10n.format("KRNL.SAX.ERR1", s), utilsexception);
        }
    }

    protected final short parseShort(String s)
        throws SAXParseException
    {
        try
        {
            return Utils.parseShort(s);
        }
        catch(UtilsException utilsexception)
        {
            throw createSaxErr(L10n.format("KRNL.SAX.ERR1", s), utilsexception);
        }
    }

    protected final long parseLong(String s)
        throws SAXParseException
    {
        try
        {
            return Utils.parseLong(s);
        }
        catch(UtilsException utilsexception)
        {
            throw createSaxErr(L10n.format("KRNL.SAX.ERR1", s), utilsexception);
        }
    }

    protected final float parseFloat(String s)
        throws SAXParseException
    {
        try
        {
            return Utils.parseFloat(s);
        }
        catch(UtilsException utilsexception)
        {
            throw createSaxErr(L10n.format("KRNL.SAX.ERR1", s), utilsexception);
        }
    }

    protected final double parseDouble(String s)
        throws SAXParseException
    {
        try
        {
            return Utils.parseDouble(s);
        }
        catch(UtilsException utilsexception)
        {
            throw createSaxErr(L10n.format("KRNL.SAX.ERR1", s), utilsexception);
        }
    }

    protected final boolean parseBoolean(String s)
        throws SAXParseException
    {
        try
        {
            return Utils.parseBoolean(s);
        }
        catch(UtilsException utilsexception)
        {
            throw createSaxErr(L10n.format("KRNL.SAX.ERR1", s), utilsexception);
        }
    }

    protected final char parseChar(String s)
        throws SAXParseException
    {
        try
        {
            return Utils.parseChar(s);
        }
        catch(UtilsException utilsexception)
        {
            throw createSaxErr(L10n.format("KRNL.SAX.ERR1", s), utilsexception);
        }
    }

    protected final Date parseDate(String s)
        throws SAXParseException
    {
        try
        {
            return Utils.parseDate(s);
        }
        catch(UtilsException utilsexception)
        {
            throw createSaxErr(L10n.format("KRNL.SAX.ERR1", s), utilsexception);
        }
    }

    protected final Date parseTime(String s)
        throws SAXParseException
    {
        try
        {
            return Utils.parseTime(s);
        }
        catch(UtilsException utilsexception)
        {
            throw createSaxErr(L10n.format("KRNL.SAX.ERR1", s), utilsexception);
        }
    }

    protected final int parseIpPort(String s)
        throws SAXParseException
    {
        try
        {
            return Utils.parseIpPort(s);
        }
        catch(UtilsException utilsexception)
        {
            throw createSaxErr(L10n.format("KRNL.SAX.ERR1", s), utilsexception);
        }
    }

    protected final Inet6Address parseIpV6(String s)
        throws SAXParseException
    {
        try
        {
            return Utils.parseIpV6(s);
        }
        catch(UtilsException utilsexception)
        {
            throw createSaxErr(L10n.format("KRNL.SAX.ERR1", s), utilsexception);
        }
    }

    protected final InetAddress parseIp(String s)
        throws SAXParseException
    {
        try
        {
            return Utils.parseIp(s);
        }
        catch(UtilsException utilsexception)
        {
            throw createSaxErr(L10n.format("KRNL.SAX.ERR1", s), utilsexception);
        }
    }

    protected final Inet4Address parseIpV4(String s)
        throws SAXParseException
    {
        try
        {
            return Utils.parseIpV4(s);
        }
        catch(UtilsException utilsexception)
        {
            throw createSaxErr(L10n.format("KRNL.SAX.ERR1", s), utilsexception);
        }
    }

    private StringBuffer buf;
    private IocDescriptor ioc;
    private IocCall call;
    private Map globalRefMap;
    private Map regionRefMap;
    private List globalIocBuf;
    private List regionIocBuf;
    private boolean isGlobal;
    private boolean javaEnv;
    private String propName;
    private String propValue;
    private boolean isArgNull;
    private boolean isArgBoxed;
    private Class argType;
    private String argRefUid;
    private String argConst;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(XmlSaxHandler.class).desiredAssertionStatus();
    }
}
