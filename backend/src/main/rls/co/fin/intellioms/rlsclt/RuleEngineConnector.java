// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   RuleEngineConnector.java

package co.fin.intellioms.rlsclt;

import co.fin.intellioms.rulesys.CondOrder;
import co.fin.intellioms.rulesys.CondOrderOperation;
import co.fin.intellioms.util.Startable;

// Referenced classes of package com.com.fin.intellioms.rlsclt:
//            RuleEngineConnectorException, RuleEngineReplyListener

public interface RuleEngineConnector
    extends Startable
{

    public abstract void registerCondOrder(CondOrder condorder)
        throws RuleEngineConnectorException;

    public abstract void submitCondOrderOperation(CondOrderOperation condorderoperation)
        throws RuleEngineConnectorException;

    /**
     * @deprecated Method setReplyListener is deprecated
     */

    public abstract void setReplyListener(RuleEngineReplyListener ruleenginereplylistener);
}
