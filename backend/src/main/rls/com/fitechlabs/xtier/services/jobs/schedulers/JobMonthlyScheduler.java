// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.jobs.schedulers;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.jobs.*;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.Utils;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.jobs.schedulers:
//            JobSchedulerUtils

public class JobMonthlyScheduler
    implements JobScheduler
{

    public JobMonthlyScheduler(String s)
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
                long l = parseMonthTime(s1);
                ArgAssert.illegalArg(l >= 0L, "configStr");
                ArgAssert.illegalArg(l <= 0x9fa52400L, "configStr");
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

    public JobMonthlyScheduler(long al[])
    {
        repeatCount = -1L;
        finishTime = -1L;
        ArgAssert.nullArg(al, "times");
        ArgAssert.illegalArg(al.length > 0, "times");
        for(int i = 0; i < al.length; i++)
        {
            ArgAssert.illegalArg(al[i] >= 0L, "times");
            ArgAssert.illegalArg(al[i] <= 0x9fa52400L, "times");
        }

        times = (long[])al.clone();
        Arrays.sort(times);
        maxIndex = al.length - 1;
    }

    public JobMonthlyScheduler(String s, long l)
    {
        this(s);
        ArgAssert.illegalRange(l > 0L, "repeatCount", "repeatCount > 0");
        repeatCount = l;
    }

    public JobMonthlyScheduler(long al[], long l)
    {
        this(al);
        ArgAssert.illegalRange(l > 0L, "repeatCount", "repeatCount > 0");
        repeatCount = l;
    }

    public JobMonthlyScheduler(String s, Date date)
    {
        this(s);
        ArgAssert.nullArg(date, "finishTime");
        finishTime = date.getTime();
    }

    public JobMonthlyScheduler(long al[], Date date)
    {
        this(al);
        ArgAssert.nullArg(date, "finishTime");
        finishTime = date.getTime();
    }

    private long parseMonthTime(String s)
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        int i = s.indexOf(' ');
        if(i < 0)
            return -1L;
        int j = parseMonthDay(s.substring(0, i));
        if(j != -1)
        {
            String s1 = s.substring(i).trim();
            if(s1.length() > 0)
            {
                long l = JobSchedulerUtils.parseToLocalMillis(s1);
                if(l >= 0L)
                    return (long)j * 0x5265c00L + l;
            }
        }
        return -1L;
    }

    private int parseMonthDay(String s)
    {
        int i;
        try
        {
            i = Integer.decode(s).intValue();
            if(i < 1 || i > 31)
                return -1;
        }
        catch(NumberFormatException numberformatexception)
        {
            return -1;
        }
        return i - 1;
    }

    public void reset(JobsTimeScaler jobstimescaler)
    {
        if(!$assertionsDisabled && jobstimescaler == null)
            throw new AssertionError();
        lastIndex = maxIndex;
        long l = jobstimescaler.getScaledTimeMillis();
        monthStart = getMonthStart(l);
        long l1 = monthStart.getTimeInMillis();
        int i = 0;
        do
        {
            if(i >= times.length)
                break;
            long l2 = times[i] + l1 + JobSchedulerUtils.getDayLightTimeShift(l1, times[i]);
            if(l2 >= l)
            {
                if(isSameMonth(l2, monthStart))
                    if(i == 0)
                    {
                        lastIndex = maxIndex;
                        monthStart.add(2, -1);
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
        if(repeatCount != -1L && (long)job.getExecCount() >= repeatCount)
            return -1L;
        long l1 = monthStart.getTimeInMillis();
        long l2 = 0L;
        if(lastIndex < maxIndex)
            l2 = JobSchedulerUtils.getDayLightTimeShift(l1, times[lastIndex + 1]);
        long l;
        if(lastIndex == maxIndex || !isSameMonth(l1 + times[lastIndex + 1] + l2, monthStart))
        {
            lastIndex = 0;
            do
            {
                monthStart.add(2, 1);
                long l3 = JobSchedulerUtils.getDayLightTimeShift(monthStart.getTimeInMillis(), times[0]);
                l = times[0] + monthStart.getTimeInMillis() + l3;
            } while(!isSameMonth(l, monthStart));
        } else
        {
            long l4 = JobSchedulerUtils.getDayLightTimeShift(l1, times[++lastIndex]);
            l = times[lastIndex] + l1 + l4;
        }
        if(finishTime != -1L && finishTime <= l)
            return -1L;
        else
            return l;
    }

    private Calendar getMonthStart(long l)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(l);
        calendar.set(9, 0);
        calendar.set(5, 1);
        calendar.set(10, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar;
    }

    private boolean isSameMonth(long l, Calendar calendar)
    {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(l);
        return calendar1.get(1) == calendar.get(1) && calendar1.get(2) == calendar.get(2);
    }

    public String toString()
    {
        if(timesStr == null)
            timesStr = Utils.arr2Str(times);
        return L10n.format("SRVC.JOBS.TXT5", timesStr);
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
    private static final long MONTH_MILLIS = 0x9fa52400L;
    private String timesStr;
    private long times[];
    private int lastIndex;
    private Calendar monthStart;
    private final int maxIndex;
    private long repeatCount;
    private long finishTime;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(JobMonthlyScheduler.class).desiredAssertionStatus();
    }
}
