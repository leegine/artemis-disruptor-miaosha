// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   MarketResponseType.java

package co.fin.intellioms.enums;


// Referenced classes of package com.com.fin.intellioms.enums:
//            Enumeration

public class MarketResponseType extends Enumeration
{
    public class Value
    {

        public static final int NEW = 1;
        public static final int MODIFY = 2;
        public static final int CANCEL = 3;
        public static final int EXECUTE = 4;
        public static final int EXPIRE = 5;
        final MarketResponseType this$0;

        public Value()
        {
            this$0 = MarketResponseType.this;
            super();
        }
    }


    public static final Enumeration getEnum(int v)
    {
        switch(v)
        {
        case 1: // '\001'
            return NEW;

        case 2: // '\002'
            return MODIFY;

        case 3: // '\003'
            return CANCEL;

        case 4: // '\004'
            return EXECUTE;

        case 5: // '\005'
            return EXPIRE;
        }
        return getEnumError(v, "MarketResponseType");
    }

    protected MarketResponseType(int v, String s)
    {
        super(v, s);
    }

    public static final MarketResponseType NEW = new MarketResponseType(1, "NEW");
    public static final MarketResponseType MODIFY = new MarketResponseType(2, "MODIFY");
    public static final MarketResponseType CANCEL = new MarketResponseType(3, "CANCEL");
    public static final MarketResponseType EXECUTE = new MarketResponseType(4, "EXECUTE");
    public static final MarketResponseType EXPIRE = new MarketResponseType(5, "EXPIRE");

}
