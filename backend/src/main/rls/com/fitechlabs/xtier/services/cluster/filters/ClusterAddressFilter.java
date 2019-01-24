// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.cluster.filters;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.cluster.ClusterFilter;
import com.fitechlabs.xtier.services.cluster.ClusterNode;
import com.fitechlabs.xtier.utils.ArgAssert;
import java.net.InetAddress;

// Referenced classes of package com.fitechlabs.xtier.services.cluster.filters:
//            ClusterFilterAdapter

public class ClusterAddressFilter extends ClusterFilterAdapter
{

    public ClusterAddressFilter()
    {
        addr = null;
    }

    public ClusterAddressFilter(InetAddress inetaddress)
    {
        addr = null;
        ArgAssert.nullArg(inetaddress, "addr");
        addr = inetaddress;
    }

    public ClusterAddressFilter(InetAddress inetaddress, ClusterFilter clusterfilter)
    {
        super(clusterfilter);
        addr = null;
        ArgAssert.nullArg(inetaddress, "addr");
        addr = inetaddress;
    }

    public InetAddress getAddress()
    {
        return addr;
    }

    public void setAddress(InetAddress inetaddress)
    {
        ArgAssert.nullArg(inetaddress, "addr");
        addr = inetaddress;
    }

    public boolean accept(ClusterNode clusternode)
    {
        if(addr == null)
            throw new IllegalArgumentException(L10n.format("SRVC.CLUSTER.ERR30"));
        ClusterFilter clusterfilter = getNested();
        if(clusterfilter != null && !clusterfilter.accept(clusternode))
            return false;
        else
            return clusternode.getAddress().equals(addr);
    }

    private InetAddress addr;
}
