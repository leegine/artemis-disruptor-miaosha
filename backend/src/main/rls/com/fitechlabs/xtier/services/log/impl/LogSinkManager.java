// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.log.impl;

import com.fitechlabs.xtier.services.log.LogSink;

// Referenced classes of package com.fitechlabs.xtier.services.log.impl:
//            LogSinkManagerMBean

public class LogSinkManager
    implements LogSinkManagerMBean
{

    public LogSinkManager(LogSink logsink)
    {
        mask = 0;
        cnt = 0;
        if(!$assertionsDisabled && logsink == null)
        {
            throw new AssertionError();
        } else
        {
            sink = logsink;
            return;
        }
    }

    public void makeDebugLevelFrom()
    {
        sink.setLevelFrom(2);
    }

    public void makeTraceLevelFrom()
    {
        sink.setLevelFrom(4);
    }

    public void makeInfoLevelFrom()
    {
        sink.setLevelFrom(8);
    }

    public void makeLogLevelFrom()
    {
        sink.setLevelFrom(16);
    }

    public void makeWarningLevelFrom()
    {
        sink.setLevelFrom(32);
    }

    public void makeErrorLevelFrom()
    {
        sink.setLevelFrom(64);
    }

    public void makeNoneLevelFrom()
    {
        sink.setLevelMask(0);
    }

    public boolean getDebugLevel()
    {
        return (sink.getLevelMask() & 2) != 0;
    }

    public boolean getTraceLevel()
    {
        return (sink.getLevelMask() & 4) != 0;
    }

    public boolean getInfoLevel()
    {
        return (sink.getLevelMask() & 8) != 0;
    }

    public boolean getLogLevel()
    {
        return (sink.getLevelMask() & 0x10) != 0;
    }

    public boolean getWarningLevel()
    {
        return (sink.getLevelMask() & 0x20) != 0;
    }

    public boolean getErrorLevel()
    {
        return (sink.getLevelMask() & 0x40) != 0;
    }

    private synchronized void setMask(int i)
    {
        mask |= i;
        if(++cnt == 6)
        {
            sink.setLevelMask(mask);
            mask = 0;
            cnt = 0;
        }
    }

    public void setDebugLevel(boolean flag)
    {
        setMask(!flag ? 0 : 2);
    }

    public void setTraceLevel(boolean flag)
    {
        setMask(!flag ? 0 : 4);
    }

    public void setInfoLevel(boolean flag)
    {
        setMask(!flag ? 0 : 8);
    }

    public void setLogLevel(boolean flag)
    {
        setMask(!flag ? 0 : 16);
    }

    public void setWarningLevel(boolean flag)
    {
        setMask(!flag ? 0 : 32);
    }

    public void setErrorLevel(boolean flag)
    {
        setMask(!flag ? 0 : 64);
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

    private static final int LOG_LEVELS_COUNT = 6;
    private LogSink sink;
    private int mask;
    private int cnt;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(LogSinkManager.class).desiredAssertionStatus();
    }
}
