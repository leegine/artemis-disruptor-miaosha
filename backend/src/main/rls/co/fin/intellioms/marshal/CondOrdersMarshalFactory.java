// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   CondOrdersMarshalFactory.java

package co.fin.intellioms.marshal;

import co.fin.intellioms.exclusive.ExclusiveCondOrder;
import co.fin.intellioms.time.TimeCondOrder;
import co.fin.intellioms.chain.ChainCondOrder;
import co.fin.intellioms.price.PriceCondOrder;
import co.fin.intellioms.rulesys.impl.CondOrderOperationImpl;
import co.fin.intellioms.simple.SimpleCondOrder;
import com.fitechlabs.xtier.services.marshal.Marshallable;
import com.fitechlabs.xtier.services.marshal.MarshallableFactory;

public class CondOrdersMarshalFactory
    implements MarshallableFactory
{

    public CondOrdersMarshalFactory()
    {
    }

    public Marshallable tryNewInstance(short typeGuid)
    {
        switch(typeGuid)
        {
        case 101: // 'e'
            return new CondOrderOperationImpl();

        case 1: // '\001'
            return new SimpleCondOrder();

        case 2: // '\002'
            return new PriceCondOrder();

        case 4: // '\004'
            return new ExclusiveCondOrder();

        case 5: // '\005'
            return new ChainCondOrder();

        case 3: // '\003'
            return new TimeCondOrder();
        }
        return null;
    }
}
