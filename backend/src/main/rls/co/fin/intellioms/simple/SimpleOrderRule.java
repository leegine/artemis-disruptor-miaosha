// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   SimpleOrderRule.java

package co.fin.intellioms.simple;

import co.fin.intellioms.persist.PersistAdaptor;
import co.fin.intellioms.persist.PersistException;
import co.fin.intellioms.tx.TxManager;
import co.fin.intellioms.tx.TxManagerException;
import com.fitechlabs.fin.intellioms.enums.*;
import co.fin.intellioms.event.Event;
import co.fin.intellioms.event.EventException;
import com.fitechlabs.fin.intellioms.omsclt.*;
import co.fin.intellioms.omsclt.impl.OrderImpl;
import com.fitechlabs.fin.intellioms.rulesys.*;
import com.fitechlabs.fin.intellioms.util.*;
import com.fitechlabs.xtier.services.objpool.ObjectPoolException;
import com.fitechlabs.xtier.utils.ArgAssert;
import java.sql.SQLException;
import java.util.*;

// Referenced classes of package com.com.fin.intellioms.simple:
//            SimpleCondOrder

public class SimpleOrderRule
    implements OrderRule
{

    public SimpleOrderRule(OmsAdaptor omsAdapter, PersistAdaptor persistAdapter)
        throws RuleEngineException
    {
        log = Log.getLogger(com/ com /fin/intellioms/simple/SimpleOrderRule);
        state = new ServiceState("SimpleOrderRule");
        mux = new Object();
        orders = new HashMap();
        pendingSubmission = new ArrayList();
        idGen = null;
        ArgAssert.nullArg(omsAdapter, "omsAdapter");
        ArgAssert.nullArg(omsAdapter, "persistAdapter");
        oms = omsAdapter;
        store = persistAdapter;
        try
        {
            idGen = new SequenceIDGenerator("rls_oms_order_id_seq", 1000L);
        }
        catch(ObjectPoolException e)
        {
            throw new RuleEngineException("Simple order rule initialization failed.", e);
        }
        catch(SQLException e)
        {
            throw new RuleEngineException("Simple order rule initialization failed.", e);
        }
    }

    public SimpleOrderRule(OmsAdaptor omsAdapter, PersistAdaptor persistAdapter, IDGenerator idGen)
    {
        log = Log.getLogger(com/ com /fin/intellioms/simple/SimpleOrderRule);
        state = new ServiceState("SimpleOrderRule");
        mux = new Object();
        orders = new HashMap();
        pendingSubmission = new ArrayList();
        this.idGen = null;
        ArgAssert.nullArg(omsAdapter, "omsAdapter");
        ArgAssert.nullArg(omsAdapter, "persistAdapter");
        oms = omsAdapter;
        store = persistAdapter;
        this.idGen = idGen;
    }

    public void execute(Event event, CondOrder ord)
        throws EventException
    {
        SimpleCondOrder simple = (SimpleCondOrder)ord;
        switch(event.getEventType().toValue())
        {
        case 5: // '\005'
            processExecEvent(event, ord);
            return;

        case 2: // '\002'
            MarketResponse rsp = (MarketResponse)event.getEventData();
            switch(rsp.getMarketResponseType().toValue())
            {
            default:
                break;

            case 1: // '\001'
            {
                if(rsp.getRejectFlag())
                {
                    Order omsOrder = simple.removeSubmittedOrder(rsp.getOrderId());
                    if(!$assertionsDisabled && omsOrder == null)
                        throw new AssertionError((new StringBuilder()).append("Unable to find matching OMS order for ").append(rsp).toString());
                    if(log.isDebug())
                        log.debug((new StringBuilder()).append("OMS rejected ").append(omsOrder).toString());
                }
                break;
            }

            case 2: // '\002'
            {
                OrderImpl omsOrder;
                if(rsp.getRejectFlag())
                {
                    if(log.isDebug())
                    {
                        omsOrder = simple.getSubmittedOrder(rsp.getOrderId());
                        log.debug((new StringBuilder()).append("OMS rejected order modification ").append(omsOrder).toString());
                    }
                    break;
                }
                omsOrder = (OrderImpl)simple.getSubmittedOrder(rsp.getOrderId());
                if(!$assertionsDisabled && omsOrder == null)
                    throw new AssertionError((new StringBuilder()).append("Unable to find matching OMS order for ").append(rsp).toString());
                if(!$assertionsDisabled && !omsOrder.getExecutedQuantity().isBelowEq(new Quantity(rsp.getQuantity())))
                    throw new AssertionError((new StringBuilder()).append("Executed quantity must be below original quantity ").append(omsOrder).append(" ").append(rsp).toString());
                omsOrder.setOriginalQuantity(new Quantity(rsp.getQuantity()));
                store.updateQuantity(omsOrder);
                break;
            }

            case 3: // '\003'
            {
                OrderImpl omsOrder = (OrderImpl)simple.getSubmittedOrder(rsp.getOrderId());
                if(!$assertionsDisabled && omsOrder == null)
                    throw new AssertionError((new StringBuilder()).append("Unable to find matching OMS order for ").append(rsp).toString());
                if(!rsp.getRejectFlag())
                {
                    omsOrder.setOriginalQuantity(omsOrder.getExecutedQuantity());
                    synchronized(mux)
                    {
                        orders.remove(omsOrder.getOrderId());
                    }
                    store.updateQuantity(omsOrder);
                    break;
                }
                if(log.isDebug())
                    log.debug((new StringBuilder()).append("OMS rejected cancel for ").append(omsOrder).toString());
                break;
            }
            }
            return;
        }
    }

    private void processExecEvent(Event event, CondOrder ord)
        throws EventException
    {
        MarketResponse rsp = (MarketResponse)event.getEventData();
        if(log.isDebug())
            log.debug((new StringBuilder()).append("Processing order execution ").append(rsp).append(" for ").append(ord).toString());
        SimpleCondOrder simple = (SimpleCondOrder)ord;
        OrderImpl omsOrd = (OrderImpl)simple.getSubmittedOrder(rsp.getOrderId());
        if(omsOrd != null)
        {
            try
            {
                TxManager.begin();
                Quantity execQty = rsp.getQuantity();
                omsOrd.execute(execQty);
                OrderImpl template = (OrderImpl)simple.getOmsOrderTemplate();
                template.execute(execQty);
                if(simple.getOmsOrderTemplate().getOriginalQuantity().isBelow(simple.getExecutedQty()))
                    throw new EventException((new StringBuilder()).append("Executed qty is above original quantity for ").append(simple).append(" : ").append(simple.getExecutedQty()).toString());
                Ratio execRatio = simple.getExecutedRatio();
                if(simple.getActivatedRatio().isBelow(execRatio))
                {
                    if(log.isDebug())
                        log.debug((new StringBuilder()).append("Activated ratio is below executed ratio ").append(simple).toString());
                    simple.applyActivatedRatio(new Ratio(execRatio));
                }
                ord.processExecution(ord, ord.getExecutedRatio());
                if(omsOrd.getQuantity().equals(Quantity.ZERO))
                    synchronized(mux)
                    {
                        orders.remove(omsOrd.getOrderId());
                    }
                if(simple.getState() != CondOrderState.CANCELED)
                {
                    if(simple.getExecutedQty().equals(simple.getOmsOrderTemplate().getOriginalQuantity()))
                        ctx.markCompleted(ord);
                } else
                if(log.isDebug())
                    log.debug((new StringBuilder()).append("Execution will not be propagated to parent: already canceled ").append(simple).toString());
                TxManager.commit();
            }
            catch(EventException e)
            {
                try
                {
                    TxManager.rollback();
                }
                catch(TxManagerException e1)
                {
                    if(log.isError())
                        log.error((new StringBuilder()).append("Unable to rollback transaction while executing ").append(ord).append(": ").append(e1.getMessage()).toString(), e1);
                }
                throw e;
            }
            catch(CondOrderException e)
            {
                try
                {
                    TxManager.rollback();
                }
                catch(TxManagerException e1)
                {
                    if(log.isError())
                        log.error((new StringBuilder()).append("Unable to rollback transaction while executing ").append(ord).append(": ").append(e1.getMessage()).toString(), e1);
                }
                throw new EventException(e.getMessage(), e);
            }
            catch(TxManagerException e)
            {
                try
                {
                    TxManager.rollback();
                }
                catch(TxManagerException e1)
                {
                    if(log.isError())
                        log.error((new StringBuilder()).append("Unable to rollback transaction while executing ").append(ord).append(": ").append(e1.getMessage()).toString(), e1);
                }
                throw new EventException(e.getMessage(), e);
            }
            if(log.isDebug())
                log.debug((new StringBuilder()).append("Done processing order execution ").append(rsp).append(" for ").append(ord).toString());
        } else
        if(log.isWarn())
            log.warn((new StringBuilder()).append("Received execution event for unknown order ").append(event).toString());
    }

    public void modifyOrder(CondOrder oldOrd, CondOrder newOrd)
        throws CondOrderException
    {
        SimpleCondOrder oldSimple = (SimpleCondOrder)oldOrd;
        SimpleCondOrder newSimple = (SimpleCondOrder)newOrd;
        Quantity oldQty = oldSimple.getOmsOrderTemplate().getOriginalQuantity();
        Quantity newQty = newSimple.getOmsOrderTemplate().getOriginalQuantity();
        Order oldOmsOrd = oldSimple.getOmsOrderTemplate();
        Order newOmsOrd = newSimple.getOmsOrderTemplate();
        oldOmsOrd.setExecType(newOmsOrd.getExecType());
        oldOmsOrd.setPrice(newOmsOrd.getPrice());
        oldOmsOrd.setOriginalQuantity(new Quantity(newOmsOrd.getOriginalQuantity()));
        oldOmsOrd.setMemo(newOmsOrd.getMemo());
        if(oldSimple.getState() == CondOrderState.ACTIVATED && !oldQty.equals(newQty))
        {
            Quantity newSbm = new Quantity(newQty);
            newSbm.multiply(oldSimple.getActivatedRatio());
            Quantity oldSbm = new Quantity(oldSimple.getSubmittedQuantity());
            if(newSbm.isAbove(oldSbm))
                generateAndSubmitNewOmsOrder(oldSimple);
            else
            if(newSbm.isBelow(oldSbm))
            {
                oldSbm.subtruct(newSbm);
                decreaseSubmitedQuantity(oldSbm, oldSimple);
            }
        }
    }

    void modify(SimpleCondOrder simple, Ratio oldActivatedRatio)
        throws CondOrderException
    {
        if(oldActivatedRatio.isBelow(simple.getActivatedRatio()))
            generateAndSubmitNewOmsOrder(simple);
        else
        if(oldActivatedRatio.isAbove(simple.getActivatedRatio()))
        {
            Quantity newQty = new Quantity(simple.getOmsOrderTemplate().getOriginalQuantity());
            newQty.multiply(simple.getActivatedRatio());
            if(newQty.isBelow(simple.getExecutedQty()))
                newQty = new Quantity(simple.getExecutedQty());
            Quantity diff = simple.getSubmittedQuantity();
            diff.subtruct(newQty);
            if(!diff.isAboveEq(Quantity.ZERO))
                diff.negate();
            decreaseSubmitedQuantity(diff, simple);
        }
    }

    private void generateAndSubmitNewOmsOrder(SimpleCondOrder simple)
        throws CondOrderException
    {
        Order omsOrd = generateOmsOrder(simple);
        if(omsOrd != null)
        {
            synchronized(mux)
            {
                orders.put(omsOrd.getOrderId(), simple);
            }
            try
            {
                if(state.isStarted())
                    oms.submitOrder(omsOrd);
                else
                    synchronized(pendingSubmission)
                    {
                        pendingSubmission.add(omsOrd);
                    }
                store.save(omsOrd);
                simple.getSubmitedOrders().add(omsOrd);
            }
            catch(OmsAdaptorException e)
            {
                throw new CondOrderException("Unable to submit order to OMS.", e, simple);
            }
            catch(PersistException e)
            {
                throw new CondOrderException("Unable to store submitted order.", e, simple);
            }
        }
    }

    private void decreaseSubmitedQuantity(Quantity decrQty, SimpleCondOrder simple)
        throws CondOrderException
    {
        if(log.isDebug())
            log.debug((new StringBuilder()).append("Decreasing submitted quantity on ").append(decrQty).append(" for ").append(simple).toString());
        List ords = simple.getSubmitedOrders();
        for(int i = ords.size() - 1; i >= 0 && decrQty.isAbove(Quantity.ZERO); i--)
        {
            Order ord = (Order)ords.get(i);
            if(ord.getQuantity().equals(Quantity.ZERO))
                continue;
            if(decrQty.equals(Quantity.ZERO))
                break;
            if(ord.getQuantity().isBelowEq(decrQty))
            {
                decrQty.subtruct(ord.getQuantity());
                try
                {
                    oms.cancelOrder(ord);
                }
                catch(OmsAdaptorException e)
                {
                    throw new CondOrderException((new StringBuilder()).append("Unable to cancel OMS order ").append(ord).toString(), e, simple);
                }
                continue;
            }
            if(!ord.getQuantity().isAbove(decrQty))
                continue;
            Order modifOrd = new OrderImpl(ord);
            Quantity newQty = (Quantity)(new Quantity(ord.getOriginalQuantity())).subtruct(decrQty);
            modifOrd.setOriginalQuantity(newQty);
            try
            {
                oms.modifyOrder(modifOrd);
            }
            catch(OmsAdaptorException e)
            {
                throw new CondOrderException((new StringBuilder()).append("Unable to modify OMS order from ").append(ord).append(" to ").append(modifOrd).toString(), e, simple);
            }
            break;
        }

    }

    public List findMatchingOrders(Event event)
    {
        event.getEventType().toValue();
        JVM INSTR lookupswitch 2: default 76
    //                   2: 56
    //                   5: 36;
           goto _L1 _L2 _L3
_L3:
        MarketResponse rsp = (MarketResponse)event.getEventData();
        OrderId orderId = rsp.getOrderId();
          goto _L4
_L2:
        rsp = (MarketResponse)event.getEventData();
        orderId = rsp.getOrderId();
          goto _L4
_L1:
        return null;
_L4:
        rsp = ((MarketResponse) (mux));
        JVM INSTR monitorenter ;
        CondOrder ord = (CondOrder)orders.get(orderId);
        if(ord != null)
        {
            List result = new ArrayList(1);
            result.add(ord);
            return result;
        }
        rsp;
        JVM INSTR monitorexit ;
          goto _L5
        Exception exception;
        exception;
        throw exception;
_L5:
        return null;
    }

    public void matchAndExecute(Event event)
        throws EventException
    {
        List ords = findMatchingOrders(event);
        if(ords != null)
        {
            for(int i = 0; i < ords.size(); i++)
            {
                CondOrder ord = (CondOrder)ords.get(i);
                synchronized(ctx.getMutex(ord))
                {
                    execute(event, ord);
                }
            }

        }
    }

    public void registerOrder(CondOrder ord)
        throws CondOrderException
    {
        if(ord.getActivatedRatio().equals(Ratio.NONE))
        {
            if(log.isDebug())
                log.debug((new StringBuilder()).append("Skiping order with zero activated ratio: ").append(ord).toString());
            return;
        } else
        {
            SimpleCondOrder simple = (SimpleCondOrder)ord;
            simple.setRule(this);
            generateAndSubmitNewOmsOrder(simple);
            return;
        }
    }

    private Order generateOmsOrder(SimpleCondOrder simple)
        throws CondOrderException
    {
        Order tmpl = simple.getOmsOrderTemplate();
        Quantity qty = new Quantity(tmpl.getOriginalQuantity());
        qty.multiply(simple.getActivatedRatio());
        qty.subtruct(simple.getSubmittedQuantity());
        if(qty.isBelowEq(Quantity.ZERO))
            return null;
        Quantity origQty = new Quantity(qty);
        Price price = tmpl.getPrice() == null ? null : new Price(tmpl.getPrice());
        OrderImpl newOmsOrd;
        try
        {
            newOmsOrd = new OrderImpl(new OrderId(idGen.next(), tmpl.getOrderType()), tmpl.getAccountId(), tmpl.getSide(), price, qty, tmpl.getTicker(), tmpl.getExecType(), new Quantity(0.0D), origQty);
            newOmsOrd.setInitialOrderId(tmpl.getOrderId());
            newOmsOrd.setMemo(tmpl.getMemo());
        }
        catch(IDGeneratorException e)
        {
            throw new CondOrderException("Error generating new identifier for OMS order.", simple);
        }
        return newOmsOrd;
    }

    public void unregisterOrder(CondOrder ord)
        throws CondOrderException
    {
        SimpleCondOrder simple = (SimpleCondOrder)ord;
        if(simple.getSubmitedOrders().size() > 0)
            try
            {
                if(log.isDebug())
                    log.debug((new StringBuilder()).append("Sending cancels to OMS for ").append(ord).toString());
                Iterator iter = simple.getSubmitedOrders().iterator();
                do
                {
                    if(!iter.hasNext())
                        break;
                    Order omsOrd = (Order)iter.next();
                    if(omsOrd.getQuantity().isAbove(Quantity.ZERO))
                        oms.cancelOrder(omsOrd);
                } while(true);
            }
            catch(OmsAdaptorException e)
            {
                throw new CondOrderException(e.getMessage(), e, simple);
            }
    }

    public void onRegister(RuleContext ctx)
    {
        this.ctx = ctx;
    }

    public String toString()
    {
        return "SimpleOrderRule";
    }

    public void start()
        throws InitializationException
    {
        state.start();
        synchronized(pendingSubmission)
        {
            if(pendingSubmission.size() > 0)
            {
                for(int i = 0; i < pendingSubmission.size(); i++)
                    try
                    {
                        Order ord = (Order)pendingSubmission.get(i);
                        SimpleCondOrder simple = (SimpleCondOrder)orders.get(ord.getOrderId());
                        if(!$assertionsDisabled && simple == null)
                            throw new AssertionError((new StringBuilder()).append("Unable to find simple conditional order for ").append(ord).toString());
                        oms.submitOrder(ord);
                    }
                    catch(OmsAdaptorException e)
                    {
                        throw new InitializationException(e.getMessage(), e);
                    }

                pendingSubmission.clear();
            }
        }
    }

    public void stop()
    {
        state.stop();
        pendingSubmission.clear();
        synchronized(mux)
        {
            orders.clear();
        }
    }

    public void validate(CondOrder ord)
        throws CondOrderValidationException
    {
        SimpleCondOrder simple = (SimpleCondOrder)ord;
        Order omsOrd = simple.getOmsOrderTemplate();
        if(omsOrd == null)
            throw new CondOrderValidationException("Missing OMS order.", ord);
        if(omsOrd.getQuantity() == null || omsOrd.getQuantity().isBelowEq(Quantity.ZERO))
            throw new CondOrderValidationException("Illegal or zero quantity.", ord);
        if(omsOrd.getOriginalQuantity() == null || omsOrd.getOriginalQuantity().isBelow(Quantity.ZERO))
            throw new CondOrderValidationException("Illeagal or zero original quantity.", ord);
        if(!omsOrd.getOriginalQuantity().equals(omsOrd.getQuantity()))
            throw new CondOrderValidationException("Quantity must have same value as original quantity.", ord);
        if(omsOrd.getAccountId() != ord.getAccountId())
            throw new CondOrderValidationException("Account ID missmatch between OMS order and conditional order.", ord);
        else
            return;
    }

    public List getRegisteredOrders()
    {
        Object obj = mux;
        JVM INSTR monitorenter ;
        return new ArrayList(orders.values());
        Exception exception;
        exception;
        throw exception;
    }

    public PersistAdaptor getStore()
    {
        return store;
    }

    RuleContext getContext()
    {
        return ctx;
    }

    public void load(CondOrder ord)
    {
        SimpleCondOrder simple = (SimpleCondOrder)ord;
        synchronized(mux)
        {
            OrderImpl omsOrd;
            for(Iterator iter = simple.getSubmitedOrders().iterator(); iter.hasNext(); orders.put(omsOrd.getOrderId(), simple))
            {
                omsOrd = (OrderImpl)iter.next();
                omsOrd.setInitialOrderId(simple.getOmsOrderTemplate().getOrderId());
            }

        }
        simple.setRule(this);
    }

    public void unload(CondOrder ord)
    {
        SimpleCondOrder simple = (SimpleCondOrder)ord;
        if(simple.getSubmitedOrders().size() > 0)
        {
            for(Iterator iter = simple.getSubmitedOrders().iterator(); iter.hasNext();)
            {
                OrderId omsOrdId = ((Order)iter.next()).getOrderId();
                synchronized(mux)
                {
                    orders.remove(omsOrdId);
                }
            }

        }
    }

    public SimpleCondOrder getCondOrderByOmsOrder(long omsOrderId, int omsOrderType)
    {
        Object obj = mux;
        JVM INSTR monitorenter ;
        return (SimpleCondOrder)orders.get(new OrderId(omsOrderId, OrderType.getEnum(omsOrderType)));
        Exception exception;
        exception;
        throw exception;
    }

    private Log log;
    private final OmsAdaptor oms;
    private final PersistAdaptor store;
    private RuleContext ctx;
    private final ServiceState state;
    private Object mux;
    private Map orders;
    private List pendingSubmission;
    private IDGenerator idGen;
    static final boolean $assertionsDisabled = !com/ com /fin/intellioms/simple/SimpleOrderRule.desiredAssertionStatus();

}
