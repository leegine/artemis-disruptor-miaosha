// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.jobs.schedulers;

import com.fitechlabs.xtier.utils.Utils;
import com.fitechlabs.xtier.utils.UtilsException;
import java.util.Calendar;
import java.util.Date;

class JobSchedulerUtils
{

    JobSchedulerUtils()
    {
    }

    static long parseToLocalMillis(String s)
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        try
        {
            Date date = Utils.parseTime(s);
            return date.getTime() + (long)Calendar.getInstance().get(15);
        }
        catch(UtilsException utilsexception)
        {
            return -1L;
        }
    }

    static long getDayLightTimeShift(long l, long l1)
    {
        if(!$assertionsDisabled && l <= 0L)
            throw new AssertionError();
        if(!$assertionsDisabled && l1 < 0L)
        {
            throw new AssertionError();
        } else
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(l);
            long l2 = calendar.get(16);
            calendar.setTimeInMillis(l + l1);
            long l3 = calendar.get(16);
            return l2 - l3;
        }
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

    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(JobSchedulerUtils.class).desiredAssertionStatus();
    }
}
