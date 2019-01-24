// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.workflow;


// Referenced classes of package com.fitechlabs.xtier.services.workflow:
//            WorkflowExecSet, WorkflowRule, WorkflowSession

public interface WorkflowListener
{

    public abstract void onRuleStart(WorkflowExecSet workflowexecset, WorkflowRule workflowrule, WorkflowSession workflowsession);

    public abstract void onRuleEnd(WorkflowExecSet workflowexecset, WorkflowRule workflowrule, WorkflowSession workflowsession, String s, boolean flag);

    public abstract void onExecSetStart(WorkflowExecSet workflowexecset, WorkflowSession workflowsession);

    public abstract void onExecSetEnd(WorkflowExecSet workflowexecset, WorkflowSession workflowsession, String s);
}
