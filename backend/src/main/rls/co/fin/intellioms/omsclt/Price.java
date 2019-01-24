// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   Price.java

package co.fin.intellioms.omsclt;

import co.fin.intellioms.util.LargeNumber;

import java.math.BigDecimal;

public class Price extends LargeNumber
{

    public Price(double val)
    {
        super(val);
    }

    public Price(BigDecimal val)
    {
        super(val);
    }

    public Price(String val)
    {
        super(val);
    }

    public Price(Price p)
    {
        super(p);
    }
}
