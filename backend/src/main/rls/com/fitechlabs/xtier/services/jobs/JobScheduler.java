// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.jobs;


// Referenced classes of package com.fitechlabs.xtier.services.jobs:
//            JobsTimeScaler, Job

public interface JobScheduler
{

    public abstract void reset(JobsTimeScaler jobstimescaler);

    public abstract long getNextExecTime(Job job);

    public static final long NEVER = -1L;
}
