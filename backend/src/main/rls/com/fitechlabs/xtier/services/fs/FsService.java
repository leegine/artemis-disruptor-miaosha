// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.fs;

import com.fitechlabs.xtier.kernel.KernelService;
import java.io.*;
import java.net.*;
import java.nio.channels.*;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

// Referenced classes of package com.fitechlabs.xtier.services.fs:
//            FsTrackingCallback

public interface FsService
    extends KernelService
{

    public abstract void cp(File file, File file1, boolean flag)
        throws IOException;

    public abstract void cp(URL url, File file, boolean flag)
        throws IOException;

    public abstract void mv(File file, File file1, boolean flag)
        throws IOException;

    public abstract void touch(File file)
        throws IOException;

    public abstract void rmOnExit(File file)
        throws IOException;

    public abstract boolean contentEquals(File file, File file1)
        throws IOException;

    public abstract int compareTimestamps(File file, File file1);

    public abstract int compareTimestamps(File file, long l);

    public abstract boolean fileContains(File file, String s, boolean flag)
        throws IOException;

    public abstract boolean fileContains(File file, Pattern pattern)
        throws IOException;

    public abstract boolean isFilenameMatch(String s, String s1);

    public abstract List searchFiles(File file, FileFilter filefilter, boolean flag)
        throws IOException;

    public abstract List searchFiles(File file, FilenameFilter filenamefilter, boolean flag)
        throws IOException;

    public abstract List searchFiles(File file, String s, boolean flag, boolean flag1)
        throws IOException;

    public abstract List searchSoundex(File file, String s, boolean flag, boolean flag1)
        throws IOException;

    public abstract String makeSoundexCode(String s);

    public abstract List searchContent(File file, FileFilter filefilter, String s, boolean flag, boolean flag1)
        throws IOException;

    public abstract List searchContent(File file, FileFilter filefilter, Pattern pattern, boolean flag)
        throws IOException;

    public abstract List searchContent(File file, FilenameFilter filenamefilter, String s, boolean flag, boolean flag1)
        throws IOException;

    public abstract List searchContent(File file, FilenameFilter filenamefilter, Pattern pattern, boolean flag)
        throws IOException;

    public abstract List searchContent(File file, String s, String s1, boolean flag, boolean flag1)
        throws IOException;

    public abstract List searchContent(File file, String s, Pattern pattern, boolean flag)
        throws IOException;

    public abstract void makeReadable(File file, boolean flag, boolean flag1)
        throws IOException;

    public abstract void makeWritable(File file, boolean flag, boolean flag1, boolean flag2)
        throws IOException;

    public abstract void makeExecutable(File file, boolean flag, boolean flag1)
        throws IOException;

    public abstract void setHidden(File file, boolean flag, boolean flag1, boolean flag2)
        throws IOException;

    public abstract void setSystem(File file, boolean flag, boolean flag1, boolean flag2)
        throws IOException;

    public abstract void setArchive(File file, boolean flag, boolean flag1, boolean flag2)
        throws IOException;

    public abstract boolean isExecutable(File file)
        throws IOException;

    public abstract boolean isSystem(File file)
        throws IOException;

    public abstract boolean isArchive(File file)
        throws IOException;

    public abstract void chmod(File file, int i, int j, int k, boolean flag, boolean flag1)
        throws IOException;

    public abstract void cleanDir(File file)
        throws IOException;

    public abstract void rm(File file)
        throws IOException;

    public abstract void cleanDirOnExit(File file);

    public abstract long getDirSize(File file, FilenameFilter filenamefilter, boolean flag)
        throws IOException;

    public abstract void touchDir(File file, boolean flag)
        throws IOException;

    public abstract String toSystemNeutral(String s);

    public abstract String toSystemSpecific(String s);

    public abstract String makePath(String s, String s1);

    public abstract String getFileExt(String s, boolean flag);

    public abstract String getFilenameWithoutExt(String s, boolean flag);

    public abstract String getFilename(String s);

    public abstract String unescapeURL(String s);

    public abstract File getClassDir(Class class1);

    public abstract void writeStr2File(File file, String s, String s1, boolean flag)
        throws IOException;

    public abstract String readFile2Str(File file, String s)
        throws IOException;

    public abstract void writeBytes2File(File file, byte abyte0[], boolean flag)
        throws IOException;

    public abstract byte[] readFile(File file)
        throws IOException;

    public abstract int cp(InputStream inputstream, OutputStream outputstream)
        throws IOException;

    public abstract int cp(Reader reader, Writer writer)
        throws IOException;

    public abstract byte[] toByteArray(InputStream inputstream)
        throws IOException;

    public abstract byte[] toByteArray(Reader reader)
        throws IOException;

    public abstract boolean contentEquals(InputStream inputstream, InputStream inputstream1)
        throws IOException;

    public abstract void registerTracking(File file, Object obj, FsTrackingCallback fstrackingcallback);

    public abstract void unregisterTracking(File file);

    public abstract Map getTrackings();

    public abstract boolean containsTracking(String s);

    public abstract boolean containsTracking(File file);

    public abstract void close(InputStream inputstream);

    public abstract void close(OutputStream outputstream);

    public abstract void close(Reader reader);

    public abstract void close(Writer writer);

    public abstract void close(Socket socket);

    public abstract void close(ServerSocket serversocket);

    public abstract void close(DatagramSocket datagramsocket);

    public abstract void close(Selector selector);

    public abstract void close(Channel channel);

    public abstract void close(DatagramChannel datagramchannel);

    public static final int SET = 1;
    public static final int UNSET = -1;
    public static final int UNCHANGED = 0;
}
