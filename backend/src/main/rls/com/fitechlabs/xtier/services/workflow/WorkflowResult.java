// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.workflow;


// Referenced classes of package com.fitechlabs.xtier.services.workflow:
//            WorkflowExecSet, WorkflowSession

public interface WorkflowResult
{

    public abstract WorkflowExecSet getExecSet();

    public abstract String getExitCode();

    public abstract boolean isSuccessful();

    public abstract WorkflowSession getReturnedSession();
}
