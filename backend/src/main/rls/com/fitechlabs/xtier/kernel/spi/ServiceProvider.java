// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.kernel.spi;

import com.fitechlabs.xtier.kernel.KernelService;
import java.util.Map;

// Referenced classes of package com.fitechlabs.xtier.kernel.spi:
//            ServiceProviderException

public interface ServiceProvider
    extends KernelService
{

    public abstract void start(Map map)
        throws ServiceProviderException;

    public abstract void stop()
        throws ServiceProviderException;
}
