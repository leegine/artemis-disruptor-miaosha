// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.log;


// Referenced classes of package com.fitechlabs.xtier.services.log:
//            LogRecord, LogErrorHandler, LogFormatter, LogFilter

public interface LogSink
{

    public abstract String getName();

    public abstract void add(LogRecord logrecord);

    public abstract LogErrorHandler getErrorHandler();

    public abstract LogFormatter getFormatter();

    public abstract LogFilter getFilter();

    public abstract int getLevelMask();

    public abstract void setLevelMask(int i);

    public abstract void setLevelFrom(int i);

    public abstract void close();
}
