// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.log;

import com.fitechlabs.xtier.kernel.KernelService;

// Referenced classes of package com.fitechlabs.xtier.services.log:
//            Logger

public interface LogService
    extends KernelService
{

    public abstract Logger getLogger();

    public abstract Logger getLogger(String s);
}
