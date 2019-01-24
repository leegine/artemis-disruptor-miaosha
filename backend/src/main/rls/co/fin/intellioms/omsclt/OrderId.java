// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   OrderId.java

package co.fin.intellioms.omsclt;

import co.fin.intellioms.enums.OrderType;

public class OrderId
{

    public OrderId(long id, OrderType type)
    {
        this.id = id;
        this.type = type;
    }

    public long getId()
    {
        return id;
    }

    public OrderType getType()
    {
        return type;
    }

    public boolean equals(Object o)
    {
        if(o instanceof OrderId)
        {
            OrderId id = (OrderId)o;
            return id.getId() == this.id && id.getType() == type;
        } else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return (int)(id + (long)type.toValue() ^ id >>> 32);
    }

    public String toString()
    {
        return (new StringBuilder()).append("[id=").append(id).append(" type=").append(type).append("]").toString();
    }

    private long id;
    private OrderType type;
}
