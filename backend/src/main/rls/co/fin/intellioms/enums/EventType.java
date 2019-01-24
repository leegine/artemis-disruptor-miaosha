// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   EventType.java

package co.fin.intellioms.enums;


// Referenced classes of package com.com.fin.intellioms.enums:
//            Enumeration

public class EventType extends Enumeration
{
    public class Value
    {

        public static final int QUOTE = 1;
        public static final int MARKET_RESP = 2;
        public static final int TIMER = 3;
        public static final int ORDER = 4;
        public static final int EXECUTION = 5;
        final EventType this$0;

        public Value()
        {
            this$0 = EventType.this;
            super();
        }
    }


    protected EventType(int v, String s)
    {
        super(v, s);
    }

    public static final Enumeration getEnum(int v)
    {
        switch(v)
        {
        case 1: // '\001'
            return QUOTE;

        case 2: // '\002'
            return MARKET_RESP;

        case 3: // '\003'
            return TIMER;

        case 4: // '\004'
            return ORDER;

        case 5: // '\005'
            return EXECUTION;
        }
        return getEnumError(v, "EventType");
    }

    public static final EventType QUOTE = new EventType(1, "QUOTE");
    public static final EventType MARKET_RESP = new EventType(2, "MARKET_RESP");
    public static final EventType TIMER = new EventType(3, "TIMER");
    public static final EventType ORDER = new EventType(4, "ORDER");
    public static final EventType EXECUTION = new EventType(5, "EXECUTION");

}
