// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   OrderType.java

package co.fin.intellioms.enums;


// Referenced classes of package com.com.fin.intellioms.enums:
//            Enumeration

public class OrderType extends Enumeration
{
    public class Value
    {

        public static final int EQUITY_CASH = 1;
        public static final int EQUITY_MARGIN = 2;
        public static final int BOND_CASH = 3;
        public static final int BOND_MARGIN = 4;
        public static final int INDEX_OPTION = 5;
        public static final int INDEX_FUTURE = 6;
        final OrderType this$0;

        public Value()
        {
            this$0 = OrderType.this;
            super();
        }
    }


    protected OrderType(int v, String s)
    {
        super(v, s);
    }

    public static final OrderType getEnum(int v)
    {
        switch(v)
        {
        case 1: // '\001'
            return EQUITY_CASH;

        case 2: // '\002'
            return EQUITY_MARGIN;

        case 3: // '\003'
            return BOND_CASH;

        case 4: // '\004'
            return BOND_MARGIN;

        case 5: // '\005'
            return INDEX_OPTION;

        case 6: // '\006'
            return INDEX_FUTURE;
        }
        throw new RuntimeException((new StringBuilder()).append("OrderType: no corresponding number for ").append(v).toString());
    }

    public static final OrderType EQUITY_CASH = new OrderType(1, "EQUITY_CASH");
    public static final OrderType EQUITY_MARGIN = new OrderType(2, "EQUITY_MARGIN");
    public static final OrderType BOND_CASH = new OrderType(3, "BOND_CASH");
    public static final OrderType BOND_MARGIN = new OrderType(4, "BOND_MARGIN");
    public static final OrderType INDEX_OPTION = new OrderType(5, "INDEX_OPTION");
    public static final OrderType INDEX_FUTURE = new OrderType(6, "INDEX_FUTURE");

}
