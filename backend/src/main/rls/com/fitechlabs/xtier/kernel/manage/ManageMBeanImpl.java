// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.kernel.manage;

import com.fitechlabs.xtier.kernel.*;
import com.fitechlabs.xtier.threads.SysThreadGroup;
import java.util.*;

public class ManageMBeanImpl
    implements XtierKernelManageMBean
{

    public ManageMBeanImpl(XtierKernel xtierkernel)
    {
        impl = null;
        if(!$assertionsDisabled && xtierkernel == null)
        {
            throw new AssertionError();
        } else
        {
            impl = xtierkernel;
            return;
        }
    }

    public void enableShutdownHook(boolean flag)
    {
        impl.enableShutdownHook(flag);
    }

    public String[] getAllServices()
    {
        KernelService akernelservice[] = impl.getAllServices();
        String as[] = new String[akernelservice.length];
        for(int i = 0; i < akernelservice.length; i++)
            as[i] = akernelservice[i].getName();

        return as;
    }

    public int getKernelState()
    {
        return impl.getState();
    }

    public String getKernelStateStr()
    {
        int i = impl.getState();
        switch(i)
        {
        case 3: // '\003'
            return "KERNEL_FATAL";

        case 1: // '\001'
            return "KERNEL_STARTED";

        case 2: // '\002'
            return "KERNEL_STOPPED";
        }
        if(!$assertionsDisabled)
            throw new AssertionError("Unknonw kernel state: " + i);
        else
            return null;
    }

    public boolean isServiceStarted(String s)
    {
        return impl.isStarted(s);
    }

    public void stop()
        throws KernelException
    {
        impl.stop();
    }

    public int availableProcessors()
    {
        return Runtime.getRuntime().availableProcessors();
    }

    public void gc()
    {
        Runtime.getRuntime().gc();
    }

    public long getUpTime()
    {
        return impl.getUpTime();
    }

    public String getUpTimeStr()
    {
        long l = getUpTime();
        if(l == -1L)
            return "n/a";
        long al[] = new long[4];
        long al1[] = {
            60L, 60L, 60L, 24L
        };
        l /= 1000L;
        for(int i = 0; i < al1.length && l > 0L; l /= al1[i++])
            al[i] = l % al1[i];

        return pad(al[3]) + " days, " + pad(al[2]) + " hours, " + pad(al[1]) + " mins, " + pad(al[0]) + "secs.";
    }

    private String pad(long l)
    {
        return l >= 10L ? Long.toString(l) : "0" + l;
    }

    public long getFreeMemoryKb()
    {
        return Runtime.getRuntime().freeMemory() / 1024L;
    }

    public Locale getLocale()
    {
        return Locale.getDefault();
    }

    public Properties sysProperties()
    {
        return System.getProperties();
    }

    public String xtierThreadsHtml()
    {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("<table border='0' cellspacing='0' cellpadding='5' style='border: 1px dotted #aaa'>");
        SysThreadGroup systhreadgroup = SysThreadGroup.getDfltGroup();
        Thread athread[] = new Thread[systhreadgroup.activeCount()];
        int i = systhreadgroup.enumerate(athread, true);
        stringbuffer.append("<tr bgcolor='#efefef'>");
        stringbuffer.append("<td style='border: 1px dotted #aaa' align='left' valign='top'>Name (total: " + i + ")</td>");
        stringbuffer.append("<td style='border: 1px dotted #aaa' align='left' valign='top'>Group</td>");
        stringbuffer.append("<td style='border: 1px dotted #aaa' align='left' valign='top'>Priority</td>");
        stringbuffer.append("<td style='border: 1px dotted #aaa' align='left' valign='top'>Alive</td>");
        stringbuffer.append("<td style='border: 1px dotted #aaa' align='left' valign='top'>Daemon</td>");
        stringbuffer.append("<td style='border: 1px dotted #aaa' align='left' valign='top'>Interrupted</td>");
        stringbuffer.append("</tr>");
        for(int j = 0; j < i; j++)
        {
            Thread thread = athread[j];
            stringbuffer.append("<tr>");
            stringbuffer.append("<td style='border: 1px dotted #aaa' align='left' valign='top'>").append(thread.getName()).append("</td>");
            stringbuffer.append("<td style='border: 1px dotted #aaa' align='left' valign='top'>").append(thread.getThreadGroup()).append("</td>");
            stringbuffer.append("<td style='border: 1px dotted #aaa' align='left' valign='top'>").append(thread.getPriority()).append("</td>");
            stringbuffer.append("<td style='border: 1px dotted #aaa' align='left' valign='top'>").append(thread.isAlive()).append("</td>");
            stringbuffer.append("<td style='border: 1px dotted #aaa' align='left' valign='top'>").append(thread.isDaemon()).append("</td>");
            stringbuffer.append("<td style='border: 1px dotted #aaa' align='left' valign='top'>").append(thread.isInterrupted()).append("</td>");
            stringbuffer.append("</tr>");
        }

        stringbuffer.append("</table>");
        return stringbuffer.toString();
    }

    public long getMaxMemoryKb()
    {
        return Runtime.getRuntime().maxMemory() / 1024L;
    }

    public String sysPropertiesHtml()
    {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("<table border='0' cellspacing='0' cellpadding='5' style='border: 1px dotted #aaa'>");
        stringbuffer.append("<tr bgcolor='#efefef'>");
        stringbuffer.append("<td style='border: 1px dotted #aaa' align='left' valign='top'>Key</td>");
        stringbuffer.append("<td style='border: 1px dotted #aaa' align='left' valign='top'>Value</td>");
        stringbuffer.append("</tr>");
        for(Enumeration enumeration = System.getProperties().keys(); enumeration.hasMoreElements(); stringbuffer.append("</tr>"))
        {
            String s = (String)enumeration.nextElement();
            stringbuffer.append("<tr>");
            stringbuffer.append("<td style='border: 1px dotted #aaa' align='left' valign='top'>").append(s).append("</td>");
            stringbuffer.append("<td style='border: 1px dotted #aaa' align='left' valign='top'>").append(System.getProperty(s)).append("</td>");
        }

        stringbuffer.append("</table>");
        return stringbuffer.toString();
    }

    public long getTotalMemoryKb()
    {
        return Runtime.getRuntime().totalMemory() / 1024L;
    }

    public int getHostEnvironment()
    {
        return impl.getHostEnvironment();
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

    private static final String CSS = "style='border: 1px dotted #aaa'";
    private XtierKernel impl;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(ManageMBeanImpl.class).desiredAssertionStatus();
    }
}
