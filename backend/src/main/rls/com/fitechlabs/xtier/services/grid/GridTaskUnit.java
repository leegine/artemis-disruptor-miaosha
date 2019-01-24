// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.grid;

import com.fitechlabs.xtier.services.marshal.Marshallable;
import java.util.Set;

// Referenced classes of package com.fitechlabs.xtier.services.grid:
//            GridTaskUnitResult, GridTaxonomy

public interface GridTaskUnit
{

    public abstract GridTaskUnitResult exec(Marshallable marshallable);

    public abstract Set split(Set set, GridTaxonomy gridtaxonomy, Marshallable marshallable);

    public abstract GridTaskUnitResult aggregate(Set set);

    public static final int ROOT_TASK_UNIT_ID = 0x7fffffff;
}
