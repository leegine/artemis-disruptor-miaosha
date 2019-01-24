// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.grid.routers;

import com.fitechlabs.xtier.services.cluster.ClusterNode;
import com.fitechlabs.xtier.services.grid.*;
import java.util.*;

public class GridTaskBasicRouter
    implements GridTaskRouter
{

    public GridTaskBasicRouter()
    {
        random = new Random();
    }

    public ClusterNode route(GridTaskSplitRef gridtasksplitref, Set set, Set set1, GridTaxonomy gridtaxonomy)
    {
        if(!$assertionsDisabled && (gridtasksplitref == null || set == null || set1 == null || set1.isEmpty()))
            throw new AssertionError();
        int i = random.nextInt(set1.size());
        Iterator iterator = set1.iterator();
        for(int j = 0; j < i; j++)
            iterator.next();

        return (ClusterNode)iterator.next();
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

    private Random random;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(GridTaskBasicRouter.class).desiredAssertionStatus();
    }
}
