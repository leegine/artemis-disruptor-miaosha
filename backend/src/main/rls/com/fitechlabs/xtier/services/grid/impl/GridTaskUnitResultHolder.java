// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.grid.impl;

import com.fitechlabs.xtier.services.grid.GridTaskUnitSplitResult;

// Referenced classes of package com.fitechlabs.xtier.services.grid.impl:
//            GridTaskSplitRefHolder

class GridTaskUnitResultHolder
{

    GridTaskUnitResultHolder(GridTaskUnitSplitResult gridtaskunitsplitresult, GridTaskSplitRefHolder gridtasksplitrefholder)
    {
        if(!$assertionsDisabled && (gridtaskunitsplitresult == null || gridtasksplitrefholder == null))
        {
            throw new AssertionError();
        } else
        {
            result = gridtaskunitsplitresult;
            refHolder = gridtasksplitrefholder;
            return;
        }
    }

    GridTaskSplitRefHolder getRefHolder()
    {
        return refHolder;
    }

    GridTaskUnitSplitResult getResult()
    {
        return result;
    }

    public String toString()
    {
        if(!$assertionsDisabled)
            throw new AssertionError();
        else
            return "Grid task unit result holder [result=" + result + ", ref-holder=" + refHolder + ']';
    }

    static Class _mthclass$(String s)
    {
        try
        {
            return Class.forName(s);
        }
        catch(ClassNotFoundException classnotfoundexception)
        {
            throw (new NoClassDefFoundError()).initCause(classnotfoundexception);
        }
    }

    private GridTaskUnitSplitResult result;
    private GridTaskSplitRefHolder refHolder;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(GridTaskUnitResultHolder.class).desiredAssertionStatus();
    }
}
