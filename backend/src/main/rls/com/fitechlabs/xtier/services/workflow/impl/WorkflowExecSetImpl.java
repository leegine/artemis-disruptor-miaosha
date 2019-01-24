// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.workflow.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.workflow.WorkflowExecSet;
import com.fitechlabs.xtier.services.workflow.WorkflowRule;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.xml.XmlLocation;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.workflow.impl:
//            WorkflowExecutable, WorkflowEvent, WorkflowRuleImpl, WorkflowRef

class WorkflowExecSetImpl extends WorkflowExecutable
    implements WorkflowExecSet
{

    WorkflowExecSetImpl(XmlLocation xmllocation, String s, String s1)
    {
        super(xmllocation, s, s1, true);
        privateRules = new HashMap();
        events = new HashMap();
        exitCodes = new HashSet();
        rulesList = null;
        mutex = new Object();
    }

    Map getEvents()
    {
        return events;
    }

    WorkflowEvent getStartEvent()
    {
        return startEvent;
    }

    void setStartEvent(WorkflowEvent workflowevent)
    {
        startEvent = workflowevent;
    }

    Map getPrivateRules()
    {
        return privateRules;
    }

    public List getAllPrivateRules()
    {
        if(rulesList == null)
            synchronized(mutex)
            {
                if(rulesList == null)
                    rulesList = Collections.unmodifiableList(new ArrayList(privateRules.values()));
            }
        return rulesList;
    }

    public WorkflowRule getPrivateRule(String s)
    {
        ArgAssert.nullArg(s, "name");
        return (WorkflowRule)privateRules.get(s);
    }

    Set getExitCodes()
    {
        return exitCodes;
    }

    WorkflowEvent getEvent(WorkflowRef workflowref)
    {
        return (WorkflowEvent)events.get(workflowref);
    }

    void addEvent(WorkflowRef workflowref, WorkflowEvent workflowevent)
    {
        if(!$assertionsDisabled && workflowevent == null)
            throw new AssertionError();
        if(!$assertionsDisabled && events.containsKey(workflowevent.getCause()))
        {
            throw new AssertionError();
        } else
        {
            events.put(workflowref, workflowevent);
            return;
        }
    }

    boolean hasEvent(WorkflowEvent workflowevent)
    {
        if(!$assertionsDisabled && workflowevent == null)
            throw new AssertionError();
        else
            return events.containsKey(workflowevent.getCause());
    }

    void addPrivateRule(WorkflowRuleImpl workflowruleimpl)
    {
        if(!$assertionsDisabled && workflowruleimpl == null)
            throw new AssertionError();
        if(!$assertionsDisabled && privateRules.containsKey(workflowruleimpl.getName()))
        {
            throw new AssertionError();
        } else
        {
            privateRules.put(workflowruleimpl.getName(), workflowruleimpl);
            return;
        }
    }

    boolean hasPrivateRule(WorkflowRuleImpl workflowruleimpl)
    {
        if(!$assertionsDisabled && workflowruleimpl == null)
            throw new AssertionError();
        else
            return privateRules.containsKey(workflowruleimpl.getName());
    }

    void addExitCode(String s)
    {
        if(!$assertionsDisabled && s == null)
        {
            throw new AssertionError();
        } else
        {
            exitCodes.add(s);
            return;
        }
    }

    boolean hasExitCode(String s)
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        else
            return exitCodes.contains(s);
    }

    public String toString()
    {
        return L10n.format("SRVC.WF.TXT5", getGroup(), getName(), new Long(getAvgExecTime()), new Date(getLastExecTime()), new Long(getExecCount()));
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

    private Map privateRules;
    private Map events;
    private Set exitCodes;
    private WorkflowEvent startEvent;
    private List rulesList;
    private Object mutex;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(WorkflowExecSetImpl.class).desiredAssertionStatus();
    }
}
