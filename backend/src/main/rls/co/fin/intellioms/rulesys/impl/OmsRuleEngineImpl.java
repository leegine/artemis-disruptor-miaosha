// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   OmsRuleEngineImpl.java

package co.fin.intellioms.rulesys.impl;

import co.fin.intellioms.tx.TxManager;
import co.fin.intellioms.tx.TxManagerException;
import co.fin.intellioms.account.AccountsInfo;
import com.fitechlabs.fin.intellioms.enums.*;
import com.fitechlabs.fin.intellioms.event.*;
import com.fitechlabs.fin.intellioms.license.*;
import com.fitechlabs.fin.intellioms.persist.*;
import com.fitechlabs.fin.intellioms.rulesys.*;
import com.fitechlabs.fin.intellioms.util.*;
import com.fitechlabs.xtier.utils.Utils;
import com.fitechlabs.xtier.utils.boxed.sync.BoxedInt64Sync;
import com.fitechlabs.xtier.utils.concurrent.WritePrefRWLock;
import java.util.*;

// Referenced classes of package com.com.fin.intellioms.rulesys.impl:
//            CondOrderOperationImpl, DuplicatedOrderException, AbstractCondOrder

public class OmsRuleEngineImpl
    implements OmsRuleEngine, EventProcessor, Startable
{

    public OmsRuleEngineImpl(PersistAdaptor store)
    {
        log = Log.getLogger(com/ com /fin/intellioms/rulesys/OmsRuleEngine);
        modulesToOrdTypes = new HashMap();
        modulesToEventTypes = new HashMap();
        orders = new HashMap();
        registeredOrdersCount = new BoxedInt64Sync(0L);
        operationsCount = new BoxedInt64Sync(0L);
        executionsCount = new BoxedInt64Sync(0L);
        if(!$assertionsDisabled && store == null)
        {
            throw new AssertionError("Persist adaptor is null.");
        } else
        {
            this.store = store;
            return;
        }
    }

    public boolean registerRuleModule(RuleModule module)
    {
        if(!$assertionsDisabled && module == null)
            throw new AssertionError("Module is null.");
        state.checkNotStarted();
        CondOrderType ordTypes[] = module.getApplicableCondOrderTypes();
        if(!$assertionsDisabled && ordTypes == null)
            throw new AssertionError("RuleModule.getApplicableCondOrderTypes() returned null.");
        for(int i = 0; i < ordTypes.length; i++)
        {
            List modules = (List)modulesToOrdTypes.get(ordTypes[i]);
            if(modules == null)
            {
                modules = new ArrayList();
                modulesToOrdTypes.put(ordTypes[i], modules);
            }
            modules.add(module);
        }

        EventType eventTypes[] = module.getApplicableEventTypes();
        if(!$assertionsDisabled && eventTypes == null)
            throw new AssertionError("RuleModule.getApplicableEventTypes() returned null.");
        for(int i = 0; i < eventTypes.length; i++)
        {
            List mods = (List)modulesToEventTypes.get(eventTypes[i]);
            if(mods == null)
            {
                mods = new ArrayList();
                modulesToEventTypes.put(eventTypes[i], mods);
            }
            mods.add(module);
        }

        module.onRegister(this);
        if(log.isInfo())
            log.info((new StringBuilder()).append("Registered rule module ").append(module).toString());
        return true;
    }

    public boolean unregisterRuleModule(RuleModule module)
    {
        if(!$assertionsDisabled && module == null)
            throw new AssertionError("Module is null.");
        state.checkNotStarted();
        CondOrderType ordTypes[] = module.getApplicableCondOrderTypes();
        if(!$assertionsDisabled && ordTypes == null)
            throw new AssertionError("RuleModule.getApplicableCondOrderTypes() returned null.");
        for(int i = 0; i < ordTypes.length; i++)
        {
            List modules = (List)modulesToOrdTypes.get(ordTypes[i]);
            if(modules == null)
                continue;
            modules.remove(module);
            if(modules.size() == 0)
                modulesToOrdTypes.remove(ordTypes[i]);
        }

        EventType eventTypes[] = module.getApplicableEventTypes();
        if(!$assertionsDisabled && eventTypes == null)
            throw new AssertionError("RuleModule.getApplicableEventTypes() returned null.");
label0:
        for(int i = 0; i < eventTypes.length; i++)
        {
            List mods = (List)modulesToEventTypes.get(eventTypes[i]);
            if(mods == null)
                continue;
            Iterator iter = mods.iterator();
            do
            {
                do
                {
                    RuleModule m;
                    do
                    {
                        if(!iter.hasNext())
                            continue label0;
                        m = (RuleModule)iter.next();
                    } while(!m.equals(module));
                    iter.remove();
                } while(mods.size() != 0);
                modulesToEventTypes.remove(ordTypes[i]);
            } while(true);
        }

        module.onUnregister(this);
        if(log.isInfo())
            log.info((new StringBuilder()).append("Unregistered rule module ").append(module).toString());
        return true;
    }

    public List getApplicableRuleModuleForEvent(Event evt)
    {
        if(!$assertionsDisabled && evt == null)
        {
            throw new AssertionError("Event is  null.");
        } else
        {
            state.checkStarted();
            return (List)modulesToEventTypes.get(evt.getEventType());
        }
    }

    public void registerCondOrder(CondOrder ord)
        throws CondOrderOperationException
    {
        if(!$assertionsDisabled && ord == null)
        {
            throw new AssertionError("Order is null.");
        } else
        {
            state.checkStarted();
            CondOrderOperation op = new CondOrderOperationImpl(CondOrderOpType.NEW, ord, null);
            operateCondOrder(op);
            return;
        }
    }

    public void unregisterCondOrder(long condOrderId)
        throws CondOrderOperationException
    {
        state.checkStarted();
        CondOrder ord = getCondOrder(condOrderId);
        if(ord == null)
        {
            throw new CondOrderOperationException((new StringBuilder()).append("Failed to unregister order. Unknown order id '").append(condOrderId).append("'.").toString(), null, 2);
        } else
        {
            CondOrderOperation op = new CondOrderOperationImpl(CondOrderOpType.CANCEL, null, ord);
            operateCondOrder(op);
            return;
        }
    }

    public void operateCondOrder(CondOrderOperation op)
        throws CondOrderOperationException
    {
        if(!$assertionsDisabled && op == null)
            throw new AssertionError("Operation is NULL.");
        state.checkStarted();
        try
        {
            accountsLock.acquireRead();
            if(log.isDebug())
                log.debug((new StringBuilder()).append("Processing ").append(op).toString());
            TxManager.begin();
            operationsCount.postIncr();
            switch(op.getOpType().toValue())
            {
            case 1: // '\001'
            {
                CondOrder ord = op.getCondOrder();
                if(!$assertionsDisabled && ord == null)
                    throw new AssertionError();
                try
                {
                    Object ordMux = getOrderMutex(ord);
                    synchronized(ordMux)
                    {
                        validate(ord);
                        store.save(op);
                        doRegister(ord);
                    }
                }
                catch(DuplicatedOrderException e)
                {
                    throw new CondOrderOperationException(e.getMessage(), e, op, 3);
                }
                catch(CondOrderDuplicateException e)
                {
                    throw new CondOrderOperationException(e.getMessage(), e, op, 3);
                }
                catch(CondOrderException e)
                {
                    throw new CondOrderOperationException("Unable to register conditional order.", e, op, 0);
                }
                break;
            }

            case 2: // '\002'
            {
                Long id = new Long(op.getOldCondOrder().getId());
                CondOrder oldOrd = null;
                synchronized(mux)
                {
                    oldOrd = (CondOrder)orders.get(id);
                }
                if(oldOrd == null)
                    throw new CondOrderOperationException("Unknown order.", op, 2);
                Object ordMux = getOrderMutex(oldOrd);
                synchronized(ordMux)
                {
                    if(oldOrd.getState() == CondOrderState.COMPLETED || oldOrd.getState() == CondOrderState.CANCELED)
                        throw new CondOrderOperationException((new StringBuilder()).append("Too late to modify: ").append(oldOrd).toString(), op, 4);
                    if(oldOrd.getId() != op.getCondOrder().getId())
                        throw new CondOrderOperationException("ID missmatch between old and new conditional orders", op, 0);
                    store.save(new CondOrderOperationImpl(CondOrderOpType.MODIFY, op.getCondOrder(), oldOrd));
                    try
                    {
                        CondOrder newOrd = op.getCondOrder();
                        doModify(oldOrd, newOrd);
                    }
                    catch(CondOrderException e)
                    {
                        throw new CondOrderOperationException(e.getMessage(), e, op, 0);
                    }
                }
                break;
            }

            case 3: // '\003'
            {
                Long id = new Long(op.getOldCondOrder().getId());
                CondOrder oldOrd = null;
                synchronized(mux)
                {
                    oldOrd = (CondOrder)orders.get(id);
                }
                if(oldOrd == null)
                    throw new CondOrderOperationException("Unknown order.", op, 2);
                Object ordMux = getOrderMutex(oldOrd);
                synchronized(ordMux)
                {
                    if(oldOrd.getState() == CondOrderState.COMPLETED || oldOrd.getState() == CondOrderState.CANCELED)
                        throw new CondOrderOperationException((new StringBuilder()).append("Too late to modify: ").append(oldOrd).toString(), op, 4);
                    store.save(new CondOrderOperationImpl(CondOrderOpType.CANCEL, op.getCondOrder(), oldOrd));
                    try
                    {
                        doCancel(oldOrd);
                    }
                    catch(UnknownCondOrderException e)
                    {
                        throw new CondOrderOperationException(e.getMessage(), e, op, 2);
                    }
                    catch(CondOrderException e)
                    {
                        throw new CondOrderOperationException(e.getMessage(), e, op, 0);
                    }
                }
                break;
            }

            default:
            {
                throw new CondOrderOperationException("Unknown operation type.", op, 0);
            }
            }
            TxManager.commit();
        }
        catch(CondOrderOperationException e)
        {
            try
            {
                TxManager.rollback();
            }
            catch(TxManagerException e1)
            {
                if(log.isError())
                    log.error(e.getMessage(), e);
            }
            throw e;
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
                    log.error(e.getMessage(), e);
            }
            throw new CondOrderOperationException(e.getMessage(), e, op, 0);
        }
        catch(RuntimeException e)
        {
            try
            {
                TxManager.rollback();
            }
            catch(TxManagerException e1)
            {
                if(log.isError())
                    log.error(e.getMessage(), e);
            }
            throw e;
        }
        catch(PersistException e)
        {
            try
            {
                TxManager.rollback();
            }
            catch(TxManagerException e1)
            {
                if(log.isError())
                    log.error(e.getMessage(), e);
            }
            throw new CondOrderOperationException(e.getMessage(), e, op, 0);
        }
        accountsLock.releaseRead();
        break MISSING_BLOCK_LABEL_910;
        Exception exception5;
        exception5;
        accountsLock.releaseRead();
        throw exception5;
    }

    private void validate(CondOrder ord)
        throws CondOrderException
    {
        List modules = (List)modulesToOrdTypes.get(ord.getType());
        if(modules != null)
        {
            for(int i = 0; i < modules.size(); i++)
            {
                RuleModule mod = (RuleModule)modules.get(i);
                mod.validate(ord);
            }

        } else
        {
            throw new CondOrderException("Unable to register order to rule module. Unknown module", ord);
        }
        if(log.isDebug())
            log.debug((new StringBuilder()).append("Validated order ").append(ord).toString());
        if(ord.hasSubOrders())
        {
            CondOrder child;
            for(Iterator iter = ord.getSubOrders().iterator(); iter.hasNext(); validate(child))
            {
                child = (CondOrder)iter.next();
                if(child.getAccountId() != ord.getAccountId())
                    throw new CondOrderException("Sub-order must have same account ID as its parent.", child);
            }

        }
    }

    private void doCancel(CondOrder old)
        throws CondOrderException
    {
        doPersistentCancel(old, true);
    }

    private void doPersistentCancel(CondOrder ord, boolean isRootOrder)
        throws CondOrderException
    {
        ord.setState(CondOrderState.CANCELED);
        removeFromMap(new Long(ord.getId()));
        if(isRootOrder)
        {
            ord.setActivatedRatio(new Ratio(ord.getExecutedRatio()));
            try
            {
                store.cancel(ord);
            }
            catch(PersistException e)
            {
                throw new CondOrderException(e.getMessage(), e, ord);
            }
        }
        List modules = (List)modulesToOrdTypes.get(ord.getType());
        if(modules != null)
        {
            for(int i = 0; i < modules.size(); i++)
            {
                RuleModule mod = (RuleModule)modules.get(i);
                mod.unregisterOrder(ord);
            }

        } else
        if(log.isWarn())
            log.warn((new StringBuilder()).append("Unable to find rule module for ").append(ord).toString());
        if(ord.hasSubOrders())
        {
            Iterator iter = ord.getSubOrders().iterator();
            do
            {
                if(!iter.hasNext())
                    break;
                CondOrder child = (CondOrder)iter.next();
                if(child.getState() != CondOrderState.CANCELED)
                    doPersistentCancel(child, false);
            } while(true);
        }
    }

    private void doModify(CondOrder order, CondOrder newOrder)
        throws CondOrderException
    {
        if(!$assertionsDisabled && order == null)
            throw new AssertionError("Order is null.");
        if(!$assertionsDisabled && newOrder == null)
            throw new AssertionError("New order is null.");
        if(!$assertionsDisabled && !order.getType().equals(newOrder.getType()))
            throw new AssertionError("Old and new order types missmatch.");
        if(!$assertionsDisabled && order.getId() != newOrder.getId())
            throw new AssertionError("Ols and new order IDs missmatch.");
        if(!$assertionsDisabled && !Thread.holdsLock(getOrderMutex(order)))
            throw new AssertionError();
        List modules = (List)modulesToOrdTypes.get(order.getType());
        if(modules == null)
            throw new CondOrderException("Unable to find rule modules.", order);
        Map oldChildrenMap = new HashMap();
        List oldChildren = order.getSubOrders();
        if(!$assertionsDisabled && oldChildren == null)
            throw new AssertionError();
        for(int i = 0; i < oldChildren.size(); i++)
        {
            CondOrder ord = (CondOrder)oldChildren.get(i);
            oldChildrenMap.put(new Long(ord.getId()), ord);
        }

        List newChildren = newOrder.getSubOrders();
        List resultChildren = new ArrayList();
        if(!$assertionsDisabled && newChildren == null)
            throw new AssertionError();
        for(int i = 0; i < newChildren.size(); i++)
        {
            CondOrder newChild = (CondOrder)newChildren.get(i);
            if(newChild.getAccountId() != order.getAccountId())
                throw new CondOrderException("Account ID missmatch.", order);
            CondOrder oldChild = (CondOrder)oldChildrenMap.get(new Long(newChild.getId()));
            CondOrder child;
            if(oldChild == null)
            {
                child = newChild;
            } else
            {
                if(!newChild.getType().equals(oldChild.getType()))
                    throw new CondOrderException((new StringBuilder()).append("Unable to modify suborder: invalid child type [old-suborder").append(oldChild).append(", new-suborder=").append(newChild).append(']').toString(), order);
                child = oldChild;
                doModify(oldChild, newChild);
            }
            if(!$assertionsDisabled && child == null)
                throw new AssertionError();
            child.setParent(order);
            resultChildren.add(child);
            putToOrdersMap(child, false);
        }

        ((AbstractCondOrder)order).setSubOrders(resultChildren);
        for(int i = 0; i < modules.size(); i++)
        {
            RuleModule mod = (RuleModule)modules.get(i);
            mod.modifyCondOrder(order, newOrder);
        }

        for(int i = 0; i < resultChildren.size(); i++)
        {
            CondOrder child = (CondOrder)resultChildren.get(i);
            oldChildrenMap.remove(new Long(child.getId()));
        }

        if(oldChildrenMap.size() > 0)
        {
            Iterator iter = oldChildrenMap.values().iterator();
            do
            {
                if(!iter.hasNext())
                    break;
                CondOrder removed = (CondOrder)iter.next();
                if(removed.getState() == CondOrderState.PENDING || removed.getState() == CondOrderState.ACTIVATED)
                    doCancel(removed);
            } while(true);
        }
    }

    public CondOrder getCondOrder(long orderId)
    {
        state.checkStarted();
        Object obj = mux;
        JVM INSTR monitorenter ;
        return (CondOrder)orders.get(new Long(orderId));
        Exception exception;
        exception;
        throw exception;
    }

    public Map getAllOrders()
    {
        Object obj = mux;
        JVM INSTR monitorenter ;
        return new HashMap(orders);
        Exception exception;
        exception;
        throw exception;
    }

    public void processEvent(Event evt)
        throws EventException
    {
        state.checkStarted();
        accountsLock.acquireRead();
        store.save(evt);
        if(evt.getEventType() == EventType.EXECUTION)
            executionsCount.postIncr();
        List mods = (List)modulesToEventTypes.get(evt.getEventType());
        if(mods != null)
        {
            for(int i = 0; i < mods.size(); i++)
            {
                RuleModule mod = (RuleModule)mods.get(i);
                if(log.isDebug() && evt.getEventType() != EventType.QUOTE)
                    log.debug((new StringBuilder()).append("Matching event ").append(evt).append(" to rule module ").append(mod.name()).toString());
                mod.matchAndExecute(evt);
            }

        }
        accountsLock.releaseRead();
        break MISSING_BLOCK_LABEL_191;
        Exception exception;
        exception;
        accountsLock.releaseRead();
        throw exception;
    }

    public EventType[] getEventTypes()
    {
        Set types = modulesToEventTypes.keySet();
        return (EventType[])(EventType[])types.toArray(new EventType[types.size()]);
    }

    public void process(Event evt)
        throws EventException
    {
        processEvent(evt);
    }

    public void registerAccounts(AccountsInfo accInfo)
        throws RuleEngineException
    {
        if(log.isInfo())
            log.info((new StringBuilder()).append("Updating accounts info ").append(accInfo).append('.').toString());
        try
        {
            accountsLock.acquireWrite();
            synchronized(mux)
            {
                TxManager.begin();
                Iterator iter = orders.values().iterator();
                do
                {
                    if(!iter.hasNext())
                        break;
                    CondOrder ord = (CondOrder)iter.next();
                    List modules = (List)modulesToOrdTypes.get(ord.getType());
                    if(modules != null)
                    {
                        int i = 0;
                        while(i < modules.size())
                        {
                            RuleModule mod = (RuleModule)modules.get(i);
                            mod.unload(ord);
                            i++;
                        }
                    } else
                    if(log.isWarn())
                        log.warn((new StringBuilder()).append("Unable to find rule module for ").append(ord).toString());
                } while(true);
                if(log.isInfo())
                    log.info((new StringBuilder()).append("Unloaded ").append(orders.size()).append(" conditional orders.").toString());
                orders.clear();
                accounts = accInfo;
                try
                {
                    Collection ords = store.loadOrders(accInfo.getRange().getStart(), accInfo.getRange().getEnd());
                    if(ords != null)
                        doLoadOrders(ords);
                    if(log.isInfo())
                        log.info((new StringBuilder()).append("Loaded ").append(orders.size()).append(" conditional orders.").toString());
                }
                catch(PersistException e)
                {
                    throw new RuleEngineException("Unable to load conditional orders.", e);
                }
                catch(CondOrderException e)
                {
                    throw new RuleEngineException("Unable to load conditional orders.", e);
                }
                TxManager.commit();
            }
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
                    log.error(e1.getMessage(), e1);
            }
            throw new RuleEngineException("Unable to load conditional orders.", e);
        }
        accountsLock.releaseWrite();
        break MISSING_BLOCK_LABEL_462;
        Exception exception1;
        exception1;
        accountsLock.releaseWrite();
        throw exception1;
    }

    private void verifyLicense(String service)
        throws LicenseException
    {
        if(!$assertionsDisabled && service == null)
            throw new AssertionError("Service name is null.");
        LicenseDescriptor ld = LicenseManager.getActiveFeatureSet().getDescriptor(service);
        if(ld == null)
            throw new LicenseException((new StringBuilder()).append("License not found for service '").append(service).append("'.").toString());
        if(ld.getExpDate().before(new Date()))
            throw new LicenseException((new StringBuilder()).append("License for service '").append(service).append("' expired.").toString());
        if(ld.getIps() != null)
        {
            java.net.InetAddress localIps[] = Utils.getLocalIpAddrs();
            boolean found = false;
            for(int i = 0; i < localIps.length && !found; i++)
                found = ld.isIpLicensed(localIps[i]);

            if(!found)
                throw new LicenseException((new StringBuilder()).append("Service '").append(service).append("' is not licensed to start on the local node's ").append("IP address.").toString());
        }
        if(ld.getCpus() < Utils.getNumberOfCpus())
            throw new LicenseException((new StringBuilder()).append("Local node CPUs exceeded number of allowed CPUs for service '").append(service).append("'.").toString());
        else
            return;
    }

    public void start()
        throws InitializationException
    {
        CondOrderType ordTypes[];
        int i;
        LicenseManager.init();
        ordTypes = getRegisteredCondOrderTypes();
        i = 0;
_L8:
        String service;
        if(i >= ordTypes.length)
            break; /* Loop/switch isn't completed */
        service = null;
        ordTypes[i].toValue();
        JVM INSTR tableswitch 1 5: default 87
    //                   1 84
    //                   2 60
    //                   3 66
    //                   4 78
    //                   5 72;
           goto _L1 _L2 _L3 _L4 _L5 _L6
_L1:
        break; /* Loop/switch isn't completed */
_L3:
        service = "price-module";
        break; /* Loop/switch isn't completed */
_L4:
        service = "time-module";
        break; /* Loop/switch isn't completed */
_L6:
        service = "chain-module";
        break; /* Loop/switch isn't completed */
_L5:
        service = "exclusive-module";
        verifyLicense(service);
_L2:
        i++;
        if(true) goto _L8; else goto _L7
        LicenseException e;
        e;
        throw new InitializationException((new StringBuilder()).append("Licensing error: ").append(e.getMessage()).toString(), e);
_L7:
        if(log.isInfo())
            log.info("Starting...");
        if(log.isInfo())
            log.info("Starting rule modules.");
        try
        {
            TxManager.begin();
            Set started = new HashSet();
            for(Iterator iter = modulesToOrdTypes.values().iterator(); iter.hasNext();)
            {
                List modules = (List)iter.next();
                int i = 0;
                while(i < modules.size())
                {
                    RuleModule mod = (RuleModule)modules.get(i);
                    if(!started.contains(mod))
                    {
                        mod.start();
                        started.add(mod);
                    }
                    i++;
                }
            }

            TxManager.commit();
        }
        // Misplaced declaration of an exception variable
        catch(Set started)
        {
            try
            {
                TxManager.rollback();
            }
            catch(TxManagerException e1)
            {
                if(log.isError())
                    log.error(e1.getMessage(), e1);
            }
        }
        if(log.isInfo())
            log.info("Started rule modules.");
        state.start();
        if(log.isInfo())
            log.info("Started.");
        return;
    }

    public void stop()
    {
        state.stop();
        Set stopped = new HashSet();
        for(Iterator iter = modulesToOrdTypes.values().iterator(); iter.hasNext();)
        {
            List modules = (List)iter.next();
            int i = 0;
            while(i < modules.size())
            {
                RuleModule mod = (RuleModule)modules.get(i);
                if(!stopped.contains(mod))
                {
                    mod.stop();
                    stopped.add(mod);
                }
                i++;
            }
        }

        synchronized(mux)
        {
            orders.clear();
            accounts = null;
        }
        if(log.isInfo())
            log.info("Stopped.");
        registeredOrdersCount.set(0L);
        operationsCount.set(0L);
        executionsCount.set(0L);
    }

    public RuleContext createRuleContext(OrderRule rule, final RuleModule ruleModule)
    {
        return new RuleContext() {

            public void activateOrder(CondOrder ord)
                throws CondOrderException, CondOrderExpiredException
            {
                synchronized(getMutex(ord))
                {
                    if(!$assertionsDisabled && ord.getState() != CondOrderState.PENDING)
                        throw new AssertionError((new StringBuilder()).append("Illegal state: ").append(ord).toString());
                    doActivateOrder(ord);
                }
            }

            private boolean isFinished(CondOrder ord)
            {
                if(ord.hasSubOrders())
                {
                    for(Iterator iter = ord.getSubOrders().iterator(); iter.hasNext();)
                    {
                        CondOrder child = (CondOrder)iter.next();
                        if(!isFinished(child))
                            return false;
                    }

                    return true;
                } else
                {
                    return ord.getState() == CondOrderState.COMPLETED || ord.getState() == CondOrderState.CANCELED;
                }
            }

            public void markCompleted(CondOrder ord)
                throws CondOrderException
            {
                if(!$assertionsDisabled && ord.getState() != CondOrderState.ACTIVATED)
                    throw new AssertionError((new StringBuilder()).append("Illegal state: ").append(ord).toString());
                if(log.isDebug())
                    log.debug((new StringBuilder()).append("Completing order ").append(ord).toString());
                try
                {
                    store.markCompleted(ord);
                }
                catch(PersistException e)
                {
                    throw new CondOrderException((new StringBuilder()).append("Unable to mark order as completed: ").append(ord).toString(), e, ord);
                }
                ord.setState(CondOrderState.COMPLETED);
                CondOrder root;
                for(root = ord; root.getParent() != null; root = root.getParent());
                if(isFinished(root))
                    removeFromMap(new Long(root.getId()));
                List modules = (List)modulesToOrdTypes.get(ord.getType());
                if(modules != null)
                {
                    for(int i = 0; i < modules.size(); i++)
                    {
                        RuleModule mod = (RuleModule)modules.get(i);
                        mod.unregisterOrder(ord);
                    }

                } else
                {
                    throw new CondOrderException("Unable to unregister order from rule module. Unknown module", ord);
                }
            }

            public RuleModule getRuleModule()
            {
                return ruleModule;
            }

            public OmsRuleEngine getRuleEngine()
            {
                return OmsRuleEngineImpl.this;
            }

            public Object getMutex(CondOrder ord)
            {
                return getOrderMutex(ord);
            }

            public void cancel(CondOrder ord)
                throws CondOrderException
            {
                if(!$assertionsDisabled && ord.getState() == CondOrderState.CANCELED)
                {
                    throw new AssertionError((new StringBuilder()).append("Illegal state: ").append(ord).toString());
                } else
                {
                    doCancel(ord);
                    return;
                }
            }

            static final boolean $assertionsDisabled = !com/ com /fin/intellioms/rulesys/impl/OmsRuleEngineImpl.desiredAssertionStatus();
            final RuleModule val$ruleModule;
            final OmsRuleEngineImpl this$0;



            {
                this$0 = OmsRuleEngineImpl.this;
                ruleModule = rulemodule;
                super();
            }
        }
;
    }

    public boolean isStarted()
    {
        return state.isStarted();
    }

    private Object getOrderMutex(CondOrder ord)
    {
        CondOrder root;
        for(root = ord; root.getParent() != null; root = root.getParent());
        return root;
    }

    private void doActivateOrder(CondOrder ord)
        throws CondOrderException
    {
        if(log.isDebug())
            log.debug((new StringBuilder()).append("Activating order ").append(ord).toString());
        List modules = (List)modulesToOrdTypes.get(ord.getType());
        if(modules != null)
        {
            try
            {
                store.markActivated(ord);
            }
            catch(PersistException e)
            {
                throw new CondOrderException("Unable to activate order.", e, ord);
            }
            ord.setState(CondOrderState.ACTIVATED);
            for(int i = 0; i < modules.size(); i++)
            {
                RuleModule mod = (RuleModule)modules.get(i);
                mod.registerOrder(ord);
            }

            if(log.isDebug())
                log.debug((new StringBuilder()).append("Activated order ").append(ord).toString());
        } else
        {
            throw new CondOrderException("Unable to register order to rule module. Unknown module", ord);
        }
    }

    private void doRegister(CondOrder ord)
        throws CondOrderException
    {
        if(!$assertionsDisabled && ord == null)
        {
            throw new AssertionError("Order is null.");
        } else
        {
            putToOrdersMap(ord, true);
            doActivateOrder(ord);
            return;
        }
    }

    private void putToOrdersMap(CondOrder ord, boolean checkDuplicates)
        throws DuplicatedOrderException
    {
        if(!$assertionsDisabled && ord == null)
            throw new AssertionError("Order is null.");
        Long id = new Long(ord.getId());
        synchronized(mux)
        {
            if(checkDuplicates && orders.containsKey(id))
                throw new DuplicatedOrderException((new StringBuilder()).append("Duplicated order id = ").append(id).toString(), ord);
            if(checkDuplicates)
                registeredOrdersCount.postIncr();
            orders.put(id, ord);
            if(ord.hasSubOrders())
            {
                for(Iterator iter = ord.getSubOrders().iterator(); iter.hasNext(); putToOrdersMap((CondOrder)iter.next(), checkDuplicates));
            }
        }
    }

    private void removeFromMap(Long id)
    {
        synchronized(mux)
        {
            CondOrder o = (CondOrder)orders.remove(id);
            if(o != null && o.getSubOrders() != null)
            {
                CondOrder subOrd;
                for(Iterator iter = o.getSubOrders().iterator(); iter.hasNext(); removeFromMap(new Long(subOrd.getId())))
                    subOrd = (CondOrder)iter.next();

            }
        }
    }

    private void doLoadOrders(Collection ords)
        throws CondOrderException
    {
        Iterator ordsIter = ords.iterator();
        do
        {
            if(!ordsIter.hasNext())
                break;
            CondOrder ord = (CondOrder)ordsIter.next();
            if(ord.getParent() == null)
            {
                Object ordMux = getOrderMutex(ord);
                synchronized(ordMux)
                {
                    loadOrder(ord);
                    putToOrdersMap(ord, false);
                    if(ord.getState() == CondOrderState.PENDING)
                        doActivateOrder(ord);
                }
            }
        } while(true);
    }

    private void loadOrder(CondOrder ord)
        throws CondOrderException
    {
        if(ord.getState() == CondOrderState.ACTIVATED)
        {
            List modules = (List)modulesToOrdTypes.get(ord.getType());
            if(modules != null)
            {
                for(int i = 0; i < modules.size(); i++)
                {
                    RuleModule mod = (RuleModule)modules.get(i);
                    mod.load(ord);
                }

            } else
            {
                throw new CondOrderException("Unable to load order to rule module. Unknown module", ord);
            }
        }
        if(ord.hasSubOrders())
        {
            List childs = ord.getSubOrders();
            for(int i = 0; i < childs.size(); i++)
            {
                CondOrder child = (CondOrder)childs.get(i);
                loadOrder(child);
            }

        }
    }

    public long getOperationsCount()
    {
        return operationsCount.get();
    }

    public long getRegisteredOrdersCount()
    {
        return registeredOrdersCount.get();
    }

    public long getActiveOrdersCount()
    {
        Object obj = mux;
        JVM INSTR monitorenter ;
        return (long)orders.size();
        Exception exception;
        exception;
        throw exception;
    }

    public long getExecutionsCount()
    {
        return executionsCount.get();
    }

    public EventType[] getRegisteredEventTypes()
    {
        EventType types[] = new EventType[modulesToEventTypes.size()];
        return (EventType[])(EventType[])modulesToEventTypes.keySet().toArray(types);
    }

    public CondOrderType[] getRegisteredCondOrderTypes()
    {
        CondOrderType types[] = new CondOrderType[modulesToOrdTypes.size()];
        return (CondOrderType[])(CondOrderType[])modulesToOrdTypes.keySet().toArray(types);
    }

    public Map getRegisteredRuleModules()
    {
        return Collections.unmodifiableMap(modulesToOrdTypes);
    }

    public String toString()
    {
        return "RuleEngine[]";
    }

    public AccountsInfo getAccountsInfo()
    {
        return accounts;
    }

    private Log log;
    private Map modulesToOrdTypes;
    private Map modulesToEventTypes;
    private Map orders;
    private final PersistAdaptor store;
    private final ServiceState state = new ServiceState("OmsRuleEngine");
    private BoxedInt64Sync registeredOrdersCount;
    private BoxedInt64Sync operationsCount;
    private BoxedInt64Sync executionsCount;
    private AccountsInfo accounts;
    private final Object mux = new Object();
    private final WritePrefRWLock accountsLock = new WritePrefRWLock();
    static final boolean $assertionsDisabled = !com/ com /fin/intellioms/rulesys/impl/OmsRuleEngineImpl.desiredAssertionStatus();








}
