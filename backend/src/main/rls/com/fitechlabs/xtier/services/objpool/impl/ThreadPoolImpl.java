// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.objpool.impl;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.log.LogService;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.services.objpool.threads.*;
import com.fitechlabs.xtier.threads.SysThread;
import com.fitechlabs.xtier.threads.SysThreadGroup;
import com.fitechlabs.xtier.utils.*;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.objpool.impl:
//            ThreadPoolStatsImpl

class ThreadPoolImpl
    implements ThreadPool
{
    private class WorkerThread extends SysThread
    {

        public void interrupt()
        {
            synchronized(mutex)
            {
                interrupted++;
            }
            super.interrupt();
        }

        protected void body()
        {
_L2:
            checkInterrupted();
            if(true)
            {
label0:
                {
                    synchronized(mutex)
                    {
                        if(!queue.isEmpty())
                            break MISSING_BLOCK_LABEL_55;
                        if(!isStopped)
                            break label0;
                    }
                    return;
                }
            }
            obj = mutex;
            JVM INSTR monitorenter ;
            Utils.waitOn(mutex);
            TaskWrapper taskwrapper;
            free--;
            taskwrapper = (TaskWrapper)queue.get();
            if(queue.isEmpty())
                mutex.notifyAll();
            obj;
            JVM INSTR monitorexit ;
            break MISSING_BLOCK_LABEL_111;
            exception;
            throw exception;
            onTaskStarted(taskwrapper);
            taskwrapper.getTask().run();
            onTaskFinished(taskwrapper);
            break MISSING_BLOCK_LABEL_152;
            Exception exception1;
            exception1;
            onTaskFinished(taskwrapper);
            throw exception1;
            continue; /* Loop/switch isn't completed */
            RuntimeException runtimeexception;
            runtimeexception;
            XtierKernel.getInstance().log().getLogger("thread-pool").error(L10n.format("SRVC.OBJPOOL.ERR12", name), runtimeexception);
            if(true) goto _L2; else goto _L1
_L1:
            synchronized(mutex)
            {
                int i = 0;
                i = resizePolicy.removeAfterTaskFinished(stats);
                if(i > size)
                    logger.error(L10n.format("SRVC.OBJPOOL.ERR20", new Integer(size), new Integer(i)));
                if(i < 0)
                    logger.error(L10n.format("SRVC.OBJPOOL.ERR19", new Integer(i)));
                if(i > 0)
                    stopWorkers(i);
                stats.onTaskFinished(ThreadPoolImpl.this);
                free++;
            }
            JVM INSTR ret 6;
        }

        protected void cleanup()
        {
            synchronized(mutex)
            {
                interrupted--;
                free--;
                if(lazy && size > workers.size() && getAdjustedFree() < queue.getCount())
                {
                    int i = queue.getCount() - getAdjustedFree();
                    int j = size - workers.size();
                    createWorkers(i < j ? i : j);
                }
            }
        }

        private Logger logger;

        public WorkerThread()
        {
            super(name + "-worker", priority, sysGrp);
            logger = XtierKernel.getInstance().log().getLogger("thread-pool");
        }
    }

    private class TaskWrapper
    {

        Runnable getTask()
        {
            return task;
        }

        long getEnqueueTime()
        {
            return enqueueTime;
        }

        private final Runnable task;
        private final long enqueueTime;

        TaskWrapper(Runnable runnable, long l)
        {
            super();
            task = runnable;
            enqueueTime = l;
        }
    }


    ThreadPoolImpl(String s, int i, boolean flag, int j, ThreadPoolResizePolicy threadpoolresizepolicy)
    {
        queue = new FifoQueue();
        isStopped = false;
        mutex = new Object();
        sysGrp = null;
        listeners = new ArrayList();
        free = 0;
        interrupted = 0;
        ArgAssert.nullArg(s, "name");
        ArgAssert.illegalArg(i > 0, "size");
        ArgAssert.illegalRange(j >= 1 && j <= 10, "priority", "priority >= Thread.MIN_PRIORITY && priority <= Thread.MAX_PRIORITY");
        ArgAssert.nullArg(threadpoolresizepolicy, "resizePolicy");
        synchronized(mutex)
        {
            name = s;
            priority = j;
            size = i;
            resizePolicy = threadpoolresizepolicy;
            lazy = flag;
            sysGrp = SysThreadGroup.getNewGroup(s);
            workers = new ArrayList(i);
            if(!flag)
                createWorkers(i);
            stats = new ThreadPoolStatsImpl(this);
        }
    }

    public boolean isLazy()
    {
        return lazy;
    }

    public String getName()
    {
        return name;
    }

    public int getPriority()
    {
        return priority;
    }

    public int getSize()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        checkState();
        return size;
        Exception exception;
        exception;
        throw exception;
    }

    public int getBusyThreads()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        checkState();
        return workers.size() - getAdjustedFree();
        Exception exception;
        exception;
        throw exception;
    }

    public int getFreeThreads()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        checkState();
        return (size + getAdjustedFree()) - workers.size();
        Exception exception;
        exception;
        throw exception;
    }

    private int getAdjustedFree()
    {
        if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            throw new AssertionError();
        else
            return free <= interrupted ? 0 : free - interrupted;
    }

    public boolean addListener(ThreadPoolListener threadpoollistener)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        checkState();
        if(listeners.contains(threadpoollistener))
            return false;
        listeners.add(threadpoollistener);
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public boolean removeListener(ThreadPoolListener threadpoollistener)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        checkState();
        return listeners.remove(threadpoollistener);
        Exception exception;
        exception;
        throw exception;
    }

    public List getAllListeners()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        checkState();
        return Collections.unmodifiableList(listeners);
        Exception exception;
        exception;
        throw exception;
    }

    public void resize(int i)
    {
label0:
        {
            ArgAssert.illegalArg(i > 0, "size");
            synchronized(mutex)
            {
                checkState();
                if(size != i)
                    break label0;
            }
            return;
        }
        size = i;
        int j = Math.abs(i - workers.size());
        if(i > workers.size())
        {
            workers.ensureCapacity(i);
            if(!lazy)
                createWorkers(i);
        } else
        {
            for(int k = 0; k < j; k++)
                ((WorkerThread)workers.remove(workers.size() - 1)).interrupt();

            workers.trimToSize();
        }
        obj;
        JVM INSTR monitorexit ;
          goto _L1
        exception;
        throw exception;
_L1:
    }

    private void createWorkers(int i)
    {
        if(!$assertionsDisabled && i < 0)
            throw new AssertionError();
        if(i == 0)
            return;
        for(int j = 0; j < i; j++)
            createWorker();

    }

    private void createWorker()
    {
        WorkerThread workerthread = new WorkerThread();
        workers.add(workerthread);
        free++;
        workerthread.start();
    }

    private void stopWorkers(int i)
    {
        if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            throw new AssertionError();
        if(!$assertionsDisabled && i < 0)
            throw new AssertionError();
        if(i == 0)
            return;
        for(int j = 0; j < i; j++)
            ((WorkerThread)workers.remove(workers.size() - 1)).interrupt();

    }

    public void resize(float f)
    {
        ArgAssert.illegalArg(f > 0.0F, "factor");
        synchronized(mutex)
        {
            checkState();
            int i = (int)((float)size * f);
            ArgAssert.illegalArg(i > 0, "factor");
            resize(size);
        }
    }

    public int getBacklog()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        checkState();
        return queue.getCount();
        Exception exception;
        exception;
        throw exception;
    }

    public void addTask(Runnable runnable)
    {
        ArgAssert.nullArg(runnable, "task");
        synchronized(mutex)
        {
            checkState();
            int i = resizePolicy.addBeforeTaskEnqueued(stats);
            if(i < 0)
                throw new IllegalStateException(L10n.format("SRVC.OBJPOOL.ERR19", new Integer(i)));
            size += i;
            queue.add(new TaskWrapper(runnable, System.currentTimeMillis()));
            if(i > 0)
            {
                if(lazy)
                {
                    if(getAdjustedFree() < queue.getCount())
                    {
                        int j = queue.getCount() - getAdjustedFree();
                        createWorkers(i < j ? i : j);
                    }
                } else
                {
                    createWorkers(i);
                }
            } else
            if(size > workers.size())
            {
                if(!$assertionsDisabled && !lazy)
                    throw new AssertionError();
                if(getAdjustedFree() < queue.getCount())
                {
                    int k = queue.getCount() - getAdjustedFree();
                    int l = size - workers.size();
                    createWorkers(l < k ? l : k);
                }
            }
            stats.onTaskAdded(this);
            onTaskEnqueued(runnable);
            if(queue.getCount() == 1)
                mutex.notifyAll();
        }
    }

    private void onTaskEnqueued(Runnable runnable)
    {
        if((runnable instanceof ThreadPoolNotifyable))
            ((ThreadPoolNotifyable)runnable).onTaskEnqueued(this);
        int i = 0;
        for(int j = listeners.size(); i < j; i++)
            ((ThreadPoolListener)listeners.get(i)).onTaskEnqueued(this, runnable);

    }

    private void onTaskStarted(TaskWrapper taskwrapper)
    {
        synchronized(mutex)
        {
            stats.onTaskStarted(taskwrapper.getEnqueueTime());
            if((taskwrapper.getTask() instanceof ThreadPoolNotifyable))
                ((ThreadPoolNotifyable)taskwrapper.getTask()).onTaskStarted(this);
            int i = 0;
            for(int j = listeners.size(); i < j; i++)
                ((ThreadPoolListener)listeners.get(i)).onTaskStarted(this, taskwrapper.getTask());

        }
    }

    private void onTaskFinished(TaskWrapper taskwrapper)
    {
        synchronized(mutex)
        {
            if((taskwrapper.getTask() instanceof ThreadPoolNotifyable))
                ((ThreadPoolNotifyable)taskwrapper.getTask()).onTaskFinished(this);
            int i = 0;
            for(int j = listeners.size(); i < j; i++)
                ((ThreadPoolListener)listeners.get(i)).onTaskFinished(this, taskwrapper.getTask());

        }
    }

    void stop()
    {
label0:
        {
            synchronized(mutex)
            {
                if(!isStopped)
                    break label0;
            }
            return;
        }
        isStopped = true;
        queue.clear();
        obj;
        JVM INSTR monitorexit ;
          goto _L1
        exception;
        throw exception;
_L1:
        sysGrp.stopAndDestroy();
        return;
    }

    void stopWait()
    {
label0:
        {
            synchronized(mutex)
            {
                if(!isStopped)
                    break label0;
            }
            return;
        }
        isStopped = true;
        for(; !queue.isEmpty(); Utils.waitOn(mutex));
        obj;
        JVM INSTR monitorexit ;
          goto _L1
        exception;
        throw exception;
_L1:
        sysGrp.stopAndDestroy();
        return;
    }

    public void waitForTasks()
    {
        synchronized(mutex)
        {
            checkState();
            for(; !queue.isEmpty(); Utils.waitOn(mutex));
        }
    }

    private void checkState()
    {
        if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            throw new AssertionError();
        if(isStopped)
            throw new IllegalStateException(L10n.format("SRVC.OBJPOOL.ERR10", name));
        else
            return;
    }

    boolean isStopped()
    {
        if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            throw new AssertionError();
        else
            return isStopped;
    }

    public ThreadPoolResizePolicy getResizePolicy()
    {
        return resizePolicy;
    }

    public String toString()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        if(isStopped)
            return L10n.format("SRVC.OBJPOOL.TXT19", name);
        L10n.format("SRVC.OBJPOOL.TXT3", name, new Integer(size), Boolean.valueOf(lazy), new Integer(priority), stats);
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
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

    private final String name;
    private final boolean lazy;
    private final int priority;
    private final ThreadPoolStatsImpl stats;
    private int size;
    private ThreadPoolResizePolicy resizePolicy;
    private ArrayList workers;
    private FifoQueue queue;
    private boolean isStopped;
    private Object mutex;
    private SysThreadGroup sysGrp;
    private List listeners;
    private int free;
    private int interrupted;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(ThreadPoolImpl.class).desiredAssertionStatus();
    }




















}
