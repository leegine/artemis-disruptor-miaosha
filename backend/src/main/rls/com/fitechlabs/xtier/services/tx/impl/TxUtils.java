// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.tx.impl;

import com.fitechlabs.xtier.l10n.L10n;

// Referenced classes of package com.fitechlabs.xtier.services.tx.impl:
//            TxImpl

final class TxUtils
    implements TxImpl.XarStates
{

    private TxUtils()
    {
    }

    static String getStrStatus(int i)
    {
        switch(i)
        {
        case 7: // '\007'
            return "STATUS_PREPARING";

        case 2: // '\002'
            return "STATUS_PREPARED";

        case 9: // '\t'
            return "STATUS_ROLLING_BACK";

        case 4: // '\004'
            return "STATUS_ROLLEDBACK";

        case 8: // '\b'
            return "STATUS_COMMITING";

        case 3: // '\003'
            return "STATUS_COMMITED";

        case 6: // '\006'
            return "STATUS_NO_TRANSACTION";

        case 5: // '\005'
            return "STATUS_UNKNOWN";

        case 1: // '\001'
            return "STATUS_MARKED_ROLLBACK";

        case 0: // '\0'
            return "STATUS_ACTIVE";
        }
        throw new IllegalArgumentException(L10n.format("ILLEGAL.ARG.ERR", "status"));
    }

    static String getStrXAErrCode(int i)
    {
        switch(i)
        {
        case 7: // '\007'
            return "XA_HEURCOM";

        case 8: // '\b'
            return "XA_HEURHAZ";

        case 5: // '\005'
            return "XA_HEURMIX";

        case 6: // '\006'
            return "XA_HEURRB";

        case 9: // '\t'
            return "XA_NOMIGRATE";

        case 101: // 'e'
            return "XA_RBCOMMFAIL";

        case 102: // 'f'
            return "XA_RBDEADLOCK";

        case 103: // 'g'
            return "XA_RBINTEGRITY";

        case 104: // 'h'
            return "XA_RBOTHER";

        case 105: // 'i'
            return "XA_RBPROTO";

        case 100: // 'd'
            return "XA_RBROLLBACK";

        case 106: // 'j'
            return "XA_RBTIMEOUT";

        case 107: // 'k'
            return "XA_RBTRANSIENT";

        case 3: // '\003'
            return "XA_RDONLY";

        case 4: // '\004'
            return "XA_RETRY";

        case -2: 
            return "XAER_ASYNC";

        case -8: 
            return "XAER_DUPID";

        case -5: 
            return "XAER_INVAL";

        case -4: 
            return "XAER_NOTA";

        case -9: 
            return "XAER_OUTSIDE";

        case -6: 
            return "XAER_PROTO";

        case -3: 
            return "XAER_RMERR";

        case -7: 
            return "XAER_RMFAIL";
        }
        throw new IllegalArgumentException(L10n.format("ILLEGAL.ARG.ERR", "errorCode"));
    }

    static String getStrXaResFlag(int i)
    {
        switch(i)
        {
        case 8388608: 
            return "TMENDRSCAN";

        case 536870912: 
            return "TMFAIL";

        case 2097152: 
            return "TMJOIN";

        case 0: // '\0'
            return "TMNOFLAGS";

        case 1073741824: 
            return "TMONEPHASE";

        case 134217728: 
            return "TMONEPHASE";

        case 16777216: 
            return "TMSTARTRSCAN";

        case 67108864: 
            return "TMSUCCESS";

        case 33554432: 
            return "TMSUSPEND";

        case 3: // '\003'
            return "XA_RDONLY";
        }
        throw new IllegalArgumentException(L10n.format("SRVC.TX.UTIL.IMPL.ERR1", new Integer(i)));
    }

    static String getStrXaResState(short word0)
    {
        switch(word0)
        {
        case 0: // '\0'
            return "XA_RES_NEW";

        case 1: // '\001'
            return "XA_RES_ENLISTED";

        case 2: // '\002'
            return "XA_RES_SUSPENDED";

        case 3: // '\003'
            return "XA_RES_ENDED";

        case 4: // '\004'
            return "XA_RES_VOTED_RDONLY";

        case 5: // '\005'
            return "XA_RES_VOTED_OK";
        }
        throw new IllegalArgumentException(L10n.format("SRVC.TX.UTIL.IMPL.ERR2", Short.toString(word0)));
    }
}
