// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.kernel.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

// Referenced classes of package com.fitechlabs.xtier.kernel.xml:
//            XmlAppPropsException, XmlSysPropsException, XmlAppPropsManager, XmlSaxHandler,
//            XmlSysPropsResolver

public class XmlAttrInterceptor
{

    XmlAttrInterceptor(Attributes attributes, XmlSaxHandler xmlsaxhandler)
    {
        impl = null;
        handler = null;
        impl = attributes;
        handler = xmlsaxhandler;
    }

    public int getIndex(String s, String s1)
    {
        return impl.getIndex(s, s1);
    }

    public int getIndex(String s)
    {
        return getIndex(s);
    }

    public int getLength()
    {
        return impl.getLength();
    }

    public String getLocalName(int i)
    {
        return impl.getLocalName(i);
    }

    public String getQName(int i)
    {
        return impl.getQName(i);
    }

    public String getType(int i)
    {
        return impl.getType(i);
    }

    public String getType(String s, String s1)
    {
        return impl.getType(s, s1);
    }

    public String getType(String s)
    {
        return impl.getType(s);
    }

    public String getURI(int i)
    {
        return impl.getURI(i);
    }

    public String getValue(int i)
        throws SAXException
    {
        String s = impl.getValue(i);
        return s != null ? resolve(s) : null;
    }

    public String getValue(String s, String s1)
        throws SAXException
    {
        String s2 = impl.getValue(s, s1);
        return s2 != null ? resolve(s2) : null;
    }

    public String getValue(String s)
        throws SAXException
    {
        String s1 = impl.getValue(s);
        return s1 != null ? resolve(s1) : null;
    }

    private String resolve(String s)
        throws SAXException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        try
        {
            s = XmlAppPropsManager.resolve(s);
        }
        catch(XmlAppPropsException xmlapppropsexception)
        {
            throw handler.createSaxErr(xmlapppropsexception.getMessage(), xmlapppropsexception);
        }
        try
        {
            s = XmlSysPropsResolver.resolve(s);
        }
        catch(XmlSysPropsException xmlsyspropsexception)
        {
            throw handler.createSaxErr(xmlsyspropsexception.getMessage(), xmlsyspropsexception);
        }
        return s;
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

    private Attributes impl;
    private XmlSaxHandler handler;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(XmlAttrInterceptor.class).desiredAssertionStatus();
    }
}
