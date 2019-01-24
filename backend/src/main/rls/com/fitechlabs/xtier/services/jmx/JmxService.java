// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.jmx;

import com.fitechlabs.xtier.kernel.KernelService;
import javax.management.MBeanServer;

public interface JmxService
    extends KernelService
{

    public abstract MBeanServer getMBeanServer();
}
