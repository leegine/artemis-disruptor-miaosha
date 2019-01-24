// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.cluster.filters;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.cluster.ClusterFilter;
import com.fitechlabs.xtier.services.cluster.ClusterNode;
import com.fitechlabs.xtier.utils.ArgAssert;

// Referenced classes of package com.fitechlabs.xtier.services.cluster.filters:
//            ClusterFilterAdapter

public class ClusterServiceFilter extends ClusterFilterAdapter
{

    public ClusterServiceFilter()
    {
        name = null;
    }

    public ClusterServiceFilter(String s)
    {
        name = null;
        ArgAssert.nullArg(s, "name");
        name = s;
    }

    public ClusterServiceFilter(String s, ClusterFilter clusterfilter)
    {
        super(clusterfilter);
        name = null;
        ArgAssert.nullArg(s, "name");
        name = s;
    }

    public String getServiceName()
    {
        return name;
    }

    public void setServiceName(String s)
    {
        ArgAssert.nullArg(s, "name");
        name = s;
    }

    public boolean accept(ClusterNode clusternode)
    {
        if(name == null)
            throw new IllegalArgumentException(L10n.format("SRVC.CLUSTER.ERR30"));
        ClusterFilter clusterfilter = getNested();
        if(clusterfilter != null && !clusterfilter.accept(clusternode))
            return false;
        else
            return clusternode.hasService(name);
    }

    private String name;
}
