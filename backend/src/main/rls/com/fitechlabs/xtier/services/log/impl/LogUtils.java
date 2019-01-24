// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.log.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.utils.ArgAssert;

public class LogUtils
{

    public LogUtils()
    {
    }

    public static int convertToMask(int i)
    {
        switch(i)
        {
        case 64: // '@'
            return 64;

        case 32: // ' '
            return 96;

        case 16: // '\020'
            return 112;

        case 8: // '\b'
            return 120;

        case 4: // '\004'
            return 124;

        case 2: // '\002'
            return 126;
        }
        throw new IllegalArgumentException(L10n.format("SRVC.LOG.LEVEL.ERR1", new Integer(i)));
    }

    public static void checkLevel(int i)
    {
        switch(i)
        {
        case 2: // '\002'
        case 4: // '\004'
        case 8: // '\b'
        case 16: // '\020'
        case 32: // ' '
        case 64: // '@'
            return;
        }
        throw new IllegalArgumentException(L10n.format("SRVC.LOG.LEVEL.ERR1", new Integer(i)));
    }

    public static String getLevelStr(int i)
    {
        switch(i)
        {
        case 2: // '\002'
            return "debug";

        case 64: // '@'
            return "error";

        case 8: // '\b'
            return "info";

        case 16: // '\020'
            return "log";

        case 4: // '\004'
            return "trace";

        case 32: // ' '
            return "warning";
        }
        throw new IllegalArgumentException(L10n.format("SRVC.LOG.LEVEL.ERR1", new Integer(i)));
    }

    public static void checkMask(int i)
    {
        ArgAssert.illegalArg((0xffffff81 & i) == 0, "mask");
    }
}
