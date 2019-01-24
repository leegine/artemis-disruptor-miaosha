// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.utils;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.jobs.JobException;
import com.fitechlabs.xtier.threads.SysThread;
import com.fitechlabs.xtier.threads.SysThreadInterrupted;
import com.fitechlabs.xtier.utils.concurrent.RWLock;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.channels.Channel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.sql.PooledConnection;

// Referenced classes of package com.fitechlabs.xtier.utils:
//            UtilsException

public final class Utils
{

    public Utils()
    {
    }

    private static void resetDateTimeFormats()
    {
        dateTimeFormatters = (new DateFormat[] {
            DateFormat.getDateTimeInstance(0, 0), DateFormat.getDateTimeInstance(0, 1), DateFormat.getDateTimeInstance(0, 2), DateFormat.getDateTimeInstance(0, 3), DateFormat.getDateTimeInstance(1, 0), DateFormat.getDateTimeInstance(1, 1), DateFormat.getDateTimeInstance(1, 2), DateFormat.getDateTimeInstance(1, 3), DateFormat.getDateTimeInstance(2, 0), DateFormat.getDateTimeInstance(2, 1),
            DateFormat.getDateTimeInstance(2, 2), DateFormat.getDateTimeInstance(2, 3), DateFormat.getDateTimeInstance(3, 0), DateFormat.getDateTimeInstance(3, 1), DateFormat.getDateTimeInstance(3, 2), DateFormat.getDateTimeInstance(3, 3), DateFormat.getDateInstance(0), DateFormat.getDateInstance(1), DateFormat.getDateInstance(2), DateFormat.getDateInstance(3)
        });
        timeFormatters = (new DateFormat[] {
            DateFormat.getTimeInstance(0), DateFormat.getTimeInstance(1), DateFormat.getTimeInstance(2), DateFormat.getTimeInstance(3)
        });
    }

    public static Locale parseLocale(String s)
    {
        Locale alocale[] = Locale.getAvailableLocales();
        for(int i = 0; i < alocale.length; i++)
        {
            Locale locale = alocale[i];
            if(locale.toString().equals(s) || locale.getDisplayName().equals(s))
                return locale;
        }

        return null;
    }

    private static void checkLocaleChange()
    {
        synchronized(localeMutex)
        {
            Locale locale = Locale.getDefault();
            if(locale != lastLocale)
            {
                lastLocale = locale;
                resetDateTimeFormats();
            }
        }
    }

    public static int getNonRehashCapacity(int i)
    {
        return getNonRehashCapacity(i, 0.75F);
    }

    public static int getNonRehashCapacity(int i, float f)
    {
        return (int)((float)i / f) + 1;
    }

    public static String getStackTrace(Throwable throwable)
    {
        StringWriter stringwriter = new StringWriter();
        PrintWriter printwriter = new PrintWriter(stringwriter);
        throwable.printStackTrace(printwriter);
        printwriter.close();
        return stringwriter.toString();
    }

    public static boolean isLocalHost(InetAddress inetaddress)
    {
        if(!$assertionsDisabled && inetaddress == null)
            throw new AssertionError();
        for(int i = localAddrs.length; i-- > 0;)
            if(localAddrs[i].equals(inetaddress))
                return true;

        return false;
    }

    public static InetAddress[] getLocalIpAddrs()
    {
        return localAddrs;
    }

    public static String getStrVersion(int ai[])
    {
        return Integer.toString(ai[0]) + '.' + ai[1] + '.' + ai[2] + '.' + ai[3];
    }

    public static String[] map2Arr(Map map)
    {
        String as[] = new String[map.size()];
        Iterator iterator = map.keySet().iterator();
        for(int i = 0; iterator.hasNext(); i++)
        {
            String s = iterator.next().toString();
            as[i] = s + '=' + map.get(s);
        }

        return as;
    }

    public static String[] list2Arr(List list)
    {
        String as[] = new String[list.size()];
        for(int i = 0; i < as.length; i++)
            as[i] = list.get(i).toString();

        return as;
    }

    public static String timeMeter(long l)
    {
        long al[] = new long[4];
        long al1[] = {
            1000L, 60L, 60L, 60L
        };
        for(int i = 0; i < al1.length && l > 0L; l /= al1[i++])
            al[i] = l % al1[i];

        return pad(al[3]) + ':' + pad(al[2]) + ':' + pad(al[1]) + ':' + pad(al[0]);
    }

    public String escape4Regex(String s)
    {
        StringBuffer stringbuffer = new StringBuffer();
        char ac[] = s.toCharArray();
        for(int i = 0; i < ac.length; i++)
        {
            char c = ac[i];
            switch(c)
            {
            case 92: // '\\'
                stringbuffer.append("\\\\");
                break;

            case 36: // '$'
            case 38: // '&'
            case 40: // '('
            case 41: // ')'
            case 42: // '*'
            case 43: // '+'
            case 46: // '.'
            case 58: // ':'
            case 63: // '?'
            case 91: // '['
            case 93: // ']'
            case 94: // '^'
            case 123: // '{'
            case 125: // '}'
                stringbuffer.append("\\").append(c);
                break;

            default:
                stringbuffer.append(c);
                break;
            }
        }

        return stringbuffer.toString();
    }

    private static String pad(long l)
    {
        return l >= 10L ? Long.toString(l) : "0" + l;
    }

    public static String makeValidPath(String s, String s1)
    {
        if(!$assertionsDisabled && (s == null || s1 == null))
            throw new AssertionError();
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

    public static void waitOn(Object obj, long l)
    {
        if(!$assertionsDisabled && obj == null)
            throw new AssertionError();
        if(!$assertionsDisabled && !Thread.holdsLock(obj))
            throw new AssertionError("Caller of 'waitOn(Object, long)' must provide outside synchronization.");
        try
        {
            obj.wait(l);
        }
        catch(InterruptedException interruptedexception)
        {
            handleSysThreadInterrupt();
        }
    }

    public static void waitOn(Object obj)
    {
        if(!$assertionsDisabled && obj == null)
            throw new AssertionError();
        if(!$assertionsDisabled && !Thread.holdsLock(obj))
            throw new AssertionError("Caller of 'waitOn(Object)' must provide outside synchronization.");
        try
        {
            obj.wait();
        }
        catch(InterruptedException interruptedexception)
        {
            handleSysThreadInterrupt();
        }
    }

    public static void safeAcquireRead(RWLock rwlock, long l)
    {
        Thread thread = Thread.currentThread();
        if((thread instanceof SysThread))
        {
            SysThread systhread = (SysThread)thread;
            for(; !rwlock.acquireRead(l); systhread.checkInterrupted());
        } else
        {
            rwlock.acquireRead();
        }
    }

    public static void safeAcquireRead(RWLock rwlock)
    {
        safeAcquireRead(rwlock, 100L);
    }

    public static void safeAcquireWrite(RWLock rwlock, long l)
    {
        Thread thread = Thread.currentThread();
        if((thread instanceof SysThread))
        {
            SysThread systhread = (SysThread)thread;
            for(; !rwlock.acquireWrite(l); systhread.checkInterrupted());
        } else
        {
            rwlock.acquireWrite();
        }
    }

    public static void safeAcquireWrite(RWLock rwlock)
    {
        safeAcquireWrite(rwlock, 100L);
    }

    public static void join(Thread thread)
    {
        if(!$assertionsDisabled && thread == null)
            throw new AssertionError();
        try
        {
            thread.join();
        }
        catch(InterruptedException interruptedexception)
        {
            handleSysThreadInterrupt();
        }
    }

    public static void join(Thread thread, long l)
    {
        if(!$assertionsDisabled && thread == null)
            throw new AssertionError();
        try
        {
            thread.join(l);
        }
        catch(InterruptedException interruptedexception)
        {
            handleSysThreadInterrupt();
        }
    }

    public static void stopThread(Thread thread)
    {
        if(thread != null)
        {
            if(!$assertionsDisabled && Thread.currentThread() == thread)
                throw new AssertionError("Cannot stop and join current thread: " + thread);
            thread.interrupt();
            join(thread);
        }
    }

    public static void sleep(long l)
    {
        try
        {
            Thread.sleep(l);
        }
        catch(InterruptedException interruptedexception)
        {
            handleSysThreadInterrupt();
        }
    }

    private static void handleSysThreadInterrupt()
    {
        if(!(Thread.currentThread() instanceof SysThread))
        {
            String s = L10n.format("KRNL.UTILS.ERR1", Thread.currentThread());
            if(!$assertionsDisabled)
                throw new AssertionError(s);
            else
                throw new IllegalStateException(s);
        } else
        {
            throw new SysThreadInterrupted();
        }
    }

    public static String getShortClassName(String s)
    {
        if(!$assertionsDisabled && s == null)
        {
            throw new AssertionError();
        } else
        {
            s = s.endsWith(";") ? s.substring(0, s.length() - 1) + "[]" : s;
            return s.indexOf('.') >= 0 ? s.substring(s.lastIndexOf('.') + 1) : s;
        }
    }

    public static String map2Str(Map map)
    {
        if(map == null)
            return "null";
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("Map [");
        Iterator iterator = map.keySet().iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            Object obj = iterator.next();
            stringbuffer.append(obj + "=" + map.get(obj));
            if(iterator.hasNext())
                stringbuffer.append(", ");
        } while(true);
        stringbuffer.append(']');
        return stringbuffer.toString();
    }

    public static String coll2Str(Collection collection)
    {
        if(collection == null)
            return "null";
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("List [");
        Iterator iterator = collection.iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            stringbuffer.append(iterator.next());
            if(iterator.hasNext())
                stringbuffer.append(", ");
        } while(true);
        stringbuffer.append(']');
        return stringbuffer.toString();
    }

    public static String list2Str(List list)
    {
        if(list == null)
            return "null";
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("List [");
        int i = list.size();
        for(int j = 0; j < i; j++)
        {
            stringbuffer.append(list.get(j).toString());
            if(j < i - 1)
                stringbuffer.append(", ");
        }

        stringbuffer.append(']');
        return stringbuffer.toString();
    }

    public static String arr2Str(Object obj)
    {
        StringBuffer stringbuffer = new StringBuffer("Array [");
        if(obj == null)
        {
            stringbuffer.append("null");
        } else
        {
            int i = Array.getLength(obj);
            for(int j = 0; j < i; j++)
            {
                stringbuffer.append(Array.get(obj, j));
                if(j + 1 < i)
                    stringbuffer.append(", ");
            }

        }
        stringbuffer.append(']');
        return stringbuffer.toString();
    }

    public static List arr2List(Object aobj[])
    {
        if(aobj == null)
            return null;
        ArrayList arraylist = new ArrayList(aobj.length);
        for(int i = 0; i < aobj.length; i++)
            arraylist.add(aobj[i]);

        return arraylist;
    }

    public static Object buildObj(Class class1)
        throws UtilsException
    {
        if(!$assertionsDisabled && class1 == null)
            throw new AssertionError();
        try
        {
            return class1.newInstance();
        }
        catch(IllegalAccessException illegalaccessexception)
        {
            throw new UtilsException(L10n.format("KRNL.UTILS.RTTI.ERR2", class1), illegalaccessexception);
        }
        catch(InstantiationException instantiationexception)
        {
            throw new UtilsException(L10n.format("KRNL.UTILS.RTTI.ERR3", class1), instantiationexception);
        }
    }

    public static Method getMethod(Class class1, String s, Class aclass[])
        throws UtilsException
    {
        try
        {
            return class1.getMethod(s, aclass);
        }
        catch(NoSuchMethodException nosuchmethodexception)
        {
            throw new UtilsException(L10n.format("KRNL.UTILS.RTTI.ERR4", class1.getName(), s), nosuchmethodexception);
        }
    }

    public static Object invokeMethod(Object obj, Method method, Object aobj[])
        throws UtilsException
    {
        try
        {
            return method.invoke(obj, aobj);
        }
        catch(IllegalAccessException illegalaccessexception)
        {
            throw new UtilsException(L10n.format("KRNL.UTILS.RTTI.ERR5", obj.getClass().getName(), method.getName()), illegalaccessexception);
        }
        catch(InvocationTargetException invocationtargetexception)
        {
            throw new UtilsException(L10n.format("KRNL.UTILS.RTTI.ERR6", getShortClassName(obj.getClass().getName()), method.getName(), obj), invocationtargetexception);
        }
    }

    public static void close(Connection connection)
    {
        if(connection != null)
            try
            {
                connection.close();
            }
            catch(SQLException sqlexception) { }
    }

    public static void close(PooledConnection pooledconnection)
    {
        if(pooledconnection != null)
            try
            {
                pooledconnection.close();
            }
            catch(SQLException sqlexception) { }
    }

    public static void close(Statement statement)
    {
        if(statement != null)
            try
            {
                statement.close();
            }
            catch(SQLException sqlexception) { }
    }

    public static void close(ResultSet resultset)
    {
        if(resultset != null)
            try
            {
                resultset.close();
            }
            catch(SQLException sqlexception) { }
    }

    public static void close(InputStream inputstream)
    {
        if(inputstream != null)
            try
            {
                inputstream.close();
            }
            catch(IOException ioexception) { }
    }

    public static void close(Writer writer)
    {
        if(writer != null)
            try
            {
                writer.close();
            }
            catch(IOException ioexception) { }
    }

    public static void close(Reader reader)
    {
        if(reader != null)
            try
            {
                reader.close();
            }
            catch(IOException ioexception) { }
    }

    public static void close(javax.jms.Connection connection)
    {
        if(connection != null)
        {
            try
            {
                connection.stop();
            }
            catch(JMSException jmsexception) { }
            try
            {
                connection.close();
            }
            catch(JMSException jmsexception1) { }
        }
    }

    public static void close(MessageConsumer messageconsumer)
    {
        if(messageconsumer != null)
            try
            {
                messageconsumer.close();
            }
            catch(JMSException jmsexception) { }
    }

    public static void close(MessageProducer messageproducer)
    {
        if(messageproducer != null)
            try
            {
                messageproducer.close();
            }
            catch(JMSException jmsexception) { }
    }

    public static void close(Session session)
    {
        if(session != null)
            try
            {
                session.close();
            }
            catch(JMSException jmsexception) { }
    }

    public static void close(OutputStream outputstream)
    {
        if(outputstream != null)
            try
            {
                outputstream.close();
            }
            catch(IOException ioexception) { }
    }

    public static void close(Selector selector)
    {
        if(selector != null)
        {
            for(Iterator iterator = selector.keys().iterator(); iterator.hasNext(); close(((Channel) (((SelectionKey)iterator.next()).channel()))));
            try
            {
                selector.close();
            }
            catch(IOException ioexception) { }
        }
    }

    public static void close(DatagramChannel datagramchannel)
    {
        if(datagramchannel != null)
        {
            close(datagramchannel.socket());
            try
            {
                datagramchannel.close();
            }
            catch(IOException ioexception) { }
        }
    }

    public static void close(Channel channel)
    {
        if(channel != null)
            try
            {
                channel.close();
            }
            catch(IOException ioexception) { }
    }

    public static void close(Socket socket)
    {
        if(socket != null)
        {
            try
            {
                socket.shutdownInput();
            }
            catch(IOException ioexception) { }
            try
            {
                socket.shutdownOutput();
            }
            catch(IOException ioexception1) { }
            try
            {
                socket.close();
            }
            catch(IOException ioexception2) { }
        }
    }

    public static void close(ServerSocket serversocket)
    {
        if(serversocket != null)
            try
            {
                serversocket.close();
            }
            catch(IOException ioexception) { }
    }

    public static void close(DatagramSocket datagramsocket)
    {
        if(datagramsocket != null)
            datagramsocket.close();
    }

    public static boolean contains(Object aobj[], Object obj)
    {
        if(!$assertionsDisabled && aobj == null)
            throw new AssertionError();
        for(int i = 0; i < aobj.length; i++)
        {
            if(aobj[i] == null && obj == null)
                return true;
            if(aobj[i] == null || obj == null)
                return false;
            if(aobj[i].equals(obj))
                return true;
        }

        return false;
    }

    public static int getNumberOfCpus()
    {
        return Runtime.getRuntime().availableProcessors();
    }

    public static void debug(String s)
    {
        debug(s, null);
    }

    public static void debug(String s, Throwable throwable)
    {
        throw new IllegalStateException("Unguarded debug output in production build.");
    }

    public static String getEnvVersion()
    {
        Properties properties = System.getProperties();
        String s = properties.getProperty("os.name");
        String s1 = properties.getProperty("os.version");
        String s2 = properties.getProperty("java.specification.vendor");
        String s3 = properties.getProperty("java.specification.name");
        String s4 = properties.getProperty("java.specification.version");
        return s + " ver. " + s1 + ", " + s2 + ' ' + s3 + " ver. " + s4;
    }

    public static String getHexStr(String s)
    {
        StringBuffer stringbuffer = new StringBuffer();
        char ac[] = s.toCharArray();
        int i = ac.length;
        int j = i - 1;
        for(int k = 0; k < i; k++)
        {
            stringbuffer.append("0x" + Integer.toHexString(ac[k]));
            if(k < j)
                stringbuffer.append(", ");
        }

        return stringbuffer.toString();
    }

    public static String toHexString(byte byte0)
    {
        return new String(new char[] {
            getHexChar((byte)(byte0 >> 4)), getHexChar((byte)(byte0 & 0xf))
        });
    }

    public static char getHexChar(byte byte0)
    {
        if(!$assertionsDisabled && byte0 > 15)
            throw new AssertionError("HEX character byte cannot be greater that 15 [byte=" + byte0 + ']');
        switch(byte0 & 0xf)
        {
        case 0: // '\0'
        case 1: // '\001'
        case 2: // '\002'
        case 3: // '\003'
        case 4: // '\004'
        case 5: // '\005'
        case 6: // '\006'
        case 7: // '\007'
        case 8: // '\b'
        case 9: // '\t'
            return (char)(48 + (byte0 & 0xf));

        case 10: // '\n'
            return 'A';

        case 11: // '\013'
            return 'B';

        case 12: // '\f'
            return 'C';

        case 13: // '\r'
            return 'D';

        case 14: // '\016'
            return 'E';

        case 15: // '\017'
            return 'F';
        }
        if(!$assertionsDisabled)
            throw new AssertionError();
        else
            return '\uFFFF';
    }

    public static boolean intersects(Set set, Set set1)
    {
        if(!$assertionsDisabled && set == null)
            throw new AssertionError();
        if(!$assertionsDisabled && set1 == null)
            throw new AssertionError();
        Set set2 = set.size() >= set1.size() ? set1 : set;
        Set set3 = set2 != set ? set : set1;
        for(Iterator iterator = set2.iterator(); iterator.hasNext();)
            if(set3.contains(iterator.next()))
                return true;

        return false;
    }

    public static int parseInt(String s)
        throws UtilsException
    {
        try
        {
            return Integer.decode(s).intValue();
        }
        catch(NumberFormatException numberformatexception)
        {
            throw new UtilsException(L10n.format("KRNL.UTILS.PARSE.ERR1", s), numberformatexception);
        }
    }

    public static byte parseByte(String s)
        throws UtilsException
    {
        try
        {
            return Byte.decode(s).byteValue();
        }
        catch(NumberFormatException numberformatexception)
        {
            throw new UtilsException(L10n.format("KRNL.UTILS.PARSE.ERR1", s), numberformatexception);
        }
    }

    public static short parseShort(String s)
        throws UtilsException
    {
        try
        {
            return Short.decode(s).shortValue();
        }
        catch(NumberFormatException numberformatexception)
        {
            throw new UtilsException(L10n.format("KRNL.UTILS.PARSE.ERR1", s), numberformatexception);
        }
    }

    public static long parseLong(String s)
        throws UtilsException
    {
        try
        {
            return Long.decode(s).longValue();
        }
        catch(NumberFormatException numberformatexception)
        {
            throw new UtilsException(L10n.format("KRNL.UTILS.PARSE.ERR1", s), numberformatexception);
        }
    }

    public static float parseFloat(String s)
        throws UtilsException
    {
        try
        {
            return Float.parseFloat(s);
        }
        catch(NumberFormatException numberformatexception)
        {
            throw new UtilsException(L10n.format("KRNL.UTILS.PARSE.ERR1", s), numberformatexception);
        }
    }

    public static double parseDouble(String s)
        throws UtilsException
    {
        try
        {
            return Double.parseDouble(s);
        }
        catch(NumberFormatException numberformatexception)
        {
            throw new UtilsException(L10n.format("KRNL.UTILS.PARSE.ERR1", s), numberformatexception);
        }
    }

    public static boolean parseBoolean(String s)
        throws UtilsException
    {
        s = s.toLowerCase();
        if(s.equals("true") || s.equals("yes") || s.equals("on"))
            return true;
        if(s.equals("false") || s.equals("no") || s.equals("off"))
            return false;
        else
            throw new UtilsException(L10n.format("KRNL.UTILS.PARSE.ERR1", s));
    }

    public static char parseChar(String s)
        throws UtilsException
    {
        if(s.length() != 1)
            throw new UtilsException(L10n.format("KRNL.UTILS.PARSE.ERR1", s));
        else
            return s.charAt(0);
    }

    public static java.util.Date parseDateOrTime(String s)
        throws UtilsException
    {
        checkLocaleChange();
        ParsePosition parseposition = new ParsePosition(0);
        for(int i = 0; i < dateTimeFormatters.length; i++)
        {
            java.util.Date date = dateTimeFormatters[i].parse(s, parseposition);
            if(date != null)
                return date;
        }

        for(int j = 0; j < timeFormatters.length; j++)
        {
            java.util.Date date1 = timeFormatters[j].parse(s, parseposition);
            if(date1 != null)
                return date1;
        }

        throw new UtilsException(L10n.format("KRNL.UTILS.PARSE.ERR1", s));
    }

    public static java.util.Date parseDate(String s)
        throws UtilsException
    {
        checkLocaleChange();
        ParsePosition parseposition = new ParsePosition(0);
        for(int i = 0; i < dateTimeFormatters.length; i++)
        {
            java.util.Date date = dateTimeFormatters[i].parse(s, parseposition);
            if(date != null)
                return date;
        }

        throw new UtilsException(L10n.format("KRNL.UTILS.PARSE.ERR1", s));
    }

    public static java.util.Date parseTime(String s)
        throws UtilsException
    {
        checkLocaleChange();
        ParsePosition parseposition = new ParsePosition(0);
        for(int i = 0; i < timeFormatters.length; i++)
        {
            java.util.Date date = timeFormatters[i].parse(s, parseposition);
            if(date != null)
                return date;
        }

        throw new UtilsException(L10n.format("KRNL.UTILS.PARSE.ERR1", s));
    }

    public static int parseIpPort(String s)
        throws UtilsException
    {
        try
        {
            int i = Integer.decode(s).intValue();
            if(i < 0 || i > 65535)
                throw new UtilsException(L10n.format("KRNL.UTILS.PARSE.ERR1", s));
            else
                return i;
        }
        catch(NumberFormatException numberformatexception)
        {
            throw new UtilsException(L10n.format("KRNL.UTILS.PARSE.ERR1", s), numberformatexception);
        }
    }

    public static Inet6Address parseIpV6(String s)
        throws UtilsException
    {
        try
        {
            Inet6Address inet6address = (Inet6Address)InetAddress.getByName(s);
            return !inet6address.isLoopbackAddress() ? inet6address : (Inet6Address)InetAddress.getLocalHost();
        }
        catch(UnknownHostException unknownhostexception)
        {
            throw new UtilsException(L10n.format("KRNL.UTILS.PARSE.ERR1", s), unknownhostexception);
        }
        catch(ClassCastException classcastexception)
        {
            throw new UtilsException(L10n.format("KRNL.UTILS.PARSE.ERR1", s));
        }
    }

    public static InetAddress parseIp(String s)
        throws UtilsException
    {
        try
        {
            InetAddress inetaddress = InetAddress.getByName(s);
            InetAddress inetaddress1 = !inetaddress.isLoopbackAddress() ? inetaddress : InetAddress.getLocalHost();
            return inetaddress1;
        }
        catch(UnknownHostException unknownhostexception)
        {
            throw new UtilsException(L10n.format("KRNL.UTILS.PARSE.ERR1", s), unknownhostexception);
        }
    }

    public static Inet4Address parseIpV4(String s)
        throws UtilsException
    {
        try
        {
            Inet4Address inet4address = (Inet4Address)InetAddress.getByName(s);
            return !inet4address.isLoopbackAddress() ? inet4address : (Inet4Address)InetAddress.getLocalHost();
        }
        catch(UnknownHostException unknownhostexception)
        {
            throw new UtilsException(L10n.format("KRNL.UTILS.PARSE.ERR1", s), unknownhostexception);
        }
        catch(ClassCastException classcastexception)
        {
            throw new UtilsException(L10n.format("KRNL.UTILS.PARSE.ERR1", s), classcastexception);
        }
    }

    public static int encodeInt8(byte byte0, byte abyte0[], int i)
    {
        abyte0[i++] = byte0;
        return i;
    }

    public static byte decodeInt8(byte abyte0[], int i)
    {
        return abyte0[i];
    }

    public static int encodeInt16(short word0, byte abyte0[], int i)
    {
        abyte0[i++] = (byte)(word0 >>> 8);
        abyte0[i++] = (byte)word0;
        return i;
    }

    public static short decodeInt16(byte abyte0[], int i)
    {
        return (short)(((abyte0[i] & 0xff) << 8) + (abyte0[i + 1] & 0xff));
    }

    public static int encodeChar16(char c, byte abyte0[], int i)
    {
        abyte0[i++] = (byte)(c >>> 8);
        abyte0[i++] = (byte)c;
        return i;
    }

    public static char decodeChar16(byte abyte0[], int i)
    {
        return (char)(((abyte0[i] & 0xff) << 8) + (abyte0[i + 1] & 0xff));
    }

    public static int encodeInt32(int i, byte abyte0[], int j)
    {
        abyte0[j++] = (byte)(i >>> 24);
        abyte0[j++] = (byte)(i >>> 16);
        abyte0[j++] = (byte)(i >>> 8);
        abyte0[j++] = (byte)i;
        return j;
    }

    public static int decodeInt32(byte abyte0[], int i)
    {
        return ((abyte0[i] & 0xff) << 24) + ((abyte0[i + 1] & 0xff) << 16) + ((abyte0[i + 2] & 0xff) << 8) + (abyte0[i + 3] & 0xff);
    }

    public static int encodeInt64(long l, byte abyte0[], int i)
    {
        abyte0[i++] = (byte)(int)(l >>> 56);
        abyte0[i++] = (byte)(int)(l >>> 48);
        abyte0[i++] = (byte)(int)(l >>> 40);
        abyte0[i++] = (byte)(int)(l >>> 32);
        abyte0[i++] = (byte)(int)(l >>> 24);
        abyte0[i++] = (byte)(int)(l >>> 16);
        abyte0[i++] = (byte)(int)(l >>> 8);
        abyte0[i++] = (byte)(int)l;
        return i;
    }

    public static long decodeInt64(byte abyte0[], int i)
    {
        return (((long)abyte0[i] & 255L) << 56) + (((long)abyte0[i + 1] & 255L) << 48) + (((long)abyte0[i + 2] & 255L) << 40) + (((long)abyte0[i + 3] & 255L) << 32) + (((long)abyte0[i + 4] & 255L) << 24) + (((long)abyte0[i + 5] & 255L) << 16) + (((long)abyte0[i + 6] & 255L) << 8) + ((long)abyte0[i + 7] & 255L);
    }

    public static int encodeFloat32(float f, byte abyte0[], int i)
    {
        int j = Float.floatToIntBits(f);
        abyte0[i++] = (byte)(j >>> 24);
        abyte0[i++] = (byte)(j >>> 16);
        abyte0[i++] = (byte)(j >>> 8);
        abyte0[i++] = (byte)j;
        return i;
    }

    public static float decodeFloat32(byte abyte0[], int i)
    {
        return Float.intBitsToFloat(((abyte0[i] & 0xff) << 24) + ((abyte0[i + 1] & 0xff) << 16) + ((abyte0[i + 2] & 0xff) << 8) + (abyte0[i + 3] & 0xff));
    }

    public static int encodeFloat64(double d, byte abyte0[], int i)
    {
        long l = Double.doubleToLongBits(d);
        abyte0[i++] = (byte)(int)(l >>> 56);
        abyte0[i++] = (byte)(int)(l >>> 48);
        abyte0[i++] = (byte)(int)(l >>> 40);
        abyte0[i++] = (byte)(int)(l >>> 32);
        abyte0[i++] = (byte)(int)(l >>> 24);
        abyte0[i++] = (byte)(int)(l >>> 16);
        abyte0[i++] = (byte)(int)(l >>> 8);
        abyte0[i++] = (byte)(int)l;
        return i;
    }

    public static double decodeFloat64(byte abyte0[], int i)
    {
        return Double.longBitsToDouble((((long)abyte0[i] & 255L) << 56) + (((long)abyte0[i + 1] & 255L) << 48) + (((long)abyte0[i + 2] & 255L) << 40) + (((long)abyte0[i + 3] & 255L) << 32) + (((long)abyte0[i + 4] & 255L) << 24) + (((long)abyte0[i + 5] & 255L) << 16) + (((long)abyte0[i + 6] & 255L) << 8) + ((long)abyte0[i + 7] & 255L));
    }

    public static int encodeBool(boolean flag, byte abyte0[], int i)
    {
        abyte0[i] = ((byte)(!flag ? 0 : 1));
        return i + 1;
    }

    public static boolean decodeBool(byte abyte0[], int i)
    {
        return abyte0[i] != 0;
    }

    public static int encodeUtf8Str(String s, byte abyte0[], int i)
    {
        if(!$assertionsDisabled && abyte0 == null)
            throw new AssertionError();
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        int j = s.length();
        i = encodeInt32(j, abyte0, i);
        for(int k = 0; k < j; k++)
        {
            char c = s.charAt(k);
            if(c >= '\001' && c <= '\177')
            {
                abyte0[i++] = (byte)c;
                continue;
            }
            if(c == 0 || '\200' <= c && c <= '\u07FF')
            {
                abyte0[i++] = (byte)(c >> 6 | 0xc0);
                abyte0[i++] = (byte)(c & 0x3f | 0x80);
            } else
            {
                abyte0[i++] = (byte)(c >> 12 | 0xe0);
                abyte0[i++] = (byte)(c >> 6 | 0xc0);
                abyte0[i++] = (byte)(c & 0x3f | 0x80);
            }
        }

        return i;
    }

    public static int decodeUtf8Str(byte abyte0[], StringBuffer stringbuffer, int i)
        throws JobException
    {
        if(!$assertionsDisabled && abyte0 == null)
            throw new AssertionError();
        if(!$assertionsDisabled && stringbuffer == null)
            throw new AssertionError();
        int j = decodeInt32(abyte0, i);
        if(j >= abyte0.length - i)
            throw new JobException(L10n.format("SRVC.JOBS.IMPL.ERR30"));
        i += 4;
        char ac[] = new char[j];
        int k = 0;
        for(int l = 0; k < j; l++)
        {
            byte byte0 = abyte0[i++];
            if(byte0 >= 1 && byte0 <= 127)
                ac[l] = (char)byte0;
            else
            if((byte0 & 0xe0) == 192)
            {
                byte byte1 = abyte0[i++];
                ac[l] = (char)((byte0 & 0x1f) << 6 | byte1 & 0x3f);
            } else
            {
                byte byte2 = abyte0[i++];
                byte byte3 = abyte0[i++];
                ac[l] = (char)((byte0 & 0xf) << 12 | (byte2 & 0x3f) << 6 | byte3 & 0x3f);
            }
            k++;
        }

        stringbuffer.append(new String(ac));
        return i;
    }

    public static int getUtf8StrSize(String s)
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        int i = s.length();
        int j = 4;
        for(int k = 0; k < i; k++)
        {
            char c = s.charAt(k);
            if(c >= '\001' && c <= '\177')
            {
                j++;
                continue;
            }
            if(c == 0 || '\200' <= c && c <= '\u07FF')
                j += 2;
            else
                j += 3;
        }

        return j;
    }

    public static Object convertNumeric(Class class1, Object obj)
        throws UtilsException
    {
        if(!$assertionsDisabled && (class1 == null || obj == null))
            throw new AssertionError();
        BigDecimal bigdecimal = null;
        if((obj instanceof BigDecimal))
            bigdecimal = (BigDecimal)obj;
        else
        if((obj instanceof Long))
            bigdecimal = new BigDecimal(((Long)obj).longValue());
        else
        if((obj instanceof Integer))
            bigdecimal = new BigDecimal(((Integer)obj).intValue());
        else
        if((obj instanceof Short))
            bigdecimal = new BigDecimal(((Short)obj).shortValue());
        else
        if((obj instanceof Byte))
            bigdecimal = new BigDecimal(((Byte)obj).byteValue());
        else
        if((obj instanceof Double))
            bigdecimal = new BigDecimal(((Double)obj).doubleValue());
        else
        if((obj instanceof Float))
            bigdecimal = new BigDecimal(((Float)obj).floatValue());
        else
            throw new UtilsException(L10n.format("KRNL.UTILS.CONV.ERR1", obj));
        if(class1 == Long.TYPE || class1 == (Long.class))
            if(bigdecimal.compareTo(BIGDEC_MAX_LONG) <= 0 && bigdecimal.compareTo(BIGDEC_MIN_LONG) >= 0)
                return new Long(bigdecimal.longValue());
            else
                throw new UtilsException(L10n.format("KRNL.UTILS.CONV.ERR3", obj, class1));
        if(class1 == Integer.TYPE || class1 == (Integer.class))
            if(bigdecimal.compareTo(BIGDEC_MAX_INT) <= 0 && bigdecimal.compareTo(BIGDEC_MIN_INT) >= 0)
                return new Integer(bigdecimal.intValue());
            else
                throw new UtilsException(L10n.format("KRNL.UTILS.CONV.ERR3", obj, class1));
        if(class1 == Short.TYPE || class1 == (Short.class))
            if(bigdecimal.compareTo(BIGDEC_MAX_SHORT) <= 0 && bigdecimal.compareTo(BIGDEC_MIN_SHORT) >= 0)
                return new Short(bigdecimal.shortValue());
            else
                throw new UtilsException(L10n.format("KRNL.UTILS.CONV.ERR3", obj, class1));
        if(class1 == Byte.TYPE || class1 == (Byte.class))
            if(bigdecimal.compareTo(BIGDEC_MAX_BYTE) <= 0 && bigdecimal.compareTo(BIGDEC_MIN_BYTE) >= 0)
                return new Byte(bigdecimal.byteValue());
            else
                throw new UtilsException(L10n.format("KRNL.UTILS.CONV.ERR3", obj, class1));
        if(class1 == Double.TYPE || class1 == (Double.class))
            if(bigdecimal.compareTo(BIGDEC_MAX_DOUBLE) <= 0 && bigdecimal.compareTo(BIGDEC_MIN_DOUBLE) >= 0)
                return new Double(bigdecimal.doubleValue());
            else
                throw new UtilsException(L10n.format("KRNL.UTILS.CONV.ERR3", obj, class1));
        if(class1 == Float.TYPE || class1 == (Float.class))
        {
            if(bigdecimal.compareTo(BIGDEC_MAX_FLOAT) <= 0 && bigdecimal.compareTo(BIGDEC_MIN_FLOAT) >= 0)
                return new Float(bigdecimal.floatValue());
            else
                throw new UtilsException(L10n.format("KRNL.UTILS.CONV.ERR3", obj, class1));
        } else
        {
            throw new UtilsException(L10n.format("KRNL.UTILS.CONV.ERR2", class1));
        }
    }

    public static Object convertDate(Class class1, Object obj)
        throws UtilsException
    {
        if(!$assertionsDisabled && (class1 == null || obj == null))
            throw new AssertionError();
        if(obj.getClass() == class1)
            return obj;
        long l = 0L;
        if((obj instanceof Timestamp))
            l = ((Timestamp)obj).getTime();
        else
        if((obj instanceof Date))
            l = ((Date)obj).getTime();
        else
        if((obj instanceof Time))
            l = ((Time)obj).getTime();
        else
            throw new UtilsException(L10n.format("KRNL.UTILS.CONV.ERR4", obj));
        if(class1 == (Timestamp.class))
            return new Timestamp(l);
        if(class1 == (Date.class))
            return new Date(l);
        if(class1 == (Time.class))
            return new Time(l);
        else
            throw new UtilsException(L10n.format("KRNL.UTILS.CONV.ERR5", class1));
    }

    public static Object getDbValue(int i, ResultSet resultset, int j)
        throws SQLException
    {
        switch(i)
        {
        case 93: // ']'
            return resultset.getTimestamp(j);
        }
        return resultset.getObject(j);
    }

    private static final long DFLT_SAFE_ACQUIRE_TIMEOUT = 100L;
    private static DateFormat dateTimeFormatters[] = null;
    private static DateFormat timeFormatters[] = null;
    private static Locale lastLocale = null;
    private static Object localeMutex = new Object();
    public static final String NEW_LINE = System.getProperty("line.separator");
    public static final String USER_NAME = System.getProperty("user.name");
    public static final String USER_HOME = System.getProperty("user.home");
    public static final String USER_DIR = System.getProperty("user.dir");
    private static final BigDecimal BIGDEC_MAX_LONG = new BigDecimal(9.2233720368547758E+018D);
    private static final BigDecimal BIGDEC_MIN_LONG = new BigDecimal(-9.2233720368547758E+018D);
    private static final BigDecimal BIGDEC_MAX_INT = new BigDecimal(2147483647D);
    private static final BigDecimal BIGDEC_MIN_INT = new BigDecimal(-2147483648D);
    private static final BigDecimal BIGDEC_MIN_FLOAT = new BigDecimal(-3.4028234663852886E+038D);
    private static final BigDecimal BIGDEC_MAX_FLOAT = new BigDecimal(3.4028234663852886E+038D);
    private static final BigDecimal BIGDEC_MIN_DOUBLE = new BigDecimal(-1.7976931348623157E+308D);
    private static final BigDecimal BIGDEC_MAX_DOUBLE = new BigDecimal(1.7976931348623157E+308D);
    private static final BigDecimal BIGDEC_MIN_SHORT = new BigDecimal(-32768D);
    private static final BigDecimal BIGDEC_MAX_SHORT = new BigDecimal(32767D);
    private static final BigDecimal BIGDEC_MIN_BYTE = new BigDecimal(-128D);
    private static final BigDecimal BIGDEC_MAX_BYTE = new BigDecimal(127D);
    private static InetAddress localAddrs[];
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(Utils.class).desiredAssertionStatus();
        try
        {
            localAddrs = InetAddress.getAllByName(InetAddress.getLocalHost().getHostName());
            System.setProperty("xtier.host.name", localAddrs[0].getHostName());
        }
        catch(UnknownHostException unknownhostexception)
        {
            throw new IllegalStateException("xTier cannot be started on network-less node.");
        }
    }
}
