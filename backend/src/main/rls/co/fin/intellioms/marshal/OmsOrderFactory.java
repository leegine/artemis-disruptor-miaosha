// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   OmsOrderFactory.java

package co.fin.intellioms.marshal;

import co.fin.intellioms.omsclt.impl.OrderImpl;
import com.fitechlabs.xtier.services.marshal.Marshallable;
import com.fitechlabs.xtier.services.marshal.MarshallableFactory;

public class OmsOrderFactory
    implements MarshallableFactory
{

    public OmsOrderFactory()
    {
    }

    public Marshallable tryNewInstance(short typeGuid)
    {
        if(typeGuid == 100)
            return new OrderImpl();
        else
            return null;
    }
}
