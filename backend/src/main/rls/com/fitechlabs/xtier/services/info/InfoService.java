// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.info;

import com.fitechlabs.xtier.kernel.KernelService;
import java.net.InetSocketAddress;

public interface InfoService
    extends KernelService
{

    public abstract String getVendorName();

    public abstract String getVendorAddress();

    public abstract String getVendorWebSite();

    public abstract String getSupportEmail();

    public abstract String getXtierVersion();

    public abstract String getXtierCopyright();

    public abstract String getXtierWebSite();

    public abstract String getKernelRegionConfigPath();

    public abstract String getKernelRegionFeatureSet();

    public abstract String getXtierInstallRoot();

    public abstract String getKernelRegionName();

    public abstract int getRmiRegPort();

    public abstract InetSocketAddress getLocalBind();
}
