// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.workflow.impl;

import com.fitechlabs.xtier.utils.xml.XmlLocation;
import java.util.HashMap;
import java.util.Map;

// Referenced classes of package com.fitechlabs.xtier.services.workflow.impl:
//            WorkflowXmlLoc, WorkflowRuleImpl, WorkflowExecSetImpl, WorkflowExecutable

class WorkflowGroup extends WorkflowXmlLoc
{

    WorkflowGroup(XmlLocation xmllocation, String s)
    {
        super(xmllocation);
        rules = new HashMap();
        execSets = new HashMap();
        if(!$assertionsDisabled && s == null)
        {
            throw new AssertionError();
        } else
        {
            name = s;
            return;
        }
    }

    String getName()
    {
        return name;
    }

    Map getExecSets()
    {
        return execSets;
    }

    Map getRules()
    {
        return rules;
    }

    WorkflowRuleImpl getRule(String s)
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        else
            return (WorkflowRuleImpl)rules.get(s);
    }

    WorkflowExecSetImpl getExecSet(String s)
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        else
            return (WorkflowExecSetImpl)execSets.get(s);
    }

    void addExecutable(WorkflowExecutable workflowexecutable)
    {
        if(!$assertionsDisabled && workflowexecutable == null)
            throw new AssertionError();
        if(!$assertionsDisabled && !workflowexecutable.getGroup().equals(name))
            throw new AssertionError();
        if(!$assertionsDisabled && rules.containsKey(workflowexecutable.getName()))
            throw new AssertionError();
        if(!$assertionsDisabled && execSets.containsKey(workflowexecutable.getName()))
            throw new AssertionError();
        if(workflowexecutable.isExecSet())
            execSets.put(workflowexecutable.getName(), workflowexecutable);
        else
            rules.put(workflowexecutable.getName(), workflowexecutable);
    }

    boolean hasExecutable(WorkflowExecutable workflowexecutable)
    {
        if(!$assertionsDisabled && workflowexecutable == null)
        {
            throw new AssertionError();
        } else
        {
            String s = workflowexecutable.getName();
            return rules.containsKey(s) || execSets.containsKey(s);
        }
    }

    public String toString()
    {
        if(!$assertionsDisabled)
            throw new AssertionError();
        else
            return "Workflow group [name=" + name + ']';
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

    private String name;
    private Map rules;
    private Map execSets;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(WorkflowGroup.class).desiredAssertionStatus();
    }
}
