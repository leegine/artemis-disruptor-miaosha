// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.jobs.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.jobs.*;
import com.fitechlabs.xtier.utils.ArgAssert;
import java.io.Serializable;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.jobs.impl:
//            JobManager

class JobImpl
    implements Job, Serializable
{

    JobImpl(String s, JobBody jobbody, JobScheduler jobscheduler, JobCalendar jobcalendar, JobsTimeScaler jobstimescaler, boolean flag)
    {
        name = null;
        isAutostart = false;
        isActive = false;
        scheduleTime = 0L;
        lastInvokeTime = 0L;
        lastInvokeDuration = 0L;
        finishTime = 0L;
        execCount = 0;
        isDeleteOnFinished = false;
        recordedExecTime = -2L;
        nextExecTime = -1L;
        isEnqueued = false;
        isInvoking = false;
        isCancelled = false;
        listeners = new ArrayList();
        if(!$assertionsDisabled && (s == null || jobbody == null || jobscheduler == null || jobcalendar == null || jobstimescaler == null))
        {
            throw new AssertionError();
        } else
        {
            name = s;
            body = jobbody;
            scheduler = jobscheduler;
            calendar = jobcalendar;
            isAutostart = flag;
            scaler = jobstimescaler;
            return;
        }
    }

    public String getName()
    {
        return name;
    }

    public JobBody getBody()
    {
        return body;
    }

    public JobScheduler getScheduler()
    {
        return scheduler;
    }

    public JobCalendar getCalendar()
    {
        return calendar;
    }

    boolean isAutostart()
    {
        return isAutostart;
    }

    boolean isDeleteOnFinished()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return isDeleteOnFinished;
        Exception exception;
        exception;
        throw exception;
    }

    void setDeleteOnFinished(boolean flag)
    {
        synchronized(mutex)
        {
            isDeleteOnFinished = flag;
        }
    }

    public boolean isActive()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return isActive;
        Exception exception;
        exception;
        throw exception;
    }

    public int getExecCount()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return execCount;
        Exception exception;
        exception;
        throw exception;
    }

    public long getLastInvokeTime()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return lastInvokeTime;
        Exception exception;
        exception;
        throw exception;
    }

    public long getLastInvokeDuration()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return lastInvokeDuration;
        Exception exception;
        exception;
        throw exception;
    }

    public long getScheduleTime()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return scheduleTime;
        Exception exception;
        exception;
        throw exception;
    }

    public long getFinishTime()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return finishTime;
        Exception exception;
        exception;
        throw exception;
    }

    public void join()
        throws InterruptedException
    {
        synchronized(mutex)
        {
            while(isActive)
                mutex.wait();
        }
    }

    public boolean join(long l)
        throws InterruptedException
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        if(isActive)
            mutex.wait(l);
        return !isActive;
        Exception exception;
        exception;
        throw exception;
    }

    public void addListener(JobListener joblistener)
    {
        ArgAssert.nullArg(joblistener, "listener");
        synchronized(mutex)
        {
            listeners.add(joblistener);
        }
    }

    public List getAllListeners()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return Collections.unmodifiableList(new ArrayList(listeners));
        Exception exception;
        exception;
        throw exception;
    }

    public boolean removeListener(JobListener joblistener)
    {
        ArgAssert.nullArg(joblistener, "listener");
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return listeners.remove(joblistener);
        Exception exception;
        exception;
        throw exception;
    }

    public long getRecordedExecTime()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return recordedExecTime;
        Exception exception;
        exception;
        throw exception;
    }

    void setRecordedExecTime(long l)
    {
        synchronized(mutex)
        {
            recordedExecTime = l;
            nextExecTime = l;
        }
    }

    public long getNextExecTime()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return nextExecTime;
        Exception exception;
        exception;
        throw exception;
    }

    public boolean cancel()
    {
        return JobManager.cancel(name);
    }

    boolean isEnqueued()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return isEnqueued;
        Exception exception;
        exception;
        throw exception;
    }

    void onScheduled()
    {
        synchronized(mutex)
        {
            if(!$assertionsDisabled && isActive)
                throw new AssertionError();
            scheduler.reset(scaler);
            isActive = true;
            lastInvokeTime = 0L;
            lastInvokeDuration = 0L;
            finishTime = 0L;
            execCount = 0;
            isEnqueued = false;
            isInvoking = false;
            isCancelled = false;
            recordedExecTime = -2L;
            nextExecTime = -1L;
            scheduleTime = scaler.getScaledTimeMillis();
        }
        notifyListeners(1);
    }

    void onRestored()
    {
        synchronized(mutex)
        {
            scheduler.reset(scaler);
            isActive = true;
        }
    }

    void onEnqueue()
    {
        synchronized(mutex)
        {
            if(!$assertionsDisabled && isEnqueued)
                throw new AssertionError("Job cannot be enqueued more than once: " + this);
            isEnqueued = true;
        }
    }

    boolean onInvokeStart()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        if(!$assertionsDisabled && !isActive)
            throw new AssertionError("Job: " + this);
        if(!$assertionsDisabled && !isEnqueued)
            throw new AssertionError("Job: " + this);
        if(!$assertionsDisabled && isInvoking)
            throw new AssertionError("Job: " + this);
        if(isCancelled)
            return false;
        isInvoking = true;
        recordedExecTime = -2L;
        lastInvokeTime = scaler.getScaledTimeMillis();
        obj;
        JVM INSTR monitorexit ;
          goto _L1
        Exception exception;
        exception;
        throw exception;
_L1:
        notifyListeners(2);
        return true;
    }

    void onInvokeEnd()
    {
        boolean flag = false;
        synchronized(mutex)
        {
            if(!$assertionsDisabled && !isActive)
                throw new AssertionError();
            lastInvokeDuration = scaler.getScaledTimeMillis() - lastInvokeTime;
            execCount++;
            isInvoking = false;
            isEnqueued = false;
            if(isCancelled)
            {
                isActive = false;
                finishTime = scaler.getScaledTimeMillis();
                mutex.notifyAll();
                flag = true;
            }
        }
        notifyListeners(3);
        if(flag)
            notifyListeners(4);
    }

    void onError(JobException jobexception)
    {
        synchronized(mutex)
        {
            finishTime = scaler.getScaledTimeMillis();
            lastInvokeDuration = finishTime - lastInvokeTime;
            execCount++;
            isActive = false;
            mutex.notifyAll();
        }
        synchronized(listeners)
        {
            int i = 0;
            for(int j = listeners.size(); i < j; i++)
                ((JobListener)listeners.get(i)).onError(this, jobexception);

        }
    }

    void onFinished()
    {
label0:
        {
            synchronized(mutex)
            {
                if(!isActive)
                    break MISSING_BLOCK_LABEL_64;
                if(isEnqueued)
                    isCancelled = true;
                if(!isInvoking)
                    break label0;
            }
            return;
        }
        isActive = false;
        finishTime = scaler.getScaledTimeMillis();
        mutex.notifyAll();
        obj;
        JVM INSTR monitorexit ;
          goto _L1
        exception;
        throw exception;
_L1:
        notifyListeners(4);
        return;
    }

    void notifyListeners(int i)
    {
        if(!$assertionsDisabled && Thread.holdsLock(mutex))
            throw new AssertionError();
        synchronized(listeners)
        {
            int j = 0;
            for(int k = listeners.size(); j < k; j++)
                ((JobListener)listeners.get(j)).onJobEvent(i, this);

        }
    }

    void setTimeScaler(JobsTimeScaler jobstimescaler)
    {
        if(!$assertionsDisabled && jobstimescaler == null)
            throw new AssertionError();
        synchronized(mutex)
        {
            scaler = jobstimescaler;
        }
    }

    void setExecCount(int i)
    {
        synchronized(mutex)
        {
            execCount = i;
        }
    }

    void setScheduleTime(long l)
    {
        scheduleTime = l;
    }

    void setLastInvokeTime(long l)
    {
        lastInvokeTime = l;
    }

    void setLastInvokeDuration(long l)
    {
        lastInvokeDuration = l;
    }

    public String toString()
    {
        return L10n.format("SRVC.JOBS.TXT2", new Object[] {
            name, Boolean.valueOf(isActive), new Integer(execCount), scheduler, calendar, body
        });
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

    static final long EXEC_TIME_NONE = -2L;
    private String name;
    private boolean isAutostart;
    private boolean isActive;
    private long scheduleTime;
    private long lastInvokeTime;
    private long lastInvokeDuration;
    private long finishTime;
    private int execCount;
    private boolean isDeleteOnFinished;
    private long recordedExecTime;
    private long nextExecTime;
    private boolean isEnqueued;
    private boolean isInvoking;
    private boolean isCancelled;
    private JobScheduler scheduler;
    private JobCalendar calendar;
    private JobsTimeScaler scaler;
    private JobBody body;
    private List listeners;
    private final Object mutex = new Object();
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(JobImpl.class).desiredAssertionStatus();
    }
}
