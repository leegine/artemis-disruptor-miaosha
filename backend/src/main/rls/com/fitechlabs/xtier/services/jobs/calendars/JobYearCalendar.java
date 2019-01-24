// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.jobs.calendars;

import com.fitechlabs.xtier.services.jobs.JobCalendar;
import com.fitechlabs.xtier.services.jobs.JobsTimeScaler;
import com.fitechlabs.xtier.utils.ArgAssert;
import java.util.*;

public class JobYearCalendar
    implements JobCalendar
{

    public JobYearCalendar()
    {
        cal = Calendar.getInstance();
        exclDaysOfWeek = null;
        exclDaysOfMonth = null;
        exclDaysOfYear = null;
    }

    public JobYearCalendar(String s)
    {
        cal = Calendar.getInstance();
        exclDaysOfWeek = null;
        exclDaysOfMonth = null;
        exclDaysOfYear = null;
        ArgAssert.nullArg(s, "excludedDaysOfWeekStr");
        exclDaysOfWeek = parseDaysOfWeek(s);
        sort();
    }

    public JobYearCalendar(int ai[])
    {
        cal = Calendar.getInstance();
        exclDaysOfWeek = null;
        exclDaysOfMonth = null;
        exclDaysOfYear = null;
        ArgAssert.nullArg(ai, "excludedDaysOfWeek");
        exclDaysOfWeek = ai;
        sort();
    }

    public JobYearCalendar(String s, int ai[], int ai1[])
    {
        cal = Calendar.getInstance();
        exclDaysOfWeek = null;
        exclDaysOfMonth = null;
        exclDaysOfYear = null;
        if(s != null)
            exclDaysOfWeek = parseDaysOfWeek(s);
        exclDaysOfMonth = ai;
        exclDaysOfYear = ai1;
        sort();
    }

    public JobYearCalendar(int ai[], int ai1[], int ai2[])
    {
        cal = Calendar.getInstance();
        exclDaysOfWeek = null;
        exclDaysOfMonth = null;
        exclDaysOfYear = null;
        exclDaysOfWeek = ai;
        exclDaysOfMonth = ai1;
        exclDaysOfYear = ai2;
        sort();
    }

    public synchronized boolean isSchedulableOn(long l)
    {
        if(exclDaysOfWeek != null)
        {
            int i = getDayOfWeek(l);
            if(Arrays.binarySearch(exclDaysOfWeek, i) >= 0)
                return false;
        }
        if(exclDaysOfMonth != null)
        {
            int j = getDayOfMonth(l);
            if(Arrays.binarySearch(exclDaysOfMonth, j) >= 0)
                return false;
        }
        if(exclDaysOfYear != null)
        {
            int k = getDayOfYear(l);
            if(Arrays.binarySearch(exclDaysOfYear, k) >= 0)
                return false;
        }
        return true;
    }

    public synchronized long getCycleSize(JobsTimeScaler jobstimescaler)
    {
        cal.setTimeInMillis(jobstimescaler.getScaledTimeMillis());
        long l = getYearStart(cal);
        cal.add(1, 1);
        long l1 = getYearStart(cal);
        if(!$assertionsDisabled && l1 <= l)
            throw new AssertionError();
        else
            return l1 - l;
    }

    private long getYearStart(Calendar calendar)
    {
        if(!$assertionsDisabled && calendar == null)
        {
            throw new AssertionError();
        } else
        {
            calendar.set(2, 0);
            calendar.set(5, 0);
            calendar.set(10, 0);
            calendar.set(12, 0);
            calendar.set(13, 0);
            calendar.set(14, 0);
            return calendar.getTimeInMillis();
        }
    }

    private int getDayOfWeek(long l)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(l);
        return calendar.get(7);
    }

    private int getDayOfMonth(long l)
    {
        cal.setTimeInMillis(l);
        return cal.get(5);
    }

    private int getDayOfYear(long l)
    {
        cal.setTimeInMillis(l);
        return cal.get(6);
    }

    private int[] parseDaysOfWeek(String s)
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        StringTokenizer stringtokenizer = new StringTokenizer(s, ",");
        int ai[] = new int[stringtokenizer.countTokens()];
        int i = 0;
        StringTokenizer stringtokenizer1 = stringtokenizer;
        do
        {
            if(!stringtokenizer1.hasMoreElements())
                break;
            String s1 = ((String)stringtokenizer1.nextElement()).trim();
            if(s1.length() > 0)
            {
                int j = parseWeekDay(s1);
                ArgAssert.illegalArg(j > -1, "daysOfWeekStr");
                ai[i++] = j;
            }
        } while(true);
        return ai;
    }

    private int parseWeekDay(String s)
    {
        if(s.equals("sun"))
            return 1;
        if(s.equals("mon"))
            return 2;
        if(s.equals("tus"))
            return 3;
        if(s.equals("wen"))
            return 4;
        if(s.equals("thu"))
            return 5;
        if(s.equals("fri"))
            return 6;
        return !s.equals("sat") ? -1 : 7;
    }

    private void sort()
    {
        if(exclDaysOfWeek != null)
            Arrays.sort(exclDaysOfWeek);
        if(exclDaysOfMonth != null)
            Arrays.sort(exclDaysOfMonth);
        if(exclDaysOfYear != null)
            Arrays.sort(exclDaysOfYear);
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

    private Calendar cal;
    private int exclDaysOfWeek[] = {
        1, 7
    };
    private int exclDaysOfMonth[];
    private int exclDaysOfYear[];
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(JobYearCalendar.class).desiredAssertionStatus();
    }
}
