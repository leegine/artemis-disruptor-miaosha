// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.i18n.impl;

import com.fitechlabs.xtier.kernel.ioc.IocDescriptor;
import com.fitechlabs.xtier.kernel.ioc.IocDescriptorException;
import com.fitechlabs.xtier.l10n.L10n;

class I18nLocale
{

    I18nLocale(String s, String s1, String s2, boolean flag)
    {
        lang = s;
        cntry = s1;
        skin = s2;
        isIocLocale = flag;
        key = (s2 != null ? '\0' + s2 : "") + (s != null ? '\0' + s : "") + (s1 != null ? '\0' + s1 : "");
    }

    I18nLocale(String s, String s1, String s2)
    {
        this(s, s1, s2, false);
    }

    String getKey()
    {
        return key;
    }

    String getCountry()
    {
        return cntry;
    }

    String getLang()
    {
        return lang;
    }

    String getSkin()
    {
        return skin;
    }

    String getValue()
    {
        return value;
    }

    void setValue(String s)
    {
        if(!$assertionsDisabled && s == null)
        {
            throw new AssertionError();
        } else
        {
            value = s;
            return;
        }
    }

    Object getIocValue()
        throws IocDescriptorException
    {
        return ioc.createNewObj();
    }

    Object getIocValue(Object obj)
        throws IocDescriptorException
    {
        if(!$assertionsDisabled && obj == null)
            throw new AssertionError();
        else
            return ioc.createNewObj(obj);
    }

    void setIocDescriptor(IocDescriptor iocdescriptor)
    {
        if(!$assertionsDisabled && iocdescriptor == null)
        {
            throw new AssertionError();
        } else
        {
            ioc = iocdescriptor;
            return;
        }
    }

    boolean isIocLocale()
    {
        return isIocLocale;
    }

    public String toString()
    {
        return L10n.format("SRVC.I18N.TXT6", lang, cntry, skin, value);
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

    private String cntry;
    private String lang;
    private String skin;
    private String value;
    private IocDescriptor ioc;
    private String key;
    private boolean isIocLocale;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(I18nLocale.class).desiredAssertionStatus();
    }
}
