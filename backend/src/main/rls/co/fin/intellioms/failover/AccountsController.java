// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   AccountsController.java

package co.fin.intellioms.failover;

import co.fin.intellioms.account.AccountIDRange;
import java.util.*;

// Referenced classes of package com.com.fin.intellioms.failover:
//            RuleEngineNode

public class AccountsController
{
    private class NodeHolder
    {

        RuleEngineNode getNode()
        {
            return node;
        }

        public AccountIDRange getRange()
        {
            return range;
        }

        public void setRange(AccountIDRange range)
        {
            this.range = range;
        }

        public int hashCode()
        {
            return node.hashCode();
        }

        public boolean equals(Object o)
        {
            return node.equals(((NodeHolder)o).node);
        }

        private final RuleEngineNode node;
        private AccountIDRange range;
        final AccountsController this$0;

        NodeHolder(RuleEngineNode node)
        {
            this$0 = AccountsController.this;
            super();
            this.node = node;
        }
    }


    public AccountsController(AccountIDRange allAccounts, List topology)
    {
        topologyComparator = new Comparator() {

            public int compare(Object o1, Object o2)
            {
                NodeHolder n1 = (NodeHolder)o1;
                NodeHolder n2 = (NodeHolder)o2;
                return n1.getNode().compareTo(n2.getNode());
            }

            final AccountsController this$0;


            {
                this$0 = AccountsController.this;
                super();
            }
        }
;
        if(!$assertionsDisabled && topology == null)
            throw new AssertionError("Topology is null.");
        allAccountsRange = allAccounts;
        for(int i = 0; i < topology.size(); i++)
        {
            RuleEngineNode node = (RuleEngineNode)topology.get(i);
            registerNode(node);
        }

        updateAccounts();
    }

    public RuleEngineNode getNodeByAccountId(long accountId)
    {
        Object obj = mux;
        JVM INSTR monitorenter ;
        int i = 0;
_L1:
        if(i >= nodes.size())
            break MISSING_BLOCK_LABEL_81;
        NodeHolder holder = (NodeHolder)nodes.get(i);
        AccountIDRange rng = holder.getRange();
        if(accountId >= rng.getStart() && accountId <= rng.getEnd())
            return holder.getNode();
        i++;
          goto _L1
        null;
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public AccountIDRange getAccountsRange()
    {
        return localNode == null ? null : localNode.getRange();
    }

    public AccountIDRange getAllAccountsRange()
    {
        return allAccountsRange;
    }

    public AccountIDRange getAccountsRange(RuleEngineNode node)
    {
        Object obj = mux;
        JVM INSTR monitorenter ;
        NodeHolder holder = (NodeHolder)nodesToHelpers.get(node);
        return holder == null ? null : holder.getRange();
        Exception exception;
        exception;
        throw exception;
    }

    public void addNode(RuleEngineNode node)
    {
        synchronized(mux)
        {
            if(registerNode(node))
                updateAccounts();
        }
    }

    public void removeNode(RuleEngineNode node)
    {
        synchronized(mux)
        {
            Iterator iter = nodes.iterator();
            do
            {
                if(!iter.hasNext())
                    break;
                NodeHolder holder = (NodeHolder)iter.next();
                if(!holder.getNode().equals(node))
                    continue;
                iter.remove();
                break;
            } while(true);
            nodesToHelpers.remove(node);
            if(node.isLocalNode())
                localNode = null;
            updateAccounts();
        }
    }

    public List getRuleEngines()
    {
        Object obj = mux;
        JVM INSTR monitorenter ;
        List engines = new ArrayList(nodes.size());
        for(int i = 0; i < nodes.size(); i++)
        {
            NodeHolder h = (NodeHolder)nodes.get(i);
            engines.add(h.getNode());
        }

        return engines;
        Exception exception;
        exception;
        throw exception;
    }

    private boolean registerNode(RuleEngineNode node)
    {
        NodeHolder holder = new NodeHolder(node);
        if(nodes.contains(holder))
            return false;
        if(node.isLocalNode())
            localNode = holder;
        nodes.add(holder);
        nodesToHelpers.put(node, holder);
        return true;
    }

    private void updateAccounts()
    {
        if(nodes.size() == 0)
            return;
        Collections.sort(nodes, topologyComparator);
        long step = (allAccountsRange.getEnd() - allAccountsRange.getStart()) / (long)nodes.size();
        for(int i = 0; i < nodes.size(); i++)
        {
            NodeHolder node = (NodeHolder)nodes.get(i);
            NodeHolder prev = i <= 0 ? null : (NodeHolder)nodes.get(i - 1);
            NodeHolder next = i >= nodes.size() - 1 ? null : (NodeHolder)nodes.get(i + 1);
            long start = prev == null ? allAccountsRange.getStart() : prev.getRange().getEnd() + 1L;
            if(start <= allAccountsRange.getEnd() && (prev == null || prev.getRange().getEnd() != -1L))
            {
                long end = start + step;
                end = end <= allAccountsRange.getEnd() && next != null ? end : allAccountsRange.getEnd();
                node.setRange(new AccountIDRange(start, end));
            } else
            {
                node.setRange(new AccountIDRange(-1L, -1L));
            }
        }

    }

    private final List nodes = new ArrayList();
    private final Map nodesToHelpers = new HashMap();
    private final Object mux = new Object();
    private NodeHolder localNode;
    private final AccountIDRange allAccountsRange;
    private Comparator topologyComparator;
    static final boolean $assertionsDisabled = !com/ com /fin/intellioms/failover/AccountsController.desiredAssertionStatus();

}
