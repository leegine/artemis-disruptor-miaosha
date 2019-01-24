// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.cluster.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.cluster.ClusterException;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.utils.Utils;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

// Referenced classes of package com.fitechlabs.xtier.services.cluster.impl:
//            ClusterUdpMsg, ClusterTcpMsg, ClusterConnectException, ClusterLockException,
//            ClusterNodeImpl, ClusterTopologyManager, ClusterConfig, ClusterCommManager,
//            ClusterLockManager, ClusterEventManager

class ClusterProtocolManager_old
{
    private class ReplyManager
    {

        synchronized void onSend(int i)
        {
            lastRes = null;
            msgId = i;
        }

        void onResponse(ClusterTcpMsg clustertcpmsg)
        {
label0:
            {
                synchronized(startMutex)
                {
                    synchronized(this)
                    {
                        if((long)clustertcpmsg.getMsgId() != msgId)
                            break MISSING_BLOCK_LABEL_132;
                        if(lastRes == null)
                            break MISSING_BLOCK_LABEL_108;
                        if(lastRes.getBool("concurrent"))
                            break label0;
                    }
                }
                return;
            }
            if(!clustertcpmsg.getBool("concurrent") || !isLower(clustertcpmsg.getAddr(), clustertcpmsg.getInt32("port"), lastRes.getAddr(), lastRes.getInt32("port")))
                break MISSING_BLOCK_LABEL_108;
            lastRes = clustertcpmsg;
            replymanager;
            JVM INSTR monitorexit ;
            obj;
            JVM INSTR monitorexit ;
            return;
            lastRes = clustertcpmsg;
            if(!clustertcpmsg.getBool("concurrent"))
                startMutex.notifyAll();
            replymanager;
            JVM INSTR monitorexit ;
            obj;
            JVM INSTR monitorexit ;
              goto _L1
            exception1;
            throw exception1;
_L1:
        }

        synchronized ClusterTcpMsg getLastReply()
        {
            return lastRes;
        }

        private ClusterTcpMsg lastRes;
        private long msgId;

        private ReplyManager()
        {
            super();
        }

    }


    ClusterProtocolManager_old(Logger logger, ClusterConfig clusterconfig)
    {
        isStarted = false;
        isRetryStart = false;
        log = logger;
        cfg = clusterconfig;
        replyMgr = new ReplyManager();
    }

    void start(ClusterLockManager clusterlockmanager, ClusterTopologyManager clustertopologymanager, ClusterCommManager clustercommmanager, ClusterEventManager clustereventmanager)
        throws ClusterException
    {
        lockMgr = clusterlockmanager;
        topMgr = clustertopologymanager;
        commMgr = clustercommmanager;
        evtMgr = clustereventmanager;
    }

    void stop()
    {
    }

    void joinCluster()
        throws ClusterException
    {
        ClusterNodeImpl clusternodeimpl;
        ClusterUdpMsg clusterudpmsg;
        clusternodeimpl = topMgr.getLocalNode();
        clusterudpmsg = new ClusterUdpMsg((byte)2);
        clusterudpmsg.setAddress(clusternodeimpl.getAddress());
        clusterudpmsg.setPort(clusternodeimpl.getPort());
_L2:
        boolean flag;
        ClusterTcpMsg clustertcpmsg;
        flag = false;
        InetSocketAddress inetsocketaddress = null;
        int i = 0;
        for(int j = cfg.getRetries(); i < j && !isRetryStart; i++)
        {
            int k = i + 1;
            clusterudpmsg.setMsgId(k);
            replyMgr.onSend(k);
            commMgr.send(clusterudpmsg);
            Utils.sleep(cfg.getTimeout());
            ClusterTcpMsg clustertcpmsg3 = replyMgr.getLastReply();
            if(clustertcpmsg3 == null)
                continue;
            if(clustertcpmsg3.getBool("concurrent"))
            {
                if(isLower(clusternodeimpl.getAddress(), clusternodeimpl.getPort(), clustertcpmsg3.getAddr(), clustertcpmsg3.getInt32("port")))
                    continue;
                flag = true;
            } else
            {
                inetsocketaddress = new InetSocketAddress(clustertcpmsg3.getAddr(), clustertcpmsg3.getInt32("port"));
            }
            break;
        }

        if(inetsocketaddress == null)
        {
            List list = cfg.getSeedNodes();
            ClusterTcpMsg clustertcpmsg1 = new ClusterTcpMsg((byte)6);
            clustertcpmsg1.putAddr(clusternodeimpl.getAddress());
            clustertcpmsg1.putInt32("port", clusternodeimpl.getPort());
            int l = 0;
            for(int i1 = list.size(); l < i1 && !flag; l++)
            {
                InetSocketAddress inetsocketaddress1 = (InetSocketAddress)list.get(l);
                InetAddress inetaddress = inetsocketaddress1.getAddress();
                int j1 = inetsocketaddress1.getPort();
                if(inetaddress.equals(clusternodeimpl.getAddress()) && j1 == clusternodeimpl.getPort())
                    continue;
                try
                {
                    ClusterTcpMsg clustertcpmsg4 = commMgr.sendWait(clustertcpmsg1, inetaddress, j1, false);
                    if(clustertcpmsg4.getBool("concurrent"))
                    {
                        if(!isLower(clusternodeimpl.getAddress(), clusternodeimpl.getPort(), inetaddress, j1))
                            flag = true;
                        continue;
                    }
                    inetsocketaddress = new InetSocketAddress(clustertcpmsg4.getAddr(), clustertcpmsg4.getInt32("port"));
                    break;
                }
                catch(ClusterConnectException clusterconnectexception)
                {
                    log.warning(L10n.format("SRVC.CLUSTER.WRN7", inetsocketaddress1.getAddress().getHostAddress(), new Integer(inetsocketaddress1.getPort())));
                    continue;
                }
                catch(ClusterException clusterexception)
                {
                    flag = true;
                }
                Utils.sleep(cfg.getTimeout());
            }

        }
        if(inetsocketaddress == null)
            break MISSING_BLOCK_LABEL_634;
        do
        {
            if(flag)
                break MISSING_BLOCK_LABEL_634;
            clustertcpmsg = null;
            try
            {
                clustertcpmsg = lockMgr.remoteLockForJoin(inetsocketaddress);
            }
            catch(ClusterLockException clusterlockexception)
            {
                flag = true;
                break MISSING_BLOCK_LABEL_634;
            }
            if(clustertcpmsg != null)
                break;
            Utils.sleep(cfg.getTimeout());
        } while(true);
        List list1 = clustertcpmsg.getList("nodes");
        synchronized(startMutex)
        {
            isStarted = true;
            topMgr.onJoinCluster(clustertcpmsg.getInt32("node-id"), list1);
        }
        ClusterTcpMsg clustertcpmsg2 = new ClusterTcpMsg((byte)3);
        clustertcpmsg2.putMarshalObj("node", topMgr.getLocalNode());
        commMgr.sendWait(clustertcpmsg2, list1);
        logTopologyChange(L10n.format("SRVC.CLUSTER.TXT9", new Integer(topMgr.getSize())));
        lockMgr.remoteUnlock();
        return;
        Exception exception1;
        exception1;
        lockMgr.remoteUnlock();
        throw exception1;
label0:
        {
            synchronized(startMutex)
            {
                if(isRetryStart || flag)
                    break label0;
                isStarted = true;
                topMgr.onJoinCluster();
                logTopologyChange(L10n.format("SRVC.CLUSTER.TXT9", new Integer(topMgr.getSize())));
            }
            return;
        }
        Utils.waitOn(startMutex, cfg.getTimeout() * cfg.getRetries());
        isRetryStart = false;
        obj;
        JVM INSTR monitorexit ;
        if(true) goto _L2; else goto _L1
_L1:
        exception2;
        throw exception2;
    }

    void leaveCluster()
        throws ClusterException
    {
        int i;
        int j;
        int k;
        i = topMgr.getLocalNode().getNodeId();
        j = topMgr.getSize() <= 0 ? 20 : topMgr.getSize() * 20;
        k = 0;
_L2:
        if(k >= j)
            break; /* Loop/switch isn't completed */
        if(!lockMgr.remoteLockForLeave())
            break MISSING_BLOCK_LABEL_168;
        ClusterTcpMsg clustertcpmsg = new ClusterTcpMsg((byte)4);
        clustertcpmsg.putInt32("node-id", i);
        commMgr.sendWait(clustertcpmsg, topMgr.getNonLocalNodes());
        synchronized(startMutex)
        {
            isStarted = false;
            topMgr.onRemoveNode(i);
        }
        logTopologyChange(L10n.format("SRVC.CLUSTER.TXT10", new Integer(topMgr.getSize())));
        lockMgr.remoteUnlock();
        return;
        Exception exception1;
        exception1;
        lockMgr.remoteUnlock();
        throw exception1;
        Utils.sleep(cfg.getTimeout());
        k++;
        if(true) goto _L2; else goto _L1
_L1:
        synchronized(startMutex)
        {
            isStarted = false;
            topMgr.onRemoveNode(i);
        }
        logTopologyChange(L10n.format("SRVC.CLUSTER.TXT10", new Integer(topMgr.getSize())));
        return;
    }

    void handleTcpMsg(ClusterTcpMsg clustertcpmsg, OutputStream outputstream)
    {
        clustertcpmsg.getMsgType();
        JVM INSTR tableswitch 3 6: default 596
    //                   3 36
    //                   4 240
    //                   5 585
    //                   6 423;
           goto _L1 _L2 _L3 _L4 _L5
_L2:
        lockMgr.localLock();
        ClusterNodeImpl clusternodeimpl = (ClusterNodeImpl)clustertcpmsg.getMarshalObjNotNull("node");
        if(topMgr.hasNode(clusternodeimpl.getAddress(), clusternodeimpl.getPort()))
        {
            log.warning(L10n.format("SRVC.CLUSTER.WRN8", clusternodeimpl));
        } else
        {
            topMgr.onAddNode(clusternodeimpl);
            evtMgr.addEvent(1, clusternodeimpl, topMgr.getVersion());
        }
        try
        {
            commMgr.send(new ClusterTcpMsg((byte)8), outputstream);
            logTopologyChange(L10n.format("SRVC.CLUSTER.TXT11", new Integer(topMgr.getSize()), nodeToStr(clusternodeimpl)));
        }
        catch(ClusterException clusterexception)
        {
            topMgr.onRemoveNode(clusternodeimpl.getNodeId());
            evtMgr.addEvent(3, clusternodeimpl, topMgr.getVersion());
            log.error(L10n.format("SRVC.CLUSTER.ERR31", clusternodeimpl), clusterexception);
        }
          goto _L6
        exception;
        lockMgr.localUnlock();
        throw exception;
_L6:
        Exception exception;
        lockMgr.localUnlock();
        evtMgr.dispatch();
        break; /* Loop/switch isn't completed */
_L3:
        lockMgr.localLock();
        int i = clustertcpmsg.getInt32("node-id");
        ClusterNodeImpl clusternodeimpl2 = topMgr.getNode(i);
        if(clusternodeimpl2 == null)
        {
            log.warning(L10n.format("SRVC.CLUSTER.WRN9", new Integer(i)));
        } else
        {
            topMgr.onRemoveNode(i);
            evtMgr.addEvent(2, clusternodeimpl2, topMgr.getVersion());
        }
        try
        {
            commMgr.send(new ClusterTcpMsg((byte)8), outputstream);
        }
        catch(ClusterException clusterexception1)
        {
            log.error(L10n.format("SRVC.CLUSTER.ERR33"), clusterexception1);
        }
        if(clusternodeimpl2 != null)
            logTopologyChange(L10n.format("SRVC.CLUSTER.TXT12", new Integer(topMgr.getSize()), nodeToStr(clusternodeimpl2)));
          goto _L7
        exception1;
        lockMgr.localUnlock();
        throw exception1;
_L7:
        Exception exception1;
        lockMgr.localUnlock();
        evtMgr.dispatch();
        break; /* Loop/switch isn't completed */
_L5:
        ClusterNodeImpl clusternodeimpl1 = topMgr.getLocalNode();
        ClusterTcpMsg clustertcpmsg1 = new ClusterTcpMsg((byte)6);
        synchronized(startMutex)
        {
            if(!isStarted)
            {
                clustertcpmsg1.putBool("concurrent", true);
                if(!isLower(clusternodeimpl1.getAddress(), clusternodeimpl1.getPort(), clustertcpmsg.getAddr(), clustertcpmsg.getInt32("port")))
                    isRetryStart = true;
            } else
            {
                clustertcpmsg1.putBool("concurrent", false);
                ClusterNodeImpl clusternodeimpl3 = topMgr.getMgr();
                clustertcpmsg1.putAddr(clusternodeimpl3.getAddress());
                clustertcpmsg1.putInt32("port", clusternodeimpl3.getPort());
            }
            try
            {
                commMgr.send(clustertcpmsg1, outputstream);
            }
            catch(ClusterException clusterexception2)
            {
                log.error(L10n.format("SRVC.CLUSTER.ERR20"), clusterexception2);
            }
        }
        break; /* Loop/switch isn't completed */
_L4:
        replyMgr.onResponse(clustertcpmsg);
        break; /* Loop/switch isn't completed */
_L1:
        if(!$assertionsDisabled)
            throw new AssertionError("Invalid message type: " + clustertcpmsg.getMsgType());
    }

    void handleUdpMsg(ClusterUdpMsg clusterudpmsg)
    {
        ClusterTcpMsg clustertcpmsg;
label0:
        {
            if(!$assertionsDisabled && clusterudpmsg.getMsgType() != 2)
                throw new AssertionError();
            clustertcpmsg = new ClusterTcpMsg((byte)5);
            clustertcpmsg.putInt32("msg-id", clusterudpmsg.getMsgId());
            synchronized(startMutex)
            {
                if(!isStarted)
                    break label0;
                if(topMgr.isMgr())
                {
                    ClusterNodeImpl clusternodeimpl = topMgr.getMgr();
                    if(!$assertionsDisabled && clusternodeimpl != topMgr.getLocalNode())
                        throw new AssertionError();
                    clustertcpmsg.putAddr(clusternodeimpl.getAddress());
                    clustertcpmsg.putInt32("port", clusternodeimpl.getPort());
                    clustertcpmsg.putBool("concurrent", false);
                    break MISSING_BLOCK_LABEL_205;
                }
            }
            return;
        }
        clustertcpmsg.putBool("concurrent", true);
        clustertcpmsg.putAddr(cfg.getLocalAddress());
        clustertcpmsg.putInt32("port", cfg.getLocalPort());
        if(!isLower(cfg.getLocalAddress(), cfg.getLocalPort(), clusterudpmsg.getAddress(), clusterudpmsg.getPort()))
            isRetryStart = true;
        try
        {
            commMgr.send(clustertcpmsg, clusterudpmsg.getAddress(), clusterudpmsg.getPort());
        }
        catch(ClusterConnectException clusterconnectexception) { }
        catch(ClusterException clusterexception)
        {
            log.error(L10n.format("SRVC.CLUSTER.ERR34", clusterudpmsg.getAddress().getHostAddress(), new Integer(clusterudpmsg.getPort())), clusterexception);
        }
        obj;
        JVM INSTR monitorexit ;
          goto _L1
        exception;
        throw exception;
_L1:
    }

    private boolean isLower(InetAddress inetaddress, int i, InetAddress inetaddress1, int j)
    {
        byte abyte0[] = inetaddress.getAddress();
        byte abyte1[] = inetaddress1.getAddress();
        for(int k = 0; k < abyte0.length; k++)
        {
            if(abyte0[k] < abyte1[k])
                return true;
            if(abyte0[k] > abyte1[k])
                return false;
        }

        return i < j;
    }

    private String nodeToStr(ClusterNodeImpl clusternodeimpl)
    {
        return L10n.format("SRVC.CLUSTER.TXT7", clusternodeimpl.getAddress().getHostAddress(), new Integer(clusternodeimpl.getPort()));
    }

    private void logTopologyChange(String s)
    {
        log.log(s);
        log.log(L10n.format("SRVC.CLUSTER.TXT8", Utils.coll2Str(topMgr.getAllNodes())));
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

    private Logger log;
    private ClusterLockManager lockMgr;
    private ClusterConfig cfg;
    private ClusterTopologyManager topMgr;
    private ReplyManager replyMgr;
    private ClusterCommManager commMgr;
    private ClusterEventManager evtMgr;
    private boolean isStarted;
    private boolean isRetryStart;
    private final Object startMutex = new Object();
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(ClusterProtocolManager.class).desiredAssertionStatus();
    }


}
