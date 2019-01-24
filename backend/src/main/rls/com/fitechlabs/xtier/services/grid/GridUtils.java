// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.grid;


public class GridUtils
{

    public GridUtils()
    {
    }

    public static String getStrRetCode(int i)
    {
        switch(i)
        {
        case 6: // '\006'
            return "ERR_INVALID_ARGS";

        case 2: // '\002'
            return "ERR_RETRY_OTHER";

        case 12: // '\f'
            return "ERR_NO_TOPOLOGY";

        case 3: // '\003'
            return "ERR_RETRY_SAME";

        case 4: // '\004'
            return "ERR_TASK_NOT_FOUND";

        case 5: // '\005'
            return "ERR_TASK_UNIT_NOT_FOUND";

        case 7: // '\007'
            return "ERR_TASK_TIMEOUT";

        case 8: // '\b'
            return "ERR_SYSTEM_FAILURE";

        case 1: // '\001'
            return "ERR_USER_DEFINED";

        case 9: // '\t'
            return "ERR_NO_RETRY";

        case 11: // '\013'
            return "ERR_NOT_EXEC_TASK_UNIT";

        case 1001:
            return "TASK_UNIT_OK";
        }
        if(!$assertionsDisabled)
            throw new AssertionError("Unknown task unit return code: " + i);
        else
            return null;
    }

    public static String getStrTraceCode(byte byte0)
    {
        switch(byte0)
        {
        case 3: // '\003'
            return "GRID_TRACE_EXEC_LOCALLY";

        case 7: // '\007'
            return "GRID_TRACE_FAILOVER";

        case 2: // '\002'
            return "GRID_TRACE_REF_ROUTED";

        case 5: // '\005'
            return "GRID_TRACE_REQ_RCVD";

        case 4: // '\004'
            return "GRID_TRACE_REQ_SENT";

        case 6: // '\006'
            return "GRID_TRACE_RES_RCVD";

        case 1: // '\001'
            return "GRID_TRACE_TOPOLOGY";
        }
        if(!$assertionsDisabled)
            throw new AssertionError("Unknown task trace code: " + byte0);
        else
            return null;
    }

    public static String getStrFailoverMode(int i)
    {
        switch(i)
        {
        case 3: // '\003'
            return "TASK_UNIT_RETRY_OTHER";

        case 2: // '\002'
            return "TASK_UNIT_RETRY_SAME";

        case 1: // '\001'
            return "TASK_UNIT_TERMINATE";
        }
        if(!$assertionsDisabled)
            throw new AssertionError("Unknown task failover mode: " + i);
        else
            return null;
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
        $assertionsDisabled = !(GridUtils.class).desiredAssertionStatus();
    }
}
