// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   TimerAdaptorManager.java

package co.fin.intellioms.jmx;

import co.fin.intellioms.time.TimerAdaptorImpl;
import java.util.List;

// Referenced classes of package com.com.fin.intellioms.jmx:
//            TimerAdaptorManagerMBean

public class TimerAdaptorManager
    implements TimerAdaptorManagerMBean
{

    public TimerAdaptorManager(TimerAdaptorImpl timer)
    {
        this.timer = timer;
    }

    public List listSchedule()
    {
        return timer.getSchedule();
    }

    private final TimerAdaptorImpl timer;
}
