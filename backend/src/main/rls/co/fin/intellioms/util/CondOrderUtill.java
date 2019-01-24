// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   CondOrderUtill.java

package co.fin.intellioms.util;

import co.fin.intellioms.rulesys.CondOrder;
import co.fin.intellioms.rulesys.Ratio;
import java.util.List;

public class CondOrderUtill
{

    public CondOrderUtill()
    {
    }

    public static Ratio findLargestExecutedRatio(List subOrds)
    {
        if(subOrds == null)
            return null;
        Ratio ratio = null;
        for(int i = 0; i < subOrds.size(); i++)
        {
            CondOrder subord = (CondOrder)subOrds.get(i);
            if(ratio == null || subord.getExecutedRatio().isAbove(ratio))
                ratio = subord.getExecutedRatio();
        }

        return new Ratio(ratio);
    }
}
