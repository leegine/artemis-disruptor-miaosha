// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   Ratio.java

package co.fin.intellioms.rulesys;

import co.fin.intellioms.util.LargeNumber;

import java.math.BigDecimal;

public class Ratio extends LargeNumber
{

    public Ratio(double val)
    {
        super(val);
    }

    public Ratio(String val)
    {
        super(val);
    }

    public Ratio(BigDecimal val)
    {
        super(val);
    }

    public Ratio(LargeNumber r)
    {
        super(r);
    }

    public static final Ratio NONE = new Ratio(0.0D);
    public static final Ratio ALL = new Ratio(1.0D);

}
