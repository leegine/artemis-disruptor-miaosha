// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   FailoverCoordinator.java

package co.fin.intellioms.failover;

import com.fitechlabs.fin.intellioms.license.*;
import com.fitechlabs.fin.intellioms.util.*;
import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.services.cluster.*;
import java.util.*;

// Referenced classes of package com.com.fin.intellioms.failover:
//            FailoverListener, RuleEngineNode

public class FailoverCoordinator
    implements Startable
{

    public FailoverCoordinator(FailoverListener lstnr)
    {
        log = Log.getLogger(com/ com /fin/intellioms/failover/FailoverCoordinator);
        cluster = XtierKernel.getInstance().cluster();
        this.lstnr = lstnr;
    }

    public void start()
        throws InitializationException
    {
        if(log.isInfo())
            log.info("Starting...");
        Set nodes = topologyToRuleEngineNodes(cluster.getAllNodes());
        try
        {
            LicenseManager.init();
            for(Iterator iter = LicenseManager.getActiveFeatureSet().getServicesIterator(); iter.hasNext();)
            {
                LicenseDescriptor sd = (LicenseDescriptor)iter.next();
                if(sd.getNodes() < nodes.size())
                    throw new LicenseException((new StringBuilder()).append("Rule engines number exceeded number of allowed nodes for service '").append(sd.getServiceName()).append("'.").toString());
            }

        }
        catch(LicenseException e)
        {
            throw new InitializationException(e.getMessage(), e);
        }
        lstnr.start(new HashSet(nodes));
        cluster.addListener(new ClusterListener() {

            public void onNodeEvent(int eventId, ClusterNode node, int topologyVersion)
            {
                RuleEngineNode ruleNode = ClusterUtil.getRuleEngineNode(node);
                if(ruleNode != null)
                {
                    Set ruleEngines = topologyToRuleEngineNodes(cluster.getAllNodes());
                    switch(eventId)
                    {
                    case 1: // '\001'
                        lstnr.onNodeJoin(ruleNode, ruleEngines);
                        break;

                    case 3: // '\003'
                        lstnr.onNodeFailure(ruleNode, ruleEngines);
                        break;

                    case 2: // '\002'
                        lstnr.onNodeLeft(ruleNode, ruleEngines);
                        break;
                    }
                }
            }

            final FailoverCoordinator this$0;


            {
                this$0 = FailoverCoordinator.this;
                super();
            }
        }
);
        if(log.isInfo())
            log.info("Started.");
    }

    public void stop()
    {
        lstnr.stop();
        if(log.isInfo())
            log.info("Stopped.");
    }

    private Set topologyToRuleEngineNodes(Set topology)
    {
        HashSet rules = new HashSet();
        if(topology != null)
        {
            Iterator iter = topology.iterator();
            do
            {
                if(!iter.hasNext())
                    break;
                ClusterNode n = (ClusterNode)iter.next();
                RuleEngineNode rn = ClusterUtil.getRuleEngineNode(n);
                if(rn != null)
                    rules.add(rn);
            } while(true);
        }
        return rules;
    }

    private Log log;
    private FailoverListener lstnr;
    private ClusterService cluster;



}
