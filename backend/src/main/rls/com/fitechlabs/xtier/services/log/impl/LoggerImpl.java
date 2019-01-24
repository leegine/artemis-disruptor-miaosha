// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.log.impl;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.kernel.mbean.DynamicMBeanFactory;
import com.fitechlabs.xtier.kernel.mbean.DynamicXtierMBean;
import com.fitechlabs.xtier.services.jmx.JmxService;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.utils.ArgAssert;
import java.util.*;
import javax.management.JMException;
import javax.management.MBeanServer;

// Referenced classes of package com.fitechlabs.xtier.services.log.impl:
//            LoggerManager, LogUtils, LogServiceImpl

class LoggerImpl
    implements Logger
{

    LoggerImpl(LogServiceImpl logserviceimpl, String s)
    {
        ctgr = null;
        cache = null;
        impl = null;
        mask = 126;
        traceLoc = false;
        impl = logserviceimpl;
        ctgr = s;
        mBeanServer = XtierKernel.getInstance().jmx().getMBeanServer();
        try
        {
            mb = DynamicMBeanFactory.createDynMBean(new LoggerManager(this), com.fitechlabs.xtier.services.log.impl.LoggerManagerMBean.class, "logger", s != null ? s : "");
            mBeanServer.registerMBean(mb, mb.getName());
        }
        catch(JMException jmexception) { }
    }

    public void setTraceLocation(boolean flag)
    {
        synchronized(mutex)
        {
            traceLoc = flag;
        }
    }

    public boolean isTraceLocation()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return traceLoc;
        Exception exception;
        exception;
        throw exception;
    }

    public int getLevelMask()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return mask;
        Exception exception;
        exception;
        throw exception;
    }

    public void setLevelMask(int i)
    {
        LogUtils.checkMask(i);
        synchronized(mutex)
        {
            mask = i;
        }
    }

    public void setLevelFrom(int i)
    {
        LogUtils.checkLevel(i);
        synchronized(mutex)
        {
            mask = LogUtils.convertToMask(i);
        }
    }

    private void say(Object obj, int i, Throwable throwable)
    {
label0:
        {
            ArgAssert.nullArg(obj, "msg");
            Object obj1 = null;
            synchronized(mutex)
            {
                if((i & mask) != 0)
                    break label0;
            }
            return;
        }
        String s = ctgr;
        obj2;
        JVM INSTR monitorexit ;
          goto _L1
        exception;
        throw exception;
_L1:
        StackTraceElement stacktraceelement = null;
        if(traceLoc)
        {
            StackTraceElement astacktraceelement[] = (new Throwable()).getStackTrace();
            int j = 0;
            do
            {
                if(j >= astacktraceelement.length - 1)
                    break;
                if(astacktraceelement[j].getClassName().equals("com.fitechlabs.xtier.services.log.impl.LoggerImpl"))
                {
                    stacktraceelement = astacktraceelement[j + 1];
                    break;
                }
                j++;
            } while(true);
        }
        impl.say(obj, i, s, throwable, stacktraceelement);
        return;
    }

    public void rawLog(int i, Object obj, Throwable throwable)
    {
        say(obj, i, throwable);
    }

    public void log(Object obj)
    {
        say(obj, 16, null);
    }

    public void log(Object obj, Throwable throwable)
    {
        say(obj, 16, throwable);
    }

    public void error(Object obj)
    {
        say(obj, 64, null);
    }

    public void error(Object obj, Throwable throwable)
    {
        say(obj, 64, throwable);
    }

    public void trace(Object obj)
    {
        say(obj, 4, null);
    }

    public void trace(Object obj, Throwable throwable)
    {
        say(obj, 4, throwable);
    }

    public void info(Object obj)
    {
        say(obj, 8, null);
    }

    public void info(Object obj, Throwable throwable)
    {
        say(obj, 8, throwable);
    }

    public void warning(Object obj)
    {
        say(obj, 32, null);
    }

    public void warning(Object obj, Throwable throwable)
    {
        say(obj, 32, throwable);
    }

    public void debug(Object obj)
    {
        say(obj, 2, null);
    }

    public void debug(Object obj, Throwable throwable)
    {
        say(obj, 2, throwable);
    }

    public Logger getLogger(String s)
    {
        ArgAssert.nullArg(s, "ctgr");
        Object obj = mutex;
        JVM INSTR monitorenter ;
        if(cache == null)
            cache = new HashMap();
        Object obj1 = (Logger)cache.get(s);
        if(obj1 == null)
        {
            String s1 = ctgr != null ? ctgr + '/' + s : s;
            obj1 = new LoggerImpl(impl, s1);
            cache.put(s, obj1);
        }
        int i = impl.getCategoryMask(((Logger) (obj1)).getCategory());
        ((Logger) (obj1)).setLevelMask(i != -1 ? i : mask);
        return ((Logger) (obj1));
        Exception exception;
        exception;
        throw exception;
    }

    public String getCategory()
    {
        return ctgr;
    }

    void close()
    {
        synchronized(mutex)
        {
            if(cache != null)
            {
                for(Iterator iterator = cache.values().iterator(); iterator.hasNext(); ((LoggerImpl)iterator.next()).close());
            }
            if(mb != null)
                try
                {
                    mBeanServer.unregisterMBean(mb.getName());
                }
                catch(JMException jmexception) { }
        }
    }

    private String ctgr;
    private Map cache;
    private LogServiceImpl impl;
    private int mask;
    private MBeanServer mBeanServer;
    private DynamicXtierMBean mb;
    private boolean traceLoc;
    private final Object mutex = new Object();
}
