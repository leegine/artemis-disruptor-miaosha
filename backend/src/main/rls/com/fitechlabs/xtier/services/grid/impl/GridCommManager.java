// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.grid.impl;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.cluster.*;
import com.fitechlabs.xtier.services.cluster.filters.ClusterNodeTypeFilter;
import com.fitechlabs.xtier.services.cluster.filters.ClusterServiceFilter;
import com.fitechlabs.xtier.services.grid.*;
import com.fitechlabs.xtier.services.log.LogService;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.services.marshal.MarshalException;
import com.fitechlabs.xtier.services.marshal.MarshalObject;
import com.fitechlabs.xtier.services.objpool.ObjectPoolService;
import com.fitechlabs.xtier.services.objpool.threads.ThreadPool;
import com.fitechlabs.xtier.threads.SysThread;
import com.fitechlabs.xtier.utils.Utils;
import com.fitechlabs.xtier.utils.boxed.BoxedBoolean;
import com.fitechlabs.xtier.utils.boxed.BoxedInt32;
import com.fitechlabs.xtier.utils.boxed.sync.BoxedInt64Sync;
import java.io.IOException;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.grid.impl:
//            GridIoManager, GridConfig, GridTraceManager, GridManager,
//            GridTaskUnitResultHolder, GridTaskUnitSplitResultImpl, GridTaskSplitRefHolder

class GridCommManager
{
    class SplitSession
    {

        private void result(int i, GridTaskSplitRefHolder gridtasksplitrefholder)
        {
            if(!$assertionsDisabled && !Thread.holdsLock(sesMutex))
                throw new AssertionError();
            GridTaskUnitResultHolder gridtaskunitresultholder = new GridTaskUnitResultHolder(new GridTaskUnitSplitResultImpl(i, null, gridtasksplitrefholder.getNode(), 0L, 0L, 0L, 0L, 0L, 0L), gridtasksplitrefholder);
            in.add(gridtaskunitresultholder);
            synchronized(mgrMutex)
            {
                mgrMutex.notifyAll();
            }
        }

        private long calcTimeout()
        {
            if(!$assertionsDisabled && !Thread.holdsLock(sesMutex))
                throw new AssertionError();
            long l = 0x7fffffffffffffffL;
            long l1 = System.currentTimeMillis();
            Iterator iterator = waiting.values().iterator();
            do
            {
                if(!iterator.hasNext())
                    break;
                GridTaskSplitRefHolder gridtasksplitrefholder = (GridTaskSplitRefHolder)iterator.next();
                long l2 = gridtasksplitrefholder.getExpireTime() - l1;
                if(l2 <= 0L)
                {
                    iterator.remove();
                    result(7, gridtasksplitrefholder);
                } else
                if(l2 < l)
                    l = l2;
            } while(true);
            return l;
        }

        void start(Object obj, Object obj1, Set set, Set set1)
        {
            if(!$assertionsDisabled && (obj == null || obj1 == null || set == null || set1 == null))
            {
                throw new AssertionError();
            } else
            {
                sesMutex = obj;
                mgrMutex = obj1;
                out = set;
                in = set1;
                waiting = new HashMap(Utils.getNonRehashCapacity(set.size()));
                sesWorker = new SysThread(obj1) {

                    protected void body()
                    {
                        Object obj2 = sesMutex;
                        JVM INSTR monitorenter ;
                        do
                        {
                            do
                            {
                                checkInterrupted();
                                if(out.isEmpty())
                                {
                                    long l = calcTimeout();
                                    Utils.waitOn(sesMutex, l);
                                    checkInterrupted();
                                }
                            } while(out.isEmpty());
                            Iterator iterator = out.iterator();
                            while(iterator.hasNext())
                            {
                                checkInterrupted();
                                final GridTaskSplitRefHolder holder = (GridTaskSplitRefHolder)iterator.next();
                                iterator.remove();
                                pool.addTask(new Runnable() {

                                    private void execLocally()
                                    {
                                        if(!$assertionsDisabled && !Thread.holdsLock(sesMutex))
                                            throw new AssertionError();
                                        GridTaskSplitRef gridtasksplitref = holder.getRef();
                                        long l1 = System.currentTimeMillis();
                                        GridTaskUnitResult gridtaskunitresult = gridMgr.execTaskUnit(gridtasksplitref.getTaskUnitContext(), gridtasksplitref.getArg());
                                        long l2 = System.currentTimeMillis();
                                        GridTaskUnitResultHolder gridtaskunitresultholder = new GridTaskUnitResultHolder(new GridTaskUnitSplitResultImpl(gridtaskunitresult.getReturnCode(), gridtaskunitresult.getReturnValue(), holder.getNode(), gridtaskunitresult.getUserErrorCode(), 0L, 0L, 0L, 0L, l1, l2), holder);
                                        in.add(gridtaskunitresultholder);
                                        synchronized(mgrMutex)
                                        {
                                            mgrMutex.notifyAll();
                                        }
                                    }

                                    private void execRemotelly()
                                    {
                                        GridTaskSplitRef gridtasksplitref;
                                        MarshalObject marshalobject;
                                        ClusterNode clusternode;
                                        if(!$assertionsDisabled && !Thread.holdsLock(sesMutex))
                                            throw new AssertionError();
                                        gridtasksplitref = holder.getRef();
                                        GridTaskUnitContext gridtaskunitcontext = gridtasksplitref.getTaskUnitContext();
                                        marshalobject = new MarshalObject(8);
                                        marshalobject.putInt8("ctrl", (byte)100);
                                        marshalobject.putInt64("sid", sid);
                                        marshalobject.putInt64("hid", hid);
                                        marshalobject.putInt32("tid", gridtaskunitcontext.getTaskId());
                                        marshalobject.putInt32("uid", gridtaskunitcontext.getUnitId());
                                        marshalobject.putInt64("eid", gridtaskunitcontext.getExecId());
                                        marshalobject.putInt32("nid", localNodeId);
                                        marshalobject.putMarshalObj("arg", gridtasksplitref.getArg());
                                        clusternode = holder.getNode();
                                        if(!clusternode.isActive())
                                        {
                                            result(2, holder);
                                            return;
                                        }
                                        try
                                        {
                                            holder.setSendReqTime(System.currentTimeMillis());
                                            ioMgr.sendPacket(marshalobject, clusternode);
                                            traceMgr.traceReqSent(marshalobject, clusternode);
                                            holder.setExpireTime(gridtasksplitref.getTimeout() != 0L ? System.currentTimeMillis() + gridtasksplitref.getTimeout() : 0x7fffffffffffffffL);
                                            waiting.put(new Long(hid), holder);
                                            hid++;
                                            sesMutex.notifyAll();
                                        }
                                        catch(IOException ioexception)
                                        {
                                            log.error(L10n.format("SRVC.GRID.ERR3", clusternode), ioexception);
                                            result(8, holder);
                                        }
                                        catch(MarshalException marshalexception)
                                        {
                                            log.error(L10n.format("SRVC.GRID.ERR3", clusternode), marshalexception);
                                            result(8, holder);
                                        }
                                        return;
                                    }

                                    public void run()
                                    {
                                        synchronized(sesMutex)
                                        {
                                            if(!discarded)
                                                if(holder.getNode().isLocalNode())
                                                    execLocally();
                                                else
                                                    execRemotelly();
                                        }
                                    }

                                    static final boolean $assertionsDisabled; /* synthetic field */



                        {
                            super();
                        }
                                }
);
                            }
                        } while(true);
                    }






                {
                    super(final_s);
                }
                }
;
                sesWorker.start();
                return;
            }
        }

        Long getSid()
        {
            if(!$assertionsDisabled && sid == null)
                throw new AssertionError();
            else
                return sid;
        }

        Object getSesMutex()
        {
            if(!$assertionsDisabled && sesMutex == null)
                throw new AssertionError();
            else
                return sesMutex;
        }

        Object getMgrMutex()
        {
            if(!$assertionsDisabled && mgrMutex == null)
                throw new AssertionError();
            else
                return mgrMutex;
        }

        Set getOutSet()
        {
            if(!$assertionsDisabled && !Thread.holdsLock(sesMutex))
                throw new AssertionError();
            if(!$assertionsDisabled && out == null)
                throw new AssertionError();
            else
                return out;
        }

        Set getInSet()
        {
            if(!$assertionsDisabled && !Thread.holdsLock(sesMutex))
                throw new AssertionError();
            if(!$assertionsDisabled && in == null)
                throw new AssertionError();
            else
                return in;
        }

        Map getWaitingMap()
        {
            if(!$assertionsDisabled && !Thread.holdsLock(sesMutex))
                throw new AssertionError();
            if(!$assertionsDisabled && waiting == null)
                throw new AssertionError();
            else
                return waiting;
        }

        void discard()
        {
            synchronized(sesMutex)
            {
                discarded = true;
            }
            Utils.stopThread(sesWorker);
            in = null;
            out = null;
            waiting = null;
        }

        public String toString()
        {
            if(!$assertionsDisabled)
                throw new AssertionError();
            else
                return "Grid task splt session [sid=" + sid + ", out=" + Utils.coll2Str(out) + ", in=" + Utils.coll2Str(in) + ']';
        }

        private Long sid;
        private long hid;
        private Object sesMutex;
        private Object mgrMutex;
        private Set out;
        private Set in;
        private Map waiting;
        private Thread sesWorker;
        private boolean discarded;
        static final boolean $assertionsDisabled; /* synthetic field */










        SplitSession()
        {
            super();
            sid = null;
            hid = 0x8000000000000000L;
            sesMutex = null;
            mgrMutex = null;
            out = null;
            in = null;
            waiting = null;
            sesWorker = null;
            discarded = false;
            sid = new Long(GridCommManager.sidGen.postIncr());
            if(sid.longValue() == 0x7fffffffffffffffL)
                throw new IllegalStateException(L10n.format("SRVC.GRID.ERR1"));
            else
                return;
        }
    }


    Long startSession(Object obj, Object obj1, Set set, Set set1)
    {
        SplitSession splitsession = new SplitSession();
        synchronized(sessions)
        {
            if(!$assertionsDisabled && sessions.containsKey(splitsession.getSid()))
                throw new AssertionError();
            sessions.put(splitsession.getSid(), splitsession);
        }
        splitsession.start(obj, obj1, set, set1);
        return splitsession.getSid();
    }

    void stopSession(Long long1)
    {
        synchronized(sessions)
        {
            ((SplitSession)sessions.remove(long1)).discard();
        }
    }

    List getTaskExecTrace(final long eid, long l)
    {
        Long long1;
        int i;
        long1 = new Long(eid);
        i = -1;
        Set set = cluster.getNodes(execTraceFilter);
        i = set.size();
        if(i == 0)
            return new ArrayList();
        synchronized(traces)
        {
            traces.put(long1, new ArrayList(i));
        }
        final BoxedInt32 sentNum = new BoxedInt32(0);
        final BoxedBoolean failed = new BoxedBoolean(false);
        final Object mux = new Object();
        final ClusterNode node;
        for(Iterator iterator = set.iterator(); iterator.hasNext(); pool.addTask(new Runnable() {

        public void run()
        {
            MarshalObject marshalobject = new MarshalObject(3);
            marshalobject.putInt8("ctrl", (byte)102);
            marshalobject.putInt64("eid", eid);
            marshalobject.putInt32("nid", localNodeId);
            try
            {
                ioMgr.sendPacket(marshalobject, node);
                synchronized(mux)
                {
                    sentNum.postIncr();
                    mux.notifyAll();
                }
            }
            catch(IOException ioexception)
            {
                log.error(L10n.format("SRVC.GRID.ERR6", node), ioexception);
                synchronized(mux)
                {
                    failed.set(true);
                    mux.notifyAll();
                }
            }
            catch(MarshalException marshalexception)
            {
                log.error(L10n.format("SRVC.GRID.ERR6", node), marshalexception);
                synchronized(mux)
                {
                    failed.set(true);
                    mux.notifyAll();
                }
            }
        }


            {
                super();
            }
    }
))
            node = (ClusterNode)iterator.next();

        synchronized(mux)
        {
            for(; sentNum.get() < i && !failed.get(); Utils.waitOn(mux));
        }
        if(failed.get())
            return null;
        long l1 = System.currentTimeMillis() + l;
        Map map1 = traces;
        JVM INSTR monitorenter ;
_L2:
        List list = (List)traces.get(long1);
        if(!$assertionsDisabled && list == null)
            throw new AssertionError();
        if(list.size() == i)
        {
            traces.remove(long1);
            ArrayList arraylist = new ArrayList();
            for(int j = 0; j < i; j++)
                arraylist.addAll((List)list.get(j));

            return arraylist;
        }
label0:
        {
            long l2 = l1 - System.currentTimeMillis();
            if(l2 <= 0L)
                break label0;
            Utils.waitOn(traces, l2);
        }
        if(true) goto _L2; else goto _L1
_L1:
        null;
        map1;
        JVM INSTR monitorexit ;
        return;
        Exception exception2;
        exception2;
        throw exception2;
    }

    GridCommManager(GridConfig gridconfig, GridManager gridmanager, GridTraceManager gridtracemanager)
        throws IOException
    {
        pool = null;
        log = null;
        traceMgr = null;
        gridMgr = null;
        cluster = null;
        objpool = null;
        ioMgr = null;
        sessions = new HashMap();
        traces = new HashMap();
        worker = null;
        execTraceFilter = new ClusterServiceFilter("grid", new ClusterNodeTypeFilter(false, true, true));
        gridMgr = gridmanager;
        traceMgr = gridtracemanager;
        XtierKernel xtierkernel = XtierKernel.getInstance();
        log = xtierkernel.log().getLogger("grid-comm");
        cluster = xtierkernel.cluster();
        objpool = xtierkernel.objpool();
        localNodeId = cluster.getLocalNode().getNodeId();
        pool = objpool.getThreadPool(gridconfig.getPoolName());
        if(pool == null)
        {
            throw new IOException(L10n.format("SRVC.GRID.ERR34", gridconfig.getPoolName()));
        } else
        {
            ioMgr = new GridIoManager(gridconfig, pool);
            worker = new SysThread(gridmanager) {

                protected void body()
                {
_L7:
                    MarshalObject marshalobject;
                    byte byte0;
                    checkInterrupted();
                    marshalobject = ioMgr.receivePacket();
                    byte0 = marshalobject.getInt8("ctrl");
                    byte0;
                    JVM INSTR tableswitch 100 103: default 966
                //                               100 317
                //                               101 688
                //                               102 52
                //                               103 226;
                       goto _L1 _L2 _L3 _L4 _L5
_L1:
                    continue; /* Loop/switch isn't completed */
_L4:
                    int i = marshalobject.getInt32("nid");
                    ClusterNode clusternode = cluster.getNode(i);
                    if(clusternode != null || !clusternode.isActive())
                    {
                        long l2 = marshalobject.getInt64("eid");
                        MarshalObject marshalobject1 = new MarshalObject(3);
                        marshalobject1.putInt8("ctrl", (byte)103);
                        marshalobject1.putInt64("eid", l2);
                        marshalobject1.putList("traces", traceMgr.getLocalExecTraces(l2));
                        if(clusternode.isActive())
                            try
                            {
                                ioMgr.sendPacket(marshalobject1, clusternode);
                            }
                            catch(IOException ioexception)
                            {
                                log.error(L10n.format("SRVC.GRID.ERR28", clusternode), ioexception);
                            }
                            catch(MarshalException marshalexception)
                            {
                                log.error(L10n.format("SRVC.GRID.ERR28", clusternode), marshalexception);
                            }
                    }
                    continue; /* Loop/switch isn't completed */
_L5:
                    Long long1 = (Long)marshalobject.getBoxed("eid");
                    synchronized(traces)
                    {
                        List list = (List)traces.get(long1);
                        if(list != null)
                        {
                            List list1 = marshalobject.getList("traces");
                            if(list1 != null)
                                list.add(list1);
                            traces.notifyAll();
                        }
                    }
                    continue; /* Loop/switch isn't completed */
_L2:
                    long l = System.currentTimeMillis();
                    int j = marshalobject.getInt32("nid");
                    ClusterNode clusternode1 = cluster.getNode(j);
                    if(clusternode1 != null && clusternode1.isActive())
                    {
                        traceMgr.traceReqRcvd(marshalobject, clusternode1);
                        GridTaskUnitContext gridtaskunitcontext = new GridTaskUnitContext(marshalobject.getInt32("tid"), marshalobject.getInt32("uid"), marshalobject.getInt64("eid"));
                        long l3 = marshalobject.getInt64("sid");
                        long l4 = marshalobject.getInt64("hid");
                        long l5 = System.currentTimeMillis();
                        GridTaskUnitResult gridtaskunitresult = gridMgr.execTaskUnit(gridtaskunitcontext, marshalobject.getMarshalObj("arg"));
                        long l6 = System.currentTimeMillis();
                        MarshalObject marshalobject2 = new MarshalObject(9);
                        marshalobject2.putInt8("ctrl", (byte)101);
                        marshalobject2.putInt32("tid", gridtaskunitcontext.getTaskId());
                        marshalobject2.putInt32("uid", gridtaskunitcontext.getUnitId());
                        marshalobject2.putInt64("eid", gridtaskunitcontext.getExecId());
                        marshalobject2.putInt64("sid", l3);
                        marshalobject2.putInt64("hid", l4);
                        marshalobject2.putInt32("ret.code", gridtaskunitresult.getReturnCode());
                        marshalobject2.putInt32("user.err.code", gridtaskunitresult.getUserErrorCode());
                        marshalobject2.putMarshalObj("ret.value", gridtaskunitresult.getReturnValue());
                        marshalobject2.putInt64("recv.req.time", l);
                        marshalobject2.putInt64("send.res.time", System.currentTimeMillis());
                        marshalobject2.putInt64("begin.exec.time", l5);
                        marshalobject2.putInt64("end.exec.time", l6);
                        if(clusternode1.isActive())
                            try
                            {
                                ioMgr.sendPacket(marshalobject2, clusternode1);
                            }
                            catch(IOException ioexception1)
                            {
                                log.error(L10n.format("SRVC.GRID.ERR29", clusternode1), ioexception1);
                            }
                            catch(MarshalException marshalexception1)
                            {
                                log.error(L10n.format("SRVC.GRID.ERR29", clusternode1), marshalexception1);
                            }
                    }
                    continue; /* Loop/switch isn't completed */
_L3:
                    long l1 = System.currentTimeMillis();
                    Long long2 = (Long)marshalobject.getBoxedNotNull("sid");
                    Long long3 = (Long)marshalobject.getBoxedNotNull("hid");
                    SplitSession splitsession = null;
                    synchronized(sessions)
                    {
                        splitsession = (SplitSession)sessions.get(long2);
                    }
                    if(splitsession != null)
                        synchronized(splitsession.getSesMutex())
                        {
                            GridTaskSplitRefHolder gridtasksplitrefholder = (GridTaskSplitRefHolder)splitsession.getWaitingMap().remove(long3);
                            if(gridtasksplitrefholder != null)
                            {
                                GridTaskUnitSplitResultImpl gridtaskunitsplitresultimpl = new GridTaskUnitSplitResultImpl(marshalobject.getInt32("ret.code"), marshalobject.getMarshalObj("ret.value"), gridtasksplitrefholder.getNode(), marshalobject.getInt32("user.err.code"), gridtasksplitrefholder.getSendReqTime(), marshalobject.getInt64("recv.req.time"), marshalobject.getInt64("send.res.time"), l1, marshalobject.getInt64("begin.exec.time"), marshalobject.getInt64("end.exec.time"));
                                traceMgr.traceResRcvd(gridtaskunitsplitresultimpl, gridtasksplitrefholder.getRef().getTaskUnitContext(), long2.longValue(), long3.longValue());
                                Set set = splitsession.getInSet();
                                set.add(new GridTaskUnitResultHolder(gridtaskunitsplitresultimpl, gridtasksplitrefholder));
                                Object obj1 = splitsession.getMgrMutex();
                                synchronized(obj1)
                                {
                                    obj1.notifyAll();
                                }
                            }
                        }
                    if(true) goto _L7; else goto _L6
_L6:
                    if($assertionsDisabled) goto _L7; else goto _L8
_L8:
                    throw new AssertionError("Invalid control byte: " + byte0);
                }

                static final boolean $assertionsDisabled; /* synthetic field */



            {
                super(final_s);
            }
            }
;
            worker.start();
            return;
        }
    }

    void stop()
    {
        synchronized(sessions)
        {
            for(Iterator iterator = sessions.values().iterator(); iterator.hasNext();)
            {
                SplitSession splitsession = (SplitSession)iterator.next();
                synchronized(splitsession.getSesMutex())
                {
                    splitsession.discard();
                }
            }

            sessions.clear();
        }
        ioMgr.stop();
        Utils.stopThread(worker);
        if(pool != null)
            objpool.deleteThreadPool(pool.getName());
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

    private static final byte CTRL_TASK_UNIT_REQ = 100;
    private static final byte CTRL_TASK_UNIT_RES = 101;
    private static final byte CTRL_TASK_EXEC_TRACE_REQ = 102;
    private static final byte CTRL_TASK_EXEC_TRACE_RES = 103;
    private ThreadPool pool;
    private Logger log;
    private int localNodeId;
    private GridTraceManager traceMgr;
    private GridManager gridMgr;
    private ClusterService cluster;
    private ObjectPoolService objpool;
    private GridIoManager ioMgr;
    private Map sessions;
    private Map traces;
    private Thread worker;
    private static BoxedInt64Sync sidGen = new BoxedInt64Sync(0x8000000000000000L);
    private ClusterFilter execTraceFilter;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(GridCommManager.class).desiredAssertionStatus();
    }










}
