// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.workflow;

import java.util.List;

// Referenced classes of package com.fitechlabs.xtier.services.workflow:
//            WorkflowRule

public interface WorkflowExecSet
{

    public abstract String getGroup();

    public abstract String getName();

    public abstract List getAllPrivateRules();

    public abstract WorkflowRule getPrivateRule(String s);

    public abstract long getAvgExecTime();

    public abstract long getLastExecTime();

    public abstract long getExecCount();
}
