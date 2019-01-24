// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   CondOrderOperation.java

package co.fin.intellioms.rulesys;

import co.fin.intellioms.enums.CondOrderOpType;

// Referenced classes of package com.com.fin.intellioms.rulesys:
//            CondOrder

public interface CondOrderOperation
{

    public abstract CondOrderOpType getOpType();

    public abstract CondOrder getCondOrder();

    public abstract CondOrder getOldCondOrder();
}
