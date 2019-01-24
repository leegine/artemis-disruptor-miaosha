// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.kernel.xml;

import com.fitechlabs.xtier.l10n.L10n;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Referenced classes of package com.fitechlabs.xtier.kernel.xml:
//            XmlAppPropsException

class XmlAppPropsManager
{

    XmlAppPropsManager()
    {
    }

    static String resolve(String s)
        throws XmlAppPropsException
    {
        StringBuffer stringbuffer = new StringBuffer(s.length());
        resolve(s, stringbuffer);
        return stringbuffer.toString();
    }

    static void resolve(String s, StringBuffer stringbuffer)
        throws XmlAppPropsException
    {
        if(!$assertionsDisabled && (s == null || stringbuffer == null))
            throw new AssertionError();
        Matcher matcher;
        String s3;
        for(matcher = regex.matcher(s); matcher.find(); matcher.appendReplacement(stringbuffer, escapeBackSlash(resolve(s3))))
        {
            String s1 = matcher.group();
            if(s1.length() < 4)
                throw new XmlAppPropsException(L10n.format("KRNL.XML.ERR4", s));
            String s2 = s1.substring(2, s1.length() - 1).trim();
            s3 = (String)props.get(s2);
            if(s3 == null)
                throw new XmlAppPropsException(L10n.format("KRNL.XML.ERR6", s2));
        }

        matcher.appendTail(stringbuffer);
    }

    static void addProp(String s, String s1)
    {
        if(!$assertionsDisabled && (s == null || s1 == null))
            throw new AssertionError();
        if(!$assertionsDisabled && props.containsKey(s))
        {
            throw new AssertionError();
        } else
        {
            props.put(s, s1);
            return;
        }
    }

    static boolean contains(String s)
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        else
            return props.containsKey(s);
    }

    static void enterServiceConfig(String s)
    {
        if(!$assertionsDisabled && s == null)
        {
            throw new AssertionError();
        } else
        {
            stack.push(s);
            return;
        }
    }

    static void leaveServiceConfig(String s)
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        if(!$assertionsDisabled && stack.size() <= 0)
            throw new AssertionError();
        String s1 = (String)stack.pop();
        if(!$assertionsDisabled && !s1.equals(s))
            throw new AssertionError();
        if(stack.size() == 0)
            props.clear();
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

    private static final Pattern regex = Pattern.compile("\\%\\{[^}]+\\}");
    private static Map props = new HashMap();
    private static Stack stack = new Stack();
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(XmlAppPropsManager.class).desiredAssertionStatus();
    }
}
