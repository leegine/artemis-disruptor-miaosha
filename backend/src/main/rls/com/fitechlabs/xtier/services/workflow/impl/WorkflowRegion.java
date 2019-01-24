// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.workflow.impl;

import com.fitechlabs.xtier.utils.xml.XmlLocation;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.workflow.impl:
//            WorkflowXmlLoc, WorkflowGroup, WorkflowExecutable, WorkflowRef,
//            WorkflowExecSetImpl, WorkflowRuleImpl

class WorkflowRegion extends WorkflowXmlLoc
{

    WorkflowRegion(XmlLocation xmllocation, String s)
    {
        super(xmllocation);
        allRules = null;
        allExecSets = null;
        groups = new HashMap();
        if(!$assertionsDisabled && s == null)
        {
            throw new AssertionError();
        } else
        {
            name = s;
            return;
        }
    }

    Map getGroups()
    {
        return groups;
    }

    String getName()
    {
        return name;
    }

    boolean isParseValidation()
    {
        return isParseValidation;
    }

    void setParseValidation(boolean flag)
    {
        isParseValidation = flag;
    }

    boolean isRunValidation()
    {
        return isRunValidation;
    }

    void setRunValidation(boolean flag)
    {
        isRunValidation = flag;
    }

    List getAllRules()
    {
        if(allRules == null)
        {
            allRules = new ArrayList();
            WorkflowGroup workflowgroup;
            for(Iterator iterator = groups.values().iterator(); iterator.hasNext(); allRules.addAll(workflowgroup.getRules().values()))
                workflowgroup = (WorkflowGroup)iterator.next();

            allRules = Collections.unmodifiableList(allRules);
        }
        return allRules;
    }

    List getAllExecSets()
    {
        if(allExecSets == null)
        {
            allExecSets = new ArrayList();
            WorkflowGroup workflowgroup;
            for(Iterator iterator = groups.values().iterator(); iterator.hasNext(); allExecSets.addAll(workflowgroup.getExecSets().values()))
                workflowgroup = (WorkflowGroup)iterator.next();

            allExecSets = Collections.unmodifiableList(allExecSets);
        }
        return allExecSets;
    }

    WorkflowRuleImpl getRule(String s, String s1)
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        if(!$assertionsDisabled && s1 == null)
            throw new AssertionError();
        WorkflowGroup workflowgroup = getGroup(s);
        if(workflowgroup != null)
            return workflowgroup.getRule(s1);
        else
            return null;
    }

    WorkflowExecSetImpl getExecSet(String s, String s1)
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        if(!$assertionsDisabled && s1 == null)
            throw new AssertionError();
        WorkflowGroup workflowgroup = getGroup(s);
        if(workflowgroup != null)
            return workflowgroup.getExecSet(s1);
        else
            return null;
    }

    WorkflowGroup getGroup(String s)
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        else
            return (WorkflowGroup)groups.get(s);
    }

    WorkflowExecutable getExecutable(WorkflowRef workflowref)
    {
        WorkflowExecSetImpl workflowexecsetimpl = workflowref.getExecSet();
        Object obj = null;
        if(workflowexecsetimpl != null && !workflowref.isExecSet())
            obj = (WorkflowExecutable)workflowexecsetimpl.getPrivateRule(workflowref.getName());
        if(obj == null)
        {
            WorkflowGroup workflowgroup = getGroup(workflowref.getGroup());
            if(workflowgroup != null)
                obj = !workflowref.isExecSet() ? ((Object) (workflowgroup.getRule(workflowref.getName()))) : ((Object) (workflowgroup.getExecSet(workflowref.getName())));
        }
        return ((WorkflowExecutable) (obj));
    }

    void addGroup(WorkflowGroup workflowgroup)
    {
        if(!$assertionsDisabled && workflowgroup == null)
            throw new AssertionError();
        if(!$assertionsDisabled && groups.containsKey(workflowgroup.getName()))
        {
            throw new AssertionError();
        } else
        {
            groups.put(workflowgroup.getName(), workflowgroup);
            return;
        }
    }

    boolean hasGroup(String s)
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        else
            return groups.containsKey(s);
    }

    public boolean equals(Object obj)
    {
        return name.equals(((WorkflowRegion)obj).name);
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
            return "Workflow region [name=" + name + " , is-run-validation-on=" + isRunValidation + ", is-parse-validation-on=" + isParseValidation + ']';
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
    private boolean isRunValidation;
    private boolean isParseValidation;
    private List allRules;
    private List allExecSets;
    private Map groups;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(WorkflowRegion.class).desiredAssertionStatus();
    }
}
