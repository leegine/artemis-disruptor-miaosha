// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.startup;

import com.fitechlabs.xtier.kernel.KernelService;
import java.util.Map;

public interface StartupService
    extends KernelService
{

    public abstract Map getStartables();

    public abstract Object getStartable(String s);
}
