// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.log.sinks.file;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.kernel.mbean.DynamicMBeanFactory;
import com.fitechlabs.xtier.kernel.mbean.DynamicXtierMBean;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.jmx.JmxService;
import com.fitechlabs.xtier.services.log.*;
import com.fitechlabs.xtier.services.log.adapters.LogSinkAdapter;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.Utils;
import java.io.*;
import java.util.HashSet;
import java.util.Set;
import javax.management.JMException;
import javax.management.MBeanServer;

// Referenced classes of package com.fitechlabs.xtier.services.log.sinks.file:
//            LogFileSinkException, LogFileSinkHtmlViewer

public class LogFileSink extends LogSinkAdapter
{

    public LogFileSink(String s, LogFormatter logformatter)
        throws LogFileSinkException
    {
        this(s, logformatter, null, null, Utils.makeValidPath(System.getProperty("user.dir"), Utils.makeValidPath("log", System.getProperty("xtier.host.name"))), "xtier.log", 0x80000, "clean");
    }

    public LogFileSink(String s, LogFormatter logformatter, String s1)
        throws LogFileSinkException
    {
        this(s, logformatter, null, null, s1, "xtier.log", 0x80000, "clean");
    }

    public LogFileSink(String s, LogFormatter logformatter, LogFilter logfilter, LogErrorHandler logerrorhandler, String s1, String s2, int i,
            String s3)
        throws LogFileSinkException
    {
        super(s, logformatter, logfilter, logerrorhandler);
        folderHistory = new HashSet();
        maxSize = 0;
        size = 0;
        cnt = 0;
        htmlViewerMBean = null;
        stream = null;
        ArgAssert.nullArg(logformatter, "formatter");
        ArgAssert.nullArg(s1, "folder");
        ArgAssert.nullArg(s2, "fileName");
        ArgAssert.nullArg(s3, "mode");
        ArgAssert.illegalArg(i > 0, "maxSize");
        folder = s1;
        maxSize = i;
        if(!(new File(s1)).exists() && !(new File(s1)).mkdirs())
            throw new LogFileSinkException(L10n.format("SRVC.LOG.FSINK.ERR7", s1));
        folderHistory.add(s1);
        int j = s2.indexOf('.');
        if(j == -1)
            throw new LogFileSinkException(L10n.format("SRVC.LOG.FSINK.ERR2", s2));
        file = s2.substring(0, j);
        ext = s2.substring(j + 1);
        if(file.length() == 0 || ext.length() == 0)
            throw new LogFileSinkException(L10n.format("SRVC.LOG.FSINK.ERR2", s2));
        if(s3.equalsIgnoreCase("append"))
            cnt = findNextLog();
        else
        if(s3.equalsIgnoreCase("clean"))
            cleanLogFiles();
        else
            ArgAssert.illegalArg(false, "mode");
        try
        {
            htmlViewerMBean = DynamicMBeanFactory.createDynMBean(new LogFileSinkHtmlViewer(this), com.fitechlabs.xtier.services.log.sinks.file.LogFileSinkHtmlViewerMBean.class, "sink", s);
            XtierKernel.getInstance().jmx().getMBeanServer().registerMBean(htmlViewerMBean, htmlViewerMBean.getName());
        }
        catch(JMException jmexception)
        {
            htmlViewerMBean = null;
            throw new LogFileSinkException(L10n.format("SRVC.LOG.FSINK.ERR9"), jmexception);
        }
    }

    public void close()
    {
        synchronized(getMutex())
        {
            closeStream();
        }
        if(htmlViewerMBean != null)
            try
            {
                XtierKernel.getInstance().jmx().getMBeanServer().unregisterMBean(htmlViewerMBean.getName());
            }
            catch(JMException jmexception) { }
    }

    private void cleanLogFiles()
        throws LogFileSinkException
    {
        File afile[] = getLogFiles();
        if(afile != null)
        {
            for(int i = 0; i < afile.length; i++)
                if(!afile[i].delete())
                    throw new LogFileSinkException(L10n.format("SRVC.LOG.FSINK.ERR5", afile[i]));

        }
    }

    private int findNextLog()
        throws LogFileSinkException
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
                    int l = Integer.decode(s.substring(file.length(), k)).intValue();
                    if(l > i)
                        i = l;
                }
                catch(NumberFormatException numberformatexception)
                {
                    throw new LogFileSinkException(L10n.format("SRVC.LOG.FSINK.ERR6", s));
                }
            }

            return i + 1;
        } else
        {
            return 0;
        }
    }

    private File[] getLogFiles()
    {
        final String dotExt = "." + ext;
        return (new File(folder)).listFiles(new FilenameFilter() {

            public boolean accept(File file1, String s)
            {
                return s.startsWith(file) && s.endsWith(dotExt);
            }


            {
                super();
            }
        }
);
    }

    private void closeStream()
    {
        if(stream != null)
        {
            String s = getFormatter().footer();
            try
            {
                if(s != null)
                {
                    stream.write(Utils.NEW_LINE.getBytes());
                    stream.write(s.getBytes());
                }
                stream.close();
            }
            catch(IOException ioexception) { }
            stream = null;
        }
    }

    private void openStream()
        throws IOException
    {
        if(!$assertionsDisabled && (!Thread.holdsLock(getMutex()) || stream != null))
            throw new AssertionError();
        if(!(new File(folder)).exists() && !(new File(folder)).mkdirs())
            throw new IOException(L10n.format("SRVC.LOG.FSINK.ERR7", folder));
        filePath = Utils.makeValidPath(folder, file + cnt++ + '.' + ext);
        if((new File(filePath)).exists())
            throw new IOException(L10n.format("SRVC.LOG.FSINK.ERR8", filePath));
        stream = new BufferedOutputStream(new FileOutputStream(filePath));
        String s = getFormatter().header();
        if(s != null)
        {
            stream.write(s.getBytes());
            stream.write(Utils.NEW_LINE.getBytes());
        }
        size = 0;
    }

    public void add(LogRecord logrecord)
    {
        synchronized(getMutex())
        {
            try
            {
                LogFilter logfilter = getFilter();
                if(logfilter == null || logfilter.isLoggable(this, logrecord))
                {
                    String s = getFormatter().format(logrecord);
                    if(stream == null)
                        openStream();
                    else
                    if(size + s.length() > maxSize)
                    {
                        closeStream();
                        openStream();
                    }
                    stream.write(s.getBytes());
                    size += s.length();
                }
            }
            catch(IOException ioexception)
            {
                getErrorHandler().onLogError(this, logrecord, ioexception);
                closeStream();
            }
        }
    }

    Set getFolderHistory()
    {
        Object obj = getMutex();
        JVM INSTR monitorenter ;
        return folderHistory;
        Exception exception;
        exception;
        throw exception;
    }

    String getFolder()
    {
        Object obj = getMutex();
        JVM INSTR monitorenter ;
        return folder;
        Exception exception;
        exception;
        throw exception;
    }

    String getFile()
    {
        return file;
    }

    String getExt()
    {
        return ext;
    }

    void setFolder(String s)
    {
        synchronized(getMutex())
        {
            folder = s;
            folderHistory.add(s);
            size = maxSize;
        }
    }

    public String toString()
    {
        return L10n.format("SRVC.LOG.FSINK.TXT1", new Object[] {
            getName(), getFormatter(), getFilter(), getErrorHandler(), folder, file + '.' + ext, new Integer(maxSize)
        });
    }

    private Set folderHistory;
    private String folder;
    private String file;
    private String ext;
    private int maxSize;
    private int size;
    private String filePath;
    private int cnt;
    private DynamicXtierMBean htmlViewerMBean;
    private OutputStream stream;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(LogFileSink.class).desiredAssertionStatus();
    }

}
