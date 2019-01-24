// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   TimerAdaptorImpl.java

package co.fin.intellioms.time;

import co.fin.intellioms.enums.EventType;
import co.fin.intellioms.event.Event;
import co.fin.intellioms.event.EventException;
import co.fin.intellioms.event.EventSystem;
import co.fin.intellioms.event.impl.EventImpl;
import co.fin.intellioms.rulesys.BizDateProvider;
import co.fin.intellioms.rulesys.impl.DefaultBizDateProvider;
import co.fin.intellioms.util.Log;
import co.fin.intellioms.util.ServiceState;
import com.fitechlabs.fin.intellioms.event.*;
import com.fitechlabs.xtier.utils.ArgAssert;

import java.util.*;

// Referenced classes of package com.com.fin.intellioms.time:
//            TimerAdaptor

public class TimerAdaptorImpl
    implements TimerAdaptor
{
    private class AdaptorTimerTask extends TimerTask
    {

        public void run()
        {
            Event event = new EventImpl(EventType.TIMER, time);
            try
            {
                if(TimerAdaptorImpl.log.isDebug())
                    TimerAdaptorImpl.log.debug((new StringBuilder()).append("Raising ").append(event).toString());
                raiseEvent(event);
            }
            catch(Throwable t)
            {
                if(TimerAdaptorImpl.log.isError())
                    TimerAdaptorImpl.log.error((new StringBuilder()).append("Error while raising timer event ").append(event).toString(), t);
            }
            synchronized(times)
            {
                times.remove(new Date(scheduledExecutionTime()));
            }
        }

        private final Date time;
        final TimerAdaptorImpl this$0;

        public AdaptorTimerTask(Date time)
        {
            this$0 = TimerAdaptorImpl.this;
            super();
            this.time = time;
        }
    }


    public TimerAdaptorImpl()
    {
        eventSys = null;
        times = new TreeSet(new Comparator() {

            public int compare(Object o1, Object o2)
            {
                Date d1 = (Date)o1;
                Date d2 = (Date)o2;
                return (int)(d1.getTime() / 1000L - d2.getTime() / 1000L);
            }

            final TimerAdaptorImpl this$0;


            {
                this$0 = TimerAdaptorImpl.this;
                super();
            }
        }
);
        timesBuffer = new ArrayList();
        state = new ServiceState("TimerAdaptor");
        bizDateProvider = new DefaultBizDateProvider();
    }

    public TimerAdaptorImpl(BizDateProvider bizDateProvider)
    {
        eventSys = null;
        times = new TreeSet(new _cls1());
        timesBuffer = new ArrayList();
        state = new ServiceState("TimerAdaptor");
        ArgAssert.nullArg(bizDateProvider, "bizDateProvider");
        this.bizDateProvider = bizDateProvider;
    }

    public void start()
    {
        state.checkNotStarted();
        if(log.isInfo())
            log.info("Starting...");
        timer = new Timer();
        state.start();
        if(timesBuffer.size() > 0)
        {
            for(Iterator iter = timesBuffer.iterator(); iter.hasNext(); iter.remove())
            {
                Date time = (Date)iter.next();
                addTask(time);
            }

        }
        if(log.isInfo())
            log.info("Started.");
    }

    public void pause()
    {
    }

    public void stop()
    {
        timer.cancel();
        timesBuffer.clear();
        state.stop();
    }

    public void onRegister(EventSystem eventSys)
    {
        this.eventSys = eventSys;
    }

    public void raiseEvent(Event evt)
        throws EventException
    {
        if(eventSys == null)
        {
            throw new AssertionError("Timer adaptor was not registered to event system.");
        } else
        {
            eventSys.dispatch(evt);
            return;
        }
    }

    public void addTask(Date time)
    {
label0:
        {
            if(!state.isStarted())
                break MISSING_BLOCK_LABEL_131;
            synchronized(times)
            {
                if(!times.contains(time))
                    break label0;
            }
            return;
        }
        times.add(time);
        set;
        JVM INSTR monitorexit ;
          goto _L1
        exception;
        throw exception;
_L1:
        if(log.isDebug())
            log.debug((new StringBuilder()).append("Scheduling time event ").append(time).toString());
        timer.schedule(new AdaptorTimerTask(time), new Date(time.getTime() - bizDateProvider.getTimeShift()));
        break MISSING_BLOCK_LABEL_177;
        if(log.isDebug())
            log.debug((new StringBuilder()).append("Buffering time event for ").append(time).toString());
        timesBuffer.add(time);
    }

    public List getSchedule()
    {
        Set set = times;
//        JVM INSTR monitorenter ;
        return new ArrayList(times);
//        Exception exception;
//        exception;
//        throw exception;
    }

    public String toString()
    {
        return "TimeAdaptor";
    }

    private BizDateProvider bizDateProvider;
    private EventSystem eventSys;
    private static final Log log = Log.getLogger(TimerAdaptorImpl.class);
    private final Set times;
    private Timer timer;
    private List timesBuffer;
    private ServiceState state;



}
