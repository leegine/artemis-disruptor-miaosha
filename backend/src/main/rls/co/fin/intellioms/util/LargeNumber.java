// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   LargeNumber.java

package co.fin.intellioms.util;

import java.math.BigDecimal;

public class LargeNumber
    implements Comparable
{

    public LargeNumber(double val)
    {
        this.val = new BigDecimal(val);
        checkScale();
    }

    public LargeNumber(BigDecimal val)
    {
        this.val = val;
        checkScale();
    }

    public LargeNumber(String val)
    {
        this.val = new BigDecimal(val);
        checkScale();
    }

    public LargeNumber(LargeNumber nmb)
    {
        val = nmb.val;
    }

    public boolean isAbove(LargeNumber nmb)
    {
        return compareTo(nmb) > 0;
    }

    public boolean isAboveEq(LargeNumber nmb)
    {
        return compareTo(nmb) >= 0;
    }

    public boolean isBelow(LargeNumber nmb)
    {
        return compareTo(nmb) < 0;
    }

    public boolean isBelowEq(LargeNumber nmb)
    {
        return compareTo(nmb) <= 0;
    }

    public int compareTo(Object o)
    {
        if(o == this)
            return 0;
        if(o instanceof LargeNumber)
        {
            LargeNumber nmb = (LargeNumber)o;
            return val.compareTo(nmb.val);
        } else
        {
            return -1;
        }
    }

    public LargeNumber negate()
    {
        val = val.negate();
        return this;
    }

    public LargeNumber add(LargeNumber num)
    {
        val = val.add(num.val);
        checkScale();
        return this;
    }

    public LargeNumber subtruct(LargeNumber num)
    {
        val = val.subtract(num.val);
        checkScale();
        return this;
    }

    public LargeNumber devide(LargeNumber num)
    {
        val = val.divide(num.val, 25, 4);
        return this;
    }

    public LargeNumber multiply(LargeNumber num)
    {
        val = val.multiply(num.val);
        checkScale();
        return this;
    }

    public double getDouble()
    {
        return val.doubleValue();
    }

    public BigDecimal getBigDecimal()
    {
        return val;
    }

    public boolean equals(Object o)
    {
        if(o instanceof LargeNumber)
        {
            LargeNumber nmb = (LargeNumber)o;
            return val.compareTo(nmb.val) == 0;
        } else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return val.hashCode();
    }

    private void checkScale()
    {
        if(val.scale() > 25)
            val = val.setScale(25, 4);
    }

    public final String toString()
    {
        return val.toString();
    }

    public static final LargeNumber ZERO = new LargeNumber(0.0D);
    public static final int SCALE = 25;
    public static final LargeNumber MIN_POSITIVE = new LargeNumber(BigDecimal.valueOf(1L, 25));
    private BigDecimal val;

}
