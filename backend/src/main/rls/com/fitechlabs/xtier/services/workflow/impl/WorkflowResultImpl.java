// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.workflow.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.workflow.*;

class WorkflowResultImpl
    implements WorkflowResult
{

    WorkflowResultImpl(WorkflowExecSet workflowexecset, WorkflowSession workflowsession)
    {
        exitCode = null;
        isSuccessful = true;
        session = workflowsession;
        execSet = workflowexecset;
    }

    public String getExitCode()
    {
        return exitCode;
    }

    public boolean isSuccessful()
    {
        return isSuccessful;
    }

    public WorkflowExecSet getExecSet()
    {
        return execSet;
    }

    public WorkflowSession getReturnedSession()
    {
        return session;
    }

    void setExitCode(String s)
    {
        exitCode = s;
    }

    void setIsSuccessful(boolean flag)
    {
        isSuccessful = flag;
    }

    public String toString()
    {
        return L10n.format("SRVC.WF.TXT3", execSet.getGroup(), execSet.getName(), exitCode, Boolean.valueOf(isSuccessful));
    }

    private WorkflowExecSet execSet;
    private WorkflowSession session;
    private String exitCode;
    private boolean isSuccessful;
}
