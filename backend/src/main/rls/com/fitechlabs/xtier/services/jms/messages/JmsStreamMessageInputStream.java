// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.jms.messages;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.utils.ArgAssert;
import java.io.IOException;
import java.io.InputStream;
import javax.jms.JMSException;
import javax.jms.StreamMessage;

public class JmsStreamMessageInputStream extends InputStream
{

    public JmsStreamMessageInputStream(StreamMessage streammessage)
    {
        msg = null;
        ArgAssert.nullArg(streammessage, "msg");
        msg = streammessage;
    }

    public boolean markSupported()
    {
        return false;
    }

    public int read()
        throws IOException
    {
        if(msg == null)
            throw new IOException(L10n.format("SRVC.JMS.ERR24"));
        try
        {
            return msg.readByte() & 0xff;
        }
        catch(JMSException jmsexception)
        {
            throw new IOException(L10n.format("SRVC.JMS.ERR25", jmsexception.getMessage()));
        }
    }

    public void close()
    {
        msg = null;
    }

    private StreamMessage msg;
}
