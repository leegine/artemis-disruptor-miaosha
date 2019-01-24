// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.jobs;

import com.fitechlabs.xtier.kernel.KernelServiceException;
import com.fitechlabs.xtier.l10n.L10n;

public class JobException extends KernelServiceException
{

    public JobException(String s)
    {
        super(s);
    }

    public JobException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public String toString()
    {
        return L10n.format("SRVC.JOBS.TXT1", getMessage());
    }
}
