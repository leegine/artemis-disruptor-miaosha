// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.tx.impl;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.cluster.ClusterNode;
import com.fitechlabs.xtier.services.cluster.ClusterService;
import com.fitechlabs.xtier.utils.Utils;
import java.net.InetAddress;
import javax.transaction.xa.Xid;

// Referenced classes of package com.fitechlabs.xtier.services.tx.impl:
//            TxIdImpl

class TxIdFactory
{

    TxIdFactory()
    {
    }

    static synchronized Xid newXid()
    {
        byte abyte0[] = new byte[64];
        if(hostArr == null)
        {
            XtierKernel xtierkernel = XtierKernel.getInstance();
            byte abyte1[] = null;
            int i = 0;
            if(xtierkernel.isStarted("cluster"))
            {
                ClusterNode clusternode = xtierkernel.cluster().getLocalNode();
                abyte1 = clusternode.getAddress().getAddress();
                i = clusternode.getPort();
            } else
            {
                abyte1 = Utils.getLocalIpAddrs()[0].getAddress();
            }
            hostArr = new byte[abyte1.length + 4];
            if(hostArr.length + 8 + 8 > abyte0.length)
                throw new IllegalStateException(L10n.format("SRVC.TX.ERR54"));
            putAsArr(i, hostArr, 0);
            System.arraycopy(abyte1, 0, hostArr, 4, abyte1.length);
        }
        putAsArr(localId++, abyte0, 0);
        putAsArr(System.currentTimeMillis(), abyte0, 8);
        System.arraycopy(hostArr, 0, abyte0, 16, hostArr.length);
        return new TxIdImpl(12609, NO_BRANCH_QUALIF, abyte0);
    }

    static synchronized Xid branchXid(Xid xid, int i)
    {
        return new TxIdImpl(xid.getFormatId(), getByteArr(i), xid.getGlobalTransactionId());
    }

    private static byte[] getByteArr(int i)
    {
        if(!$assertionsDisabled && !Thread.holdsLock(TxIdFactory.class))
            throw new AssertionError();
        else
            return (new byte[] {
                (byte)(i >>> 24 & 0xff), (byte)(i >>> 16 & 0xff), (byte)(i >>> 8 & 0xff), (byte)(i & 0xff)
            });
    }

    private static void putAsArr(int i, byte abyte0[], int j)
    {
        if(!$assertionsDisabled && !Thread.holdsLock(TxIdFactory.class))
        {
            throw new AssertionError();
        } else
        {
            abyte0[j] = (byte)(i >>> 24);
            abyte0[j + 1] = (byte)(i >>> 16);
            abyte0[j + 2] = (byte)(i >>> 8);
            abyte0[j + 3] = (byte)i;
            return;
        }
    }

    private static void putAsArr(long l, byte abyte0[], int i)
    {
        if(!$assertionsDisabled && !Thread.holdsLock(TxIdFactory.class))
        {
            throw new AssertionError();
        } else
        {
            abyte0[i] = (byte)(int)(l >>> 56);
            abyte0[i + 1] = (byte)(int)(l >>> 48);
            abyte0[i + 2] = (byte)(int)(l >>> 40);
            abyte0[i + 3] = (byte)(int)(l >>> 32);
            abyte0[i + 4] = (byte)(int)(l >>> 24);
            abyte0[i + 5] = (byte)(int)(l >>> 16);
            abyte0[i + 6] = (byte)(int)(l >>> 8);
            abyte0[i + 7] = (byte)(int)l;
            return;
        }
    }

    private static long localId = 0x8000000000000000L;
    private static byte hostArr[] = null;
    private static final byte NO_BRANCH_QUALIF[] = new byte[64];
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(TxIdFactory.class).desiredAssertionStatus();
    }
}
