// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   CondOrderState.java

package co.fin.intellioms.enums;


// Referenced classes of package com.com.fin.intellioms.enums:
//            Enumeration

public class CondOrderState extends Enumeration
{
    public class Value
    {

        public static final int PENDING = 0;
        public static final int ACTIVATED = 2;
        public static final int CANCELED = 3;
        public static final int COMPLETED = 4;
        final CondOrderState this$0;

        public Value()
        {
            this$0 = CondOrderState.this;
            super();
        }
    }


    protected CondOrderState(int v, String s)
    {
        super(v, s);
    }

    public static final Enumeration getEnum(int v)
    {
        switch(v)
        {
        case 0: // '\0'
            return PENDING;

        case 2: // '\002'
            return ACTIVATED;

        case 3: // '\003'
            return CANCELED;

        case 4: // '\004'
            return COMPLETED;

        case 1: // '\001'
        default:
            return getEnumError(v, "CondOrderState");
        }
    }

    public static final CondOrderState PENDING = new CondOrderState(0, "PENDING");
    public static final CondOrderState ACTIVATED = new CondOrderState(2, "ACTIVATED");
    public static final CondOrderState CANCELED = new CondOrderState(3, "CANCELED");
    public static final CondOrderState COMPLETED = new CondOrderState(4, "COMPLETED");

}
