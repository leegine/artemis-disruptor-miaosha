// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.jms;

import com.fitechlabs.xtier.l10n.L10n;
import javax.jms.JMSException;

public class JmsTimeoutException extends JMSException
{

    public JmsTimeoutException(String s, long l)
    {
        super(s);
        timeout = l;
    }

    public long getTimeout()
    {
        return timeout;
    }

    public String toString()
    {
        return L10n.format("SRVC.JMS.TXT1", getMessage(), new Long(timeout));
    }

    private long timeout;
}
