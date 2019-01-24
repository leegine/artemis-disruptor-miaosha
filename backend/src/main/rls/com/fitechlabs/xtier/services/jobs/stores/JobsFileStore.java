// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.jobs.stores;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.jobs.JobException;
import com.fitechlabs.xtier.services.jobs.JobsStore;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.Utils;
import java.io.*;

public class JobsFileStore
    implements JobsStore
{

    public JobsFileStore(String s)
    {
        ArgAssert.nullArg(s, "filePath");
        file = new File(s);
    }

    public JobsFileStore(File file1)
    {
        ArgAssert.nullArg(file1, "file");
        file = file1;
    }

    public void storeState(byte abyte0[])
        throws JobException
    {
        BufferedOutputStream bufferedoutputstream;
        ArgAssert.nullArg(abyte0, "state");
        if(!$assertionsDisabled && file == null)
            throw new AssertionError();
        try
        {
            checkFile();
        }
        catch(IOException ioexception)
        {
            throw new JobException(L10n.format("SRVC.JOBS.IMPL.ERR21", file.getAbsolutePath()), ioexception);
        }
        bufferedoutputstream = null;
        try
        {
            bufferedoutputstream = new BufferedOutputStream(new FileOutputStream(file));
            bufferedoutputstream.write(abyte0);
        }
        catch(IOException ioexception1)
        {
            throw new JobException(L10n.format("SRVC.JOBS.IMPL.ERR22", file.getAbsolutePath()), ioexception1);
        }
        Utils.close(bufferedoutputstream);
        break MISSING_BLOCK_LABEL_120;
        Exception exception;
        exception;
        Utils.close(bufferedoutputstream);
        throw exception;
    }

    public byte[] restoreState()
        throws JobException
    {
        byte abyte0[];
        BufferedInputStream bufferedinputstream;
        if(!$assertionsDisabled && file == null)
            throw new AssertionError();
        if(!file.exists() || file.isDirectory())
            throw new JobException(L10n.format("SRVC.JOBS.IMPL.ERR23", file.getAbsoluteFile()));
        abyte0 = new byte[(int)file.length()];
        bufferedinputstream = null;
        byte abyte1[];
        try
        {
            bufferedinputstream = new BufferedInputStream(new FileInputStream(file));
            bufferedinputstream.read(abyte0);
            abyte1 = abyte0;
        }
        catch(IOException ioexception)
        {
            throw new JobException(L10n.format("SRVC.JOBS.IMPL.ERR24", file.getAbsoluteFile()), ioexception);
        }
        Utils.close(bufferedinputstream);
        return abyte1;
        Exception exception;
        exception;
        Utils.close(bufferedinputstream);
        throw exception;
    }

    private void checkFile()
        throws IOException
    {
        if(!$assertionsDisabled && file == null)
            throw new AssertionError();
        if(!file.exists())
        {
            File file1 = file.getAbsoluteFile().getParentFile();
            if(file1 == null)
                throw new IOException(L10n.format("SRVC.JOBS.IMPL.ERR25", file.getAbsolutePath()));
            if(!file1.exists())
                file1.mkdirs();
        } else
        if(file.isDirectory())
            throw new IOException(L10n.format("SRVC.JOBS.IMPL.ERR26", file.getAbsolutePath()));
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

    private File file;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(JobsFileStore.class).desiredAssertionStatus();
    }
}
