// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.jobs.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.jobs.Job;
import com.fitechlabs.xtier.services.jobs.JobsGroup;
import com.fitechlabs.xtier.utils.ArgAssert;
import java.util.*;

public class JobsGroupImpl
    implements JobsGroup
{

    JobsGroupImpl(String s)
    {
        jobs = new Hashtable();
        name = s;
    }

    public String getName()
    {
        return name;
    }

    public Job getJob(String s)
    {
        ArgAssert.nullArg(s, "jobName");
        return (Job)jobs.get(s);
    }

    public boolean containsJob(String s)
    {
        ArgAssert.nullArg(s, "jobName");
        return jobs.containsKey(s);
    }

    public void addJob(Job job)
    {
        ArgAssert.nullArg(job, "job");
        jobs.put(job.getName(), job);
    }

    public boolean removeJob(String s)
    {
        ArgAssert.nullArg(s, "jobName");
        return jobs.remove(s) != null;
    }

    public Map getAllJobs()
    {
        return Collections.unmodifiableMap(jobs);
    }

    public void cancelAllJobs()
    {
        for(Iterator iterator = jobs.values().iterator(); iterator.hasNext(); ((Job)iterator.next()).cancel());
    }

    public void removeAllJobs()
    {
        jobs.clear();
    }

    public String toString()
    {
        return L10n.format("SRVC.JOBS.TXT11", name, jobs);
    }

    private String name;
    private Map jobs;
}
