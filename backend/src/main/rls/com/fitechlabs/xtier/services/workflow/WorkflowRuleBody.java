// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.workflow;


// Referenced classes of package com.fitechlabs.xtier.services.workflow:
//            WorkflowException, WorkflowSession

public interface WorkflowRuleBody
{

    public abstract String invoke(WorkflowSession workflowsession)
        throws WorkflowException;
}
