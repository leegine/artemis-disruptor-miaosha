// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.i18n;

import com.fitechlabs.xtier.kernel.KernelService;
import java.util.List;
import java.util.Locale;

// Referenced classes of package com.fitechlabs.xtier.services.i18n:
//            I18nException, I18nChangeListener

public interface I18nService
    extends KernelService
{

    public abstract void reload()
        throws I18nException;

    public abstract void setLocale(Locale locale);

    public abstract void setSkin(String s);

    public abstract String getSkin();

    public abstract Locale getLocale();

    public abstract void addListener(I18nChangeListener i18nchangelistener);

    public abstract List getAllListeners();

    public abstract boolean removeListener(I18nChangeListener i18nchangelistener);

    public abstract String format(String s, String s1);

    public abstract String format(Locale locale, String s, String s1);

    public abstract String format(String s, String s1, Object obj);

    public abstract String format(Locale locale, String s, String s1, Object obj);

    public abstract String format(String s, String s1, Object obj, Object obj1);

    public abstract String format(Locale locale, String s, String s1, Object obj, Object obj1);

    public abstract String format(String s, String s1, Object obj, Object obj1, Object obj2);

    public abstract String format(Locale locale, String s, String s1, Object obj, Object obj1, Object obj2);

    public abstract String format(String s, String s1, Object obj, Object obj1, Object obj2, Object obj3);

    public abstract String format(Locale locale, String s, String s1, Object obj, Object obj1, Object obj2, Object obj3);

    public abstract String format(String s, String s1, Object obj, Object obj1, Object obj2, Object obj3, Object obj4);

    public abstract String format(Locale locale, String s, String s1, Object obj, Object obj1, Object obj2, Object obj3,
                                  Object obj4);

    public abstract String format(String s, String s1, Object aobj[]);

    public abstract String format(Locale locale, String s, String s1, Object aobj[]);

    public abstract Object getObject(String s, String s1);

    public abstract Object getObject(Locale locale, String s, String s1);

    public abstract Object getObject(String s, String s1, Object obj);

    public abstract Object getObject(Locale locale, String s, String s1, Object obj);
}
