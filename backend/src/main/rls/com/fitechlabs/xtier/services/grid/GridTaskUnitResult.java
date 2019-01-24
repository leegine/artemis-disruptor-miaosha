// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.grid;

import com.fitechlabs.xtier.services.marshal.Marshallable;

public interface GridTaskUnitResult
{

    public abstract int getReturnCode();

    public abstract int getUserErrorCode();

    public abstract Marshallable getReturnValue();

    public static final int ERR_USER_DEFINED = 1;
    public static final int ERR_RETRY_OTHER = 2;
    public static final int ERR_RETRY_SAME = 3;
    public static final int ERR_TASK_NOT_FOUND = 4;
    public static final int ERR_TASK_UNIT_NOT_FOUND = 5;
    public static final int ERR_INVALID_ARGS = 6;
    public static final int ERR_TASK_UNIT_TIMEOUT = 7;
    public static final int ERR_SYSTEM_FAILURE = 8;
    public static final int ERR_NO_RETRY = 9;
    public static final int ERR_NO_ROUTE = 10;
    public static final int ERR_NOT_EXEC_TASK_UNIT = 11;
    public static final int ERR_NO_TOPOLOGY = 12;
    public static final int DFLT_USER_ERR_CODE = 0x80000000;
    public static final int TASK_UNIT_OK = 1001;
}
