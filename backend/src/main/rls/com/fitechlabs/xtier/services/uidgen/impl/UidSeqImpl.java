// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.uidgen.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.uidgen.*;
import com.fitechlabs.xtier.utils.ArgAssert;

public class UidSeqImpl
    implements UidSeq
{

    public UidSeqImpl(String s, long l, long l1, boolean flag, long l2, UidSeqSink uidseqsink, int i)
    {
        sink = null;
        overflowException = null;
        ArgAssert.nullArg(s, "name");
        ArgAssert.illegalArg(l2 != 0L, "step");
        ArgAssert.illegalArg(i > 0, "saveFreq");
        if(l == 0x8000000000000000L)
            throw new IllegalArgumentException(L10n.format("SRVC.UIDGEN.ERR17"));
        absStep = Math.abs(l2);
        long l3 = Math.max(Math.abs(l), Math.abs(l1));
        long l4 = Math.min(Math.abs(l), Math.abs(l1));
        boolean flag1 = l >= 0L;
        boolean flag2 = l1 >= 0L;
        if(l3 - l4 < absStep && (flag1 == flag2 || flag1 != flag2 && l3 < absStep - l4))
        {
            throw new IllegalArgumentException(L10n.format("SRVC.UIDGEN.SEQ.ERR1", s));
        } else
        {
            name = s;
            start = l;
            end = l1;
            step = l2;
            cycle = flag;
            saveFreq = i;
            sink = uidseqsink;
            curr = l;
            last = l1 - l2;
            counter = 0;
            return;
        }
    }

    public long getStart()
    {
        return start;
    }

    public long getEnd()
    {
        return end;
    }

    public long getStep()
    {
        return step;
    }

    public boolean isCycle()
    {
        return cycle;
    }

    public String getName()
    {
        return name;
    }

    UidSeqSink getSink()
    {
        return sink;
    }

    private long nextSaveValue(long l)
    {
        long l1 = curr;
        for(int i = 0; (long)i < l; i++)
        {
            if(step > 0L && last < l1 || step < 0L && last > l1)
            {
                if(cycle)
                    l1 = start;
                else
                    return step <= 0L ? 0x8000000000000000L : 0x7fffffffffffffffL;
                continue;
            }
            if(!$assertionsDisabled && (step <= 0L ? 0x8000000000000000L - step > curr : 0x7fffffffffffffffL - step < curr))
                throw new AssertionError("Illegal overflow detected in: " + this);
            l1 += step;
        }

        return l1;
    }

    public long getNext()
        throws UidSeqException
    {
        if(overflowException != null)
            throw overflowException;
        long l = curr;
        if(step > 0L && last < curr || step < 0L && last > curr)
        {
            if(cycle)
                curr = start;
            else
                overflowException = new UidSeqException(L10n.format("SRVC.UIDGEN.SEQ.ERR2", name));
        } else
        {
            if(!$assertionsDisabled && (step <= 0L ? 0x8000000000000000L - step > curr : 0x7fffffffffffffffL - step < curr))
                throw new AssertionError("Illegal overflow detected in: " + this);
            curr += step;
        }
        if(sink != null)
        {
            counter++;
            if(!$assertionsDisabled && counter > saveFreq)
                throw new AssertionError();
            if(counter == saveFreq)
            {
                counter = 0;
                long l1 = nextSaveValue(absStep * ((long)saveFreq / absStep + 1L) + 1L);
                sink.save(name, l1);
            }
        }
        return l;
    }

    public void reset()
    {
        curr = start;
    }

    public void reset(long l)
        throws UidSeqException
    {
        if(step <= 0L || l > end || step < 0L && l >= end)
        {
            throw new UidSeqException(L10n.format("SRVC.UIDGEN.ERR16", this, new Long(l)));
        } else
        {
            curr = l;
            return;
        }
    }

    public boolean isPersistent()
    {
        return sink == null;
    }

    public int getSaveFreq()
    {
        return saveFreq;
    }

    public String toString()
    {
        return L10n.format("SRVC.UIDGEN.TXT2", new Object[] {
            name, new Long(curr), new Long(start), new Long(end), new Long(step), Boolean.toString(cycle), new Long(saveFreq), sink
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

    private String name;
    private long start;
    private long end;
    private long step;
    private boolean cycle;
    private int saveFreq;
    private long absStep;
    private UidSeqSink sink;
    private long curr;
    private int counter;
    private long last;
    private UidSeqException overflowException;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(UidSeqImpl.class).desiredAssertionStatus();
    }
}
