// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.utils.concurrent;


// Referenced classes of package com.fitechlabs.xtier.utils.concurrent:
//            RWLock

public interface ReentrantRWLock
    extends RWLock
{

    public abstract boolean holdsWriteLock();

    public abstract boolean holdsReadLock();
}
