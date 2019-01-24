// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.grid.impl;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.services.cluster.ClusterNode;
import com.fitechlabs.xtier.services.cluster.ClusterService;
import com.fitechlabs.xtier.services.grid.*;
import com.fitechlabs.xtier.services.marshal.MarshalObject;
import com.fitechlabs.xtier.utils.FifoQueue;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.grid.impl:
//            GridConfig

class GridTraceManager
{

    GridTraceManager(GridConfig gridconfig)
    {
        traces = new HashMap();
        tracesLru = new FifoQueue();
        maxExecTraces = gridconfig.getMaxExecTraces();
        localNodeId = XtierKernel.getInstance().cluster().getLocalNode().getNodeId();
    }

    List getLocalExecTraces(long l)
    {
        Map map = traces;
        JVM INSTR monitorenter ;
        return (List)traces.get(new Long(l));
        Exception exception;
        exception;
        throw exception;
    }

    private void addTraceElement(long l, MarshalObject marshalobject)
    {
        Long long1 = new Long(l);
        synchronized(traces)
        {
            if(traces.size() == maxExecTraces)
            {
                if(!$assertionsDisabled && tracesLru.isEmpty())
                    throw new AssertionError();
                traces.remove(tracesLru.get());
            }
            Object obj = (List)traces.get(long1);
            if(obj == null)
            {
                traces.put(long1, obj = new ArrayList());
                tracesLru.add(long1);
            }
            synchronized(obj)
            {
                ((List) (obj)).add(marshalobject);
            }
        }
    }

    void traceTopology(GridTaskUnitContext gridtaskunitcontext, Set set)
    {
        MarshalObject marshalobject = new MarshalObject(7);
        marshalobject.putInt8("ctrl", (byte)1);
        marshalobject.putInt64("timestamp", System.currentTimeMillis());
        marshalobject.putInt32("local.nid", localNodeId);
        marshalobject.putInt32("tid", gridtaskunitcontext.getTaskId());
        marshalobject.putInt32("uid", gridtaskunitcontext.getUnitId());
        marshalobject.putInt64("eid", gridtaskunitcontext.getExecId());
        ArrayList arraylist = new ArrayList(set.size());
        for(Iterator iterator = set.iterator(); iterator.hasNext(); arraylist.add(new Integer(((ClusterNode)iterator.next()).getNodeId())));
        marshalobject.putList("node.ids", arraylist);
        addTraceElement(gridtaskunitcontext.getExecId(), marshalobject);
    }

    void traceRefRouting(GridTaskSplitRef gridtasksplitref, ClusterNode clusternode)
    {
        MarshalObject marshalobject = new MarshalObject(10);
        GridTaskUnitContext gridtaskunitcontext = gridtasksplitref.getTaskUnitContext();
        long l = gridtaskunitcontext.getExecId();
        marshalobject.putInt8("ctrl", (byte)2);
        marshalobject.putInt64("timestamp", System.currentTimeMillis());
        marshalobject.putInt32("local.nid", localNodeId);
        marshalobject.putInt32("ref.tid", gridtaskunitcontext.getTaskId());
        marshalobject.putInt32("ref.uid", gridtaskunitcontext.getUnitId());
        marshalobject.putInt64("ref.eid", l);
        marshalobject.putFloat64("ref.cpu.weight", gridtasksplitref.getCpuWeight());
        marshalobject.putFloat64("ref.mem.weight", gridtasksplitref.getMemoryWeight());
        marshalobject.putFloat64("ref.io.weight", gridtasksplitref.getIoWeight());
        marshalobject.putInt32("nid", clusternode.getNodeId());
        addTraceElement(l, marshalobject);
    }

    void traceExecLocally(GridTaskUnitResult gridtaskunitresult, GridTaskUnitContext gridtaskunitcontext, long l, long l1)
    {
        MarshalObject marshalobject = new MarshalObject(10);
        long l2 = gridtaskunitcontext.getExecId();
        marshalobject.putInt8("ctrl", (byte)3);
        marshalobject.putInt64("timestamp", System.currentTimeMillis());
        marshalobject.putInt32("local.nid", localNodeId);
        marshalobject.putInt64("start.time", l);
        marshalobject.putInt64("end.time", l1);
        marshalobject.putInt32("tid", gridtaskunitcontext.getTaskId());
        marshalobject.putInt32("uid", gridtaskunitcontext.getUnitId());
        marshalobject.putInt64("eid", l2);
        marshalobject.putInt32("ret.code", gridtaskunitresult.getReturnCode());
        marshalobject.putInt32("user.err.code", gridtaskunitresult.getUserErrorCode());
        addTraceElement(l2, marshalobject);
    }

    void traceReqSent(MarshalObject marshalobject, ClusterNode clusternode)
    {
        MarshalObject marshalobject1 = new MarshalObject(10);
        long l = marshalobject.getInt64("eid");
        marshalobject1.putInt8("ctrl", (byte)4);
        marshalobject1.putInt64("timestamp", System.currentTimeMillis());
        marshalobject1.putInt32("local.nid", localNodeId);
        marshalobject1.putInt64("sid", marshalobject.getInt64("sid"));
        marshalobject1.putInt64("hid", marshalobject.getInt64("hid"));
        marshalobject1.putInt32("tid", marshalobject.getInt32("tid"));
        marshalobject1.putInt32("uid", marshalobject.getInt32("uid"));
        marshalobject1.putInt64("eid", l);
        marshalobject1.putInt32("sender.nid", marshalobject.getInt32("nid"));
        marshalobject1.putInt32("dest.nid", clusternode.getNodeId());
        addTraceElement(l, marshalobject1);
    }

    void traceResRcvd(GridTaskUnitResult gridtaskunitresult, GridTaskUnitContext gridtaskunitcontext, long l, long l1)
    {
        MarshalObject marshalobject = new MarshalObject(9);
        long l2 = gridtaskunitcontext.getExecId();
        marshalobject.putInt8("ctrl", (byte)6);
        marshalobject.putInt64("timestamp", System.currentTimeMillis());
        marshalobject.putInt32("local.nid", localNodeId);
        marshalobject.putInt64("sid", l);
        marshalobject.putInt64("hid", l1);
        marshalobject.putInt32("tid", gridtaskunitcontext.getTaskId());
        marshalobject.putInt32("uid", gridtaskunitcontext.getUnitId());
        marshalobject.putInt64("eid", l2);
        marshalobject.putInt32("ret.code", gridtaskunitresult.getReturnCode());
        marshalobject.putInt32("user.err.code", gridtaskunitresult.getUserErrorCode());
        addTraceElement(l2, marshalobject);
    }

    void traceReqRcvd(MarshalObject marshalobject, ClusterNode clusternode)
    {
        MarshalObject marshalobject1 = new MarshalObject(9);
        long l = marshalobject.getInt64("eid");
        marshalobject1.putInt8("ctrl", (byte)5);
        marshalobject1.putInt64("timestamp", System.currentTimeMillis());
        marshalobject1.putInt32("local.nid", localNodeId);
        marshalobject1.putInt64("sid", marshalobject.getInt64("sid"));
        marshalobject1.putInt64("hid", marshalobject.getInt64("hid"));
        marshalobject1.putInt32("tid", marshalobject.getInt32("tid"));
        marshalobject1.putInt32("uid", marshalobject.getInt32("uid"));
        marshalobject1.putInt64("eid", l);
        marshalobject1.putInt32("sender.nid", clusternode.getNodeId());
        addTraceElement(l, marshalobject1);
    }

    void traceFailover(GridTaskSplitRef gridtasksplitref, GridTaskUnitResult gridtaskunitresult, int i)
    {
        MarshalObject marshalobject = new MarshalObject(12);
        GridTaskUnitContext gridtaskunitcontext = gridtasksplitref.getTaskUnitContext();
        long l = gridtaskunitcontext.getExecId();
        marshalobject.putInt8("ctrl", (byte)7);
        marshalobject.putInt64("timestamp", System.currentTimeMillis());
        marshalobject.putInt32("local.nid", localNodeId);
        marshalobject.putInt32("ref.tid", gridtaskunitcontext.getTaskId());
        marshalobject.putInt32("ref.uid", gridtaskunitcontext.getUnitId());
        marshalobject.putInt64("ref.eid", gridtaskunitcontext.getExecId());
        marshalobject.putFloat64("ref.cpu.weight", gridtasksplitref.getCpuWeight());
        marshalobject.putFloat64("ref.mem.weight", gridtasksplitref.getMemoryWeight());
        marshalobject.putFloat64("ref.io.weight", gridtasksplitref.getIoWeight());
        marshalobject.putInt32("ret.code", gridtaskunitresult.getReturnCode());
        marshalobject.putInt32("user.err.code", gridtaskunitresult.getUserErrorCode());
        marshalobject.putInt32("failover.mode", i);
        addTraceElement(l, marshalobject);
    }

    void stop()
    {
        traces.clear();
        tracesLru.clear();
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

    private Map traces;
    private FifoQueue tracesLru;
    private int maxExecTraces;
    private int localNodeId;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(GridTraceManager.class).desiredAssertionStatus();
    }
}
