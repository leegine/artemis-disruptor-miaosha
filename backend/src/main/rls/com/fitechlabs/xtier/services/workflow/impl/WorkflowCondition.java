// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.workflow.impl;

import com.fitechlabs.xtier.utils.xml.XmlLocation;

// Referenced classes of package com.fitechlabs.xtier.services.workflow.impl:
//            WorkflowXmlLoc, WorkflowExecSetImpl, WorkflowAction

class WorkflowCondition extends WorkflowXmlLoc
{

    WorkflowCondition(XmlLocation xmllocation, String s)
    {
        super(xmllocation);
        if(!$assertionsDisabled && s == null)
        {
            throw new AssertionError();
        } else
        {
            code = s;
            return;
        }
    }

    String getCode()
    {
        return code;
    }

    WorkflowAction getAction()
    {
        return action;
    }

    void setAction(WorkflowAction workflowaction)
    {
        action = workflowaction;
    }

    WorkflowExecSetImpl getExecSet()
    {
        return execSet;
    }

    void setExecSet(WorkflowExecSetImpl workflowexecsetimpl)
    {
        execSet = workflowexecsetimpl;
    }

    public String toString()
    {
        if(!$assertionsDisabled)
            throw new AssertionError();
        else
            return "Workflow condition [group=" + execSet.getGroup() + ", exec-set=" + execSet.getName() + ", code=" + code + ']';
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
    private String code;
    private WorkflowAction action;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(WorkflowCondition.class).desiredAssertionStatus();
    }
}
