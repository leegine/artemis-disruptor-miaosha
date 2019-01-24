// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   RuleEngineException.java

package co.fin.intellioms.rulesys;


public class RuleEngineException extends Exception
{

    public RuleEngineException()
    {
    }

    public RuleEngineException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public RuleEngineException(String message)
    {
        super(message);
    }

    public RuleEngineException(Throwable cause)
    {
        super(cause);
    }
}
