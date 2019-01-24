// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.os.impl;

import com.fitechlabs.xtier.kernel.spi.ServiceProviderAdapter;
import com.fitechlabs.xtier.kernel.spi.ServiceProviderException;
import com.fitechlabs.xtier.services.os.OsService;
import com.fitechlabs.xtier.utils.Utils;

public class OsServiceImpl extends ServiceProviderAdapter
    implements OsService
{

    public OsServiceImpl()
    {
        win95 = false;
        win98 = false;
        winNT = false;
        win2k = false;
        winXP = false;
        win2003 = false;
        unix = false;
        solaris = false;
        linux = false;
        netware = false;
        mac = false;
        SPARC = false;
        x86 = false;
        osName = null;
        osVersion = null;
        osArch = null;
        jdkVendor = null;
        jdkName = null;
        jdkVersion = null;
    }

    protected void onStart()
    {
        osName = System.getProperty("os.name");
        String s = osName.toLowerCase();
        if(s.indexOf("win") != -1)
        {
            if(s.indexOf("95") != -1)
                win95 = true;
            else
            if(s.indexOf("98") != -1)
                win98 = true;
            else
            if(s.indexOf("nt") != -1)
                winNT = true;
            else
            if(s.indexOf("2000") != -1)
                win2k = true;
            else
            if(s.indexOf("xp") != -1)
                winXP = true;
            else
                win2003 = true;
        } else
        if(s.indexOf("netware") != -1)
        {
            netware = true;
        } else
        {
            String as[] = {
                "ix", "inux", "olaris", "un", "ux", "sco", "bsd", "att"
            };
            int i = 0;
            do
            {
                if(i >= as.length)
                    break;
                if(s.indexOf(as[i]) != -1)
                {
                    unix = true;
                    break;
                }
                i++;
            } while(true);
            if(s.indexOf("olaris") != -1)
                solaris = true;
            else
            if(s.indexOf("inux") != -1)
                linux = true;
        }
        osArch = System.getProperty("os.arch");
        String s1 = osArch.toLowerCase();
        if(s1.indexOf("x86") != -1)
            x86 = true;
        else
        if(s1.indexOf("sparc") != -1)
            SPARC = true;
        jdkVendor = System.getProperty("java.specification.vendor");
        jdkName = System.getProperty("java.specification.name");
        jdkVersion = System.getProperty("java.specification.version");
        osVersion = System.getProperty("os.version");
        fullEnvDesc = s + " ver. " + osVersion + ", " + jdkVendor + ' ' + jdkName + " ver. " + jdkVersion;
    }

    protected void onStop()
        throws ServiceProviderException
    {
    }

    public String getName()
    {
        return "os";
    }

    public String getFullEnvDesc()
    {
        return fullEnvDesc;
    }

    public int getNumberOfCpus()
    {
        return Utils.getNumberOfCpus();
    }

    public boolean isLinux()
    {
        return linux;
    }

    public String getJdkName()
    {
        return jdkName;
    }

    public String getJdkVendor()
    {
        return jdkVendor;
    }

    public String getJdkVersion()
    {
        return jdkVersion;
    }

    public String getOsArch()
    {
        return osArch;
    }

    public String getOsName()
    {
        return osName;
    }

    public String getOsVersion()
    {
        return osVersion;
    }

    public boolean isMacOs()
    {
        return mac;
    }

    public boolean isNetWare()
    {
        return netware;
    }

    public boolean isSolaris()
    {
        return solaris;
    }

    public boolean isSolarisSPARC()
    {
        return solaris && SPARC;
    }

    public boolean isSolarisX86()
    {
        return solaris && x86;
    }

    public boolean isUnix()
    {
        return unix;
    }

    public boolean isWindows()
    {
        return winXP || win95 || win98 || winNT || win2k || win2003;
    }

    public boolean isWindows2k()
    {
        return win2k;
    }

    public boolean isWindows2003()
    {
        return win2003;
    }

    public boolean isWindows95()
    {
        return win95;
    }

    public boolean isWindows98()
    {
        return win98;
    }

    public boolean isWindowsNT()
    {
        return winNT;
    }

    public boolean isWindowsXP()
    {
        return winXP;
    }

    private String fullEnvDesc;
    private boolean win95;
    private boolean win98;
    private boolean winNT;
    private boolean win2k;
    private boolean winXP;
    private boolean win2003;
    private boolean unix;
    private boolean solaris;
    private boolean linux;
    private boolean netware;
    private boolean mac;
    private boolean SPARC;
    private boolean x86;
    private String osName;
    private String osVersion;
    private String osArch;
    private String jdkVendor;
    private String jdkName;
    private String jdkVersion;
}
