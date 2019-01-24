// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.jobs.adapters;

import com.fitechlabs.xtier.services.jobs.JobException;
import com.fitechlabs.xtier.services.jobs.JobsStore;

public class JobsStoreAdapter
    implements JobsStore
{

    public JobsStoreAdapter()
    {
    }

    public void storeState(byte abyte0[])
        throws JobException
    {
    }

    public byte[] restoreState()
        throws JobException
    {
        return null;
    }
}
