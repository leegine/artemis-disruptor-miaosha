// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.security;

import java.util.Map;

public interface SecurityRole
{

    public abstract String getName();

    public abstract Map getIdentities();

    public abstract Map getGrants();
}
