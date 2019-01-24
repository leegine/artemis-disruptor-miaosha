// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   PersistAdaptor.java

package co.fin.intellioms.persist;

import co.fin.intellioms.rulesys.CondOrder;
import co.fin.intellioms.rulesys.CondOrderOperation;
import co.fin.intellioms.event.Event;
import co.fin.intellioms.omsclt.Order;

import java.util.Collection;

// Referenced classes of package com.com.fin.intellioms.persist:
//            PersistException

public interface PersistAdaptor
{

    public abstract void save(Event event)
        throws PersistException;

    public abstract void save(CondOrderOperation condorderoperation)
        throws PersistException;

    public abstract void cancel(CondOrder condorder)
        throws PersistException;

    public abstract void save(Order order)
        throws PersistException;

    public abstract void updateQuantity(Order order)
        throws PersistException;

    public abstract void markCompleted(CondOrder condorder)
        throws PersistException;

    public abstract void markActivated(CondOrder condorder)
        throws PersistException;

    public abstract void updateActivationRatio(CondOrder condorder)
        throws PersistException;

    public abstract Collection loadOrders(long l, long l1)
        throws PersistException;
}
