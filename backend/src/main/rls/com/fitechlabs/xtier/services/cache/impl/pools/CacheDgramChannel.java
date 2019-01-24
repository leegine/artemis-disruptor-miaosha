// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.cache.impl.pools;

import com.fitechlabs.xtier.services.objpool.adapters.PoolObjectAbstractAdapter;
import java.nio.channels.DatagramChannel;

public class CacheDgramChannel extends PoolObjectAbstractAdapter
{

    CacheDgramChannel(DatagramChannel datagramchannel)
    {
        if(!$assertionsDisabled && datagramchannel == null)
        {
            throw new AssertionError();
        } else
        {
            channel = datagramchannel;
            return;
        }
    }

    public DatagramChannel getChannel()
    {
        return channel;
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

    private DatagramChannel channel;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(CacheDgramChannel.class).desiredAssertionStatus();
    }
}
