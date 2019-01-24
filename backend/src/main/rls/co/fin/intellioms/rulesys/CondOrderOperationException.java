// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   CondOrderOperationException.java

package co.fin.intellioms.rulesys;


// Referenced classes of package com.com.fin.intellioms.rulesys:
//            CondOrderOperation

public class CondOrderOperationException extends Exception
{

    public CondOrderOperationException(CondOrderOperation op, int errType)
    {
        this.op = op;
        this.errType = errType;
    }

    public CondOrderOperationException(String message, Throwable cause, CondOrderOperation op, int errType)
    {
        super(message, cause);
        this.op = op;
        this.errType = errType;
    }

    public CondOrderOperationException(String message, CondOrderOperation op, int errType)
    {
        super(message);
        this.op = op;
        this.errType = errType;
    }

    public CondOrderOperationException(Throwable cause, CondOrderOperation op, int errType)
    {
        super(cause);
        this.op = op;
        this.errType = errType;
    }

    public CondOrderOperation getOperation()
    {
        return op;
    }

    public String getMessage()
    {
        return (new StringBuilder()).append(super.getMessage()).append(" ").append(op).toString();
    }

    public int getErrType()
    {
        return errType;
    }

    public static final int OTHER_ERR = 0;
    public static final int NETWORK_ERR = 1;
    public static final int NO_ORDER_ID = 2;
    public static final int DUP_ORDER_ID = 3;
    public static final int TOO_LATE = 4;
    private final int errType;
    private final CondOrderOperation op;
}
