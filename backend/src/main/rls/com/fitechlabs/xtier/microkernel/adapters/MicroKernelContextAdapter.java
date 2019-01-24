// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.microkernel.adapters;

import com.fitechlabs.xtier.microkernel.*;
import java.util.Locale;
import javax.management.MBeanServer;

public class MicroKernelContextAdapter
    implements MicroKernelContext
{

    public MicroKernelContextAdapter(int i, MBeanServer mbeanserver, String s, String s1, Locale locale1, MicroKernelLogger microkernellogger, String s2,
            int j)
    {
        jmx = null;
        root = null;
        appRegion = null;
        locale = null;
        log = null;
        localBind = null;
        rmiRegPort = 10991;
        if(!$assertionsDisabled && mbeanserver == null)
            throw new AssertionError();
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        if(!$assertionsDisabled && s1 == null)
            throw new AssertionError();
        if(!$assertionsDisabled && microkernellogger == null)
        {
            throw new AssertionError();
        } else
        {
            hid = i;
            jmx = mbeanserver;
            root = s;
            appRegion = s1;
            locale = locale1;
            log = microkernellogger;
            localBind = s2;
            rmiRegPort = j;
            return;
        }
    }

    public MicroKernelContextAdapter(int i, MBeanServer mbeanserver, String s, String s1, MicroKernelLogger microkernellogger)
    {
        jmx = null;
        root = null;
        appRegion = null;
        locale = null;
        log = null;
        localBind = null;
        rmiRegPort = 10991;
        if(!$assertionsDisabled && (mbeanserver == null || s == null || s1 == null || microkernellogger == null))
        {
            throw new AssertionError();
        } else
        {
            hid = i;
            jmx = mbeanserver;
            root = s;
            appRegion = s1;
            log = microkernellogger;
            return;
        }
    }

    public MicroKernelLogger getLogger()
    {
        if(!$assertionsDisabled && log == null)
            throw new AssertionError();
        else
            return log;
    }

    public MBeanServer getMBeanServer()
    {
        if(!$assertionsDisabled && jmx == null)
            throw new AssertionError();
        else
            return jmx;
    }

    public int getHostEnvId()
    {
        return hid;
    }

    public Locale getLocale()
    {
        return locale;
    }

    public int getRmiRegPort()
    {
        return rmiRegPort;
    }

    public String getLocalBindHost()
    {
        return localBind;
    }

    public String getXtierRoot()
    {
        if(!$assertionsDisabled && root == null)
            throw new AssertionError();
        else
            return root;
    }

    public String getKernelRegion()
    {
        if(!$assertionsDisabled && appRegion == null)
            throw new AssertionError();
        else
            return appRegion;
    }

    public void onKernelShutdown()
        throws MicroKernelException
    {
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

    private int hid;
    private MBeanServer jmx;
    private String root;
    private String appRegion;
    private Locale locale;
    private MicroKernelLogger log;
    private String localBind;
    private int rmiRegPort;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(MicroKernelContextAdapter.class).desiredAssertionStatus();
    }
}
