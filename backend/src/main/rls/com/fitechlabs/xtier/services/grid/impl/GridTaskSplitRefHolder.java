// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.grid.impl;

import com.fitechlabs.xtier.services.cluster.ClusterNode;
import com.fitechlabs.xtier.services.grid.GridTaskSplitRef;

class GridTaskSplitRefHolder
{

    GridTaskSplitRefHolder(GridTaskSplitRef gridtasksplitref, ClusterNode clusternode)
    {
        sendReqTime = 0L;
        expireTime = -1L;
        if(!$assertionsDisabled && (gridtasksplitref == null || clusternode == null))
        {
            throw new AssertionError();
        } else
        {
            ref = gridtasksplitref;
            node = clusternode;
            return;
        }
    }

    void setSendReqTime(long l)
    {
        sendReqTime = l;
    }

    long getSendReqTime()
    {
        return sendReqTime;
    }

    void setExpireTime(long l)
    {
        expireTime = l;
    }

    long getExpireTime()
    {
        if(!$assertionsDisabled && expireTime == -1L)
            throw new AssertionError();
        else
            return expireTime;
    }

    GridTaskSplitRef getRef()
    {
        return ref;
    }

    ClusterNode getNode()
    {
        return node;
    }

    public String toString()
    {
        if(!$assertionsDisabled)
            throw new AssertionError();
        else
            return "Grid task unit reference holder [ref=" + ref + ", node=" + node + ", expire-time=" + expireTime + ']';
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

    private GridTaskSplitRef ref;
    private ClusterNode node;
    private long sendReqTime;
    private long expireTime;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(GridTaskSplitRefHolder.class).desiredAssertionStatus();
    }
}
