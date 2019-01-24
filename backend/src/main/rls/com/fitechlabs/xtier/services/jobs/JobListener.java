// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.jobs;

import java.util.EventListener;

// Referenced classes of package com.fitechlabs.xtier.services.jobs:
//            Job, JobException

public interface JobListener
    extends EventListener
{

    public abstract void onJobEvent(int i, Job job);

    public abstract void onError(Job job, JobException jobexception);

    public static final int JOB_SCHEDULED = 1;
    public static final int JOB_INVOKE_START = 2;
    public static final int JOB_INVOKE_END = 3;
    public static final int JOB_FINISHED = 4;
}
