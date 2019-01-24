// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.jobs;

import java.util.Map;

// Referenced classes of package com.fitechlabs.xtier.services.jobs:
//            Job

public interface JobContext
{

    public abstract Job getJob();

    public abstract Map getActiveJobs();
}
