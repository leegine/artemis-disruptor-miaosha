// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.kernel;


public interface KernelService
{

    public abstract int[] getVersion();

    public abstract String getName();

    public abstract long getStartupTime();

    public abstract long getUpTime();

    public abstract String getRegionName();

    public abstract String getFeatureSet();

    public abstract String getConfigPath();

    public abstract int getState();

    public static final int SERVICE_STARTED = 1;
    public static final int SERVICE_STARTING = 2;
    public static final int SERVICE_STOPPED = 3;
    public static final int SERVICE_STOPPING = 4;
    public static final int SERVICE_FATAL = 5;
}
