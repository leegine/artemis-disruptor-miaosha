// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   ExclusiveOrderRule.java

package co.fin.intellioms.exclusive;

import co.fin.intellioms.util.Log;
import co.fin.intellioms.util.ServiceState;
import co.fin.intellioms.enums.CondOrderState;
import co.fin.intellioms.enums.CondOrderType;
import co.fin.intellioms.event.Event;
import com.fitechlabs.fin.intellioms.rulesys.*;

import java.util.Iterator;
import java.util.List;

// Referenced classes of package com.com.fin.intellioms.exclusive:
//            ExclusiveCondOrder

public class ExclusiveOrderRule
    implements OrderRule
{

    public ExclusiveOrderRule()
    {
    }

    public void execute(Event event1, CondOrder condorder)
    {
    }

    public List findMatchingOrders(Event event)
    {
        return null;
    }

    public void matchAndExecute(Event event1)
    {
    }

    void finishOrder(ExclusiveCondOrder ord)
        throws CondOrderException
    {
        ctx.markCompleted(ord);
    }

    public void registerOrder(CondOrder ord)
        throws CondOrderException
    {
        if(!$assertionsDisabled && ord.getType() != CondOrderType.EXCLUSIVE)
            throw new AssertionError((new StringBuilder()).append("Conditional order type missmatch: ").append(ord).toString());
        ExclusiveCondOrder exclOrd = (ExclusiveCondOrder)ord;
        load(exclOrd);
        try
        {
            CondOrder subOrd;
            for(Iterator iter = exclOrd.getSubOrders().iterator(); iter.hasNext(); ctx.activateOrder(subOrd))
                subOrd = (CondOrder)iter.next();

        }
        catch(CondOrderExpiredException e)
        {
            if(log.isDebug())
                log.debug(e.getMessage());
        }
        catch(CondOrderException e)
        {
            throw new CondOrderException(e.getMessage(), e, ord);
        }
    }

    private void doLoad(ExclusiveCondOrder ord)
    {
        ord.setRule(this);
    }

    public void unregisterOrder(CondOrder condorder)
    {
    }

    public String toString()
    {
        return "ExclusiveOrderRule";
    }

    public void onRegister(RuleContext ctx)
    {
        this.ctx = ctx;
    }

    public void modifyOrder(CondOrder oldOrd, CondOrder newOrd)
        throws CondOrderException
    {
        if(!$assertionsDisabled && !oldOrd.getType().equals(CondOrderType.EXCLUSIVE))
            throw new AssertionError();
        if(!$assertionsDisabled && !newOrd.getType().equals(CondOrderType.EXCLUSIVE))
            throw new AssertionError();
        ExclusiveCondOrder ord = (ExclusiveCondOrder)oldOrd;
        ord.update();
        if(oldOrd.getState() == CondOrderState.ACTIVATED)
        {
            List subOrders = ord.getSubOrders();
            for(int i = 0; i < subOrders.size(); i++)
            {
                CondOrder subOrd = (CondOrder)subOrders.get(i);
                if(subOrd.getState() == CondOrderState.PENDING)
                    ctx.activateOrder(subOrd);
            }

        }
    }

    public void start()
    {
        state.start();
    }

    public void stop()
    {
        state.stop();
    }

    public void validate(CondOrder condorder)
    {
    }

    RuleContext getRuleContext()
    {
        return ctx;
    }

    public void load(CondOrder ord)
    {
        doLoad((ExclusiveCondOrder)ord);
    }

    public void unload(CondOrder condorder)
    {
    }

    private static final Log log = Log.getLogger(com/ com /fin/intellioms/exclusive/ExclusiveOrderRule);
    private RuleContext ctx;
    private final ServiceState state = new ServiceState("ExclusiveOrderRule");
    static final boolean $assertionsDisabled = !com/ com /fin/intellioms/exclusive/ExclusiveOrderRule.desiredAssertionStatus();

}
