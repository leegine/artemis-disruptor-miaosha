// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.grid;

import com.fitechlabs.xtier.services.marshal.Marshallable;

public interface GridTaskResult
{

    public abstract int getTaskId();

    public abstract long getExecId();

    public abstract long getStartTime();

    public abstract long getEndTime();

    public abstract boolean isSuccessful();

    public abstract Marshallable getReturnValue();
}
