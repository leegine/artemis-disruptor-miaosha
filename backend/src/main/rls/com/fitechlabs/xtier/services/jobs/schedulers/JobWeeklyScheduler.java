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

public class JobWeeklyScheduler
    implements JobScheduler, Serializable
{

    public JobWeeklyScheduler(String s)
    {
        repeatCount = -1L;
        finishTime = -1L;
        ArgAssert.nullArg(s, "configStr");
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
                long l = parseWeekTime(s1);
                ArgAssert.illegalArg(l >= 0L, "configStr");
                ArgAssert.illegalArg(l <= 0x240c8400L, "configStr");
                arraylist.add(new Long(l));
            }
        } while(true);
        ArgAssert.illegalArg(!arraylist.isEmpty(), "configStr");
        Collections.sort(arraylist);
        int i = arraylist.size();
        times = new long[i];
        for(int j = 0; j < i; j++)
            times[j] = ((Long)arraylist.get(j)).longValue();

        maxIndex = times.length - 1;
    }

    public JobWeeklyScheduler(long al[])
    {
        repeatCount = -1L;
        finishTime = -1L;
        ArgAssert.nullArg(al, "times");
        ArgAssert.illegalArg(al.length > 0, "times");
        for(int i = 0; i < al.length; i++)
        {
            ArgAssert.illegalArg(al[i] >= 0L, "times");
            ArgAssert.illegalArg(al[i] <= 0x240c8400L, "times");
        }

        times = (long[])al.clone();
        Arrays.sort(times);
        maxIndex = al.length - 1;
    }

    public JobWeeklyScheduler(String s, long l)
    {
        this(s);
        ArgAssert.illegalRange(l > 0L, "repeatCount", "repeatCount > 0");
        repeatCount = l;
    }

    public JobWeeklyScheduler(long al[], long l)
    {
        this(al);
        ArgAssert.illegalRange(l > 0L, "repeatCount", "repeatCount > 0");
        repeatCount = l;
    }

    public JobWeeklyScheduler(String s, Date date)
    {
        this(s);
        ArgAssert.nullArg(date, "finishTime");
        finishTime = date.getTime();
    }

    public JobWeeklyScheduler(long al[], Date date)
    {
        this(al);
        ArgAssert.nullArg(date, "finishTime");
        finishTime = date.getTime();
    }

    private long parseWeekTime(String s)
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        if(s.length() < 3)
            return -1L;
        int i = parseWeekDay(s.substring(0, 3).toLowerCase());
        if(i != -1)
        {
            String s1 = s.substring(3).trim();
            if(s1.length() > 0)
            {
                long l = JobSchedulerUtils.parseToLocalMillis(s1);
                if(l >= 0L)
                    return (long)i * 0x5265c00L + l;
            }
        }
        return -1L;
    }

    private int parseWeekDay(String s)
    {
        if(s.equals("sun"))
            return 0;
        if(s.equals("mon"))
            return 1;
        if(s.equals("tue") || s.equals("tus"))
            return 2;
        if(s.equals("wed") || s.equals("wen"))
            return 3;
        if(s.equals("thu"))
            return 4;
        if(s.equals("fri"))
            return 5;
        return !s.equals("sat") ? -1 : 6;
    }

    public void reset(JobsTimeScaler jobstimescaler)
    {
        if(!$assertionsDisabled && jobstimescaler == null)
            throw new AssertionError();
        lastIndex = maxIndex;
        long l = jobstimescaler.getScaledTimeMillis();
        weekStart = getWeekStart(l);
        int i = 0;
        do
        {
            if(i >= times.length)
                break;
            long l1 = JobSchedulerUtils.getDayLightTimeShift(weekStart.getTimeInMillis(), times[i]);
            if(times[i] + weekStart.getTimeInMillis() + l1 >= l)
            {
                if(i == 0)
                {
                    lastIndex = maxIndex;
                    weekStart.add(4, -1);
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
        long l3 = weekStart.getTimeInMillis();
        long l;
        if(lastIndex == maxIndex)
        {
            lastIndex = 0;
            weekStart.add(4, 1);
            long l1 = JobSchedulerUtils.getDayLightTimeShift(weekStart.getTimeInMillis(), times[0]);
            l = times[0] + weekStart.getTimeInMillis() + l1;
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

    private Calendar getWeekStart(long l)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(l);
        calendar.set(9, 0);
        calendar.set(7, 1);
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
        return L10n.format("SRVC.JOBS.TXT4", timesStr);
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
    private static final long WEEK_MILLIS = 0x240c8400L;
    private String timesStr;
    private long times[];
    private int lastIndex;
    private Calendar weekStart;
    private final int maxIndex;
    private long repeatCount;
    private long finishTime;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(JobWeeklyScheduler.class).desiredAssertionStatus();
    }
}
