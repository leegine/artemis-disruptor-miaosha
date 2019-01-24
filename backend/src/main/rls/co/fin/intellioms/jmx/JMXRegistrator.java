// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   JMXRegistrator.java

package co.fin.intellioms.jmx;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.services.jmx.JmxService;
import javax.management.*;

public class JMXRegistrator
{

    public JMXRegistrator()
    {
    }

    public void register(String objName, Object obj)
        throws MalformedObjectNameException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException
    {
        ObjectName name = new ObjectName(objName);
        XtierKernel.getInstance().jmx().getMBeanServer().registerMBean(obj, name);
    }
}
