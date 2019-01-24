// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   BARecord.java

package co.fin.intellioms.quote;

import co.fin.intellioms.omsclt.Price;
import co.fin.intellioms.omsclt.Quantity;

public interface BARecord
{

    public abstract Price getPrice();

    public abstract Quantity getQuantity();
}
