// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   AccountIDRange.java

package co.fin.intellioms.account;


public class AccountIDRange
{

    public AccountIDRange(long start, long end)
    {
        if(!$assertionsDisabled && start > end)
        {
            throw new AssertionError((new StringBuilder()).append("Start value must be below or equals to end value [start=").append(start).append(", end=").append(end).append(']').toString());
        } else
        {
            this.end = end;
            this.start = start;
            return;
        }
    }

    public long getEnd()
    {
        return end;
    }

    public long getStart()
    {
        return start;
    }

    public String toString()
    {
        return (new StringBuilder()).append("AccountIDRange [start=").append(start).append(", end=").append(end).append(']').toString();
    }

    private final long start;
    private final long end;
    static final boolean $assertionsDisabled = !com/ com /fin/intellioms/account/AccountIDRange.desiredAssertionStatus();

}
