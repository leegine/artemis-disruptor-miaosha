// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   DbStoreWrapper.java

package co.fin.intellioms.persist.db;

import co.fin.intellioms.rulesys.CondOrder;
import co.fin.intellioms.rulesys.CondOrderOperation;
import co.fin.intellioms.event.Event;
import co.fin.intellioms.omsclt.Order;
import co.fin.intellioms.persist.PersistAdaptor;
import co.fin.intellioms.persist.PersistException;

import java.util.Collection;

public class DbStoreWrapper
    implements PersistAdaptor
{

    public DbStoreWrapper(PersistAdaptor store)
    {
        if(!$assertionsDisabled && store == null)
        {
            throw new AssertionError("Store is null");
        } else
        {
            this.store = store;
            return;
        }
    }

    public void save(Event event)
        throws PersistException
    {
        store.save(event);
    }

    public void save(CondOrderOperation op)
        throws PersistException
    {
        store.save(op);
    }

    public void cancel(CondOrder ord)
        throws PersistException
    {
        store.cancel(ord);
    }

    public void save(Order ord)
        throws PersistException
    {
        store.save(ord);
    }

    public void updateQuantity(Order ord)
        throws PersistException
    {
        store.updateQuantity(ord);
    }

    public void markCompleted(CondOrder ord)
        throws PersistException
    {
        store.markCompleted(ord);
    }

    public void markActivated(CondOrder ord)
        throws PersistException
    {
        store.markActivated(ord);
    }

    public void updateActivationRatio(CondOrder ord)
        throws PersistException
    {
        store.updateActivationRatio(ord);
    }

    public Collection loadOrders(long start, long end)
        throws PersistException
    {
        return store.loadOrders(start, end);
    }

    private final PersistAdaptor store;
    static final boolean $assertionsDisabled = !com/ com /fin/intellioms/persist/db/DbStoreWrapper.desiredAssertionStatus();

}
