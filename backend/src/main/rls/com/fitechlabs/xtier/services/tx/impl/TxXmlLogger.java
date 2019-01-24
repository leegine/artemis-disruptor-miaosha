// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.tx.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.utils.Utils;
import java.io.*;
import java.util.List;

// Referenced classes of package com.fitechlabs.xtier.services.tx.impl:
//            TxException, TxUtils

class TxXmlLogger
{

    TxXmlLogger(String s, String s1, int i, Logger logger)
        throws TxException
    {
        writer = null;
        size = 0;
        if(!$assertionsDisabled && (s == null || s1 == null || i <= 0 || logger == null))
        {
            throw new AssertionError();
        } else
        {
            logFolder = s;
            logFilePrefix = s1;
            logMaxSize = i;
            appLog = logger;
            cnt = findNextLog();
            return;
        }
    }

    private void addTxInfo(StringBuffer stringbuffer, byte abyte0[], long l, int i)
    {
        if(!$assertionsDisabled && !Thread.holdsLock(mutex))
            throw new AssertionError();
        stringbuffer.append("xid=\"");
        for(int j = 0; j < abyte0.length; j++)
        {
            stringbuffer.append(Utils.toHexString(abyte0[j]));
            if(j != abyte0.length - 1)
                stringbuffer.append(",");
        }

        stringbuffer.append("\"");
        stringbuffer.append(" status=\"" + TxUtils.getStrStatus(i) + "\"");
        stringbuffer.append(" timestamp=\"" + l + "\"");
    }

    void logTx(byte abyte0[], int i)
    {
        if(!$assertionsDisabled && abyte0 == null)
            throw new AssertionError();
        long l = System.currentTimeMillis();
        synchronized(mutex)
        {
            StringBuffer stringbuffer = new StringBuffer(128);
            stringbuffer.append("\t<tlog ");
            addTxInfo(stringbuffer, abyte0, l, i);
            stringbuffer.append("/>" + NEW_LINE);
            writeLog(stringbuffer.toString());
        }
    }

    void logTxRsrcs(byte abyte0[], int i, List list)
    {
        if(!$assertionsDisabled && list == null)
            throw new AssertionError();
        long l = System.currentTimeMillis();
        synchronized(mutex)
        {
            StringBuffer stringbuffer = new StringBuffer(128);
            stringbuffer.append("\t<tlog ");
            addTxInfo(stringbuffer, abyte0, l, i);
            stringbuffer.append(">" + NEW_LINE);
            int j = list.size();
            for(int k = 0; k < j; k++)
                stringbuffer.append("\t\t<resource>" + list.get(k).toString() + "</resource>" + NEW_LINE);

            stringbuffer.append("\t</tlog>" + NEW_LINE);
            writeLog(stringbuffer.toString());
        }
    }

    private void writeLog(String s)
    {
        if(!$assertionsDisabled && (s == null || !Thread.holdsLock(mutex)))
            throw new AssertionError();
        try
        {
            int i = s.length();
            if(writer == null)
                openWriter();
            else
            if(size + i > logMaxSize)
            {
                closeWriter();
                openWriter();
            }
            writer.write(s);
            writer.flush();
            size += i;
        }
        catch(IOException ioexception)
        {
            appLog.error(L10n.format("SRVC.TX.ERR47"), ioexception);
            closeWriter();
        }
    }

    private void openWriter()
        throws IOException
    {
        if(!$assertionsDisabled && writer != null)
            throw new AssertionError();
        if(!(new File(logFolder)).exists() && !(new File(logFolder)).mkdirs())
            throw new IOException(L10n.format("SRVC.TX.ERR49", logFolder));
        filePath = Utils.makeValidPath(logFolder, logFilePrefix + cnt++ + '.' + "tlog");
        if((new File(filePath)).exists())
        {
            throw new IOException(L10n.format("SRVC.TX.ERR50", filePath));
        } else
        {
            writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(header);
            writer.write(NEW_LINE);
            size = 0;
            return;
        }
    }

    void closeWriter()
    {
        if(writer != null)
        {
            try
            {
                writer.write("</log>");
                writer.close();
            }
            catch(IOException ioexception) { }
            writer = null;
        }
    }

    private File[] getLogFiles()
    {
        return (new File(logFolder)).listFiles(new FilenameFilter() {

            public boolean accept(File file, String s)
            {
                return s.startsWith(logFilePrefix) && s.endsWith(".tlog");
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

    public String toString()
    {
        return L10n.format("SRVC.TX.LOG.TXT1", new Object[] {
            logFolder, logFilePrefix, new Integer(logMaxSize)
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

    private static final String LOG_FILE_EXT = "tlog";
    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NEW_LINE + "<log>";
    private static final String footer = "</log>";
    private String logFolder;
    private String logFilePrefix;
    private int logMaxSize;
    private final Object mutex = new Object();
    private Writer writer;
    private int size;
    private int cnt;
    private String filePath;
    private Logger appLog;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(TxXmlLogger.class).desiredAssertionStatus();
    }

}
