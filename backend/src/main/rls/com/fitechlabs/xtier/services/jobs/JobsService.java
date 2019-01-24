// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.jobs;

import com.fitechlabs.xtier.kernel.KernelService;
import java.util.Map;

// Referenced classes of package com.fitechlabs.xtier.services.jobs:
//            JobException, Job, JobsGroup, JobBody, 
//            JobScheduler, JobCalendar, JobsTimeScaler, JobsStore

public interface JobsService
    extends KernelService
{

    public abstract Map getActiveJobs();

    public abstract Job getJob(String s);

    public abstract JobsGroup getGroup(String s);

    public abstract Map getAllJobs();

    public abstract Map getAllGroups();

    public abstract void schedule(String s)
        throws JobException;

    public abstract void schedule(String s, boolean flag)
        throws JobException;

    public abstract void scheduleGroup(String s)
        throws JobException;

    public abstract void scheduleGroup(String s, boolean flag)
        throws JobException;

    public abstract Job addJob(String s, JobBody jobbody, JobScheduler jobscheduler, JobCalendar jobcalendar)
        throws JobException;

    public abstract JobsGroup createGroup(String s)
        throws JobException;

    public abstract boolean cancel(String s);

    public abstract void cancelAll();

    public abstract void cancellGroup(String s);

    public abstract boolean delete(String s);

    public abstract void deleteAll();

    public abstract void deleteGroup(String s, boolean flag);

    public abstract JobsTimeScaler getTimeScaler();

    public abstract void setTimeScaler(JobsTimeScaler jobstimescaler);

    public abstract JobsStore getStore();

    public abstract void setStore(JobsStore jobsstore);

    public abstract void storeState()
        throws JobException;
}
