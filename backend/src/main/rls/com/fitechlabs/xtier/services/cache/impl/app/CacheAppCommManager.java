// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.cache.impl.app;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.cache.*;
import com.fitechlabs.xtier.services.cache.impl.*;
import com.fitechlabs.xtier.services.cache.impl.nio.CacheNio;
import com.fitechlabs.xtier.services.cache.impl.nio.CacheNioListener;
import com.fitechlabs.xtier.services.cache.impl.pools.CacheByteBuffer;
import com.fitechlabs.xtier.services.cache.impl.pools.CacheByteBufferFactory;
import com.fitechlabs.xtier.services.cache.keyattrs.CacheKeyAttrsAdapter;
import com.fitechlabs.xtier.services.cluster.ClusterNode;
import com.fitechlabs.xtier.services.cluster.ClusterService;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.services.marshal.NioMarshaller;
import com.fitechlabs.xtier.services.objpool.*;
import com.fitechlabs.xtier.services.objpool.adapters.PoolObjectAbstractAdapter;
import com.fitechlabs.xtier.utils.Utils;
import java.nio.ByteBuffer;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.cache.impl.app:
//            CacheAppConfig, CacheAppRemoteTxException, CacheAppRegistry, CacheAppLocalTxImpl,
//            CacheAppRemoteTxImpl, CacheAppTxManager

public class CacheAppCommManager extends CacheCommManager
{
    private class DeadlockReplyListener
        implements CacheNioListener
    {

        public void onReceive(ByteBuffer bytebuffer)
        {
            int i = DeadlockReplyListener.decodeInt32(bytebuffer);
            long l = DeadlockReplyListener.decodeInt64(bytebuffer);
            boolean flag = DeadlockReplyListener.decodeBool(bytebuffer);
            com.fitechlabs.xtier.services.cache.impl.CacheCommManager.DeadlockReply deadlockreply = (com.fitechlabs.xtier.services.cache.impl.CacheCommManager.DeadlockReply)replyMap.get(new Long(l));
            if(deadlockreply != null)
                synchronized(deadlockreply)
                {
                    deadlockreply.onReply(i, flag);
                    deadlockreply.notifyAll();
                }
        }

        private DeadlockReplyListener()
        {
            super();
        }

    }

    private class ChannelListener
        implements CacheNioListener
    {

        public void onReceive(ByteBuffer bytebuffer)
        {
            byte byte0 = ChannelListener.decodeInt8(bytebuffer);
            if(byte0 == 1)
                onPhaseOne(bytebuffer);
            else
            if(byte0 == 2)
                onDeadlockDetect(bytebuffer);
        }

        private void onDeadlockDetect(ByteBuffer bytebuffer)
        {
            try
            {
                int i = ChannelListener.decodeInt32(bytebuffer);
                long l = ChannelListener.decodeInt64(bytebuffer);
                List list = demarshalDeadlock(bytebuffer);
                bytebuffer.clear();
                Set set = registry.getAppTxMgr().getLocalTxSnapshot();
                boolean flag = false;
                Object obj = set.iterator();
                do
                {
                    if(!((Iterator) (obj)).hasNext())
                        break;
                    CacheTxImpl cachetximpl = (CacheTxImpl)((Iterator) (obj)).next();
                    CacheTxSet cachetxset = cachetximpl.getWriteSet();
                    if(cachetxset == null || !cachetxset.hasReverseOrder(list))
                        continue;
                    flag = true;
                    break;
                } while(true);
                obj = aquireBuf(deadlockReplyPool).getBuf();
                ChannelListener.encodeInt32(access$2700.getLocalNode().getNodeId(), ((ByteBuffer) (obj)));
                ChannelListener.encodeInt64(l, ((ByteBuffer) (obj)));
                ChannelListener.encodeBool(flag, ((ByteBuffer) (obj)));
                ClusterNode clusternode = access$2700.getNode(i);
                if(clusternode != null)
                    send(((ByteBuffer) (obj)), clusternode, registry.getAppConfig().getDeadlockReplyPort());
            }
            catch(CacheException cacheexception)
            {
                registry.getLogger().error(L10n.format("SRVC.CACHE.ERR20"), cacheexception);
            }
        }

        private void onPhaseOne(ByteBuffer bytebuffer)
        {
            CacheAppRemoteTxImpl cacheappremotetximpl;
            boolean flag;
            ClusterNode clusternode;
            CacheByteBuffer cachebytebuffer;
            cacheappremotetximpl = null;
            flag = true;
            try
            {
                cacheappremotetximpl = demarshalPhaseOne(bytebuffer);
                cacheappremotetximpl.prepare();
            }
            catch(CacheAppRemoteTxException cacheappremotetxexception)
            {
                if(!$assertionsDisabled && cacheappremotetximpl != null)
                    throw new AssertionError();
                else
                    return;
            }
            catch(CacheTxRollbackException cachetxrollbackexception)
            {
                cacheappremotetximpl.rollback();
                return;
            }
            catch(CacheTxOptimisticLockException cachetxoptimisticlockexception)
            {
                cacheappremotetximpl.rollback();
                flag = false;
            }
            catch(CacheException cacheexception)
            {
                if(!$assertionsDisabled)
                    throw new AssertionError();
                registry.getLogger().error(L10n.format("SRVC.CACHE.ERR47", cacheappremotetximpl), cacheexception);
            }
            clusternode = access$2700.getNode(registry.getTxMgr().extractNodeId(cacheappremotetximpl.getXid()));
            if(clusternode == null)
                break MISSING_BLOCK_LABEL_253;
            cachebytebuffer = aquireBuf(phaseOneReplyPool);
            marshalPhaseOneReply(cacheappremotetximpl.getXid(), flag, cachebytebuffer.getBuf());
            send(cachebytebuffer.getBuf(), clusternode, config.getPhaseOneReplyPort());
            release(cachebytebuffer);
            break MISSING_BLOCK_LABEL_253;
            CacheException cacheexception1;
            cacheexception1;
            registry.getLogger().error(L10n.format("SRVC.CACHE.ERR19"), cacheexception1);
            release(cachebytebuffer);
            break MISSING_BLOCK_LABEL_253;
            Exception exception;
            exception;
            release(cachebytebuffer);
            throw exception;
        }

        static final boolean $assertionsDisabled; /* synthetic field */


        private ChannelListener()
        {
            super();
        }

    }

    private class DgcReplyListener
        implements CacheNioListener
    {

        public void onReceive(ByteBuffer bytebuffer)
        {
            int i = DgcReplyListener.decodeInt32(bytebuffer);
            long l = DgcReplyListener.decodeInt64(bytebuffer);
            boolean flag = DgcReplyListener.decodeBool(bytebuffer);
            com.fitechlabs.xtier.services.cache.impl.CacheCommManager.CacheReply cachereply = (com.fitechlabs.xtier.services.cache.impl.CacheCommManager.CacheReply)replyMap.get(new Long(l));
            if(cachereply != null)
                synchronized(cachereply)
                {
                    cachereply.onReply(i, flag);
                    if(!cachereply.isSuccess() || cachereply.isAllReplied())
                        cachereply.notifyAll();
                }
        }

        private DgcReplyListener()
        {
            super();
        }

    }

    private class DgcListener
        implements CacheNioListener
    {

        public void onReceive(ByteBuffer bytebuffer)
        {
            try
            {
                int i = DgcListener.decodeInt32(bytebuffer);
                long l = DgcListener.decodeInt64(bytebuffer);
                bytebuffer.clear();
                CacheTxImpl cachetximpl = registry.getTxMgr().getLocalTx(new Long(l));
                DgcListener.encodeInt32(clear.getLocalNode().getNodeId(), bytebuffer);
                DgcListener.encodeInt64(l, bytebuffer);
                DgcListener.encodeBool(cachetximpl == null, bytebuffer);
                ClusterNode clusternode = clear.getNode(i);
                if(clusternode != null)
                    send(bytebuffer, clusternode, registry.getConfig().getDgcReplyPort());
            }
            catch(CacheException cacheexception)
            {
                registry.getLogger().error(L10n.format("SRVC.CACHE.ERR20"), cacheexception);
            }
        }

        private DgcListener()
        {
            super();
        }

    }

    private class PhaseTwoListener
        implements CacheNioListener
    {

        public void onReceive(ByteBuffer bytebuffer)
        {
            long l = PhaseTwoListener.decodeInt64(bytebuffer);
            boolean flag = PhaseTwoListener.decodeBool(bytebuffer);
            long l1 = PhaseTwoListener.decodeInt64(bytebuffer);
            CacheAppRemoteTxImpl cacheappremotetximpl = registry.getAppTxMgr().getRemoteTx(new Long(l), l1);
            if(cacheappremotetximpl != null)
                try
                {
                    if(flag)
                        cacheappremotetximpl.commit();
                    else
                        cacheappremotetximpl.rollback();
                }
                catch(CacheException cacheexception)
                {
                    registry.getLogger().error(L10n.format("SRVC.CACHE.ERR57", cacheappremotetximpl), cacheexception);
                }
        }

        private PhaseTwoListener()
        {
            super();
        }

    }

    private class PhaseOneReplyListener
        implements CacheNioListener
    {

        public void onReceive(ByteBuffer bytebuffer)
        {
            int i = PhaseOneReplyListener.decodeInt32(bytebuffer);
            if(!$assertionsDisabled && i == decodeBool.getNodeId())
                throw new AssertionError();
            long l = PhaseOneReplyListener.decodeInt64(bytebuffer);
            boolean flag = PhaseOneReplyListener.decodeBool(bytebuffer);
            com.fitechlabs.xtier.services.cache.impl.CacheCommManager.CacheReply cachereply = (com.fitechlabs.xtier.services.cache.impl.CacheCommManager.CacheReply)replyMap.get(new Long(l));
            if(cachereply != null)
                synchronized(cachereply)
                {
                    cachereply.onReply(i, flag);
                    if(!cachereply.isSuccess() || cachereply.isAllReplied())
                        cachereply.notifyAll();
                }
        }

        static final boolean $assertionsDisabled; /* synthetic field */


        private PhaseOneReplyListener()
        {
            super();
        }

    }


    public CacheAppCommManager(CacheAppRegistry cacheappregistry)
        throws CacheException
    {
        super(cacheappregistry, 7);
        replyMap = Collections.synchronizedMap(new HashMap());
        registry = cacheappregistry;
        config = (CacheAppConfig)cacheappregistry.getConfig();
        String s = config.getCacheName();
        List list = config.getChannels();
        int i = 0;
        for(int j = list.size(); i < j; i++)
        {
            CacheChannel cachechannel = (CacheChannel)list.get(i);
            cachechannel.setPool(registerNio(new ChannelListener(), cachechannel.getPort(), s + "-channel-" + i, getMaxMsgSize(cachechannel)));
        }

        phaseOneReplyPool = registerNio(new PhaseOneReplyListener(), config.getPhaseOneReplyPort(), s + "-phase-one-reply", 13);
        deadlockReplyPool = registerNio(new DeadlockReplyListener(), config.getDeadlockReplyPort(), s + "-deadlock-reply", 13);
        phaseTwoPool = registerNio(new PhaseTwoListener(), config.getPhaseTwoPort(), s + "-phase-two", 17);
        registerDgc();
    }

    public void stop()
    {
        super.stop();
        delete(phaseOneReplyPool);
        delete(deadlockReplyPool);
        delete(phaseTwoPool);
        delete(dgcBufPool);
        List list = config.getChannels();
        int i = 0;
        for(int j = list.size(); i < j; i++)
            delete(((CacheChannel)list.get(i)).getPool());

    }

    private void registerDgc()
        throws CacheException
    {
        CacheConfig cacheconfig = registry.getConfig();
        String s = cacheconfig.getCacheName();
        try
        {
            dgcBufPool = objpool.createPool(s + "-dgc", new CacheByteBufferFactory(13));
        }
        catch(ObjectPoolException objectpoolexception)
        {
            throw new CacheException(objectpoolexception.getMessage(), objectpoolexception);
        }
        nio.register(new DgcListener(), cacheconfig.getDgcPort(), dgcBufPool);
        nio.register(new DgcReplyListener(), cacheconfig.getDgcReplyPort(), dgcBufPool);
    }

    void sendPhaseOne(CacheAppLocalTxImpl cacheapplocaltximpl)
        throws CacheException
    {
        CacheChannel cachechannel;
        com.fitechlabs.xtier.services.cache.impl.CacheCommManager.CacheReply cachereply;
        CacheByteBuffer cachebytebuffer;
        if(!$assertionsDisabled && cacheapplocaltximpl == null)
            throw new AssertionError();
        if(!$assertionsDisabled && cacheapplocaltximpl.isEmpty())
            throw new AssertionError();
        Set set = registry.getStartupMgr().getValidNodes(findNodes(cacheapplocaltximpl));
        cacheapplocaltximpl.setNodes(set);
        if(set.isEmpty())
            return;
        cachechannel = findChannel(config.getChannels(), cacheapplocaltximpl.readCount() + cacheapplocaltximpl.writeCount() + cacheapplocaltximpl.groupCount());
        cacheapplocaltximpl.setTimespan((long)cachechannel.getAttempts() * cachechannel.getTimeout());
        cachereply = new com.fitechlabs.xtier.services.cache.impl.CacheCommManager.CacheReply(this, set);
        replyMap.put(cacheapplocaltximpl.getBoxedXid(), cachereply);
        cachebytebuffer = aquireBuf(cachechannel.getPool());
        int i;
        marshalPhaseOne(cacheapplocaltximpl, cachebytebuffer.getBuf());
        i = cachechannel.getAttempts();
_L3:
        if(i-- <= 0)
            break MISSING_BLOCK_LABEL_285;
label0:
        {
            synchronized(cachereply)
            {
                if(!cachereply.isAllReplied() && cachereply.isSuccess())
                {
                    send(cachebytebuffer.getBuf(), cachereply.getNodesArr(), cachechannel.getPort());
                    Utils.waitOn(cachereply, cachechannel.getTimeout());
                }
                if(cachereply.isSuccess())
                    break label0;
            }
            break MISSING_BLOCK_LABEL_285;
        }
        if(!cachereply.isAllReplied()) goto _L2; else goto _L1
_L1:
        cachereply2;
        JVM INSTR monitorexit ;
        release(cachebytebuffer);
        replyMap.remove(cacheapplocaltximpl.getBoxedXid());
        return;
_L2:
        cachereply2;
        JVM INSTR monitorexit ;
          goto _L3
        synchronized(cachereply)
        {
            if(!cachereply.isSuccess())
                throw new CacheTxOptimisticLockException(L10n.format("SRVC.CACHE.ERR2", cacheapplocaltximpl.getBoxedXid()));
            if(!cachereply.isAllReplied())
            {
                List list = cachereply.getNodesList();
                throw new CacheTxReplyFailureException(L10n.format("SRVC.CACHE.ERR16", Utils.list2Str(list)), list);
            }
        }
        release(cachebytebuffer);
        replyMap.remove(cacheapplocaltximpl.getBoxedXid());
        break MISSING_BLOCK_LABEL_413;
        Exception exception2;
        exception2;
        release(cachebytebuffer);
        replyMap.remove(cacheapplocaltximpl.getBoxedXid());
        throw exception2;
    }

    void sendPhaseTwo(CacheAppLocalTxImpl cacheapplocaltximpl, boolean flag)
    {
        CacheByteBuffer cachebytebuffer;
        if(!$assertionsDisabled && cacheapplocaltximpl == null)
            throw new AssertionError();
        if(!$assertionsDisabled && cacheapplocaltximpl.isEmpty())
            throw new AssertionError();
        if(cacheapplocaltximpl.getNodes().isEmpty())
            break MISSING_BLOCK_LABEL_136;
        cachebytebuffer = aquireBuf(phaseTwoPool);
        marshalPhaseTwo(cacheapplocaltximpl, flag, cachebytebuffer.getBuf());
        send(cachebytebuffer.getBuf(), cacheapplocaltximpl.getNodes(), config.getPhaseTwoPort());
        release(cachebytebuffer);
        break MISSING_BLOCK_LABEL_136;
        CacheException cacheexception;
        cacheexception;
        registry.getLogger().error(L10n.format("SRVC.CACHE.ERR17"), cacheexception);
        release(cachebytebuffer);
        break MISSING_BLOCK_LABEL_136;
        Exception exception;
        exception;
        release(cachebytebuffer);
        throw exception;
    }

    public boolean checkDgc(CacheAppRemoteTxImpl cacheappremotetximpl)
    {
        CacheConfig cacheconfig;
        ClusterNode clusternode;
        CacheByteBuffer cachebytebuffer;
        cacheconfig = registry.getConfig();
        if(System.currentTimeMillis() - cacheappremotetximpl.getTimestamp() <= cacheconfig.getDgcTxAge() || cacheappremotetximpl.getState() != 16)
            break MISSING_BLOCK_LABEL_317;
        clusternode = cluster.getNode(registry.getTxMgr().extractNodeId(cacheappremotetximpl.getXid()));
        if(clusternode == null)
            return true;
        cachebytebuffer = aquireBuf(dgcBufPool);
        com.fitechlabs.xtier.services.cache.impl.CacheCommManager.CacheReply cachereply;
        encoder.encodeInt32(localNode.getNodeId(), cachebytebuffer.getBuf());
        encoder.encodeInt64(cacheappremotetximpl.getXid(), cachebytebuffer.getBuf());
        cachereply = new com.fitechlabs.xtier.services.cache.impl.CacheCommManager.CacheReply(this, clusternode);
        replyMap.put(cacheappremotetximpl.getBoxedXid(), cachereply);
        boolean flag;
        synchronized(cachereply)
        {
            send(cachebytebuffer.getBuf(), clusternode, cacheconfig.getDgcPort());
            Utils.waitOn(cachereply, cacheconfig.getDgcTimeout());
            if(!cachereply.isSuccess() || !cachereply.isAllReplied())
                break MISSING_BLOCK_LABEL_217;
            flag = true;
        }
        replyMap.remove(cacheappremotetximpl.getBoxedXid());
        release(cachebytebuffer);
        return flag;
        cachereply1;
        JVM INSTR monitorexit ;
        replyMap.remove(cacheappremotetximpl.getBoxedXid());
        break MISSING_BLOCK_LABEL_267;
        Exception exception1;
        exception1;
        replyMap.remove(cacheappremotetximpl.getBoxedXid());
        throw exception1;
        release(cachebytebuffer);
        break MISSING_BLOCK_LABEL_317;
        CacheException cacheexception;
        cacheexception;
        registry.getLogger().error(L10n.format("SRVC.CACHE.ERR18"), cacheexception);
        release(cachebytebuffer);
        break MISSING_BLOCK_LABEL_317;
        Exception exception2;
        exception2;
        release(cachebytebuffer);
        throw exception2;
        return false;
    }

    private int getMaxMsgSize(CacheChannel cachechannel)
    {
        return 30 + cachechannel.getMax() * 22;
    }

    private void marshalPhaseOne(CacheAppLocalTxImpl cacheapplocaltximpl, ByteBuffer bytebuffer)
    {
        if(!$assertionsDisabled && cacheapplocaltximpl == null)
            throw new AssertionError();
        encoder.encodeInt8((byte)1, bytebuffer);
        encoder.encodeInt64(cacheapplocaltximpl.getXid(), bytebuffer);
        encoder.encodeInt8((byte)cacheapplocaltximpl.getIsolationLevel(), bytebuffer);
        encoder.encodeInt64(cacheapplocaltximpl.getTimespan(), bytebuffer);
        encoder.encodeInt32(cacheapplocaltximpl.readCount(), bytebuffer);
        if(cacheapplocaltximpl.hasReads())
        {
            for(Iterator iterator = cacheapplocaltximpl.getReadSet().iterator(); iterator.hasNext(); marshalEntry((CacheTxEntry)iterator.next(), bytebuffer));
        }
        encoder.encodeInt32(cacheapplocaltximpl.writeCount(), bytebuffer);
        if(cacheapplocaltximpl.hasWrites())
        {
            for(Iterator iterator1 = cacheapplocaltximpl.getWriteSet().iterator(); iterator1.hasNext(); marshalEntry((CacheTxEntry)iterator1.next(), bytebuffer));
        }
        encoder.encodeInt32(cacheapplocaltximpl.groupCount(), bytebuffer);
        if(cacheapplocaltximpl.hasTxGrps())
        {
            for(Iterator iterator2 = cacheapplocaltximpl.getTxGrpSet().iterator(); iterator2.hasNext(); marshalGroup(((CacheTxGroup)iterator2.next()).getGroup(), bytebuffer));
        }
    }

    private void marshalPhaseTwo(CacheAppLocalTxImpl cacheapplocaltximpl, boolean flag, ByteBuffer bytebuffer)
    {
        encoder.encodeInt64(cacheapplocaltximpl.getXid(), bytebuffer);
        encoder.encodeBool(flag, bytebuffer);
        encoder.encodeInt64(cacheapplocaltximpl.getTimespan(), bytebuffer);
    }

    private void marshalPhaseOneReply(long l, boolean flag, ByteBuffer bytebuffer)
    {
        encoder.encodeInt32(localNode.getNodeId(), bytebuffer);
        encoder.encodeInt64(l, bytebuffer);
        encoder.encodeBool(flag, bytebuffer);
    }

    private void marshalEntry(CacheTxEntry cachetxentry, ByteBuffer bytebuffer)
    {
        encoder.encodeInt32(cachetxentry.getKey().hashCode(), bytebuffer);
        CacheKeyAttrs cachekeyattrs = cachetxentry.getKeyAttrs();
        encoder.encodeInt64(cachekeyattrs.getTypeId(), bytebuffer);
        encoder.encodeInt64(cachekeyattrs.getGroupId(), bytebuffer);
        encoder.encodeBool(cachekeyattrs.isDepended(), bytebuffer);
        encoder.encodeInt8(cachetxentry.getOp(), bytebuffer);
    }

    private void marshalGroup(CacheGroup cachegroup, ByteBuffer bytebuffer)
    {
        encoder.encodeInt64(cachegroup.getGroupId().longValue(), bytebuffer);
    }

    private void marshalDeadlock(CacheAppLocalTxImpl cacheapplocaltximpl, ByteBuffer bytebuffer)
    {
        if(!$assertionsDisabled && cacheapplocaltximpl == null)
            throw new AssertionError();
        encoder.encodeInt8((byte)2, bytebuffer);
        encoder.encodeInt32(cluster.getLocalNode().getNodeId(), bytebuffer);
        encoder.encodeInt64(cacheapplocaltximpl.getXid(), bytebuffer);
        encoder.encodeInt32(cacheapplocaltximpl.writeCount(), bytebuffer);
        if(cacheapplocaltximpl.hasWrites())
        {
            for(ListIterator listiterator = cacheapplocaltximpl.getWriteSet().listIterator(); listiterator.hasNext(); marshalEntry((CacheTxEntry)listiterator.next(), bytebuffer));
        }
    }

    private List demarshalDeadlock(ByteBuffer bytebuffer)
    {
        int i = encoder.decodeInt32(bytebuffer);
        ArrayList arraylist = new ArrayList(i);
        for(int j = 0; j < i; j++)
        {
            encoder.decodeInt32(bytebuffer);
            long l = encoder.decodeInt64(bytebuffer);
            long l1 = encoder.decodeInt64(bytebuffer);
            boolean flag = encoder.decodeBool(bytebuffer);
            encoder.decodeInt8(bytebuffer);
            CacheKeyAttrsAdapter cachekeyattrsadapter = new CacheKeyAttrsAdapter(l, l1, flag);
            arraylist.add(cachekeyattrsadapter);
        }

        return arraylist;
    }

    private CacheAppRemoteTxImpl demarshalPhaseOne(ByteBuffer bytebuffer)
        throws CacheAppRemoteTxException
    {
        long l = encoder.decodeInt64(bytebuffer);
        byte byte0 = encoder.decodeInt8(bytebuffer);
        long l1 = encoder.decodeInt64(bytebuffer);
        CacheAppRemoteTxImpl cacheappremotetximpl = registry.getAppTxMgr().startRemoteTx(l, byte0, l1);
        int i = encoder.decodeInt32(bytebuffer);
        for(int j = 0; j < i; j++)
            demarshalBucket(bytebuffer, cacheappremotetximpl);

        int k = encoder.decodeInt32(bytebuffer);
        for(int i1 = 0; i1 < k; i1++)
            demarshalBucket(bytebuffer, cacheappremotetximpl);

        int j1 = encoder.decodeInt32(bytebuffer);
        for(int k1 = 0; k1 < j1; k1++)
            demarshalGroup(bytebuffer, cacheappremotetximpl);

        return cacheappremotetximpl;
    }

    private void demarshalBucket(ByteBuffer bytebuffer, CacheAppRemoteTxImpl cacheappremotetximpl)
    {
        int i = encoder.decodeInt32(bytebuffer);
        final long typeId = encoder.decodeInt64(bytebuffer);
        final long groupId = encoder.decodeInt64(bytebuffer);
        final boolean isDepended = encoder.decodeBool(bytebuffer);
        byte byte0 = encoder.decodeInt8(bytebuffer);
        cacheappremotetximpl.add(i, new CacheKeyAttrs() {

            public long getTypeId()
            {
                return typeId;
            }

            public long getGroupId()
            {
                return groupId;
            }

            public boolean isDepended()
            {
                return isDepended;
            }


            {
                super();
            }
        }
, byte0);
    }

    private void demarshalGroup(ByteBuffer bytebuffer, CacheAppRemoteTxImpl cacheappremotetximpl)
    {
        long l = encoder.decodeInt64(bytebuffer);
        cacheappremotetximpl.addGroup(registry.getDataMgr().getOrCreateGroup(new Long(l)), null);
    }

    boolean sendDeadlockDetect(CacheAppLocalTxImpl cacheapplocaltximpl)
        throws CacheException
    {
        CacheChannel cachechannel;
        com.fitechlabs.xtier.services.cache.impl.CacheCommManager.DeadlockReply deadlockreply;
        CacheByteBuffer cachebytebuffer;
        if(!$assertionsDisabled && cacheapplocaltximpl == null)
            throw new AssertionError();
        Set set = registry.getStartupMgr().getValidNodes(new HashSet(cluster.getAllNodes()));
        if(set.isEmpty())
            return false;
        cachechannel = findChannel(config.getChannels(), cacheapplocaltximpl.writeCount());
        deadlockreply = new com.fitechlabs.xtier.services.cache.impl.CacheCommManager.DeadlockReply(this, set);
        replyMap.put(cacheapplocaltximpl.getBoxedXid(), deadlockreply);
        cachebytebuffer = aquireBuf(cachechannel.getPool());
        marshalDeadlock(cacheapplocaltximpl, cachebytebuffer.getBuf());
        send(cachebytebuffer.getBuf(), deadlockreply.getNodesArr(), cachechannel.getPort());
_L2:
label0:
        {
            synchronized(deadlockreply)
            {
                if(!deadlockreply.isAllReplied() && !deadlockreply.isDeadlocked())
                {
                    Utils.waitOn(deadlockreply, cachechannel.getTimeout());
                    break label0;
                }
            }
            break; /* Loop/switch isn't completed */
        }
        deadlockreply1;
        JVM INSTR monitorexit ;
        if(true) goto _L2; else goto _L1
_L1:
        boolean flag = deadlockreply.isDeadlocked();
        release(cachebytebuffer);
        replyMap.remove(cacheapplocaltximpl.getBoxedXid());
        return flag;
        Exception exception1;
        exception1;
        release(cachebytebuffer);
        replyMap.remove(cacheapplocaltximpl.getBoxedXid());
        throw exception1;
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

    private static final int DGC_MSG_SIZE = 13;
    private static final int PHASE_ONE_PREFIX_SIZE = 30;
    private static final int BUCKET_SIZE = 22;
    private static final int PHASE_TWO_SIZE = 17;
    private static final int PHASE_ONE_REPLY_SIZE = 13;
    private static final int DEADLOCK_REPLY_SIZE = 13;
    private static final byte PHASE_ONE_REQUEST = 1;
    private static final byte DEADLOCK_DETECT_REQUEST = 2;
    private final CacheAppRegistry registry;
    private final ObjectPool phaseOneReplyPool;
    private final ObjectPool deadlockReplyPool;
    private final ObjectPool phaseTwoPool;
    private CacheAppConfig config;
    private ObjectPool dgcBufPool;
    private Map replyMap;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(CacheAppCommManager.class).desiredAssertionStatus();
    }











































}
