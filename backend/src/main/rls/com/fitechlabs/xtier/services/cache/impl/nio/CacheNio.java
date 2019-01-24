// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.cache.impl.nio;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.cache.CacheException;
import com.fitechlabs.xtier.services.cache.impl.CacheImpl;
import com.fitechlabs.xtier.services.cache.impl.CacheRegistry;
import com.fitechlabs.xtier.services.cache.impl.pools.CacheByteBuffer;
import com.fitechlabs.xtier.services.cluster.ClusterNode;
import com.fitechlabs.xtier.services.cluster.ClusterService;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.services.marshal.MarshalService;
import com.fitechlabs.xtier.services.marshal.NioMarshaller;
import com.fitechlabs.xtier.services.objpool.ObjectPool;
import com.fitechlabs.xtier.services.objpool.ObjectPoolException;
import com.fitechlabs.xtier.services.objpool.threads.ThreadPool;
import com.fitechlabs.xtier.threads.SysThread;
import com.fitechlabs.xtier.utils.Utils;
import com.fitechlabs.xtier.utils.boxed.sync.BoxedBooleanSync;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.cache.impl.nio:
//            CacheNioListener

public class CacheNio
{
    private class NioWorker extends SysThread
    {

        public void interrupt()
        {
            interrupted.set(true);
            synchronized(selectorLock)
            {
                if(multiplexor != null)
                    multiplexor.wakeup();
            }
            super.interrupt();
        }

        protected void body()
        {
            ThreadPool threadpool = registry.getPool();
            while(!interrupted.get()) 
            {
                checkInterrupted();
                try
                {
                    synchronized(selectorLock)
                    {
                        if(multiplexor == null)
                            createMultiplexor();
                    }
                    int i = multiplexor.select();
                    if(i > 0)
                    {
                        Iterator iterator = multiplexor.selectedKeys().iterator();
                        do
                        {
                            if(!iterator.hasNext())
                                break;
                            SelectionKey selectionkey = (SelectionKey)iterator.next();
                            final NioChannel ch = (NioChannel)selectionkey.attachment();
                            final CacheByteBuffer buf = aquire(ch.getBufPool());
                            try
                            {
                                if(((DatagramChannel)selectionkey.channel()).receive(buf.getBuf()) == null)
                                {
                                    release(buf);
                                    continue;
                                }
                            }
                            catch(IOException ioexception1)
                            {
                                release(buf);
                                if(!isInterrupted() && !interrupted.get())
                                    throw ioexception1;
                                continue;
                            }
                            iterator.remove();
                            buf.getBuf().flip();
                            int j = encoder.decodeInt32(buf.getBuf());
                            if(j == localNodeId)
                            {
                                release(buf);
                            } else
                            {
                                buf.getBuf().rewind();
                                threadpool.addTask(new Runnable() {

                                    public void run()
                                    {
                                        ch.getListener().onReceive(buf.getBuf());
                                        release(buf);
                                        break MISSING_BLOCK_LABEL_47;
                                        Exception exception1;
                                        exception1;
                                        release(buf);
                                        throw exception1;
                                    }

                
                {
                    super();
                }
                                }
);
                            }
                        } while(true);
                    } else
                    {
                        resetAndWait();
                    }
                    throttleErrors = false;
                }
                catch(IOException ioexception)
                {
                    if(!throttleErrors)
                    {
                        registry.getLogger().error(L10n.format("SRVC.CACHE.ERR32"), ioexception);
                        throttleErrors = true;
                    }
                    resetAndWait();
                }
            }
        }

        protected void cleanup()
        {
            synchronized(selectorLock)
            {
                closeMultiplexor();
            }
        }

        private void resetAndWait()
        {
            if(!interrupted.get())
            {
                synchronized(selectorLock)
                {
                    closeMultiplexor();
                    multiplexor = null;
                }
                Utils.sleep(2000L);
            }
        }

        private void closeMultiplexor()
        {
            synchronized(selectorLock)
            {
                if(multiplexor != null)
                {
                    for(Iterator iterator = multiplexor.keys().iterator(); iterator.hasNext(); Utils.close((DatagramChannel)((SelectionKey)iterator.next()).channel()));
                    try
                    {
                        multiplexor.close();
                    }
                    catch(IOException ioexception) { }
                }
            }
        }

        private void createMultiplexor()
            throws IOException
        {
            synchronized(selectorLock)
            {
                multiplexor = Selector.open();
                int i = 0;
                for(int j = channels.size(); i < j; i++)
                    createChannel((NioChannel)channels.get(i));

            }
        }

        private void createChannel(NioChannel niochannel)
            throws IOException
        {
            DatagramChannel datagramchannel = DatagramChannel.open();
            DatagramSocket datagramsocket = datagramchannel.socket();
            datagramsocket.setReuseAddress(true);
            datagramsocket.bind(niochannel.getLocalBind());
            datagramchannel.configureBlocking(false);
            datagramchannel.register(multiplexor, 1, niochannel);
        }

        private CacheByteBuffer aquire(ObjectPool objectpool)
        {
            try
            {
                return (CacheByteBuffer)objectpool.acquire();
            }
            catch(ObjectPoolException objectpoolexception)
            {
                if(!$assertionsDisabled)
                {
                    throw new AssertionError();
                } else
                {
                    registry.getLogger().error(L10n.format("SRVC.CACHE.ERR43"), objectpoolexception);
                    return null;
                }
            }
        }

        private void release(CacheByteBuffer cachebytebuffer)
        {
            try
            {
                cachebytebuffer.release();
            }
            catch(ObjectPoolException objectpoolexception)
            {
                if(!$assertionsDisabled)
                    throw new AssertionError();
                registry.getLogger().error(L10n.format("SRVC.CACHE.ERR42"), objectpoolexception);
            }
        }

        private boolean throttleErrors;
        private BoxedBooleanSync interrupted;
        private final Object selectorLock = new Object();
        static final boolean $assertionsDisabled; /* synthetic field */



        NioWorker(String s, int i)
            throws IOException
        {
            super(s + "-nio-worker", i);
            throttleErrors = false;
            interrupted = new BoxedBooleanSync(false);
            createMultiplexor();
        }
    }

    private class NioChannel
    {

        public ObjectPool getBufPool()
        {
            return bufPool;
        }

        public InetSocketAddress getLocalBind()
        {
            return localBind;
        }

        public CacheNioListener getListener()
        {
            return listener;
        }

        private final CacheNioListener listener;
        private final InetSocketAddress localBind;
        private final ObjectPool bufPool;
        static final boolean $assertionsDisabled; /* synthetic field */


        public NioChannel(CacheNioListener cacheniolistener, InetSocketAddress inetsocketaddress, ObjectPool objectpool)
        {
            super();
            if(!$assertionsDisabled && inetsocketaddress == null)
                throw new AssertionError();
            if(!$assertionsDisabled && objectpool == null)
            {
                throw new AssertionError();
            } else
            {
                listener = cacheniolistener;
                localBind = inetsocketaddress;
                bufPool = objectpool;
                return;
            }
        }
    }


    public CacheNio(CacheRegistry cacheregistry)
        throws CacheException
    {
        channels = new ArrayList();
        registry = cacheregistry;
    }

    public void register(CacheNioListener cacheniolistener, int i, ObjectPool objectpool)
    {
        java.net.InetAddress inetaddress = XtierKernel.getInstance().cluster().getLocalNode().getAddress();
        channels.add(new NioChannel(cacheniolistener, new InetSocketAddress(inetaddress, i), objectpool));
    }

    public void start()
        throws CacheException
    {
        try
        {
            nioWorker = new NioWorker(registry.getCache().getName(), registry.getPool().getPriority());
        }
        catch(IOException ioexception)
        {
            throw new CacheException(L10n.format("SRVC.CACHE.ERR32"), ioexception);
        }
        nioWorker.start();
    }

    public void stop()
    {
        Utils.stopThread(nioWorker);
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

    private static final int ERROR_WAIT_TIME = 2000;
    private CacheRegistry registry;
    private final NioMarshaller encoder = XtierKernel.getInstance().marshal().getNioMarshaller();
    private final int localNodeId = XtierKernel.getInstance().cluster().getLocalNode().getNodeId();
    private List channels;
    private SysThread nioWorker;
    private Selector multiplexor;
    static Class class$com$fitechlabs$xtier$services$cache$impl$nio$CacheNio; /* synthetic field */






}
