// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.log.sinks.file;

import com.fitechlabs.xtier.l10n.L10n;
import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.log.sinks.file:
//            LogFileSinkHtmlViewerMBean, LogFileSink

public class LogFileSinkHtmlViewer
    implements LogFileSinkHtmlViewerMBean
{

    public LogFileSinkHtmlViewer()
    {
    }

    LogFileSinkHtmlViewer(LogFileSink logfilesink)
    {
        impl = logfilesink;
    }

    public String viewLogFiles()
    {
        StringBuffer stringbuffer = new StringBuffer();
        Set set = impl.getFolderHistory();
        Iterator iterator = set.iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            String s = (String)iterator.next();
            File file1 = new File(s);
            final String file = impl.getFile();
            final String ext = impl.getExt();
            if(file1.exists())
            {
                File afile[] = file1.listFiles(new FilenameFilter() {

                    public boolean accept(File file2, String s2)
                    {
                        return s2.startsWith(file) && s2.endsWith(ext);
                    }

            
            {
                super();
            }
                }
);
                Arrays.sort(afile, new Comparator() {

                    public int compare(Object obj, Object obj1)
                    {
                        File file2 = (File)obj;
                        File file3 = (File)obj1;
                        long l = file2.lastModified();
                        long l1 = file3.lastModified();
                        return l != l1 ? l >= l1 ? 1 : -1 : 0;
                    }

            
            {
                super();
            }
                }
);
                if(afile.length == 0)
                {
                    stringbuffer.append(L10n.format("SRVC.LOG.FSINK.TXT2", s) + "<br>");
                } else
                {
                    stringbuffer.append("<table aling=center cellspacing=1 cellpadding=3 border=0>");
                    for(int i = 0; i < afile.length; i++)
                    {
                        String s1 = afile[i].getAbsolutePath();
                        stringbuffer.append("<tr><td><a href=\"file:///" + s1 + "\">" + s1 + "</a></td></tr>");
                    }

                    stringbuffer.append("</table>");
                }
            }
        } while(true);
        return stringbuffer.toString();
    }

    public String getDestinationFolder()
    {
        return impl.getFolder();
    }

    public void setDestinationFolder(String s)
    {
        impl.setFolder(s);
    }

    private LogFileSink impl;
}
