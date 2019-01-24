// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   EventSource.java

package co.fin.intellioms.event;


// Referenced classes of package com.com.fin.intellioms.event:
//            EventException, EventSystem, Event

public interface EventSource
{

    public abstract void start()
        throws EventException;

    public abstract void pause()
        throws EventException;

    public abstract void stop()
        throws EventException;

    public abstract void onRegister(EventSystem eventsystem);

    public abstract void raiseEvent(Event event)
        throws EventException;
}
