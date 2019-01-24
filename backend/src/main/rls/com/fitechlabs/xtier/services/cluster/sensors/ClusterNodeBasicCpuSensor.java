// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.cluster.sensors;

import com.fitechlabs.xtier.services.cluster.ClusterNodeCpuSensor;
import com.fitechlabs.xtier.threads.SysThread;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.Utils;
import com.fitechlabs.xtier.utils.boxed.sync.BoxedFloat32Sync;

public class ClusterNodeBasicCpuSensor
    implements ClusterNodeCpuSensor
{

    public ClusterNodeBasicCpuSensor(long l, float f)
    {
        load = new BoxedFloat32Sync(0.0F);
        ArgAssert.illegalArg(l > 0L, "sampling");
        ArgAssert.illegalArg(f > 0.0F, "coefficient");
        worker = new SysThread(l, f) {

            protected void body()
            {
                do
                {
                    checkInterrupted();
                    long l1 = System.currentTimeMillis();
                    Utils.sleep(sampling);
                    yield();
                    long l2 = Math.max(System.currentTimeMillis() - l1, sampling);
                    float f1 = coefficient * (1.0F - (float)((double)sampling / (double)l2));
                    load.set(f1 <= 1.0F ? f1 : 1.0F);
                } while(true);
            }


            {
                super(final_s, final_i);
            }
        }
;
        worker.start();
    }

    public float getCpuLoad()
    {
        return load.get();
    }

    public void stop()
    {
        if(!$assertionsDisabled && worker == null)
        {
            throw new AssertionError();
        } else
        {
            Utils.stopThread(worker);
            load.set(0.0F);
            return;
        }
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

    private SysThread worker;
    private BoxedFloat32Sync load;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(ClusterNodeBasicCpuSensor.class).desiredAssertionStatus();
    }

}
