// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   ExclusiveCondOrder.java

package co.fin.intellioms.exclusive;

import co.fin.intellioms.enums.CondOrderState;
import co.fin.intellioms.enums.CondOrderType;
import co.fin.intellioms.persist.PersistAdaptor;
import co.fin.intellioms.persist.PersistException;
import co.fin.intellioms.rulesys.CondOrder;
import co.fin.intellioms.rulesys.CondOrderException;
import co.fin.intellioms.rulesys.Ratio;
import co.fin.intellioms.rulesys.impl.AbstractCondOrder;
import co.fin.intellioms.util.Log;
import com.fitechlabs.fin.intellioms.rulesys.*;
import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.services.ioc.IocServiceException;
import com.fitechlabs.xtier.utils.ArgAssert;

import java.util.Iterator;
import java.util.Map;

// Referenced classes of package com.com.fin.intellioms.exclusive:
//            ExclusiveOrderRule

public class ExclusiveCondOrder extends AbstractCondOrder
{
    private static class MarshalConst
    {

        private static final Integer ACTIVATED_RATIO = new Integer(3001);



        private MarshalConst()
        {
        }
    }


    public ExclusiveCondOrder(long condOrderId, long accountId)
    {
        super(condOrderId, accountId, CondOrderType.EXCLUSIVE, null);
        activatedRatio = new Ratio(1.0D);
    }

    public ExclusiveCondOrder()
    {
        super(CondOrderType.EXCLUSIVE);
    }

    public ExclusiveCondOrder(long condOrderId, long accountId, Ratio activationRatio)
    {
        super(condOrderId, accountId, CondOrderType.EXCLUSIVE, null);
        activatedRatio = activationRatio;
    }

    public Ratio getActivatedRatio()
    {
        return activatedRatio;
    }

    public Ratio getExecutedRatio()
    {
        Ratio rt = new Ratio(0.0D);
        for(int i = 0; i < subOrders.size(); i++)
        {
            CondOrder ord = (CondOrder)subOrders.get(i);
            rt.add(ord.getExecutedRatio());
        }

        return !rt.isBelowEq(Ratio.ALL) ? new Ratio(Ratio.ALL) : rt;
    }

    public void setActivatedRatio(Ratio activatedRatio)
        throws CondOrderException
    {
        ArgAssert.nullArg(activatedRatio, "activatedRatio");
        ArgAssert.illegalArg(activatedRatio.isAboveEq(Ratio.ZERO), "activatedRatio");
        if(getState() != CondOrderState.CANCELED)
        {
            Ratio ratio;
            if(this.activatedRatio.isAbove(Ratio.ZERO))
            {
                ratio = new Ratio(activatedRatio);
                ratio.devide(this.activatedRatio);
            } else
            {
                ratio = activatedRatio;
            }
            for(int i = 0; i < subOrders.size(); i++)
            {
                CondOrder child = (CondOrder)subOrders.get(i);
                Ratio newRatio = new Ratio(child.getActivatedRatio());
                if(child.getActivatedRatio().equals(Ratio.ZERO))
                    newRatio.add(ratio);
                else
                    newRatio.multiply(ratio);
                if(newRatio.isAbove(Ratio.ALL))
                    child.setActivatedRatio(new Ratio(1.0D));
                else
                    child.setActivatedRatio(newRatio);
            }

        }
        this.activatedRatio = activatedRatio;
        PersistAdaptor store = null;
        try
        {
            store = (PersistAdaptor)XtierKernel.getInstance().ioc().makeIocObject("store");
        }
        catch(IocServiceException e)
        {
            throw new CondOrderException("Unable to locate PersistAdaptor to store activation ratio.", e, this);
        }
        if(store == null)
            throw new CondOrderException("Unable to locate PersistAdaptor to store activation ratio.", this);
        try
        {
            store.updateActivationRatio(this);
        }
        catch(PersistException e)
        {
            throw new CondOrderException(e.getMessage(), e, this);
        }
    }

    public void processExecution(CondOrder executed, Ratio executedRatio)
        throws CondOrderException
    {
        ArgAssert.nullArg(executed, "executed");
        ArgAssert.nullArg(executedRatio, "executedRatio");
        Ratio thisExecRatio = getExecutedRatio();
        if(thisExecRatio.isAbove(getActivatedRatio()))
            setActivatedRatio(thisExecRatio);
        update(executed, executedRatio);
        super.processExecution(executed, getExecutedRatio());
    }

    void update(CondOrder updated, Ratio execRatio)
        throws CondOrderException
    {
        long updatedId = updated.getId();
        Ratio newTradeRatio = new Ratio(updated.getActivatedRatio());
        newTradeRatio.subtruct(execRatio);
        if(!$assertionsDisabled && !newTradeRatio.isAboveEq(Ratio.ZERO))
            throw new AssertionError((new StringBuilder()).append(execRatio).append(" ~ ").append(newTradeRatio).append(" ~ ").append(updated).toString());
        if(activatedRatio.equals(Ratio.ALL) && newTradeRatio.equals(Ratio.ZERO))
        {
            for(int i = 0; i < subOrders.size(); i++)
            {
                CondOrder sub = (CondOrder)subOrders.get(i);
                if(!sub.getExecutedRatio().equals(Ratio.ALL) && sub.getState() != CondOrderState.CANCELED)
                {
                    if(log.isDebug())
                        log.debug((new StringBuilder()).append("Cancelling ").append(sub).toString());
                    rule.getRuleContext().cancel(sub);
                }
            }

        } else
        {
            for(int i = 0; i < subOrders.size(); i++)
            {
                CondOrder sub = (CondOrder)subOrders.get(i);
                if(updatedId == sub.getId() || sub.getState() == CondOrderState.CANCELED)
                    continue;
                Ratio newActRatio = (Ratio)(new Ratio(sub.getExecutedRatio())).add(newTradeRatio);
                if(newActRatio.isAbove(Ratio.ALL))
                    newActRatio = new Ratio(Ratio.ALL);
                if(log.isDebug())
                    log.debug((new StringBuilder()).append("Setting activated ratio to ").append(newActRatio).append(" for ").append(sub).toString());
                if(!sub.getActivatedRatio().equals(newActRatio))
                    sub.setActivatedRatio(newActRatio);
            }

        }
        if(activatedRatio.equals(Ratio.ALL) && newTradeRatio.equals(Ratio.ZERO) && getState() == CondOrderState.ACTIVATED)
            rule.finishOrder(this);
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
                if(!subOrd.getState().equals(CondOrderState.CANCELED))
                    update(subOrd, subOrd.getExecutedRatio());
            } while(true);
        }
    }

    void setRule(ExclusiveOrderRule rule)
    {
        this.rule = rule;
    }

    protected void marshal(Map params)
    {
        params.put(MarshalConst.ACTIVATED_RATIO, activatedRatio.toString());
    }

    protected void demarshal(Map params)
    {
        String actRatio = (String)params.get(MarshalConst.ACTIVATED_RATIO);
        activatedRatio = new Ratio(actRatio);
    }

    public short typeGuid()
    {
        return 4;
    }

    public static final short GUID = 4;
    private static final Log log = Log.getLogger(com/ com /fin/intellioms/exclusive/ExclusiveCondOrder);
    private ExclusiveOrderRule rule;
    private Ratio activatedRatio;
    static final boolean $assertionsDisabled = !com/ com /fin/intellioms/exclusive/ExclusiveCondOrder.desiredAssertionStatus();

}
