// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   LicenseDescriptor.java

package co.fin.intellioms.license;

import com.fitechlabs.xtier.utils.Utils;
import java.net.InetAddress;
import java.util.Date;

public class LicenseDescriptor
{

    byte[] getSignature()
    {
        return sig;
    }

    void setSignature(byte sig[])
    {
        if(!$assertionsDisabled && sig == null)
        {
            throw new AssertionError("Signature is null.");
        } else
        {
            this.sig = sig;
            return;
        }
    }

    void setCpus(int cpus)
    {
        if(!$assertionsDisabled && cpus <= 0)
        {
            throw new AssertionError("CPUs must be above zero.");
        } else
        {
            this.cpus = cpus;
            return;
        }
    }

    void setExpDate(Date expDate)
    {
        if(!$assertionsDisabled && expDate == null)
        {
            throw new AssertionError("expDate is null.");
        } else
        {
            this.expDate = expDate;
            return;
        }
    }

    void setIps(String ips[])
    {
        this.ips = ips;
    }

    void setNodes(int nodes)
    {
        this.nodes = nodes;
    }

    LicenseDescriptor(String name)
    {
        expDate = null;
        expDateStr = null;
        ips = null;
        ipsStr = null;
        if(!$assertionsDisabled && name == null)
        {
            throw new AssertionError("Name is null.");
        } else
        {
            this.name = name;
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

    public boolean isIpLicensed(InetAddress addr)
    {
        if(ips == null)
            return true;
        for(int k = 0; k < ips.length; k++)
            if(checkIps(addr.getHostAddress(), ips[k]))
                return true;

        return false;
    }

    private boolean checkIps(String ip, String ipMask)
    {
        String maskParts[] = ipMask.split("\\.");
        String ipParts[] = ip.split("\\.");
        for(int i = 0; i < maskParts.length; i++)
            if(!maskParts[i].equals("*"))
            {
                String maskPart = trimLeadingZeros(maskParts[i]);
                if(!maskPart.equals(ipParts[i]))
                    return false;
            } else
            {
                return true;
            }

        return true;
    }

    private String trimLeadingZeros(String str)
    {
        if(str.length() == 1)
            return str;
        if(str.startsWith("00"))
            return str.substring(2);
        if(str.startsWith("0"))
            return str.substring(1);
        else
            return str;
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

    void setCpusStr(String cpusStr)
    {
        this.cpusStr = cpusStr;
    }

    void setExpDateStr(String expDateStr)
    {
        this.expDateStr = expDateStr;
    }

    void setIpsStr(String ipsStr)
    {
        this.ipsStr = ipsStr;
    }

    void setNodesStr(String nodesStr)
    {
        this.nodesStr = nodesStr;
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
        byte arr[] = new byte[20];
        int off = 0;
        off = Utils.encodeInt32(name.hashCode(), arr, off);
        off = Utils.encodeInt32(cpusStr.hashCode(), arr, off);
        off = Utils.encodeInt32(expDateStr.hashCode(), arr, off);
        off = Utils.encodeInt32(ipsStr.hashCode(), arr, off);
        off = Utils.encodeInt32(nodesStr.hashCode(), arr, off);
        return arr;
    }

    public String toString()
    {
        return (new StringBuilder()).append("LicenseDescriptor[name=").append(name).append(", expDateStr=").append(expDateStr).append(", nodes=").append(int2Str(nodes)).append(", cpus=").append(int2Str(cpus)).append(", ipStr=").append(ipsStr).append(']').toString();
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
    static final boolean $assertionsDisabled = !com/ com /fin/intellioms/license/LicenseDescriptor.desiredAssertionStatus();

}
