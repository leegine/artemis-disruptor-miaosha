// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.workflow;

import com.fitechlabs.xtier.kernel.KernelService;
import com.fitechlabs.xtier.services.objpool.threads.ThreadPool;
import java.util.List;
import java.util.Map;

// Referenced classes of package com.fitechlabs.xtier.services.workflow:
//            WorkflowException, WorkflowRule, WorkflowExecSet, WorkflowResult, 
//            WorkflowResultListener, WorkflowListener

public interface WorkflowService
    extends KernelService
{

    public abstract List getAllRules();

    public abstract WorkflowRule getRule(String s, String s1);

    public abstract List getAllExecSets();

    public abstract WorkflowExecSet getExecSet(String s, String s1);

    public abstract WorkflowResult exec(String s, String s1, Map map)
        throws WorkflowException;

    public abstract WorkflowResult exec(WorkflowExecSet workflowexecset, Map map)
        throws WorkflowException;

    public abstract void exec(String s, String s1, Map map, WorkflowResultListener workflowresultlistener);

    public abstract void exec(String s, String s1, Map map, WorkflowResultListener workflowresultlistener, ThreadPool threadpool);

    public abstract void exec(WorkflowExecSet workflowexecset, Map map, WorkflowResultListener workflowresultlistener);

    public abstract void exec(WorkflowExecSet workflowexecset, Map map, WorkflowResultListener workflowresultlistener, ThreadPool threadpool);

    public abstract boolean addListener(WorkflowListener workflowlistener);

    public abstract boolean removeListener(WorkflowListener workflowlistener);

    public abstract List getAllListeners();
}
