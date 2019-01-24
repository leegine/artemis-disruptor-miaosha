// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.db.multipool;


// Referenced classes of package com.fitechlabs.xtier.services.db.multipool:
//            PoolFailoverCallback

public class BasicFailoverCallback
    implements PoolFailoverCallback
{

    public BasicFailoverCallback()
    {
    }

    public int allowFailover(int i, String s, String s1)
    {
        return s1 == null ? 1 : 0;
    }
}
