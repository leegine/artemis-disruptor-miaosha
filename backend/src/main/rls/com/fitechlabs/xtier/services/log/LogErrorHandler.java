// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.log;


// Referenced classes of package com.fitechlabs.xtier.services.log:
//            LogSink, LogRecord

public interface LogErrorHandler
{

    public abstract void onLogError(LogSink logsink, LogRecord logrecord, Exception exception);
}
