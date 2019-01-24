// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.security.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.security.SecurityResource;
import com.fitechlabs.xtier.utils.Utils;
import java.util.Collections;
import java.util.Set;

class SecurityResourceImpl
    implements SecurityResource
{

    SecurityResourceImpl(String s, Set set)
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        if(!$assertionsDisabled && set == null)
        {
            throw new AssertionError();
        } else
        {
            name = s;
            actions = Collections.unmodifiableSet(set);
            return;
        }
    }

    public String getName()
    {
        return name;
    }

    public Set getActions()
    {
        return actions;
    }

    public String toString()
    {
        return L10n.format("SRVC.SCRT.TXT4", getName(), Utils.coll2Str(actions));
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

    private String name;
    private Set actions;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(SecurityResourceImpl.class).desiredAssertionStatus();
    }
}
