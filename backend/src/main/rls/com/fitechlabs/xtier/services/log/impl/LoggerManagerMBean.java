// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.log.impl;


public interface LoggerManagerMBean
{

    public abstract void makeDebugLevelFrom();

    public abstract void makeTraceLevelFrom();

    public abstract void makeInfoLevelFrom();

    public abstract void makeLogLevelFrom();

    public abstract void makeWarningLevelFrom();

    public abstract void makeNoneLevelFrom();

    public abstract void makeErrorLevelFrom();

    public abstract boolean getDebugLevel();

    public abstract boolean getTraceLevel();

    public abstract boolean getInfoLevel();

    public abstract boolean getLogLevel();

    public abstract boolean getWarningLevel();

    public abstract boolean getErrorLevel();

    public abstract void setDebugLevel(boolean flag);

    public abstract void setTraceLevel(boolean flag);

    public abstract void setInfoLevel(boolean flag);

    public abstract void setLogLevel(boolean flag);

    public abstract void setWarningLevel(boolean flag);

    public abstract void setErrorLevel(boolean flag);
}
