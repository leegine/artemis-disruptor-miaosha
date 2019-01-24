// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.uidgen;


// Referenced classes of package com.fitechlabs.xtier.services.uidgen:
//            UidSeqException

public interface UidSeqSink
{

    public abstract Long load(String s)
        throws UidSeqException;

    public abstract void save(String s, long l)
        throws UidSeqException;

    public abstract void close()
        throws UidSeqException;
}
