// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.cache.impl.app;

import com.fitechlabs.xtier.services.cache.impl.CacheRegistry;

// Referenced classes of package com.fitechlabs.xtier.services.cache.impl.app:
//            CacheAppCommManager, CacheAppTxManager, CacheAppConfig

public class CacheAppRegistry extends CacheRegistry
{

    public CacheAppRegistry()
    {
    }

    CacheAppCommManager getAppCommMgr()
    {
        return (CacheAppCommManager)getCommMgr();
    }

    CacheAppTxManager getAppTxMgr()
    {
        return (CacheAppTxManager)getTxMgr();
    }

    CacheAppConfig getAppConfig()
    {
        return (CacheAppConfig)getConfig();
    }
}
