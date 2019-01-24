// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.uidgen.sinks;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.uidgen.UidSeqException;
import com.fitechlabs.xtier.services.uidgen.UidSeqSink;
import com.fitechlabs.xtier.utils.Utils;
import java.io.*;

public class UidFileSink
    implements UidSeqSink
{

    public UidFileSink(String s)
    {
        folder = s;
    }

    public void close()
    {
    }

    public Long load(String s)
        throws UidSeqException
    {
        String s1;
        File file;
        DataInputStream datainputstream;
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        s1 = makePath(s);
        file = new File(s1);
        if(!file.exists())
            return null;
        datainputstream = null;
        Long long1;
        try
        {
            datainputstream = new DataInputStream(new FileInputStream(file));
            long1 = new Long(datainputstream.readLong());
        }
        catch(IOException ioexception)
        {
            throw new UidSeqException(L10n.format("SRVC.UIDGEN.FSINK.ERR1", s1), ioexception);
        }
        Utils.close(datainputstream);
        return long1;
        Exception exception;
        exception;
        Utils.close(datainputstream);
        throw exception;
    }

    private String makePath(String s)
    {
        String s1 = s + ".obj";
        return folder != null ? Utils.makeValidPath(folder, s1) : s1;
    }

    public void save(String s, long l)
        throws UidSeqException
    {
        String s1;
        File file;
        DataOutputStream dataoutputstream;
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        s1 = makePath(s);
        file = new File(s1);
        File file1 = file.getParentFile();
        if(file1 != null && !file1.exists() && !file1.mkdirs())
            throw new UidSeqException(L10n.format("SRVC.UIDGEN.FSINK.ERR3", s1));
        dataoutputstream = null;
        try
        {
            dataoutputstream = new DataOutputStream(new FileOutputStream(file));
            dataoutputstream.writeLong(l);
        }
        catch(IOException ioexception)
        {
            throw new UidSeqException(L10n.format("SRVC.UIDGEN.FSINK.ERR2", s1), ioexception);
        }
        Utils.close(dataoutputstream);
        break MISSING_BLOCK_LABEL_143;
        Exception exception;
        exception;
        Utils.close(dataoutputstream);
        throw exception;
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

    private String folder;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(UidFileSink.class).desiredAssertionStatus();
    }
}
