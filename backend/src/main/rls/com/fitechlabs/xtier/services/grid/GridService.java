// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.grid;

import com.fitechlabs.xtier.kernel.KernelService;
import com.fitechlabs.xtier.services.marshal.Marshallable;
import com.fitechlabs.xtier.services.objpool.threads.ThreadPool;
import java.util.List;

// Referenced classes of package com.fitechlabs.xtier.services.grid:
//            GridException, GridTaxonomy, GridTask, GridTaskResult, 
//            GridTaskResultListener, GridTaskBatch

public interface GridService
    extends KernelService
{

    public abstract GridTaxonomy getGridTaxonomy();

    public abstract GridTaskResult exec(GridTask gridtask, Marshallable marshallable);

    public abstract GridTaskResult exec(int i, Marshallable marshallable);

    public abstract GridTaskResult exec(GridTask gridtask, long l, Marshallable marshallable);

    public abstract GridTaskResult exec(int i, long l, Marshallable marshallable);

    public abstract void exec(GridTask gridtask, Marshallable marshallable, GridTaskResultListener gridtaskresultlistener);

    public abstract void exec(int i, Marshallable marshallable, GridTaskResultListener gridtaskresultlistener);

    public abstract void exec(GridTask gridtask, long l, Marshallable marshallable, GridTaskResultListener gridtaskresultlistener);

    public abstract void exec(int i, long l, Marshallable marshallable, GridTaskResultListener gridtaskresultlistener);

    public abstract void exec(GridTask gridtask, Marshallable marshallable, GridTaskResultListener gridtaskresultlistener, ThreadPool threadpool);

    public abstract void exec(int i, Marshallable marshallable, GridTaskResultListener gridtaskresultlistener, ThreadPool threadpool);

    public abstract void exec(GridTask gridtask, long l, Marshallable marshallable, GridTaskResultListener gridtaskresultlistener, ThreadPool threadpool);

    public abstract void exec(int i, long l, Marshallable marshallable, GridTaskResultListener gridtaskresultlistener, ThreadPool threadpool);

    public abstract List exec(GridTaskBatch gridtaskbatch);

    public abstract List exec(GridTaskBatch gridtaskbatch, long l);

    public abstract GridTaskBatch createBatch(boolean flag);

    public abstract void regsiterTask(GridTask gridtask)
        throws GridException;

    public abstract boolean unregisterTask(int i);

    public abstract void unregisterAll();

    public abstract GridTask getTask(int i);

    public abstract List getExecTrace(long l, long l1);

    public abstract List getAllTasks();

    public static final byte GRID_TRACE_TOPOLOGY = 1;
    public static final byte GRID_TRACE_REF_ROUTED = 2;
    public static final byte GRID_TRACE_EXEC_LOCALLY = 3;
    public static final byte GRID_TRACE_REQ_SENT = 4;
    public static final byte GRID_TRACE_REQ_RCVD = 5;
    public static final byte GRID_TRACE_RES_RCVD = 6;
    public static final byte GRID_TRACE_FAILOVER = 7;
}
