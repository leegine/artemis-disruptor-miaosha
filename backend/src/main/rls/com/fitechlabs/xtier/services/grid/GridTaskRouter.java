// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.grid;

import com.fitechlabs.xtier.services.cluster.ClusterNode;
import java.util.Set;

// Referenced classes of package com.fitechlabs.xtier.services.grid:
//            GridTaskSplitRef, GridTaxonomy

public interface GridTaskRouter
{

    public abstract ClusterNode route(GridTaskSplitRef gridtasksplitref, Set set, Set set1, GridTaxonomy gridtaxonomy);
}
