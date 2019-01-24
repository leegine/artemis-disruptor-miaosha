// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   Event.java

package co.fin.intellioms.event;

import co.fin.intellioms.enums.EventType;

public interface Event
{

    public abstract EventType getEventType();

    public abstract Object getEventData();

    public abstract long getTime();
}
