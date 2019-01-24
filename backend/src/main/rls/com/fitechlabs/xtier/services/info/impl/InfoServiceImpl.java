// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.info.impl;

import com.fitechlabs.xtier.kernel.mbean.DynamicMBeanFactory;
import com.fitechlabs.xtier.kernel.mbean.DynamicXtierMBean;
import com.fitechlabs.xtier.kernel.spi.ServiceProviderAdapter;
import com.fitechlabs.xtier.kernel.spi.ServiceProviderException;
import com.fitechlabs.xtier.microkernel.MicroKernelContext;
import com.fitechlabs.xtier.services.info.InfoService;
import java.net.InetSocketAddress;
import javax.management.JMException;

// Referenced classes of package com.fitechlabs.xtier.services.info.impl:
//            XmlConfigViewer

public class InfoServiceImpl extends ServiceProviderAdapter
    implements InfoService
{

    public InfoServiceImpl()
    {
        mbeans = null;
    }

    public String getName()
    {
        return "info";
    }

    public int getRmiRegPort()
    {
        return getMicroKernelContext().getRmiRegPort();
    }

    public InetSocketAddress getLocalBind()
    {
        return getLocalBind();
    }

    public String getKernelRegionConfigPath()
    {
        return getConfigPath();
    }

    public String getKernelRegionFeatureSet()
    {
        return getFeatureSet();
    }

    public String getKernelRegionName()
    {
        return getRegionName();
    }

    public String getSupportEmail()
    {
        return "support@fitechlabs.com";
    }

    public String getVendorAddress()
    {
        return "Century Plaza, Suite 405, 1065 East Hillsdale Boulevard, Foster City, CA 94404 USA";
    }

    public String getVendorName()
    {
        return "Fitech Laboratories, Inc.";
    }

    public String getVendorWebSite()
    {
        return "http://www.fitechlabs.com";
    }

    public String getXtierCopyright()
    {
        return "Copyright 2000-2005 Fitech Laboratories, Inc. All Rights Reserved.";
    }

    public String getXtierVersion()
    {
        int ai[] = getVersion();
        return "" + ai[0] + '.' + ai[1] + '.' + ai[2] + '.' + ai[3];
    }

    public String getXtierWebSite()
    {
        return "http://www.fitechlabs.com";
    }

    protected DynamicXtierMBean[] getMBeans()
        throws JMException
    {
        if(mbeans == null)
            mbeans = (new DynamicXtierMBean[] {
                DynamicMBeanFactory.createDynMBean(new XmlConfigViewer(getXtierRoot(), getConfigPath()), com.fitechlabs.xtier.services.info.impl.XmlConfigViewerMBean.class)
            });
        return mbeans;
    }

    public String getXtierInstallRoot()
    {
        return getMicroKernelContext().getXtierRoot();
    }

    protected void onStart()
        throws ServiceProviderException
    {
    }

    protected void onStop()
        throws ServiceProviderException
    {
    }

    private DynamicXtierMBean mbeans[];
}
