// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.jobs;


// Referenced classes of package com.fitechlabs.xtier.services.jobs:
//            JobsTimeScaler

public interface JobCalendar
{

    public abstract boolean isSchedulableOn(long l);

    public abstract long getCycleSize(JobsTimeScaler jobstimescaler);
}
