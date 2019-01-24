// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   AbstractCondOrder.java

package co.fin.intellioms.rulesys.impl;

import co.fin.intellioms.enums.CondOrderState;
import co.fin.intellioms.enums.CondOrderType;
import co.fin.intellioms.rulesys.CondOrder;
import co.fin.intellioms.rulesys.CondOrderException;
import co.fin.intellioms.rulesys.Ratio;
import com.fitechlabs.xtier.services.marshal.MarshalException;
import com.fitechlabs.xtier.services.marshal.Marshallable;
import com.fitechlabs.xtier.utils.Utils;

import java.util.*;

public abstract class AbstractCondOrder
    implements CondOrder, Marshallable
{
    private static class MarshalConst
    {

        private static final Integer ORD_ID = new Integer(1001);
        private static final Integer ORD_ACCOUNT_ID = new Integer(1002);
        private static final Integer ORD_STATE = new Integer(1003);
        static final Integer ORD_PARENT = new Integer(1004);
        static final Integer ORD_CHILDREN = new Integer(1005);





        private MarshalConst()
        {
        }
    }


    public AbstractCondOrder(CondOrderType oType)
    {
        state = CondOrderState.PENDING;
        type = oType;
    }

    public AbstractCondOrder(long id, long accountId, CondOrderType oType, Object param)
    {
        state = CondOrderState.PENDING;
        this.id = id;
        type = oType;
        this.param = param;
        this.accountId = accountId;
    }

    public void setParent(CondOrder order)
    {
        parent = order;
    }

    public CondOrder getParent()
    {
        return parent;
    }

    public synchronized CondOrderState getState()
    {
        return state;
    }

    public synchronized void setState(CondOrderState state)
    {
        this.state = state;
    }

    public CondOrderType getType()
    {
        return type;
    }

    public long getId()
    {
        return id;
    }

    protected void setAccountId(long accountId)
    {
        this.accountId = accountId;
    }

    protected void setId(long id)
    {
        this.id = id;
    }

    public long getAccountId()
    {
        return accountId;
    }

    public Object getParam()
    {
        return param;
    }

    public void setParam(Object param)
    {
        this.param = param;
    }

    public void processExecution(CondOrder child, Ratio executedRatio)
        throws CondOrderException
    {
        if(parent != null)
            parent.processExecution(this, executedRatio);
    }

    public void addSubOrder(CondOrder order)
    {
        if(subOrders == null)
            subOrders = new ArrayList();
        order.setParent(this);
        subOrders.add(order);
    }

    public List getSubOrders()
    {
        return subOrders == null ? NULL_LIST : Collections.unmodifiableList(subOrders);
    }

    public boolean hasSubOrders()
    {
        return subOrders != null && !subOrders.isEmpty();
    }

    public void setSubOrders(List subOrders)
    {
        if(subOrders == null)
            subOrders = new ArrayList();
        this.subOrders = subOrders;
    }

    public Map getObjs()
        throws MarshalException
    {
        Map param = new HashMap();
        param.put(MarshalConst.ORD_ID, new Long(getId()));
        param.put(MarshalConst.ORD_ACCOUNT_ID, new Long(getAccountId()));
        param.put(MarshalConst.ORD_STATE, new Integer(getState().toValue()));
        if(parent != null)
            param.put(MarshalConst.ORD_PARENT, parent);
        if(subOrders != null)
            param.put(MarshalConst.ORD_CHILDREN, subOrders);
        marshal(param);
        return param;
    }

    public void setObjs(Map objs)
        throws MarshalException
    {
        Long id = (Long)objs.get(MarshalConst.ORD_ID);
        Long accountId = (Long)objs.get(MarshalConst.ORD_ACCOUNT_ID);
        int state = ((Integer)objs.get(MarshalConst.ORD_STATE)).intValue();
        CondOrder parent = (CondOrder)objs.get(MarshalConst.ORD_PARENT);
        List subOrders = (List)objs.get(MarshalConst.ORD_CHILDREN);
        setId(id.longValue());
        setAccountId(accountId.longValue());
        setState((CondOrderState)CondOrderState.getEnum(state));
        if(parent != null)
            setParent(parent);
        if(subOrders != null)
        {
            for(int i = 0; i < subOrders.size(); i++)
            {
                CondOrder subOrd = (CondOrder)subOrders.get(i);
                addSubOrder(subOrd);
            }

        }
        demarshal(objs);
    }

    protected abstract void marshal(Map map)
        throws MarshalException;

    protected abstract void demarshal(Map map)
        throws MarshalException;

    public void onMarshal()
    {
    }

    public void onDemarshal()
    {
    }

    public boolean equals(Object o)
    {
        if(o instanceof CondOrder)
        {
            CondOrder ord = (CondOrder)o;
            return id == ord.getId();
        } else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return (int)id;
    }

    public String toString()
    {
        return (new StringBuilder()).append("CondOrder[type=").append(type).append(", id=").append(id).append(",parentId=").append(parent == null ? -1L : parent.getId()).append(", accountId=").append(accountId).append(", activated=").append(getActivatedRatio()).append(", executed=").append(getExecutedRatio()).append(", state=").append(state).append(" , param=").append(getParam()).append(", children=").append(subOrders == null ? null : Utils.list2Str(subOrders)).append("]").toString();
    }

    private static final List NULL_LIST = Collections.unmodifiableList(new ArrayList(0));
    private long id;
    private long accountId;
    private final CondOrderType type;
    private Object param;
    protected List subOrders;
    private CondOrder parent;
    private CondOrderState state;

}
