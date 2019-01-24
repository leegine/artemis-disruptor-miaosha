// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   ChainCondOrder.java

package co.fin.intellioms.chain;

import co.fin.intellioms.enums.CondOrderState;
import co.fin.intellioms.enums.CondOrderType;
import co.fin.intellioms.rulesys.CondOrder;
import co.fin.intellioms.rulesys.CondOrderException;
import co.fin.intellioms.rulesys.Ratio;
import co.fin.intellioms.rulesys.impl.AbstractCondOrder;
import co.fin.intellioms.util.Log;
import com.fitechlabs.xtier.utils.ArgAssert;

import java.util.Iterator;
import java.util.Map;

// Referenced classes of package com.com.fin.intellioms.chain:
//            ChainCondition, ChainOrderRule

public class ChainCondOrder extends AbstractCondOrder
{
    private static class MarshalConst
    {

        private static final Integer INCREMENTAL = new Integer(5001);



        private MarshalConst()
        {
        }
    }


    public ChainCondOrder(long condOrderId, long accountId, ChainCondition cond)
    {
        super(condOrderId, accountId, CondOrderType.CHAIN, cond);
    }

    public ChainCondOrder()
    {
        super(CondOrderType.CHAIN);
    }

    public CondOrder getCurrentChainElement()
    {
        if( subOrders == null)
            throw new AssertionError((new StringBuilder()).append("No suborders in chain order: ").append(this).toString());
        if(subOrders.size() <= 0)
            throw new AssertionError((new StringBuilder()).append("No suborders in chain order: ").append(this).toString());
        int size = subOrders.size();
        for(int i = 0; i < size; i++)
        {
            CondOrder child = (CondOrder)subOrders.get(i);
            if(child.getState() == CondOrderState.PENDING && i > 0)
                return (CondOrder)subOrders.get(i - 1);
            if(i == size - 1)
                return child;
        }

        return null;
    }

    public Ratio getActivatedRatio()
    {
        return getFirstChild().getActivatedRatio();
    }

    private CondOrder getFirstChild()
    {
        if(subOrders == null || subOrders.size() == 0)
            throw new IllegalStateException((new StringBuilder()).append("Missing suborders of conditional chain order: ").append(this).toString());
        else
            return (CondOrder)subOrders.get(0);
    }

    public void setActivatedRatio(Ratio activatedRatio)
        throws CondOrderException
    {
        ArgAssert.nullArg(activatedRatio, "activatedRatio");
        getFirstChild().setActivatedRatio(activatedRatio);
    }

    public Ratio getExecutedRatio()
    {
        if(subOrders == null)
            return null;
        int cnt = 0;
        CondOrder theLast = null;
        int size = subOrders.size();
        for(int i = 0; i < size; i++)
        {
            CondOrder ord = (CondOrder)subOrders.get(i);
            if(ord.getExecutedRatio().isBelow(Ratio.ALL))
            {
                cnt++;
                theLast = ord;
            }
        }

        if(cnt == 0)
            return new Ratio(Ratio.ALL);
        if(cnt == 1)
            return theLast.getExecutedRatio();
        else
            return new Ratio(Ratio.NONE);
    }

    public CondOrder getNextChainElement()
    {
        if(subOrders == null)
            throw new AssertionError((new StringBuilder()).append("No suborders in chain order: ").append(this).toString());
        if(subOrders.size() <= 0)
            throw new AssertionError((new StringBuilder()).append("No suborders in chain order: ").append(this).toString());
        int size = subOrders.size();
        for(int i = 0; i < size; i++)
        {
            CondOrder child = (CondOrder)subOrders.get(i);
            if(child.getState() == CondOrderState.PENDING)
                return child;
        }

        return null;
    }

    public void processExecution(CondOrder executed, Ratio executedRatio)
        throws CondOrderException
    {
        update(executed, executedRatio);
        Ratio exec = getExecutedRatio();
        if(exec.isAbove(getActivatedRatio()))
            setActivatedRatio(exec);
        if(exec.equals(Ratio.ALL))
        {
            if(getState() == CondOrderState.ACTIVATED)
                rule.finishChain(this);
            super.processExecution(this, new Ratio(exec));
        } else
        if(exec.isAbove(Ratio.ZERO))
            super.processExecution(this, new Ratio(executedRatio));
    }

    void update(CondOrder updated, Ratio execRatio)
        throws CondOrderException
    {
        if( rule == null)
            throw new AssertionError((new StringBuilder()).append("Chain order was not registered to its rule: ").append(this).toString());
        int idx = subOrders.indexOf(updated);
        if(isIncremental())
        {
            if(idx >= 0 && idx + 1 < subOrders.size())
            {
                Ratio actRatio = new Ratio(execRatio);
                CondOrder subOrd = (CondOrder)subOrders.get(idx + 1);
                if(!subOrd.getActivatedRatio().equals(actRatio))
                {
                    if(log.isDebug())
                        log.debug((new StringBuilder()).append("Setting activated ratio for suborder [ratio=").append(actRatio).append(", ord=").append(subOrd).append(']').toString());
                    subOrd.setActivatedRatio(actRatio);
                }
            }
            if(idx < subOrders.size() - 1)
            {
                CondOrder next = (CondOrder)subOrders.get(idx + 1);
                if(next.getState() == CondOrderState.PENDING)
                    rule.switchChain(this);
            }
        } else
        if(idx < subOrders.size() - 1 && execRatio.equals(Ratio.ALL))
        {
            CondOrder next = (CondOrder)subOrders.get(idx + 1);
            if(next.getState() == CondOrderState.PENDING)
                rule.switchChain(this);
        }
    }

    void update()
        throws CondOrderException
    {
        if(getState() != CondOrderState.CANCELED)
        {
            Iterator iter = subOrders.iterator();
            do
            {
                if(!iter.hasNext())
                    break;
                CondOrder subOrd = (CondOrder)iter.next();
                if(subOrd.getState() != CondOrderState.CANCELED)
                    update(subOrd, subOrd.getExecutedRatio());
            } while(true);
        }
    }

    void setRule(ChainOrderRule rule)
    {
        this.rule = rule;
    }

    public boolean isIncremental()
    {
        return ((ChainCondition)getParam()).isIncremental();
    }

    protected void marshal(Map params)
    {
        if(isIncremental())
            params.put(MarshalConst.INCREMENTAL, Boolean.TRUE);
    }

    protected void demarshal(Map params)
    {
        if(params.containsKey(MarshalConst.INCREMENTAL))
            setParam(new ChainCondition(true));
        else
            setParam(new ChainCondition(false));
    }

    public short typeGuid()
    {
        return 5;
    }

    public static final short GUID = 5;
    private static final Log log = Log.getLogger(ChainCondOrder.class);
    private ChainOrderRule rule;
    //static final boolean $assertionsDisabled = !com/ com /fin/intellioms/chain/ChainCondOrder.desiredAssertionStatus();

}
