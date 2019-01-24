// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.marshal.impl;


class MarshalByteBuffer
{

    MarshalByteBuffer()
    {
        arr = new byte[16384];
        cnt = 0;
    }

    private void expand(int i)
    {
        if(!$assertionsDisabled && cnt <= 0)
            throw new AssertionError();
        if(!$assertionsDisabled && i <= arr.length)
        {
            throw new AssertionError();
        } else
        {
            byte abyte0[] = new byte[i];
            System.arraycopy(arr, 0, abyte0, 0, cnt);
            arr = abyte0;
            return;
        }
    }

    void put(byte byte0)
    {
        if(!$assertionsDisabled && cnt > arr.length)
            throw new AssertionError();
        if(cnt == arr.length)
            expand(arr.length * 2);
        arr[cnt++] = byte0;
    }

    void put(byte abyte0[], int i, int j)
    {
        if(cnt + j > arr.length)
            expand(Math.max(arr.length * 2, cnt + j));
        System.arraycopy(abyte0, i, arr, cnt, j);
        cnt += j;
    }

    void put(byte abyte0[])
    {
        put(abyte0, 0, abyte0.length);
    }

    void reset()
    {
        cnt = 0;
    }

    byte[] toArray()
    {
        byte abyte0[] = new byte[cnt];
        System.arraycopy(arr, 0, abyte0, 0, cnt);
        return abyte0;
    }

    public String toString()
    {
        if(!$assertionsDisabled)
            throw new AssertionError();
        else
            return "Byte buffer [count=" + cnt + ", size=" + arr.length + ']';
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

    private static final int INIT_SIZE = 16384;
    private byte arr[];
    private int cnt;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(MarshalByteBuffer.class).desiredAssertionStatus();
    }
}
