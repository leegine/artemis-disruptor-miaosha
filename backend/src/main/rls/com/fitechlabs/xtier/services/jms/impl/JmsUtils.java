// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.jms.impl;

import com.fitechlabs.xtier.services.jms.JmsObjectReceiver;
import com.fitechlabs.xtier.services.jms.JmsObjectSender;
import javax.jms.JMSException;

class JmsUtils
{

    private JmsUtils()
    {
    }

    static void close(JmsObjectSender jmsobjectsender)
    {
        if(jmsobjectsender != null)
            try
            {
                jmsobjectsender.close();
            }
            catch(JMSException jmsexception) { }
    }

    static void close(JmsObjectReceiver jmsobjectreceiver)
    {
        if(jmsobjectreceiver != null)
            try
            {
                jmsobjectreceiver.close();
            }
            catch(JMSException jmsexception) { }
    }
}
