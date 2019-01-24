// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.cache.impl.pools;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.objpool.ObjectPoolException;
import com.fitechlabs.xtier.services.objpool.ObjectPoolFactory;
import com.fitechlabs.xtier.utils.Utils;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

// Referenced classes of package com.fitechlabs.xtier.services.cache.impl.pools:
//            CacheDgramChannel

public class CacheDgramChannelFactory
    implements ObjectPoolFactory
{

    public CacheDgramChannelFactory(InetSocketAddress inetsocketaddress)
    {
        if(!$assertionsDisabled && inetsocketaddress == null)
        {
            throw new AssertionError();
        } else
        {
            localBind = inetsocketaddress;
            return;
        }
    }

    public Object createObj()
        throws ObjectPoolException
    {
        try
        {
            DatagramChannel datagramchannel = DatagramChannel.open();
            datagramchannel.socket().bind(localBind);
            return new CacheDgramChannel(datagramchannel);
        }
        catch(IOException ioexception)
        {
            throw new ObjectPoolException(L10n.format("SRVC.CACHE.ERR13"), ioexception);
        }
    }

    public void disposeObj(Object obj)
    {
        Utils.close(((CacheDgramChannel)obj).getChannel());
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

    private InetSocketAddress localBind;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(CacheDgramChannelFactory.class).desiredAssertionStatus();
    }
}
