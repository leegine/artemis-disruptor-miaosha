// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.ioc;

import com.fitechlabs.xtier.kernel.KernelService;
import java.util.Map;

// Referenced classes of package com.fitechlabs.xtier.services.ioc:
//            IocServiceException, IocObjectProxy

public interface IocService
    extends KernelService
{

    public abstract Object makeIocObject(String s)
        throws IocServiceException;

    public abstract Object makeIocObject(String s, Object obj)
        throws IocServiceException;

    public abstract IocObjectProxy getIocProxy(String s);

    public abstract Map getIocProxies();
}
