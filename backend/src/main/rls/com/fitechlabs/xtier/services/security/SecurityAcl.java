// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.security;


// Referenced classes of package com.fitechlabs.xtier.services.security:
//            SecurityIdentity, SecurityResource

public interface SecurityAcl
{

    public abstract SecurityIdentity getIdentity();

    public abstract int getType();

    public abstract SecurityResource getResource();

    public abstract String getAction();

    public static final int ACL_GRANT = 1;
    public static final int ACL_DENY = 2;
}
