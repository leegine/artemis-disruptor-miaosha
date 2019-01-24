// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   CondOrderOperationImpl.java

package co.fin.intellioms.rulesys.impl;

import co.fin.intellioms.rulesys.CondOrder;
import co.fin.intellioms.rulesys.CondOrderOperation;
import co.fin.intellioms.enums.CondOrderOpType;
import com.fitechlabs.xtier.services.marshal.Marshallable;
import java.util.HashMap;
import java.util.Map;

public class CondOrderOperationImpl
    implements CondOrderOperation, Marshallable
{
    private static class MarshalConst
    {

        static final Integer COND_OP_TYPE = new Integer(0x12f9a);
        static final Integer COND_OP_ORDER = new Integer(0x12f9b);
        static final Integer COND_OP_OLD_ORDER = new Integer(0x12f9c);


        private MarshalConst()
        {
        }
    }


    public CondOrderOperationImpl()
    {
    }

    public CondOrderOperationImpl(CondOrderOpType type, CondOrder newOrd, CondOrder oldOrd)
    {
        this.type = type;
        this.newOrd = newOrd;
        this.oldOrd = oldOrd;
    }

    public CondOrderOpType getOpType()
    {
        return type;
    }

    public CondOrder getCondOrder()
    {
        return newOrd;
    }

    public CondOrder getOldCondOrder()
    {
        return oldOrd;
    }

    public String toString()
    {
        return (new StringBuilder()).append("CondOrderOperation[type=").append(type).append(", old=").append(oldOrd).append(", new=").append(newOrd).append("]").toString();
    }

    public short typeGuid()
    {
        return 101;
    }

    public Map getObjs()
    {
        Map param = new HashMap(3, 1.0F);
        param.put(MarshalConst.COND_OP_TYPE, new Integer(type.toValue()));
        if(newOrd != null)
            param.put(MarshalConst.COND_OP_ORDER, newOrd);
        if(oldOrd != null)
            param.put(MarshalConst.COND_OP_OLD_ORDER, oldOrd);
        return param;
    }

    public void setObjs(Map objs)
    {
        int typeInt = ((Integer)objs.get(MarshalConst.COND_OP_TYPE)).intValue();
        type = (CondOrderOpType)CondOrderOpType.getEnum(typeInt);
        oldOrd = (CondOrder)objs.get(MarshalConst.COND_OP_OLD_ORDER);
        newOrd = (CondOrder)objs.get(MarshalConst.COND_OP_ORDER);
    }

    public void onMarshal()
    {
    }

    public void onDemarshal()
    {
    }

    public static final short GUID = 101;
    private CondOrderOpType type;
    private CondOrder oldOrd;
    private CondOrder newOrd;
}
