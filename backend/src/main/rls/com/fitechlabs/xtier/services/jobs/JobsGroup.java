// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.jobs;

import java.util.Map;

// Referenced classes of package com.fitechlabs.xtier.services.jobs:
//            Job

public interface JobsGroup
{

    public abstract String getName();

    public abstract Job getJob(String s);

    public abstract boolean containsJob(String s);

    public abstract void addJob(Job job);

    public abstract boolean removeJob(String s);

    public abstract Map getAllJobs();

    public abstract void cancelAllJobs();

    public abstract void removeAllJobs();
}
