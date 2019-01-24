// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.kernel.xml;

import com.fitechlabs.xtier.l10n.L10n;

class XmlAppPropsException extends Exception
{

    XmlAppPropsException(String s)
    {
        super(s);
    }

    XmlAppPropsException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public String toString()
    {
        return L10n.format("KRNL.XML.TXT2", getMessage());
    }
}
