// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.i18n.impl;

import com.fitechlabs.xtier.l10n.L10n;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.i18n.impl:
//            I18nProp

class I18nGroup
{

    I18nGroup(String s)
    {
        props = new HashMap();
        name = s;
    }

    Iterator getAllProps()
    {
        return props.values().iterator();
    }

    void addProp(I18nProp i18nprop)
    {
        props.put(i18nprop.getName(), i18nprop);
    }

    boolean containsProp(String s)
    {
        return props.containsKey(s);
    }

    public I18nProp getProp(String s)
    {
        return (I18nProp)props.get(s);
    }

    String getName()
    {
        return name;
    }

    public String toString()
    {
        return L10n.format("SRVC.I18N.TXT3", name);
    }

    private String name;
    private Map props;
}
