// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.cluster;

import java.util.Map;

public interface ClusterGroupMembership
{

    public abstract int getNodeId();

    public abstract String getGroupName();

    public abstract Map getGroupProps();
}
