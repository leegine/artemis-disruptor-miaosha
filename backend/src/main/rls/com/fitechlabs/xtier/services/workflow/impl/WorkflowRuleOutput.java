// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.workflow.impl;

import com.fitechlabs.xtier.utils.Utils;
import com.fitechlabs.xtier.utils.xml.XmlLocation;
import java.util.HashMap;
import java.util.Map;

// Referenced classes of package com.fitechlabs.xtier.services.workflow.impl:
//            WorkflowXmlLoc, WorkflowParam

class WorkflowRuleOutput extends WorkflowXmlLoc
{

    WorkflowRuleOutput(XmlLocation xmllocation, String s, boolean flag)
    {
        super(xmllocation);
        outputs = new HashMap();
        if(!$assertionsDisabled && s == null)
        {
            throw new AssertionError();
        } else
        {
            code = s;
            isSuccessful = flag;
            return;
        }
    }

    boolean isSuccessful()
    {
        return isSuccessful;
    }

    String getCode()
    {
        return code;
    }

    Map getParams()
    {
        return outputs;
    }

    WorkflowParam get(String s)
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        else
            return (WorkflowParam)outputs.get(s);
    }

    void addParam(WorkflowParam workflowparam)
    {
        if(!$assertionsDisabled && workflowparam == null)
            throw new AssertionError();
        if(!$assertionsDisabled && outputs.containsKey(workflowparam.getName()))
        {
            throw new AssertionError();
        } else
        {
            outputs.put(workflowparam.getName(), workflowparam);
            return;
        }
    }

    boolean hasParam(String s)
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        else
            return outputs.containsKey(s);
    }

    public String toString()
    {
        if(!$assertionsDisabled)
            throw new AssertionError();
        else
            return "Workflow rule output [code=" + code + ", is-successful=" + isSuccessful + ", output-parameters=" + Utils.map2Str(outputs);
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

    private Map outputs;
    private boolean isSuccessful;
    private String code;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(WorkflowRuleOutput.class).desiredAssertionStatus();
    }
}
