// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.security;

import java.util.Map;

public interface SecurityIdentity
{

    public abstract String getName();

    public abstract boolean isGroup();

    public abstract Map getIdentities();
}