// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.workflow.impl;

import com.fitechlabs.xtier.utils.xml.XmlLocation;

// Referenced classes of package com.fitechlabs.xtier.services.workflow.impl:
//            WorkflowXmlLoc, WorkflowExecSetImpl, WorkflowEvent

class WorkflowAction extends WorkflowXmlLoc
{

    WorkflowAction(XmlLocation xmllocation, boolean flag)
    {
        super(xmllocation);
        isTerminal = flag;
    }

    boolean isTerminal()
    {
        return isTerminal;
    }

    String getExitCode()
    {
        return exitCode;
    }

    void setExitCode(String s)
    {
        exitCode = s;
    }

    WorkflowEvent getNextEvent()
    {
        return nextEvent;
    }

    void setNextEvent(WorkflowEvent workflowevent)
    {
        nextEvent = workflowevent;
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
            return "Workflow action [group=" + execSet.getGroup() + ", exec-set=" + execSet.getName() + ", is-terminal=" + isTerminal + (!isTerminal ? "next-event=" + nextEvent : "exit-code=" + exitCode) + ']';
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
    private boolean isTerminal;
    private WorkflowEvent nextEvent;
    private String exitCode;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(WorkflowAction.class).desiredAssertionStatus();
    }
}
