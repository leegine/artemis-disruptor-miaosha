// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.utils.xml;

import com.fitechlabs.xtier.utils.Utils;
import java.io.*;
import org.xml.sax.*;

public class DomEntityResolver
    implements EntityResolver
{

    public DomEntityResolver(String s, String s1)
    {
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

    private String root;
    private String dtd;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(DomEntityResolver.class).desiredAssertionStatus();
    }
}
