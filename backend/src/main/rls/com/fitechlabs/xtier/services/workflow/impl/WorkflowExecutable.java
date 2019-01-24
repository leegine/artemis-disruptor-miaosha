// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.workflow.impl;

import com.fitechlabs.xtier.utils.xml.XmlLocation;

// Referenced classes of package com.fitechlabs.xtier.services.workflow.impl:
//            WorkflowXmlLoc

abstract class WorkflowExecutable extends WorkflowXmlLoc
{

    WorkflowExecutable(XmlLocation xmllocation, String s, String s1, boolean flag)
    {
        super(xmllocation);
        avgExecTime = 0L;
        lastExecTime = 0L;
        execCount = 0L;
        mutex = new Object();
        group = s;
        name = s1;
        isExecSet = flag;
    }

    public String getGroup()
    {
        return group;
    }

    public String getName()
    {
        return name;
    }

    boolean isExecSet()
    {
        return isExecSet;
    }

    public long getAvgExecTime()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return avgExecTime;
        Exception exception;
        exception;
        throw exception;
    }

    public long getLastExecTime()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return lastExecTime;
        Exception exception;
        exception;
        throw exception;
    }

    public long getExecCount()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return execCount;
        Exception exception;
        exception;
        throw exception;
    }

    void onExec(long l)
    {
        synchronized(mutex)
        {
            lastExecTime = System.currentTimeMillis();
            avgExecTime = (execCount * avgExecTime + l) / (execCount + 1L);
            execCount++;
        }
    }

    public String toString()
    {
        if(!$assertionsDisabled)
            throw new AssertionError();
        else
            return "Workflow element [group=" + group + ", name=" + name + ", is-exec-set=" + isExecSet + ']';
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

    private String group;
    private String name;
    private boolean isExecSet;
    private long avgExecTime;
    private long lastExecTime;
    private long execCount;
    private Object mutex;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(WorkflowExecutable.class).desiredAssertionStatus();
    }
}
