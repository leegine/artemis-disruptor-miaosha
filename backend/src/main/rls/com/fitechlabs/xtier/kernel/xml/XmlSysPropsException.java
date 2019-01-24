// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.kernel.xml;

import com.fitechlabs.xtier.l10n.L10n;

class XmlSysPropsException extends Exception
{

    XmlSysPropsException(String s)
    {
        super(s);
    }

    XmlSysPropsException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public String toString()
    {
        return L10n.format("KRNL.XML.TXT1", getMessage());
    }
}
