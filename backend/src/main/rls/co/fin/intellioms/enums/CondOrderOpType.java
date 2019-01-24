// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   CondOrderOpType.java

package co.fin.intellioms.enums;


// Referenced classes of package com.com.fin.intellioms.enums:
//            Enumeration

public class CondOrderOpType extends Enumeration
{
    public class Value
    {

        public static final int NEW = 1;
        public static final int MODIFY = 2;
        public static final int CANCEL = 3;
        final CondOrderOpType this$0;

        public Value()
        {
            this$0 = CondOrderOpType.this;
            super();
        }
    }


    protected CondOrderOpType(int v, String s)
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
        }
        return getEnumError(v, "CondOrderOpType");
    }

    public static final CondOrderOpType NEW = new CondOrderOpType(1, "NEW");
    public static final CondOrderOpType MODIFY = new CondOrderOpType(2, "MODIFY");
    public static final CondOrderOpType CANCEL = new CondOrderOpType(3, "CANCEL");

}
