// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.security.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.security.*;
import java.util.*;

class SecurityRoleImpl
    implements SecurityRole
{

    SecurityRoleImpl(String s)
    {
        ids = new HashMap();
        grants = new HashMap();
        if(!$assertionsDisabled && s == null)
        {
            throw new AssertionError();
        } else
        {
            name = s;
            return;
        }
    }

    public String getName()
    {
        return name;
    }

    public Map getGrants()
    {
        return Collections.unmodifiableMap(grants);
    }

    void addIdentity(SecurityIdentity securityidentity)
    {
        if(!$assertionsDisabled && securityidentity == null)
            throw new AssertionError();
        if(!$assertionsDisabled && ids.containsKey(securityidentity.getName()))
        {
            throw new AssertionError();
        } else
        {
            ids.put(securityidentity.getName(), securityidentity);
            return;
        }
    }

    void grant(SecurityResource securityresource, Set set)
    {
        if(!$assertionsDisabled && securityresource == null)
            throw new AssertionError();
        if(!$assertionsDisabled && set == null)
            throw new AssertionError();
        if(!$assertionsDisabled && grants.containsKey(securityresource.getName()))
        {
            throw new AssertionError();
        } else
        {
            grants.put(securityresource.getName(), set);
            return;
        }
    }

    boolean containsAction(String s, String s1)
    {
        Set set = (Set)grants.get(s);
        return set != null ? set.contains(s1) : false;
    }

    public Map getIdentities()
    {
        return Collections.unmodifiableMap(ids);
    }

    Map getIds()
    {
        return ids;
    }

    boolean hasAccess(String s, String s1)
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        if(!$assertionsDisabled && s1 == null)
        {
            throw new AssertionError();
        } else
        {
            Set set = (Set)grants.get(s);
            return set != null && set.contains(s1);
        }
    }

    boolean hasAccess(String s, String as[])
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        if(!$assertionsDisabled && as == null)
            throw new AssertionError();
        Set set = (Set)grants.get(s);
        if(set != null)
        {
            for(int i = 0; i < as.length; i++)
                if(!set.contains(as[i]))
                    return false;

        }
        return true;
    }

    public String toString()
    {
        return L10n.format("SRVC.SCRT.TXT3", getName());
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
    private Map ids;
    private Map grants;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(SecurityRoleImpl.class).desiredAssertionStatus();
    }
}
