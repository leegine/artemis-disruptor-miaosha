// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.security.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.security.SecurityAcl;
import com.fitechlabs.xtier.services.security.SecurityResource;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.security.impl:
//            SecurityIdentityImpl, SecurityResourceImpl, SecurityAclImpl, SecurityRoleImpl

class SecurityRealm
{

    SecurityRealm(String s)
    {
        roles = new HashMap();
        resources = new HashMap();
        ids = new HashMap();
        acls = new HashSet();
        name = s;
    }

    void addIdentity(SecurityIdentityImpl securityidentityimpl)
    {
        if(!$assertionsDisabled && securityidentityimpl == null)
            throw new AssertionError();
        if(!$assertionsDisabled && ids.containsKey(securityidentityimpl.getName()))
        {
            throw new AssertionError();
        } else
        {
            ids.put(securityidentityimpl.getName(), securityidentityimpl);
            return;
        }
    }

    void addResource(SecurityResourceImpl securityresourceimpl)
    {
        if(!$assertionsDisabled && securityresourceimpl == null)
            throw new AssertionError();
        if(!$assertionsDisabled && resources.containsKey(securityresourceimpl.getName()))
        {
            throw new AssertionError();
        } else
        {
            resources.put(securityresourceimpl.getName(), securityresourceimpl);
            return;
        }
    }

    void addRole(SecurityRoleImpl securityroleimpl)
    {
        if(!$assertionsDisabled && securityroleimpl == null)
            throw new AssertionError();
        if(!$assertionsDisabled && roles.containsKey(securityroleimpl.getName()))
        {
            throw new AssertionError();
        } else
        {
            roles.put(securityroleimpl.getName(), securityroleimpl);
            return;
        }
    }

    void addAcl(SecurityAclImpl securityaclimpl)
    {
        if(!$assertionsDisabled && securityaclimpl == null)
            throw new AssertionError();
        if(!$assertionsDisabled && acls.contains(securityaclimpl))
        {
            throw new AssertionError();
        } else
        {
            acls.add(securityaclimpl);
            return;
        }
    }

    SecurityIdentityImpl getIdentity(String s)
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        else
            return (SecurityIdentityImpl)ids.get(s);
    }

    SecurityResourceImpl getResource(String s)
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        else
            return (SecurityResourceImpl)resources.get(s);
    }

    Map getIdentities()
    {
        return ids;
    }

    Map getResources()
    {
        return resources;
    }

    Map getRoles()
    {
        return roles;
    }

    Set getAcls()
    {
        return acls;
    }

    boolean isInRole(String s, SecurityRoleImpl securityroleimpl)
    {
        for(Iterator iterator = securityroleimpl.getIds().values().iterator(); iterator.hasNext();)
            if(((SecurityIdentityImpl)iterator.next()).contains(s))
                return true;

        return false;
    }

    SecurityAcl getAcl(String s, String s1, String s2)
    {
        for(Iterator iterator = acls.iterator(); iterator.hasNext();)
        {
            SecurityAclImpl securityaclimpl = (SecurityAclImpl)iterator.next();
            if(securityaclimpl.getId().contains(s) && securityaclimpl.getResource().getName().equals(s1) && securityaclimpl.getAction().equals(s2))
                return securityaclimpl;
        }

        return null;
    }

    boolean isAllowed(String s, String s1, String as[])
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        if(!$assertionsDisabled && s1 == null)
            throw new AssertionError();
        if(!$assertionsDisabled && as == null)
            throw new AssertionError();
        SecurityIdentityImpl securityidentityimpl = (SecurityIdentityImpl)ids.get(s);
        if(securityidentityimpl == null)
            throw new IllegalArgumentException(L10n.format("SRVC.SCRT.ERR28", s));
        if(securityidentityimpl.isGroup())
            throw new IllegalArgumentException(L10n.format("SRVC.SCRT.ERR29", s));
        boolean flag = false;
        Iterator iterator = roles.values().iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            SecurityRoleImpl securityroleimpl = (SecurityRoleImpl)iterator.next();
            if(!isInRole(s, securityroleimpl) || !securityroleimpl.hasAccess(s1, as))
                continue;
            flag = true;
            break;
        } while(true);
        if(flag)
        {
            for(int i = 0; i < as.length; i++)
            {
                SecurityAcl securityacl = getAcl(s, s1, as[i]);
                if(securityacl != null && securityacl.getType() == 2)
                    return false;
            }

        } else
        {
            for(int j = 0; j < as.length; j++)
            {
                SecurityAcl securityacl1 = getAcl(s, s1, as[j]);
                if(securityacl1 == null || securityacl1.getType() == 2)
                    return false;
            }

        }
        return true;
    }

    boolean isAllowed(String s, String s1, String s2)
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        if(!$assertionsDisabled && s1 == null)
            throw new AssertionError();
        if(!$assertionsDisabled && s2 == null)
            throw new AssertionError();
        SecurityIdentityImpl securityidentityimpl = (SecurityIdentityImpl)ids.get(s);
        if(securityidentityimpl == null)
            throw new IllegalArgumentException(L10n.format("SRVC.SCRT.ERR28", s));
        if(securityidentityimpl.isGroup())
            throw new IllegalArgumentException(L10n.format("SRVC.SCRT.ERR29", s));
        boolean flag = false;
        Object obj = roles.values().iterator();
        do
        {
            if(!((Iterator) (obj)).hasNext())
                break;
            SecurityRoleImpl securityroleimpl = (SecurityRoleImpl)((Iterator) (obj)).next();
            if(!isInRole(s, securityroleimpl) || !securityroleimpl.hasAccess(s1, s2))
                continue;
            flag = true;
            break;
        } while(true);
        obj = getAcl(s, s1, s2);
        if(flag)
            return obj != null ? ((SecurityAcl) (obj)).getType() != 2 : true;
        else
            return obj != null ? ((SecurityAcl) (obj)).getType() == 1 : false;
    }

    public String toString()
    {
        return L10n.format("SRVC.SCRT.TXT2", name);
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
    private Map roles;
    private Map resources;
    private Map ids;
    private Set acls;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(SecurityRealm.class).desiredAssertionStatus();
    }
}
