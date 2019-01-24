// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.utils.xml;

import com.fitechlabs.xtier.utils.Utils;
import java.io.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

// Referenced classes of package com.fitechlabs.xtier.utils.xml:
//            XmlLocation

public abstract class SaxAbstractHandler extends DefaultHandler
{

    public SaxAbstractHandler(String s, String s1)
    {
        loc = null;
        if(!$assertionsDisabled && (s == null || s1 == null))
        {
            throw new AssertionError();
        } else
        {
            root = s;
            dtd = s1;
            return;
        }
    }

    protected final String getRoot()
    {
        return root;
    }

    protected final String getDtd()
    {
        return dtd;
    }

    public void setDocumentLocator(Locator locator)
    {
        loc = locator;
    }

    protected final Locator getLocator()
    {
        return loc;
    }

    public InputSource resolveEntity(String s, String s1)
        throws SAXException
    {
        if(s1.endsWith(dtd))
        {
            String s2 = Utils.makeValidPath(root, "config/dtd/" + dtd);
            if((new File(s2)).exists())
                try
                {
                    return new InputSource(new FileInputStream(s2));
                }
                catch(FileNotFoundException filenotfoundexception)
                {
                    throw new SAXException(filenotfoundexception.getMessage(), filenotfoundexception);
                }
        }
        return null;
    }

    public void error(SAXParseException saxparseexception)
        throws SAXException
    {
        rethrow(saxparseexception);
    }

    public void fatalError(SAXParseException saxparseexception)
        throws SAXException
    {
        rethrow(saxparseexception);
    }

    public void warning(SAXParseException saxparseexception)
        throws SAXException
    {
        rethrow(saxparseexception);
    }

    private void rethrow(SAXParseException saxparseexception)
        throws SAXException
    {
        String s = saxparseexception.getMessage();
        String s1 = XmlLocation.toStrLocation(loc);
        SAXParseException saxparseexception1 = new SAXParseException((s1 != null ? s1 + ": " : "") + s, loc);
        saxparseexception1.initCause(saxparseexception.getCause());
        throw saxparseexception1;
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

    protected String root;
    private String dtd;
    private Locator loc;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(SaxAbstractHandler.class).desiredAssertionStatus();
    }
}
