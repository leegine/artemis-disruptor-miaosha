// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   ConnectorClient.java

package co.fin.intellioms.connector;

import com.fitechlabs.xtier.services.marshal.MarshalException;
import com.fitechlabs.xtier.services.marshal.MarshalObject;
import java.io.IOException;

// Referenced classes of package com.com.fin.intellioms.connector:
//            MessageListener

public interface ConnectorClient
{

    public abstract void connect()
        throws IOException;

    public abstract void connect(int i)
        throws IOException;

    public abstract void disconnect()
        throws IOException;

    public abstract void setListener(MessageListener messagelistener);

    public abstract void send(MarshalObject marshalobject)
        throws MarshalException, IOException;

    public abstract MarshalObject sendSync(MarshalObject marshalobject)
        throws MarshalException, IOException, InterruptedException;

    public abstract boolean isConnected();
}
