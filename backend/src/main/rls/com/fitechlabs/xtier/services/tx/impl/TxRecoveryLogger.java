// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.tx.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.threads.SysThread;
import com.fitechlabs.xtier.utils.Utils;
import java.io.*;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.tx.impl:
//            TxState, TxException

class TxRecoveryLogger
{

    TxRecoveryLogger(String s, String s1, String s2, int i, Logger logger)
        throws TxException
    {
        out = null;
        cachedFiles = new HashMap();
        record = new byte[76];
        logCleaner = new SysThread("tx-log-cleaner") {

            protected void body()
            {
                do
                {
                    checkInterrupted();
                    cleanLogs(null);
                    synchronized(mutex)
                    {
                        Utils.waitOn(mutex);
                    }
                } while(true);
            }


            {
                super(s);
            }
        }
;
        if(!$assertionsDisabled && (s == null || s1 == null || i <= 0 || logger == null))
        {
            throw new AssertionError();
        } else
        {
            logFolder = s;
            logFilePrefix = s1;
            logFileExt = s2;
            maxTxPerFile = i;
            appLog = logger;
            cnt = findNextLog();
            logCleaner.start();
            return;
        }
    }

    void logTx(byte abyte0[], int i)
    {
        synchronized(mutex)
        {
            fillRecord(abyte0, i);
            writeLog(abyte0);
        }
    }

    TxState readTxState(byte abyte0[])
        throws IOException
    {
        if(!$assertionsDisabled && abyte0 == null)
            throw new AssertionError();
        File afile[] = getLogFiles();
        int i = -1;
        long l = 0L;
        if(afile != null)
        {
            for(int j = 0; j < afile.length; j++)
            {
                synchronized(mutex)
                {
                    BufferedInputStream bufferedinputstream = new BufferedInputStream(new FileInputStream(afile[j]));
                    byte abyte1[] = new byte[abyte0.length];
                    do
                    {
                        if(bufferedinputstream.available() <= 0)
                            break;
                        bufferedinputstream.read(abyte1);
                        if(Arrays.equals(abyte0, abyte1))
                        {
                            skip(bufferedinputstream, 64 - abyte1.length);
                            int k = (bufferedinputstream.read() << 24) + (bufferedinputstream.read() << 16) + (bufferedinputstream.read() << 8) + bufferedinputstream.read();
                            long l1 = ((long)bufferedinputstream.read() << 56) + ((long)bufferedinputstream.read() << 48) + ((long)bufferedinputstream.read() << 40) + ((long)bufferedinputstream.read() << 32) + ((long)bufferedinputstream.read() << 24) + ((long)bufferedinputstream.read() << 16) + ((long)bufferedinputstream.read() << 8) + (long)bufferedinputstream.read();
                            if(l1 >= l)
                            {
                                i = k;
                                l = l1;
                            }
                        } else
                        {
                            skip(bufferedinputstream, 76 - abyte1.length);
                        }
                    } while(true);
                    Utils.close(bufferedinputstream);
                }
                if(i != -1)
                    return new TxState(i, l);
            }

        }
        return null;
    }

    Map getTxStates(boolean flag)
        throws IOException
    {
        HashMap hashmap = new HashMap();
        File afile[] = getLogFiles();
        if(afile != null)
        {
            for(int i = 0; i < afile.length; i++)
                synchronized(mutex)
                {
                    BufferedInputStream bufferedinputstream = new BufferedInputStream(new FileInputStream(afile[i]));
                    byte abyte0[] = new byte[64];
                    int k;
                    long l;
                    for(; bufferedinputstream.available() > 0; hashmap.put(new String(abyte0), new TxState(k, l)))
                    {
                        bufferedinputstream.read(abyte0);
                        k = (bufferedinputstream.read() << 24) + (bufferedinputstream.read() << 16) + (bufferedinputstream.read() << 8) + bufferedinputstream.read();
                        l = ((long)bufferedinputstream.read() << 56) + ((long)bufferedinputstream.read() << 48) + ((long)bufferedinputstream.read() << 40) + ((long)bufferedinputstream.read() << 32) + ((long)bufferedinputstream.read() << 24) + ((long)bufferedinputstream.read() << 16) + ((long)bufferedinputstream.read() << 8) + (long)bufferedinputstream.read();
                    }

                    Utils.close(bufferedinputstream);
                }

        }
        if(flag)
        {
            Iterator iterator = hashmap.entrySet().iterator();
            do
            {
                if(!iterator.hasNext())
                    break;
                Map.Entry entry = (Map.Entry)iterator.next();
                int j = ((TxState)entry.getValue()).getStatus();
                if(j != 2 && j != 5 && j != 9 && j != 1)
                    iterator.remove();
            } while(true);
        }
        return hashmap;
    }

    void stop()
    {
        synchronized(mutex)
        {
            mutex.notifyAll();
        }
        closeDefaultStream();
        logCleaner.safeStop();
    }

    private File[] getLogFiles()
    {
        return (new File(logFolder)).listFiles(new FilenameFilter() {

            public boolean accept(File file, String s)
            {
                return s.startsWith(logFilePrefix) && s.endsWith("." + logFileExt);
            }


            {
                super();
            }
        }
);
    }

    private int findNextLog()
        throws TxException
    {
        File afile[] = getLogFiles();
        if(afile != null)
        {
            int i = 0;
            for(int j = 0; j < afile.length; j++)
            {
                String s = afile[j].getName();
                int k = s.lastIndexOf('.');
                if(!$assertionsDisabled && k == -1)
                    throw new AssertionError("Invalid file name: " + s);
                try
                {
                    int l = Integer.decode(s.substring(logFilePrefix.length(), k)).intValue();
                    if(l > i)
                        i = l;
                }
                catch(NumberFormatException numberformatexception)
                {
                    throw new TxException(L10n.format("SRVC.TX.ERR48", s));
                }
            }

            return i + 1;
        } else
        {
            return 0;
        }
    }

    private void fillRecord(byte abyte0[], int i)
    {
        if(!$assertionsDisabled && abyte0 == null)
            throw new AssertionError();
        byte byte0 = 0;
        Arrays.fill(record, byte0);
        for(int j = 0; j < abyte0.length; j++)
            record[j] = abyte0[j];

        int k = 64;
        record[k++] = (byte)(i >> 24);
        record[k++] = (byte)(i >> 16);
        record[k++] = (byte)(i >> 8);
        record[k++] = (byte)i;
        long l = System.currentTimeMillis();
        record[k++] = (byte)(int)(l >> 56);
        record[k++] = (byte)(int)(l >> 48);
        record[k++] = (byte)(int)(l >> 40);
        record[k++] = (byte)(int)(l >> 32);
        record[k++] = (byte)(int)(l >> 24);
        record[k++] = (byte)(int)(l >> 26);
        record[k++] = (byte)(int)(l >> 8);
        record[k] = (byte)(int)l;
        if(!$assertionsDisabled && k != 75)
            throw new AssertionError();
        else
            return;
    }

    private void writeLog(byte abyte0[])
    {
        if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            throw new AssertionError();
        if(!$assertionsDisabled && record == null)
            throw new AssertionError();
        if(!$assertionsDisabled && abyte0 == null)
            throw new AssertionError();
        OutputStream outputstream = null;
        try
        {
            outputstream = getStream(abyte0);
            outputstream.write(record);
            outputstream.flush();
            closeNonDefaultStream(outputstream);
        }
        catch(IOException ioexception)
        {
            appLog.error(L10n.format("SRVC.TX.ERR61"), ioexception);
            Utils.close(outputstream);
        }
    }

    private OutputStream getStream(byte abyte0[])
        throws IOException
    {
        if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            throw new AssertionError();
        if(!$assertionsDisabled && abyte0 == null)
            throw new AssertionError();
        if(cachedFiles.containsKey(abyte0))
        {
            File file = (File)cachedFiles.get(abyte0);
            if(file == dfltFile)
                return getDefaultStream();
            else
                return openStream(file);
        }
        if(dfltFile == null)
            setDefaultFile(cnt);
        if(txPerFile >= maxTxPerFile)
        {
            setDefaultFile(cnt++);
            txPerFile = 0;
        }
        txPerFile++;
        cachedFiles.put(abyte0, dfltFile);
        return getDefaultStream();
    }

    private void closeDefaultStream()
    {
        Utils.close(out);
    }

    private OutputStream openStream(File file)
        throws IOException
    {
        if(!$assertionsDisabled && file == null)
        {
            throw new AssertionError();
        } else
        {
            boolean flag = file.exists();
            return new FileOutputStream(file, flag);
        }
    }

    private OutputStream getDefaultStream()
        throws IOException
    {
        if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            throw new AssertionError();
        if(!$assertionsDisabled && dfltFile == null)
            throw new AssertionError();
        if(out == null)
            out = openStream(dfltFile);
        return out;
    }

    private void setDefaultFile(int i)
        throws IOException
    {
        if(!$assertionsDisabled && i <= 0)
            throw new AssertionError();
        dfltFile = new File(Utils.makeValidPath(logFolder, logFilePrefix + i + '.' + logFileExt));
        if(!(new File(logFolder)).exists() && !(new File(logFolder)).mkdirs())
            throw new IOException(L10n.format("SRVC.TX.ERR49", logFolder));
        if(dfltFile.exists())
            throw new IOException(L10n.format("SRVC.TX.ERR60", dfltFile));
        Utils.close(out);
        out = null;
        out = getDefaultStream();
        synchronized(mutex)
        {
            mutex.notifyAll();
        }
    }

    void cleanLogs(Set set)
    {
        BufferedInputStream bufferedinputstream = new BufferedInputStream(new FileInputStream(file));
        HashMap hashmap = new HashMap();
        byte abyte0[] = new byte[64];
        do
        {
            if(bufferedinputstream.available() <= 0)
                break;
            bufferedinputstream.read(abyte0);
            int j = (bufferedinputstream.read() << 24) + (bufferedinputstream.read() << 16) + (bufferedinputstream.read() << 8) + bufferedinputstream.read();
            skip(bufferedinputstream, 8L);
            hashmap.put(abyte0, new Integer(j));
            if(j == 3 || j == 4 || set != null && set.contains(new String(abyte0)))
                hashmap.remove(abyte0);
        } while(true);
        bufferedinputstream.close();
        if(hashmap.size() == 0 && !file.delete())
            throw new IOException(L10n.format("SRVC.TX.ERR65", file.getAbsolutePath()));
        obj;
        JVM INSTR monitorexit ;
        continue;
        exception;
        throw exception;
label0:
        {
            File afile[] = getLogFiles();
            if(afile == null)
                break label0;
            File file;
            for(int i = 0; i < afile.length;)
                try
                {
label1:
                    {
                        synchronized(mutex)
                        {
                            file = afile[i];
                            if(!file.equals(dfltFile))
                                break label1;
                        }
                        continue;
                    }
                }
                catch(IOException ioexception)
                {
                    appLog.error(L10n.format("SRVC.TX.ERR66"), ioexception);
                    i++;
                }

        }
        return;
    }

    private void closeNonDefaultStream(OutputStream outputstream)
    {
        if(outputstream != out)
            Utils.close(outputstream);
    }

    private void skip(InputStream inputstream, long l)
        throws IOException
    {
        if(!$assertionsDisabled && inputstream == null)
            throw new AssertionError();
        if(!$assertionsDisabled && l > (long)inputstream.available())
            throw new AssertionError();
        for(int i = 0; (long)i < l; i++)
            inputstream.read();

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

    private static final int TX_REC_LENGTH = 76;
    private final Object mutex = new Object();
    private OutputStream out;
    private String logFolder;
    private String logFilePrefix;
    private String logFileExt;
    private int cnt;
    private int txPerFile;
    private int maxTxPerFile;
    private Logger appLog;
    private HashMap cachedFiles;
    private byte record[];
    private File dfltFile;
    private SysThread logCleaner;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(TxRecoveryLogger.class).desiredAssertionStatus();
    }



}
