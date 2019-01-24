// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.cache.impl;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.cache.CacheException;
import com.fitechlabs.xtier.services.cluster.*;
import com.fitechlabs.xtier.services.cluster.filters.ClusterNodeTypeFilter;
import com.fitechlabs.xtier.services.cluster.filters.ClusterServiceFilter;
import com.fitechlabs.xtier.services.log.LogService;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.services.marshal.IoMarshaller;
import com.fitechlabs.xtier.services.marshal.MarshalService;
import com.fitechlabs.xtier.threads.SysThread;
import com.fitechlabs.xtier.utils.Utils;
import com.fitechlabs.xtier.utils.boxed.BoxedInt32;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.cache.impl:
//            CacheImpl

public class CacheStartupManager
    implements ClusterListener
{
    private class StartupListener extends SysThread
    {

        public void interrupt()
        {
            super.interrupt();
            Utils.close(server);
        }

        protected void cleanup()
        {
            Utils.close(server);
        }

        private void createServer()
            throws IOException
        {
            server = new ServerSocket(port, 50, cluster.getLocalNode().getAddress());
        }

        protected void body()
        {
_L2:
            checkInterrupted();
            Socket socket;
            InputStream inputstream;
            OutputStream outputstream;
            if(server == null)
                createServer();
            socket = server.accept();
            inputstream = socket.getInputStream();
            outputstream = socket.getOutputStream();
            inputstream = socket.getInputStream();
            int i = encoder.decodeInt32(inputstream);
            byte byte0 = encoder.decodeInt8(inputstream);
            ClusterNode clusternode = cluster.getNode(i);
            byte byte1 = 3;
            if(clusternode == null)
            {
                logger.error(L10n.format("SRVC.CACHE.ERR54", new Integer(i)));
                byte1 = 4;
            }
            if(byte1 == 3)
                if(byte0 == 1)
                {
                    synchronized(mutex)
                    {
                        cacheNodes.put(new BoxedInt32(i), clusternode);
                    }
                    onTopologyEvent();
                } else
                {
                    if(!$assertionsDisabled && byte0 != 2)
                        throw new AssertionError();
                    synchronized(mutex)
                    {
                        cacheNodes.remove(new BoxedInt32(i));
                    }
                    onTopologyEvent();
                }
            outputstream = socket.getOutputStream();
            encoder.encodeInt8(byte1, outputstream);
            outputstream.flush();
            Utils.close(inputstream);
            Utils.close(outputstream);
            Utils.close(socket);
            break MISSING_BLOCK_LABEL_364;
            IOException ioexception1;
            ioexception1;
            logger.error(L10n.format("SRVC.CACHE.ERR55"), ioexception1);
            Utils.close(inputstream);
            Utils.close(outputstream);
            Utils.close(socket);
            continue; /* Loop/switch isn't completed */
            Exception exception2;
            exception2;
            Utils.close(inputstream);
            Utils.close(outputstream);
            Utils.close(socket);
            throw exception2;
            isThrottle = false;
            continue; /* Loop/switch isn't completed */
            IOException ioexception;
            ioexception;
            if(!isInterrupted())
            {
                if(!isThrottle)
                {
                    logger.error(L10n.format("SRVC.CACHE.ERR55"), ioexception);
                    isThrottle = true;
                }
                Utils.close(server);
                server = null;
                Utils.sleep(1000L);
            }
            if(true) goto _L2; else goto _L1
_L1:
        }

        private ServerSocket server;
        private boolean isThrottle;
        static final boolean $assertionsDisabled; /* synthetic field */


        StartupListener()
            throws CacheException
        {
            super("cache-startup-listener");
            server = null;
            isThrottle = false;
            try
            {
                createServer();
            }
            catch(IOException ioexception)
            {
                throw new CacheException("Error trying to start listening on port: " + port, ioexception);
            }
        }
    }

    private class SocketHolder
    {

        InputStream getIn()
        {
            return in;
        }

        OutputStream getOut()
        {
            return out;
        }

        Socket getSock()
        {
            return sock;
        }

        private Socket sock;
        private InputStream in;
        private OutputStream out;

        SocketHolder(Socket socket)
            throws IOException
        {
            super();
            sock = socket;
            in = socket.getInputStream();
            out = socket.getOutputStream();
        }
    }


    CacheStartupManager(int i, Map map)
    {
        if(!$assertionsDisabled && map == null)
        {
            throw new AssertionError();
        } else
        {
            port = i;
            caches = map;
            XtierKernel xtierkernel = XtierKernel.getInstance();
            cluster = xtierkernel.cluster();
            encoder = xtierkernel.marshal().getIoMarshaller();
            logger = xtierkernel.log().getLogger("cache-startup");
            return;
        }
    }

    public Set getValidNodes(Set set)
    {
        if(!set.isEmpty())
        {
            BoxedInt32 boxedint32 = new BoxedInt32(0);
            for(Iterator iterator = set.iterator(); iterator.hasNext();)
            {
                ClusterNode clusternode = (ClusterNode)iterator.next();
                boxedint32.set(clusternode.getNodeId());
                synchronized(mutex)
                {
                    if(!cacheNodes.containsKey(boxedint32))
                        iterator.remove();
                }
            }

        }
        return set;
    }

    void start()
        throws CacheException
    {
        listener = new StartupListener();
        synchronized(mutex)
        {
            Set set = cluster.getNodes(new ClusterServiceFilter("cache", new ClusterNodeTypeFilter(false, true, true)));
            cacheNodes = new HashMap(Utils.getNonRehashCapacity(set.size()));
            ClusterNode clusternode;
            for(Iterator iterator = set.iterator(); iterator.hasNext(); cacheNodes.put(new BoxedInt32(clusternode.getNodeId()), clusternode))
            {
                clusternode = (ClusterNode)iterator.next();
                if(!$assertionsDisabled && !clusternode.hasService("cache"))
                    throw new AssertionError();
            }

            cluster.addListener(this);
            listener.start();
        }
        onEvent((byte)1);
    }

    void stop()
        throws CacheException
    {
        onEvent((byte)2);
        Utils.stopThread(listener);
    }

    public void onNodeEvent(int i, ClusterNode clusternode, int j)
    {
        synchronized(mutex)
        {
            if(clusternode.hasService("cache"))
                switch(i)
                {
                case 3: // '\003'
                    cacheNodes.remove(new BoxedInt32(clusternode.getNodeId()));
                    break;
                }
        }
    }

    private boolean hasCacheNodeId(int i)
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return cacheNodes.containsKey(new BoxedInt32(i));
        Exception exception;
        exception;
        throw exception;
    }

    private void onEvent(byte byte0)
        throws CacheException
    {
        ClusterNode clusternode;
        CacheException cacheexception;
        clusternode = cluster.getLocalNode();
        cacheexception = null;
        Object obj = null;
        Object obj1 = null;
_L6:
        HashMap hashmap;
        HashMap hashmap1;
        boolean flag;
        flag = true;
        synchronized(mutex)
        {
            hashmap = new HashMap(cacheNodes);
        }
        hashmap1 = new HashMap(hashmap.size());
        Iterator iterator = hashmap.values().iterator();
_L4:
        ClusterNode clusternode1;
        SocketHolder socketholder;
        if(!iterator.hasNext())
            break; /* Loop/switch isn't completed */
        clusternode1 = (ClusterNode)iterator.next();
        socketholder = null;
_L2:
        socketholder = new SocketHolder(new Socket(clusternode1.getAddress(), port, clusternode.getAddress(), 0));
        hashmap1.put(clusternode1, socketholder);
        encoder.encodeInt32(clusternode.getNodeId(), socketholder.getOut());
        encoder.encodeInt8(byte0, socketholder.getOut());
        continue; /* Loop/switch isn't completed */
        IOException ioexception;
        ioexception;
        close(socketholder);
        hashmap1.remove(clusternode1);
        if(byte0 != 1)
            break; /* Loop/switch isn't completed */
        if(!clusternode1.isActive() || !hasCacheNodeId(clusternode1.getNodeId()) || clusternode1.checkFailureStatus())
            continue; /* Loop/switch isn't completed */
        logger.warning(L10n.format("SRVC.CACHE.ERR51", new Long(300L), clusternode1), ioexception);
        Utils.sleep(1000L);
        if(true) goto _L2; else goto _L1
_L1:
        if(!$assertionsDisabled && byte0 != 2)
            throw new AssertionError();
        if(hasCacheNodeId(clusternode1.getNodeId()) && clusternode1.isActive() && !clusternode1.checkFailureStatus() && cacheexception == null)
            cacheexception = new CacheException(L10n.format("SRVC.CACHE.ERR56", clusternode1), ioexception);
        if(true) goto _L4; else goto _L3
_L3:
        Iterator iterator1 = hashmap1.entrySet().iterator();
        do
        {
            if(!iterator1.hasNext())
                break;
            Map.Entry entry = (Map.Entry)iterator1.next();
            ClusterNode clusternode2 = (ClusterNode)entry.getKey();
            SocketHolder socketholder1 = (SocketHolder)entry.getValue();
            try
            {
                byte byte1 = encoder.decodeInt8(socketholder1.getIn());
                if(byte1 != 3)
                    throw new CacheException(L10n.format("SRVC.CACHE.ERR53", clusternode2));
                continue;
            }
            catch(IOException ioexception1) { }
            if(!clusternode2.isActive() || !hasCacheNodeId(clusternode2.getNodeId()) || clusternode2.checkFailureStatus())
                continue;
            flag = false;
            Utils.sleep(1000L);
            break;
        } while(true);
        continue; /* Loop/switch isn't completed */
        local;
        for(Iterator iterator2 = hashmap1.values().iterator(); iterator2.hasNext(); close((SocketHolder)iterator2.next()));
        JVM INSTR ret 13;
        if(!flag) goto _L6; else goto _L5
_L5:
        if(cacheexception != null)
            throw cacheexception;
        else
            return;
    }

    private void onTopologyEvent()
    {
        for(Iterator iterator = caches.values().iterator(); iterator.hasNext(); ((CacheImpl)iterator.next()).onTopologyEvent());
    }

    private void close(SocketHolder socketholder)
    {
        if(socketholder != null)
        {
            Utils.close(socketholder.getIn());
            Utils.close(socketholder.getOut());
            Utils.close(socketholder.getSock());
        }
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

    private static final long ERR_WAIT_TIME = 1000L;
    private static final byte START = 1;
    private static final byte STOP = 2;
    private static final byte SUCCESS = 3;
    private static final byte FAILURE = 4;
    private int port;
    private ClusterService cluster;
    private Logger logger;
    private Map cacheNodes;
    private IoMarshaller encoder;
    private StartupListener listener;
    private Map caches;
    private final Object mutex = new Object();
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(CacheStartupManager.class).desiredAssertionStatus();
    }







}
