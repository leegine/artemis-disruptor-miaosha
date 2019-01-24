// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.utils.xml;

import javax.xml.parsers.*;
import org.xml.sax.SAXException;

public class XmlUtils
{

    public XmlUtils()
    {
    }

    public static SAXParser makeSaxParser()
        throws SAXException, ParserConfigurationException
    {
        if(factory == null)
            factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(true);
        SAXParser saxparser = factory.newSAXParser();
        return saxparser;
    }

    private static SAXParserFactory factory = null;

}
