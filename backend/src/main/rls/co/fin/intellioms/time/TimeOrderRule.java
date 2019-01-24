// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   TimeOrderRule.java

package co.fin.intellioms.time;

import co.fin.intellioms.util.Log;
import co.fin.intellioms.util.ServiceState;
import com.fitechlabs.fin.intellioms.enums.*;
import co.fin.intellioms.event.Event;
import co.fin.intellioms.event.EventException;
import com.fitechlabs.fin.intellioms.rulesys.*;
import co.fin.intellioms.tx.TxManager;
import co.fin.intellioms.tx.TxManagerException;

import java.util.*;

// Referenced classes of package com.com.fin.intellioms.time:
//            TimeCondOrder, TimerAdaptor

public class TimeOrderRule
    implements OrderRule
{

    public TimeOrderRule(TimerAdaptor adaptor)
    {
        log = Log.getLogger(com/ com /fin/intellioms/time/TimeOrderRule);
        timerAdaptor = adaptor;
        ordersByTime = new TreeMap();
    }

    public void execute(Event event, CondOrder ord)
        throws EventException
    {
        if(log.isDebug())
            log.debug((new StringBuilder()).append("Executing ").append(ord).append(" on ").append(event).toString());
        if(event.getEventType() == EventType.TIMER && ord.getType().equals(CondOrderType.TIME))
            try
            {
                TxManager.begin();
                Iterator iter = ord.getSubOrders().iterator();
                do
                {
                    if(!iter.hasNext())
                        break;
                    CondOrder child = (CondOrder)iter.next();
                    if(child.getState() == CondOrderState.PENDING)
                        ctx.activateOrder(child);
                } while(true);
                ctx.markCompleted(ord);
                TxManager.commit();
            }
            catch(CondOrderExpiredException e)
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
                if(log.isDebug())
                    log.debug(e.getMessage());
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
            log.debug((new StringBuilder()).append("Finished ").append(ord).toString());
    }

    public List findMatchingOrders(Event event)
    {
        if(event.getEventType() == EventType.TIMER)
        {
            Date time = (Date)event.getEventData();
            if(log.isDebug())
                log.debug((new StringBuilder()).append("Finding matching order for time event ").append(time).toString());
            if(time != null)
            {
                List matches = new ArrayList();
                synchronized(ordersByTime)
                {
                    Iterator iter = ordersByTime.entrySet().iterator();
                    do
                    {
                        if(!iter.hasNext())
                            break;
                        Map.Entry entry = (Map.Entry)iter.next();
                        Date ordTime = (Date)entry.getKey();
                        if(ordTime.compareTo(time) > 0)
                            break;
                        matches.addAll((List)entry.getValue());
                    } while(true);
                }
                if(log.isDebug())
                    if(matches != null)
                        log.debug((new StringBuilder()).append("Found ").append(matches.size()).append(" matches.").toString());
                    else
                        log.debug("Matches not found.");
                return matches;
            }
        }
        return null;
    }

    public void matchAndExecute(Event event)
        throws EventException
    {
        List ords = findMatchingOrders(event);
        if(ords != null)
        {
            EventException errors = null;
            for(int i = 0; i < ords.size(); i++)
            {
                CondOrder ord = (CondOrder)ords.get(i);
                try
                {
                    synchronized(ctx.getMutex(ord))
                    {
                        if(ord.getState() == CondOrderState.ACTIVATED)
                            execute(event, ord);
                    }
                    continue;
                }
                catch(EventException e)
                {
                    if(errors == null)
                        errors = new EventException((new StringBuilder()).append("Error(s) while processing timer event ").append(event).toString(), e);
                    else
                        errors.addCause(e);
                }
            }

            if(errors != null)
                throw errors;
        }
    }

    public void registerOrder(CondOrder ord)
        throws CondOrderException
    {
        if(log.isDebug())
            log.debug((new StringBuilder()).append("Registering ").append(ord).toString());
        if(!$assertionsDisabled && ord.getType() != CondOrderType.TIME)
        {
            throw new AssertionError((new StringBuilder()).append("Unexpected order type: ").append(ord).toString());
        } else
        {
            doLoad(ord);
            return;
        }
    }

    private void doLoad(CondOrder ord)
        throws CondOrderException
    {
        Date time = (Date)ord.getParam();
        synchronized(ordersByTime)
        {
            Collection orders = (Collection)ordersByTime.get(time);
            if(orders == null)
            {
                orders = new ArrayList();
                ordersByTime.put(time, orders);
            }
            orders.add(ord);
            try
            {
                timerAdaptor.addTask(time);
            }
            catch(EventException e)
            {
                throw new CondOrderException(e.getMessage(), e, ord);
            }
        }
    }

    public void unregisterOrder(CondOrder ord)
    {
        unload(ord);
    }

    public String toString()
    {
        return "TimeOrderRule";
    }

    public void onRegister(RuleContext ctx)
    {
        this.ctx = ctx;
    }

    public void modifyOrder(CondOrder oldOrd, CondOrder newOrd)
        throws CondOrderException
    {
        if(!$assertionsDisabled && !oldOrd.getType().equals(CondOrderType.TIME))
            throw new AssertionError();
        if(!$assertionsDisabled && !newOrd.getType().equals(CondOrderType.TIME))
            throw new AssertionError();
        TimeCondOrder oldTimeOrd = (TimeCondOrder)oldOrd;
        TimeCondOrder newTimeOrd = (TimeCondOrder)newOrd;
        if(oldOrd.getState() == CondOrderState.ACTIVATED)
            unload(oldOrd);
        oldTimeOrd.setParam(newTimeOrd.getParam());
        if(oldOrd.getState() == CondOrderState.ACTIVATED)
            doLoad(oldOrd);
    }

    public void start()
    {
        state.start();
    }

    public void stop()
    {
        state.stop();
        synchronized(ordersByTime)
        {
            ordersByTime.clear();
        }
    }

    public void validate(CondOrder ord)
        throws CondOrderValidationException
    {
        TimeCondOrder timeOrd = (TimeCondOrder)ord;
        if(!ord.hasSubOrders())
            throw new CondOrderValidationException("Time order must have suborder.", ord);
        if(ord.getSubOrders().size() > 1)
            throw new CondOrderValidationException("Time order can have only one suborder.", ord);
        if(timeOrd.getFireTime() == null)
            throw new CondOrderValidationException("Missing fire time condition.", ord);
        else
            return;
    }

    public void load(CondOrder ord)
        throws CondOrderException
    {
        doLoad(ord);
    }

    public void unload(CondOrder ord)
    {
        Date time = (Date)ord.getParam();
        synchronized(ordersByTime)
        {
            Collection orders = (Collection)ordersByTime.get(time);
            if(orders != null)
            {
                orders.remove(ord);
                if(orders.size() == 0)
                    ordersByTime.remove(time);
            }
        }
    }

    private Log log;
    private SortedMap ordersByTime;
    private RuleContext ctx;
    private final TimerAdaptor timerAdaptor;
    private final ServiceState state = new ServiceState("TimeOrderRule");
    static final boolean $assertionsDisabled = !com/ com /fin/intellioms/time/TimeOrderRule.desiredAssertionStatus();

}
