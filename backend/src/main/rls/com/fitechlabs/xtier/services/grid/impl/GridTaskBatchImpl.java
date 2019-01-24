// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.grid.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.grid.GridTask;
import com.fitechlabs.xtier.services.grid.GridTaskBatch;
import com.fitechlabs.xtier.services.marshal.Marshallable;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.Utils;
import java.util.*;

class GridTaskBatchImpl
    implements GridTaskBatch
{

    GridTaskBatchImpl(boolean flag)
    {
        tasks = new ArrayList();
        args = new ArrayList();
        isAllOrNone = flag;
    }

    public List getTasks()
    {
        return Collections.unmodifiableList(new ArrayList(tasks));
    }

    public int getSize()
    {
        return tasks.size();
    }

    public List getArgs()
    {
        return Collections.unmodifiableList(new ArrayList(args));
    }

    public boolean isAllOrNone()
    {
        return isAllOrNone;
    }

    public void addTask(GridTask gridtask, Marshallable marshallable)
    {
        ArgAssert.nullArg(gridtask, "task");
        tasks.add(gridtask);
        args.add(marshallable);
    }

    public boolean removeTask(int i)
    {
        int j = tasks.size();
        for(int k = 0; k < j; k++)
        {
            GridTask gridtask = (GridTask)tasks.get(k);
            if(gridtask.getId() == i)
            {
                tasks.remove(k);
                args.remove(k);
                return true;
            }
        }

        return false;
    }

    public void removeAll()
    {
        tasks.clear();
        args.clear();
    }

    public String toString()
    {
        return L10n.format("SRVC.GRID.TXT3", new Boolean(isAllOrNone), Utils.coll2Str(tasks));
    }

    private boolean isAllOrNone;
    private List tasks;
    private List args;
}
