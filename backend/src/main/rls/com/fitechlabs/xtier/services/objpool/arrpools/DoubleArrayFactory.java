// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.objpool.arrpools;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.objpool.ObjectPoolFactory;
import com.fitechlabs.xtier.utils.ArgAssert;

// Referenced classes of package com.fitechlabs.xtier.services.objpool.arrpools:
//            DoubleArray

public class DoubleArrayFactory
    implements ObjectPoolFactory
{

    public DoubleArrayFactory(int i)
    {
        ArgAssert.illegalArg(i >= 0, "size");
        size = i;
    }

    public Object createObj()
    {
        return new DoubleArray(size);
    }

    public void disposeObj(Object obj)
    {
    }

    public String toString()
    {
        return L10n.format("SRVC.OBJPOOL.TXT9", new Integer(size));
    }

    private int size;
}
