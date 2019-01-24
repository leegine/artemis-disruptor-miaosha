// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.jmx.impl;

import com.fitechlabs.xtier.kernel.spi.ServiceProviderAdapter;
import com.fitechlabs.xtier.microkernel.MicroKernelContext;
import com.fitechlabs.xtier.services.jmx.JmxService;
import javax.management.MBeanServer;

public class JmxServiceImpl extends ServiceProviderAdapter
    implements JmxService
{

    public JmxServiceImpl()
    {
        server = null;
    }

    protected void onStart()
    {
        server = getMicroKernelContext().getMBeanServer();
    }

    protected void onStop()
    {
        server = null;
    }

    public MBeanServer getMBeanServer()
    {
        if(!$assertionsDisabled && server == null)
            throw new AssertionError();
        else
            return server;
    }

    public String getName()
    {
        return "jmx";
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

    private MBeanServer server;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(JmxServiceImpl.class).desiredAssertionStatus();
    }
}
