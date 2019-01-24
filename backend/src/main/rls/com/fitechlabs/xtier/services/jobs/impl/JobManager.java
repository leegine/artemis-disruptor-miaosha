// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.jobs.impl;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.jobs.*;
import com.fitechlabs.xtier.services.jobs.scalers.JobsLinearTimeScaler;
import com.fitechlabs.xtier.services.log.LogService;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.services.objpool.ObjectPoolService;
import com.fitechlabs.xtier.services.objpool.threads.ThreadPool;
import com.fitechlabs.xtier.threads.SysThread;
import com.fitechlabs.xtier.utils.Utils;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.jobs.impl:
//            JobImpl, JobsGroupImpl, JobStateManager

class JobManager
{
    private static class SchedulerThread extends SysThread
    {

        protected void body()
        {
            do
            {
                checkInterrupted();
                synchronized(JobManager.mutex)
                {
                    for(; JobManager.activeJobs.isEmpty(); Utils.waitOn(JobManager.mutex));
                    long l = 0x7fffffffffffffffL;
                    long l1 = JobManager.scaler.getScaledTimeMillis();
                    Iterator iterator = JobManager.activeJobs.values().iterator();
                    do
                    {
                        if(!iterator.hasNext())
                            break;
                        final JobImpl job = (JobImpl)iterator.next();
                        if(!job.isEnqueued())
                        {
                            long l3 = findNextExecTime(job);
                            if(l3 == -1L)
                            {
                                job.onFinished();
                                iterator.remove();
                                if(job.isDeleteOnFinished())
                                    JobManager.jobs.remove(job.getName());
                            } else
                            {
                                long l4 = JobManager.scaler.getScaledTimeMillis();
                                if(l3 <= l4)
                                {
                                    job.onEnqueue();
                                    JobManager.workerPool.addTask(new Runnable() {

                                        public void run()
                                        {
                                            if(job.onInvokeStart())
                                            {
                                                try
                                                {
                                                    job.getBody().invoke(new JobContext() {

                                                        public Job getJob()
                                                        {
                                                            return job;
                                                        }

                                                        public Map getActiveJobs()
                                                        {
                                                            return JobManager.getActiveJobs();
                                                        }


                        {
                            super();
                        }
                                                    }
);
                                                }
                                                catch(JobException jobexception)
                                                {
                                                    job.onError(jobexception);
                                                    synchronized(JobManager.mutex)
                                                    {
                                                        JobManager.activeJobs.remove(job.getName());
                                                        if(job.isDeleteOnFinished())
                                                            JobManager.jobs.remove(job.getName());
                                                    }
                                                    return;
                                                }
                                                job.onInvokeEnd();
                                                synchronized(JobManager.mutex)
                                                {
                                                    JobManager.mutex.notifyAll();
                                                }
                                            }
                                        }



                {
                    super();
                }
                                    }
);
                                } else
                                {
                                    long l5 = l3 - l4;
                                    if(l5 < l - (l4 - l1))
                                    {
                                        l = l5;
                                        l1 = l4;
                                    }
                                }
                            }
                        }
                    } while(true);
                    if(l < 0x7fffffffffffffffL)
                    {
                        l -= JobManager.scaler.getScaledTimeMillis() - l1;
                        long l2 = JobManager.scaler.scale(l);
                        if(l2 > 0L)
                            Utils.waitOn(JobManager.mutex, l2);
                    } else
                    if(!JobManager.activeJobs.isEmpty())
                        Utils.waitOn(JobManager.mutex);
                }
            } while(true);
        }

        private static long findNextExecTime(JobImpl jobimpl)
        {
            if(jobimpl.getRecordedExecTime() == -2L)
            {
                JobScheduler jobscheduler = jobimpl.getScheduler();
                JobCalendar jobcalendar = jobimpl.getCalendar();
                long l = jobscheduler.getNextExecTime(jobimpl);
                long l1 = l;
                long l2 = jobcalendar.getCycleSize(JobManager.scaler);
                do
                {
                    if(l < 0L)
                        return -1L;
                    if(l - l1 > l2)
                        return -1L;
                    if(jobcalendar.isSchedulableOn(l))
                    {
                        jobimpl.setRecordedExecTime(l);
                        return l;
                    }
                    l = jobscheduler.getNextExecTime(jobimpl);
                } while(true);
            } else
            {
                return jobimpl.getRecordedExecTime();
            }
        }

        SchedulerThread()
        {
            super("job-scheduler");
        }
    }


    JobManager()
    {
    }

    static void start(String s, Map map, Map map1, JobsTimeScaler jobstimescaler, JobsStore jobsstore, boolean flag, boolean flag1)
        throws JobException
    {
        if(!$assertionsDisabled && (s == null || map == null || map1 == null || jobstimescaler == null))
            throw new AssertionError();
        jobs = map;
        groups = map1;
        scaler = jobstimescaler;
        store = jobsstore;
        storeOnShutdown = flag1;
        scheduler = new SchedulerThread();
        activeJobs = new HashMap();
        workerPool = XtierKernel.getInstance().objpool().getThreadPool(s);
        log = XtierKernel.getInstance().log().getLogger("jobs");
        if(!$assertionsDisabled && workerPool == null)
            throw new AssertionError();
        JobStateManager.start(map);
        if(flag)
            try
            {
                restoreState();
            }
            catch(JobException jobexception)
            {
                log.warning(L10n.format("SRVC.JOBS.IMPL.WRN3"), jobexception);
            }
        Iterator iterator = map.values().iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            JobImpl jobimpl = (JobImpl)iterator.next();
            if(!jobimpl.isActive() && jobimpl.isAutostart())
                schedule(jobimpl.getName());
        } while(true);
        scheduler.start();
    }

    static void stop()
    {
        Utils.stopThread(scheduler);
        XtierKernel.getInstance().objpool().deleteThreadPool(workerPool.getName());
        synchronized(mutex)
        {
            if(storeOnShutdown)
                try
                {
                    storeState();
                }
                catch(JobException jobexception)
                {
                    log.warning(L10n.format("SRVC.JOBS.IMPL.WRN4"), jobexception);
                }
            activeJobs.clear();
            jobs.clear();
            groups.clear();
        }
        JobStateManager.stop();
        jobs = null;
        log = null;
        groups = null;
        scheduler = null;
        activeJobs = null;
        workerPool = null;
        scaler = null;
        store = null;
    }

    static Map getActiveJobs()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return Collections.unmodifiableMap(new HashMap(activeJobs));
        Exception exception;
        exception;
        throw exception;
    }

    static JobImpl getJob(String s)
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return (JobImpl)jobs.get(s);
        Exception exception;
        exception;
        throw exception;
    }

    static Map getAllJobs()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return Collections.unmodifiableMap(new HashMap(jobs));
        Exception exception;
        exception;
        throw exception;
    }

    static void schedule(String s, boolean flag)
        throws JobException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        synchronized(mutex)
        {
            JobImpl jobimpl = (JobImpl)jobs.get(s);
            if(jobimpl == null)
                throw new JobException(L10n.format("SRVC.JOBS.IMPL.ERR10", s));
            if(jobimpl.isActive())
                throw new JobException(L10n.format("SRVC.JOBS.IMPL.ERR11", s));
            jobimpl.setDeleteOnFinished(flag);
            jobimpl.onScheduled();
            activeJobs.put(s, jobimpl);
            mutex.notifyAll();
        }
    }

    static void schedule(String s)
        throws JobException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        synchronized(mutex)
        {
            JobImpl jobimpl = (JobImpl)jobs.get(s);
            if(jobimpl == null)
                throw new JobException(L10n.format("SRVC.JOBS.IMPL.ERR10", s));
            if(jobimpl.isActive())
                throw new JobException(L10n.format("SRVC.JOBS.IMPL.ERR11", s));
            jobimpl.onScheduled();
            activeJobs.put(s, jobimpl);
            mutex.notifyAll();
        }
    }

    static Job addJob(String s, JobBody jobbody, JobScheduler jobscheduler, JobCalendar jobcalendar)
        throws JobException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        if(!$assertionsDisabled && jobbody == null)
            throw new AssertionError();
        if(!$assertionsDisabled && jobscheduler == null)
            throw new AssertionError();
        if(!$assertionsDisabled && jobcalendar == null)
            throw new AssertionError();
        if(!$assertionsDisabled && scaler == null)
            throw new AssertionError();
        Object obj = mutex;
        JVM INSTR monitorenter ;
        if(jobs.containsKey(s))
            throw new JobException(L10n.format("SRVC.JOBS.IMPL.ERR12", s));
        JobImpl jobimpl = new JobImpl(s, jobbody, jobscheduler, jobcalendar, scaler, false);
        jobs.put(s, jobimpl);
        return jobimpl;
        Exception exception;
        exception;
        throw exception;
    }

    static boolean cancel(String s)
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        Object obj = mutex;
        JVM INSTR monitorenter ;
        JobImpl jobimpl;
        jobimpl = (JobImpl)activeJobs.remove(s);
        if(jobimpl == null)
            return false;
        jobimpl.onFinished();
        if(jobimpl.isDeleteOnFinished())
            jobs.remove(s);
        true;
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    static void cancelAll()
    {
        synchronized(mutex)
        {
            Iterator iterator = activeJobs.values().iterator();
            do
            {
                if(!iterator.hasNext())
                    break;
                JobImpl jobimpl = (JobImpl)iterator.next();
                iterator.remove();
                jobimpl.onFinished();
                if(jobimpl.isDeleteOnFinished())
                    jobs.remove(jobimpl.getName());
            } while(true);
        }
    }

    static boolean delete(String s)
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        Object obj = mutex;
        JVM INSTR monitorenter ;
        JobImpl jobimpl = (JobImpl)activeJobs.remove(s);
        if(jobimpl != null)
            jobimpl.onFinished();
        for(Iterator iterator = groups.values().iterator(); iterator.hasNext(); ((JobsGroup)iterator.next()).removeJob(s));
        return jobs.remove(s) != null;
        Exception exception;
        exception;
        throw exception;
    }

    static void deleteAll()
    {
        synchronized(mutex)
        {
            Iterator iterator = jobs.values().iterator();
            do
            {
                if(!iterator.hasNext())
                    break;
                JobImpl jobimpl = (JobImpl)iterator.next();
                iterator.remove();
                if(activeJobs.remove(jobimpl.getName()) != null)
                    jobimpl.onFinished();
            } while(true);
        }
    }

    static void setTimeScaler(JobsTimeScaler jobstimescaler)
    {
        if(jobstimescaler == null)
            jobstimescaler = DFLT_SCALER;
        synchronized(mutex)
        {
            long l = scaler != null ? scaler.getScaledTimeMillis() : System.currentTimeMillis();
            jobstimescaler.setScaledInitTime(l);
            scaler = jobstimescaler;
            for(Iterator iterator = jobs.values().iterator(); iterator.hasNext(); ((JobImpl)iterator.next()).setTimeScaler(jobstimescaler));
            mutex.notifyAll();
        }
    }

    static JobsTimeScaler getTimeScaler()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return scaler;
        Exception exception;
        exception;
        throw exception;
    }

    static JobsGroup createGroup(String s)
        throws JobException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        Object obj = mutex;
        JVM INSTR monitorenter ;
        if(groups.containsKey(s))
            throw new JobException(L10n.format("SRVC.JOBS.IMPL.ERR18", s));
        JobsGroupImpl jobsgroupimpl = new JobsGroupImpl(s);
        groups.put(s, jobsgroupimpl);
        return jobsgroupimpl;
        Exception exception;
        exception;
        throw exception;
    }

    static JobsGroup getGroup(String s)
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return (JobsGroupImpl)groups.get(s);
        Exception exception;
        exception;
        throw exception;
    }

    static void scheduleGroup(String s)
        throws JobException
    {
        scheduleGroup(s, false);
    }

    static void scheduleGroup(String s, boolean flag)
        throws JobException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        synchronized(mutex)
        {
            if(groups.containsKey(s))
            {
                JobsGroupImpl jobsgroupimpl = (JobsGroupImpl)groups.get(s);
                JobImpl jobimpl;
                for(Iterator iterator = jobsgroupimpl.getAllJobs().values().iterator(); iterator.hasNext(); schedule(jobimpl.getName(), flag))
                    jobimpl = (JobImpl)iterator.next();

            }
        }
    }

    static void cancelGroup(String s)
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        synchronized(mutex)
        {
            if(groups.containsKey(s))
            {
                JobsGroupImpl jobsgroupimpl = (JobsGroupImpl)groups.get(s);
                jobsgroupimpl.cancelAllJobs();
            }
        }
    }

    static void deleteGroup(String s, boolean flag)
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        synchronized(mutex)
        {
            JobsGroupImpl jobsgroupimpl = (JobsGroupImpl)groups.get(s);
            if(flag)
            {
                for(Iterator iterator = jobsgroupimpl.getAllJobs().values().iterator(); iterator.hasNext(); jobs.remove(((JobImpl)iterator.next()).getName()));
            }
            jobsgroupimpl.removeAllJobs();
            groups.remove(s);
        }
    }

    static Map getAllGroups()
    {
        return Collections.unmodifiableMap(groups);
    }

    static JobsStore getStore()
    {
        return store;
    }

    static void setStore(JobsStore jobsstore)
    {
        synchronized(mutex)
        {
            store = jobsstore;
        }
    }

    static void storeState()
        throws JobException
    {
        if(store == null)
            throw new JobException(L10n.format("SRVC.JOBS.IMPL.ERR19"));
        synchronized(mutex)
        {
            byte abyte0[] = JobStateManager.encodeState(activeJobs);
            store.storeState(abyte0);
        }
    }

    static void restoreState()
        throws JobException
    {
        if(store == null)
            throw new JobException(L10n.format("SRVC.JOBS.IMPL.ERR20"));
        synchronized(mutex)
        {
            byte abyte0[] = store.restoreState();
            Map map = JobStateManager.decodeState(abyte0);
            if(!$assertionsDisabled && map == null)
                throw new AssertionError();
            for(Iterator iterator = map.values().iterator(); iterator.hasNext(); reschedule((JobImpl)iterator.next()));
        }
    }

    private static void reschedule(JobImpl jobimpl)
    {
        JobImpl jobimpl1 = (JobImpl)jobs.get(jobimpl.getName());
        if(!$assertionsDisabled && jobimpl1 == null)
        {
            throw new AssertionError();
        } else
        {
            jobs.put(jobimpl.getName(), jobimpl);
            jobimpl.onRestored();
            activeJobs.put(jobimpl.getName(), jobimpl);
            mutex.notifyAll();
            return;
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

    private static Map jobs;
    private static Map groups;
    private static Map activeJobs;
    private static ThreadPool workerPool;
    private static SchedulerThread scheduler;
    private static JobsTimeScaler scaler;
    private static final JobsTimeScaler DFLT_SCALER = new JobsLinearTimeScaler();
    private static JobsStore store;
    private static Logger log;
    private static final Object mutex = new Object();
    private static boolean storeOnShutdown;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(JobManager.class).desiredAssertionStatus();
    }





}
