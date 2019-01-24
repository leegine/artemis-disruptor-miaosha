// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   LicenseFeatureSet.java

package co.fin.intellioms.license;

import com.fitechlabs.xtier.utils.Utils;
import java.util.*;

// Referenced classes of package com.com.fin.intellioms.license:
//            LicenseDescriptor

public class LicenseFeatureSet
{

    public LicenseFeatureSet()
    {
        descriptors = new TreeMap();
    }

    LicenseDescriptor[] getAllDescriptors()
    {
        return (LicenseDescriptor[])(LicenseDescriptor[])descriptors.values().toArray(new LicenseDescriptor[descriptors.size()]);
    }

    public LicenseDescriptor getDescriptor(String name)
    {
        return (LicenseDescriptor)descriptors.get(name);
    }

    boolean containsDescriptor(String name)
    {
        return descriptors.containsKey(name);
    }

    void addDescriptor(LicenseDescriptor descriptor)
    {
        descriptors.put(descriptor.getServiceName(), descriptor);
    }

    public Iterator getServicesIterator()
    {
        return descriptors.values().iterator();
    }

    byte[] toLicenseArr()
    {
        byte arr[] = new byte[4];
        StringBuffer buf = new StringBuffer();
        for(Iterator iter = descriptors.keySet().iterator(); iter.hasNext(); buf.append('|'))
            buf.append((String)iter.next());

        Utils.encodeInt32(buf.toString().hashCode(), arr, 0);
        return arr;
    }

    public String toString()
    {
        return "LicenseFeatureSet[]";
    }

    private Map descriptors;
}
