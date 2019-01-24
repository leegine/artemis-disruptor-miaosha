// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.jobs;


public interface JobsTimeScaler
{

    public abstract long getScaledTimeMillis();

    public abstract void setScaledInitTime(long l);

    public abstract long scale(long l);
}
