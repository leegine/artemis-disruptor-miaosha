// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   ChainCondition.java

package co.fin.intellioms.chain;


public class ChainCondition
{

    public ChainCondition(boolean incremental)
    {
        this.incremental = incremental;
    }

    public boolean isIncremental()
    {
        return incremental;
    }

    public String toString()
    {
        return (new StringBuilder()).append("ChainCondition [incremental=").append(incremental).append("]").toString();
    }

    private boolean incremental;
}
