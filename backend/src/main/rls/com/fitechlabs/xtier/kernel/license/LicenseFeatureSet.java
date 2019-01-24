// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.kernel.license;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.utils.Utils;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.kernel.license:
//            LicenseDescriptor

public class LicenseFeatureSet
{

    LicenseFeatureSet(String s)
    {
        descriptors = new TreeMap();
        name = s;
    }

    String getName()
    {
        return name;
    }

    LicenseDescriptor[] getAllDescriptors()
    {
        return (LicenseDescriptor[])descriptors.values().toArray(new LicenseDescriptor[descriptors.size()]);
    }

    public LicenseDescriptor getDescriptor(String s)
    {
        return (LicenseDescriptor)descriptors.get(s);
    }

    boolean containsDescriptor(String s)
    {
        return descriptors.containsKey(s);
    }

    void addDescriptor(LicenseDescriptor licensedescriptor)
    {
        descriptors.put(licensedescriptor.getServiceName(), licensedescriptor);
    }

    public int hashCode()
    {
        return name.hashCode();
    }

    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;
        if(!(obj instanceof LicenseFeatureSet))
            return false;
        else
            return ((LicenseFeatureSet)obj).name.equals(name);
    }

    byte[] getSignature()
    {
        return sig;
    }

    Iterator getServicesIterator()
    {
        return descriptors.values().iterator();
    }

    void setSignature(byte abyte0[])
    {
        sig = abyte0;
    }

    byte[] toLicenseArr()
    {
        byte abyte0[] = new byte[8];
        StringBuffer stringbuffer = new StringBuffer();
        for(Iterator iterator = descriptors.keySet().iterator(); iterator.hasNext(); stringbuffer.append('|'))
            stringbuffer.append((String)iterator.next());

        Utils.encodeInt32(stringbuffer.toString().hashCode(), abyte0, Utils.encodeInt32(name.hashCode(), abyte0, 0));
        return abyte0;
    }

    public String toString()
    {
        return L10n.format("KRNL.LCNS.TXT3", name);
    }

    private String name;
    private byte sig[];
    private Map descriptors;
}
