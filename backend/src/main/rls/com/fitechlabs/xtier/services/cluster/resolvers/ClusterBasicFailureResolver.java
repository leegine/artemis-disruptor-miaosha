// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.cluster.resolvers;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.cluster.ClusterNode;
import com.fitechlabs.xtier.services.cluster.ClusterNodeFailureResolver;

public class ClusterBasicFailureResolver
    implements ClusterNodeFailureResolver
{

    public ClusterBasicFailureResolver(boolean flag)
    {
        fail = flag;
    }

    public boolean isFail()
    {
        return fail;
    }

    public void setFail(boolean flag)
    {
        fail = flag;
    }

    public boolean isFailed(ClusterNode clusternode)
    {
        return fail;
    }

    public String toString()
    {
        return L10n.format("SRVC.CLUSTER.TXT2", Boolean.valueOf(fail));
    }

    private boolean fail;
}
