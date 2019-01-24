// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   ConnectorImpl.java

package co.fin.intellioms.rlsclt.impl;

import co.fin.intellioms.marshal.Fields;
import co.fin.intellioms.rulesys.CondOrder;
import co.fin.intellioms.rulesys.CondOrderOperation;
import co.fin.intellioms.rulesys.impl.CondOrderOperationImpl;
import co.fin.intellioms.account.AccountsManager;
import co.fin.intellioms.enums.CondOrderOpType;
import com.fitechlabs.fin.intellioms.rlsclt.*;
import com.fitechlabs.xtier.services.marshal.MarshalObject;
import com.fitechlabs.xtier.services.marshal.Marshallable;

// Referenced classes of package com.com.fin.intellioms.rlsclt.impl:
//            AbstractConnector

public class ConnectorImpl extends AbstractConnector
    implements RuleEngineConnector
{

    /**
     * @deprecated Method ConnectorImpl is deprecated
     */

    public ConnectorImpl(AccountsManager accountsManager, long discoverInterval)
    {
        super(accountsManager, discoverInterval);
    }

    public ConnectorImpl(AccountsManager accountsManager, int retries, long retryInterval)
    {
        super(accountsManager, retries, retryInterval);
    }

    public ConnectorImpl(AccountsManager accountsManager)
    {
        super(accountsManager);
    }

    public void registerCondOrder(CondOrder ord)
        throws RuleEngineConnectorException
    {
        CondOrderOperation op = new CondOrderOperationImpl(CondOrderOpType.NEW, ord, null);
        submitCondOrderOperation(op);
    }

    public void submitCondOrderOperation(CondOrderOperation op)
        throws RuleEngineConnectorException
    {
        if(!$assertionsDisabled && op == null)
            throw new AssertionError("Operation is null.");
        if(!$assertionsDisabled && !(op instanceof Marshallable))
            throw new AssertionError((new StringBuilder()).append(op.getClass().getName()).append(" must implement ").append(com/ com /xtier/services/marshal/Marshallable.getName()).toString());
        Long accId = getAccountId(op);
        MarshalObject opMsg = new MarshalObject();
        opMsg.putInt32(Fields.MSG_TYPE, 4);
        opMsg.putMarshalObj(Fields.OPERATION, (Marshallable)op);
        MarshalObject reply = null;
        reply = send(accId.longValue(), opMsg);
        int type = reply.getInt32(Fields.MSG_TYPE);
        if(type == 6)
        {
            int errType = reply.getInt32(Fields.ERROR_TYPE);
            String msg = reply.getUtf8Str(Fields.ERROR_MSG);
            throw new RuleEngineConnectorException(msg, errType);
        } else
        {
            return;
        }
    }

    /**
     * @deprecated Method setReplyListener is deprecated
     */

    public void setReplyListener(RuleEngineReplyListener ruleenginereplylistener)
    {
    }

    private Long getAccountId(CondOrderOperation op)
    {
        return op.getCondOrder() == null ? new Long(op.getOldCondOrder().getAccountId()) : new Long(op.getCondOrder().getAccountId());
    }

    static final boolean $assertionsDisabled = !com/ com /fin/intellioms/rlsclt/impl/ConnectorImpl.desiredAssertionStatus();

}
