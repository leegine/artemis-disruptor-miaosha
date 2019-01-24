// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.i18n.impl;

import com.fitechlabs.xtier.l10n.L10n;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.i18n.impl:
//            I18nLocale

class I18nProp
{

    I18nProp(String s)
    {
        locales = new HashMap(2);
        name = s;
    }

    Iterator getAllLocales()
    {
        return locales.values().iterator();
    }

    void addLocale(I18nLocale i18nlocale)
    {
        locales.put(i18nlocale.getKey(), i18nlocale);
    }

    boolean contains(I18nLocale i18nlocale)
    {
        return locales.containsKey(i18nlocale.getKey());
    }

    I18nLocale findLocale(String s, String s1, String s2)
    {
        String as[] = null;
        if(s2 == null)
            as = (new String[] {
                '\0' + s + '\0' + s1, '\0' + s, '\0' + s1, ""
            });
        else
            as = (new String[] {
                '\0' + s2 + '\0' + s + '\0' + s1, '\0' + s2 + '\0' + s, '\0' + s2 + '\0' + s1, '\0' + s2, '\0' + s + '\0' + s1, '\0' + s, '\0' + s1, ""
            });
        for(int i = 0; i < as.length; i++)
        {
            I18nLocale i18nlocale = (I18nLocale)locales.get(as[i]);
            if(i18nlocale != null)
                return i18nlocale;
        }

        return null;
    }

    I18nLocale findLocale(Locale locale, String s)
    {
        return findLocale(locale.getLanguage(), locale.getCountry(), s);
    }

    String getName()
    {
        return name;
    }

    public String toString()
    {
        return L10n.format("SRVC.I18N.TXT4", name);
    }

    private String name;
    private Map locales;
}
