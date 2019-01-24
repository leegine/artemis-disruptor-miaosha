// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.cache.impl.app;

import com.fitechlabs.xtier.services.cache.impl.CacheChannel;
import com.fitechlabs.xtier.services.cache.impl.CacheConfig;
import java.util.ArrayList;
import java.util.List;

public class CacheAppConfig extends CacheConfig
{

    public CacheAppConfig()
    {
        super(1);
        channels = new ArrayList();
    }

    public int getPhaseOneReplyPort()
    {
        return phaseOneReplyPort;
    }

    public void setPhaseOneReplyPort(int i)
    {
        phaseOneReplyPort = i;
    }

    public int getPhaseTwoPort()
    {
        return phaseTwoPort;
    }

    public void setPhaseTwoPort(int i)
    {
        phaseTwoPort = i;
    }

    public List getChannels()
    {
        return channels;
    }

    public boolean isDeadlockDetectionOn()
    {
        return isDeadlockDetectionOn;
    }

    public void setDeadlockDetection(boolean flag)
    {
        isDeadlockDetectionOn = flag;
    }

    public long getTxDeadlockTimeout()
    {
        return txDeadlockTimeout;
    }

    public void setTxDeadlockTimeout(long l)
    {
        txDeadlockTimeout = l;
    }

    public void addChannel(CacheChannel cachechannel)
    {
        if(!$assertionsDisabled && cachechannel == null)
            throw new AssertionError();
        if(!$assertionsDisabled && channels.contains(cachechannel))
        {
            throw new AssertionError();
        } else
        {
            channels.add(cachechannel);
            return;
        }
    }

    public void setDeadlockReplyPort(int i)
    {
        deadlockReplyPort = i;
    }

    public int getDeadlockReplyPort()
    {
        return deadlockReplyPort;
    }

    static Class _mthclass$(String s)
    {
        try
        {
            return Class.forName(s);
        }
        catch(ClassNotFoundException classnotfoundexception)
        {
            throw (new NoClassDefFoundError()).initCause(classnotfoundexception);
        }
    }

    private int phaseOneReplyPort;
    private int phaseTwoPort;
    private List channels;
    private int deadlockReplyPort;
    private long txDeadlockTimeout;
    private boolean isDeadlockDetectionOn;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(CacheAppConfig.class).desiredAssertionStatus();
    }
}
