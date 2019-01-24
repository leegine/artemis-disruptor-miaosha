// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.jobs.scalers;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.jobs.JobsTimeScaler;
import com.fitechlabs.xtier.utils.ArgAssert;

public class JobsLinearTimeScaler
    implements JobsTimeScaler
{

    public JobsLinearTimeScaler()
    {
        this(1.0F);
    }

    public JobsLinearTimeScaler(float f)
    {
        mutex = new Object();
        ArgAssert.illegalRange(f > 0.0F, "factor", "factor > 0");
        factor = f;
        initTime = System.currentTimeMillis();
        scaledInitTime = System.currentTimeMillis();
    }

    public long getScaledTimeMillis()
    {
        if(factor == 1.0F && scaledInitTime - initTime == 0L)
            return System.currentTimeMillis();
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return calculateScaledTime();
        Exception exception;
        exception;
        throw exception;
    }

    public float getTimeFactor()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return factor;
        Exception exception;
        exception;
        throw exception;
    }

    public long scale(long l)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return (long)((float)l / factor);
        Exception exception;
        exception;
        throw exception;
    }

    public void setScaledInitTime(long l)
    {
        ArgAssert.illegalRange(l >= 0L, "initTime", "initTime >= 0");
        synchronized(mutex)
        {
            initTime = System.currentTimeMillis();
            scaledInitTime = l;
        }
    }

    private long calculateScaledTime()
    {
        if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            throw new AssertionError();
        else
            return scaledInitTime + (long)((float)(System.currentTimeMillis() - initTime) * factor);
    }

    public String toString()
    {
        return L10n.format("SRVC.JOBS.TXT10", new Float(factor), new Long(initTime), new Long(scaledInitTime));
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

    public static final float FACTOR_NORMAL = 1F;
    public static final float FACTOR_1MIN_10SEC = 6F;
    public static final float FACTOR_1HOUR_10SEC = 360F;
    public static final float FACTOR_1DAY_10SEC = 8640F;
    public static final float FACTOR_1WEEK_10SEC = 60480F;
    public static final float FACTOR_1MONTH_10SEC = 1814400F;
    public static final float FACTOR_1HOUR_1MIN = 60F;
    public static final float FACTOR_1DAY_1MIN = 1440F;
    public static final float FACTOR_1WEEK_1MIN = 10080F;
    public static final float FACTOR_1MONTH_1MIN = 302400F;
    private float factor;
    private long initTime;
    private long scaledInitTime;
    private final Object mutex;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(JobsLinearTimeScaler.class).desiredAssertionStatus();
    }
}
