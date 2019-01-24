// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   BizDateProvider.java

package co.fin.intellioms.rulesys;

import java.util.Date;

public interface BizDateProvider
{

    public abstract Date getCurrentDate();

    public abstract long getCurrentTimeMillis();

    public abstract long getTimeShift();
}
