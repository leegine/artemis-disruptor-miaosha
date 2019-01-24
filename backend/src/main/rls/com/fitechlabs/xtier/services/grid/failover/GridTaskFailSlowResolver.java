// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.grid.failover;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.grid.*;

public class GridTaskFailSlowResolver
    implements GridTaskFailoverResolver
{

    public GridTaskFailSlowResolver()
    {
    }

    public int resolve(GridTaskSplitRef gridtasksplitref, GridTaskUnitSplitResult gridtaskunitsplitresult)
    {
        if(!$assertionsDisabled && (gridtasksplitref == null || gridtaskunitsplitresult == null))
            throw new AssertionError();
        switch(gridtaskunitsplitresult.getReturnCode())
        {
        case 1001:
            if(!$assertionsDisabled)
                throw new AssertionError("TASK_UNIT_OK should never be here.");
            else
                return 0;

        case 4: // '\004'
        case 5: // '\005'
        case 6: // '\006'
        case 9: // '\t'
        case 11: // '\013'
            return 1;

        case 2: // '\002'
        case 7: // '\007'
        case 8: // '\b'
        case 12: // '\f'
            return 3;

        case 3: // '\003'
            return 2;

        case 1: // '\001'
            throw new IllegalStateException(L10n.format("SRVC.GRID.ERR31"));
        }
        if(!$assertionsDisabled)
            throw new AssertionError("Unknown return code in: " + gridtaskunitsplitresult);
        else
            return 0;
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

    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(GridTaskFailSlowResolver.class).desiredAssertionStatus();
    }
}
