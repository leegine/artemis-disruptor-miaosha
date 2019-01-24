// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.kernel.mbean;

import java.util.Set;
import javax.management.MBeanInfo;
import javax.management.ObjectName;

class DynamicMBeanMetadata
{

    DynamicMBeanMetadata(MBeanInfo mbeaninfo, Object obj, Set set, ObjectName objectname)
    {
        info = null;
        impl = null;
        boolAttrs = null;
        objName = null;
        if(!$assertionsDisabled && (mbeaninfo == null || obj == null || objectname == null))
        {
            throw new AssertionError();
        } else
        {
            info = mbeaninfo;
            impl = obj;
            boolAttrs = set;
            objName = objectname;
            return;
        }
    }

    Set getBoolAttrs()
    {
        return boolAttrs;
    }

    Object getImpl()
    {
        return impl;
    }

    MBeanInfo getInfo()
    {
        return info;
    }

    ObjectName getObjName()
    {
        return objName;
    }

    public String toString()
    {
        if(!$assertionsDisabled)
            throw new AssertionError();
        else
            return "Dynamic MBean metadata [impl=" + impl + ", obj-name=" + objName + ", mbean-info=" + info + ", bool-attrs=" + boolAttrs + ']';
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

    private MBeanInfo info;
    private Object impl;
    private Set boolAttrs;
    private ObjectName objName;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(DynamicMBeanMetadata.class).desiredAssertionStatus();
    }
}
