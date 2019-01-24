// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   RuleEngineServicesManagerMBean.java

package co.fin.intellioms.jmx;

import com.fitechlabs.xtier.kernel.KernelException;

public interface RuleEngineServicesManagerMBean
{

    public abstract void terminate()
        throws KernelException;
}
