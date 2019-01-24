// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   ClusterUtil.java

package co.fin.intellioms.util;

import co.fin.intellioms.failover.RuleEngineNode;
import com.fitechlabs.xtier.services.cluster.ClusterGroupMembership;
import com.fitechlabs.xtier.services.cluster.ClusterNode;

public class ClusterUtil
{

    public ClusterUtil()
    {
    }

    public static RuleEngineNode getRuleEngineNode(ClusterNode n)
    {
        ClusterGroupMembership gr = n.getGroupMembership("rule-engines");
        if(gr == null)
            return null;
        try
        {
            int port = Integer.parseInt((String)gr.getGroupProps().get("rule-engine-connector-port"));
            return new RuleEngineNode(n.getAddress().getHostAddress(), port, n.isLocalNode());
        }
        catch(NumberFormatException e)
        {
            return null;
        }
    }
}
