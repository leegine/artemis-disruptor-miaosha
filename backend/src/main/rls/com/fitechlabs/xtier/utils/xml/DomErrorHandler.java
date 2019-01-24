// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.utils.xml;

import org.xml.sax.*;

public class DomErrorHandler
    implements ErrorHandler
{

    public DomErrorHandler()
    {
    }

    public void error(SAXParseException saxparseexception)
        throws SAXException
    {
        throw saxparseexception;
    }

    public void fatalError(SAXParseException saxparseexception)
        throws SAXException
    {
        throw saxparseexception;
    }

    public void warning(SAXParseException saxparseexception)
        throws SAXException
    {
        throw saxparseexception;
    }
}
