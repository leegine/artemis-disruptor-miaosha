// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   ServerMessagesListener.java

package co.fin.intellioms.connector;

import com.fitechlabs.xtier.services.marshal.MarshalObject;

// Referenced classes of package com.com.fin.intellioms.connector:
//            ClientConnection

public interface ServerMessagesListener
{

    public abstract void onMessage(ClientConnection clientconnection, MarshalObject marshalobject);

    public abstract void onClose(ClientConnection clientconnection);

    public abstract void onConnect(ClientConnection clientconnection);
}
