// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   ChainOrderRule.java

package co.fin.intellioms.chain;

import co.fin.intellioms.enums.CondOrderState;
import co.fin.intellioms.enums.CondOrderType;
import co.fin.intellioms.event.Event;
import co.fin.intellioms.rulesys.*;
import co.fin.intellioms.util.Log;
import co.fin.intellioms.util.ServiceState;

import java.util.List;

// Referenced classes of package com.com.fin.intellioms.chain:
//            ChainCondOrder

public class ChainOrderRule
    implements OrderRule
{

    public ChainOrderRule()
    {
        log = Log.getLogger(ChainOrderRule.class);
    }

    public void registerOrder(CondOrder ord)
        throws CondOrderException
    {
        if(ord.getType() != CondOrderType.CHAIN)
            throw new AssertionError((new StringBuilder()).append("Conditional order type mismatch. Expected CHAIN but got: ").append(ord.getType()).toString());
        ChainCondOrder chainOrd = (ChainCondOrder)ord;
        if(!chainOrd.hasSubOrders())
            throw new CondOrderException("Chain order must have at least one suborder.", ord);
        doLoad(chainOrd);
        CondOrder firstChainElement = chainOrd.getNextChainElement();
        if( firstChainElement == null)
            throw new AssertionError((new StringBuilder()).append("Unable to find next chain element: ").append(chainOrd).toString());
        try
        {
            ctx.activateOrder(firstChainElement);
        }
        catch(CondOrderExpiredException e)
        {
            if(log.isDebug())
                log.debug(e.getMessage());
        }
    }

    private void doLoad(ChainCondOrder chainOrd)
    {
        chainOrd.setRule(this);
    }

    void finishChain(ChainCondOrder chain)
        throws CondOrderException
    {
        if(log.isDebug())
            log.debug((new StringBuilder()).append("Finishing chain: ").append(chain).toString());
        ctx.markCompleted(chain);
    }

    void switchChain(ChainCondOrder chain)
        throws CondOrderException
    {
        CondOrder nextElmt = chain.getNextChainElement();
        if(log.isDebug())
            log.debug((new StringBuilder()).append("Switching chain element [next=").append(nextElmt).append(", chain=").append(chain).append(']').toString());
        if(nextElmt == null)
            throw new CondOrderException("Unable to swithch chain element: Chain reached its end.", chain);
        try
        {
            ctx.activateOrder(nextElmt);
        }
        catch(CondOrderExpiredException e)
        {
            if(log.isDebug())
                log.debug(e.getMessage());
        }
    }

    public List findMatchingOrders(Event event)
    {
        return null;
    }

    public void matchAndExecute(Event event1)
    {
    }

    public void execute(Event event1, CondOrder condorder)
    {
    }

    public void unregisterOrder(CondOrder condorder)
    {
    }

    public void onRegister(RuleContext ctx)
    {
        this.ctx = ctx;
    }

    public String toString()
    {
        return "ChainOrderRule";
    }

    public void modifyOrder(CondOrder oldOrd, CondOrder newOrd)
        throws CondOrderException
    {
        if(!oldOrd.getType().equals(CondOrderType.CHAIN))
            throw new AssertionError();
        if(!newOrd.getType().equals(CondOrderType.CHAIN))
            throw new AssertionError();
        if(oldOrd.getState() == CondOrderState.ACTIVATED)
            ((ChainCondOrder)oldOrd).update();
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

    public void load(CondOrder ord)
    {
        doLoad((ChainCondOrder)ord);
    }

    public void unload(CondOrder condorder)
    {
    }

    private Log log;
    private RuleContext ctx;
    private final ServiceState state = new ServiceState("ChainOrderRule");

}
