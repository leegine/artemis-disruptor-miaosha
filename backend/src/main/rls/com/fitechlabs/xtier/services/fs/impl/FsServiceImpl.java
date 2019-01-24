// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.fs.impl;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.kernel.spi.ServiceProviderAdapter;
import com.fitechlabs.xtier.kernel.spi.ServiceProviderException;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.fs.FsService;
import com.fitechlabs.xtier.services.fs.FsTrackingCallback;
import com.fitechlabs.xtier.services.os.OsService;
import com.fitechlabs.xtier.threads.SysThread;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.Utils;
import java.io.*;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.net.*;
//import java.nio.channels.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FsServiceImpl extends ServiceProviderAdapter
    implements FsService
{

    public FsServiceImpl()
    {
        trackings = null;
        trackingCallbacks = null;
        queue = new ReferenceQueue();
        filesToDelete = new ArrayList();
        dirsToClean = new ArrayList();
        osCaseSensitive = false;
        cleanerHook = new Thread("cleaner-hook") {

            private void rm(File file)
            {
                if(!$assertionsDisabled && file == null)
                    throw new AssertionError();
                if(file.isDirectory())
                    cleanDir(file);
                file.delete();
            }

            private void cleanDir(File file)
            {
                if(!$assertionsDisabled && (file == null || !file.isDirectory()))
                    throw new AssertionError();
                File afile[] = file.listFiles();
                for(int i = 0; i < afile.length; i++)
                    rm(afile[i]);

            }

            public void run()
            {
                int i = filesToDelete.size();
                for(int j = 0; j < i; j++)
                    rm((File)filesToDelete.get(j));

                i = dirsToClean.size();
                for(int k = 0; k < i; k++)
                    cleanDir((File)dirsToClean.get(k));

            }

            static final boolean $assertionsDisabled; /* synthetic field */



            {
                super(s);
            }
        }
;
        poller = new SysThread("xtier-fs-cleaner") {

            protected void body()
            {
                do
                {
                    PhantomReference phantomreference;
                    do
                    {
                        checkInterrupted();
                        phantomreference = null;
                        try
                        {
                            phantomreference = (PhantomReference)queue.remove();
                        }
                        catch(InterruptedException interruptedexception)
                        {
                            return;
                        }
                    } while(phantomreference == null);
                    synchronized(trackings)
                    {
                        Iterator iterator = trackings.entrySet().iterator();
                        do
                        {
                            if(!iterator.hasNext())
                                break;
                            Map.Entry entry = (Map.Entry)iterator.next();
                            PhantomReference phantomreference1 = (PhantomReference)entry.getValue();
                            if(phantomreference.equals(phantomreference1))
                            {
                                iterator.remove();
                                File file = (File)entry.getKey();
                                ((FsTrackingCallback)trackingCallbacks.get(file)).onGC(file);
                                phantomreference1.clear();
                            }
                        } while(true);
                    }
                    phantomreference.clear();
                } while(true);
            }

            public void cleanup()
            {
                synchronized(trackings)
                {
                    Iterator iterator = trackings.values().iterator();
                    do
                    {
                        if(!iterator.hasNext())
                            break;
                        PhantomReference phantomreference = (PhantomReference)iterator.next();
                        if(phantomreference != null)
                            phantomreference.clear();
                    } while(true);
                    trackings.clear();
                }
            }


            {
                super(s);
            }
        }
;
    }

    public void registerTracking(File file, Object obj, FsTrackingCallback fstrackingcallback)
    {
        ArgAssert.nullArg(file, "file");
        ArgAssert.nullArg(obj, "obj");
        ArgAssert.nullArg(fstrackingcallback, "callback");
        synchronized(trackings)
        {
            if(trackings.containsKey(file))
                throw new IllegalArgumentException(L10n.format("SRVC.FS.ERR4", file));
            trackings.put(file, new PhantomReference(obj, queue));
            trackingCallbacks.put(file, fstrackingcallback);
        }
    }

    public void unregisterTracking(File file)
    {
        ArgAssert.nullArg(file, "file");
        synchronized(trackings)
        {
            PhantomReference phantomreference = (PhantomReference)trackings.remove(file);
            if(phantomreference != null)
                phantomreference.clear();
        }
    }

    public boolean containsTracking(String s)
    {
        ArgAssert.nullArg(s, "path");
        Map map = trackings;
        JVM INSTR monitorenter ;
label0:
        {
            Iterator iterator = trackings.keySet().iterator();
            do
                if(!iterator.hasNext())
                    break label0;
            while(!((File)iterator.next()).getAbsolutePath().equals(s));
            return true;
        }
        map;
        JVM INSTR monitorexit ;
          goto _L1
        Exception exception;
        exception;
        throw exception;
_L1:
        return false;
    }

    public boolean containsTracking(File file)
    {
        ArgAssert.nullArg(file, "file");
        Map map = trackings;
        JVM INSTR monitorenter ;
        return trackings.containsKey(file);
        Exception exception;
        exception;
        throw exception;
    }

    public Map getTrackings()
    {
        return Collections.unmodifiableMap(trackings);
    }

    protected void onStart()
    {
        trackings = new HashMap();
        trackingCallbacks = new HashMap();
        os = XtierKernel.getInstance().os();
        osCaseSensitive = os.isUnix();
        poller.start();
        Runtime.getRuntime().addShutdownHook(cleanerHook);
    }

    protected void onStop()
        throws ServiceProviderException
    {
        Utils.stopThread(poller);
        if(filesToDelete.size() == 0 && dirsToClean.size() == 0)
            try
            {
                Runtime.getRuntime().removeShutdownHook(cleanerHook);
            }
            catch(IllegalStateException illegalstateexception) { }
    }

    public String getName()
    {
        return "fs";
    }

    public void cp(File file, File file1, boolean flag)
        throws IOException
    {
        File file3;
        FileInputStream fileinputstream;
        FileOutputStream fileoutputstream;
        ArgAssert.nullArg(file, "src");
        ArgAssert.nullArg(file1, "dest");
        if(!file.exists())
            throw new FileNotFoundException(L10n.format("SRVC.FS.ERR8", file));
        if(file.getAbsoluteFile().equals(file1.getAbsoluteFile()))
            throw new IOException(L10n.format("SRVC.FS.ERR5", file));
        if(file1.exists())
        {
            if(!file1.isDirectory() && !flag)
                throw new IOException(L10n.format("SRVC.FS.ERR14", file1));
            if(!file1.canWrite())
                throw new IOException(L10n.format("SRVC.FS.ERR3", file1));
        } else
        {
            File file2 = file1.getParentFile();
            if(file2 != null && !file2.exists())
                file2.mkdirs();
            if(file.isDirectory())
                file1.mkdir();
        }
        if(file.isDirectory())
        {
            File afile[] = file.listFiles();
            for(int i = 0; i < afile.length; i++)
            {
                File file4 = afile[i];
                if(file4.isDirectory())
                {
                    File file5 = new File(file1, file4.getName());
                    if(!file5.exists() && !file5.mkdirs())
                        throw new IOException(L10n.format("SRVC.FS.ERR20", file5));
                    cp(file4, file5, flag);
                } else
                {
                    cp(afile[i], file1, flag);
                }
            }

            break MISSING_BLOCK_LABEL_405;
        }
        file3 = null;
        if(file1.exists() && file1.isDirectory())
            file3 = new File(file1, file.getName());
        else
            file3 = file1;
        if(!flag && file3.exists())
            throw new IOException(L10n.format("SRVC.FS.ERR14", file3));
        fileinputstream = null;
        fileoutputstream = null;
        fileinputstream = new FileInputStream(file);
        fileoutputstream = new FileOutputStream(file3);
        cp(((InputStream) (fileinputstream)), ((OutputStream) (fileoutputstream)));
        close(fileinputstream);
        close(fileoutputstream);
        break MISSING_BLOCK_LABEL_405;
        Exception exception;
        exception;
        close(fileinputstream);
        close(fileoutputstream);
        throw exception;
    }

    public void cp(URL url, File file, boolean flag)
        throws IOException
    {
        InputStream inputstream;
        FileOutputStream fileoutputstream;
        ArgAssert.nullArg(url, "url");
        ArgAssert.nullArg(file, "dest");
        if(file.exists())
        {
            if(file.isDirectory())
                throw new IllegalArgumentException(L10n.format("SRVC.FS.ERR7", file));
            if(!flag)
                throw new IOException(L10n.format("SRVC.FS.ERR14", file));
            if(!file.canWrite())
                throw new IOException(L10n.format("SRVC.FS.ERR3", file));
        } else
        {
            File file1 = file.getParentFile();
            if(file1 != null && !file1.exists())
                file1.mkdirs();
        }
        inputstream = null;
        fileoutputstream = null;
        inputstream = url.openStream();
        fileoutputstream = new FileOutputStream(file);
        cp(inputstream, ((OutputStream) (fileoutputstream)));
        close(inputstream);
        close(fileoutputstream);
        break MISSING_BLOCK_LABEL_169;
        Exception exception;
        exception;
        close(inputstream);
        close(fileoutputstream);
        throw exception;
    }

    public void mv(File file, File file1, boolean flag)
        throws IOException
    {
        cp(file, file1, flag);
        rm(file);
    }

    public void touch(File file)
        throws IOException
    {
        ArgAssert.nullArg(file, "file");
        if(file.isDirectory())
            throw new IllegalArgumentException(L10n.format("SRVC.FS.ERR7", file));
        if(!file.exists())
        {
            if(!file.createNewFile())
                throw new IOException(L10n.format("SRVC.FS.ERR6", file));
        } else
        {
            file.setLastModified(System.currentTimeMillis());
        }
    }

    public void rm(File file)
        throws IOException
    {
        ArgAssert.nullArg(file, "file");
        if(file.isDirectory())
            cleanDir(file);
        if(!file.delete())
            throw new IOException(L10n.format("SRVC.FS.ERR1", file));
        else
            return;
    }

    public void rmOnExit(File file)
        throws IOException
    {
        ArgAssert.nullArg(file, "file");
        filesToDelete.add(file);
    }

    public boolean contentEquals(File file, File file1)
        throws IOException
    {
        FileInputStream fileinputstream;
        FileInputStream fileinputstream1;
        ArgAssert.nullArg(file, "file1");
        ArgAssert.nullArg(file1, "file2");
        if(!file.exists())
            throw new FileNotFoundException(L10n.format("SRVC.FS.ERR8", file));
        if(!file1.exists())
            throw new FileNotFoundException(L10n.format("SRVC.FS.ERR8", file1));
        if(file.isDirectory() != file1.isDirectory())
            throw new IllegalArgumentException(L10n.format("SRVC.FS.ERR19", file, file1));
        if(file.getAbsoluteFile().equals(file1.getAbsoluteFile()))
            return true;
        if(file.length() != file1.length())
            return false;
        if(file.isDirectory())
            break MISSING_BLOCK_LABEL_181;
        fileinputstream = null;
        fileinputstream1 = null;
        boolean flag;
        fileinputstream = new FileInputStream(file);
        fileinputstream1 = new FileInputStream(file1);
        flag = contentEquals(((InputStream) (fileinputstream)), ((InputStream) (fileinputstream1)));
        close(fileinputstream);
        close(fileinputstream1);
        return flag;
        Exception exception;
        exception;
        close(fileinputstream);
        close(fileinputstream1);
        throw exception;
        File afile[] = file.listFiles();
        File afile1[] = file1.listFiles();
        if(afile.length != afile1.length)
            return false;
        Arrays.sort(afile);
        Arrays.sort(afile1);
        for(int i = 0; i < afile.length; i++)
        {
            File file2 = afile[i];
            File file3 = afile1[i];
            if(!file2.getName().equals(file3.getName()))
                return false;
            if(file2.isDirectory() != file3.isDirectory())
                return false;
            if(!contentEquals(file2, file3))
                return false;
        }

        return true;
    }

    public int compareTimestamps(File file, File file1)
    {
        ArgAssert.nullArg(file, "file1");
        ArgAssert.nullArg(file1, "file2");
        if(!file1.exists())
            return file.exists() ? 1 : 0;
        else
            return compareTimestamps(file, file1.lastModified());
    }

    public int compareTimestamps(File file, long l)
    {
        ArgAssert.nullArg(file, "file");
        if(!file.exists() || file.lastModified() < l)
            return -1;
        return file.lastModified() <= l ? 0 : 1;
    }

    public boolean fileContains(File file, String s, boolean flag)
        throws IOException
    {
        BufferedReader bufferedreader;
        ArgAssert.nullArg(file, "file");
        ArgAssert.nullArg(s, "str");
        if(!file.exists() || file.isDirectory())
            return false;
        if(!flag)
            s = s.toLowerCase();
        bufferedreader = null;
        boolean flag1;
        bufferedreader = new BufferedReader(new FileReader(file));
        String s1 = new String();
        do
        {
            if((s1 = bufferedreader.readLine()) == null)
                break MISSING_BLOCK_LABEL_111;
            if(!flag)
                s1 = s1.toLowerCase();
        } while(s1.indexOf(s) <= -1);
        flag1 = true;
        close(bufferedreader);
        return flag1;
        flag1 = false;
        close(bufferedreader);
        return flag1;
        Exception exception;
        exception;
        close(bufferedreader);
        throw exception;
    }

    public boolean fileContains(File file, Pattern pattern)
        throws IOException
    {
        BufferedReader bufferedreader;
        ArgAssert.nullArg(file, "file");
        ArgAssert.nullArg(pattern, "regex");
        if(!file.exists() || file.isDirectory())
            return false;
        bufferedreader = null;
        boolean flag;
        String s = new String();
        bufferedreader = new BufferedReader(new FileReader(file));
        do
            if((s = bufferedreader.readLine()) == null)
                break MISSING_BLOCK_LABEL_90;
        while(!pattern.matcher(s).find());
        flag = true;
        close(bufferedreader);
        return flag;
        flag = false;
        close(bufferedreader);
        return flag;
        Exception exception;
        exception;
        close(bufferedreader);
        throw exception;
    }

    public boolean isFilenameMatch(String s, String s1)
    {
        ArgAssert.nullArg(s, "filename");
        ArgAssert.nullArg(s1, "pattern");
        return wildcardMatch(s, s1);
    }

    private boolean wildcardMatch(String s, String s1)
    {
        boolean flag = false;
        char ac[] = s1.toCharArray();
        char ac1[] = s.toCharArray();
        int i = 0;
        int j = 0;
        do
        {
            if(j >= ac1.length)
                break;
            boolean flag1 = false;
            if(ac[i] == '?')
            {
                if(ac1[j] == '.' && !flag)
                    return false;
            } else
            if(ac[i] == '*')
            {
                for(; i < ac.length - 1 && ac[i + 1] == '*'; i++);
                if(i == ac.length - 1)
                    return true;
                flag1 = flag = true;
            } else
            if(flag)
            {
                if(cmpChars(ac[i], ac1[j]))
                {
                    flag = false;
                } else
                {
                    j++;
                    continue;
                }
            } else
            if(!cmpChars(ac[i], ac1[j]))
                return false;
            if(++i == ac.length)
                return !flag ? j == ac1.length - 1 : true;
            if(!flag1)
                j++;
        } while(true);
        return false;
    }

    private boolean cmpChars(char c, char c1)
    {
        return !osCaseSensitive ? Character.toLowerCase(c) == Character.toLowerCase(c1) : c == c1;
    }

    public String makeSoundexCode(String s)
    {
        ArgAssert.nullArg(s, "str");
        return getSoundexCode(s);
    }

    private String getSoundexCode(String s)
    {
        ArgAssert.nullArg(s, "str");
        s = s.toUpperCase();
        StringBuffer stringbuffer = new StringBuffer(s.substring(0, 1));
        byte byte0 = -1;
        byte byte2 = -1;
        for(int i = 1; i < s.length() && stringbuffer.length() < 4; i++)
        {
            char c = s.charAt(i);
            byte byte1;
            if(c == 'B' || c == 'P' || c == 'F' || c == 'V')
                byte1 = 1;
            else
            if(c == 'C' || c == 'S' || c == 'G' || c == 'J' || c == 'K' || c == 'Q' || c == 'X' || c == 'Z')
                byte1 = 2;
            else
            if(c == 'D' || c == 'T')
                byte1 = 3;
            else
            if(c == 'L')
                byte1 = 4;
            else
            if(c == 'M' || c == 'N')
                byte1 = 5;
            else
            if(c == 'R')
                byte1 = 6;
            else
                byte1 = -1;
            if(byte1 != -1 && byte1 != byte2)
                stringbuffer.append(byte1);
            byte2 = byte1;
        }

        for(int j = stringbuffer.length(); j < 4; j++)
            stringbuffer.append(0);

        return stringbuffer.toString();
    }

    public List searchContent(File file, FileFilter filefilter, String s, boolean flag, boolean flag1)
        throws IOException
    {
        ArgAssert.nullArg(file, "dir");
        ArgAssert.nullArg(s, "str");
        if(!file.isDirectory())
            throw new IOException(L10n.format("SRVC.FS.ERR9", file));
        ArrayList arraylist = new ArrayList();
        File afile[] = file.listFiles();
        for(int i = 0; i < afile.length; i++)
        {
            File file1 = afile[i];
            if(file1.isDirectory() && flag1)
            {
                arraylist.addAll(searchContent(file1, filefilter, s, flag, flag1));
                continue;
            }
            if((filefilter == null || filefilter.accept(file1)) && fileContains(file1, s, flag))
                arraylist.add(file1);
        }

        return arraylist;
    }

    public List searchContent(File file, FilenameFilter filenamefilter, String s, boolean flag, boolean flag1)
        throws IOException
    {
        ArgAssert.nullArg(file, "dir");
        ArgAssert.nullArg(s, "str");
        if(!file.isDirectory())
            throw new IOException(L10n.format("SRVC.FS.ERR9", file));
        ArrayList arraylist = new ArrayList();
        File afile[] = file.listFiles();
        for(int i = 0; i < afile.length; i++)
        {
            File file1 = afile[i];
            if(file1.isDirectory() && flag1)
            {
                arraylist.addAll(searchContent(file1, filenamefilter, s, flag, flag1));
                continue;
            }
            if(filenamefilter.accept(file1.getParentFile(), file1.getName()) && fileContains(file1, s, flag))
                arraylist.add(file1);
        }

        return arraylist;
    }

    public List searchContent(File file, String s, String s1, boolean flag, boolean flag1)
        throws IOException
    {
        ArgAssert.nullArg(file, "dir");
        ArgAssert.nullArg(s, "pattern");
        ArgAssert.nullArg(s1, "str");
        if(!file.isDirectory())
            throw new IOException(L10n.format("SRVC.FS.ERR9", file));
        ArrayList arraylist = new ArrayList();
        File afile[] = file.listFiles();
        for(int i = 0; i < afile.length; i++)
        {
            File file1 = afile[i];
            if(file1.isDirectory() && flag1)
            {
                arraylist.addAll(searchContent(file1, s, s1, flag, flag1));
                continue;
            }
            if(isFilenameMatch(file1.getName(), s) && fileContains(file1, s1, flag))
                arraylist.add(file1);
        }

        return arraylist;
    }

    public List searchFiles(File file, FileFilter filefilter, boolean flag)
        throws IOException
    {
        ArgAssert.nullArg(file, "dir");
        if(!file.isDirectory())
            throw new IOException(L10n.format("SRVC.FS.ERR9", file));
        if(!flag)
            return Arrays.asList(file.listFiles(filefilter));
        ArrayList arraylist = new ArrayList();
        File afile[] = file.listFiles();
        for(int i = 0; i < afile.length; i++)
        {
            File file1 = afile[i];
            if(filefilter == null || filefilter.accept(file1))
                arraylist.add(file1);
            if(file1.isDirectory())
                arraylist.addAll(searchFiles(file1, filefilter, flag));
        }

        return arraylist;
    }

    public List searchFiles(File file, FilenameFilter filenamefilter, boolean flag)
        throws IOException
    {
        ArgAssert.nullArg(file, "dir");
        if(!file.isDirectory())
            throw new IOException(L10n.format("SRVC.FS.ERR9", file));
        if(!flag)
            return Arrays.asList(file.listFiles(filenamefilter));
        ArrayList arraylist = new ArrayList();
        File afile[] = file.listFiles();
        for(int i = 0; i < afile.length; i++)
        {
            File file1 = afile[i];
            if(filenamefilter.accept(file1.getParentFile(), file1.getName()))
                arraylist.add(file1);
            if(file1.isDirectory())
                arraylist.addAll(searchFiles(file1, filenamefilter, flag));
        }

        return arraylist;
    }

    public List searchFiles(File file, String s, boolean flag, boolean flag1)
        throws IOException
    {
        ArgAssert.nullArg(file, "dir");
        ArgAssert.nullArg(s, "pattern");
        if(!file.isDirectory())
            throw new IOException(L10n.format("SRVC.FS.ERR9", file));
        ArrayList arraylist = new ArrayList();
        File afile[] = file.listFiles();
        for(int i = 0; i < afile.length; i++)
        {
            File file1 = afile[i];
            if(isFilenameMatch(file1.getName(), s) && (!file1.isDirectory() || flag1))
                arraylist.add(file1);
            if(file1.isDirectory() && flag)
                arraylist.addAll(searchFiles(file1, s, flag, flag1));
        }

        return arraylist;
    }

    public List searchSoundex(File file, String s, boolean flag, boolean flag1)
        throws IOException
    {
        ArgAssert.nullArg(file, "dir");
        ArgAssert.nullArg(s, "str");
        if(!file.isDirectory())
            throw new IOException(L10n.format("SRVC.FS.ERR9", file));
        String s1 = getSoundexCode(s);
        ArrayList arraylist = new ArrayList();
        File afile[] = file.listFiles();
        for(int i = 0; i < afile.length; i++)
        {
            File file1 = afile[i];
            if((!file1.isDirectory() || flag1) && s1.equals(getSoundexCode(file1.getName())))
                arraylist.add(file1);
            if(file1.isDirectory() && flag)
                arraylist.addAll(searchSoundex(file1, s, flag, flag1));
        }

        return arraylist;
    }

    public List searchContent(File file, FileFilter filefilter, Pattern pattern, boolean flag)
        throws IOException
    {
        ArgAssert.nullArg(file, "dir");
        ArgAssert.nullArg(pattern, "regex");
        if(!file.isDirectory())
            throw new IOException(L10n.format("SRVC.FS.ERR9", file));
        if(!flag)
            return Arrays.asList(file.listFiles(filefilter));
        ArrayList arraylist = new ArrayList();
        File afile[] = file.listFiles();
        for(int i = 0; i < afile.length; i++)
        {
            File file1 = afile[i];
            if(file1.isDirectory() && flag)
            {
                arraylist.addAll(searchContent(file1, filefilter, pattern, flag));
                continue;
            }
            if((filefilter == null || filefilter.accept(file1)) && fileContains(file1, pattern))
                arraylist.add(file1);
        }

        return arraylist;
    }

    public List searchContent(File file, FilenameFilter filenamefilter, Pattern pattern, boolean flag)
        throws IOException
    {
        ArgAssert.nullArg(file, "dir");
        ArgAssert.nullArg(pattern, "regex");
        if(!file.isDirectory())
            throw new IOException(L10n.format("SRVC.FS.ERR9", file));
        if(!flag)
            return Arrays.asList(file.listFiles(filenamefilter));
        ArrayList arraylist = new ArrayList();
        File afile[] = file.listFiles();
        for(int i = 0; i < afile.length; i++)
        {
            File file1 = afile[i];
            if(file1.isDirectory() && flag)
            {
                arraylist.addAll(searchContent(file1, filenamefilter, pattern, flag));
                continue;
            }
            if((filenamefilter == null || filenamefilter.accept(file1.getParentFile(), file1.getName())) && fileContains(file1, pattern))
                arraylist.add(file1);
        }

        return arraylist;
    }

    public List searchContent(File file, String s, Pattern pattern, boolean flag)
        throws IOException
    {
        ArgAssert.nullArg(file, "dir");
        ArgAssert.nullArg(s, "pattern");
        ArgAssert.nullArg(pattern, "regex");
        if(!file.isDirectory())
            throw new IOException(L10n.format("SRVC.FS.ERR9", file));
        ArrayList arraylist = new ArrayList();
        File afile[] = file.listFiles();
        for(int i = 0; i < afile.length; i++)
        {
            File file1 = afile[i];
            if(file1.isDirectory() && flag)
            {
                arraylist.addAll(searchContent(file1, s, pattern, flag));
                continue;
            }
            if(isFilenameMatch(file1.getName(), s) && fileContains(file1, pattern))
                arraylist.add(file1);
        }

        return arraylist;
    }

    public void makeReadable(File file, boolean flag, boolean flag1)
        throws IOException
    {
        ArgAssert.nullArg(file, "file");
        if(!file.exists())
            throw new FileNotFoundException(L10n.format("SRVC.FS.ERR8", file));
        if(os.isWindows())
        {
            if(!flag)
                throw new IOException(L10n.format("SRVC.FS.ERR10", "read"));
        } else
        if(os.isUnix())
            setUnixFileAttr(file, "r", flag, flag1);
        else
            throw new IOException(L10n.format("SRVC.FS.ERR11", file));
    }

    public void makeWritable(File file, boolean flag, boolean flag1, boolean flag2)
        throws IOException
    {
        ArgAssert.nullArg(file, "file");
        if(!file.exists())
            throw new FileNotFoundException(L10n.format("SRVC.FS.ERR8", file));
        if(os.isWindows())
            setWinFileAttr(file, "R", !flag, flag1, flag2);
        else
        if(os.isUnix())
            setUnixFileAttr(file, "w", flag, flag1);
        else
            throw new IOException(L10n.format("SRVC.FS.ERR11", file));
    }

    public void makeExecutable(File file, boolean flag, boolean flag1)
        throws IOException
    {
        ArgAssert.nullArg(file, "file");
        if(!file.exists())
            throw new FileNotFoundException(L10n.format("SRVC.FS.ERR8", file));
        if(os.isWindows())
        {
            if(!flag)
                throw new IOException(L10n.format("SRVC.FS.ERR10", "read"));
        } else
        if(os.isUnix())
            setUnixFileAttr(file, "x", flag, flag1);
        else
            throw new IOException(L10n.format("SRVC.FS.ERR11", file));
    }

    public void setHidden(File file, boolean flag, boolean flag1, boolean flag2)
        throws IOException
    {
        ArgAssert.nullArg(file, "file");
        if(!file.exists())
            throw new FileNotFoundException(L10n.format("SRVC.FS.ERR8", file));
        if(os.isWindows())
            setWinFileAttr(file, "H", flag, flag1, flag2);
        else
        if(os.isUnix())
        {
            if(flag)
                throw new IOException(L10n.format("SRVC.FS.ERR10", "hidden"));
        } else
        {
            throw new IOException(L10n.format("SRVC.FS.ERR11", file));
        }
    }

    public void setSystem(File file, boolean flag, boolean flag1, boolean flag2)
        throws IOException
    {
        ArgAssert.nullArg(file, "file");
        if(!file.exists())
            throw new FileNotFoundException(L10n.format("SRVC.FS.ERR8", file));
        if(os.isWindows())
            setWinFileAttr(file, "S", flag, flag1, flag2);
        else
        if(os.isUnix())
        {
            if(flag)
                throw new IOException(L10n.format("SRVC.FS.ERR10", "system"));
        } else
        {
            throw new IOException(L10n.format("SRVC.FS.ERR11", file));
        }
    }

    public void setArchive(File file, boolean flag, boolean flag1, boolean flag2)
        throws IOException
    {
        ArgAssert.nullArg(file, "file");
        if(os.isWindows())
            setWinFileAttr(file, "A", flag, flag1, flag2);
        else
        if(os.isUnix())
        {
            if(flag)
                throw new IOException(L10n.format("SRVC.FS.ERR10", "hidden"));
            else
                return;
        } else
        {
            throw new IOException(L10n.format("SRVC.FS.ERR11", file));
        }
    }

    public boolean isExecutable(File file)
        throws IOException
    {
        ArgAssert.nullArg(file, "file");
        if(os.isWindows())
            return true;
        if(os.isUnix())
            return readUnixFileAttrs(file).indexOf('x') > -1;
        else
            throw new IOException(L10n.format("SRVC.FS.ERR11", file));
    }

    public boolean isSystem(File file)
        throws IOException
    {
        ArgAssert.nullArg(file, "file");
        if(os.isWindows())
            return readWinFileAttrs(file).indexOf('S') > -1;
        if(os.isUnix())
            return false;
        else
            throw new IOException(L10n.format("SRVC.FS.ERR11", file));
    }

    public boolean isArchive(File file)
        throws IOException
    {
        ArgAssert.nullArg(file, "file");
        if(os.isWindows())
            return readWinFileAttrs(file).indexOf('A') > -1;
        if(os.isUnix())
            return false;
        else
            throw new IOException(L10n.format("SRVC.FS.ERR11", file));
    }

    public void chmod(File file, int i, int j, int k, boolean flag, boolean flag1)
        throws IOException
    {
        ArgAssert.nullArg(file, "file");
        ArgAssert.illegalArg(i == 1 || i == -1 || i == 0, "read");
        ArgAssert.illegalArg(j == 1 || j == -1 || j == 0, "write");
        ArgAssert.illegalArg(k == 1 || k == -1 || k == 0, "exec");
        if(i == 1)
            makeReadable(file, true, flag);
        else
        if(i == -1)
            makeReadable(file, false, flag);
        if(j == 1)
            makeWritable(file, true, flag, flag1);
        else
        if(j == -1)
            makeWritable(file, false, flag, flag1);
        if(k == 1)
            makeExecutable(file, true, flag);
        else
        if(k == -1)
            makeExecutable(file, false, flag);
    }

    private void setWinFileAttr(File file, String s, boolean flag, boolean flag1, boolean flag2)
        throws IOException
    {
        ArgAssert.nullArg(file, "file");
        ArgAssert.nullArg(s, "attr");
        if(!$assertionsDisabled && !os.isWindows())
            throw new AssertionError();
        char c = '+';
        if(!flag)
            c = '-';
        boolean flag3 = isSystem(file);
        boolean flag4 = file.isHidden();
        if(flag2)
            unsetSystemHidden(file);
        else
        if(flag3 || flag4)
            throw new IOException(L10n.format("SRVC.FS.ERR13", file));
        String s1 = c + s;
        if(flag3 && (!"S".equals(s) || !flag))
            s1 = s1 + " +S";
        if(flag4 && (!"H".equals(s) || !flag))
            s1 = s1 + " +H";
        executeProcess("attrib " + s1 + " " + file.getCanonicalPath(), file);
        if(flag1 && file.isDirectory())
        {
            List list = searchFiles(file, (FileFilter)null, true);
            for(int i = 0; i < list.size(); i++)
                setWinFileAttr((File)list.get(i), s, flag, flag1, flag2);

        }
    }

    private void unsetSystemHidden(File file)
        throws IOException
    {
        ArgAssert.nullArg(file, "file");
        if(!$assertionsDisabled && !os.isWindows())
            throw new AssertionError();
        if(!file.exists())
        {
            throw new FileNotFoundException(L10n.format("SRVC.FS.ERR8", file));
        } else
        {
            executeProcess("attrib -S -H " + file.getCanonicalPath(), file);
            return;
        }
    }

    private void setUnixFileAttr(File file, String s, boolean flag, boolean flag1)
        throws IOException
    {
        ArgAssert.nullArg(file, "file");
        ArgAssert.nullArg(s, "attr");
        if(!$assertionsDisabled && !os.isUnix())
            throw new AssertionError();
        char c = '+';
        if(!flag)
            c = '-';
        String s1 = "chmod u" + c + s + " " + file.getCanonicalPath();
        if(flag1)
            s1 = s1 + " -R";
        executeProcess(s1, file);
    }

    private String executeProcess(String s, File file)
        throws IOException
    {
        InputStream inputstream;
        BufferedReader bufferedreader;
        ArgAssert.nullArg(s, "cmd");
        ArgAssert.nullArg(file, "file");
        Process process = Runtime.getRuntime().exec(s);
        try
        {
            if(process.waitFor() != 0)
                throw new IOException(L10n.format("SRVC.FS.ERR11", file));
        }
        catch(InterruptedException interruptedexception)
        {
            throw new IOException(L10n.format("SRVC.FS.ERR11", file));
        }
        inputstream = process.getInputStream();
        bufferedreader = null;
        String s1;
        bufferedreader = new BufferedReader(new InputStreamReader(inputstream));
        s1 = bufferedreader.readLine();
        close(bufferedreader);
        return s1;
        Exception exception;
        exception;
        close(bufferedreader);
        throw exception;
    }

    private String readWinFileAttrs(File file)
        throws IOException
    {
        ArgAssert.nullArg(file, "file");
        if(!file.exists())
            throw new FileNotFoundException(L10n.format("SRVC.FS.ERR8", file));
        String s = executeProcess("attrib " + file.getCanonicalPath(), file);
        if(s == null)
            throw new IOException(L10n.format("SRVC.FS.ERR12", file));
        int i = s.lastIndexOf(' ');
        if(i < 0)
            throw new IOException(L10n.format("SRVC.FS.ERR12", file));
        else
            return s.substring(0, i);
    }

    private String readUnixFileAttrs(File file)
        throws IOException
    {
        ArgAssert.nullArg(file, "file");
        if(!file.exists())
            throw new FileNotFoundException(L10n.format("SRVC.FS.ERR8", file));
        String s = executeProcess("ls -d -l " + file.getCanonicalPath(), file);
        if(s == null)
            throw new IOException(L10n.format("SRVC.FS.ERR12", file));
        if(s.length() < 9)
            throw new IOException(L10n.format("SRVC.FS.ERR12", file));
        else
            return s.substring(1, 4);
    }

    public void cleanDir(File file)
        throws IOException
    {
        ArgAssert.nullArg(file, "dir");
        if(!file.isDirectory())
            throw new IOException(L10n.format("SRVC.FS.ERR9", file));
        File afile[] = file.listFiles();
        boolean flag = false;
        for(int i = 0; i < afile.length; i++)
            try
            {
                rm(afile[i]);
            }
            catch(IOException ioexception)
            {
                flag = true;
            }

        if(flag)
            throw new IOException(L10n.format("SRVC.FS.ERR16", file));
        else
            return;
    }

    public void cleanDirOnExit(File file)
    {
        ArgAssert.nullArg(file, "dir");
        if(!file.isDirectory())
        {
            throw new IllegalArgumentException(L10n.format("SRVC.FS.ERR9", file));
        } else
        {
            dirsToClean.add(file);
            return;
        }
    }

    public long getDirSize(File file, FilenameFilter filenamefilter, boolean flag)
        throws IOException
    {
        ArgAssert.nullArg(file, "dir");
        if(!file.exists())
            throw new FileNotFoundException(L10n.format("SRVC.FS.ERR8", file));
        if(!file.isDirectory())
            throw new IllegalArgumentException(L10n.format("SRVC.FS.ERR9", file));
        File afile[] = file.listFiles(filenamefilter);
        int i = 0;
        if(!flag)
        {
            for(int j = 0; j < afile.length; j++)
            {
                File file1 = afile[j];
                if(!file1.isDirectory())
                    i = (int)((long)i + file1.length());
            }

        } else
        {
            for(int k = 0; k < afile.length; k++)
            {
                File file2 = afile[k];
                i = (int)((long)i + (!file2.isDirectory() ? file2.length() : getDirSize(file2, filenamefilter, true)));
            }

        }
        return (long)i;
    }

    public void touchDir(File file, boolean flag)
        throws IOException
    {
        ArgAssert.nullArg(file, "dir");
        if(!file.isDirectory())
            throw new IllegalArgumentException(L10n.format("SRVC.FS.ERR9", file));
        if(!file.exists())
            if(!file.mkdirs())
                throw new IOException(L10n.format("SRVC.FS.ERR2", file));
            else
                return;
        file.setLastModified(System.currentTimeMillis());
        if(flag)
        {
            File afile[] = file.listFiles();
            for(int i = 0; i < afile.length; i++)
            {
                File file1 = afile[i];
                if(!file1.isDirectory())
                    touch(file1);
                else
                    touchDir(file1, flag);
            }

        }
    }

    public String toSystemNeutral(String s)
    {
        ArgAssert.nullArg(s, "path");
        return s.replace('\\', '/');
    }

    public String toSystemSpecific(String s)
    {
        ArgAssert.nullArg(s, "path");
        if(os.isWindows())
            return s.replace('/', '\\');
        else
            return s.replace('\\', '/');
    }

    public String makePath(String s, String s1)
    {
        ArgAssert.nullArg(s, "subPath1");
        ArgAssert.nullArg(s1, "subPath2");
        char c = s.charAt(s.length() - 1);
        boolean flag = c == '/' || c == '\\';
        c = s1.charAt(0);
        boolean flag1 = c == '/' || c == '\\';
        if(flag != flag1)
            return s + s1;
        if(!flag)
            return s + File.separatorChar + s1;
        else
            return s.substring(0, s.length() - 1) + File.separatorChar + s1.substring(1);
    }

    public String getFileExt(String s, boolean flag)
    {
        ArgAssert.nullArg(s, "path");
        String s1 = (new File(s)).getName();
        int i = s1.lastIndexOf('.');
        return i != -1 ? s1.substring(!flag ? i + 1 : i) : null;
    }

    public String getFilenameWithoutExt(String s, boolean flag)
    {
        ArgAssert.nullArg(s, "path");
        String s1 = (new File(s)).getName();
        int i = s1.lastIndexOf('.');
        return i != -1 ? s1.substring(0, !flag ? i : i + 1) : s1;
    }

    public String getFilename(String s)
    {
        ArgAssert.nullArg(s, "path");
        return (new File(s)).getName();
    }

    public String unescapeURL(String s)
    {
        ArgAssert.nullArg(s, "url");
        char ac[] = s.toCharArray();
        int i = ac.length;
        StringBuffer stringbuffer = new StringBuffer(i);
        for(int j = 0; j < i; j++)
        {
            char c = ac[j];
            if(c == '%')
            {
                if(j >= i - 2)
                    return null;
                int k = Character.digit(ac[++j], 16);
                int l = Character.digit(ac[++j], 16);
                if(k == -1 || l == -1)
                    return null;
                stringbuffer.append((char)(l + k << 4));
            } else
            {
                stringbuffer.append(c);
            }
        }

        return toSystemNeutral(stringbuffer.toString());
    }

    public File getClassDir(Class class1)
    {
        ArgAssert.nullArg(class1, "claxx");
        String s = class1.getName();
        int i = s.lastIndexOf('.');
        if(i != -1)
            s = s.substring(i + 1);
        s = s + ".class";
        URL url = class1.getResource(s);
        if(url == null)
            return null;
        String s1 = unescapeURL(url.toExternalForm());
        if(s1.startsWith("jar:file:"))
        {
            int j = s1.indexOf('!');
            if(j != -1)
            {
                String s3 = s1.substring("jar:file:".length(), j);
                if(!(new File(s3)).exists() && s3.charAt(0) == '/')
                    s3 = s3.substring(1);
                if((new File(s3)).exists() && s3.endsWith(".jar"))
                    return (new File(s3)).getParentFile();
            }
        } else
        if(s1.startsWith("file:"))
        {
            String s2 = class1.getName().replace('.', '/');
            int k = s1.indexOf('/' + s2);
            if(k != -1)
            {
                String s4 = s1.substring("file:".length(), k);
                if(!(new File(s4)).exists() && s4.charAt(0) == '/')
                    s4 = s4.substring(1);
                if((new File(s4)).exists())
                    return new File(s4);
            }
        }
        return null;
    }

    public void writeStr2File(File file, String s, String s1, boolean flag)
        throws IOException
    {
        BufferedOutputStream bufferedoutputstream;
        ArgAssert.nullArg(file, "file");
        ArgAssert.nullArg(s, "data");
        ArgAssert.nullArg(s1, "enc");
        if(file.exists())
        {
            if(file.isDirectory())
                throw new IllegalArgumentException(L10n.format("SRVC.FS.ERR7", file));
            if(!flag)
                throw new IOException(L10n.format("SRVC.FS.ERR14", file));
            if(!file.canWrite())
                throw new IOException(L10n.format("SRVC.FS.ERR3", file));
        }
        bufferedoutputstream = null;
        bufferedoutputstream = new BufferedOutputStream(new FileOutputStream(file));
        bufferedoutputstream.write(s.getBytes(s1));
        close(bufferedoutputstream);
        break MISSING_BLOCK_LABEL_138;
        Exception exception;
        exception;
        close(bufferedoutputstream);
        throw exception;
    }

    public String readFile2Str(File file, String s)
        throws IOException
    {
        InputStreamReader inputstreamreader;
        StringWriter stringwriter;
        ArgAssert.nullArg(file, "file");
        ArgAssert.nullArg(s, "enc");
        inputstreamreader = null;
        stringwriter = null;
        String s1;
        inputstreamreader = new InputStreamReader(new FileInputStream(file), s);
        stringwriter = new StringWriter();
        cp(inputstreamreader, stringwriter);
        s1 = stringwriter.toString();
        close(inputstreamreader);
        close(stringwriter);
        return s1;
        Exception exception;
        exception;
        close(inputstreamreader);
        close(stringwriter);
        throw exception;
    }

    public void writeBytes2File(File file, byte abyte0[], boolean flag)
        throws IOException
    {
        BufferedOutputStream bufferedoutputstream;
        ArgAssert.nullArg(file, "file");
        ArgAssert.nullArg(abyte0, "data");
        if(file.exists())
        {
            if(file.isDirectory())
                throw new IllegalArgumentException(L10n.format("SRVC.FS.ERR7", file));
            if(!flag)
                throw new IOException(L10n.format("SRVC.FS.ERR14", file));
            if(!file.canWrite())
                throw new IOException(L10n.format("SRVC.FS.ERR3", file));
        }
        bufferedoutputstream = null;
        bufferedoutputstream = new BufferedOutputStream(new FileOutputStream(file));
        bufferedoutputstream.write(abyte0);
        close(bufferedoutputstream);
        break MISSING_BLOCK_LABEL_127;
        Exception exception;
        exception;
        close(bufferedoutputstream);
        throw exception;
    }

    public byte[] readFile(File file)
        throws IOException
    {
        byte abyte0[];
        BufferedInputStream bufferedinputstream;
        ArgAssert.nullArg(file, "file");
        ArgAssert.illegalArg(!file.isDirectory(), "file");
        if(!file.exists())
            throw new FileNotFoundException(L10n.format("SRVC.FS.ERR8", file));
        if(file.isDirectory())
            throw new IOException(L10n.format("SRVC.FS.ERR7", file));
        abyte0 = new byte[(int)file.length()];
        bufferedinputstream = null;
        bufferedinputstream = new BufferedInputStream(new FileInputStream(file));
        bufferedinputstream.read(abyte0);
        close(bufferedinputstream);
        break MISSING_BLOCK_LABEL_116;
        Exception exception;
        exception;
        close(bufferedinputstream);
        throw exception;
        return abyte0;
    }

    public int cp(InputStream inputstream, OutputStream outputstream)
        throws IOException
    {
        ArgAssert.nullArg(inputstream, "in");
        ArgAssert.nullArg(outputstream, "out");
        byte abyte0[] = new byte[4096];
        int i = 0;
        for(int j = 0; (j = inputstream.read(abyte0)) > 0;)
        {
            outputstream.write(abyte0, 0, j);
            i += j;
        }

        return i;
    }

    public int cp(Reader reader, Writer writer)
        throws IOException
    {
        ArgAssert.nullArg(reader, "in");
        ArgAssert.nullArg(writer, "out");
        char ac[] = new char[4096];
        int i = 0;
        for(int j = 0; (j = reader.read(ac)) > 0;)
        {
            writer.write(ac, 0, j);
            i += j;
        }

        return i;
    }

    public byte[] toByteArray(InputStream inputstream)
        throws IOException
    {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        byte abyte0[];
        cp(inputstream, bytearrayoutputstream);
        abyte0 = bytearrayoutputstream.toByteArray();
        close(bytearrayoutputstream);
        return abyte0;
        Exception exception;
        exception;
        close(bytearrayoutputstream);
        throw exception;
    }

    public byte[] toByteArray(Reader reader)
        throws IOException
    {
        ByteArrayOutputStream bytearrayoutputstream;
        OutputStreamWriter outputstreamwriter;
        bytearrayoutputstream = null;
        outputstreamwriter = null;
        byte abyte0[];
        bytearrayoutputstream = new ByteArrayOutputStream();
        outputstreamwriter = new OutputStreamWriter(new ByteArrayOutputStream());
        cp(reader, outputstreamwriter);
        abyte0 = bytearrayoutputstream.toByteArray();
        close(bytearrayoutputstream);
        close(outputstreamwriter);
        return abyte0;
        Exception exception;
        exception;
        close(bytearrayoutputstream);
        close(outputstreamwriter);
        throw exception;
    }

    public boolean contentEquals(InputStream inputstream, InputStream inputstream1)
        throws IOException
    {
        BufferedInputStream bufferedinputstream;
        BufferedInputStream bufferedinputstream1;
        ArgAssert.nullArg(inputstream, "in1");
        ArgAssert.nullArg(inputstream1, "in2");
        bufferedinputstream = null;
        bufferedinputstream1 = null;
        boolean flag;
        bufferedinputstream = new BufferedInputStream(inputstream);
        bufferedinputstream1 = new BufferedInputStream(inputstream1);
        int i;
        do
            if((i = bufferedinputstream.read()) == -1)
                break MISSING_BLOCK_LABEL_76;
        while(i == bufferedinputstream1.read());
        flag = false;
        close(bufferedinputstream);
        close(bufferedinputstream1);
        return flag;
        if(bufferedinputstream1.read() == -1)
            break MISSING_BLOCK_LABEL_102;
        flag = false;
        close(bufferedinputstream);
        close(bufferedinputstream1);
        return flag;
        close(bufferedinputstream);
        close(bufferedinputstream1);
        break MISSING_BLOCK_LABEL_132;
        Exception exception;
        exception;
        close(bufferedinputstream);
        close(bufferedinputstream1);
        throw exception;
        return true;
    }

    public void close(InputStream inputstream)
    {
        Utils.close(inputstream);
    }

    public void close(OutputStream outputstream)
    {
        Utils.close(outputstream);
    }

    public void close(Reader reader)
    {
        Utils.close(reader);
    }

    public void close(Writer writer)
    {
        Utils.close(writer);
    }

    public void close(Socket socket)
    {
        if(socket != null)
            try
            {
                socket.close();
            }
            catch(IOException ioexception) { }
    }

    public void close(ServerSocket serversocket)
    {
        if(serversocket != null)
            try
            {
                serversocket.close();
            }
            catch(IOException ioexception) { }
    }

    public void close(DatagramSocket datagramsocket)
    {
        if(datagramsocket != null)
            try
            {
                datagramsocket.close();
            }
            catch(Exception exception) { }
    }

    public void close(Selector selector)
    {
        if(selector != null)
            try
            {
                selector.close();
            }
            catch(IOException ioexception) { }
    }

    public void close(Channel channel)
    {
        if(channel != null)
            try
            {
                channel.close();
            }
            catch(IOException ioexception) { }
    }

    public void close(DatagramChannel datagramchannel)
    {
        if(datagramchannel != null)
            try
            {
                datagramchannel.close();
            }
            catch(IOException ioexception) { }
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

    private static final int BUFFER_SIZE = 4096;
    private Map trackings;
    private Map trackingCallbacks;
    private ReferenceQueue queue;
    private ArrayList filesToDelete;
    private ArrayList dirsToClean;
    private OsService os;
    private boolean osCaseSensitive;
    private Thread cleanerHook;
    private SysThread poller;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(FsServiceImpl.class).desiredAssertionStatus();
    }





}
