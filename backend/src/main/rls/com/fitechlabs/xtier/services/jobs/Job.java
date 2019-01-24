// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.jobs;

import java.util.List;

// Referenced classes of package com.fitechlabs.xtier.services.jobs:
//            JobListener, JobBody, JobScheduler, JobCalendar

public interface Job
{

    public abstract String getName();

    public abstract boolean isActive();

    public abstract long getScheduleTime();

    public abstract long getFinishTime();

    public abstract long getLastInvokeTime();

    public abstract long getLastInvokeDuration();

    public abstract int getExecCount();

    public abstract long getNextExecTime();

    public abstract void addListener(JobListener joblistener);

    public abstract boolean removeListener(JobListener joblistener);

    public abstract List getAllListeners();

    public abstract void join()
        throws InterruptedException;

    public abstract boolean join(long l)
        throws InterruptedException;

    public abstract boolean cancel();

    public abstract JobBody getBody();

    public abstract JobScheduler getScheduler();

    public abstract JobCalendar getCalendar();
}
