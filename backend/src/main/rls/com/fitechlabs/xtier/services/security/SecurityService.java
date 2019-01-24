// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.security;

import com.fitechlabs.xtier.kernel.KernelService;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.security:
//            SecurityConfigException, SecurityChangeListener

public interface SecurityService
    extends KernelService
{

    public abstract void reload()
        throws SecurityConfigException;

    public abstract Map getAllResources();

    public abstract Map getAllIdentities();

    public abstract Map getAllRoles();

    public abstract Set getAllAcls();

    public abstract boolean checkAccess(String s, String s1, String s2);

    public abstract boolean checkAccess(String s, String s1, String as[]);

    public abstract void addListener(SecurityChangeListener securitychangelistener);

    public abstract List getAllListeners();

    public abstract boolean removeListener(SecurityChangeListener securitychangelistener);
}
