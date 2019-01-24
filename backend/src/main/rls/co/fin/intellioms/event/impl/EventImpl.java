// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   EventImpl.java

package co.fin.intellioms.event.impl;

import co.fin.intellioms.enums.EventType;
import co.fin.intellioms.event.Event;

import java.util.Date;

public class EventImpl
    implements Event
{

    public EventImpl(EventType type, Object data)
    {
        this.data = data;
        this.type = type;
    }

    public EventType getEventType()
    {
        return type;
    }

    public Object getEventData()
    {
        return data;
    }

    public long getTime()
    {
        return time;
    }

    public String toString()
    {
        return (new StringBuilder()).append("Event[type=").append(type).append(", data=").append(data).append(", time=").append(new Date(time)).append("]").toString();
    }

    private final EventType type;
    private final Object data;
    private final long time = System.currentTimeMillis();
}
