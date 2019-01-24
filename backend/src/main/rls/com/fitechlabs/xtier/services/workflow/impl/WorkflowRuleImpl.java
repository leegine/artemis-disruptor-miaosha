// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.workflow.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.workflow.WorkflowRule;
import com.fitechlabs.xtier.services.workflow.WorkflowRuleBody;
import com.fitechlabs.xtier.utils.xml.XmlLocation;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.workflow.impl:
//            WorkflowExecutable, WorkflowParam, WorkflowRuleOutput

class WorkflowRuleImpl extends WorkflowExecutable
    implements WorkflowRule
{

    WorkflowRuleImpl(XmlLocation xmllocation, String s, String s1)
    {
        super(xmllocation, s, s1, false);
        inputs = new HashMap();
        outputs = new HashMap();
    }

    Map getInputs()
    {
        return inputs;
    }

    Map getOutputs()
    {
        return outputs;
    }

    public WorkflowRuleBody getBody()
    {
        return body;
    }

    void setBody(WorkflowRuleBody workflowrulebody)
    {
        body = workflowrulebody;
    }

    WorkflowParam getInput(String s)
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        else
            return (WorkflowParam)inputs.get(s);
    }

    void addInput(WorkflowParam workflowparam)
    {
        if(!$assertionsDisabled && workflowparam == null)
            throw new AssertionError();
        if(!$assertionsDisabled && inputs.containsKey(workflowparam.getName()))
        {
            throw new AssertionError();
        } else
        {
            inputs.put(workflowparam.getName(), workflowparam);
            return;
        }
    }

    boolean hasInput(String s)
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        else
            return inputs.containsKey(s);
    }

    WorkflowRuleOutput getOutput(String s)
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        else
            return (WorkflowRuleOutput)outputs.get(s);
    }

    void addOutput(WorkflowRuleOutput workflowruleoutput)
    {
        if(!$assertionsDisabled && workflowruleoutput == null)
            throw new AssertionError();
        if(!$assertionsDisabled && outputs.containsKey(workflowruleoutput.getCode()))
        {
            throw new AssertionError();
        } else
        {
            outputs.put(workflowruleoutput.getCode(), workflowruleoutput);
            return;
        }
    }

    boolean hasOutput(String s)
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        else
            return outputs.containsKey(s);
    }

    public String toString()
    {
        return L10n.format("SRVC.WF.TXT6", getGroup(), getName(), new Long(getAvgExecTime()), new Date(getLastExecTime()), new Long(getExecCount()));
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

    private WorkflowRuleBody body;
    private Map inputs;
    private Map outputs;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(WorkflowRuleImpl.class).desiredAssertionStatus();
    }
}
