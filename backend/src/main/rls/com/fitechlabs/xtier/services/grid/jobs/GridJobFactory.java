// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.grid.jobs;

import com.fitechlabs.xtier.services.grid.*;
import com.fitechlabs.xtier.services.grid.adapters.GridTaskSplitRefAdapter;
import com.fitechlabs.xtier.services.grid.adapters.GridTaskUnitAdapter;
import com.fitechlabs.xtier.services.marshal.Marshallable;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.grid.jobs:
//            GridJobBody

public class GridJobFactory
    implements GridTaskUnitFactory
{
    private class NoSplitTaskUnit extends GridTaskUnitAdapter
    {

        public GridTaskUnitResult aggregate(Set set)
        {
            if(!$assertionsDisabled && set.size() != 1)
                throw new AssertionError();
            else
                return (GridTaskUnitResult)set.iterator().next();
        }

        public GridTaskUnitResult exec(Marshallable marshallable)
        {
            if(!$assertionsDisabled && getTaskUnitContext().getUnitId() != 1)
                throw new AssertionError();
            else
                return body.exec(getTaskUnitContext(), marshallable);
        }

        public Set split(Set set, GridTaxonomy gridtaxonomy, Marshallable marshallable)
        {
            GridTaskUnitContext gridtaskunitcontext = getTaskUnitContext();
            if(gridtaskunitcontext.getUnitId() == 0x7fffffff)
            {
                HashSet hashset = new HashSet(1);
                hashset.add(new GridTaskSplitRefAdapter(new GridTaskUnitContext(gridtaskunitcontext.getTaskId(), 1, gridtaskunitcontext.getExecId()), marshallable));
                return hashset;
            }
            if(!$assertionsDisabled && gridtaskunitcontext.getUnitId() != 1)
                throw new AssertionError();
            else
                return null;
        }

        static final boolean $assertionsDisabled; /* synthetic field */


        private NoSplitTaskUnit(GridTaskUnitContext gridtaskunitcontext)
        {
            super(gridtaskunitcontext);
        }

    }


    public GridJobFactory(GridJobBody gridjobbody)
    {
        body = null;
        if(!$assertionsDisabled && gridjobbody == null)
        {
            throw new AssertionError();
        } else
        {
            body = gridjobbody;
            return;
        }
    }

    public GridTaskUnit newTaskUnit(GridTaskUnitContext gridtaskunitcontext)
    {
        return new NoSplitTaskUnit(gridtaskunitcontext);
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

    public static final int NO_SPLIT_UID = 1;
    private GridJobBody body;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(GridJobFactory.class).desiredAssertionStatus();
    }

}
