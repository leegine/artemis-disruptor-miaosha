// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.grid.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.cluster.ClusterNode;
import com.fitechlabs.xtier.services.grid.GridTaskUnitSplitResult;
import com.fitechlabs.xtier.services.grid.GridUtils;
import com.fitechlabs.xtier.services.grid.adapters.GridTaskUnitResultAdapter;
import com.fitechlabs.xtier.services.marshal.Marshallable;

class GridTaskUnitSplitResultImpl extends GridTaskUnitResultAdapter
    implements GridTaskUnitSplitResult
{

    GridTaskUnitSplitResultImpl(int i, Marshallable marshallable, ClusterNode clusternode, int j, long l, long l1, long l2, long l3, long l4,
            long l5)
    {
        super(i, marshallable, j);
        node = null;
        sendReqTime = 0L;
        recvReqTime = 0L;
        sendResTime = 0L;
        recvResTime = 0L;
        beginExecTime = 0L;
        endExecTime = 0L;
        if(!$assertionsDisabled && clusternode == null)
        {
            throw new AssertionError();
        } else
        {
            node = clusternode;
            sendReqTime = l;
            recvReqTime = l1;
            sendResTime = l2;
            recvResTime = l3;
            beginExecTime = l4;
            endExecTime = l5;
            return;
        }
    }

    GridTaskUnitSplitResultImpl(int i, Marshallable marshallable, ClusterNode clusternode, long l, long l1,
            long l2, long l3, long l4, long l5)
    {
        super(i, marshallable);
        node = null;
        sendReqTime = 0L;
        recvReqTime = 0L;
        sendResTime = 0L;
        recvResTime = 0L;
        beginExecTime = 0L;
        endExecTime = 0L;
        if(!$assertionsDisabled && clusternode == null)
        {
            throw new AssertionError();
        } else
        {
            node = clusternode;
            sendReqTime = l;
            recvReqTime = l1;
            sendResTime = l2;
            recvResTime = l3;
            beginExecTime = l4;
            endExecTime = l5;
            return;
        }
    }

    public ClusterNode getClusterNode()
    {
        return node;
    }

    public long getBeginExecTime()
    {
        return beginExecTime;
    }

    public long getEndExecTime()
    {
        return endExecTime;
    }

    public long getRecvReqTime()
    {
        return recvReqTime;
    }

    public long getRecvResTime()
    {
        return recvResTime;
    }

    public long getSendReqTime()
    {
        return sendReqTime;
    }

    public long getSendResTime()
    {
        return sendResTime;
    }

    public String toString()
    {
        if(!$assertionsDisabled)
            throw new AssertionError();
        else
            return L10n.format("SRVC.GRID.TXT8", new Object[] {
                GridUtils.getStrRetCode(getReturnCode()), getReturnValue(), new Integer(getUserErrorCode()), node, new Long(sendReqTime), new Long(recvReqTime), new Long(sendResTime), new Long(recvResTime), new Long(beginExecTime), new Long(endExecTime)
            });
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

    private ClusterNode node;
    private long sendReqTime;
    private long recvReqTime;
    private long sendResTime;
    private long recvResTime;
    private long beginExecTime;
    private long endExecTime;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(GridTaskUnitSplitResultImpl.class).desiredAssertionStatus();
    }
}
