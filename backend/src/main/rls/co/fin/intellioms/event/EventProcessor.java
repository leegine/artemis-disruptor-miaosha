// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   EventProcessor.java

package co.fin.intellioms.event;

import co.fin.intellioms.enums.EventType;

// Referenced classes of package com.com.fin.intellioms.event:
//            EventException, Event

public interface EventProcessor
{

    public abstract EventType[] getEventTypes();

    public abstract void process(Event event)
        throws EventException;
}
