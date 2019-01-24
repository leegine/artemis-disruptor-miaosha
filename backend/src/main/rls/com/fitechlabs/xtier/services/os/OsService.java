// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.os;

import com.fitechlabs.xtier.kernel.KernelService;

public interface OsService
    extends KernelService
{

    public abstract String getOsName();

    public abstract String getOsVersion();

    public abstract String getOsArch();

    public abstract String getJdkVendor();

    public abstract String getJdkVersion();

    public abstract String getJdkName();

    public abstract boolean isWindows();

    public abstract boolean isWindows95();

    public abstract boolean isWindows98();

    public abstract boolean isWindowsXP();

    public abstract boolean isWindows2003();

    public abstract boolean isWindowsNT();

    public abstract boolean isWindows2k();

    public abstract boolean isNetWare();

    public abstract boolean isUnix();

    public abstract boolean isSolaris();

    public abstract boolean isSolarisSPARC();

    public abstract boolean isSolarisX86();

    public abstract boolean isLinux();

    public abstract boolean isMacOs();

    public abstract int getNumberOfCpus();

    public abstract String getFullEnvDesc();
}
