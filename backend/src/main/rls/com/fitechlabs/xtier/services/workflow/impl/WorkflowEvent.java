// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.workflow.impl;

import com.fitechlabs.xtier.utils.xml.XmlLocation;
import java.util.HashMap;
import java.util.Map;

// Referenced classes of package com.fitechlabs.xtier.services.workflow.impl:
//            WorkflowXmlLoc, WorkflowCondition, WorkflowExecSetImpl, WorkflowExecutable

class WorkflowEvent extends WorkflowXmlLoc
{

    WorkflowEvent(XmlLocation xmllocation)
    {
        super(xmllocation);
        conditions = new HashMap();
    }

    Map getConditions()
    {
        return conditions;
    }

    WorkflowExecutable getCause()
    {
        return cause;
    }

    void setCause(WorkflowExecutable workflowexecutable)
    {
        cause = workflowexecutable;
    }

    WorkflowExecSetImpl getExecSet()
    {
        return execSet;
    }

    void setExecSet(WorkflowExecSetImpl workflowexecsetimpl)
    {
        execSet = workflowexecsetimpl;
    }

    WorkflowCondition getCondition(String s)
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        else
            return (WorkflowCondition)conditions.get(s);
    }

    void addCondition(WorkflowCondition workflowcondition)
    {
        if(!$assertionsDisabled && conditions.containsKey(workflowcondition.getCode()))
        {
            throw new AssertionError();
        } else
        {
            conditions.put(workflowcondition.getCode(), workflowcondition);
            return;
        }
    }

    boolean hasCondition(String s)
    {
        return conditions.containsKey(s);
    }

    public String toString()
    {
        if(!$assertionsDisabled)
            throw new AssertionError();
        else
            return "Workflow event [group=" + execSet.getGroup() + ", exec-set=" + execSet.getName() + ", cause=" + cause + ']';
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

    private WorkflowExecSetImpl execSet;
    private Map conditions;
    private WorkflowExecutable cause;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(WorkflowEvent.class).desiredAssertionStatus();
    }
}
