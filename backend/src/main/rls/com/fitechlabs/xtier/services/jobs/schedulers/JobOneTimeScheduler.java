// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.jobs.schedulers;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.jobs.*;
import com.fitechlabs.xtier.utils.ArgAssert;
import java.io.Serializable;
import java.util.Date;

public class JobOneTimeScheduler
    implements JobScheduler, Serializable
{

    public JobOneTimeScheduler(long l)
    {
        delay = 0L;
        firstTime = -1L;
        calls = 0L;
        ArgAssert.illegalArg(l >= 0L, "delay");
        delay = l;
    }

    public JobOneTimeScheduler(Date date)
    {
        delay = 0L;
        firstTime = -1L;
        calls = 0L;
        ArgAssert.nullArg(date, "firstTime");
        firstTime = date.getTime();
    }

    public void reset(JobsTimeScaler jobstimescaler)
    {
        if(!$assertionsDisabled && jobstimescaler == null)
            throw new AssertionError();
        if(firstTime == -1L)
            firstTime = jobstimescaler.getScaledTimeMillis() + delay;
        calls = 0L;
    }

    public long getNextExecTime(Job job)
    {
        if(!$assertionsDisabled && job == null)
            throw new AssertionError();
        if(calls == 1L)
        {
            return -1L;
        } else
        {
            calls++;
            return firstTime;
        }
    }

    public String toString()
    {
        Object obj;
        if(firstTime == -1L)
            obj = "null";
        else
            obj = new Date(firstTime);
        return L10n.format("SRVC.JOBS.TXT6", new Long(delay), obj);
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
    private long calls;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(JobOneTimeScheduler.class).desiredAssertionStatus();
    }
}
