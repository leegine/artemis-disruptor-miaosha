// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.grid.impl;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.cluster.ClusterNode;
import com.fitechlabs.xtier.services.cluster.ClusterService;
import com.fitechlabs.xtier.services.grid.*;
import com.fitechlabs.xtier.services.grid.adapters.GridTaskUnitResultAdapter;
import com.fitechlabs.xtier.services.log.LogService;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.services.marshal.Marshallable;
import com.fitechlabs.xtier.services.objpool.threads.ThreadPool;
import com.fitechlabs.xtier.threads.SysThread;
import com.fitechlabs.xtier.threads.SysThreadGroup;
import com.fitechlabs.xtier.utils.Utils;
import java.io.IOException;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.grid.impl:
//            GridTraceManager, GridCommManager, GridTaskSplitRefHolder, GridTaskUnitResultHolder,
//            GridTaskResultImpl, GridConfig

class GridManager
{

    GridManager(GridConfig gridconfig)
        throws IOException
    {
        tax = null;
        regTasks = new ArrayList();
        commMgr = null;
        traceMgr = null;
        sysGrp = SysThreadGroup.getNewGroup("grid");
        eidCnt = 0x80000000;
        log = null;
        cfg = null;
        cfg = gridconfig;
        XtierKernel xtierkernel = XtierKernel.getInstance();
        log = xtierkernel.log().getLogger("grid-mgr");
        ClusterService clusterservice = xtierkernel.cluster();
        tax = gridconfig.getTaxonomy();
        eidBase = ((long)clusterservice.getLocalNode().getNodeId() & 65535L) << 32;
        traceMgr = new GridTraceManager(gridconfig);
        commMgr = new GridCommManager(gridconfig, this, traceMgr);
    }

    void stop()
    {
        sysGrp.stopAndDestroy();
        commMgr.stop();
        traceMgr.stop();
    }

    long getNextExecId()
    {
        Object obj = mux;
        JVM INSTR monitorenter ;
        if(eidCnt == 0x7fffffff)
            throw new IllegalStateException(L10n.format("SRVC.GRID.ERR15"));
        return eidBase + ((long)(eidCnt++) & 65535L);
        Exception exception;
        exception;
        throw exception;
    }

    GridTaskUnitResult execTaskUnit(GridTaskUnitContext gridtaskunitcontext, Marshallable marshallable)
    {
        long l = System.currentTimeMillis();
        GridTaskUnitResult gridtaskunitresult = execTaskUnit0(gridtaskunitcontext, marshallable);
        traceMgr.traceExecLocally(gridtaskunitresult, gridtaskunitcontext, l, System.currentTimeMillis());
        return gridtaskunitresult;
    }

    private GridTaskUnitResult execTaskUnit0(GridTaskUnitContext gridtaskunitcontext, Marshallable marshallable)
    {
        GridTaskUnit gridtaskunit;
        HashSet hashset;
        int i;
        Set set;
        int j;
        HashSet hashset1;
        HashSet hashset2;
        IdentityHashMap identityhashmap;
        HashSet hashset3;
        GridTaskRouter gridtaskrouter;
        GridTaskFailoverResolver gridtaskfailoverresolver;
        GridTaxonomy gridtaxonomy;
        Object obj1;
        Object obj2;
        Long long1;
        GridTask gridtask = null;
        synchronized(mux)
        {
            gridtask = lookupTask(gridtaskunitcontext.getTaskId());
        }
        if(gridtask == null)
            return new GridTaskUnitResultAdapter(4, null);
        gridtaskunit = gridtask.getUnitFactory().newTaskUnit(gridtaskunitcontext);
        if(gridtaskunit == null)
            return new GridTaskUnitResultAdapter(5, null);
        GridTaskTopology gridtasktopology = gridtask.getTopology();
        hashset = new HashSet(gridtasktopology.getNodes(gridtaskunitcontext));
        if(hashset == null || hashset.isEmpty())
            return new GridTaskUnitResultAdapter(12);
        traceMgr.traceTopology(gridtaskunitcontext, hashset);
        i = hashset.size();
        if(cfg.isExecLocalNoSplit() && i == 1 && ((ClusterNode)hashset.iterator().next()).isLocalNode())
            return gridtaskunit.exec(marshallable);
        set = gridtaskunit.split(Collections.unmodifiableSet(hashset), tax, marshallable);
        if(set == null)
            return gridtaskunit.exec(marshallable);
        j = set.size();
        if(j > i)
            log.warning(L10n.format("SRVC.GRID.WRN3", gridtaskunitcontext, new Integer(j), new Integer(i)));
        int k = Utils.getNonRehashCapacity(j);
        hashset1 = new HashSet(k);
        hashset2 = new HashSet(k);
        identityhashmap = new IdentityHashMap();
        hashset3 = new HashSet(k);
        gridtaskrouter = gridtask.getRouter();
        gridtaskfailoverresolver = gridtask.getFailoverResolver();
        gridtaxonomy = getTaxonomy();
        obj1 = new Object();
        obj2 = new Object();
        long1 = commMgr.startSession(obj1, obj2, hashset1, hashset2);
        HashSet hashset4 = new HashSet();
_L30:
        Object obj3 = obj1;
        JVM INSTR monitorenter ;
        boolean flag = false;
        if(set.isEmpty() || hashset.isEmpty()) goto _L2; else goto _L1
_L1:
        Iterator iterator = set.iterator();
_L10:
        if(!iterator.hasNext() || hashset.isEmpty()) goto _L2; else goto _L3
_L3:
        GridTaskSplitRef gridtasksplitref;
        Set set1;
        boolean flag1;
        gridtasksplitref = (GridTaskSplitRef)iterator.next();
        iterator.remove();
        if(gridtasksplitref.getTimeout() == 0L)
            log.warning(L10n.format("SRVC.GRID.WRN5", gridtaskunitcontext));
        if(gridtasksplitref.getTaskUnitContext().getUnitId() == gridtaskunitcontext.getUnitId())
            log.warning(L10n.format("SRVC.GRID.WRN4", gridtaskunitcontext));
        double d = gridtasksplitref.getCpuWeight();
        double d1 = gridtasksplitref.getMemoryWeight();
        double d2 = gridtasksplitref.getIoWeight();
        if(d != -1D && d <= 0.0D || d1 != -1D && d1 <= 0.0D || d2 != -1D && d2 <= 0.0D)
            throw new IllegalStateException(L10n.format("SRVC.GRID.ERR35", gridtasksplitref));
        set1 = (Set)identityhashmap.get(gridtasksplitref);
        flag1 = false;
        if(set1 == null) goto _L5; else goto _L4
_L4:
        hashset.removeAll(set1);
        if(!hashset.isEmpty()) goto _L5; else goto _L6
_L6:
        if(set1.size() != i) goto _L8; else goto _L7
_L7:
        Object obj6 = new GridTaskUnitResultAdapter(9);
        commMgr.stopSession(long1);
        return ((GridTaskUnitResult) (obj6));
_L8:
        flag1 = true;
        hashset4.add(gridtasksplitref);
_L5:
        if(!$assertionsDisabled && hashset.isEmpty())
            throw new AssertionError();
        if(flag1) goto _L10; else goto _L9
_L9:
        obj6 = gridtaskrouter.route(gridtasksplitref, set, hashset, gridtaxonomy);
        if(obj6 != null) goto _L12; else goto _L11
_L11:
        GridTaskUnitResultAdapter gridtaskunitresultadapter1 = new GridTaskUnitResultAdapter(10);
        obj3;
        JVM INSTR monitorexit ;
        commMgr.stopSession(long1);
        return gridtaskunitresultadapter1;
_L12:
        traceMgr.traceRefRouting(gridtasksplitref, ((ClusterNode) (obj6)));
        if(set1 != null)
            hashset.addAll(set1);
        hashset.remove(obj6);
        hashset1.add(new GridTaskSplitRefHolder(gridtasksplitref, ((ClusterNode) (obj6))));
        flag = true;
          goto _L10
_L2:
        if(hashset2.isEmpty()) goto _L14; else goto _L13
_L13:
        iterator = hashset2.iterator();
_L19:
        if(!iterator.hasNext()) goto _L16; else goto _L15
_L15:
        ClusterNode clusternode;
        GridTaskSplitRef gridtasksplitref1;
        GridTaskUnitSplitResult gridtaskunitsplitresult;
        GridTaskUnitResultHolder gridtaskunitresultholder = (GridTaskUnitResultHolder)iterator.next();
        iterator.remove();
        clusternode = gridtaskunitresultholder.getRefHolder().getNode();
        gridtasksplitref1 = gridtaskunitresultholder.getRefHolder().getRef();
        gridtaskunitsplitresult = gridtaskunitresultholder.getResult();
        if(gridtaskunitsplitresult.getReturnCode() != 1001) goto _L18; else goto _L17
_L17:
        hashset.add(clusternode);
        hashset3.add(gridtaskunitsplitresult);
          goto _L19
_L18:
        int l;
        l = gridtaskfailoverresolver.resolve(gridtasksplitref1, gridtaskunitsplitresult);
        traceMgr.traceFailover(gridtasksplitref1, gridtaskunitsplitresult, l);
        l;
        JVM INSTR tableswitch 1 3: default 1140
    //                   1 1012
    //                   2 1115
    //                   3 1043;
           goto _L20 _L21 _L22 _L23
_L20:
        continue; /* Loop/switch isn't completed */
_L21:
        GridTaskUnitResultAdapter gridtaskunitresultadapter = new GridTaskUnitResultAdapter(gridtaskunitsplitresult.getReturnCode());
        obj3;
        JVM INSTR monitorexit ;
        commMgr.stopSession(long1);
        return gridtaskunitresultadapter;
_L23:
        hashset.add(clusternode);
        Object obj5 = (Set)identityhashmap.get(gridtasksplitref1);
        if(obj5 == null)
            identityhashmap.put(gridtasksplitref1, obj5 = new HashSet());
        ((Set) (obj5)).add(clusternode);
        set.add(gridtasksplitref1);
          goto _L19
_L22:
        hashset1.add(new GridTaskSplitRefHolder(gridtasksplitref1, clusternode));
        flag = true;
          goto _L19
        if($assertionsDisabled) goto _L19; else goto _L24
_L24:
        throw new AssertionError("Unknown failover mode: " + l);
_L16:
        if(!hashset4.isEmpty())
        {
            set.addAll(hashset4);
            hashset4.clear();
        }
_L14:
        if(hashset3.size() != j) goto _L26; else goto _L25
_L25:
        obj3;
        JVM INSTR monitorexit ;
          goto _L27
_L26:
        if(set.isEmpty() || hashset.isEmpty()) goto _L29; else goto _L28
_L28:
        obj3;
        JVM INSTR monitorexit ;
          goto _L30
_L29:
        if(flag)
            obj1.notifyAll();
        obj3;
        JVM INSTR monitorexit ;
          goto _L31
        Exception exception1;
        exception1;
        obj3;
        JVM INSTR monitorexit ;
        throw exception1;
_L31:
        synchronized(obj2)
        {
            Utils.waitOn(obj2, 300L);
        }
          goto _L30
_L27:
        if(!$assertionsDisabled && hashset3.size() != j)
            throw new AssertionError();
        obj4 = gridtaskunit.aggregate(hashset3);
        commMgr.stopSession(long1);
        return ((GridTaskUnitResult) (obj4));
        Exception exception3;
        exception3;
        commMgr.stopSession(long1);
        throw exception3;
    }

    GridTaxonomy getTaxonomy()
    {
        return tax;
    }

    GridTaskResult exec(GridTask gridtask, Marshallable marshallable)
    {
        return exec(gridtask, getNextExecId(), marshallable);
    }

    GridTaskResult exec(int i, Marshallable marshallable)
    {
        return exec(i, getNextExecId(), marshallable);
    }

    GridTaskResult exec(GridTask gridtask, long l, Marshallable marshallable)
    {
        long l1 = System.currentTimeMillis();
        GridTaskUnitResult gridtaskunitresult = execTaskUnit(new GridTaskUnitContext(gridtask.getId(), 0x7fffffff, l), marshallable);
        return new GridTaskResultImpl(gridtask.getId(), l, gridtaskunitresult.getReturnValue(), gridtaskunitresult.getReturnCode() == 1001, l1, System.currentTimeMillis());
    }

    GridTaskResult exec(int i, long l, Marshallable marshallable)
    {
        long l1 = System.currentTimeMillis();
        GridTaskUnitResult gridtaskunitresult = execTaskUnit(new GridTaskUnitContext(i, 0x7fffffff, l), marshallable);
        return new GridTaskResultImpl(i, l, gridtaskunitresult.getReturnValue(), gridtaskunitresult.getReturnCode() == 1001, l1, System.currentTimeMillis());
    }

    void exec(GridTask gridtask, Marshallable marshallable, GridTaskResultListener gridtaskresultlistener)
    {
        exec(gridtask, getNextExecId(), marshallable, gridtaskresultlistener);
    }

    void exec(GridTask gridtask, Marshallable marshallable, GridTaskResultListener gridtaskresultlistener, ThreadPool threadpool)
    {
        exec(gridtask, getNextExecId(), marshallable, gridtaskresultlistener, threadpool);
    }

    void exec(int i, Marshallable marshallable, GridTaskResultListener gridtaskresultlistener)
    {
        exec(i, getNextExecId(), marshallable, gridtaskresultlistener);
    }

    void exec(int i, Marshallable marshallable, GridTaskResultListener gridtaskresultlistener, ThreadPool threadpool)
    {
        exec(i, getNextExecId(), marshallable, gridtaskresultlistener, threadpool);
    }

    void exec(final GridTask task, final long eid, final Marshallable arg, final GridTaskResultListener lstnr, ThreadPool threadpool)
    {
        threadpool.addTask(new Runnable() {

            public void run()
            {
                lstnr.onResult(exec(task, eid, arg));
            }


            {
                super();
            }
        }
);
    }

    void exec(final int tid, final long eid, final Marshallable arg, final GridTaskResultListener lstnr, ThreadPool threadpool)
    {
        threadpool.addTask(new Runnable() {

            public void run()
            {
                lstnr.onResult(exec(tid, eid, arg));
            }


            {
                super();
            }
        }
);
    }

    void exec(GridTask gridtask, long l, Marshallable marshallable, GridTaskResultListener gridtaskresultlistener)
    {
        (new SysThread(gridtask, l, marshallable) {

            protected void body()
            {
                lstnr.onResult(exec(task, eid, arg));
            }


            {
                super(final_s, final_i, final_threadgroup);
            }
        }
).start();
    }

    void exec(int i, long l, Marshallable marshallable, GridTaskResultListener gridtaskresultlistener)
    {
        (new SysThread(i, l, marshallable) {

            protected void body()
            {
                lstnr.onResult(exec(tid, eid, arg));
            }


            {
                super(final_s, final_i, final_threadgroup);
            }
        }
).start();
    }

    List exec(GridTaskBatch gridtaskbatch)
    {
        return exec(gridtaskbatch, getNextExecId());
    }

    List exec(GridTaskBatch gridtaskbatch, long l)
    {
        List list = gridtaskbatch.getTasks();
        List list1 = gridtaskbatch.getArgs();
        int i = list.size();
        ArrayList arraylist = new ArrayList(i);
        for(int j = 0; j < i; j++)
        {
            GridTaskResult gridtaskresult = exec((GridTask)list.get(j), l, (Marshallable)list1.get(j));
            if(!gridtaskresult.isSuccessful() && gridtaskbatch.isAllOrNone())
                return arraylist;
            arraylist.add(gridtaskresult);
        }

        return arraylist;
    }

    void regsiterTask(GridTask gridtask)
        throws GridException
    {
        synchronized(mux)
        {
            if(lookupTask(gridtask.getId()) != null)
                throw new GridException(L10n.format("SRVC.GRID.ERR16", new Integer(gridtask.getId())));
            regTasks.add(gridtask);
        }
    }

    boolean unregisterTask(int i)
    {
        Object obj = mux;
        JVM INSTR monitorenter ;
        int j;
        int k;
        j = regTasks.size();
        k = 0;
_L1:
        if(k >= j)
            break MISSING_BLOCK_LABEL_73;
        GridTask gridtask = (GridTask)regTasks.get(k);
        if(gridtask.getId() == i)
        {
            regTasks.remove(k);
            return true;
        }
        k++;
          goto _L1
        false;
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    void unregisterAll()
    {
        synchronized(mux)
        {
            regTasks.clear();
        }
    }

    GridTask getTask(int i)
    {
        Object obj = mux;
        JVM INSTR monitorenter ;
        return lookupTask(i);
        Exception exception;
        exception;
        throw exception;
    }

    List getExecTrace(long l, long l1)
    {
        List list = commMgr.getTaskExecTrace(l, l1);
        if(list == null)
            return null;
        List list1 = traceMgr.getLocalExecTraces(l);
        if(list1 != null)
            synchronized(list1)
            {
                list.addAll(list1);
            }
        return list;
    }

    public List getAllTasks()
    {
        Object obj = mux;
        JVM INSTR monitorenter ;
        return Collections.unmodifiableList(new ArrayList(regTasks));
        Exception exception;
        exception;
        throw exception;
    }

    private GridTask lookupTask(int i)
    {
        if(!$assertionsDisabled && !Thread.holdsLock(mux))
            throw new AssertionError();
        int j = regTasks.size();
        for(int k = 0; k < j; k++)
        {
            GridTask gridtask = (GridTask)regTasks.get(k);
            if(gridtask.getId() == i)
                return gridtask;
        }

        return null;
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

    private GridTaxonomy tax;
    private List regTasks;
    private final Object mux = new Object();
    private GridCommManager commMgr;
    private GridTraceManager traceMgr;
    private SysThreadGroup sysGrp;
    private long eidBase;
    private int eidCnt;
    private Logger log;
    private GridConfig cfg;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(GridManager.class).desiredAssertionStatus();
    }
}
