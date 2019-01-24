// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.workflow;


// Referenced classes of package com.fitechlabs.xtier.services.workflow:
//            WorkflowResult, WorkflowExecSet, WorkflowSession, WorkflowException

public interface WorkflowResultListener
{

    public abstract void onResult(WorkflowResult workflowresult);

    public abstract void onError(WorkflowExecSet workflowexecset, WorkflowSession workflowsession, WorkflowException workflowexception);
}
