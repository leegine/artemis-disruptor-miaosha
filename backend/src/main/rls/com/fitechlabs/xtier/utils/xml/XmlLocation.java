// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.utils.xml;

import java.io.Serializable;
import org.xml.sax.Locator;

public class XmlLocation
    implements Serializable
{

    public XmlLocation(Locator locator1)
    {
        locator = null;
        final String pubId = locator1.getPublicId();
        final String sysId = locator1.getSystemId();
        final int line = locator1.getLineNumber();
        final int col = locator1.getColumnNumber();
        locator = new Locator() {

            public String getPublicId()
            {
                return pubId;
            }

            public String getSystemId()
            {
                return sysId;
            }

            public int getLineNumber()
            {
                return line;
            }

            public int getColumnNumber()
            {
                return col;
            }


            {
                super();
            }
        }
;
    }

    public Locator getLocator()
    {
        return locator;
    }

    public static String toStrLocation(Locator locator1)
    {
        if(!$assertionsDisabled && locator1 == null)
            throw new AssertionError();
        int i = locator1.getLineNumber();
        int j = locator1.getColumnNumber();
        String s = locator1.getSystemId() != null ? locator1.getSystemId() : locator1.getPublicId();
        if(s == null)
            return null;
        if(i != -1 && j != -1)
            return s + '(' + i + ',' + j + ')';
        if(i != -1)
            return s + '(' + i + ')';
        else
            return s + "(...)";
    }

    public String toStrLocation()
    {
        if(!$assertionsDisabled && locator == null)
            throw new AssertionError("XML locator cannot be null.");
        else
            return toStrLocation(locator);
    }

    public String toString()
    {
        if(!$assertionsDisabled)
            throw new AssertionError();
        else
            return "XML location [locator=" + toStrLocation(locator) + ']';
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

    private Locator locator;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(XmlLocation.class).desiredAssertionStatus();
    }
}
