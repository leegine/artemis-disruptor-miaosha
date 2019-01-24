// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   Quantity.java

package co.fin.intellioms.omsclt;

import co.fin.intellioms.util.LargeNumber;

import java.math.BigDecimal;

public class Quantity extends LargeNumber
{

    public Quantity(double val)
    {
        super(val);
    }

    public Quantity(String val)
    {
        super(val);
    }

    public Quantity(BigDecimal val)
    {
        super(val);
    }

    public Quantity(Quantity nmb)
    {
        super(nmb);
    }
}
