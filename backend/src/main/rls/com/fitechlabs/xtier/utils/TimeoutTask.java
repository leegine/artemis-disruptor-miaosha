// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.utils;

import java.util.TimerTask;

public abstract class TimeoutTask extends TimerTask
{

    public TimeoutTask()
    {
    }

    protected abstract void onTimeout();

    public final void run()
    {
        onTimeout();
    }
}
