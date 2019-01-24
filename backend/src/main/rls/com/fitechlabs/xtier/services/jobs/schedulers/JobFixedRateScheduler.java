// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.jobs.schedulers;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.jobs.*;
import com.fitechlabs.xtier.utils.ArgAssert;
import java.io.Serializable;
import java.util.Date;

public class JobFixedRateScheduler
    implements JobScheduler, Serializable
{

    public JobFixedRateScheduler(long l, long l1)
    {
        delay = -1L;
        firstTime = -1L;
        repeatCount = -1L;
        finishTime = -1L;
        ArgAssert.illegalRange(l >= 0L, "delay", "delay >= 0");
        ArgAssert.illegalRange(l1 > 0L, "period", "period > 0");
        period = l1;
        delay = l;
    }

    public JobFixedRateScheduler(Date date, long l)
    {
        delay = -1L;
        firstTime = -1L;
        repeatCount = -1L;
        finishTime = -1L;
        ArgAssert.nullArg(date, "firstTime");
        ArgAssert.illegalRange(l > 0L, "period", "period > 0");
        period = l;
        firstTime = date.getTime();
    }

    public JobFixedRateScheduler(long l, long l1, long l2)
    {
        delay = -1L;
        firstTime = -1L;
        repeatCount = -1L;
        finishTime = -1L;
        ArgAssert.illegalRange(l >= 0L, "delay", "delay >= 0");
        ArgAssert.illegalRange(l1 > 0L, "period", "period > 0");
        ArgAssert.illegalRange(l2 > 0L, "repeatCount", "repeatCount > 0");
        period = l1;
        delay = l;
        repeatCount = l2;
    }

    public JobFixedRateScheduler(Date date, long l, long l1)
    {
        delay = -1L;
        firstTime = -1L;
        repeatCount = -1L;
        finishTime = -1L;
        ArgAssert.nullArg(date, "firstTime");
        ArgAssert.illegalRange(l > 0L, "period", "period > 0");
        ArgAssert.illegalRange(l1 > 0L, "repeatCount", "repeatCount > 0");
        period = l;
        firstTime = date.getTime();
        repeatCount = l1;
    }

    public JobFixedRateScheduler(long l, long l1, Date date)
    {
        delay = -1L;
        firstTime = -1L;
        repeatCount = -1L;
        finishTime = -1L;
        ArgAssert.illegalRange(l >= 0L, "delay", "delay >= 0");
        ArgAssert.illegalRange(l1 > 0L, "period", "period > 0");
        ArgAssert.nullArg(date, "finishTime");
        period = l1;
        delay = l;
        finishTime = date.getTime();
    }

    public JobFixedRateScheduler(Date date, long l, Date date1)
    {
        delay = -1L;
        firstTime = -1L;
        repeatCount = -1L;
        finishTime = -1L;
        ArgAssert.nullArg(date, "firstTime");
        ArgAssert.illegalArg(l > 0L, "period");
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
        calls = 0;
    }

    public long getNextExecTime(Job job)
    {
        if(!$assertionsDisabled && job == null)
            throw new AssertionError();
        if(repeatCount != -1L && (long)job.getExecCount() >= repeatCount)
            return -1L;
        long l = firstTime + (long)(calls++) * period;
        if(finishTime != -1L && finishTime <= l)
            return -1L;
        else
            return l;
    }

    public String toString()
    {
        Object obj;
        if(firstTime == -1L)
            obj = "null";
        else
            obj = new Date(firstTime);
        return L10n.format("SRVC.JOBS.TXT8", new Long(delay), obj, new Long(period));
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
    private long period;
    private int calls;
    private long repeatCount;
    private long finishTime;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(JobFixedRateScheduler.class).desiredAssertionStatus();
    }
}
