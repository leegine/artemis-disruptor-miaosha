// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   DefaultBizDateProvider.java

package co.fin.intellioms.rulesys.impl;

import co.fin.intellioms.rulesys.BizDateProvider;
import com.fitechlabs.xtier.utils.ArgAssert;
import java.util.Date;

public class DefaultBizDateProvider
    implements BizDateProvider
{

    public DefaultBizDateProvider()
    {
        shift = 0L;
    }

    public DefaultBizDateProvider(long shift)
    {
        this.shift = shift;
    }

    public DefaultBizDateProvider(Date date)
    {
        ArgAssert.nullArg(date, "date");
        shift = date.getTime() - System.currentTimeMillis();
    }

    public Date getCurrentDate()
    {
        return new Date(System.currentTimeMillis() + shift);
    }

    public long getCurrentTimeMillis()
    {
        return System.currentTimeMillis() + shift;
    }

    public long getTimeShift()
    {
        return shift;
    }

    public String toString()
    {
        return (new StringBuilder()).append("Default biz date provider [current-date=").append(getCurrentDate()).append(", time-shift=").append(shift).append(']').toString();
    }

    private long shift;
}
