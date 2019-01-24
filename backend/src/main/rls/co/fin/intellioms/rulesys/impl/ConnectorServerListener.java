// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   ConnectorServerListener.java

package co.fin.intellioms.rulesys.impl;

import co.fin.intellioms.connector.ClientConnection;
import co.fin.intellioms.connector.ServerMessagesListener;
import co.fin.intellioms.connector.impl.MsgHelper;
import co.fin.intellioms.marshal.Fields;
import co.fin.intellioms.rulesys.CondOrderOperation;
import co.fin.intellioms.rulesys.CondOrderOperationException;
import co.fin.intellioms.util.Log;
import com.fitechlabs.xtier.services.marshal.MarshalException;
import com.fitechlabs.xtier.services.marshal.MarshalObject;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

// Referenced classes of package com.com.fin.intellioms.rulesys.impl:
//            OmsRuleEngineImpl

public class ConnectorServerListener
    implements ServerMessagesListener
{

    public ConnectorServerListener(OmsRuleEngineImpl rls)
    {
        ruleEng = rls;
    }

    public Set getClients()
    {
        return new HashSet(clients);
    }

    public void onMessage(ClientConnection conn, MarshalObject msg)
    {
        switch(msg.getInt32(Fields.MSG_TYPE))
        {
        case 4: // '\004'
            CondOrderOperation op = (CondOrderOperation)msg.getMarshalObj(Fields.OPERATION);
            try
            {
                ruleEng.operateCondOrder(op);
                conn.send(MsgHelper.createConfirmation(msg));
            }
            catch(CondOrderOperationException e)
            {
                if(e.getErrType() == 2 || e.getErrType() == 4)
                {
                    if(log.isDebug())
                        log.debug(e.getMessage());
                } else
                if(log.isError())
                    log.error(e.getMessage(), e);
                sendErrReply(conn, e, msg);
            }
            catch(MarshalException e)
            {
                if(log.isError())
                    log.error((new StringBuilder()).append("Unable to send operation confirmation for ").append(op).append(": ").append(e.getMessage()).toString(), e);
            }
            catch(IOException e)
            {
                if(log.isError())
                    log.error((new StringBuilder()).append("Unable to send operation confirmation for ").append(op).append(": ").append(e.getMessage()).toString(), e);
            }
            break;

        default:
            if(log.isWarn())
                log.warn((new StringBuilder()).append("Unsupported message type ").append(msg).toString());
            break;
        }
    }

    public void onClose(ClientConnection conn)
    {
        synchronized(clients)
        {
            clients.remove(conn);
        }
    }

    public void onConnect(ClientConnection conn)
    {
        synchronized(clients)
        {
            clients.add(conn);
        }
    }

    private void sendErrReply(ClientConnection conn, CondOrderOperationException ex, MarshalObject src)
    {
        MarshalObject resp = MsgHelper.createReply(src);
        resp.putInt32(Fields.MSG_TYPE, 6);
        resp.putInt32(Fields.ERROR_TYPE, ex.getErrType());
        resp.putUtf8Str(Fields.ERROR_MSG, ex.getMessage());
        try
        {
            conn.send(resp);
        }
        catch(MarshalException e)
        {
            if(log.isError())
                log.error((new StringBuilder()).append("Unable to send error reply to client ").append(conn).toString(), e);
        }
        catch(IOException e)
        {
            if(log.isError())
                log.error((new StringBuilder()).append("Unable to send error reply to client ").append(conn).toString(), e);
        }
    }

    private static final Log log = Log.getLogger(com/ com /fin/intellioms/rulesys/impl/ConnectorServerListener);
    private final Set clients = new HashSet();
    private OmsRuleEngineImpl ruleEng;

}
