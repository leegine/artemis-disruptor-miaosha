// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.cache.impl;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.cache.CacheException;
import com.fitechlabs.xtier.services.cache.CacheTopology;
import com.fitechlabs.xtier.services.cache.impl.nio.CacheNio;
import com.fitechlabs.xtier.services.cache.impl.nio.CacheNioListener;
import com.fitechlabs.xtier.services.cache.impl.pools.CacheByteBuffer;
import com.fitechlabs.xtier.services.cache.impl.pools.CacheByteBufferFactory;
import com.fitechlabs.xtier.services.cache.impl.pools.CacheDgramChannel;
import com.fitechlabs.xtier.services.cache.impl.pools.CacheDgramChannelFactory;
import com.fitechlabs.xtier.services.cluster.ClusterNode;
import com.fitechlabs.xtier.services.cluster.ClusterService;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.services.marshal.MarshalService;
import com.fitechlabs.xtier.services.marshal.NioMarshaller;
import com.fitechlabs.xtier.services.objpool.*;
import com.fitechlabs.xtier.services.objpool.adapters.PoolObjectAbstractAdapter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.cache.impl:
//            CacheChannel, CacheTxEntry, CacheTxGroup, CacheRegistry,
//            CacheConfig, CacheUtils, CacheTxImpl, CacheTxSet,
//            CacheGroup

public class CacheCommManager
{
    protected class DeadlockReply
    {

        public void onReply(int i, boolean flag)
        {
            if(!$assertionsDisabled && !Thread.holdsLock(this))
                throw new AssertionError();
            if(flag)
                detected = true;
            int j = 0;
            do
            {
                if(j >= nodes.length)
                    break;
                if(nodes[j] != null && nodes[j].getNodeId() == i)
                {
                    nodes[j] = null;
                    cnt--;
                    break;
                }
                j++;
            } while(true);
        }

        public boolean isAllReplied()
        {
            if(!$assertionsDisabled && !Thread.holdsLock(this))
                throw new AssertionError();
            else
                return cnt == 0;
        }

        public boolean isDeadlocked()
        {
            return detected;
        }

        public ClusterNode[] getNodesArr()
        {
            return nodes;
        }

        public List getNodesList()
        {
            if(!$assertionsDisabled && !Thread.holdsLock(this))
                throw new AssertionError();
            if(cnt == 0)
                return Collections.EMPTY_LIST;
            if(!$assertionsDisabled && cnt <= 0)
                throw new AssertionError();
            ArrayList arraylist = new ArrayList(cnt);
            for(int i = 0; i < nodes.length; i++)
                if(nodes[i] != null)
                    arraylist.add(nodes[i]);

            return arraylist;
        }

        private ClusterNode nodes[];
        private boolean detected;
        private int cnt;
        static final boolean $assertionsDisabled; /* synthetic field */


        public DeadlockReply(Set set)
        {
            super();
            detected = false;
            if(!$assertionsDisabled && set == null)
                throw new AssertionError();
            if(!$assertionsDisabled && set.isEmpty())
            {
                throw new AssertionError();
            } else
            {
                nodes = (ClusterNode[])set.toArray(new ClusterNode[set.size()]);
                cnt = set.size();
                return;
            }
        }

        public DeadlockReply(ClusterNode clusternode)
        {
            super();
            detected = false;
            if(!$assertionsDisabled && clusternode == null)
            {
                throw new AssertionError();
            } else
            {
                nodes = (new ClusterNode[] {
                    clusternode
                });
                cnt = 1;
                return;
            }
        }
    }

    protected class CacheReply
    {

        public void onReply(int i, boolean flag)
        {
            if(!$assertionsDisabled && !Thread.holdsLock(this))
                throw new AssertionError();
            if(!flag)
                success = false;
            int j = 0;
            do
            {
                if(j >= nodes.length)
                    break;
                if(nodes[j] != null && nodes[j].getNodeId() == i)
                {
                    nodes[j] = null;
                    cnt--;
                    break;
                }
                j++;
            } while(true);
        }

        public boolean isAllReplied()
        {
            if(!$assertionsDisabled && !Thread.holdsLock(this))
                throw new AssertionError();
            else
                return cnt == 0;
        }

        public boolean isSuccess()
        {
            return success;
        }

        public ClusterNode[] getNodesArr()
        {
            return nodes;
        }

        public List getNodesList()
        {
            if(!$assertionsDisabled && !Thread.holdsLock(this))
                throw new AssertionError();
            if(cnt == 0)
                return Collections.EMPTY_LIST;
            if(!$assertionsDisabled && cnt <= 0)
                throw new AssertionError();
            ArrayList arraylist = new ArrayList(cnt);
            for(int i = 0; i < nodes.length; i++)
                if(nodes[i] != null)
                    arraylist.add(nodes[i]);

            return arraylist;
        }

        private ClusterNode nodes[];
        private boolean success;
        private int cnt;
        static final boolean $assertionsDisabled; /* synthetic field */


        public CacheReply(Set set)
        {
            super();
            success = true;
            if(!$assertionsDisabled && set == null)
                throw new AssertionError();
            if(!$assertionsDisabled && set.isEmpty())
            {
                throw new AssertionError();
            } else
            {
                nodes = (ClusterNode[])set.toArray(new ClusterNode[set.size()]);
                cnt = set.size();
                return;
            }
        }

        public CacheReply(ClusterNode clusternode)
        {
            super();
            success = true;
            if(!$assertionsDisabled && clusternode == null)
            {
                throw new AssertionError();
            } else
            {
                nodes = (new ClusterNode[] {
                    clusternode
                });
                cnt = 1;
                return;
            }
        }
    }


    protected CacheCommManager(CacheRegistry cacheregistry, int i)
        throws CacheException
    {
        if(!$assertionsDisabled && cacheregistry == null)
        {
            throw new AssertionError();
        } else
        {
            registry = cacheregistry;
            objpool = XtierKernel.getInstance().objpool();
            cluster = XtierKernel.getInstance().cluster();
            encoder = XtierKernel.getInstance().marshal().getNioMarshaller();
            localNode = cluster.getLocalNode();
            nio = new CacheNio(cacheregistry);
            return;
        }
    }

    public void start()
        throws CacheException
    {
        try
        {
            dgramPool = objpool.createPool(registry.getConfig().getCacheName() + "-dgram", new CacheDgramChannelFactory(new InetSocketAddress(localNode.getAddress(), 0)));
        }
        catch(ObjectPoolException objectpoolexception)
        {
            throw new CacheException(L10n.format("SRVC.CACHE.ERR13"), objectpoolexception);
        }
        nio.start();
    }

    public void stop()
    {
        nio.stop();
        delete(dgramPool);
    }

    protected ObjectPool registerNio(CacheNioListener cacheniolistener, int i, String s, int j)
        throws CacheException
    {
        try
        {
            ObjectPool objectpool = objpool.createPool(s, new CacheByteBufferFactory(j));
            nio.register(cacheniolistener, i, objectpool);
            return objectpool;
        }
        catch(ObjectPoolException objectpoolexception)
        {
            throw new CacheException(L10n.format("SRVC.CACHE.ERR40", s), objectpoolexception);
        }
    }

    protected void delete(ObjectPool objectpool)
    {
        try
        {
            objpool.deletePool(objectpool.getName());
        }
        catch(ObjectPoolException objectpoolexception)
        {
            registry.getLogger().error(L10n.format("SRVC.CACHE.ERR41", objectpool.getName()), objectpoolexception);
        }
    }

    protected CacheDgramChannel aquireCh(ObjectPool objectpool)
        throws CacheException
    {
        return (CacheDgramChannel)CacheUtils.aquire(objectpool);
    }

    protected CacheByteBuffer aquireBuf(ObjectPool objectpool)
    {
        try
        {
            return (CacheByteBuffer)CacheUtils.aquire(objectpool);
        }
        catch(CacheException cacheexception)
        {
            if(!$assertionsDisabled)
            {
                throw new AssertionError();
            } else
            {
                registry.getLogger().error("SRVC.CACHE.ERR43", cacheexception);
                return null;
            }
        }
    }

    protected void release(PoolObjectAbstractAdapter poolobjectabstractadapter)
    {
        try
        {
            CacheUtils.release(poolobjectabstractadapter);
        }
        catch(CacheException cacheexception)
        {
            if(!$assertionsDisabled)
                throw new AssertionError();
            registry.getLogger().error(L10n.format("SRVC.CACHE.ERR42"), cacheexception);
        }
    }

    protected Set findNodes(CacheTxImpl cachetximpl)
        throws CacheException
    {
        Object obj = new HashSet(cluster.getAllNodes().size() - 1);
        obj = findNodes(((Set) (obj)), cachetximpl.getReadSet());
        obj = findNodes(((Set) (obj)), cachetximpl.getWriteSet());
        obj = findNodes(((Set) (obj)), cachetximpl.getTxGrpSet());
        return ((Set) (obj));
    }

    protected void send(ByteBuffer bytebuffer, ClusterNode aclusternode[], int i)
        throws CacheException
    {
        CacheDgramChannel cachedgramchannel = aquireCh(dgramPool);
        for(int j = 0; j < aclusternode.length; j++)
            if(aclusternode[j] != null && aclusternode[j].isActive())
                send(cachedgramchannel, bytebuffer, aclusternode[j].getAddress(), i);

        release(cachedgramchannel);
    }

    protected void send(ByteBuffer bytebuffer, Set set, int i)
        throws CacheException
    {
        CacheDgramChannel cachedgramchannel = aquireCh(dgramPool);
        for(Iterator iterator = set.iterator(); iterator.hasNext(); send(cachedgramchannel, bytebuffer, ((ClusterNode)iterator.next()).getAddress(), i));
        release(cachedgramchannel);
    }

    protected void send(ByteBuffer bytebuffer, ClusterNode clusternode, int i)
        throws CacheException
    {
        CacheDgramChannel cachedgramchannel = aquireCh(dgramPool);
        send(cachedgramchannel, bytebuffer, clusternode.getAddress(), i);
        release(cachedgramchannel);
    }

    private void send(CacheDgramChannel cachedgramchannel, ByteBuffer bytebuffer, InetAddress inetaddress, int i)
        throws CacheException
    {
        try
        {
            bytebuffer.flip();
            cachedgramchannel.getChannel().send(bytebuffer, new InetSocketAddress(inetaddress, i));
        }
        catch(IOException ioexception)
        {
            CacheUtils.invalidate(cachedgramchannel);
            throw new CacheException(L10n.format("SRVC.CACHE.ERR14"), ioexception);
        }
    }

    protected CacheChannel findChannel(List list, int i)
        throws CacheException
    {
        int j = 0;
        for(int k = list.size(); j < k; j++)
        {
            CacheChannel cachechannel = (CacheChannel)list.get(j);
            if(i >= cachechannel.getMin() && i <= cachechannel.getMax())
                return cachechannel;
        }

        throw new CacheException(L10n.format("SRVC.CACHE.ERR15", new Integer(i)));
    }

    private Set findNodes(Set set, CacheTxSet cachetxset)
        throws CacheException
    {
        if(cachetxset != null)
        {
            CacheTopology cachetopology = registry.getConfig().getTopology();
            CacheTxEntry cachetxentry;
            for(Iterator iterator = cachetxset.iterator(); iterator.hasNext(); set.addAll(cachetopology.getNodes(cachetxentry.getEntry(), cachetxentry.getUserArgs())))
                cachetxentry = (CacheTxEntry)iterator.next();

        }
        return set;
    }

    private Set findNodes(Set set, Set set1)
        throws CacheException
    {
        if(set1 != null && !set1.isEmpty())
        {
            CacheTopology cachetopology = registry.getConfig().getTopology();
            CacheTxGroup cachetxgroup;
            for(Iterator iterator = set1.iterator(); iterator.hasNext(); set.addAll(cachetopology.getNodes(cachetxgroup.getGroup().getGroupId().longValue(), cachetxgroup.getUserArgs())))
                cachetxgroup = (CacheTxGroup)iterator.next();

        }
        return set;
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

    protected final CacheRegistry registry;
    protected final ObjectPoolService objpool;
    protected final ClusterService cluster;
    protected final NioMarshaller encoder;
    protected final ClusterNode localNode;
    protected CacheNio nio;
    private ObjectPool dgramPool;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(CacheCommManager.class).desiredAssertionStatus();
    }
}
