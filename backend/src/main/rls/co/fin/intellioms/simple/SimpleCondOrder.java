// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   SimpleCondOrder.java

package co.fin.intellioms.simple;

import co.fin.intellioms.persist.PersistAdaptor;
import co.fin.intellioms.persist.PersistException;
import co.fin.intellioms.rulesys.CondOrderException;
import co.fin.intellioms.rulesys.Ratio;
import co.fin.intellioms.rulesys.impl.AbstractCondOrder;
import co.fin.intellioms.enums.CondOrderState;
import co.fin.intellioms.enums.CondOrderType;
import com.fitechlabs.fin.intellioms.omsclt.*;
import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.services.ioc.IocService;
import com.fitechlabs.xtier.services.ioc.IocServiceException;
import com.fitechlabs.xtier.services.marshal.Marshallable;
import java.util.*;

// Referenced classes of package com.com.fin.intellioms.simple:
//            SimpleOrderRule

public class SimpleCondOrder extends AbstractCondOrder
    implements Marshallable
{
    private static class MarshalConst
    {

        private static final Integer ACTIVATED_RATIO = new Integer(2001);
        private static final Integer OMS_TEMPLATE = new Integer(2002);
        private static final Integer OMS_ORDERS = new Integer(2003);





        private MarshalConst()
        {
        }
    }


    public SimpleCondOrder(long condOrderId, long accountId, Order omsOrder)
    {
        super(condOrderId, accountId, CondOrderType.SIMPLE, omsOrder);
        submitedOrders = new ArrayList();
        activeRatio = new Ratio(1.0D);
        store = null;
        if(!$assertionsDisabled && omsOrder == null)
            throw new AssertionError("OMS order is null.");
        else
            return;
    }

    public SimpleCondOrder()
    {
        super(CondOrderType.SIMPLE);
        submitedOrders = new ArrayList();
        activeRatio = new Ratio(1.0D);
        store = null;
    }

    public SimpleCondOrder(long condOrderId, long accountId, Order omsOrder, Ratio activatedRatio)
    {
        super(condOrderId, accountId, CondOrderType.SIMPLE, omsOrder);
        submitedOrders = new ArrayList();
        activeRatio = new Ratio(1.0D);
        store = null;
        if(!$assertionsDisabled && omsOrder == null)
        {
            throw new AssertionError("OMS order is null.");
        } else
        {
            activeRatio = activatedRatio;
            return;
        }
    }

    public Order getOmsOrderTemplate()
    {
        return (Order)getParam();
    }

    public Quantity getSubmittedQuantity()
    {
        return getSubmittedQty();
    }

    public void setActivatedRatio(Ratio activatedRatio)
        throws CondOrderException
    {
        if(!$assertionsDisabled && !activatedRatio.isBelowEq(Ratio.ALL))
            throw new AssertionError((new StringBuilder()).append("Activated ratio must be below or equals to").append(Ratio.ALL.getBigDecimal()).toString());
        if(!$assertionsDisabled && !activatedRatio.isAboveEq(Ratio.ZERO))
            throw new AssertionError((new StringBuilder()).append("Activated ratio must be above or equals to").append(Ratio.ZERO.getBigDecimal()).toString());
        Ratio oldRatio = activeRatio;
        Ratio execRatio = getExecutedRatio();
        if(activatedRatio.isBelow(execRatio))
            activeRatio = new Ratio(execRatio);
        else
            activeRatio = activatedRatio;
        if(!oldRatio.equals(activeRatio))
        {
            if(store == null)
            {
                if(rule != null)
                    store = rule.getStore();
                if(store == null)
                    try
                    {
                        store = (PersistAdaptor)XtierKernel.getInstance().ioc().makeIocObject("store");
                    }
                    catch(IocServiceException e)
                    {
                        throw new CondOrderException("Unable to locate PersistAdaptor to store activation ratio.", e, this);
                    }
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
            if(rule != null && getState() != CondOrderState.CANCELED)
                rule.modify(this, oldRatio);
        }
    }

    public Ratio getActivatedRatio()
    {
        return activeRatio;
    }

    public Ratio getExecutedRatio()
    {
        Quantity origQty = getOmsOrderTemplate().getOriginalQuantity();
        Quantity execQty = getExecutedQty();
        if(execQty.isAbove(Quantity.ZERO))
        {
            Ratio execRatio = new Ratio(execQty);
            execRatio.devide(origQty);
            return execRatio.isBelowEq(Ratio.ALL) ? execRatio : new Ratio(Ratio.ALL);
        } else
        {
            return new Ratio(0.0D);
        }
    }

    public List getSubmitedOrders()
    {
        return submitedOrders;
    }

    public void setSubmitedOrders(List submitedOrders)
    {
        this.submitedOrders = submitedOrders;
    }

    public void addSubmittedOrder(Order omsOrder)
    {
        submitedOrders.add(omsOrder);
    }

    public Quantity getExecutedQty()
    {
        return new Quantity(getOmsOrderTemplate().getExecutedQuantity());
    }

    public short typeGuid()
    {
        return 1;
    }

    void setRule(SimpleOrderRule rule)
    {
        this.rule = rule;
    }

    void applyActivatedRatio(Ratio activatedRatio)
    {
        activeRatio = activatedRatio;
    }

    Order getSubmittedOrder(OrderId orderId)
    {
        for(int i = 0; i < submitedOrders.size(); i++)
        {
            Order omsOrder = (Order)submitedOrders.get(i);
            if(omsOrder.getOrderId().equals(orderId))
                return omsOrder;
        }

        return null;
    }

    Order removeSubmittedOrder(OrderId orderId)
    {
        for(Iterator iter = submitedOrders.iterator(); iter.hasNext();)
        {
            Order omsOrder = (Order)iter.next();
            if(omsOrder.getOrderId().equals(orderId))
            {
                iter.remove();
                return omsOrder;
            }
        }

        return null;
    }

    private Quantity getSubmittedQty()
    {
        Quantity qty = new Quantity(0.0D);
        for(int i = 0; i < submitedOrders.size(); i++)
        {
            Order ord = (Order)submitedOrders.get(i);
            if(!$assertionsDisabled && ord == null)
                throw new AssertionError();
            qty.add(ord.getOriginalQuantity());
        }

        return qty;
    }

    protected void marshal(Map params)
    {
        params.put(MarshalConst.ACTIVATED_RATIO, activeRatio.toString());
        params.put(MarshalConst.OMS_TEMPLATE, getOmsOrderTemplate());
        params.put(MarshalConst.OMS_ORDERS, submitedOrders);
    }

    protected void demarshal(Map params)
    {
        String actRatio = (String)params.get(MarshalConst.ACTIVATED_RATIO);
        Order template = (Order)params.get(MarshalConst.OMS_TEMPLATE);
        List submitted = (List)params.get(MarshalConst.OMS_ORDERS);
        activeRatio = new Ratio(actRatio);
        setParam(template);
        submitedOrders = submitted;
    }

    public static final short GUID = 1;
    private SimpleOrderRule rule;
    private List submitedOrders;
    private Ratio activeRatio;
    private PersistAdaptor store;
    static final boolean $assertionsDisabled = !com/ com /fin/intellioms/simple/SimpleCondOrder.desiredAssertionStatus();

}
