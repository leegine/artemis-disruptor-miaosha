// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   EventSystemImpl.java

package co.fin.intellioms.event.impl;

import co.fin.intellioms.enums.EventType;
import com.fitechlabs.fin.intellioms.event.*;
import com.fitechlabs.fin.intellioms.util.*;
import java.util.*;

public class EventSystemImpl
    implements EventSystem, Startable
{

    public EventSystemImpl()
    {
        log = Log.getLogger(com/ com /fin/intellioms/event/EventSystem);
        processors = new HashMap();
        processorsList = new ArrayList();
        sources = new ArrayList();
    }

    public void startAllSources()
        throws EventException
    {
        state.checkStarted();
        synchronized(sources)
        {
            for(int i = 0; i < sources.size(); i++)
            {
                EventSource source = (EventSource)sources.get(i);
                source.start();
                if(log.isInfo())
                    log.info((new StringBuilder()).append("Started event source: ").append(source).toString());
            }

        }
    }

    public void pauseAllSources()
        throws EventException
    {
        state.checkStarted();
        synchronized(sources)
        {
            for(int i = 0; i < sources.size(); i++)
            {
                EventSource source = (EventSource)sources.get(i);
                source.pause();
                if(log.isInfo())
                    log.info((new StringBuilder()).append("Paused event source: ").append(source).toString());
            }

        }
    }

    public void stopAllSources()
        throws EventException
    {
        state.checkStarted();
        synchronized(sources)
        {
            for(int i = 0; i < sources.size(); i++)
            {
                EventSource source = (EventSource)sources.get(i);
                if(log.isInfo())
                    log.info((new StringBuilder()).append("Stopping event source: ").append(source).toString());
                source.stop();
            }

        }
    }

    public void startSource(EventSource src)
        throws EventException
    {
        state.checkStarted();
        synchronized(sources)
        {
            for(int i = 0; i < sources.size(); i++)
            {
                EventSource source = (EventSource)sources.get(i);
                if(!source.equals(src))
                    continue;
                src.start();
                if(log.isInfo())
                    log.info((new StringBuilder()).append("Started event source: ").append(src).toString());
            }

        }
    }

    public void pauseSource(EventSource src)
        throws EventException
    {
        state.checkStarted();
        synchronized(sources)
        {
            for(int i = 0; i < sources.size(); i++)
            {
                EventSource source = (EventSource)sources.get(i);
                if(!source.equals(src))
                    continue;
                src.pause();
                if(log.isInfo())
                    log.info((new StringBuilder()).append("Paused event source: ").append(src).toString());
            }

        }
    }

    public void stopSource(EventSource src)
        throws EventException
    {
        state.checkStarted();
        synchronized(sources)
        {
            for(int i = 0; i < sources.size(); i++)
            {
                EventSource source = (EventSource)sources.get(i);
                if(!source.equals(src))
                    continue;
                src.stop();
                if(log.isInfo())
                    log.info((new StringBuilder()).append("Stopped event source: ").append(src).toString());
            }

        }
    }

    public void registerEventSource(EventSource src)
    {
        synchronized(sources)
        {
            sources.add(src);
        }
        src.onRegister(this);
        if(log.isInfo())
            log.info((new StringBuilder()).append("Registered event source: ").append(src).toString());
    }

    public synchronized void registerEventProcessor(EventProcessor proc)
    {
        processorsList.add(proc);
        if(log.isInfo())
            log.info((new StringBuilder()).append("Registered event processor ").append(proc).toString());
    }

    public void dispatch(Event evt)
        throws EventException
    {
        state.checkStarted();
        if(log.isDebug())
            log.debug((new StringBuilder()).append("Dispatching event: ").append(evt).toString());
        List procs = (List)processors.get(evt.getEventType());
        if(procs != null)
        {
            for(int i = 0; i < procs.size(); i++)
                ((EventProcessor)procs.get(i)).process(evt);

        } else
        if(log.isWarn())
            log.warn((new StringBuilder()).append("No processors are registered to event type ").append(evt.getEventType()).toString());
    }

    public EventProcessor[] getProcessors(Event evt)
    {
        List procs = (List)processors.get(evt.getEventType());
        return (EventProcessor[])(EventProcessor[])procs.toArray(new EventProcessor[procs.size()]);
    }

    public void start()
        throws InitializationException
    {
        if(log.isInfo())
            log.info("Starting event system...");
        for(int j = 0; j < processorsList.size(); j++)
        {
            EventProcessor proc = (EventProcessor)processorsList.get(j);
            EventType eventTypes[] = proc.getEventTypes();
            if(eventTypes == null)
                throw new NullPointerException("Processor must provide event types.");
            if(!$assertionsDisabled && eventTypes.length <= 0)
                throw new AssertionError("Processor can't have empty array of event types.");
            for(int i = 0; i < eventTypes.length; i++)
            {
                List procs = (List)processors.get(eventTypes[i]);
                if(procs == null)
                {
                    procs = new ArrayList();
                    processors.put(eventTypes[i], procs);
                }
                if(procs.indexOf(proc) < 0)
                    procs.add(proc);
                if(log.isInfo())
                    log.info((new StringBuilder()).append("Mapped event processor ").append(proc).append(" to event type ").append(eventTypes[i]).toString());
            }

        }

        state.start();
        try
        {
            startAllSources();
        }
        catch(EventException e)
        {
            throw new InitializationException("Error while starting event system.", e);
        }
        if(log.isInfo())
            log.info("Started event system.");
    }

    public void stop()
    {
        if(log.isInfo())
            log.info("Stopping event system...");
        try
        {
            stopAllSources();
        }
        catch(EventException e)
        {
            if(log.isError())
                log.error("Error while stopping event sources.", e);
        }
        state.stop();
        if(log.isInfo())
            log.info("Stopped event system.");
    }

    private Log log;
    private Map processors;
    private List processorsList;
    private List sources;
    private final ServiceState state = new ServiceState("EventSystem");
    static final boolean $assertionsDisabled = !com/ com /fin/intellioms/event/impl/EventSystemImpl.desiredAssertionStatus();

}
