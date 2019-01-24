// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.kernel;

import java.util.EventListener;

public interface KernelLifeCycleListener
    extends EventListener
{

    public abstract void stateChanged(int i);
}
