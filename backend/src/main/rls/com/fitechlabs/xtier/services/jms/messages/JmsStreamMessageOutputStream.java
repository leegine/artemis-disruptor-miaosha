// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.jms.messages;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.utils.ArgAssert;
import java.io.IOException;
import java.io.OutputStream;
import javax.jms.JMSException;
import javax.jms.StreamMessage;

public class JmsStreamMessageOutputStream extends OutputStream
{

    public JmsStreamMessageOutputStream(StreamMessage streammessage)
    {
        msg = null;
        ArgAssert.nullArg(streammessage, "msg");
        msg = streammessage;
    }

    public void write(int i)
        throws IOException
    {
        if(msg == null)
            throw new IOException(L10n.format("SRVC.JMS.ERR26"));
        try
        {
            msg.writeByte((byte)i);
        }
        catch(JMSException jmsexception)
        {
            throw new IOException(L10n.format("SRVC.JMS.ERR27", jmsexception.getMessage()));
        }
    }

    public void write(byte abyte0[])
        throws IOException
    {
        ArgAssert.nullArg(abyte0, "arr");
        write(abyte0, 0, abyte0.length);
    }

    public void write(byte abyte0[], int i, int j)
        throws IOException
    {
        ArgAssert.nullArg(abyte0, "arr");
        ArgAssert.illegalArg(i >= 0, "off");
        ArgAssert.illegalArg(j >= 0, "len");
        if(msg == null)
            throw new IOException(L10n.format("SRVC.JMS.ERR26"));
        try
        {
            msg.writeBytes(abyte0, i, j);
        }
        catch(JMSException jmsexception)
        {
            throw new IOException(L10n.format("SRVC.JMS.ERR27", jmsexception.getMessage()));
        }
    }

    public void close()
    {
        msg = null;
    }

    private StreamMessage msg;
}
