// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.uidgen;

import com.fitechlabs.xtier.kernel.KernelService;

// Referenced classes of package com.fitechlabs.xtier.services.uidgen:
//            UidSeqException, Uid, UidSeq, UidSeqSink

public interface UidGenService
    extends KernelService
{

    public abstract Uid makeWanUid();

    public abstract Uid makeLanUid();

    public abstract Uid makeHostUid();

    public abstract Uid makeVmUid();

    public abstract int putWanUid(byte abyte0[], int i);

    public abstract int putLanUid(byte abyte0[], int i);

    public abstract int putHostUid(byte abyte0[], int i);

    public abstract int putVmUid(byte abyte0[], int i);

    public abstract Uid decodeWanUid(byte abyte0[], int i);

    public abstract Uid decodeLanUid(byte abyte0[], int i);

    public abstract Uid decodeHostUid(byte abyte0[], int i);

    public abstract Uid decodeVmUid(byte abyte0[], int i);

    public abstract UidSeq getUidSeq(String s);

    public abstract boolean deleteUidSeq(String s)
        throws UidSeqException;

    public abstract void deleteAllUidSeqs()
        throws UidSeqException;

    public abstract UidSeq createUidSeq(String s, long l, long l1, boolean flag, long l2, UidSeqSink uidseqsink, int i)
        throws UidSeqException;

    public abstract UidSeq createUidSeq(String s, long l, long l1, boolean flag, long l2)
        throws UidSeqException;
}
