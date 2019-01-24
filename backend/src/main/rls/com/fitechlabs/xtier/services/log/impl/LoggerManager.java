// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.log.impl;

import com.fitechlabs.xtier.services.log.Logger;

// Referenced classes of package com.fitechlabs.xtier.services.log.impl:
//            LoggerManagerMBean

public class LoggerManager
    implements LoggerManagerMBean
{

    public LoggerManager(Logger logger1)
    {
        mask = 0;
        cnt = 0;
        if(!$assertionsDisabled && logger1 == null)
        {
            throw new AssertionError();
        } else
        {
            logger = logger1;
            return;
        }
    }

    public void makeDebugLevelFrom()
    {
        logger.setLevelFrom(2);
    }

    public void makeTraceLevelFrom()
    {
        logger.setLevelFrom(4);
    }

    public void makeInfoLevelFrom()
    {
        logger.setLevelFrom(8);
    }

    public void makeLogLevelFrom()
    {
        logger.setLevelFrom(16);
    }

    public void makeWarningLevelFrom()
    {
        logger.setLevelFrom(32);
    }

    public void makeErrorLevelFrom()
    {
        logger.setLevelFrom(64);
    }

    public void makeNoneLevelFrom()
    {
        logger.setLevelMask(0);
    }

    public boolean getDebugLevel()
    {
        return (logger.getLevelMask() & 2) != 0;
    }

    public boolean getTraceLevel()
    {
        return (logger.getLevelMask() & 4) != 0;
    }

    public boolean getInfoLevel()
    {
        return (logger.getLevelMask() & 8) != 0;
    }

    public boolean getLogLevel()
    {
        return (logger.getLevelMask() & 0x10) != 0;
    }

    public boolean getWarningLevel()
    {
        return (logger.getLevelMask() & 0x20) != 0;
    }

    public boolean getErrorLevel()
    {
        return (logger.getLevelMask() & 0x40) != 0;
    }

    private synchronized void setMask(int i)
    {
        mask |= i;
        if(++cnt == 6)
        {
            logger.setLevelMask(mask);
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
    private int mask;
    private int cnt;
    private Logger logger;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(LoggerManager.class).desiredAssertionStatus();
    }
}
