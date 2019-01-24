// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.ioc;

import java.io.Serializable;

// Referenced classes of package com.fitechlabs.xtier.services.ioc:
//            IocServiceException

public interface IocObjectProxy
    extends Serializable
{

    public abstract String getUid();

    public abstract int getPolicy()
        throws IocServiceException;

    public abstract boolean isReference();

    public abstract String getRefUid();

    public abstract String getJavaImpl();

    public abstract Object makeIocObject()
        throws IocServiceException;

    public abstract Object makeIocObject(Object obj)
        throws IocServiceException;

    public static final int POLICY_SINGLETON = 1;
    public static final int POLICY_NEW = 2;
    public static final int POLICY_KEYED = 3;
}
