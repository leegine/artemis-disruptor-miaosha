// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.grid;


// Referenced classes of package com.fitechlabs.xtier.services.grid:
//            GridTaskSplitRef, GridTaskUnitSplitResult

public interface GridTaskFailoverResolver
{

    public abstract int resolve(GridTaskSplitRef gridtasksplitref, GridTaskUnitSplitResult gridtaskunitsplitresult);

    public static final int TASK_UNIT_TERMINATE = 1;
    public static final int TASK_UNIT_RETRY_SAME = 2;
    public static final int TASK_UNIT_RETRY_OTHER = 3;
}
