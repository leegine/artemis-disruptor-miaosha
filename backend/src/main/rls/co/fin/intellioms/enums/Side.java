// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   Side.java

package co.fin.intellioms.enums;


// Referenced classes of package com.com.fin.intellioms.enums:
//            Enumeration

public class Side extends Enumeration
{
    public class Value
    {

        public static final int BUY = 1;
        public static final int SELL = 2;
        final Side this$0;

        public Value()
        {
            this$0 = Side.this;
            super();
        }
    }


    protected Side(int v, String s)
    {
        super(v, s);
    }

    public static final Side getEnum(int v)
    {
        switch(v)
        {
        case 1: // '\001'
            return BUY;

        case 2: // '\002'
            return SELL;
        }
        throw new RuntimeException((new StringBuilder()).append("Side: no corresponding number for ").append(v).toString());
    }

    public static final Side BUY = new Side(1, "BUY");
    public static final Side SELL = new Side(2, "SELL");

}
