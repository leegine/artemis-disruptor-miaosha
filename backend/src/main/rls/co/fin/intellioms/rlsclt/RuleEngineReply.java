// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   RuleEngineReply.java

package co.fin.intellioms.rlsclt;

import co.fin.intellioms.rulesys.CondOrderOperation;

/**
 * @deprecated Interface RuleEngineReply is deprecated
 */

public interface RuleEngineReply
{

    public abstract int getReplyType();

    public abstract Object getValue();

    public abstract CondOrderOperation getCondOrderOperation();

    public static final int ERROR_REPLY = 1;
    public static final int OTHER_REPLY = 2;
}
