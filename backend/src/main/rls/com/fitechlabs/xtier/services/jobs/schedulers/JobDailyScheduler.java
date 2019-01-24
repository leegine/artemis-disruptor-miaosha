// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.jobs.schedulers;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.jobs.*;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.Utils;
import java.io.Serializable;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.jobs.schedulers:
//            JobSchedulerUtils

public class JobDailyScheduler
    implements JobScheduler, Serializable
{

    public JobDailyScheduler(String s)
    {
        repeatCount = -1L;
        finishTime = -1L;
        ArgAssert.nullArg(s, "timesStr");
        timesStr = s;
        ArrayList arraylist = new ArrayList();
        StringTokenizer stringtokenizer = new StringTokenizer(s, ",");
        do
        {
            if(!stringtokenizer.hasMoreElements())
                break;
            String s1 = ((String)stringtokenizer.nextElement()).trim();
            if(s1.length() > 0)
            {
                long l = JobSchedulerUtils.parseToLocalMillis(s1);
                ArgAssert.illegalArg(l >= 0L, "timesStr");
                ArgAssert.illegalArg(l <= 0x5265c00L, "timesStr");
                arraylist.add(new Long(l));
            }
        } while(true);
        ArgAssert.illegalArg(!arraylist.isEmpty(), "timesStr");
        Collections.sort(arraylist);
        int i = arraylist.size();
        times = new long[i];
        for(int j = 0; j < i; j++)
            times[j] = ((Long)arraylist.get(j)).longValue();

        maxIndex = times.length - 1;
    }

    public JobDailyScheduler(long al[])
    {
        repeatCount = -1L;
        finishTime = -1L;
        ArgAssert.nullArg(al, "times");
        ArgAssert.illegalArg(al.length > 0, "times");
        for(int i = 0; i < al.length; i++)
        {
            ArgAssert.illegalArg(al[i] >= 0L, "times");
            ArgAssert.illegalArg(al[i] <= 0x5265c00L, "times");
        }

        times = (long[])al.clone();
        Arrays.sort(times);
        maxIndex = al.length - 1;
    }

    public JobDailyScheduler(String s, long l)
    {
        this(s);
        ArgAssert.illegalRange(l > 0L, "repeatCount", "repeatCount > 0");
        repeatCount = l;
    }

    public JobDailyScheduler(long al[], long l)
    {
        this(al);
        ArgAssert.illegalRange(l > 0L, "repeatCount", "repeatCount > 0");
        repeatCount = l;
    }

    public JobDailyScheduler(String s, Date date)
    {
        this(s);
        ArgAssert.nullArg(date, "finishTime");
        finishTime = date.getTime();
    }

    public JobDailyScheduler(long al[], Date date)
    {
        this(al);
        ArgAssert.nullArg(date, "finishTime");
        finishTime = date.getTime();
    }

    public void reset(JobsTimeScaler jobstimescaler)
    {
        if(!$assertionsDisabled && jobstimescaler == null)
            throw new AssertionError();
        lastIndex = maxIndex;
        long l = jobstimescaler.getScaledTimeMillis();
        dayStart = getDayStart(l);
        int i = 0;
        do
        {
            if(i >= times.length)
                break;
            long l1 = JobSchedulerUtils.getDayLightTimeShift(dayStart.getTimeInMillis(), times[i]);
            if(times[i] + dayStart.getTimeInMillis() + l1 >= l)
            {
                if(i == 0)
                {
                    lastIndex = maxIndex;
                    dayStart.add(5, -1);
                } else
                {
                    lastIndex = i - 1;
                }
                break;
            }
            i++;
        } while(true);
    }

    public long getNextExecTime(Job job)
    {
        if(!$assertionsDisabled && job == null)
            throw new AssertionError();
        if(!$assertionsDisabled && lastIndex > maxIndex)
            throw new AssertionError();
        int i = job.getExecCount();
        if(repeatCount != -1L && (long)i >= repeatCount)
            return -1L;
        long l3 = dayStart.getTimeInMillis();
        long l;
        if(lastIndex == maxIndex)
        {
            lastIndex = 0;
            dayStart.add(5, 1);
            long l1 = JobSchedulerUtils.getDayLightTimeShift(dayStart.getTimeInMillis(), times[0]);
            l = times[0] + dayStart.getTimeInMillis() + l1;
            if(!$assertionsDisabled && l < dayStart.getTimeInMillis())
                throw new AssertionError();
        } else
        {
            long l2 = JobSchedulerUtils.getDayLightTimeShift(l3, times[++lastIndex]);
            l = times[lastIndex] + l3 + l2;
        }
        if(finishTime != -1L && finishTime <= l)
            return -1L;
        else
            return l;
    }

    private Calendar getDayStart(long l)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(l);
        calendar.set(9, 0);
        calendar.set(10, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar;
    }

    public String toString()
    {
        if(timesStr == null)
            timesStr = Utils.arr2Str(times);
        return L10n.format("SRVC.JOBS.TXT3", timesStr);
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

    private static final long DAY_MILLIS = 0x5265c00L;
    private String timesStr;
    private long times[];
    private int lastIndex;
    private Calendar dayStart;
    private final int maxIndex;
    private long repeatCount;
    private long finishTime;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(JobDailyScheduler.class).desiredAssertionStatus();
    }
}
