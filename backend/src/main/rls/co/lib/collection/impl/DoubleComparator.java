// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   DoubleComparator.java

package co.lib.collection.impl;

import java.util.Comparator;

public class DoubleComparator
    implements Comparator
{

    public DoubleComparator()
    {
    }

    public int compare(Object x, Object y)
    {
        Double xx = (Double)x;
        Double yy;
        try
        {
            yy = (Double)y;
            if(xx.doubleValue() > yy.doubleValue())
                return 1;
        }
        catch(Exception e)
        {
            return 1;
        }
        if(xx.doubleValue() < yy.doubleValue())
            return -1;
        return 0;
//        Exception ee;
//        ee;
//        return -1;
    }
}
