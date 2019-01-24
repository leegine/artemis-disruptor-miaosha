// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   PriceOrderRule.java

package co.fin.intellioms.price;

import co.fin.intellioms.omsclt.Price;
import co.fin.intellioms.quote.Quote;
import co.fin.intellioms.tx.TxManager;
import co.fin.intellioms.tx.TxManagerException;
import com.fitechlabs.fin.intellioms.enums.*;
import co.fin.intellioms.event.Event;
import co.fin.intellioms.event.EventException;
import com.fitechlabs.fin.intellioms.rulesys.*;
import com.fitechlabs.fin.intellioms.ticker.*;
import com.fitechlabs.fin.intellioms.util.*;
import com.fitechlabs.xtier.utils.boxed.sync.BoxedInt64Sync;
import java.util.*;

// Referenced classes of package com.com.fin.intellioms.price:
//            PriceCluster, PriceCondOrder, PriceCondition

public class PriceOrderRule
    implements OrderRule
{

    public PriceOrderRule(TickersManager tickersProvider)
    {
        this(tickersProvider, true);
    }

    public PriceOrderRule(TickersManager tickersProvider, boolean matchOnRegistration)
    {
        log = Log.getLogger(com/ com /fin/intellioms/price/PriceOrderRule);
        ordersByTicker = new HashMap();
        state = new ServiceState("PriceOrderRule");
        this.matchOnRegistration = true;
        matchesCounter = new BoxedInt64Sync(0L);
        this.tickersProvider = tickersProvider;
        this.matchOnRegistration = matchOnRegistration;
    }

    private void loadTickers()
        throws InitializationException
    {
        List tickers = null;
        try
        {
            tickers = tickersProvider.getAllTickers();
        }
        catch(TickersManagerException e)
        {
            throw new InitializationException("Unable to load tickers information.", e);
        }
        for(int i = 0; i < tickers.size(); i++)
            ordersByTicker.put(tickers.get(i), new PriceCluster());

        if(log.isDebug())
            log.debug((new StringBuilder()).append("Loaded ").append(tickers.size()).append(" tickers.").toString());
    }

    public void execute(Event event, CondOrder ord)
        throws EventException
    {
        if(event.getEventType() == EventType.QUOTE)
        {
            Quote qt = (Quote)event.getEventData();
            PriceCondOrder priceOrd = (PriceCondOrder)ord;
            if(qt != null)
            {
                Ticker key = priceOrd.getPriceCondition().getTicker();
                PriceCluster prices = (PriceCluster)ordersByTicker.get(key);
                if(prices != null)
                    doExecute(ord);
            }
        }
        if(log.isDebug())
            log.debug((new StringBuilder()).append("Finished ").append(ord).toString());
    }

    private void doExecute(CondOrder ord)
        throws EventException
    {
        matchesCounter.postIncr();
        if(log.isDebug())
            log.debug((new StringBuilder()).append("Executing conditional price order: ").append(ord).toString());
        try
        {
            TxManager.begin();
            CondOrder child;
            for(Iterator iter = ord.getSubOrders().iterator(); iter.hasNext(); ctx.activateOrder(child))
                child = (CondOrder)iter.next();

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
    }

    public List findMatchingOrders(Event event)
    {
        if(event.getEventType() == EventType.QUOTE)
        {
            Quote qt = (Quote)event.getEventData();
            if(qt != null)
            {
                List matches = null;
                PriceCluster prices = (PriceCluster)ordersByTicker.get(qt.getTicker());
                if(prices != null)
                    matches = prices.findMatches(qt);
                else
                if(log.isWarn())
                    log.warn((new StringBuilder()).append("Received quote event with unknown ticker: ").append(qt).toString());
                if(log.isDebug() && matches != null)
                {
                    if(!$assertionsDisabled && matches.size() <= 0)
                        throw new AssertionError();
                    log.debug((new StringBuilder()).append("Found ").append(matches.size()).append(" matches.").toString());
                }
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
                        errors = new EventException((new StringBuilder()).append("Error(s) while processing quote event ").append(event).toString(), e);
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
        if(!$assertionsDisabled && ord.getType() != CondOrderType.PRICE)
            throw new AssertionError(ord);
        PriceCluster prices = findPriceCluster(ord);
        if(matchOnRegistration && prices.isMatchesLastQuote(ord))
            try
            {
                doExecute(ord);
            }
            catch(EventException e)
            {
                throw new CondOrderException((new StringBuilder()).append("Unable execute conditional price order: ").append(e.getMessage()).toString(), e, ord);
            }
        else
            prices.addOrder(ord);
    }

    public void unregisterOrder(CondOrder ord)
    {
        unload(ord);
    }

    public String toString()
    {
        return "PriceOrderRule";
    }

    public void onRegister(RuleContext ctx)
    {
        this.ctx = ctx;
    }

    public void modifyOrder(CondOrder oldOrd, CondOrder newOrd)
        throws CondOrderException
    {
        if(!$assertionsDisabled && !oldOrd.getType().equals(CondOrderType.PRICE))
            throw new AssertionError();
        if(!$assertionsDisabled && !newOrd.getType().equals(CondOrderType.PRICE))
            throw new AssertionError();
        PriceCondOrder oldPriceOrd = (PriceCondOrder)oldOrd;
        PriceCondOrder newPriceOrd = (PriceCondOrder)newOrd;
        if(oldOrd.getState() == CondOrderState.ACTIVATED)
            unload(oldOrd);
        PriceCondition oldCond = oldPriceOrd.getPriceCondition();
        PriceCondition newCond = newPriceOrd.getPriceCondition();
        if(!oldCond.getTicker().equals(newCond.getTicker()))
            log.warn((new StringBuilder()).append("Illegal modification: Conditional price order ticker will not be changed. ").append(oldCond).toString());
        newCond = new PriceCondition(newCond.getTargetPrice(), newCond.getDirection(), oldCond.getTicker());
        oldPriceOrd.setParam(newCond);
        if(oldOrd.getState() == CondOrderState.ACTIVATED)
        {
            PriceCluster cluster = findPriceCluster(oldPriceOrd);
            if(matchOnRegistration && cluster.isMatchesLastQuote(oldPriceOrd))
                try
                {
                    doExecute(oldPriceOrd);
                }
                catch(EventException e)
                {
                    throw new CondOrderException((new StringBuilder()).append("Unable execute conditional price order: ").append(e.getMessage()).toString(), e, oldPriceOrd);
                }
            else
                cluster.addOrder(oldPriceOrd);
        }
    }

    public void start()
        throws InitializationException
    {
        loadTickers();
        state.start();
    }

    public void stop()
    {
        state.stop();
        synchronized(ordersByTicker)
        {
            ordersByTicker.clear();
        }
        matchesCounter.set(0L);
    }

    public List getRegisteredTickers()
    {
        return new ArrayList(ordersByTicker.keySet());
    }

    public void validate(CondOrder ord)
        throws CondOrderValidationException
    {
        PriceCondOrder price = (PriceCondOrder)ord;
        if(!ord.hasSubOrders())
            throw new CondOrderValidationException("Price order must have suborder.", ord);
        if(ord.getSubOrders().size() > 1)
            throw new CondOrderValidationException("Price order can have only one suborder.", ord);
        PriceCondition cnd = price.getPriceCondition();
        if(cnd == null)
            throw new CondOrderValidationException("Missing price condition.", ord);
        if(cnd.getTicker() == null)
            throw new CondOrderValidationException("Missing ticker.", ord);
        if(cnd.getTargetPrice() == null || cnd.getTargetPrice().isBelowEq(Price.ZERO))
            throw new CondOrderValidationException("Missing target price.", ord);
        if(cnd.getDirection() == null)
            throw new CondOrderValidationException("Missing target price direction.", ord);
        else
            return;
    }

    public void load(CondOrder ord)
        throws CondOrderException
    {
        PriceCluster prices = findPriceCluster(ord);
        prices.addOrder(ord);
    }

    private PriceCluster findPriceCluster(CondOrder ord)
        throws CondOrderException
    {
        PriceCondition cond = (PriceCondition)ord.getParam();
        Ticker ticker = cond.getTicker();
        PriceCluster prices = (PriceCluster)ordersByTicker.get(ticker);
        if(prices == null)
            throw new CondOrderException((new StringBuilder()).append("Unsupported ticker ").append(ticker).toString(), ord);
        else
            return prices;
    }

    public void unload(CondOrder ord)
    {
        PriceCondition cond = (PriceCondition)ord.getParam();
        Ticker ticker = cond.getTicker();
        PriceCluster prices = (PriceCluster)ordersByTicker.get(ticker);
        if(prices != null)
            prices.remove(ord);
    }

    public long getMatchesCount()
    {
        return matchesCounter.get();
    }

    private Log log;
    private final Map ordersByTicker;
    private RuleContext ctx;
    private final ServiceState state;
    private final TickersManager tickersProvider;
    private boolean matchOnRegistration;
    private BoxedInt64Sync matchesCounter;
    static final boolean $assertionsDisabled = !com/ com /fin/intellioms/price/PriceOrderRule.desiredAssertionStatus();

}
