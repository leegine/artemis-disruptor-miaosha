// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.kernel.xml;

import com.fitechlabs.xtier.l10n.L10n;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Referenced classes of package com.fitechlabs.xtier.kernel.xml:
//            XmlSysPropsException

class XmlSysPropsResolver
{

    XmlSysPropsResolver()
    {
    }

    static String resolve(String s)
        throws XmlSysPropsException
    {
        StringBuffer stringbuffer = new StringBuffer(s.length());
        resolve(s, stringbuffer);
        return stringbuffer.toString();
    }

    static void resolve(String s, StringBuffer stringbuffer)
        throws XmlSysPropsException
    {
        if(!$assertionsDisabled && (s == null || stringbuffer == null))
            throw new AssertionError();
        Matcher matcher;
        for(matcher = regex.matcher(s); matcher.find();)
        {
            String s1 = matcher.group();
            if(s1.length() < 4)
                throw new XmlSysPropsException(L10n.format("KRNL.XML.ERR2", s));
            String s2 = s1.substring(2, s1.length() - 1);
            int i = s2.indexOf(':');
            if(i == -1)
            {
                s2 = s2.trim();
                String s3 = System.getProperty(s2);
                if(s3 == null)
                    throw new XmlSysPropsException(L10n.format("KRNL.XML.ERR3", s2));
                matcher.appendReplacement(stringbuffer, escapeBackSlash(resolve(s3)));
            } else
            {
                String s4 = System.getProperty(s2.substring(0, i).trim());
                if(s4 == null)
                    matcher.appendReplacement(stringbuffer, escapeBackSlash(s2.substring(i + 1).trim()));
                else
                    matcher.appendReplacement(stringbuffer, escapeBackSlash(resolve(s4)));
            }
        }

        matcher.appendTail(stringbuffer);
    }

    private static String escapeBackSlash(String s)
    {
        return s.replaceAll("\\\\", "\\\\\\\\");
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

    private static final Pattern regex = Pattern.compile("\\@\\{[^}:]+(:[^}]*)?\\}");
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(XmlSysPropsResolver.class).desiredAssertionStatus();
    }
}
