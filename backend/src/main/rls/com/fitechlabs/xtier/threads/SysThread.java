// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.threads;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.utils.Utils;
import com.fitechlabs.xtier.utils.boxed.sync.BoxedBooleanSync;
import com.fitechlabs.xtier.utils.boxed.sync.BoxedInt32Sync;

// Referenced classes of package com.fitechlabs.xtier.threads:
//            SysThreadInterrupted, SysThreadGroup

public abstract class SysThread extends Thread
{

    public SysThread(String s, int i, ThreadGroup threadgroup)
    {
        super(threadgroup, "");
        num = numGen.preIncr();
        interrupted = new BoxedBooleanSync(false);
        if(!$assertionsDisabled && s == null)
        {
            throw new AssertionError();
        } else
        {
            setName("xtier~" + s + "-[#" + num + ']');
            setPriority(i);
            return;
        }
    }

    public SysThread(String s)
    {
        this(s, 5, ((ThreadGroup) (SysThreadGroup.getDfltGroup())));
    }

    public SysThread(String s, int i)
    {
        this(s, i, ((ThreadGroup) (SysThreadGroup.getDfltGroup())));
    }

    public static int getThreadsCount()
    {
        return numGen.get();
    }

    public boolean isInterrupted()
    {
        return super.isInterrupted() || interrupted.get();
    }

    public void interrupt()
    {
        interrupted.set(true);
        super.interrupt();
    }

    public int getNum()
    {
        return num;
    }

    public void safeStop()
    {
        Utils.stopThread(this);
    }

    public final void checkInterrupted()
    {
        if(isInterrupted())
            throw new SysThreadInterrupted();
        else
            return;
    }

    protected void cleanup()
    {
    }

    public void start()
    {
        super.start();
    }

    public final void run()
    {
        try
        {
            body();
        }
        catch(SysThreadInterrupted systhreadinterrupted)
        {
            cleanup();
            break MISSING_BLOCK_LABEL_26;
        }
        cleanup();
        break MISSING_BLOCK_LABEL_26;
        Exception exception;
        exception;
        cleanup();
        throw exception;
    }

    protected abstract void body();

    public String toString()
    {
        return L10n.format("SYS.THRDS.TXT2", getName(), getThreadGroup(), new Integer(getPriority()), new Integer(getNum()));
    }



    public static final int SYS_LOWEST_PRIORITY = 1;
    public static final int SYS_NORMAL_PRIORITY = 5;
    public static final int SYS_HIGHEST_PRIORITY = 10;
    public static final int SYS_ABOVE_NORMAL_PRIORITY = 7;
    public static final int SYS_BELOW_NORMAL_PRIORITY = 3;
    private static BoxedInt32Sync numGen = new BoxedInt32Sync(0);
    private int num;
    private BoxedBooleanSync interrupted;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(SysThread.class).desiredAssertionStatus();
    }
}
