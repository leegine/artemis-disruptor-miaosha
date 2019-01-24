// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.utils.concurrent;

import com.fitechlabs.xtier.utils.Utils;

// Referenced classes of package com.fitechlabs.xtier.utils.concurrent:
//            NonReentrantRWLock

public class WritePrefRWLock
    implements NonReentrantRWLock
{

    public WritePrefRWLock()
    {
        n = 0;
        pendingWrites = 0;
    }

    public synchronized void acquireRead()
    {
        if(!$assertionsDisabled && n < -1)
            throw new AssertionError();
        do
        {
            if(n != -1 && pendingWrites <= 0)
                break;
            try
            {
                Utils.waitOn(this);
            }
            catch(RuntimeException runtimeexception)
            {
                notifyAll();
                throw runtimeexception;
            }
        } while(true);
        n++;
    }

    public synchronized void acquireWrite()
    {
        if(!$assertionsDisabled && n < -1)
            throw new AssertionError();
        pendingWrites++;
        while(n != 0)
            try
            {
                Utils.waitOn(this);
            }
            catch(RuntimeException runtimeexception)
            {
                notifyAll();
                throw runtimeexception;
            }
        pendingWrites--;
        break MISSING_BLOCK_LABEL_82;
        Exception exception;
        exception;
        pendingWrites--;
        throw exception;
        n = -1;
        return;
    }

    public synchronized void releaseRead()
    {
        if(!$assertionsDisabled && n <= 0)
            throw new AssertionError("Invalid read-lock release.");
        if(--n == 0)
            notifyAll();
    }

    public synchronized void releaseWrite()
    {
        if(!$assertionsDisabled && n != -1)
        {
            throw new AssertionError("Invalid write-lock release.");
        } else
        {
            n = 0;
            notifyAll();
            return;
        }
    }

    public boolean acquireRead(long l)
    {
        if(!$assertionsDisabled && l < 0L)
            throw new AssertionError();
        if(!$assertionsDisabled && n < -1)
            throw new AssertionError();
        long l1 = System.currentTimeMillis();
        while(n == -1 || pendingWrites > 0)
            try
            {
                Utils.waitOn(this, l);
                l -= System.currentTimeMillis() - l1;
                if(l <= 0L)
                    return false;
            }
            catch(RuntimeException runtimeexception)
            {
                notifyAll();
                throw runtimeexception;
            }
        n++;
        return true;
    }

    public boolean acquireWrite(long l)
    {
        if(!$assertionsDisabled && l < 0L)
            throw new AssertionError();
        if(!$assertionsDisabled && n < -1)
            throw new AssertionError();
        pendingWrites++;
        long l1 = System.currentTimeMillis();
_L2:
        if(n == 0)
            break MISSING_BLOCK_LABEL_112;
        Utils.waitOn(this, l);
        l -= System.currentTimeMillis() - l1;
        if(l > 0L) goto _L2; else goto _L1
_L1:
        boolean flag = false;
        pendingWrites--;
        return flag;
        RuntimeException runtimeexception;
        runtimeexception;
        notifyAll();
        throw runtimeexception;
        pendingWrites--;
        break MISSING_BLOCK_LABEL_142;
        Exception exception;
        exception;
        pendingWrites--;
        throw exception;
        n = -1;
        return true;
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

    private short n;
    private short pendingWrites;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(WritePrefRWLock.class).desiredAssertionStatus();
    }
}
