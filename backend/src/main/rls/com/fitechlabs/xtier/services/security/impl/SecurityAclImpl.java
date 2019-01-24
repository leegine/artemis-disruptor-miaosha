// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.security.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.security.*;

// Referenced classes of package com.fitechlabs.xtier.services.security.impl:
//            SecurityIdentityImpl, SecurityResourceImpl

public class SecurityAclImpl
    implements SecurityAcl
{

    SecurityAclImpl(int i, SecurityIdentityImpl securityidentityimpl, SecurityResourceImpl securityresourceimpl, String s)
    {
        type = i;
        id = securityidentityimpl;
        rs = securityresourceimpl;
        action = s;
    }

    SecurityIdentityImpl getId()
    {
        return id;
    }

    public String getAction()
    {
        return action;
    }

    public SecurityIdentity getIdentity()
    {
        return id;
    }

    public SecurityResource getResource()
    {
        return rs;
    }

    public int getType()
    {
        return type;
    }

    public String toString()
    {
        String s = type != 2 ? "GRANT" : "DENY";
        return L10n.format("SRVC.SCRT.TXT11", s, id, rs, action);
    }

    private int type;
    private SecurityIdentityImpl id;
    private SecurityResourceImpl rs;
    private String action;
}
