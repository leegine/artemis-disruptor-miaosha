// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.jobs.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.jobs.JobCalendar;
import com.fitechlabs.xtier.services.jobs.JobsTimeScaler;
import java.io.Serializable;

class JobDefaultCalendar
    implements JobCalendar, Serializable
{

    JobDefaultCalendar()
    {
    }

    public boolean isSchedulableOn(long l)
    {
        return true;
    }

    public long getCycleSize(JobsTimeScaler jobstimescaler)
    {
        return 0x7fffffffffffffffL;
    }

    public String toString()
    {
        return L10n.format("SRVC.JOBS.TXT9", new Long(0x7fffffffffffffffL));
    }
}
