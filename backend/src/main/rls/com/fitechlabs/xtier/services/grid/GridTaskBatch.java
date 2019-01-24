// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.grid;

import com.fitechlabs.xtier.services.marshal.Marshallable;
import java.util.List;

// Referenced classes of package com.fitechlabs.xtier.services.grid:
//            GridTask

public interface GridTaskBatch
{

    public abstract List getTasks();

    public abstract List getArgs();

    public abstract boolean isAllOrNone();

    public abstract int getSize();

    public abstract void addTask(GridTask gridtask, Marshallable marshallable);

    public abstract boolean removeTask(int i);

    public abstract void removeAll();
}
