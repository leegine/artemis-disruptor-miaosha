// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.workflow.impl;


// Referenced classes of package com.fitechlabs.xtier.services.workflow.impl:
//            WorkflowExecSetImpl

class WorkflowRef
{

    WorkflowRef(String s, String s1, WorkflowExecSetImpl workflowexecsetimpl, boolean flag)
    {
        group = s;
        name = s1;
        execSet = workflowexecsetimpl;
        isExecSet = flag;
    }

    String getGroup()
    {
        return group;
    }

    String getName()
    {
        return name;
    }

    boolean isExecSet()
    {
        return isExecSet;
    }

    WorkflowExecSetImpl getExecSet()
    {
        return execSet;
    }

    public boolean equals(Object obj)
    {
        WorkflowRef workflowref = (WorkflowRef)obj;
        return execSet == workflowref.execSet && isExecSet == workflowref.isExecSet && group.equals(workflowref.group) && name.equals(workflowref.name);
    }

    public int hashCode()
    {
        return name.hashCode();
    }

    public String toString()
    {
        if(!$assertionsDisabled)
            throw new AssertionError();
        else
            return "Workflow reference [group=" + group + ", name=" + name + ", is-exec-set=" + isExecSet + ']';
    }

    static Class _mthclass$(String s)
    {
        try
        {
            return Class.forName(s);
        }
        catch(ClassNotFoundException classnotfoundexception)
        {
            throw (new NoClassDefFoundError()).initCause(classnotfoundexception);
        }
    }

    private String group;
    private String name;
    private boolean isExecSet;
    private WorkflowExecSetImpl execSet;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(WorkflowRef.class).desiredAssertionStatus();
    }
}
