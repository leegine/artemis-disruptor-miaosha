// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.security.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.security.SecurityIdentity;
import java.util.*;

class SecurityIdentityImpl
    implements SecurityIdentity
{

    SecurityIdentityImpl(String s, boolean flag)
    {
        ids = null;
        users = null;
        groups = null;
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        name = s;
        isGroup = flag;
        if(flag)
            ids = new HashMap();
    }

    boolean contains(String s)
    {
        return getUsers().contains(s) || getGroups().contains(s);
    }

    boolean contains(SecurityIdentityImpl securityidentityimpl)
    {
        return contains(securityidentityimpl.getName());
    }

    private Set getUsers()
    {
        if(users == null)
        {
            users = new HashSet();
            if(isGroup)
            {
                for(Iterator iterator = ids.values().iterator(); iterator.hasNext();)
                {
                    SecurityIdentityImpl securityidentityimpl = (SecurityIdentityImpl)iterator.next();
                    if(!securityidentityimpl.isGroup())
                        users.add(securityidentityimpl.getName());
                    else
                        users.addAll(securityidentityimpl.getUsers());
                }

            } else
            {
                users.add(name);
            }
        }
        return users;
    }

    private Set getGroups()
    {
        if(groups == null)
        {
            groups = new HashSet();
            if(isGroup)
            {
                groups.add(name);
                Iterator iterator = ids.values().iterator();
                do
                {
                    if(!iterator.hasNext())
                        break;
                    SecurityIdentityImpl securityidentityimpl = (SecurityIdentityImpl)iterator.next();
                    if(securityidentityimpl.isGroup())
                    {
                        groups.add(securityidentityimpl.getName());
                        groups.addAll(securityidentityimpl.getGroups());
                    }
                } while(true);
            }
        }
        return groups;
    }

    Map getIds()
    {
        return isGroup ? ids : null;
    }

    public String getName()
    {
        return name;
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

    public boolean isGroup()
    {
        return isGroup;
    }

    public Map getIdentities()
    {
        return isGroup ? Collections.unmodifiableMap(ids) : null;
    }

    public String toString()
    {
        return L10n.format("SRVC.SCRT.TXT1", getName(), Boolean.toString(isGroup));
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
    private boolean isGroup;
    private Map ids;
    private Set users;
    private Set groups;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(SecurityIdentityImpl.class).desiredAssertionStatus();
    }
}
