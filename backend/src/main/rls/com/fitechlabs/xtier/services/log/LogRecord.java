// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.log;

import java.util.Date;

public interface LogRecord
{

    public abstract long getSeqNum();

    public abstract String getMessage();

    public abstract long getTimeElapsed();

    public abstract long getTimeStamp();

    public abstract Date getTimeStampDate();

    public abstract int getLevel();

    public abstract String getThreadName();

    public abstract String getLoggerCategory();

    public abstract Throwable getThrowable();

    public abstract String getClassName();

    public abstract String getFileName();

    public abstract String getMethodName();

    public abstract int getLineNumber();
}
