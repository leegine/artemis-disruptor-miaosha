// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.cluster;

import java.net.InetSocketAddress;

public interface ClusterErrorListener
{

    public abstract void onUnknownNode(InetSocketAddress inetsocketaddress);
}
