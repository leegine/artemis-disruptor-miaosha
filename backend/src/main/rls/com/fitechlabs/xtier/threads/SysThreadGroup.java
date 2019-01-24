// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.threads;

import com.fitechlabs.xtier.utils.Utils;

// Referenced classes of package com.fitechlabs.xtier.threads:
//            SysThread

public final class SysThreadGroup extends ThreadGroup
{

    private SysThreadGroup(String s)
    {
        super(s);
    }

    private SysThreadGroup(SysThreadGroup systhreadgroup, String s)
    {
        super(systhreadgroup, s);
    }

    public static SysThreadGroup getNewGroup(String s)
    {
        String s1 = "xtier:" + s;
        return new SysThreadGroup(dfltGrp, s1);
    }

    public static SysThreadGroup getDfltGroup()
    {
        return dfltGrp;
    }

    public void stopThreads()
    {
        Thread athread[] = new Thread[activeCount()];
        int i = enumerate(athread);
        for(int j = 0; j < i; j++)
            if(!athread[j].isDaemon())
                Utils.stopThread(athread[j]);

    }

    public void stopAndDestroy()
    {
        stopThreads();
        try
        {
            destroy();
        }
        catch(IllegalThreadStateException illegalthreadstateexception) { }
    }

    public void uncaughtException(Thread thread, Throwable throwable)
    {
        if(!$assertionsDisabled && !(thread instanceof SysThread))
        {
            throw new AssertionError();
        } else
        {
            super.uncaughtException(thread, throwable);
            return;
        }
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

    private static final SysThreadGroup dfltGrp = new SysThreadGroup("xtier");
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(SysThreadGroup.class).desiredAssertionStatus();
    }
}
