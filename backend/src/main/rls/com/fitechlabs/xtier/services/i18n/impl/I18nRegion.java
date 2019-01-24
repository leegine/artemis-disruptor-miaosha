// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.i18n.impl;

import com.fitechlabs.xtier.l10n.L10n;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.i18n.impl:
//            I18nGroup

class I18nRegion
{

    I18nRegion(String s)
    {
        groups = new HashMap();
        name = s;
    }

    String getName()
    {
        return name;
    }

    Iterator getAllGroups()
    {
        return groups.values().iterator();
    }

    boolean containsGroup(String s)
    {
        return groups.containsKey(s);
    }

    void addGroup(I18nGroup i18ngroup)
    {
        if(!$assertionsDisabled && i18ngroup == null)
        {
            throw new AssertionError();
        } else
        {
            groups.put(i18ngroup.getName(), i18ngroup);
            return;
        }
    }

    I18nGroup getGroup(String s)
    {
        return (I18nGroup)groups.get(s);
    }

    long getReloadMsec()
    {
        return msec;
    }

    boolean isReload()
    {
        return reload;
    }

    void setReloadMsec(long l)
    {
        msec = l;
    }

    void setIsReload(boolean flag)
    {
        reload = flag;
    }

    public String toString()
    {
        return L10n.format("SRVC.I18N.TXT5", name);
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

    private Map groups;
    private String name;
    private boolean reload;
    private long msec;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(I18nRegion.class).desiredAssertionStatus();
    }
}
