// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   OrderExecType.java

package co.fin.intellioms.enums;


// Referenced classes of package com.com.fin.intellioms.enums:
//            Enumeration

public class OrderExecType extends Enumeration
{
    public class Value
    {

        public static final int NO_CONDITION = 1;
        public static final int MARKET = 2;
        public static final int MARKET_OPEN = 3;
        public static final int MARKET_CLOSE = 4;
        public static final int MARKET_ON_CLOSE = 5;
        final OrderExecType this$0;

        public Value()
        {
            this$0 = OrderExecType.this;
            super();
        }
    }


    protected OrderExecType(int v, String s)
    {
        super(v, s);
    }

    public static final OrderExecType getEnum(int v)
    {
        switch(v)
        {
        case 1: // '\001'
            return NO_CONDITION;

        case 2: // '\002'
            return MARKET;

        case 3: // '\003'
            return MARKET_OPEN;

        case 4: // '\004'
            return MARKET_CLOSE;

        case 5: // '\005'
            return MARKET_ON_CLOSE;
        }
        throw new RuntimeException((new StringBuilder()).append("OrderExecType: no corresponding number for ").append(v).toString());
    }

    public static final OrderExecType NO_CONDITION = new OrderExecType(1, "NO_CONDITION");
    public static final OrderExecType MARKET = new OrderExecType(2, "MARKET");
    public static final OrderExecType MARKET_OPEN = new OrderExecType(3, "MARKET_OPEN");
    public static final OrderExecType MARKET_CLOSE = new OrderExecType(4, "MARKET_CLOSE");
    public static final OrderExecType MARKET_ON_CLOSE = new OrderExecType(5, "MARKET_ON_CLOSE");

}
