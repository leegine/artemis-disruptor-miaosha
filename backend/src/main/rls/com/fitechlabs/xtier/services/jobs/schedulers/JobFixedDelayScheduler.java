// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.jobs.schedulers;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.jobs.*;
import com.fitechlabs.xtier.utils.ArgAssert;
import java.io.Serializable;
import java.util.Date;

public class JobFixedDelayScheduler
    implements JobScheduler, Serializable
{

    public JobFixedDelayScheduler(long l, long l1)
    {
        delay = 0L;
        firstTime = -1L;
        lastExecTime = -1L;
        lastExecCount = 0L;
        repeatCount = -1L;
        finishTime = -1L;
        ArgAssert.illegalRange(l >= 0L, "delay", "delay >= 0");
        ArgAssert.illegalRange(l1 > 0L, "period", "period > 0");
        period = l1;
        delay = l;
    }

    public JobFixedDelayScheduler(Date date, long l)
    {
        delay = 0L;
        firstTime = -1L;
        lastExecTime = -1L;
        lastExecCount = 0L;
        repeatCount = -1L;
        finishTime = -1L;
        ArgAssert.nullArg(date, "firstTime");
        ArgAssert.illegalRange(l > 0L, "period", "period > 0");
        period = l;
        firstTime = date.getTime();
    }

    public JobFixedDelayScheduler(long l, long l1, long l2)
    {
        delay = 0L;
        firstTime = -1L;
        lastExecTime = -1L;
        lastExecCount = 0L;
        repeatCount = -1L;
        finishTime = -1L;
        ArgAssert.illegalRange(l >= 0L, "delay", "delay >= 0");
        ArgAssert.illegalRange(l1 > 0L, "period", "period > 0");
        ArgAssert.illegalRange(l2 > 0L, "repeatCount", "repeatCount > 0");
        delay = l;
        period = l1;
        repeatCount = l2;
    }

    public JobFixedDelayScheduler(Date date, long l, long l1)
    {
        delay = 0L;
        firstTime = -1L;
        lastExecTime = -1L;
        lastExecCount = 0L;
        repeatCount = -1L;
        finishTime = -1L;
        ArgAssert.nullArg(date, "firstTime");
        ArgAssert.illegalRange(l > 0L, "period", "period > 0");
        ArgAssert.illegalRange(l1 > 0L, "repeatCount", "repeatCount > 0");
        period = l;
        firstTime = date.getTime();
        repeatCount = l1;
    }

    public JobFixedDelayScheduler(long l, long l1, Date date)
    {
        delay = 0L;
        firstTime = -1L;
        lastExecTime = -1L;
        lastExecCount = 0L;
        repeatCount = -1L;
        finishTime = -1L;
        ArgAssert.illegalRange(l >= 0L, "delay", "delay >= 0");
        ArgAssert.illegalRange(l1 > 0L, "period", "period > 0");
        ArgAssert.nullArg(date, "finishTime");
        delay = l;
        period = l1;
        finishTime = date.getTime();
    }

    public JobFixedDelayScheduler(Date date, long l, Date date1)
    {
        delay = 0L;
        firstTime = -1L;
        lastExecTime = -1L;
        lastExecCount = 0L;
        repeatCount = -1L;
        finishTime = -1L;
        ArgAssert.nullArg(date, "firstTime");
        ArgAssert.illegalRange(l > 0L, "period", "period > 0");
        ArgAssert.nullArg(date1, "finishTime");
        ArgAssert.illegalRange(date1.after(date), "finishTime", "finishTime after firstTime");
        period = l;
        firstTime = date.getTime();
        finishTime = date1.getTime();
    }

    public void reset(JobsTimeScaler jobstimescaler)
    {
        if(!$assertionsDisabled && jobstimescaler == null)
            throw new AssertionError();
        if(firstTime == -1L)
            firstTime = jobstimescaler.getScaledTimeMillis() + delay;
        lastExecTime = 0L;
        lastExecCount = 0L;
    }

    public long getNextExecTime(Job job)
    {
        if(!$assertionsDisabled && job == null)
            throw new AssertionError();
        int i = job.getExecCount();
        if(repeatCount != -1L && (long)i >= repeatCount)
            return -1L;
        long l;
        if(lastExecTime == -1L)
            l = firstTime;
        else
        if((long)i == lastExecCount)
            l = lastExecTime + period;
        else
            l = job.getLastInvokeTime() + job.getLastInvokeDuration() + period;
        if(finishTime != -1L && finishTime <= l)
        {
            return -1L;
        } else
        {
            lastExecTime = l;
            lastExecCount = i;
            return l;
        }
    }

    public String toString()
    {
        Object obj;
        if(firstTime == -1L)
            obj = "null";
        else
            obj = new Date(firstTime);
        return L10n.format("SRVC.JOBS.TXT7", new Long(delay), obj, new Long(period));
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

    private long delay;
    private long firstTime;
    private long lastExecTime;
    private long lastExecCount;
    private long period;
    private long repeatCount;
    private long finishTime;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(JobFixedDelayScheduler.class).desiredAssertionStatus();
    }
}
