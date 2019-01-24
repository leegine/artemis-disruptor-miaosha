// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.workflow.impl;

import com.fitechlabs.xtier.utils.xml.XmlLocation;

abstract class WorkflowXmlLoc
{

    WorkflowXmlLoc(XmlLocation xmllocation)
    {
        if(!$assertionsDisabled && xmllocation == null)
        {
            throw new AssertionError();
        } else
        {
            xmlLoc = xmllocation;
            return;
        }
    }

    XmlLocation getXmlLoc()
    {
        return xmlLoc;
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

    private XmlLocation xmlLoc;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(WorkflowXmlLoc.class).desiredAssertionStatus();
    }
}
