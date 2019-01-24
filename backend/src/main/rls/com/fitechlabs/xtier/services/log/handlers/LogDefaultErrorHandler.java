// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.log.handlers;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.log.*;
import java.io.PrintStream;

public class LogDefaultErrorHandler
    implements LogErrorHandler
{

    public LogDefaultErrorHandler()
    {
    }

    public void onLogError(LogSink logsink, LogRecord logrecord, Exception exception)
    {
        if(!$assertionsDisabled && logrecord == null)
        {
            throw new AssertionError();
        } else
        {
            System.err.println(L10n.format("SRVC.LOG.DFL.HND", new Object[] {
                logsink, logrecord, exception
            }));
            return;
        }
    }

    public String toString()
    {
        return L10n.format("SRVC.LOG.DFL.HND.TXT1");
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

    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(LogDefaultErrorHandler.class).desiredAssertionStatus();
    }
}
