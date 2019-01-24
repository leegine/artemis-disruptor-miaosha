// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   EventSystem.java

package co.fin.intellioms.event;

import co.fin.intellioms.util.InitializationException;

// Referenced classes of package com.com.fin.intellioms.event:
//            EventException, EventSource, EventProcessor, Event

public interface EventSystem
{

    public abstract void start()
        throws InitializationException;

    public abstract void stop();

    public abstract void startAllSources()
        throws EventException;

    public abstract void pauseAllSources()
        throws EventException;

    public abstract void stopAllSources()
        throws EventException;

    public abstract void startSource(EventSource eventsource)
        throws EventException;

    public abstract void pauseSource(EventSource eventsource)
        throws EventException;

    public abstract void stopSource(EventSource eventsource)
        throws EventException;

    public abstract void registerEventSource(EventSource eventsource);

    public abstract void registerEventProcessor(EventProcessor eventprocessor);

    public abstract void dispatch(Event event)
        throws EventException;

    public abstract EventProcessor[] getProcessors(Event event);
}
