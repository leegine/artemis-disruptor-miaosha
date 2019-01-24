// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   MsgHelper.java

package co.fin.intellioms.connector.impl;

import co.fin.intellioms.marshal.Fields;
import com.fitechlabs.xtier.services.marshal.MarshalObject;

public class MsgHelper
{

    public MsgHelper()
    {
    }

    public static boolean isReply(MarshalObject msg)
    {
        return msg.containsName(REPLY_FLAG);
    }

    public static MarshalObject createReply(MarshalObject src)
    {
        MarshalObject msg = new MarshalObject();
        msg.putBool(REPLY_FLAG, Boolean.TRUE);
        return msg;
    }

    public static MarshalObject createConfirmation(MarshalObject src)
    {
        MarshalObject cfm = createReply(src);
        cfm.putInt32(Fields.MSG_TYPE, 7);
        return cfm;
    }

    private static final Integer REPLY_FLAG = new Integer(0x3fffffff);

}
