// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.objpool.threads;


// Referenced classes of package com.fitechlabs.xtier.services.objpool.threads:
//            ThreadPoolStats

public interface ThreadPoolResizePolicy
{

    public abstract int addBeforeTaskEnqueued(ThreadPoolStats threadpoolstats);

    public abstract int removeAfterTaskFinished(ThreadPoolStats threadpoolstats);
}
