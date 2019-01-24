// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   ServicesManager.java

package co.fin.intellioms.util;

import co.fin.intellioms.jmx.RuleEngineServicesManager;
import co.fin.intellioms.tx.TxManager;
import com.fitechlabs.xtier.kernel.KernelException;
import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.services.jmx.JmxService;
import java.util.*;
import javax.management.ObjectName;

// Referenced classes of package com.com.fin.intellioms.util:
//            InitializationException, Startable

public class ServicesManager
{

    public ServicesManager()
    {
        services = new ArrayList();
    }

    public void register(Object service)
    {
        services.add(service);
    }

    public List getServices()
    {
        return Collections.unmodifiableList(services);
    }

    public void start()
        throws InitializationException
    {
        XtierKernel.getInstance().enableShutdownHook(false);
        TxManager.initialize();
        try
        {
            ObjectName name = new ObjectName("IntelliOMS:name=RuleEngineServicesManager");
            RuleEngineServicesManager mb = new RuleEngineServicesManager(this);
            XtierKernel.getInstance().jmx().getMBeanServer().registerMBean(mb, name);
        }
        catch(Exception e)
        {
            throw new InitializationException((new StringBuilder()).append("Unable to register services manager MBean: ").append(e.getMessage()).toString(), e);
        }
        for(int i = 0; i < services.size(); i++)
        {
            Object serv = services.get(i);
            if(serv instanceof Startable)
            {
                Startable st = (Startable)serv;
                st.start();
            }
        }

        if(hook == null)
        {
            hook = new Thread() {

                public void run()
                {
                    try
                    {
                        hook = null;
                        terminate0();
                    }
                    catch(KernelException e)
                    {
                        e.printStackTrace();
                    }
                }

                final ServicesManager this$0;


            {
                this$0 = ServicesManager.this;
                super();
            }
            }
;
            Runtime.getRuntime().addShutdownHook(hook);
        }
    }

    public void terminate()
        throws KernelException
    {
        removeHook();
        terminate0();
    }

    private void terminate0()
        throws KernelException
    {
        stop();
        XtierKernel.getInstance().stop();
        break MISSING_BLOCK_LABEL_22;
        Exception exception;
        exception;
        XtierKernel.getInstance().stop();
        throw exception;
    }

    public void stop()
    {
        removeHook();
        for(int i = services.size() - 1; i >= 0; i--)
        {
            Object serv = services.get(i);
            if(serv instanceof Startable)
            {
                Startable st = (Startable)serv;
                st.stop();
            }
        }

        TxManager.dispose();
    }

    private void removeHook()
    {
        if(hook != null)
            Runtime.getRuntime().removeShutdownHook(hook);
        hook = null;
    }

    private ArrayList services;
    private Thread hook;


}
