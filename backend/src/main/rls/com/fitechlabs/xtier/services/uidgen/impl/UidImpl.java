// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.uidgen.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.uidgen.Uid;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.Utils;
import java.util.Arrays;

public class UidImpl
    implements Uid
{

    UidImpl(byte abyte0[], int i, int j)
    {
        arr = null;
        hexStr = null;
        hash = 0;
        arr = new byte[j];
        System.arraycopy(abyte0, i, arr, 0, j);
        for(int k = 0; k < abyte0.length; k++)
            hash = 31 * hash + abyte0[k];

    }

    UidImpl(byte abyte0[])
    {
        arr = null;
        hexStr = null;
        hash = 0;
        if(!$assertionsDisabled && abyte0 == null)
            throw new AssertionError();
        arr = abyte0;
        for(int i = 0; i < abyte0.length; i++)
            hash = 31 * hash + abyte0[i];

    }

    public int getBytesLength()
    {
        return arr.length;
    }

    public byte[] getBytes()
    {
        return arr;
    }

    public String getHexString()
    {
        if(hexStr == null)
        {
            StringBuffer stringbuffer = new StringBuffer();
            for(int i = 0; i < arr.length; i++)
                stringbuffer.append(Utils.toHexString(arr[i]));

            return stringbuffer.toString();
        } else
        {
            return hexStr;
        }
    }

    public int hashCode()
    {
        return hash;
    }

    public boolean equals(Object obj)
    {
        if(obj == this)
            return true;
        if(obj == null)
            return false;
        else
            return Arrays.equals(((UidImpl)obj).arr, arr);
    }

    public int compareTo(Object obj)
    {
        ArgAssert.nullArg(obj, "obj");
        ArgAssert.illegalArg((obj instanceof UidImpl), "obj");
        byte abyte0[] = ((UidImpl)obj).arr;
        if(!$assertionsDisabled && abyte0 == null)
            throw new AssertionError();
        for(int i = 0; i < arr.length; i++)
            if(arr[i] != abyte0[i])
                return arr[i] >= abyte0[i] ? 1 : -1;

        return 0;
    }

    public String toString()
    {
        return L10n.format("SRVC.UIDGEN.TXT3", new Integer(arr.length), getHexString());
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

    private byte arr[];
    private String hexStr;
    private int hash;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(UidImpl.class).desiredAssertionStatus();
    }
}
