// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   RuleEngineConnectorException.java

package co.fin.intellioms.rlsclt;


public class RuleEngineConnectorException extends Exception
{

    public RuleEngineConnectorException(int errType)
    {
        this.errType = errType;
    }

    public RuleEngineConnectorException(String message, Throwable cause, int errType)
    {
        super(message, cause);
        this.errType = errType;
    }

    public RuleEngineConnectorException(String message, int errType)
    {
        super(message);
        this.errType = errType;
    }

    public RuleEngineConnectorException(Throwable cause, int errType)
    {
        super(cause);
        this.errType = errType;
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
}
