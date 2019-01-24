// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.uidgen;


// Referenced classes of package com.fitechlabs.xtier.services.uidgen:
//            UidSeqException

public interface UidSeq
{

    public abstract long getStart();

    public abstract long getEnd();

    public abstract long getStep();

    public abstract boolean isCycle();

    public abstract String getName();

    public abstract long getNext()
        throws UidSeqException;

    public abstract void reset();

    public abstract void reset(long l)
        throws UidSeqException;

    public abstract boolean isPersistent();

    public abstract int getSaveFreq();
}
