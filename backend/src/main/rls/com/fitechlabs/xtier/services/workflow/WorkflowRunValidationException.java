// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.workflow;

import com.fitechlabs.xtier.kernel.KernelServiceRuntimeException;
import com.fitechlabs.xtier.l10n.L10n;

public class WorkflowRunValidationException extends KernelServiceRuntimeException
{

    public WorkflowRunValidationException(String s)
    {
        super(s);
    }

    public WorkflowRunValidationException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public String toString()
    {
        return L10n.format("SRVC.WF.TXT7", getMessage());
    }
}
