// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   PriceDirection.java

package co.fin.intellioms.enums;


// Referenced classes of package com.com.fin.intellioms.enums:
//            Enumeration

public class PriceDirection extends Enumeration
{
    public class Value
    {

        public static final int UP = 1;
        public static final int DOWN = 2;
        final PriceDirection this$0;

        public Value()
        {
            this$0 = PriceDirection.this;
            super();
        }
    }


    protected PriceDirection(int v, String s)
    {
        super(v, s);
    }

    public static final Enumeration getEnum(int v)
    {
        switch(v)
        {
        case 1: // '\001'
            return UP;

        case 2: // '\002'
            return DOWN;
        }
        return getEnumError(v, "PriceDirection");
    }

    public static final PriceDirection UP = new PriceDirection(1, "UP");
    public static final PriceDirection DOWN = new PriceDirection(2, "DOWN");

}
