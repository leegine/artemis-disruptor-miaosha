// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   ClientConnection.java

package co.fin.intellioms.connector;

import com.fitechlabs.xtier.services.marshal.MarshalException;
import com.fitechlabs.xtier.services.marshal.MarshalObject;
import java.io.IOException;
import java.net.SocketAddress;

public interface ClientConnection
{

    public abstract SocketAddress getRemoteSocketAddress();

    public abstract void send(MarshalObject marshalobject)
        throws MarshalException, IOException;
}
