// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.kernel;

import java.util.EventListener;

// Referenced classes of package com.fitechlabs.xtier.kernel:
//            KernelServiceException

public interface KernelServiceListener
    extends EventListener
{

    public abstract void afterStart(String s)
        throws KernelServiceException;

    public abstract void beforeStop(String s)
        throws KernelServiceException;
}
