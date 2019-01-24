// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.kernel.license;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.utils.Utils;
import java.net.InetAddress;
import java.util.Date;

public class LicenseDescriptor
{

    byte[] getSignature()
    {
        return sig;
    }

    void setSignature(byte abyte0[])
    {
        if(!$assertionsDisabled && abyte0 == null)
        {
            throw new AssertionError();
        } else
        {
            sig = abyte0;
            return;
        }
    }

    void setCpus(int i)
    {
        if(!$assertionsDisabled && i <= 0)
        {
            throw new AssertionError();
        } else
        {
            cpus = i;
            return;
        }
    }

    void setExpDate(Date date)
    {
        if(!$assertionsDisabled && date == null)
        {
            throw new AssertionError();
        } else
        {
            expDate = date;
            return;
        }
    }

    void setIps(String as[])
    {
        ips = as;
    }

    void setNodes(int i)
    {
        nodes = i;
    }

    LicenseDescriptor(String s)
    {
        expDate = null;
        expDateStr = null;
        ips = null;
        ipsStr = null;
        if(!$assertionsDisabled && s == null)
        {
            throw new AssertionError();
        } else
        {
            name = s;
            return;
        }
    }

    public String getServiceName()
    {
        return name;
    }

    public String[] getIps()
    {
        return ips;
    }

    public boolean isIpLicensed(InetAddress inetaddress)
    {
        if(ips == null)
            return true;
        for(int i = 0; i < ips.length; i++)
            if(checkIps(inetaddress.getHostAddress(), ips[i]))
                return true;

        return false;
    }

    private boolean checkIps(String s, String s1)
    {
        String as[] = s1.split("\\.");
        String as1[] = s.split("\\.");
        for(int i = 0; i < as.length; i++)
            if(!as[i].equals("*"))
            {
                String s2 = trimLeadingZeros(as[i]);
                if(!s2.equals(as1[i]))
                    return false;
            } else
            {
                return true;
            }

        return true;
    }

    private String trimLeadingZeros(String s)
    {
        if(s.length() == 1)
            return s;
        if(s.startsWith("00"))
            return s.substring(2);
        if(s.startsWith("0"))
            return s.substring(1);
        else
            return s;
    }

    public int getCpus()
    {
        return cpus;
    }

    public Date getExpDate()
    {
        return expDate;
    }

    public int getNodes()
    {
        return nodes;
    }

    void setCpusStr(String s)
    {
        cpusStr = s;
    }

    void setExpDateStr(String s)
    {
        expDateStr = s;
    }

    void setIpsStr(String s)
    {
        ipsStr = s;
    }

    void setNodesStr(String s)
    {
        nodesStr = s;
    }

    public int hashCode()
    {
        return name.hashCode();
    }

    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;
        if(!(obj instanceof LicenseDescriptor))
            return false;
        else
            return ((LicenseDescriptor)obj).name.equals(name);
    }

    private String int2Str(int i)
    {
        return i != 0x7fffffff ? Integer.toString(i) : "any";
    }

    byte[] toLicenseArr()
    {
        byte abyte0[] = new byte[20];
        int i = 0;
        i = Utils.encodeInt32(name.hashCode(), abyte0, i);
        i = Utils.encodeInt32(cpusStr.hashCode(), abyte0, i);
        i = Utils.encodeInt32(expDateStr.hashCode(), abyte0, i);
        i = Utils.encodeInt32(ipsStr.hashCode(), abyte0, i);
        i = Utils.encodeInt32(nodesStr.hashCode(), abyte0, i);
        return abyte0;
    }

    public String toString()
    {
        return L10n.format("KRNL.LCNS.TXT2", name, expDateStr, int2Str(nodes), int2Str(cpus), ipsStr);
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
    private Date expDate;
    private String expDateStr;
    private int nodes;
    private String nodesStr;
    private int cpus;
    private String cpusStr;
    private String ips[];
    private String ipsStr;
    private byte sig[];
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(LicenseDescriptor.class).desiredAssertionStatus();
    }
}
