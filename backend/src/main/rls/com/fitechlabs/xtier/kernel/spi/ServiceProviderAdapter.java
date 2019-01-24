// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.kernel.spi;

import com.fitechlabs.xtier.kernel.mbean.DynamicMBeanFactory;
import com.fitechlabs.xtier.kernel.mbean.DynamicXtierMBean;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.microkernel.MicroKernelContext;
import com.fitechlabs.xtier.utils.Utils;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Map;
import javax.management.*;

// Referenced classes of package com.fitechlabs.xtier.kernel.spi:
//            ServiceProviderException, ServiceProvider

public abstract class ServiceProviderAdapter
    implements ServiceProvider
{

    public ServiceProviderAdapter()
    {
        state = 3;
        endTime = 0x7fffffffffffffffL;
        dfltMBean = null;
        regionName = null;
        cfgPath = null;
        featureSet = null;
        xTierRoot = null;
        declServices = null;
        mkCtx = null;
        localBind = null;
    }

    protected DynamicXtierMBean[] getMBeans()
        throws JMException
    {
        return null;
    }

    protected MicroKernelContext getMicroKernelContext()
    {
        return mkCtx;
    }

    protected String getXtierRoot()
    {
        return xTierRoot;
    }

    protected InetSocketAddress getLocalBind()
    {
        return localBind;
    }

    protected final String[] getDeclServices()
    {
        return declServices;
    }

    public final int[] getVersion()
    {
        return version;
    }

    public final long getStartupTime()
    {
        return startTime;
    }

    public final String getRegionName()
    {
        return regionName;
    }

    public final String getFeatureSet()
    {
        return featureSet;
    }

    public final String getConfigPath()
    {
        return cfgPath;
    }

    public final long getUpTime()
    {
        long l = System.currentTimeMillis();
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return l <= endTime ? l - startTime : endTime - startTime;
        Exception exception;
        exception;
        throw exception;
    }

    public final String getUpTimeStr()
    {
        long l = getUpTime();
        long al[] = new long[4];
        long al1[] = {
            60L, 60L, 60L, 24L
        };
        l /= 1000L;
        for(int i = 0; i < al1.length && l > 0L; l /= al1[i++])
            al[i] = l % al1[i];

        return L10n.format("KRNL.KRNL.TXT1", pad(al[3]), pad(al[2]), pad(al[1]), pad(al[0]));
    }

    private String pad(long l)
    {
        return l >= 10L ? Long.toString(l) : "0" + l;
    }

    public final String getStartupTimeStr()
    {
        return (new Date(startTime)).toString();
    }

    public final String getStateStr()
    {
        switch(getState())
        {
        case 5: // '\005'
            return "SERVICE_FATAL";

        case 1: // '\001'
            return "SERVICE_STARTED";

        case 2: // '\002'
            return "SERVICE_STARTING";

        case 3: // '\003'
            return "SERVICE_STARTING";

        case 4: // '\004'
            return "SERVICE_STARTING";
        }
        if(!$assertionsDisabled)
            throw new AssertionError("Unknown service state: " + state);
        else
            return null;
    }

    public final String getVersionStr()
    {
        return "" + version[0] + '.' + version[1] + '.' + version[2] + '.' + version[3];
    }

    public final int getState()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return state;
        Exception exception;
        exception;
        throw exception;
    }

    protected void onStart()
        throws ServiceProviderException
    {
        if(!$assertionsDisabled)
            throw new AssertionError("Callback onStart() must be overridden for service: " + getName());
        else
            return;
    }

    protected void onStop()
        throws ServiceProviderException
    {
        if(!$assertionsDisabled)
            throw new AssertionError("Callback onStop() must be overridden for service: " + getName());
        else
            return;
    }

    public final void start(Map map)
        throws ServiceProviderException
    {
        if(!$assertionsDisabled && map == null)
            throw new AssertionError("Start properties cannot be null.");
        synchronized(mutex)
        {
            if(!$assertionsDisabled && state != 3)
                throw new AssertionError();
            state = 2;
            mkCtx = (MicroKernelContext)map.get("micro.kernel.context");
            cfgPath = (String)map.get("config.path");
            featureSet = (String)map.get("feature.set");
            regionName = (String)map.get("region.name");
            declServices = (String[])map.get("decl.services");
            localBind = (InetSocketAddress)map.get("local.bind");
            if(!$assertionsDisabled && mkCtx == null)
                throw new AssertionError("Micro kernel context must be supplied.");
            if(!$assertionsDisabled && cfgPath == null)
                throw new AssertionError("Configuration path must be supplied.");
            if(!$assertionsDisabled && featureSet == null)
                throw new AssertionError("Feature set must be supplied.");
            if(!$assertionsDisabled && declServices == null)
                throw new AssertionError("Declared services must be supplied.");
            xTierRoot = mkCtx.getXtierRoot();
            startTime = System.currentTimeMillis();
            try
            {
                onStart();
                state = 1;
                MBeanServer mbeanserver = mkCtx.getMBeanServer();
                dfltMBean = DynamicMBeanFactory.createDynMBean(this, getServiceItf());
                mbeanserver.registerMBean(dfltMBean, dfltMBean.getName());
                DynamicXtierMBean adynamicxtiermbean[] = getMBeans();
                if(adynamicxtiermbean != null)
                {
                    for(int i = 0; i < adynamicxtiermbean.length; i++)
                    {
                        DynamicXtierMBean dynamicxtiermbean = adynamicxtiermbean[i];
                        mbeanserver.registerMBean(dynamicxtiermbean, dynamicxtiermbean.getName());
                    }

                }
            }
            catch(JMException jmexception)
            {
                state = 5;
                throw new ServiceProviderException(L10n.format("KRNL.KRNL.ERR29", jmexception.getMessage()), jmexception);
            }
        }
    }

    public final void stop()
        throws ServiceProviderException
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        if(!$assertionsDisabled && state != 1)
            throw new AssertionError();
        state = 4;
        try
        {
            MBeanServer mbeanserver = mkCtx.getMBeanServer();
            DynamicXtierMBean adynamicxtiermbean[] = getMBeans();
            if(adynamicxtiermbean != null)
            {
                for(int i = adynamicxtiermbean.length - 1; i >= 0; i--)
                {
                    ObjectName objectname = adynamicxtiermbean[i].getName();
                    mbeanserver.unregisterMBean(objectname);
                }

            }
            try
            {
                mbeanserver.unregisterMBean(dfltMBean.getName());
            }
            catch(InstanceNotFoundException instancenotfoundexception) { }
            onStop();
            state = 3;
        }
        catch(ServiceProviderException serviceproviderexception)
        {
            state = 5;
            throw serviceproviderexception;
        }
        catch(JMException jmexception)
        {
            state = 5;
            throw new ServiceProviderException(L10n.format("KRNL.KRNL.ERR30", jmexception.getMessage()), jmexception);
        }
        endTime = System.currentTimeMillis();
        break MISSING_BLOCK_LABEL_168;
        Exception exception;
        exception;
        endTime = System.currentTimeMillis();
        throw exception;
        Exception exception1;
        exception1;
        throw exception1;
    }

    private Class getServiceItf()
    {
        String s = getClass().getName();
        if(!$assertionsDisabled && !s.endsWith("Impl"))
            throw new AssertionError("Service implementation class is invalid: " + s);
        String s1 = s.substring(s.lastIndexOf('.') + 1);
        String s2 = s1.substring(0, s1.length() - 4);
        Class class1 = null;
        Class aclass[] = getClass().getInterfaces();
        int i = 0;
        do
        {
            if(i >= aclass.length)
                break;
            if(aclass[i].getName().endsWith(s2))
            {
                class1 = aclass[i];
                break;
            }
            i++;
        } while(true);
        if(!$assertionsDisabled && class1 == null)
            throw new AssertionError("Service interface is not found for: " + s);
        else
            return class1;
    }

    public String toString()
    {
        if(!$assertionsDisabled)
            throw new AssertionError();
        else
            return "Service provider [name=" + getName() + ", state=" + getStateStr() + ", startup-time=" + getStartupTimeStr() + ", up-time=" + getUpTimeStr() + ", version=" + Utils.arr2Str(getVersion()) + ']';
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

    private final int version[] = {
        2, 3, 2, 1599
    };
    private final Object mutex = new Object();
    private int state;
    private long startTime;
    private long endTime;
    private DynamicXtierMBean dfltMBean;
    private String regionName;
    private String cfgPath;
    private String featureSet;
    private String xTierRoot;
    private String declServices[];
    private MicroKernelContext mkCtx;
    private InetSocketAddress localBind;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(ServiceProviderAdapter.class).desiredAssertionStatus();
    }
}
