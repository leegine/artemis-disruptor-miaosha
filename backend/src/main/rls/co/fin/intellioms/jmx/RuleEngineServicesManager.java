// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   RuleEngineServicesManager.java

package co.fin.intellioms.jmx;

import co.fin.intellioms.util.ServicesManager;
import com.fitechlabs.xtier.kernel.KernelException;

// Referenced classes of package com.com.fin.intellioms.jmx:
//            RuleEngineServicesManagerMBean

public class RuleEngineServicesManager
    implements RuleEngineServicesManagerMBean
{

    public RuleEngineServicesManager(ServicesManager mngr)
    {
        this.mngr = mngr;
    }

    public void terminate()
        throws KernelException
    {
        mngr.terminate();
    }

    private final ServicesManager mngr;
}
