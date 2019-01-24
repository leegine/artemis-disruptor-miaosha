// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.cluster.sensors;

import com.fitechlabs.xtier.services.cluster.ClusterNodeCpuSensor;

public class ClusterNodeWinCpuSensor
    implements ClusterNodeCpuSensor
{

    public ClusterNodeWinCpuSensor()
    {
        getCpuLoad();
        getCpuLoad();
    }

    private native float getPdhCpuLoad();

    private native void closePdhQuery();

    public synchronized float getCpuLoad()
    {
        return getPdhCpuLoad();
    }

    public synchronized void stop()
    {
        closePdhQuery();
    }

    static 
    {
        System.loadLibrary("xtier_win_cpu");
    }
}
