// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   OrderActionType.java

package co.fin.intellioms.enums;


// Referenced classes of package com.com.fin.intellioms.enums:
//            Enumeration

public class OrderActionType extends Enumeration
{
    public class Value
    {

        public static final int NEW = 1;
        public static final int MODIFY = 2;
        public static final int CANCEL = 3;
        public static final int EXECUTE = 4;
        public static final int EXPIRE = 5;
        final OrderActionType this$0;

        public Value()
        {
            this$0 = OrderActionType.this;
            super();
        }
    }


    protected OrderActionType(int v, String s)
    {
        super(v, s);
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
        return getEnumError(v, "OrderActionType");
    }

    public static final OrderActionType NEW = new OrderActionType(1, "NEW");
    public static final OrderActionType MODIFY = new OrderActionType(2, "MODIFY");
    public static final OrderActionType CANCEL = new OrderActionType(3, "CANCEL");
    public static final OrderActionType EXECUTE = new OrderActionType(4, "EXECUTE");
    public static final OrderActionType EXPIRE = new OrderActionType(5, "EXPIRE");

}
