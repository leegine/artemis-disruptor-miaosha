// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.kernel;

import java.util.Locale;
import java.util.Properties;

// Referenced classes of package com.fitechlabs.xtier.kernel:
//            KernelException

public interface XtierKernelManageMBean
{

    public abstract int getKernelState();

    public abstract String getKernelStateStr();

    public abstract boolean isServiceStarted(String s);

    public abstract void enableShutdownHook(boolean flag);

    public abstract void stop()
        throws KernelException;

    public abstract String[] getAllServices();

    public abstract Locale getLocale();

    public abstract int availableProcessors();

    public abstract int getHostEnvironment();

    public abstract long getFreeMemoryKb();

    public abstract long getMaxMemoryKb();

    public abstract long getTotalMemoryKb();

    public abstract void gc();

    public abstract String sysPropertiesHtml();

    public abstract Properties sysProperties();

    public abstract long getUpTime();

    public abstract String getUpTimeStr();

    public abstract String xtierThreadsHtml();
}
