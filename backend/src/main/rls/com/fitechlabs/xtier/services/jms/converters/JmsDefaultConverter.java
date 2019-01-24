// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.jms.converters;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.jms.JmsObjectConverter;
import com.fitechlabs.xtier.services.jms.messages.JmsStreamMessageInputStream;
import java.io.*;
import javax.jms.*;

public class JmsDefaultConverter
    implements JmsObjectConverter
{

    public JmsDefaultConverter()
    {
    }

    public Message convert(Session session, Object obj)
        throws JMSException
    {
        if((obj instanceof String))
        {
            TextMessage textmessage = session.createTextMessage();
            textmessage.setText((String)obj);
            return textmessage;
        }
        if((obj instanceof byte[]))
        {
            BytesMessage bytesmessage = session.createBytesMessage();
            bytesmessage.writeBytes((byte[])obj);
            return bytesmessage;
        }
        if((obj instanceof InputStream))
        {
            StreamMessage streammessage = session.createStreamMessage();
            InputStream inputstream = (InputStream)obj;
            int i;
            try
            {
                while((i = inputstream.read()) > 0) 
                    streammessage.writeByte((byte)i);
            }
            catch(IOException ioexception)
            {
                JMSException jmsexception = new JMSException(L10n.format("SRVC.JMS.ERR21"));
                jmsexception.setLinkedException(ioexception);
                throw jmsexception;
            }
            return streammessage;
        }
        if((obj instanceof Serializable))
        {
            ObjectMessage objectmessage = session.createObjectMessage();
            objectmessage.setObject((Serializable)obj);
            return objectmessage;
        } else
        {
            throw new JMSException(L10n.format("SRVC.JMS.ERR22", obj.getClass()));
        }
    }

    public Object convert(Message message)
        throws JMSException
    {
        if((message instanceof TextMessage))
            return ((TextMessage)message).getText();
        if((message instanceof BytesMessage))
        {
            BytesMessage bytesmessage = (BytesMessage)message;
            byte abyte0[] = new byte[(int)bytesmessage.getBodyLength()];
            bytesmessage.readBytes(abyte0);
            return abyte0;
        }
        if((message instanceof StreamMessage))
            return new JmsStreamMessageInputStream((StreamMessage)message);
        if((message instanceof ObjectMessage))
            return ((ObjectMessage)message).getObject();
        else
            throw new JMSException(L10n.format("SRVC.JMS.ERR23", message.getClass()));
    }
}
