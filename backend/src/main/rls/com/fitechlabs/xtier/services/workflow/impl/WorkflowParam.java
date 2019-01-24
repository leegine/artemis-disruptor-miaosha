// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.workflow.impl;

import com.fitechlabs.xtier.utils.xml.XmlLocation;

// Referenced classes of package com.fitechlabs.xtier.services.workflow.impl:
//            WorkflowXmlLoc

class WorkflowParam extends WorkflowXmlLoc
{

    WorkflowParam(XmlLocation xmllocation, String s, boolean flag)
    {
        super(xmllocation);
        if(!$assertionsDisabled && s == null)
        {
            throw new AssertionError();
        } else
        {
            name = s;
            isOptional = flag;
            return;
        }
    }

    String getName()
    {
        return name;
    }

    boolean isOptional()
    {
        return isOptional;
    }

    public String toString()
    {
        if(!$assertionsDisabled)
            throw new AssertionError();
        else
            return "Workflow parameter [name=" + name + ", optional=" + isOptional + ']';
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
    private boolean isOptional;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(WorkflowParam.class).desiredAssertionStatus();
    }
}
