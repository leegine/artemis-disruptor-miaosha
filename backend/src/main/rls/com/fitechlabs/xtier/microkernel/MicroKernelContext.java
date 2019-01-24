// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.microkernel;

import java.util.Locale;
import javax.management.MBeanServer;

// Referenced classes of package com.fitechlabs.xtier.microkernel:
//            MicroKernelException, MicroKernelLogger

public interface MicroKernelContext
{

    public abstract MicroKernelLogger getLogger();

    public abstract MBeanServer getMBeanServer();

    public abstract int getHostEnvId();

    public abstract Locale getLocale();

    public abstract int getRmiRegPort();

    public abstract String getLocalBindHost();

    public abstract String getXtierRoot();

    public abstract String getKernelRegion();

    public abstract void onKernelShutdown()
        throws MicroKernelException;

    public static final int HOST_ENV_J2EE = 1;
    public static final int HOST_ENV_JSP = 2;
    public static final int HOST_ENV_XTIER = 3;
    public static final int HOST_ENV_UNKNOWN = 4;
    public static final String MICRO_KERNEL_CONTEXT = "micro.kernel.context";
}
