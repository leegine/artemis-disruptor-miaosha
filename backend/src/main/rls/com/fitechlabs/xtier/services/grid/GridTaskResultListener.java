// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.grid;

import java.util.EventListener;

// Referenced classes of package com.fitechlabs.xtier.services.grid:
//            GridTaskResult

public interface GridTaskResultListener
    extends EventListener
{

    public abstract void onResult(GridTaskResult gridtaskresult);
}
