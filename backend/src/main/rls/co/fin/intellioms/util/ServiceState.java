// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   ServiceState.java

package co.fin.intellioms.util;


public class ServiceState
{

    public ServiceState(String name)
    {
        state = 0;
        this.name = name;
    }

    public synchronized void start()
    {
        if(state == 1)
        {
            throw new IllegalStateException((new StringBuilder()).append("Service '").append(name).append("' allready started.").toString());
        } else
        {
            state = 1;
            return;
        }
    }

    public synchronized void stop()
    {
        if(state == 0)
        {
            throw new IllegalStateException((new StringBuilder()).append("Service '").append(name).append("' allready stopped.").toString());
        } else
        {
            state = 0;
            return;
        }
    }

    public synchronized boolean isStarted()
    {
        return state == 1;
    }

    public synchronized void checkStarted()
        throws IllegalStateException
    {
        if(state != 1)
            throw new IllegalStateException((new StringBuilder()).append("Service '").append(name).append("' not started.").toString());
        else
            return;
    }

    public synchronized void checkNotStarted()
        throws IllegalStateException
    {
        if(state == 1)
            throw new IllegalStateException((new StringBuilder()).append("Service '").append(name).append("' already started.").toString());
        else
            return;
    }

    private final int STOPPED = 0;
    private final int STARTED = 1;
    private int state;
    private final String name;
}
