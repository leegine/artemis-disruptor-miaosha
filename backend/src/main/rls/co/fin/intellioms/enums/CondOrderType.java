// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   CondOrderType.java

package co.fin.intellioms.enums;


// Referenced classes of package com.com.fin.intellioms.enums:
//            Enumeration

public class CondOrderType extends Enumeration
{
    public class Value
    {

        public static final int SIMPLE = 1;
        public static final int PRICE = 2;
        public static final int TIME = 3;
        public static final int EXCLUSIVE = 4;
        public static final int CHAIN = 5;
        final CondOrderType this$0;

        public Value()
        {
            this$0 = CondOrderType.this;
            super();
        }
    }


    protected CondOrderType(int v, String s)
    {
        super(v, s);
    }

    public static final Enumeration getEnum(int v)
    {
        switch(v)
        {
        case 1: // '\001'
            return SIMPLE;

        case 2: // '\002'
            return PRICE;

        case 3: // '\003'
            return TIME;

        case 4: // '\004'
            return EXCLUSIVE;

        case 5: // '\005'
            return CHAIN;
        }
        return getEnumError(v, "CondOrderType");
    }

    public static final CondOrderType SIMPLE = new CondOrderType(1, "SIMPLE");
    public static final CondOrderType PRICE = new CondOrderType(2, "PRICE");
    public static final CondOrderType TIME = new CondOrderType(3, "TIME");
    public static final CondOrderType EXCLUSIVE = new CondOrderType(4, "EXCLUSIVE");
    public static final CondOrderType CHAIN = new CondOrderType(5, "CHAIN");

}
