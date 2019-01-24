// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   TimeCondOrder.java

package co.fin.intellioms.time;

import co.fin.intellioms.util.CondOrderUtill;
import co.fin.intellioms.enums.CondOrderType;
import com.fitechlabs.fin.intellioms.rulesys.*;
import co.fin.intellioms.rulesys.impl.AbstractCondOrder;

import java.util.*;

public class TimeCondOrder extends AbstractCondOrder
{
    private static class MarshalConst
    {

        private static final Integer FIRE_TIME = new Integer(4001);



        private MarshalConst()
        {
        }
    }


    public TimeCondOrder(long condOrderId, long accId, Date fireTime)
    {
        super(condOrderId, accId, CondOrderType.TIME, fireTime);
    }

    public TimeCondOrder()
    {
        super(CondOrderType.TIME);
    }

    public Date getFireTime()
    {
        return (Date)getParam();
    }

    public Ratio getExecutedRatio()
    {
        return CondOrderUtill.findLargestExecutedRatio(subOrders);
    }

    public Ratio getActivatedRatio()
    {
        if(subOrders == null)
        {
            return null;
        } else
        {
            CondOrder firstChild = (CondOrder)subOrders.get(0);
            return firstChild.getActivatedRatio();
        }
    }

    public void setActivatedRatio(Ratio ratio)
        throws CondOrderException
    {
        for(int i = 0; i < subOrders.size(); i++)
        {
            CondOrder ord = (CondOrder)subOrders.get(i);
            ord.setActivatedRatio(ratio);
        }

    }

    protected void marshal(Map params)
    {
        params.put(MarshalConst.FIRE_TIME, getFireTime());
    }

    protected void demarshal(Map params)
    {
        setParam(params.get(MarshalConst.FIRE_TIME));
    }

    public short typeGuid()
    {
        return 3;
    }

    public static final short GUID = 3;
}
